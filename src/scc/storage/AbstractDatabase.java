package scc.storage;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import scc.data.dao.HouseDAO;
import scc.data.dao.QuestionDAO;
import scc.data.dao.RentalDAO;
import scc.data.dao.UserDAO;
import scc.data.dto.*;
import scc.server.auth.LoginDetails;
import scc.server.resources.MediaResource;
import scc.utils.Result;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public abstract class AbstractDatabase implements Database {

    protected final UsersStorage users;
    protected final HousesStorage houses;
    protected final RentalsStorage rentals;
    protected final QuestionsStorage questions;
    protected final MediaStorage media;
    protected static final Logger Log = Logger.getLogger(AbstractDatabase.class.getName());

    protected AbstractDatabase(UsersStorage users, HousesStorage houses, RentalsStorage rentals, QuestionsStorage questions, MediaStorage media){
        this.users = users;
        this.houses = houses;
        this.rentals = rentals;
        this.questions = questions;
        this.media = media;
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
        var value = users.putUser(new UserDAO(user)).toUser();
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

        return Result.ok(user);
    }

    public Result<List<HouseOwner>> listUserHouses(Cookie session, String ownerId, int start, int length) {
        if(ownerId == null ){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(!this.hasUser(ownerId)){
            return Result.error(Response.Status.NOT_FOUND);
        }

        var authRes = checkCookie(session, ownerId);
        if(!authRes.isOK())
            return Result.error(authRes.error());

        return Result.ok(houses.getHousesByOwner(ownerId, start, length));
    }

    public Result<List<Rental>> listUserRentals(Cookie session, String userId, int start, int length) {
        if( userId == null ){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(!this.hasUser(userId)){
            return Result.error(Response.Status.NOT_FOUND);
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

    public Result<House> createHouse(Cookie session, House house) {
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
            return Result.error(Response.Status.NOT_FOUND);

        var authRes = checkCookie(session, owner.getId());
        if(!authRes.isOK())
            return Result.error(authRes.error());

        var houseId = UUID.randomUUID().toString();
        house.setId(houseId);
        return Result.ok(houses.putHouse(new HouseDAO(house)).toHouse());
    }

    public Result<House> getHouse(String houseId){
        if(houseId == null) {
            return Result.error(Response.Status.BAD_REQUEST);
        }

        var house = this.getHouseDAO(houseId);
        if(house == null){
            return Result.error(Response.Status.NOT_FOUND);
        }

        house.setViews(house.getViews()+1);
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

        return Result.ok(house.toHouse());
    }

    public Result<List<HouseList>> searchHouses(String location, String startDate, String endDate, int start, int length) {
        if(location == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        return Result.ok(houses.searchHouses(location, startDate, endDate, start, length));
    }

    public abstract Result<List<HouseSearch>> searchByNameAndDescription(String queryText, String ownerId, Boolean useName,
                                                        Boolean useDescription, int start, int length);

    /*-------------------------------------------------- RENTALS -----------------------------------------------------*/

    public Result<String> createRental(Cookie session, String houseId, Rental rental) {
        if(houseId == null || rental.getTenantId() == null || rental.getLandlordId() == null
                || rental.getPeriod() == null) {
            return Result.error(Response.Status.BAD_REQUEST);
        }
        var house = this.getHouseDAO(houseId);
        var tenant = this.getUser(rental.getTenantId());
        if (house == null || tenant == null || !this.hasUser(rental.getLandlordId()))
            return Result.error(Response.Status.NOT_FOUND);
        if(!house.getOwnerId().equals(rental.getLandlordId()))
            return Result.error(Response.Status.FORBIDDEN);

        var authRes = checkCookie(session, tenant.getId());
        if(!authRes.isOK())
            return Result.error(authRes.error());

        var housePeriods = house.getPeriods();
        var periodIdx = housePeriods.indexOf(rental.getPeriod());
        if(periodIdx == -1)
            return Result.error(Response.Status.NOT_FOUND);

        rental.setHouseId(houseId);
        rental.setId(UUID.randomUUID().toString());

        var period = housePeriods.get(periodIdx);
        period.setAvailable(false);
        rental.setPeriod(period);

        return Result.ok(rentals.putRental(new RentalDAO(rental)).getId());
    }

    public abstract Result<Rental> updateRental(Cookie session, String houseId, String rentalId, Rental rentalToUpdate);

    public Result<List<Rental>> listHouseRentals(Cookie session, String houseId, int start, int length) {
        if(houseId == null)
            return Result.error(Response.Status.BAD_REQUEST);

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
            return Result.error(Response.Status.NOT_FOUND);

        var authRes = checkCookie(session, question.getUserId());
        if(!authRes.isOK())
            return Result.error(authRes.error());

        question.setId(UUID.randomUUID().toString());
        var questionDao = new QuestionDAO(question);
        questionDao.setHouseId(houseId);
        return Result.ok(questions.putQuestion(questionDao).getId());
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
