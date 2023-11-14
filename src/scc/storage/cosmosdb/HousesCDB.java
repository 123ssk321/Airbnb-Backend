package scc.storage.cosmosdb;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import scc.data.dao.HouseDAO;
import scc.data.dto.DiscountedRental;

import java.time.LocalDate;
import java.util.logging.Logger;

public class HousesCDB {
    private final CosmosContainer container;
    private static final Logger Log = Logger.getLogger(HousesCDB.class.getName());

    public HousesCDB(CosmosContainer container) {
        this.container = container;
    }

    public CosmosItemResponse<HouseDAO> putHouse(HouseDAO house){
        return container.createItem(house);
    }

    public HouseDAO getHouse(String houseId){
        try {
            var houses = container.queryItems(
                    "SELECT * FROM houses WHERE houses.id=\"" + houseId + "\"",
                    new CosmosQueryRequestOptions(),
                    HouseDAO.class);
            var houseIt = houses.iterator();
            return houseIt.hasNext()? houseIt.next() : null;
        } catch (Exception e){
            Log.info("Exception caught:" + e);
            return null;
        }

    }

    public CosmosItemResponse<Object> deleteHouseById(String houseId) {
        PartitionKey key = new PartitionKey(houseId);
        return container.deleteItem(houseId, key, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<Object> deleteHouse(HouseDAO house) {
        return container.deleteItem(house, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<HouseDAO> updateHouse(String houseId, CosmosPatchOperations updateOps){
        PartitionKey key = new PartitionKey(houseId);
        return container.patchItem(houseId, key, updateOps, HouseDAO.class);
    }

    public CosmosPagedIterable<HouseDAO> searchHouses(String location, String startDate, String endDate, int start, int length) {
        if(startDate == null && endDate == null){
            return container.queryItems(
                    "SELECT DISTINCT houses.id, houses.name, houses.ownerId, houses.location, houses.description, houses.photoIds, houses.periods " +
                            "FROM houses " +
                            "JOIN p IN houses.periods " +
                            "WHERE houses.location=\"" + location + "\" AND p.available = true " +
                            "OFFSET " + start + " LIMIT " + length,
                    new CosmosQueryRequestOptions(),
                    HouseDAO.class);
        }
        return container.queryItems(
                "SELECT DISTINCT houses.id, houses.name, houses.ownerId, houses.location, houses.description, houses.photoIds, houses.periods " +
                        "FROM houses " +
                        "JOIN p IN houses.periods " +
                        "WHERE houses.location=\"" + location + "\" AND p.available = true AND p.startDate >= \"" + startDate + "\" AND p.endDate <= \"" + endDate + "\" " +
                        "OFFSET " + start + " LIMIT " + length,
                new CosmosQueryRequestOptions(),
                HouseDAO.class);
    }

    public CosmosPagedIterable<DiscountedRental> getDiscountedHouses(int start, int length) {
        var now = LocalDate.now();
        var in2Weeks = now.plusWeeks(2);
        Log.info("Getting discounted houses");
        return container.queryItems(
                "SELECT DISTINCT houses.id, houses.name, houses.ownerId, houses.location, houses.photoIds[0] as photoId, " +
                                "availablePeriods.p as period " +
                        "FROM houses " +
                        "JOIN (SELECT p FROM p IN houses.periods " +
                            "WHERE p.promotionPrice <= p.price AND p.available = true " +
                            "AND p.startDate >= \"" + now + "\" AND p.startDate <= \"" + in2Weeks + "\") AS availablePeriods " +
                        "OFFSET " + start + " LIMIT " + length,
                new CosmosQueryRequestOptions(),
                DiscountedRental.class);
    }

    public CosmosPagedIterable<HouseDAO> getHousesByOwner(String ownerId, int start, int length) {
        return container.queryItems(
                "SELECT * FROM houses WHERE houses.ownerId=\"" + ownerId + "\" " +
                        "OFFSET " + start + " LIMIT " + length,
                new CosmosQueryRequestOptions(),
                HouseDAO.class);
    }

}
