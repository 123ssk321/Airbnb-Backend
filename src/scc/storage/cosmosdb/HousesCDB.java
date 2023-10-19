package scc.storage.cosmosdb;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import scc.data.dao.HouseDAO;

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
            Log.info("Execption caught:" + e);
            return null;
        }

    }

    public boolean hasHouse(String houseId){
        return this.getHouse(houseId) != null;
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

    public CosmosPagedIterable<HouseDAO> getHousesByLocation(String location) {
        return container.queryItems(
                "SELECT * FROM houses WHERE houses.location=\"" + location + "\"",
                new CosmosQueryRequestOptions(),
                HouseDAO.class);
    }

    public CosmosPagedIterable<HouseDAO> getHousesByOwner(String ownerId) {
        return container.queryItems(
                "SELECT * FROM houses WHERE houses.ownerId=\"" + ownerId + "\"",
                new CosmosQueryRequestOptions(),
                HouseDAO.class);
    }

}
