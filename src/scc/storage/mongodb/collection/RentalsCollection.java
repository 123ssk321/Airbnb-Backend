package scc.storage.mongodb.collection;

import com.mongodb.client.MongoCollection;
import org.bson.conversions.Bson;
import scc.data.dao.RentalDAO;
import scc.storage.RentalsStorage;

import java.util.List;

public class RentalsCollection implements RentalsStorage {
    private final MongoCollection<RentalDAO> collection;

    public RentalsCollection(MongoCollection<RentalDAO> collection){
        this.collection = collection;
    }
    @Override
    public RentalDAO putRental(RentalDAO rental) {
        return null;
    }

    @Override
    public RentalDAO getRental(String rentalId) {
        return null;
    }

    @Override
    public boolean hasRental(String rentalId) {
        return false;
    }

    @Override
    public void deleteRentalById(String rentalId) {

    }

    @Override
    public void deleteRental(RentalDAO rental) {

    }

    public RentalDAO updateRental(String rentalId, Bson updates){
        return null;
    }

    @Override
    public List<RentalDAO> getRentalsByUser(String userId, int start, int length) {
        return null;
    }

    @Override
    public List<RentalDAO> getRentalsByHouse(String houseId, int start, int length) {
        return null;
    }
}
