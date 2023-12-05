package scc.storage.mongodb.collection;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.conversions.Bson;
import scc.data.dao.UserDAO;
import scc.storage.UsersStorage;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.empty;
import static com.mongodb.client.model.Filters.eq;

public class UsersCollection implements UsersStorage {
    private final MongoCollection<UserDAO> collection;

    public UsersCollection(MongoCollection<UserDAO> collection){
        this.collection = collection;
    }

    @Override
    public UserDAO putUser(UserDAO user) {
        if(collection.insertOne(user).wasAcknowledged())
            return user;
        return null;
    }

    @Override
    public UserDAO getUser(String userId) {
        return collection.find(eq("id", userId)).first();
    }

    @Override
    public List<UserDAO> getUsers() {
        return collection.find(empty()).into(new ArrayList<>());
    }

    public UserDAO updateUser(String userId, Bson updates){
        return collection.findOneAndUpdate(eq("id", userId), updates, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }

    @Override
    public void delUserById(String id) {
        collection.deleteOne(eq("id", id));
    }

    @Override
    public void delUser(UserDAO user) {
        collection.deleteOne(eq("id", user.getId()));
    }
}