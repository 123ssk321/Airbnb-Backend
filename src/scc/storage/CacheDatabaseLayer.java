package scc.storage;

import com.azure.cosmos.CosmosClient;
import com.azure.storage.blob.BlobContainerClient;
import scc.data.dao.HouseDAO;
import scc.data.dao.UserDAO;
import scc.data.dto.*;
import scc.server.resources.MediaResource;
import scc.utils.Result;

import java.util.List;

// TODO
public class CacheDatabaseLayer extends DatabaseLayer implements Database {

    public CacheDatabaseLayer(CosmosClient cClient,
                              String cosmosdbDatabase,
                              String userCosmosDBContainerName,
                              String houseCosmosDBContainerName,
                              String rentalCosmosDBContainerName,
                              String questionCosmosDBContainerName,
                              BlobContainerClient userBlobContainer,
                              BlobContainerClient houseBlobContainer) {
        super(cClient, cosmosdbDatabase, userCosmosDBContainerName, houseCosmosDBContainerName, rentalCosmosDBContainerName, questionCosmosDBContainerName, userBlobContainer, houseBlobContainer);
    }

    @Override
    protected UserDAO getUser(String userId) {
        return super.getUser(userId);
    }

    @Override
    protected boolean hasUser(String userId) {
        return super.hasUser(userId);
    }

    @Override
    protected HouseDAO getHouse(String houseId) {
        return super.getHouse(houseId);
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
        return super.deleteUser(userId, password);
    }

    @Override
    public Result<User> updateUser(String userId, String password, User user) {
        return super.updateUser(userId, password, user);
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
        return super.deleteHouse(houseId);
    }

    @Override
    public Result<House> updateHouse(String houseId, House house) {
        return super.updateHouse(houseId, house);
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
