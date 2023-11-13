package scc.storage;

import com.azure.cosmos.CosmosClient;
import com.azure.storage.blob.BlobContainerClient;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import scc.data.dao.*;
import scc.server.auth.LoginDetails;
import scc.utils.Result;


public class DatabaseLayer extends AbstractDatabase implements Database{

    public DatabaseLayer(CosmosClient cClient,
                         String cosmosdbDatabase,
                         String userCosmosDBContainerName,
                         String houseCosmosDBContainerName,
                         String rentalCosmosDBContainerName,
                         String questionCosmosDBContainerName,
                         BlobContainerClient userBlobContainer,
                         BlobContainerClient houseBlobContainer){
        super(cClient, cosmosdbDatabase, userCosmosDBContainerName, houseCosmosDBContainerName, rentalCosmosDBContainerName, questionCosmosDBContainerName, userBlobContainer, houseBlobContainer);

    }

    /*---------------------------------------------------- AUTH ------------------------------------------------------*/

    public Result<Response> auth(LoginDetails loginDetails){
        return Result.ok(Response.ok().build());
    }

    public Result<String> checkCookie(Cookie session, String id){return Result.ok("OK");}

    /*--------------------------------------------------- USERS ------------------------------------------------------*/

    protected UserDAO getUser(String userId){
        return users.getUser(userId);
    }

    /*-------------------------------------------------- HOUSES ------------------------------------------------------*/

    protected HouseDAO getHouseDAO(String houseId){
        return houses.getHouse(houseId);
    }

}
