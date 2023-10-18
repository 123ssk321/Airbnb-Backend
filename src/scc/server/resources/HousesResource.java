package scc.server.resources;

import scc.data.dto.House;
import scc.data.dto.Question;
import scc.data.dto.Rental;
import scc.data.dto.Reply;
import scc.server.service.RestHouses;
import scc.storage.DatabaseLayer;

import java.util.List;

public class HousesResource extends Resource implements RestHouses {
    private final DatabaseLayer db;

    public HousesResource(DatabaseLayer db){
        super();
        this.db = db;
    }

    @Override
    public String createHouse(House house) {
        return super.getResult(() -> db.createHouse(house));
    }

    @Override
    public House deleteHouse(String houseId) {
        return super.getResult(() -> db.deleteHouse(houseId));
    }

    @Override
    public House updateHouse(String houseId, House house) {
        return super.getResult(() -> db.updateHouse(houseId, house));
    }

    @Override
    public List<House> listHousesByLocation(String location) {
        return super.getResult(() -> db.listHousesByLocation(location));
    }

    @Override
    public List<House> listUserHouses(String ownerId) {
        return super.getResult(() -> db.listUserHouses(ownerId));
    }

    @Override
    public String createRental(String houseId, Rental rental) {
        return super.getResult(() -> db.createRental(houseId, rental));
    }

    @Override
    public Rental updateRental(String houseId, String rentalId, Rental rental) {
        return super.getResult(() -> db.updateRental(houseId, rentalId, rental));
    }

    @Override
    public List<Rental> listRentals(String houseId) {
        return super.getResult(() -> db.listRentals(houseId));
    }

    @Override
    public List<Rental> listDiscountedRentals() {
        return super.getResult(db::listDiscountedRentals);
    }

    @Override
    public String createQuestion(String houseId, Question question) {
        return super.getResult(() -> db.createQuestion(houseId, question));
    }

    @Override
    public void createReply(String houseId, String questionId, Reply reply) {
        super.getResult(() -> db.createReply(houseId, questionId, reply));
    }

    @Override
    public List<House> listHouseQuestions(String houseId) {
        return super.getResult(() -> db.listHouseQuestions(houseId));
    }

}
