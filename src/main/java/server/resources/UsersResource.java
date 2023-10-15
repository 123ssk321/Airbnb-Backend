package server.resources;

import data.dto.User;
import server.service.RestUsers;
import storage.DatabaseLayer;

public class UsersResource implements RestUsers {
    private final DatabaseLayer db;

    @Override
    public String createUser(User user) {
        return null;
    }

    @Override
    public User deleteUser(String userId, String password) {
        return null;
    }

    @Override
    public User updateUser(String userId, String password, User user) {
        return null;
    }
}
