package scc.storage;

import com.azure.cosmos.CosmosClient;
import com.azure.storage.blob.BlobContainerClient;
import scc.cache.RedisCache;
import scc.data.dao.HouseDAO;
import scc.data.dao.UserDAO;
import scc.data.dto.*;
import scc.server.resources.MediaResource;
import scc.utils.Result;

import java.util.List;

// TODO
public class CacheDatabaseLayer extends DatabaseLayer implements Database {

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

    @Override
    protected UserDAO getUser(String userId) {
        var user = cache.get(USER_REDIS_KEY + userId, UserDAO.class);
        if(user == null){
            user = super.getUser(userId);
            if(user != null)
                cache.set(USER_REDIS_KEY + userId, user);
        }
        return user;
    }

    @Override
    protected boolean hasUser(String userId) {
        return super.hasUser(userId);
    }

    @Override
    protected HouseDAO getHouse(String houseId) {
        var house = cache.get(HOUSE_REDIS_KEY + houseId, HouseDAO.class);
        if(house == null){
            house = super.getHouse(houseId);
            if(house != null)
                cache.set(HOUSE_REDIS_KEY + houseId, house);
        }
        return house;
    }

    @Override
    public boolean hasHouse(String houseId) {
        return super.hasHouse(houseId);
    }

    @Override
    protected boolean isOwner(String houseId, String userId) {
        return super.isOwner(houseId, userId);
    }

    @Override
    public Result<String> createUser(User user) {
        return super.createUser(user);
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

    @Override
    public Result<String> uploadMedia(byte[] contents, MediaResource.BlobType type) {
        return super.uploadMedia(contents, type);
    }

    @Override
    public Result<byte[]> downloadMedia(String id, MediaResource.BlobType type) {
        return super.downloadMedia(id, type);
    }

    @Override
    public Result<String> createHouse(House house) {
        return super.createHouse(house);
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

    @Override
    public Result<List<House>> searchHouses(String location, String startDate, String endDate) {
        return super.searchHouses(location, startDate, endDate);
    }

    @Override
    public Result<List<House>> listUserHouses(String ownerId) {
        return super.listUserHouses(ownerId);
    }

    @Override
    public Result<String> createRental(String houseId, Rental rental) {
        return super.createRental(houseId, rental);
    }

    @Override
    public Result<Rental> updateRental(String houseId, String rentalId, Rental rental) {
        return super.updateRental(houseId, rentalId, rental);
    }

    @Override
    public Result<List<Rental>> listRentals(String houseId) {
        return super.listRentals(houseId);
    }

    @Override
    public Result<List<DiscountedRental>> listDiscountedRentals() {
        return super.listDiscountedRentals();
    }

    @Override
    public Result<String> createQuestion(String houseId, Question question) {
        return super.createQuestion(houseId, question);
    }

    @Override
    public Result<String> createReply(String houseId, String questionId, Reply reply) {
        return super.createReply(houseId, questionId, reply);
    }

    @Override
    public Result<List<Question>> listHouseQuestions(String houseId) {
        return super.listHouseQuestions(houseId);
    }

}
