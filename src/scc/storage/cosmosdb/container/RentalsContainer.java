package scc.storage.cosmosdb.container;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.*;
import scc.data.dao.RentalDAO;
import scc.storage.RentalsStorage;

import java.util.List;
import java.util.logging.Logger;

public class RentalsContainer implements RentalsStorage {
    private final CosmosContainer container;
    private static final Logger Log = Logger.getLogger(RentalsContainer.class.getName());

    public RentalsContainer(CosmosContainer container) {
        this.container = container;
    }
    
    public RentalDAO putRental(RentalDAO rental){
        return container.createItem(rental).getItem();
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

    public void deleteRentalById(String rentalId) {
        PartitionKey key = new PartitionKey(rentalId);
        container.deleteItem(rentalId, key, new CosmosItemRequestOptions());
    }

    public void deleteRental(RentalDAO rental) {
        container.deleteItem(rental, new CosmosItemRequestOptions());
    }

    public RentalDAO updateRental(String rentalId, CosmosPatchOperations updateOps){
        PartitionKey key = new PartitionKey(rentalId);
        return container.patchItem(rentalId, key, updateOps, RentalDAO.class).getItem();
    }

    public List<RentalDAO> getRentalsByUser(String userId, int start, int length) {
        return container.queryItems(
                "SELECT * FROM rentals WHERE rentals.tenantId=\"" + userId + "\" " +
                        "OFFSET " + start + " LIMIT " + length,
                new CosmosQueryRequestOptions(),
                RentalDAO.class).stream().toList();
    }
    public List<RentalDAO> getRentalsByHouse(String houseId, int start, int length) {
        return container.queryItems(
                "SELECT * FROM rentals WHERE rentals.houseId=\"" + houseId + "\" " +
                        "OFFSET " + start + " LIMIT " + length,
                new CosmosQueryRequestOptions(),
                RentalDAO.class).stream().toList();
    }

}
