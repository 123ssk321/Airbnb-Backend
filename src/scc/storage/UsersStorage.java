package scc.storage;

import scc.data.dao.UserDAO;

import java.util.List;

public interface UsersStorage {

    UserDAO putUser(UserDAO user);

    UserDAO getUser(String userId);

    List<UserDAO> getUsers();

    void delUserById(String id);

    void delUser(UserDAO user);

}
