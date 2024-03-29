package scc.storage.cosmosdb;

import com.azure.cosmos.CosmosClient;
import com.azure.storage.blob.BlobContainerClient;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import scc.cache.RedisCache;
import scc.data.dao.HouseDAO;
import scc.data.dao.UserDAO;
import scc.data.dto.*;
import scc.server.auth.LoginDetails;
import scc.storage.Database;
import scc.storage.MediaStorage;
import scc.utils.Hash;
import scc.utils.Result;

import java.util.UUID;

public class CacheDatabaseLayer extends CosmosDBLayer implements Database {

    private final RedisCache cache;
    private static final String SESSION_REDIS_KEY = "session:";
    private static final String USER_REDIS_KEY = "user:";
    private static final String HOUSE_REDIS_KEY = "house:";

    public CacheDatabaseLayer(CosmosClient cClient,
                              String cosmosdbDatabase,
                              String userCosmosDBContainerName,
                              String houseCosmosDBContainerName,
                              String rentalCosmosDBContainerName,
                              String questionCosmosDBContainerName,
                              MediaStorage mediaStorage) {
        super(cClient.getDatabase(cosmosdbDatabase), userCosmosDBContainerName, houseCosmosDBContainerName, rentalCosmosDBContainerName, questionCosmosDBContainerName, mediaStorage);
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


    /*--------------------------------------------------- USERS ------------------------------------------------------*/

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
        var result = super.deleteUser(session, userId);
        if(result.isOK()){
            for(String houseId : result.value().getHouseIds()){
                var house = cache.get(getCacheKey(HOUSE_REDIS_KEY, houseId), HouseDAO.class);
                if(house != null)
                    cache.set(getCacheKey(HOUSE_REDIS_KEY, houseId), super.houses.getHouse(houseId));
            }
            cache.delete(getCacheKey(USER_REDIS_KEY, userId));
        }
        return result;
    }

    @Override
    public Result<User> updateUser(Cookie session, String userId, User user) {
        var result = super.updateUser(session, userId, user);
        if(result.isOK())
            cache.set(getCacheKey(USER_REDIS_KEY, userId), new UserDAO(result.value()));
        return result;
    }

    /*--------------------------------------------------- HOUSES -----------------------------------------------------*/

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
    public Result<House> createHouse(Cookie session, House house){
        var result = super.createHouse(session, house);
        if (result.isOK()){
            var ownerId = result.value().getOwnerId();
            cache.set(getCacheKey(USER_REDIS_KEY, ownerId), super.users.getUser(ownerId));
        }
        return result;
    }

    @Override
    public Result<House> deleteHouse(Cookie session, String houseId) {
        var result = super.deleteHouse(session, houseId);
        if(result.isOK()){
            var ownerId = result.value().getOwnerId();
            cache.set(getCacheKey(USER_REDIS_KEY, ownerId), super.users.getUser(ownerId));
            cache.delete(getCacheKey(HOUSE_REDIS_KEY, houseId));
        }
        return result;
    }

    @Override
    public Result<House> updateHouse(Cookie session, String houseId, House houseToUpdate) {
        var result = super.updateHouse(session, houseId, houseToUpdate);
        if(result.isOK())
            cache.set(getCacheKey(HOUSE_REDIS_KEY, houseId), new HouseDAO(result.value()));
        return result;
    }

    /*-------------------------------------------------- RENTALS -----------------------------------------------------*/

    @Override
    public Result<String> createRental(Cookie session, String houseId, Rental rental) {
        var result = super.createRental(session, houseId, rental);
        if (result.isOK()){
            var tenantId = rental.getTenantId();
            cache.set(getCacheKey(USER_REDIS_KEY, tenantId), super.users.getUser(tenantId));

            cache.set(getCacheKey(HOUSE_REDIS_KEY, houseId), super.houses.getHouse(houseId));
        }
        return result;
    }



    /*---------------------------------------------------- UTILS -----------------------------------------------------*/

    private String getCacheKey(String cacheKey, String id){
        return cacheKey + id;
    }

}
