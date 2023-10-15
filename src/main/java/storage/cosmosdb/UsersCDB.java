package storage.cosmosdb;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import data.dao.UserDAO;

public class UsersCDB {
    private final CosmosContainer container;

    public UsersCDB(CosmosContainer container) {
        this.container = container;
    }

    public CosmosItemResponse<Object> delUserById(String id) {
        PartitionKey key = new PartitionKey( id);
        return container.deleteItem(id, key, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<Object> delUser(UserDAO user) {
        return container.deleteItem(user, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<UserDAO> putUser(UserDAO user) {
        return container.createItem(user);
    }

    public CosmosPagedIterable<UserDAO> getUserById(String id) {
        return container.queryItems("SELECT * FROM users WHERE users.id=\"" + id + "\"", new CosmosQueryRequestOptions(), UserDAO.class);
    }

    public CosmosPagedIterable<UserDAO> getUsers() {
        return container.queryItems("SELECT * FROM users ", new CosmosQueryRequestOptions(), UserDAO.class);
    }
    
}
