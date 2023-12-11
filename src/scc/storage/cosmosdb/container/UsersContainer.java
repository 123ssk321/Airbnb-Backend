package scc.storage.cosmosdb.container;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.*;
import scc.data.dao.UserDAO;
import scc.storage.UsersStorage;

import java.util.List;
import java.util.logging.Logger;


public class UsersContainer implements UsersStorage {
    private final CosmosContainer container;
    private static final Logger Log = Logger.getLogger(UsersContainer.class.getName());

    public UsersContainer(CosmosContainer container) {
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
            Log.info("Exception caught:" + ce);
            return null;
        }
    }

    public List<UserDAO> getUsers() {
        return container.queryItems("SELECT * FROM users ", new CosmosQueryRequestOptions(), UserDAO.class).stream().toList();
    }

    public UserDAO updateUser(String userId, CosmosPatchOperations updateOps){
        PartitionKey key = new PartitionKey(userId);
        return container.patchItem(userId, key, updateOps, UserDAO.class).getItem();
    }

    public void delUserById(String id) {
        PartitionKey key = new PartitionKey(id);
        container.deleteItem(id, key, new CosmosItemRequestOptions());
    }

    public void delUser(UserDAO user) {
        container.deleteItem(user, new CosmosItemRequestOptions());
    }

    public UserDAO putUser(UserDAO user) {
        return container.createItem(user).getItem();
    }
    
}
