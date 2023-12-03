package scc.storage.mongodb.collection;

import com.mongodb.client.MongoCollection;
import org.bson.conversions.Bson;
import scc.data.dao.UserDAO;
import scc.storage.UsersStorage;

import java.util.List;

public class UsersCollection implements UsersStorage {
    private final MongoCollection<UserDAO> collection;

    public UsersCollection(MongoCollection<UserDAO> collection){
        this.collection = collection;
    }

    @Override
    public UserDAO putUser(UserDAO user) {
        return null;
    }

    @Override
    public UserDAO getUser(String userId) {
        return null;
    }

    @Override
    public List<UserDAO> getUsers() {
        return null;
    }

    public UserDAO updateUser(String userId, Bson updates){
        return null;
    }

    @Override
    public void delUserById(String id) {

    }

    @Override
    public void delUser(UserDAO user) {

    }
}
