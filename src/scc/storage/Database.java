package scc.storage;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import scc.data.dto.*;
import scc.server.auth.LoginDetails;
import scc.server.resources.MediaResource;
import scc.utils.Result;

import java.util.List;

public interface Database {

    /*---------------------------------------------------- AUTH ------------------------------------------------------*/

    Result<Response> auth(LoginDetails loginDetails);


    /*---------------------------------------------------- USERS -----------------------------------------------------*/

    Result<User> createUser(User user);

    Result<User> deleteUser(Cookie session, String userId);
    
    Result<User> updateUser(Cookie session, String userId, User user);

    Result<List<HouseOwner>> listUserHouses(Cookie session, String ownerId, int start, int length);

    Result<List<Rental>> listUserRentals(Cookie session, String userId, int start, int length);

    /*---------------------------------------------------- MEDIA -----------------------------------------------------*/

    Result<String> uploadMedia(byte[] contents, MediaResource.BlobType type);

    Result<byte[]> downloadMedia(String id, MediaResource.BlobType type);

    /*--------------------------------------------------- HOUSES -----------------------------------------------------*/

    Result<String> createHouse(Cookie session, House house);

    Result<House> getHouse(String houseId);

    Result<House> deleteHouse(Cookie session, String houseId);

    Result<House> updateHouse(Cookie session, String houseId, House house);

    Result<List<HouseList>> searchHouses(String location, String startDate, String endDate, int start, int length);

    Result<List<HouseSearch>> searchByNameAndDescription(String queryText, String ownerId, Boolean useName,
                                                         Boolean useDescription, int start, int length);

    /*-------------------------------------------------- RENTALS -----------------------------------------------------*/

    Result<String> createRental(Cookie session, String houseId, Rental rental);

    Result<Rental> updateRental(Cookie session, String houseId, String rentalId, Rental rental);

    Result<List<Rental>> listHouseRentals(Cookie session, String houseId, int start, int length);

    Result<List<DiscountedRental>> listDiscountedRentals(int start, int length);

    /*------------------------------------------------ QUESTIONS -----------------------------------------------------*/

    Result<String> createQuestion(Cookie session, String houseId, Question question);

    Result<String> createReply(Cookie session, String houseId, String questionId, Reply reply);

    Result<List<Question>> listHouseQuestions(String houseId, Boolean answered, int start, int length);

}
