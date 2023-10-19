package scc.storage;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.models.CosmosPatchOperations;
import com.azure.resourcemanager.monitor.models.SyslogDataSource;
import com.azure.storage.blob.BlobContainerClient;
import jakarta.ws.rs.core.Response;
import org.glassfish.jaxb.runtime.v2.runtime.output.SAXOutput;
import scc.server.resources.MediaResource;
import scc.utils.Result;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import scc.data.dto.*;
import scc.data.dao.*;
import scc.storage.cosmosdb.*;
import scc.server.resources.MediaResource.BlobType;

public class DatabaseLayer {

    private final UsersCDB users;
    private final MediaBlobStorage media;
    private final HousesCDB houses;
    private final RentalsCDB rentals;
    private final QuestionsCDB questions;
    private static final Logger Log = Logger.getLogger(DatabaseLayer.class.getName());

    public DatabaseLayer(CosmosClient cClient,
                         String cosmosdbDatabase,
                         String userCosmosDBContainerName,
                         String houseCosmosDBContainerName,
                         String rentalCosmosDBContainerName,
                         String questionCosmosDBContainerName,
                         BlobContainerClient userBlobContainer,
                         BlobContainerClient houseBlobContainer){
        var db = cClient.getDatabase(cosmosdbDatabase);

        users = new UsersCDB(db.getContainer(userCosmosDBContainerName));
        houses = new HousesCDB(db.getContainer(houseCosmosDBContainerName));
        rentals = new RentalsCDB(db.getContainer(rentalCosmosDBContainerName));
        questions = new QuestionsCDB(db.getContainer(questionCosmosDBContainerName));
        media = new MediaBlobStorage(userBlobContainer, houseBlobContainer);
    }

    public Result<String> createUser(User user) {
        if(user == null || user.getId() == null || user.getName() == null || user.getPwd() == null
                || user.getPhotoId() == null || !media.exists(user.getPhotoId(), BlobType.USER)
                || user.getHouseIds() == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        if(users.hasUser(user.getId()))
            return Result.error(Response.Status.CONFLICT);

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


    public Result<String> uploadMedia(byte[] contents, BlobType type) {
        if (contents == null)
            return Result.error(Response.Status.BAD_REQUEST);

        return switch (type) {
            case USER -> Result.ok(media.uploadUserPhoto(contents));
            case HOUSE -> Result.ok(media.uploadHousePhoto(contents));
        };
    }


    public Result<byte[]> downloadMedia(String id, BlobType type) {
        if (id == null)
            return Result.error(Response.Status.BAD_REQUEST);
        if (!media.exists(id, type))
            return Result.error(Response.Status.NOT_FOUND);

        var mediaValue = switch (type) {
            case USER -> media.downloadUserPhoto(id);
            case HOUSE -> media.downloadHousePhoto(id);
        };
        return Result.ok(mediaValue);
    }


    public Result<String> createHouse(House house) {
        if(house == null || house.getName() == null || house.getLocation() == null || house.getDescription() == null
                || house.getPhotoIds() == null || house.getPhotoIds().length < 1
                || !house.isAvailable() || house.getPrice() <= 0 || house.getPromotionPrice() <= 0) {
            return Result.error(Response.Status.BAD_REQUEST);
        }
        for (String photoId : house.getPhotoIds()){
            if (!media.exists(photoId, BlobType.HOUSE))
                return Result.error(Response.Status.BAD_REQUEST);
        }
        if (!users.hasUser(house.getOwnerId()))
            return Result.error(Response.Status.BAD_REQUEST);

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
        var nameToUpdate = house.getName();
        var descriptionToUpdate = house.getDescription();
        var priceToUpdate = house.getPrice();
        var promotionPriceToUpdate = house.getPromotionPrice();

        if(nameToUpdate != null)
            updateOps.replace("/name", nameToUpdate);
        if(descriptionToUpdate != null)
            updateOps.replace("/description", descriptionToUpdate);
        if (priceToUpdate > 0 && priceToUpdate > promotionPriceToUpdate)
            updateOps.replace("/price", priceToUpdate);
        if (promotionPriceToUpdate > 0 && promotionPriceToUpdate < priceToUpdate)
            updateOps.replace("/promotionPrice", house.getPromotionPrice());
        updateOps.replace("/isAvailable", house.isAvailable());

        return Result.ok(houses.updateHouse(houseId, updateOps).getItem().toHouse());
    }


    public Result<List<House>> listHousesByLocation(String location) {
        if(location == null){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        return Result.ok(houses.getHousesByLocation(location).stream().map(HouseDAO::toHouse).toList());
    }


    public Result<List<House>> listUserHouses(String ownerId) {
        if(ownerId == null || !users.hasUser(ownerId)){
            return Result.error(Response.Status.BAD_REQUEST);
        }
        return Result.ok(houses.getHousesByOwner(ownerId).stream().map(HouseDAO::toHouse).toList());
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
