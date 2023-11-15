package scc.server.service;

import jakarta.ws.rs.core.Cookie;
import scc.data.dto.House;
import scc.data.dto.HouseOwner;
import scc.data.dto.Rental;
import scc.data.dto.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path(RestUsers.PATH)
public interface RestUsers {
    String PATH="/users";
    String START= "st";
    String LENGTH= "len";

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

    /**
     * Returns the list of houses of a given user.
     *
     * @param session with login details
     * @param ownerId ID of the user
     * @param start  the number of results to skip
     * @param length  the number of results to return
     * @return 200 when the search is successful, regardless of the number of hits (including 0 hits)
     *         401 if userId and password are incorrect.
     *         400 otherwise.
     */
    @GET
    @Path("/{ownerId}/house")
    @Produces(MediaType.APPLICATION_JSON)
    List<HouseOwner> listUserHouses(@CookieParam("scc:session") Cookie session,
                                    @PathParam("ownerId") String ownerId,
                                    @QueryParam(START) int start,
                                    @QueryParam(LENGTH) int length);

    /**
     * Returns the list of rentals for a given user.
     *
     * @param session with login details
     * @param userId the ID of the user
     * @param start  the number of results to skip
     * @param length  the number of results to return
     * @return 200 when the search is successful, regardless of the number of hits (including 0 hits)
     *         401 if userId and password are incorrect.
     *         404 if user does not exist.
     *         400 otherwise.
     */
    @GET
    @Path("/{userId}/rental")
    @Produces(MediaType.APPLICATION_JSON)
    List<Rental> listUserRentals(@CookieParam("scc:session") Cookie session,
                                 @PathParam("userId") String userId,
                                 @QueryParam(START) int start,
                                 @QueryParam(LENGTH) int length);

}
