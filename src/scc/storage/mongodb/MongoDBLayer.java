package scc.storage.mongodb;

import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Updates.*;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.bson.conversions.Bson;
import scc.cache.RedisCache;
import scc.data.dao.HouseDAO;
import scc.data.dao.QuestionDAO;
import scc.data.dao.RentalDAO;
import scc.data.dao.UserDAO;
import scc.data.dto.*;
import scc.server.auth.LoginDetails;
import scc.storage.AbstractDatabase;
import scc.storage.Database;
import scc.storage.media.PersistentVolumeStorage;
import scc.storage.mongodb.collection.HousesCollection;
import scc.storage.mongodb.collection.QuestionsCollection;
import scc.storage.mongodb.collection.RentalsCollection;
import scc.storage.mongodb.collection.UsersCollection;
import scc.utils.Hash;
import scc.utils.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MongoDBLayer extends AbstractDatabase implements Database {

    public static final String USER_MONGODB_COLLECTION_NAME = "users";
    public static final String HOUSE_MONGODB_COLLECTION_NAME = "houses";
    public static final String RENTAL_MONGODB_COLLECTION_NAME = "rentals";
    public static final String QUESTION_MONGODB_COLLECTION_NAME = "questions";

    private static final String SESSION_REDIS_KEY = "session:";
    private static final String USER_REDIS_KEY = "user:";
    private static final String HOUSE_REDIS_KEY = "house:";

    private final RedisCache cache;

    public MongoDBLayer(MongoDatabase db){
        super(new UsersCollection(db.getCollection(USER_MONGODB_COLLECTION_NAME, UserDAO.class)),
                new HousesCollection(db.getCollection(HOUSE_MONGODB_COLLECTION_NAME, HouseDAO.class)),
                new RentalsCollection(db.getCollection(RENTAL_MONGODB_COLLECTION_NAME, RentalDAO.class)),
                new QuestionsCollection(db.getCollection(QUESTION_MONGODB_COLLECTION_NAME, QuestionDAO.class)),
                new PersistentVolumeStorage());
        cache = new RedisCache();
    }

    /*---------------------------------------------------- AUTH ------------------------------------------------------*/

    public Result<Response> auth(LoginDetails loginDetails){
        boolean pwdOk = false;
        var user = this.getUser(loginDetails.getUserId());

        if(user != null && user.getPwd().equals(Hash.of(loginDetails.getPwd())))
            pwdOk = true;


        if( pwdOk) {
            String uid = UUID.randomUUID().toString();
            NewCookie cookie = new NewCookie.Builder("scc:session")
                    .value(uid)
                    .path("/")
                    .comment("sessionid")
                    .maxAge(3600)
                    .secure(false)
                    .httpOnly(true)
                    .build();
            cache.set( SESSION_REDIS_KEY + uid, loginDetails.getUserId());
            return Result.ok(Response.ok().cookie(cookie).build());
        } else
            return Result.error(Response.Status.UNAUTHORIZED);
    }

    public Result<String> checkCookie(Cookie session, String id) {
        if (session == null || session.getValue() == null)
            return Result.error(Response.Status.UNAUTHORIZED);
        var userId = cache.get(getCacheKey(SESSION_REDIS_KEY, session.getValue()), String.class);

        if (userId == null || userId.isEmpty())
            return Result.error(Response.Status.UNAUTHORIZED);
        if (!userId.equals(id) && !userId.equals("admin"))
            return Result.error(Response.Status.UNAUTHORIZED);
        return Result.ok(userId);
    }


    /*---------------------------------------------------- USERS -----------------------------------------------------*/

    @Override
    protected UserDAO getUser(String userId) {
        var user = cache.get(getCacheKey(USER_REDIS_KEY, userId), UserDAO.class);
        if(user == null){
            user = super.users.getUser(userId);
            if(user != null)
                cache.set(getCacheKey(USER_REDIS_KEY, userId), user);
        }
        return user;
    }

    @Override
    public Result<User> deleteUser(Cookie session, String userId) {
        var res = super.deleteUser(session, userId);
        if (res.isOK()){
            var user = res.value();
            for(String houseId : user.getHouseIds()){
                ((HousesCollection) houses).updateHouse(houseId, set("ownerId", "Deleted User"));
                var house = cache.get(getCacheKey(HOUSE_REDIS_KEY, houseId), HouseDAO.class);
                if(house != null)
                    cache.set(getCacheKey(HOUSE_REDIS_KEY, houseId), super.houses.getHouse(houseId));
            }

            for(String rentalId : user.getRentalIds()){
                ((RentalsCollection) rentals).updateRental(rentalId, set("tenantId", "Deleted User"));
            }
            cache.delete(getCacheKey(USER_REDIS_KEY, userId));
        }
        return res;
    }

    @Override
    public Result<User> updateUser(Cookie session, String userId, User user) {
        var res = super.updateUser(session, userId, user);
        if (res.isOK()){
            var updateOps = getUpdateOperations(res.value());
            var userDAO  = ((UsersCollection) users).updateUser(userId, updateOps);
            cache.set(getCacheKey(USER_REDIS_KEY, userId), new UserDAO(res.value()));
            return Result.ok(userDAO.toUser());
        }
        return res;
    }

    private static Bson getUpdateOperations(User user) {
        var updateOps = new ArrayList<Bson>();
        var nameToUpdate = user.getName();
        var pwdToUpdate = user.getPwd();
        var photoIdToUpdate = user.getPhotoId();
        var houseIdsToUpdate = user.getHouseIds();

        if(nameToUpdate != null)
            updateOps.add(set("name", nameToUpdate));
        if(pwdToUpdate != null)
            updateOps.add(set("pwd", pwdToUpdate));
        if(photoIdToUpdate != null)
            updateOps.add(set("photoId", photoIdToUpdate));
        if(houseIdsToUpdate != null && houseIdsToUpdate.length > 1) {
            updateOps.add(set("houseIds", houseIdsToUpdate));
        }
        return combine(updateOps);
    }


    /*--------------------------------------------------- HOUSES -----------------------------------------------------*/

    @Override
    public Result<House> createHouse(Cookie session, House house) {
        var res = super.createHouse(session, house);
        if (res.isOK()){
            var h = res.value();
            var ownerId = h.getOwnerId();
            ((UsersCollection) users).updateUser(ownerId, push("houseIds", h.getId()));
            cache.set(getCacheKey(USER_REDIS_KEY, ownerId), super.users.getUser(ownerId));
        }
        return res;
    }

    @Override
    public Result<House> getHouse(String houseId){
        var res = super.getHouse(houseId);
        if(res.isOK()){
            var updateViews = new Thread(() -> ((HousesCollection) houses).updateHouse(houseId, inc("views", 1)));
            updateViews.start();
        }
        return res;
    }

    @Override
    protected HouseDAO getHouseDAO(String houseId) {
        var house = cache.get(getCacheKey(HOUSE_REDIS_KEY, houseId), HouseDAO.class);
        if(house == null){
            house = super.houses.getHouse(houseId);
            if(house != null)
                cache.set(getCacheKey(HOUSE_REDIS_KEY, houseId), house);
        }
        return house;
    }

    @Override
    public Result<House> deleteHouse(Cookie session, String houseId) {
        var res = super.deleteHouse(session, houseId);
        if(res.isOK()){
            var house = res.value();
            var ownerId = res.value().getOwnerId();
            ((UsersCollection) users).updateUser(ownerId, pull("houseIds", houseId));
            cache.set(getCacheKey(USER_REDIS_KEY, ownerId), super.users.getUser(ownerId));

            for(RentalDAO rental : rentals.getRentalsByHouse(houseId, 0, house.getPeriods().length)){
                ((RentalsCollection) rentals).updateRental(rental.getId(), set("houseId", "Deleted House"));
            }
            cache.delete(getCacheKey(HOUSE_REDIS_KEY, houseId));
        }
        return res;
    }

    @Override
    public Result<House> updateHouse(Cookie session, String houseId, House houseToUpdate) {
        var res = super.updateHouse(session, houseId, houseToUpdate);
        if(res.isOK()){
            var updateOps = new ArrayList<Bson>();
            var nameToUpdate = houseToUpdate.getName();
            var descriptionToUpdate = houseToUpdate.getDescription();
            var periodsToUpdate = houseToUpdate.getPeriods();

            if(nameToUpdate != null)
                updateOps.add(set("name", nameToUpdate));
            if(descriptionToUpdate != null)
                updateOps.add(set("description", descriptionToUpdate));
            if(periodsToUpdate != null) {
                updateOps.add(set("periods", periodsToUpdate));
            }
            var houseDAO = ((HousesCollection) houses).updateHouse(houseId, combine(updateOps));
            cache.set(getCacheKey(HOUSE_REDIS_KEY, houseId), new HouseDAO(res.value()));
            return Result.ok(houseDAO.toHouse());
        }
        return res;

    }

    @Override
    public Result<List<HouseSearch>> searchByNameAndDescription(String queryText, String ownerId, Boolean useName, Boolean useDescription, int start, int length) {
        return null;
    }

    /*-------------------------------------------------- RENTALS -----------------------------------------------------*/

    @Override
    public Result<String> createRental(Cookie session, String houseId, Rental rental) {
        var res = super.createRental(session, houseId, rental);
        if(res.isOK()){
            var tenantId = rental.getTenantId();
            var house = this.getHouseDAO(houseId);
            var housePeriods = Arrays.asList(house.getPeriods());
            var periodIdx = housePeriods.indexOf(rental.getPeriod());

            ((HousesCollection) houses).updateHouse(houseId, set("periods."+periodIdx+".available", false));
            ((UsersCollection) users).updateUser(tenantId, push("rentalIds", res.value()));

            cache.set(getCacheKey(USER_REDIS_KEY, tenantId), super.users.getUser(tenantId));
            cache.set(getCacheKey(HOUSE_REDIS_KEY, houseId), super.houses.getHouse(houseId));
        }
        return res;
    }

    @Override
    public Result<Rental> updateRental(Cookie session, String houseId, String rentalId, Rental rentalToUpdate) {
        return Result.error(Response.Status.NOT_IMPLEMENTED);
    }

    /*------------------------------------------------ QUESTIONS -----------------------------------------------------*/

    @Override
    public Result<String> createReply(Cookie session, String houseId, String questionId, Reply reply) {
        var res = super.createReply(session, houseId, questionId, reply);
        if(res.isOK()){
            ((QuestionsCollection) questions).addReply(questionId, set("reply", reply));
        }
        return res;

    }

    /*---------------------------------------------------- UTILS -----------------------------------------------------*/

    private String getCacheKey(String cacheKey, String id){
        return cacheKey + id;
    }


}
