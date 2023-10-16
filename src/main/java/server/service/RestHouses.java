package server.service;

import java.util.*;

import data.dto.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path(RestHouses.PATH)
public interface RestHouses {
    static final String PATH = "/house";
    static final String LOCATION = "location";
    static final String USERID = "userId";

    /**
     * Creates a new house.
     *
     * @param house House to be created.
     * @return 200 the houseId.
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
    House deleteHouse(@PathParam("houseId") String houseId);

    /**
     * Modifies the information of a house. Values of null in any field of the house will be
     * considered as if the fields is not to be modified (the id and ownerId cannot be modified).
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

    /**
     * Creates a new rental for a given house.
     *
     * @param houseId the ID of the house.
     * @param rental the Rental to be created.
     * @return 200 the rentalId.
     *         404 if the house does not exist.
     *         400 otherwise.
     */
    @POST
    @Path("/{houseId}/rental")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    String createRental(@PathParam("houseId") String houseId, Rental rental);


    /**
     * Modifies the information of a rental. Values of null in any field of the rental will be
     * considered as if the fields is not to be modified (the id cannot be modified).
     *
     * @param houseId the ID of the house of the rental.
     * @param rentalId the ID of the rental.
     * @param rental Updated information.
     * @return 200 the updated house object.
     *         404 if no house exists with the provided houseId.
     *         400 otherwise.
     */
    @PUT
    @Path("/{houseId}/rental/{rentalId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Rental updateRental(@PathParam("houseId") String houseId, @PathParam("rentalId") String rentalId, Rental rental);

    /**
     * Returns the list of rentals for a given house.
     *
     * @param houseId the ID of the house.
     * @return 200 when the search is successful, regardless of the number of hits (including 0 hits)
     *         404 if houseId does not exist.
     *         400 otherwise.
     */
    @GET
    @Path("/{houseId}/rental")
    @Produces(MediaType.APPLICATION_JSON)
    List<Rental> listRentals(@PathParam("houseId") String houseId);

    /**
     * Returns the list of discounted rentals in the near future (2 weeks).
     *
     * @return 200 when the search is successful, regardless of the number of hits (including 0 hits)
     *         400 otherwise.
     */
    @GET
    @Path("/discount")
    @Produces(MediaType.APPLICATION_JSON)
    List<Rental> listDiscountedRentals();

    /**
     * Creates a new question for a given house.
     *
     * @param houseId the ID of the house.
     * @param question Question to be created.
     * @return 200 the questionId.
     *         404 if the house id does not exist.
     *         400 otherwise.
     */
    @POST
    @Path("/{houseId}/question")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    String createQuestion(@PathParam("houseId") String houseId, Question question);

    /**
     * Creates a reply for a question of a given house.
     *
     * @param houseId the ID of the house.
     * @param questionId the ID of the question.
     * @param reply the Reply to be created.
     * @return 200 the reply was created.
     *         403 if the author of the reply is not the owner of the house.
     *         404 if the houseId does not exist or the questionId does not exist.
     *         400 otherwise.
     */
    @POST
    @Path("/{houseId}/question/{questionId}")
    @Consumes(MediaType.APPLICATION_JSON)
    void createReply(@PathParam("houseId") String houseId, @PathParam("questionId") String questionId, Reply reply);

    /**
     * Returns the list of questions for a given house.
     *
     * @param houseId the ID of the house
     * @return 200 when the search is successful, regardless of the number of hits (including 0 hits)
     *         400 otherwise.
     */
    @GET
    @Path("{houseId}/question")
    @Produces(MediaType.APPLICATION_JSON)
    List<House> listHouseQuestions(@PathParam("houseId") String houseId);

}
