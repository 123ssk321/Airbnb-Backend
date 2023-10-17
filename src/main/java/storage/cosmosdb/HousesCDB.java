package main.java.storage.cosmosdb;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import main.java.data.dao.HouseDAO;

public class HousesCDB {
    private final CosmosContainer container;

    public HousesCDB(CosmosContainer container) {
        this.container = container;

    }

    public CosmosItemResponse<HouseDAO> putHouse(HouseDAO house){
        return container.createItem(house);
    }

    public HouseDAO getHouse(String houseId){
        var houses = container.queryItems(
                "SELECT * FROM houses WHERE houses.id=\"" + houseId + "\"",
                new CosmosQueryRequestOptions(),
                HouseDAO.class);
        var houseIt = houses.iterator();
        return houseIt.hasNext()? houseIt.next() : null;
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
