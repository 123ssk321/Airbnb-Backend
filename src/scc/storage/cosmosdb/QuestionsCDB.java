package scc.storage.cosmosdb;

import com.azure.cosmos.CosmosContainer;

public class QuestionsCDB {
    private final CosmosContainer container;

    public QuestionsCDB(CosmosContainer container) {
        this.container = container;
    }

}
