package scc.storage.cosmosdb;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import scc.data.dao.UserDAO;

import java.util.logging.Logger;


public class UsersCDB {
    private final CosmosContainer container;
    private static final Logger Log = Logger.getLogger(UsersCDB.class.getName());

    public UsersCDB(CosmosContainer container) {
        this.container = container;
    }

    public UserDAO getUser(String userId){
        try{
            var users = container.queryItems("SELECT * FROM users WHERE users.id=\"" + userId + "\"",
                    new CosmosQueryRequestOptions(),
                    UserDAO.class);
            var userIt = users.iterator();
            var res = userIt.hasNext();
            return res? userIt.next() : null;
        } catch (Exception ce){
            Log.info("Execption caught:" + ce);
            return null;
        }
    }

    public CosmosPagedIterable<UserDAO> getUserById(String id) {
        return container.queryItems("SELECT * FROM users WHERE users.id=\"" + id + "\"", new CosmosQueryRequestOptions(), UserDAO.class);
    }

    public CosmosPagedIterable<UserDAO> getUsers() {
        return container.queryItems("SELECT * FROM users ", new CosmosQueryRequestOptions(), UserDAO.class);
    }

    public boolean hasUser(String userId){
        return this.getUser(userId) != null;
    }

    public CosmosItemResponse<UserDAO> updateUser(String userId, CosmosPatchOperations updateOps){
        PartitionKey key = new PartitionKey(userId);
        return container.patchItem(userId, key, updateOps, UserDAO.class);
    }

    public CosmosItemResponse<Object> delUserById(String id) {
        PartitionKey key = new PartitionKey(id);
        return container.deleteItem(id, key, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<Object> delUser(UserDAO user) {
        return container.deleteItem(user, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<UserDAO> putUser(UserDAO user) {
        return container.createItem(user);
    }
    
}
