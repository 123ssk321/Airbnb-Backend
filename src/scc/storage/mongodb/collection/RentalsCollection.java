package scc.storage.mongodb.collection;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.conversions.Bson;
import scc.data.dao.RentalDAO;
import scc.storage.RentalsStorage;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class RentalsCollection implements RentalsStorage {
    private final MongoCollection<RentalDAO> collection;

    public RentalsCollection(MongoCollection<RentalDAO> collection){
        this.collection = collection;
    }
    @Override
    public RentalDAO putRental(RentalDAO rental) {
        if(collection.insertOne(rental).wasAcknowledged())
            return rental;
        return null;
    }

    @Override
    public RentalDAO getRental(String rentalId) {
        return collection.find(eq("_id", rentalId), RentalDAO.class).first();
    }

    @Override
    public boolean hasRental(String rentalId) {
        return this.getRental(rentalId) != null;
    }

    @Override
    public void deleteRentalById(String rentalId) {
        collection.deleteOne(eq("_id", rentalId));
    }

    @Override
    public void deleteRental(RentalDAO rental) {
        collection.deleteOne(eq("_id", rental.getId()));
    }

    public RentalDAO updateRental(String rentalId, Bson updates){
        return collection.findOneAndUpdate(eq("_id", rentalId), updates, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }

    @Override
    public List<RentalDAO> getRentalsByUser(String userId, int start, int length) {
        return collection.find(eq("tenantId", userId), RentalDAO.class).skip(start).limit(length).into(new ArrayList<>());
    }

    @Override
    public List<RentalDAO> getRentalsByHouse(String houseId, int start, int length) {
        return collection.find(eq("houseId", houseId), RentalDAO.class).skip(start).limit(length).into(new ArrayList<>());
    }
}
