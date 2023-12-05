package scc.storage.mongodb.collection;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.conversions.Bson;
import scc.data.dao.HouseDAO;
import scc.data.dto.DiscountedRental;
import scc.data.dto.HouseList;
import scc.data.dto.HouseOwner;
import scc.storage.HousesStorage;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HousesCollection implements HousesStorage {
    private final MongoCollection<HouseDAO> collection;
    public HousesCollection(MongoCollection<HouseDAO> collection){
        this.collection = collection;
    }

    @Override
    public HouseDAO putHouse(HouseDAO house) {
        if(collection.insertOne(house).wasAcknowledged())
            return house;
        return null;
    }

    @Override
    public HouseDAO getHouse(String houseId) {
        return collection.find(eq("id", houseId)).first();
    }

    @Override
    public void deleteHouseById(String houseId) {
        collection.deleteOne(eq("id", houseId));
    }

    @Override
    public void deleteHouse(HouseDAO house) {
        collection.deleteOne(eq("id", house.getId()));
    }

    public HouseDAO updateHouse(String houseId, Bson updates){
        return collection.findOneAndUpdate(eq("id", houseId), updates, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }

    @Override
    public List<HouseList> searchHouses(String location, String startDate, String endDate, int start, int length) {
        if(startDate == null && endDate == null){
            return collection.find(and(eq("location", location), eq("periods.available", true)))
                    .projection(fields(include("id", "name", "location"), slice("photoIds", 1), elemMatch("periods"), excludeId()))
                    .skip(start).limit(length).map(HouseDAO::toHouseList).into(new ArrayList<>());
        }
        return collection.find(and(eq("location", location), eq("periods.available", true), gte("periods.startDate", startDate), lte("periods.endDate", endDate)))
                .projection(fields(include("id", "name", "location"), slice("photoIds", 1), elemMatch("periods"), excludeId()))
                .skip(start).limit(length).map(HouseDAO::toHouseList).into(new ArrayList<>());
    }

    @Override
    public List<DiscountedRental> getDiscountedHouses(int start, int length) {
        var now = LocalDate.now();
        var in2Weeks = now.plusWeeks(2);
        return collection.find(and(lte("periods.promotionPrice", "periods.price"), eq("periods.available", true), gte("periods.startDate", now), lte("periods.endDate", in2Weeks)))
                .projection(fields(include("id", "name", "ownerId", "location"), slice("photoIds", 1), elemMatch("periods"), excludeId()))
                .skip(start).limit(length).map(HouseDAO::toDiscountedRental).into(new ArrayList<>());
    }

    @Override
    public List<HouseOwner> getHousesByOwner(String ownerId, int start, int length) {
        return collection.find(eq("ownerId", ownerId))
                .projection(fields(include("id", "name", "ownerId", "location"), slice("photoIds", 1), excludeId()))
                .skip(start).limit(length).map(HouseDAO::toHouseOwner).into(new ArrayList<>());
    }
}
