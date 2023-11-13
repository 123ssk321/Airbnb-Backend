package scc.storage;

import com.azure.cosmos.CosmosClient;
import com.azure.storage.blob.BlobContainerClient;
import scc.data.dao.*;



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

    /*--------------------------------------------------- USERS ------------------------------------------------------*/

    protected UserDAO getUser(String userId){
        return users.getUser(userId);
    }

    /*-------------------------------------------------- HOUSES ------------------------------------------------------*/

    protected HouseDAO getHouse(String houseId){
        return houses.getHouse(houseId);
    }

}
