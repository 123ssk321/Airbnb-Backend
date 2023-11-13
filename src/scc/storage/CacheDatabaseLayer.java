package scc.storage;

import com.azure.cosmos.CosmosClient;
import com.azure.storage.blob.BlobContainerClient;
import scc.cache.RedisCache;
import scc.data.dao.HouseDAO;
import scc.data.dao.UserDAO;
import scc.data.dto.*;
import scc.utils.Result;

public class CacheDatabaseLayer extends AbstractDatabase implements Database {

    private final RedisCache cache;
    private static final String USER_REDIS_KEY = "user:";
    private static final String HOUSE_REDIS_KEY = "house:";


    public CacheDatabaseLayer(CosmosClient cClient,
                              String cosmosdbDatabase,
                              String userCosmosDBContainerName,
                              String houseCosmosDBContainerName,
                              String rentalCosmosDBContainerName,
                              String questionCosmosDBContainerName,
                              BlobContainerClient userBlobContainer,
                              BlobContainerClient houseBlobContainer) {
        super(cClient, cosmosdbDatabase, userCosmosDBContainerName, houseCosmosDBContainerName, rentalCosmosDBContainerName, questionCosmosDBContainerName, userBlobContainer, houseBlobContainer);
        cache = new RedisCache();
    }

    /*--------------------------------------------------- USERS ------------------------------------------------------*/

    @Override
    protected UserDAO getUser(String userId) {
        var user = cache.get(USER_REDIS_KEY + userId, UserDAO.class);
        if(user == null){
            user = super.users.getUser(userId);
            if(user != null)
                cache.set(USER_REDIS_KEY + userId, user);
        }
        return user;
    }

    @Override
    public Result<User> deleteUser(String userId, String password) {
        var result = super.deleteUser(userId, password);
        if(result.isOK())
            cache.delete(USER_REDIS_KEY + userId);
        return result;
    }

    @Override
    public Result<User> updateUser(String userId, String password, User user) {
        var result = super.updateUser(userId, password, user);
        if(result.isOK())
            cache.set(USER_REDIS_KEY + userId, new UserDAO(user));
        return result;
    }

    /*--------------------------------------------------- HOUSES -----------------------------------------------------*/

    @Override
    protected HouseDAO getHouse(String houseId) {
        var house = cache.get(HOUSE_REDIS_KEY + houseId, HouseDAO.class);
        if(house == null){
            house = super.houses.getHouse(houseId);
            if(house != null)
                cache.set(HOUSE_REDIS_KEY + houseId, house);
        }
        return house;
    }

    @Override
    public Result<House> deleteHouse(String houseId) {
        var result = super.deleteHouse(houseId);
        if(result.isOK())
            cache.delete(HOUSE_REDIS_KEY + houseId);
        return result;
    }

    @Override
    public Result<House> updateHouse(String houseId, House house) {
        var result = super.updateHouse(houseId, house);
        if(result.isOK())
            cache.set(HOUSE_REDIS_KEY + houseId, new HouseDAO(house));
        return result;
    }

}
