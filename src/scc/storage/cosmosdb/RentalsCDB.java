package scc.storage.cosmosdb;

import com.azure.cosmos.CosmosContainer;

public class RentalsCDB {
    private final CosmosContainer container;

    public RentalsCDB(CosmosContainer container) {
        this.container = container;
    }

}
