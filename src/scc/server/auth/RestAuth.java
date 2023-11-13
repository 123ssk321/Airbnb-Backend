package scc.server.auth;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path(RestAuth.PATH)
public interface RestAuth {
    String PATH="/auth";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response auth(LoginDetails loginDetails);

}
