package storage.cosmosdb;

import com.azure.cosmos.CosmosContainer;

public class HousesCDB {
    private final CosmosContainer container;

    public HousesCDB(CosmosContainer container) {
        this.container = container;
    }

}
