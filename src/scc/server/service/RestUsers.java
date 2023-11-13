package scc.server.service;

import jakarta.ws.rs.core.Cookie;
import scc.data.dto.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path(RestUsers.PATH)
public interface RestUsers {
    String PATH="/users";

    /**
     * Creates a new user.
     *
     * @param user User to be created.
     * @return 200 the userId.
     *         409 if the user id already exists.
     *         400 otherwise.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    User createUser(User user);

//    /**
//     * Returns the user identified by userId.
//     *
//     * @param session with login details
//     * @param userId the userId of the user
//     * @return 200 the deleted user object, if the name exists and pwd matches the
//     *         existing password
//     *         401 if id and password are incorrect
//     *         404 if no user exists with the provided userId
//     *//*
//    @GET
//    @Path("/{userId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    User getUser(@CookieParam("scc:session") Cookie session,
//                    @PathParam("userId") String userId);*/

    /**
     * Deletes the user identified by userId.
     *
     * @param session with login details
     * @param userId the userId of the user
     * @return 200 the deleted user object, if the name exists and pwd matches the
     *         existing password
     *         401 if id and password are incorrect
     *         404 if no user exists with the provided userId
     */
    @DELETE
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    User deleteUser(@CookieParam("scc:session") Cookie session,
                    @PathParam("userId") String userId);

    /**
     * Modifies the information of a user. Values of null in any field of the user will be
     * considered as if the fields is not to be modified (the id cannot be modified).
     *
     * @param session with login details
     * @param userId the userId of the user
     * @param user Updated information
     * @return 200 the updated user object, if the name exists and password matches the
     *         existing password
     *         401 if id and password are incorrect
     *         404 if no user exists with the provided userId
     *         400 otherwise.
     */
    @PUT
    @Path("/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    User updateUser(@CookieParam("scc:session") Cookie session,
                    @PathParam("userId") String userId,
                    User user);

}
