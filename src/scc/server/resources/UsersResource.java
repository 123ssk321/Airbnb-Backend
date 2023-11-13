package scc.server.resources;

import jakarta.ws.rs.core.Cookie;
import scc.data.dto.House;
import scc.data.dto.Rental;
import scc.data.dto.User;
import scc.server.service.RestUsers;
import scc.storage.Database;

import java.util.List;

public class UsersResource extends Resource implements RestUsers {

    public UsersResource(Database db) {
        super(db);
    }

    @Override
    public User createUser(User user) {
        return super.getResult(() -> super.db.createUser(user));
    }

//    @Override
//    public User getUser(Cookie session, String userId) {
//        return super.getResult(() -> super.db.getUser(session, userId));
//    }

    @Override
    public User deleteUser(Cookie session, String userId) {
        return super.getResult(() -> db.deleteUser(session, userId));
    }

    @Override
    public User updateUser(Cookie session, String userId, User user) {
        return super.getResult(() -> db.updateUser(session, userId, user));
    }

    @Override
    public List<House> listUserHouses(Cookie session, String ownerId) {
        return super.getResult(() -> db.listUserHouses(session, ownerId));
    }

    @Override
    public List<Rental> listUserRentals(Cookie session, String userId) {
        return super.getResult(() -> db.listUserRentals(session, userId));
    }
}
