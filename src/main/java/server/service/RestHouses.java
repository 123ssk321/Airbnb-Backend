package server.service;

import java.util.*;

import data.dto.House;
import data.dto.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path(RestHouses.PATH)
public interface RestHouses {
    static final String PATH="/house";
    /**
     * Creates a new house.
     *
     * @param house House to be created.
     * @return 200 the houseId.
     *         409 if the house id already exists.
     *         400 otherwise.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    String createHouse(House house);

    /**
     * Deletes the house identified by houseId.
     *
     * @param houseId the userId of the user
     * @return 200 the deleted house object
     *         404 if no house exists with the provided houseId
     */
    @DELETE
    @Path("/{houseId}")
    @Produces(MediaType.APPLICATION_JSON)
    User deleteHouse(@PathParam("houseId") String houseId);

    /**
     * Modifies the information of a house. Values of null in any field of the house will be
     * considered as if the fields is not to be modified (the id cannot be modified).
     *
     * @param houseId the houseId of the user
     * @param house Updated information
     * @return 200 the updated house object
     *         404 if no house exists with the provided houseId
     */
    @PUT
    @Path("/{houseId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    House updateHouse(@PathParam("houseId") String houseId, House house);

    /**
     * Returns the list of available houses for a given location.
     *
     * @param location substring to search
     * @return 200 when the search is successful, regardless of the number of hits (including 0 hits)
     *         400 otherwise.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<House> listHousesByLocation(@QueryParam("location") String location);

    /**
     * Returns the list of houses of a given user.
     *
     * @param userId ID of the user
     * @return 200 when the search is successful, regardless of the number of hits (including 0 hits)
     *         400 otherwise.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<House> listUserHouses(@QueryParam("userId") String userId);
}
