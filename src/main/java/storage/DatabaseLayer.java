package storage;

import com.azure.cosmos.models.CosmosPatchOperations;
import data.dao.HouseDAO;
import data.dao.UserDAO;
import data.dto.*;
import jakarta.ws.rs.core.Response;
import storage.cosmosdb.HousesCDB;
import storage.cosmosdb.QuestionsCDB;
import storage.cosmosdb.RentalsCDB;
import storage.cosmosdb.UsersCDB;
import utils.Result;

import java.util.List;
import java.util.UUID;

import static server.resources.MediaResource.HOUSE_MEDIA;
import static server.resources.MediaResource.USER_MEDIA;

public class DatabaseLayer {

    private final UsersCDB users;
    private final MediaBlobStorage media;
    private final HousesCDB houses;
    private final RentalsCDB rentals;
    private final QuestionsCDB questions;


    public Result<String> createUser(User user) {
        if(user == null || user.getName() == null || user.getPwd() == null ||user.getPhotoId() == null || user.getHouseIds() == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(user.getId() != null)
            if(users.hasUser(user.getId()))
                return Result.error(Response.Status.CONFLICT);

        user.setId(UUID.randomUUID().toString());
        return Result.ok(users.putUser(new UserDAO(user)).getItem().getId());
    }

    public Result<User> deleteUser(String userId, String password) {
        if(userId == null || password == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        UserDAO user = users.getUser(userId);
        if(user == null){
            return Result.error(Response.Status.NOT_FOUND);
        }
        if(!user.getPwd().equals(password)){
            return Result.error(Response.Status.FORBIDDEN);
        }
        return Result.ok(((UserDAO) users.delUserById(userId).getItem()).toUser());
    }


    public Result<User> updateUser(String userId, String password, User user) {
        if(userId == null || password == null || user == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        UserDAO dbUser = users.getUser(userId);
        if(dbUser == null){
            return Result.error(Response.Status.NOT_FOUND);
        }
        if(!dbUser.getPwd().equals(password)){
            return Result.error(Response.Status.FORBIDDEN);
        }

        var updateOps = CosmosPatchOperations.create();
        if(user.getName() != null)
            updateOps.replace("/name", user.getName());
        if(user.getPwd() != null)
            updateOps.replace("/pwd", user.getPwd());
        if(user.getPhotoId() != null)
            updateOps.replace("/photoId", user.getPhotoId());

        return Result.ok(users.updateUser(userId, updateOps).getItem().toUser());
    }


    public Result<String> uploadMedia(byte[] contents, String type) {
        if (contents == null)
            return Result.error(Response.Status.BAD_REQUEST);

        return switch (type) {
            case USER_MEDIA -> Result.ok(media.uploadUserPhoto(contents));
            case HOUSE_MEDIA -> Result.ok(media.uploadHousePhoto(contents));
            default -> null;
        };
    }


    public Result<byte[]> downloadMedia(String id, String type) {
        if (id == null)
            return Result.error(Response.Status.BAD_REQUEST);

        var mediaValue = switch (type) {
            case USER_MEDIA -> media.downloadUserPhoto(id);
            case HOUSE_MEDIA -> media.downloadHousePhoto(id);
            default -> null;
        };
        if (mediaValue == null || mediaValue.length == 0)
            return Result.error(Response.Status.NOT_FOUND);

        return Result.ok(mediaValue);
    }


    public Result<String> createHouse(House house) {
        if(house == null || house.getName() == null || house.getLocation() == null || house.getDescription() == null
                || house.getPhotoIds() == null || house.getPhotoIds().length < 1 || !house.isAvailable() || house.getPrice() <= 0
                || house.getPromotionPrice() <= 0) {
            return Result.error(Response.Status.BAD_REQUEST);
        }
        // TODO: Check if owner exists

        house.setId(UUID.randomUUID().toString());
        return Result.ok(houses.putHouse(new HouseDAO(house)).getItem().getId());
    }


    public Result<House> deleteHouse(String houseId) {
        if(houseId == null) {
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(!houses.hasHouse(houseId)){
            return Result.error(Response.Status.NOT_FOUND);
        }
        return Result.ok(((HouseDAO) houses.deleteHouseById(houseId).getItem()).toHouse());
    }

    public Result<House> updateHouse(String houseId, House house) {
        if(houseId == null || house == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(!houses.hasHouse(houseId)){
            return Result.error(Response.Status.NOT_FOUND);
        }
        var updateOps = CosmosPatchOperations.create();
        if(house.getName() != null)
            updateOps.replace("/name", house.getName());
        if(house.getName() != null)
            updateOps.replace("/description", house.getDescription());
        if(house.getName() != null)
            updateOps.replace("/isAvailable", house.isAvailable());
        if(house.getName() != null)
            updateOps.replace("/price", house.getPrice());
        if(house.getName() != null)
            updateOps.replace("/promotionPrice", house.getPromotionPrice());

        return Result.ok(houses.updateHouse(houseId, updateOps).getItem().toHouse());
    }


    public Result<List<House>> listHousesByLocation(String location) {
        if(location == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        return Result.ok(houses.getHousesByLocation(location).stream().map(HouseDAO::toHouse).toList());
    }


    public Result<List<House>> listUserHouses(String userId) {
        if(userId == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        return Result.ok(houses.getHousesByOwner(userId).stream().map(HouseDAO::toHouse).toList());
    }


    public Result<String> createRental(String houseId, Rental rental) {
        return null;
    }


    public Result<Rental> updateRental(String houseId, String rentalId, Rental rental) {
        return null;
    }


    public Result<List<Rental>> listRentals(String houseId) {
        return null;
    }


    public Result<List<Rental>> listDiscountedRentals() {
        return null;
    }


    public Result<String> createQuestion(String houseId, Question question) {
        return null;
    }


    public Result<Void> createReply(String houseId, String questionId, Reply reply) {
        return null;
    }


    public Result<List<House>> listHouseQuestions(String houseId) {
        return null;
    }

}
