package scc.storage.cosmosdb;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import scc.data.dao.RentalDAO;

import java.util.logging.Logger;

public class RentalsCDB {
    private final CosmosContainer container;
    private static final Logger Log = Logger.getLogger(RentalsCDB.class.getName());

    public RentalsCDB(CosmosContainer container) {
        this.container = container;
    }
    
    public CosmosItemResponse<RentalDAO> putRental(RentalDAO rental){
        return container.createItem(rental);
    }

    public RentalDAO getRental(String rentalId){
        try {
            var rentals = container.queryItems(
                    "SELECT * FROM rentals WHERE rentals.id=\"" + rentalId + "\"",
                    new CosmosQueryRequestOptions(),
                    RentalDAO.class);
            var rentalIt = rentals.iterator();
            return rentalIt.hasNext()? rentalIt.next() : null;
        } catch (Exception e){
            Log.info("Execption caught:" + e);
            return null;
        }

    }

    public boolean hasRental(String rentalId){
        return this.getRental(rentalId) != null;
    }

    public CosmosItemResponse<Object> deleteRentalById(String rentalId) {
        PartitionKey key = new PartitionKey(rentalId);
        return container.deleteItem(rentalId, key, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<Object> deleteRental(RentalDAO rental) {
        return container.deleteItem(rental, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<RentalDAO> updateRental(String rentalId, CosmosPatchOperations updateOps){
        PartitionKey key = new PartitionKey(rentalId);
        return container.patchItem(rentalId, key, updateOps, RentalDAO.class);
    }

    public CosmosPagedIterable<RentalDAO> getRentalsByUser(String userId, int start, int length) {
        return container.queryItems(
                "SELECT * FROM rentals WHERE rentals.tenantId=\"" + userId + "\" " +
                        "OFFSET " + start + " LIMIT " + length,
                new CosmosQueryRequestOptions(),
                RentalDAO.class);
    }
    public CosmosPagedIterable<RentalDAO> getRentalsByHouse(String houseId, int start, int length) {
        return container.queryItems(
                "SELECT * FROM rentals WHERE rentals.houseId=\"" + houseId + "\" " +
                        "OFFSET " + start + " LIMIT " + length,
                new CosmosQueryRequestOptions(),
                RentalDAO.class);
    }

}
