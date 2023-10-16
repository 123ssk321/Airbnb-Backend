package server.resources;

import data.dto.User;
import server.service.RestUsers;
import storage.DatabaseLayer;

public class UsersResource extends Resource implements RestUsers {
    private final DatabaseLayer db;

    public UsersResource(DatabaseLayer db) {
        super();
        this.db = db;
    }

    @Override
    public String createUser(User user) {
        return super.getResult(() -> db.createUser(user));
    }

    @Override
    public User deleteUser(String userId, String password) {
        return super.getResult(() -> db.deleteUser(userId, password));
    }

    @Override
    public User updateUser(String userId, String password, User user) {
        return super.getResult(() -> db.updateUser(userId, password, user));
    }
}
