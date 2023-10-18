package scc.utils;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 *
 * Utility class for catching server exceptions and showing the stack trace.
 *
 *
 * @author smd
 *
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {

        if (ex instanceof WebApplicationException wex) {
            Response r = wex.getResponse();
            if( r.getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode())
                ex.printStackTrace();
            return r;
        }

        ex.printStackTrace();
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.APPLICATION_JSON).build();
    }

}
