package scc.server.resources;

import jakarta.ws.rs.core.Cookie;
import scc.data.dto.*;
import scc.server.service.RestHouses;
import scc.storage.Database;

import java.util.List;

public class HousesResource extends Resource implements RestHouses {

    public HousesResource(Database db){
        super(db);
    }

    @Override
    public String createHouse(Cookie session, House house) {
        return super.getResult(() -> db.createHouse(session, house));
    }

    @Override
    public House getHouse(String houseId) {
        return super.getResult(() -> db.getHouse(houseId));
    }

    @Override
    public House deleteHouse(Cookie session, String houseId) {
        return super.getResult(() -> db.deleteHouse(session, houseId));
    }

    @Override
    public House updateHouse(Cookie session, String houseId, House house) {
        return super.getResult(() -> db.updateHouse(session, houseId, house));
    }

    @Override
    public List<House> searchHouses(String location, String startDate, String endDate) {
        return super.getResult(() -> db.searchHouses(location, startDate, endDate));
    }

    @Override
    public String createRental(Cookie session, String houseId, Rental rental) {
        return super.getResult(() -> db.createRental(session, houseId, rental));
    }

    @Override
    public Rental updateRental(Cookie session, String houseId, String rentalId, Rental rental) {
        return super.getResult(() -> db.updateRental(session, houseId, rentalId, rental));
    }

    @Override
    public List<Rental> listHouseRentals(Cookie session, String houseId) {
        return super.getResult(() -> db.listHouseRentals(session, houseId));
    }

    @Override
    public List<DiscountedRental> listDiscountedRentals() {
        return super.getResult(db::listDiscountedRentals);
    }

    @Override
    public String createQuestion(Cookie session, String houseId, Question question) {
        return super.getResult(() -> db.createQuestion(session, houseId, question));
    }

    @Override
    public String createReply(Cookie session, String houseId, String questionId, Reply reply) {
        return super.getResult(() -> db.createReply(session, houseId, questionId, reply));
    }

    @Override
    public List<Question> listHouseQuestions(String houseId, Boolean answered) {
        return super.getResult(() -> db.listHouseQuestions(houseId, answered));
    }

}
