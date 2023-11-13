package scc.storage;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.models.CosmosPatchOperations;
import com.azure.storage.blob.BlobContainerClient;
import jakarta.ws.rs.core.Response;
import scc.data.dao.HouseDAO;
import scc.data.dao.QuestionDAO;
import scc.data.dao.RentalDAO;
import scc.data.dao.UserDAO;
import scc.data.dto.*;
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

    /*---------------------------------------------------- USERS -----------------------------------------------------*/

    public Result<User> createUser(User user) {
        if(user == null || user.getId() == null || user.getName() == null || user.getPwd() == null
                || user.getPhotoId() == null || !media.exists(user.getPhotoId(), MediaResource.BlobType.USER)){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(this.hasUser(user.getId()))
            return Result.error(Response.Status.CONFLICT);

        return Result.ok(users.putUser(new UserDAO(user)).getItem().toUser());
    }

    protected abstract UserDAO getUser(String userId);

    protected boolean hasUser(String userId){return this.getUser(userId) != null;}

    public Result<User> deleteUser(String userId, String password) {
        if(userId == null || password == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        UserDAO user = this.getUser(userId);
        if(user == null){
            return Result.error(Response.Status.NOT_FOUND);
        }
        if(!user.getPwd().equals(password)){
            return Result.error(Response.Status.FORBIDDEN);
        }
        return Result.ok(((UserDAO) users.delUserById(userId).getItem()).toUser());
    }

    public Result<User> updateUser(String userId, String password, User user) {
        if(userId == null || password == null || user == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        UserDAO dbUser = this.getUser(userId);
        if(dbUser == null){
            return Result.error(Response.Status.NOT_FOUND);
        }
        if(!dbUser.getPwd().equals(password)){
            return Result.error(Response.Status.FORBIDDEN);
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
            for (String houseId : houseIdsToUpdate)
                updateOps.add("/houseIds", houseId);
        }
        return updateOps;
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

    public Result<String> createHouse(House house) {
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
        var houseId = UUID.randomUUID().toString();
        var ownerHouseIds = new ArrayList<>(Arrays.asList(owner.getHouseIds()));
        ownerHouseIds.add(houseId);
        owner.setHouseIds(ownerHouseIds.toArray(new String[0]));
        house.setId(houseId);
        return Result.ok(houses.putHouse(new HouseDAO(house)).getItem().getId());
    }

    protected abstract HouseDAO getHouse(String houseId);

    public boolean hasHouse(String houseId){return this.getHouse(houseId) != null;}

    protected boolean isOwner(String houseId, String userId){
        var house = this.getHouse(houseId);
        return userId.equals(house.getOwnerId());
    }

    public Result<House> deleteHouse(String houseId) {
        if(houseId == null) {
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(!this.hasHouse(houseId)){
            return Result.error(Response.Status.NOT_FOUND);
        }
        return Result.ok(((HouseDAO) houses.deleteHouseById(houseId).getItem()).toHouse());
    }

    public Result<House> updateHouse(String houseId, House house) {
        if(houseId == null || house == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(!this.hasHouse(houseId)){
            return Result.error(Response.Status.NOT_FOUND);
        }
        var updateOps = CosmosPatchOperations.create();
        var nameToUpdate = house.getName();
        var descriptionToUpdate = house.getDescription();
        var periodsToUpdate = house.getPeriods();

        if(nameToUpdate != null)
            updateOps.replace("/name", nameToUpdate);
        if(descriptionToUpdate != null)
            updateOps.replace("/description", descriptionToUpdate);
        if(periodsToUpdate != null) {
            //TODO: Update periods
        }
        var houseDAO = houses.updateHouse(houseId, updateOps).getItem();
        return Result.ok(houseDAO.toHouse());
    }

    public Result<List<House>> searchHouses(String location, String startDate, String endDate) {
        if(location == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        return Result.ok(houses.searchHouses(location, startDate, endDate).stream().map(HouseDAO::toHouse).toList());
    }

    public Result<List<House>> listUserHouses(String ownerId) {
        if(ownerId == null || !this.hasUser(ownerId)){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        return Result.ok(houses.getHousesByOwner(ownerId).stream().map(HouseDAO::toHouse).toList());
    }

    /*-------------------------------------------------- RENTALS -----------------------------------------------------*/

    public Result<String> createRental(String houseId, Rental rental) {
        if(houseId == null || rental.getId() == null || rental.getTenantId() == null || rental.getLandlordId() == null
                || rental.getPeriod() == null
                || !this.hasUser(rental.getTenantId()) || !this.hasUser(rental.getLandlordId())){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if (!this.hasHouse(houseId))
            return Result.error(Response.Status.NOT_FOUND);
        if(!this.isOwner(houseId, rental.getLandlordId()))
            return Result.error(Response.Status.BAD_REQUEST);

        var rentalDao = new RentalDAO(rental);
        rentalDao.setHouseId(houseId);
        return Result.ok(rentals.putRental(rentalDao).getItem().getId());
    }

    public Result<Rental> updateRental(String houseId, String rentalId, Rental rental) {
        if(houseId == null || rentalId == null || rental == null )
            return Result.error(Response.Status.BAD_REQUEST);
        if (!this.hasHouse(houseId))
            return Result.error(Response.Status.NOT_FOUND);

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

    public Result<List<Rental>> listRentals(String houseId) {
        if(houseId == null || !this.hasHouse(houseId))
            return Result.error(Response.Status.BAD_REQUEST);
        return Result.ok(rentals.getRentalsByHouse(houseId).stream().map(RentalDAO::toRental).toList());
    }

    public Result<List<DiscountedRental>> listDiscountedRentals() {
        return Result.ok(houses.getDiscountedHouses().stream().toList());
    }

    /*------------------------------------------------ QUESTIONS -----------------------------------------------------*/

    public Result<String> createQuestion(String houseId, Question question) {
        if(houseId == null || question == null || question.getUserId() == null || question.getMessage() == null
                || question.getReply() != null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(!this.hasHouse(houseId)){
            return Result.error(Response.Status.NOT_FOUND);
        }
        if (!this.hasUser(question.getUserId()))
            return Result.error(Response.Status.BAD_REQUEST);

        question.setId(UUID.randomUUID().toString());
        var questionDao = new QuestionDAO(question);
        questionDao.setHouseId(houseId);
        return Result.ok(questions.putQuestion(questionDao).getItem().getId());
    }

    public Result<String> createReply(String houseId, String questionId, Reply reply) {
        if (houseId == null || questionId == null || reply == null || reply.getMessage() == null
                || reply.getUserId() == null) {
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(!this.hasHouse(houseId) || !questions.hasQuestion(questionId)){
            return Result.error(Response.Status.NOT_FOUND);
        }
        if(!this.isOwner(houseId, reply.getUserId()) || questions.hasReply(questionId)){
            return Result.error((Response.Status.FORBIDDEN));
        }

        var updateOps = CosmosPatchOperations.create();
        updateOps.replace("/reply", reply);

        questions.addReply(questionId, updateOps);
        return Result.ok("The reply was created");

    }

    public Result<List<Question>> listHouseQuestions(String houseId) {
        if (houseId == null) {
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(!this.hasHouse(houseId)){
            return Result.error(Response.Status.NOT_FOUND);
        }

        return Result.ok(questions.getHouseQuestions(houseId).stream().map(QuestionDAO::toQuestion).toList());
    }

}
