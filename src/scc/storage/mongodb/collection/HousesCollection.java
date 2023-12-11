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
        return collection.find(eq("_id", houseId), HouseDAO.class).first();
    }

    @Override
    public void deleteHouseById(String houseId) {
        collection.deleteOne(eq("_id", houseId));
    }

    @Override
    public void deleteHouse(HouseDAO house) {
        collection.deleteOne(eq("_id", house.getId()));
    }

    public HouseDAO updateHouse(String houseId, Bson updates){
        return collection.findOneAndUpdate(eq("_id", houseId), updates, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }

    @Override
    public List<HouseList> searchHouses(String location, String startDate, String endDate, int start, int length) {
        if(startDate == null && endDate == null){
            return collection.find(and(eq("location", location), eq("periods.available", true)), HouseList.class)
                    .projection(fields(include("_id", "name", "location"), slice("photoIds", 1), elemMatch("periods")))
                    .skip(start).limit(length).into(new ArrayList<>());
        }
        return collection.find(and(eq("location", location), eq("periods.available", true), gte("periods.startDate", startDate), lte("periods.endDate", endDate)), HouseList.class)
                .projection(fields(include("_id", "name", "location"), slice("photoIds", 1), elemMatch("periods")))
                .skip(start).limit(length).into(new ArrayList<>());
    }

    @Override
    public List<DiscountedRental> getDiscountedHouses(int start, int length) {
        var now = LocalDate.now();
        var in2Weeks = now.plusWeeks(2);
        return collection.find(and(lte("periods.promotionPrice", "periods.price"), eq("periods.available", true), gte("periods.startDate", now), lte("periods.endDate", in2Weeks)), DiscountedRental.class)
                .projection(fields(include("_id", "name", "ownerId", "location"), slice("photoIds", 1), elemMatch("periods")))
                .skip(start).limit(length).into(new ArrayList<>());
    }

    @Override
    public List<HouseOwner> getHousesByOwner(String ownerId, int start, int length) {
        return collection.find(eq("ownerId", ownerId), HouseOwner.class)
                .projection(fields(include("_id", "name", "ownerId", "location"), slice("photoIds", 1)))
                .skip(start).limit(length).into(new ArrayList<>());
    }
}
