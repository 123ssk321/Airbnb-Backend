package storage;

import com.azure.cosmos.models.CosmosPatchOperations;
import data.dao.HouseDAO;
import data.dto.*;
import jakarta.ws.rs.core.Response;
import storage.cosmosdb.HousesCDB;
import storage.cosmosdb.QuestionsCDB;
import storage.cosmosdb.RentalsCDB;
import storage.cosmosdb.UsersCDB;
import utils.Result;

import java.util.List;
import java.util.UUID;

public class DatabaseLayer {
    private final UsersCDB users;
    private final MediaBlobStorage media;
    private final HousesCDB houses;
    private final RentalsCDB rentals;
    private final QuestionsCDB questions;





    public Result<String> createUser(User user) {
        return null;
    }


    public Result<User> deleteUser(String userId, String password) {
        return null;
    }


    public Result<User> updateUser(String userId, String password, User user) {
        return null;
    }


    public Result<String> upload(byte[] contents) {
        return null;
    }


    public Result<byte[]> download(String id) {
        return new byte[0];
    }


    public Result<String> createHouse(House house) {
        if(house == null || house.getName() == null || house.getLocation() == null || house.getDescription() == null
                || house.getPhotoIds() == null || house.getPhotoIds().length < 1 || !house.isAvailable() || house.getPrice() <= 0
                || house.getPromotionPrice() <= 0) {
            return Result.error(Response.Status.BAD_REQUEST);
        }
        // TODO: Check if owner exists

        house.setId(UUID.randomUUID().toString());
        return Result.ok(houses.putHouse(new HouseDAO(house)).getItem().getId());
    }


    public Result<House> deleteHouse(String houseId) {
        if(houseId == null) {
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(!houses.hasHouse(houseId)){
            return Result.error(Response.Status.NOT_FOUND);
        }
        return Result.ok(((HouseDAO) houses.deleteHouseById(houseId).getItem()).toHouse());
    }

    public Result<House> updateHouse(String houseId, House house) {
        if(houseId == null || house == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(!houses.hasHouse(houseId)){
            return Result.error(Response.Status.NOT_FOUND);
        }
        var updateOps = CosmosPatchOperations.create();
        if(house.getName() != null)
            updateOps.replace("/name", house.getName());
        if(house.getName() != null)
            updateOps.replace("/description", house.getDescription());
        if(house.getName() != null)
            updateOps.replace("/isAvailable", house.isAvailable());
        if(house.getName() != null)
            updateOps.replace("/price", house.getPrice());
        if(house.getName() != null)
            updateOps.replace("/promotionPrice", house.getPromotionPrice());

        return Result.ok(houses.updateHouse(houseId, updateOps).getItem().toHouse());
    }


    public Result<List<House>> listHousesByLocation(String location) {
        if(location == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        return Result.ok(houses.getHousesByLocation(location).stream().map(HouseDAO::toHouse).toList());
    }


    public Result<List<House>> listUserHouses(String userId) {
        if(userId == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        return Result.ok(houses.getHousesByOwner(userId).stream().map(HouseDAO::toHouse).toList());
    }


    public Result<String> createRental(String houseId, Rental rental) {
        return null;
    }


    public Result<Rental> updateRental(String houseId, String rentalId, Rental rental) {
        return null;
    }


    public Result<List<Rental>> listRentals(String houseId) {
        return null;
    }


    public Result<List<Rental>> listDiscountedRentals() {
        return null;
    }


    public Result<String> createQuestion(String houseId, Question question) {
        return null;
    }


    public Result<Void> createReply(String houseId, String questionId, Reply reply) {

    }


    public Result<List<House>> listHouseQuestions(String houseId) {
        return null;
    }

}
