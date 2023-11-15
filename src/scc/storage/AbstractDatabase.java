package scc.storage;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.models.CosmosPatchOperations;
import com.azure.storage.blob.BlobContainerClient;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import scc.data.dao.HouseDAO;
import scc.data.dao.QuestionDAO;
import scc.data.dao.RentalDAO;
import scc.data.dao.UserDAO;
import scc.data.dto.*;
import scc.server.auth.LoginDetails;
import scc.server.resources.MediaResource;
import scc.storage.cosmosdb.HousesCDB;
import scc.storage.cosmosdb.QuestionsCDB;
import scc.storage.cosmosdb.RentalsCDB;
import scc.storage.cosmosdb.UsersCDB;
import scc.utils.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public abstract class AbstractDatabase implements Database {

    protected final UsersCDB users;
    protected final MediaBlobStorage media;
    protected final HousesCDB houses;
    protected final RentalsCDB rentals;
    protected final QuestionsCDB questions;
    protected static final Logger Log = Logger.getLogger(AbstractDatabase.class.getName());

    protected AbstractDatabase(CosmosClient cClient,
                         String cosmosdbDatabase,
                         String userCosmosDBContainerName,
                         String houseCosmosDBContainerName,
                         String rentalCosmosDBContainerName,
                         String questionCosmosDBContainerName,
                         BlobContainerClient userBlobContainer,
                         BlobContainerClient houseBlobContainer){
        var db = cClient.getDatabase(cosmosdbDatabase);

        users = new UsersCDB(db.getContainer(userCosmosDBContainerName));
        houses = new HousesCDB(db.getContainer(houseCosmosDBContainerName));
        rentals = new RentalsCDB(db.getContainer(rentalCosmosDBContainerName));
        questions = new QuestionsCDB(db.getContainer(questionCosmosDBContainerName));
        media = new MediaBlobStorage(userBlobContainer, houseBlobContainer);
    }

    /*---------------------------------------------------- AUTH ------------------------------------------------------*/

    public abstract Result<Response> auth(LoginDetails loginDetails);

    protected abstract Result<String> checkCookie(Cookie session, String id);


    /*---------------------------------------------------- USERS -----------------------------------------------------*/

    public Result<User> createUser(User user) {
        if(user == null || user.getId() == null || user.getName() == null || user.getPwd() == null
                || user.getPhotoId() == null || !media.exists(user.getPhotoId(), MediaResource.BlobType.USER)){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(this.hasUser(user.getId()))
            return Result.error(Response.Status.CONFLICT);
        var value = users.putUser(new UserDAO(user)).getItem().toUser();
        value.setPwd(user.getPwd());
        return Result.ok(value);
    }

    protected abstract UserDAO getUser(String userId);

    protected boolean hasUser(String userId){return this.getUser(userId) != null;}

    public Result<User> deleteUser(Cookie session, String userId) {
        if(userId == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }

        var authRes = checkCookie(session, userId);
        if(!authRes.isOK())
            return Result.error(authRes.error());

        UserDAO user = this.getUser(userId);
        if(user == null){
            return Result.error(Response.Status.NOT_FOUND);
        }

        for(String houseId : user.getHouseIds()){
            var house = this.getHouseDAO(houseId);
            house.setOwnerId("Deleted User");
        }

        for(String rentalId : user.getRentalIds()){
            var rental = rentals.getRental(rentalId);
            rental.setTenantId("Deleted User");
        }

        users.delUserById(userId);
        return Result.ok(user.toUser());
    }

    public Result<User> updateUser(Cookie session, String userId, User user) {
        if(userId == null || user == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }

        var authRes = checkCookie(session, userId);
        if(!authRes.isOK())
            return Result.error(authRes.error());

        UserDAO dbUser = this.getUser(userId);
        if(dbUser == null){
            return Result.error(Response.Status.NOT_FOUND);
        }

        var updateOps = getCosmosPatchOperations(user);

        var userDAO  = users.updateUser(userId, updateOps).getItem();

        return Result.ok(userDAO.toUser());
    }

    private static CosmosPatchOperations getCosmosPatchOperations(User user) {
        var updateOps = CosmosPatchOperations.create();
        var nameToUpdate = user.getName();
        var pwdToUpdate = user.getPwd();
        var photoIdToUpdate = user.getPhotoId();
        var houseIdsToUpdate = user.getHouseIds();

        if(nameToUpdate != null)
            updateOps.replace("/name", nameToUpdate);
        if(pwdToUpdate != null)
            updateOps.replace("/pwd", pwdToUpdate);
        if(photoIdToUpdate != null)
            updateOps.replace("/photoId", photoIdToUpdate);
        if(houseIdsToUpdate != null && houseIdsToUpdate.length > 1) {
            updateOps.set("/houseIds", houseIdsToUpdate);
        }
        return updateOps;
    }

    public Result<List<HouseOwner>> listUserHouses(Cookie session, String ownerId, int start, int length) {
        if(ownerId == null || !this.hasUser(ownerId)){
            return Result.error(Response.Status.BAD_REQUEST);
        }

        var authRes = checkCookie(session, ownerId);
        if(!authRes.isOK())
            return Result.error(authRes.error());

        return Result.ok(houses.getHousesByOwner(ownerId, start, length).stream().toList());
    }

    public Result<List<Rental>> listUserRentals(Cookie session, String userId, int start, int length) {
        if(userId == null || !this.hasUser(userId)){
            return Result.error(Response.Status.BAD_REQUEST);
        }

        var authRes = checkCookie(session, userId);
        if(!authRes.isOK())
            return Result.error(authRes.error());

        return Result.ok(rentals.getRentalsByUser(userId, start, length).stream().map(RentalDAO::toRental).toList());
    }


    /*---------------------------------------------------- MEDIA -----------------------------------------------------*/

    public Result<String> uploadMedia(byte[] contents, MediaResource.BlobType type) {
        if (contents == null)
            return Result.error(Response.Status.BAD_REQUEST);

        return switch (type) {
            case USER -> Result.ok(media.uploadUserPhoto(contents));
            case HOUSE -> Result.ok(media.uploadHousePhoto(contents));
        };
    }

    public Result<byte[]> downloadMedia(String id, MediaResource.BlobType type) {
        if (id == null)
            return Result.error(Response.Status.BAD_REQUEST);
        if (!media.exists(id, type))
            return Result.error(Response.Status.NOT_FOUND);

        var mediaValue = switch (type) {
            case USER -> media.downloadUserPhoto(id);
            case HOUSE -> media.downloadHousePhoto(id);
        };
        return Result.ok(mediaValue);
    }

    /*--------------------------------------------------- HOUSES -----------------------------------------------------*/

    public Result<String> createHouse(Cookie session, House house) {
        if(house == null || house.getName() == null || house.getLocation() == null || house.getDescription() == null
                || house.getPhotoIds() == null || house.getPhotoIds().length == 0
                || house.getPeriods() == null || house.getPeriods().length == 0) {
            return Result.error(Response.Status.BAD_REQUEST);
        }
        for (String photoId : house.getPhotoIds()){
            if (!media.exists(photoId, MediaResource.BlobType.HOUSE))
                return Result.error(Response.Status.BAD_REQUEST);
        }
        var owner = this.getUser(house.getOwnerId());
        if (owner == null)
            return Result.error(Response.Status.BAD_REQUEST);

        var authRes = checkCookie(session, owner.getId());
        if(!authRes.isOK())
            return Result.error(authRes.error());

        var houseId = UUID.randomUUID().toString();

        var ownerHouseIds = new ArrayList<>(Arrays.asList(owner.getHouseIds()));
        ownerHouseIds.add(houseId);

        users.updateUser(owner.getId(), CosmosPatchOperations.create().set("/houseIds", ownerHouseIds));

        house.setId(houseId);
        return Result.ok(houses.putHouse(new HouseDAO(house)).getItem().getId());
    }

    public Result<House> getHouse(String houseId){
        if(houseId == null) {
            return Result.error(Response.Status.BAD_REQUEST);
        }

        var house = this.getHouseDAO(houseId);
        if(house == null){
            return Result.error(Response.Status.NOT_FOUND);
        }

        return Result.ok(house.toHouse());
    }

    protected abstract HouseDAO getHouseDAO(String houseId);

    public boolean hasHouse(String houseId){return this.getHouseDAO(houseId) != null;}

    protected boolean isOwner(String houseId, String userId){
        var house = this.getHouseDAO(houseId);
        return userId.equals(house.getOwnerId());
    }

    public Result<House> deleteHouse(Cookie session, String houseId) {
        if(houseId == null) {
            return Result.error(Response.Status.BAD_REQUEST);
        }

        var house = this.getHouseDAO(houseId);
        if(house == null){
            return Result.error(Response.Status.NOT_FOUND);
        }

        var owner = this.getUser(house.getOwnerId());
        if (owner == null)
            return Result.error(Response.Status.NOT_FOUND);

        var authRes = checkCookie(session, owner.getId());
        if(!authRes.isOK())
            return Result.error(authRes.error());

        var ownerHouseIds = new ArrayList<>(Arrays.asList(owner.getHouseIds()));
        ownerHouseIds.remove(houseId);

        users.updateUser(owner.getId(), CosmosPatchOperations.create().set("/houseIds", ownerHouseIds));

        houses.deleteHouseById(houseId);
        return Result.ok(house.toHouse());
    }

    public Result<House> updateHouse(Cookie session, String houseId, House houseToUpdate) {
        if(houseId == null || houseToUpdate == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        var house = this.getHouseDAO(houseId);
        if(house == null){
            return Result.error(Response.Status.NOT_FOUND);
        }

        var owner = this.getUser(house.getOwnerId());
        if (owner == null)
            return Result.error(Response.Status.NOT_FOUND);

        var authRes = checkCookie(session, owner.getId());
        if(!authRes.isOK())
            return Result.error(authRes.error());

        var updateOps = CosmosPatchOperations.create();
        var nameToUpdate = house.getName();
        var descriptionToUpdate = house.getDescription();
        var periodsToUpdate = house.getPeriods();

        if(nameToUpdate != null)
            updateOps.replace("/name", nameToUpdate);
        if(descriptionToUpdate != null)
            updateOps.replace("/description", descriptionToUpdate);
        if(periodsToUpdate != null) {
            updateOps.set("/periods", periodsToUpdate);
        }
        var houseDAO = houses.updateHouse(houseId, updateOps).getItem();
        return Result.ok(houseDAO.toHouse());
    }

    public Result<List<HouseList>> searchHouses(String location, String startDate, String endDate, int start, int length) {
        if(location == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        return Result.ok(houses.searchHouses(location, startDate, endDate, start, length).stream().toList());
    }

    /*-------------------------------------------------- RENTALS -----------------------------------------------------*/

    public Result<String> createRental(Cookie session, String houseId, Rental rental) {
        if(houseId == null || rental.getId() == null || rental.getTenantId() == null || rental.getLandlordId() == null
                || rental.getPeriod() == null
                || !this.hasUser(rental.getTenantId()) || !this.hasUser(rental.getLandlordId())){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        var house = this.getHouseDAO(houseId);
        if (house == null)
            return Result.error(Response.Status.NOT_FOUND);
        if(!house.getOwnerId().equals(rental.getLandlordId()))
            return Result.error(Response.Status.BAD_REQUEST);

        var authRes = checkCookie(session, rental.getTenantId());
        if(!authRes.isOK())
            return Result.error(authRes.error());
        rental.setHouseId(houseId);
        return Result.ok(rentals.putRental(new RentalDAO(rental)).getItem().getId());
    }

    public Result<Rental> updateRental(Cookie session, String houseId, String rentalId, Rental rentalToUpdate) {
        if(houseId == null || rentalId == null || rentalToUpdate == null )
            return Result.error(Response.Status.BAD_REQUEST);
        if (!this.hasHouse(houseId))
            return Result.error(Response.Status.NOT_FOUND);

        var rental = rentals.getRental(rentalId);
        if (rental == null)
            return Result.error(Response.Status.BAD_REQUEST);

        var authRes = checkCookie(session, rental.getLandlordId());
        if(!authRes.isOK())
            return Result.error(authRes.error());

        var updateOps = CosmosPatchOperations.create();
        var tenantIdToUpdate = rental.getTenantId();
        var periodToUpdate = rental.getPeriod();

        if(tenantIdToUpdate != null){
            if(!this.hasUser(tenantIdToUpdate))
                return Result.error(Response.Status.BAD_REQUEST);
            updateOps.replace("/tenantId", tenantIdToUpdate);
        }
        if(periodToUpdate != null)
            updateOps.replace("/period", periodToUpdate);

        return Result.ok(rentals.updateRental(rentalId, updateOps).getItem().toRental());
    }

    public Result<List<Rental>> listHouseRentals(Cookie session, String houseId, int start, int length) {
        if(houseId == null)
            return Result.error(Response.Status.BAD_REQUEST);

        var house = this.getHouseDAO(houseId);
        if(house == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }

        var owner = this.getUser(house.getOwnerId());
        if (owner == null)
            return Result.error(Response.Status.NOT_FOUND);

        var authRes = checkCookie(session, owner.getId());
        if(!authRes.isOK())
            return Result.error(authRes.error());

        return Result.ok(rentals.getRentalsByHouse(houseId, start, length).stream().map(RentalDAO::toRental).toList());
    }

    public Result<List<DiscountedRental>> listDiscountedRentals(int start, int length) {
        return Result.ok(houses.getDiscountedHouses(start, length).stream().toList());
    }

    /*------------------------------------------------ QUESTIONS -----------------------------------------------------*/

    public Result<String> createQuestion(Cookie session, String houseId, Question question) {
        if(houseId == null || question == null || question.getUserId() == null || question.getMessage() == null
                || question.getReply() != null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(!this.hasHouse(houseId)){
            return Result.error(Response.Status.NOT_FOUND);
        }

        if (!this.hasUser(question.getUserId()))
            return Result.error(Response.Status.BAD_REQUEST);

        var authRes = checkCookie(session, question.getUserId());
        if(!authRes.isOK())
            return Result.error(authRes.error());

        question.setId(UUID.randomUUID().toString());
        var questionDao = new QuestionDAO(question);
        questionDao.setHouseId(houseId);
        return Result.ok(questions.putQuestion(questionDao).getItem().getId());
    }

    public Result<String> createReply(Cookie session, String houseId, String questionId, Reply reply) {
        if (houseId == null || questionId == null || reply == null || reply.getMessage() == null
                || reply.getUserId() == null) {
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(!this.hasHouse(houseId) || !questions.hasQuestion(questionId)){
            return Result.error(Response.Status.NOT_FOUND);
        }

        var authRes = checkCookie(session, reply.getUserId());
        if(!authRes.isOK())
            return Result.error(authRes.error());

        if(!this.isOwner(houseId, reply.getUserId()) || questions.hasReply(questionId)){
            return Result.error((Response.Status.FORBIDDEN));
        }

        var updateOps = CosmosPatchOperations.create();
        updateOps.replace("/reply", reply);

        questions.addReply(questionId, updateOps);
        return Result.ok("The reply was created");

    }

    public Result<List<Question>> listHouseQuestions(String houseId, Boolean answered, int start, int length) {
        if (houseId == null) {
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(!this.hasHouse(houseId)){
            return Result.error(Response.Status.NOT_FOUND);
        }

        return Result.ok(questions.getHouseQuestions(houseId, answered, start, length).stream().map(QuestionDAO::toQuestion).toList());
    }

}
