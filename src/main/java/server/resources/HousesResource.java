package server.resources;

import data.dto.House;
import data.dto.Question;
import data.dto.Rental;
import data.dto.Reply;
import server.service.RestHouses;
import storage.DatabaseLayer;

import java.util.List;

public class HousesResource implements RestHouses {
    private final DatabaseLayer db;

    @Override
    public String createHouse(House house) {
        return null;
    }

    @Override
    public House deleteHouse(String houseId) {
        return null;
    }

    @Override
    public House updateHouse(String houseId, House house) {
        return null;
    }

    @Override
    public List<House> listHousesByLocation(String location) {
        return null;
    }

    @Override
    public List<House> listUserHouses(String userId) {
        return null;
    }

    @Override
    public String createRental(String houseId, Rental rental) {
        return null;
    }

    @Override
    public Rental updateRental(String houseId, String rentalId, Rental rental) {
        return null;
    }

    @Override
    public List<Rental> listRentals(String houseId) {
        return null;
    }

    @Override
    public List<Rental> listDiscountedRentals() {
        return null;
    }

    @Override
    public String createQuestion(String houseId, Question question) {
        return null;
    }

    @Override
    public void createReply(String houseId, String questionId, Reply reply) {

    }

    @Override
    public List<House> listHouseQuestions(String houseId) {
        return null;
    }
}
