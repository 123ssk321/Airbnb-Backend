package scc.storage;

import scc.data.dto.*;
import scc.server.resources.MediaResource;
import scc.utils.Result;

import java.util.List;

public interface Database {
    
    boolean hasHouse(String houseId);
    
    Result<String> createUser(User user);

    Result<User> deleteUser(String userId, String password);
    
    Result<User> updateUser(String userId, String password, User user);


    Result<String> uploadMedia(byte[] contents, MediaResource.BlobType type);

    Result<byte[]> downloadMedia(String id, MediaResource.BlobType type);

    Result<String> createHouse(House house);

    Result<House> deleteHouse(String houseId);

    Result<House> updateHouse(String houseId, House house);

    Result<List<House>> searchHouses(String location, String startDate, String endDate);

    Result<List<House>> listUserHouses(String ownerId);

    Result<String> createRental(String houseId, Rental rental);

    Result<Rental> updateRental(String houseId, String rentalId, Rental rental);

    Result<List<Rental>> listRentals(String houseId);

    Result<List<DiscountedRental>> listDiscountedRentals();

    Result<String> createQuestion(String houseId, Question question);

    Result<String> createReply(String houseId, String questionId, Reply reply);

    Result<List<Question>> listHouseQuestions(String houseId);
    
}
