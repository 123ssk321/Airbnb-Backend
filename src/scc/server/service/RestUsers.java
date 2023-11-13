package scc.server.service;

import scc.data.dto.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path(RestUsers.PATH)
public interface RestUsers {
    String PATH="/users";
    String PASSWORD = "password";

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

    /**
     * Deletes the user identified by userId.
     *
     * @param userId the userId of the user
     * @param password password of the user
     * @return 200 the deleted user object, if the name exists and pwd matches the
     *         existing password
     *         403 if the password is incorrect
     *         404 if no user exists with the provided userId
     */
    @DELETE
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    User deleteUser(@PathParam("userId") String userId, @QueryParam(PASSWORD) String password);

    /**
     * Modifies the information of a user. Values of null in any field of the user will be
     * considered as if the fields is not to be modified (the id cannot be modified).
     *
     * @param userId the userId of the user
     * @param password password of the user
     * @param user Updated information
     * @return 200 the updated user object, if the name exists and password matches the
     *         existing password
     *         403 if the password is incorrect
     *         404 if no user exists with the provided userId
     *         400 otherwise.
     */
    @PUT
    @Path("/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    User updateUser(@PathParam("userId") String userId, @QueryParam(PASSWORD) String password, User user);

}
