package scc.storage.cosmosdb;

import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosPatchOperations;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import scc.data.dao.HouseDAO;
import scc.data.dao.RentalDAO;
import scc.data.dao.UserDAO;
import scc.data.dto.*;
import scc.mgt.AzureProperties;
import scc.mgt.CognitiveSearch;
import scc.server.auth.LoginDetails;
import scc.storage.AbstractDatabase;
import scc.storage.Database;
import scc.storage.MediaStorage;
import scc.storage.cosmosdb.container.HousesContainer;
import scc.storage.cosmosdb.container.QuestionsContainer;
import scc.storage.cosmosdb.container.RentalsContainer;
import scc.storage.cosmosdb.container.UsersContainer;
import scc.utils.Result;

import java.util.List;

public abstract class CosmosDBLayer extends AbstractDatabase implements Database {

    protected final CognitiveSearch cognitiveSearch;

    protected CosmosDBLayer(CosmosDatabase db,
                            String userCosmosDBContainerName,
                            String houseCosmosDBContainerName,
                            String rentalCosmosDBContainerName,
                            String questionCosmosDBContainerName,
                            MediaStorage mediaStorage){
        super(new UsersContainer(db.getContainer(userCosmosDBContainerName)),
                new HousesContainer(db.getContainer(houseCosmosDBContainerName)),
                new RentalsContainer(db.getContainer(rentalCosmosDBContainerName)),
                new QuestionsContainer(db.getContainer(questionCosmosDBContainerName)),
                mediaStorage);

        if(System.getenv(AzureProperties.USE_COG_SEARCH).equals(AzureProperties.USE_COG_SEARCH_TRUE))
            cognitiveSearch = new CognitiveSearch();
        else
            cognitiveSearch = null;
    }

    /*---------------------------------------------------- AUTH ------------------------------------------------------*/

    public abstract Result<Response> auth(LoginDetails loginDetails);

    protected abstract Result<String> checkCookie(Cookie session, String id);


    /*---------------------------------------------------- USERS -----------------------------------------------------*/

    protected abstract UserDAO getUser(String userId);

    @Override
    public Result<User> deleteUser(Cookie session, String userId) {
        var res = super.deleteUser(session, userId);
        if (res.isOK()){
            var user = res.value();
            for(String houseId : user.getHouseIds()){
                ((HousesContainer) houses).updateHouse(houseId, CosmosPatchOperations.create().replace("/ownerId", "Deleted User"));
            }

            for(String rentalId : user.getRentalIds()){
                ((RentalsContainer) rentals).updateRental(rentalId, CosmosPatchOperations.create().replace("/tenantId", "Deleted User"));
            }
        }
        return res;
    }

    @Override
    public Result<User> updateUser(Cookie session, String userId, User user) {
        var res = super.updateUser(session, userId, user);
        if (res.isOK()){
            var updateOps = getCosmosPatchOperations(res.value());
            var userDAO  = ((UsersContainer) users).updateUser(userId, updateOps);
            return Result.ok(userDAO.toUser());
        }

        return res;
    }

    private static CosmosPatchOperations getCosmosPatchOperations(User user) {
        var updateOps = CosmosPatchOperations.create();
        var nameToUpdate = user.getName();
        var pwdToUpdate = user.getPwd();
        var photoIdToUpdate = user.getPhotoId();
        var houseIdsToUpdate = user.getHouseIds();

        if(nameToUpdate != null)
            updateOps.replace("/name", nameToUpdate);
        if(pwdToUpdate != null)
            updateOps.replace("/pwd", pwdToUpdate);
        if(photoIdToUpdate != null)
            updateOps.replace("/photoId", photoIdToUpdate);
        if(houseIdsToUpdate != null && houseIdsToUpdate.length > 1) {
            updateOps.set("/houseIds", houseIdsToUpdate);
        }
        return updateOps;
    }


    /*--------------------------------------------------- HOUSES -----------------------------------------------------*/

    @Override
    public Result<House> createHouse(Cookie session, House house) {
        var res = super.createHouse(session, house);
        if (res.isOK()){
            var h = res.value();
            ((UsersContainer) users).updateUser(h.getOwnerId(), CosmosPatchOperations.create().add("/houseIds/-", h.getId()));
        }
        return res;
    }

    @Override
    public Result<House> getHouse(String houseId){
        var res = super.getHouse(houseId);
        if(res.isOK()){
            var updateViews = new Thread(() -> ((HousesContainer) houses).updateHouse(houseId, CosmosPatchOperations.create().increment("/views", 1)));
            updateViews.start();
        }
        return res;
    }

    protected abstract HouseDAO getHouseDAO(String houseId);

    @Override
    public Result<House> deleteHouse(Cookie session, String houseId) {
        var res = super.deleteHouse(session, houseId);
        if(res.isOK()){
            var house = res.value();
            var owner = this.getUser(house.getOwnerId());
            var ownerHouseIdx = owner.getHouseIds().indexOf(houseId);
            ((UsersContainer) users).updateUser(owner.getId(), CosmosPatchOperations.create().remove("/houseIds/"+ownerHouseIdx));

            for(RentalDAO rental : rentals.getRentalsByHouse(houseId, 0, house.getPeriods().length)){
                ((RentalsContainer) rentals).updateRental(rental.getId(), CosmosPatchOperations.create().replace("/houseId", "Deleted House"));
            }
        }
        return res;
    }

    @Override
    public Result<House> updateHouse(Cookie session, String houseId, House houseToUpdate) {
        var res = super.updateHouse(session, houseId, houseToUpdate);
        if(res.isOK()){
            var updateOps = CosmosPatchOperations.create();
            var nameToUpdate = houseToUpdate.getName();
            var descriptionToUpdate = houseToUpdate.getDescription();
            var periodsToUpdate = houseToUpdate.getPeriods();

            if(nameToUpdate != null)
                updateOps.replace("/name", nameToUpdate);
            if(descriptionToUpdate != null)
                updateOps.replace("/description", descriptionToUpdate);
            if(periodsToUpdate != null) {
                updateOps.set("/periods", periodsToUpdate);
            }
            var houseDAO = ((HousesContainer) houses).updateHouse(houseId, updateOps);
            return Result.ok(houseDAO.toHouse());
        }
        return res;

    }

    @Override
    public Result<List<HouseSearch>> searchByNameAndDescription(String queryText, String ownerId, Boolean useName,
                                                                Boolean useDescription, int start, int length) {

        if(cognitiveSearch == null)
            return  Result.ok();

        return Result.ok(cognitiveSearch.searchHouses(queryText, ownerId, useName, useDescription, start, length));
    }

    /*-------------------------------------------------- RENTALS -----------------------------------------------------*/

    @Override
    public Result<String> createRental(Cookie session, String houseId, Rental rental) {
        var res = super.createRental(session, houseId, rental);
        if(res.isOK()){
            var house = this.getHouseDAO(houseId);
            var housePeriods = house.getPeriods();
            var rentalPeriod = rental.getPeriod();
            rentalPeriod.setAvailable(true);
            var periodIdx = housePeriods.indexOf(rentalPeriod);
            ((HousesContainer) houses).updateHouse(houseId, CosmosPatchOperations.create().replace("/periods/"+periodIdx+"/available", false));
            ((UsersContainer) users).updateUser(rental.getTenantId(), CosmosPatchOperations.create().set("/rentalIds/-", res.value()));
        }
        return res;
    }

    @Override
    public Result<Rental> updateRental(Cookie session, String houseId, String rentalId, Rental rentalToUpdate) {
//        if(houseId == null || rentalId == null || rentalToUpdate == null )
//            return Result.error(Response.Status.BAD_REQUEST);
//        if (!this.hasHouse(houseId))
//            return Result.error(Response.Status.NOT_FOUND);
//
//        var rental = rentals.getRental(rentalId);
//        if (rental == null)
//            return Result.error(Response.Status.NOT_FOUND);
//
//        var authRes = checkCookie(session, rental.getLandlordId());
//        if(!authRes.isOK())
//            return Result.error(authRes.error());
//
//        var updateOps = CosmosPatchOperations.create();
//        var tenantIdToUpdate = rental.getTenantId();
//        var periodToUpdate = rental.getPeriod();
//
//        if(tenantIdToUpdate != null){
//            var tenant = this.getUser(tenantIdToUpdate);
//            if(tenant == null)
//                return Result.error(Response.Status.NOT_FOUND);
//            updateOps.replace("/tenantId", tenantIdToUpdate);
//
//            var tenantRentalIds = new ArrayList<>(Arrays.asList(tenant.getRentalIds()));
//            tenantRentalIds.add(rental.getId());
//            users.updateUser(tenant.getId(), CosmosPatchOperations.create().set("/rentalIds", tenantRentalIds));
//        }
//        if(periodToUpdate != null)
//            updateOps.replace("/period", periodToUpdate);
//
//        return Result.ok(rentals.updateRental(rentalId, updateOps).getItem().toRental());
        return Result.error(Response.Status.NOT_IMPLEMENTED);
    }

    /*------------------------------------------------ QUESTIONS -----------------------------------------------------*/

    @Override
    public Result<String> createReply(Cookie session, String houseId, String questionId, Reply reply) {
        var res = super.createReply(session, houseId, questionId, reply);
        if(res.isOK()){
            var updateOps = CosmosPatchOperations.create();
            updateOps.replace("/reply", reply);
            ((QuestionsContainer) questions).addReply(questionId, updateOps);
        }
        return res;

    }

}
