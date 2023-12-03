package scc.storage.mongodb.collection;

import com.mongodb.client.MongoCollection;
import org.bson.conversions.Bson;
import scc.data.dao.HouseDAO;
import scc.data.dto.DiscountedRental;
import scc.data.dto.HouseList;
import scc.data.dto.HouseOwner;
import scc.storage.HousesStorage;

import java.util.List;

public class HousesCollection implements HousesStorage {
    private final MongoCollection<HouseDAO> collection;
    public HousesCollection(MongoCollection<HouseDAO> collection){
        this.collection = collection;
    }

    @Override
    public HouseDAO putHouse(HouseDAO house) {
        return null;
    }

    @Override
    public HouseDAO getHouse(String houseId) {
        return null;
    }

    @Override
    public void deleteHouseById(String houseId) {

    }

    @Override
    public void deleteHouse(HouseDAO house) {

    }

    public HouseDAO updateHouse(String houseId, Bson updates){
        return null;
    }

    @Override
    public List<HouseList> searchHouses(String location, String startDate, String endDate, int start, int length) {
        return null;
    }

    @Override
    public List<DiscountedRental> getDiscountedHouses(int start, int length) {
        return null;
    }

    @Override
    public List<HouseOwner> getHousesByOwner(String ownerId, int start, int length) {
        return null;
    }
}
