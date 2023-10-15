package storage;

import data.dto.*;
import server.service.RestHouses;
import server.service.RestMedia;
import server.service.RestUsers;
import storage.cosmosdb.HousesCDB;
import storage.cosmosdb.QuestionsCDB;
import storage.cosmosdb.RentalsCDB;
import storage.cosmosdb.UsersCDB;
import utils.Result;

import java.util.List;

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
        return null;
    }


    public Result<House> deleteHouse(String houseId) {
        return null;
    }


    public Result<House> updateHouse(String houseId, House house) {
        return null;
    }


    public Result<List<House>> listHousesByLocation(String location) {
        return null;
    }


    public Result<List<House>> listUserHouses(String userId) {
        return null;
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
