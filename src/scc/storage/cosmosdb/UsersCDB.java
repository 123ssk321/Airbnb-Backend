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
            Log.info("Selecting an user with id="+userId);
            var users = container.queryItems("SELECT * FROM users WHERE users.id=\"" + userId + "\"",
                    new CosmosQueryRequestOptions(),
                    UserDAO.class);
            Log.info("Getting iterator");
            var userIt = users.iterator();
            Log.info("Checking if query returned any userss");
            var res = userIt.hasNext();
            Log.info("Query returned users: " + res);
            var ret = res? userIt.next() : null;
            return ret;
        } catch (Exception ce){
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
        Log.info("Checking if exists an user with id="+userId);
        return this.getUser(userId) != null;
    }

    public CosmosItemResponse<UserDAO> updateUser(String userId, CosmosPatchOperations updateOps){
        PartitionKey key = new PartitionKey(userId);
        return container.patchItem(userId, key, updateOps, UserDAO.class);
    }

    public CosmosItemResponse<Object> delUserById(String id) {
        PartitionKey key = new PartitionKey(id);
        var response = container.deleteItem(id, key, new CosmosItemRequestOptions());
        Log.info("Response code: " + response.getStatusCode());
        return (CosmosItemResponse<Object>) response.getItem();
        //return container.deleteItem(id, key, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<Object> delUser(UserDAO user) {
        return container.deleteItem(user, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<UserDAO> putUser(UserDAO user) {
        Log.info("Inserting User in database");
        return container.createItem(user);
    }
    
}
