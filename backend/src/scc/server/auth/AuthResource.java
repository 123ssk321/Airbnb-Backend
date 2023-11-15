package scc.server.auth;

import jakarta.ws.rs.core.Response;
import scc.server.resources.Resource;
import scc.storage.Database;

public class AuthResource extends Resource implements RestAuth {

    public AuthResource(Database db) {
        super(db);
    }
    @Override
    public Response auth(LoginDetails loginDetails) {
        return super.getResult(() -> db.auth(loginDetails));
    }
}
