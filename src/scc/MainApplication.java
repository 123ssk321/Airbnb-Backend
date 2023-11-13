package scc;


import jakarta.ws.rs.core.Application;
import scc.mgt.AzureProperties;
import scc.server.auth.AuthResource;
import scc.server.resources.ControlResource;

import java.util.HashSet;
import java.util.Set;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import scc.server.resources.HousesResource;
import scc.server.resources.MediaResource;
import scc.server.resources.UsersResource;
import scc.storage.CacheDatabaseLayer;
import scc.storage.DatabaseLayer;
import scc.storage.Database;
import scc.utils.GenericExceptionMapper;

public class MainApplication extends Application
{
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> resources = new HashSet<Class<?>>();

	public MainApplication() {
		resources.add(ControlResource.class);
		resources.add(GenericExceptionMapper.class);
		CosmosClient cosmosClient = new CosmosClientBuilder()
				.endpoint(System.getenv(AzureProperties.COSMOSDB_URL))
				.key(System.getenv(AzureProperties.COSMOSDB_KEY))
				.directMode()
				.connectionSharingAcrossClientsEnabled(true)
				.contentResponseOnWriteEnabled(true)
				.buildClient();

		BlobContainerClient userBlobContainer = new BlobContainerClientBuilder()
				.connectionString(System.getenv(AzureProperties.BLOB_KEY))
				.containerName(AzureProperties.USER_BLOB_CONTAINER_NAME)
				.buildClient();

		BlobContainerClient houseBlobContainer = new BlobContainerClientBuilder()
				.connectionString(System.getenv(AzureProperties.BLOB_KEY))
				.containerName(AzureProperties.HOUSE_BLOB_CONTAINER_NAME)
				.buildClient();
		Database dbl;
		if (System.getenv(AzureProperties.USE_CACHE).equals(AzureProperties.USE_CACHE_TRUE)){
			dbl = new CacheDatabaseLayer(cosmosClient,
					System.getenv(AzureProperties.COSMOSDB_DATABASE),
					AzureProperties.USER_COSMOSDB_CONTAINER_NAME,
					AzureProperties.HOUSE_COSMOSDB_CONTAINER_NAME,
					AzureProperties.RENTAL_COSMOSDB_CONTAINER_NAME,
					AzureProperties.QUESTION_COSMOSDB_CONTAINER_NAME,
					userBlobContainer,
					houseBlobContainer);
		} else {
			dbl = new DatabaseLayer(cosmosClient,
					System.getenv(AzureProperties.COSMOSDB_DATABASE),
					AzureProperties.USER_COSMOSDB_CONTAINER_NAME,
					AzureProperties.HOUSE_COSMOSDB_CONTAINER_NAME,
					AzureProperties.RENTAL_COSMOSDB_CONTAINER_NAME,
					AzureProperties.QUESTION_COSMOSDB_CONTAINER_NAME,
					userBlobContainer,
					houseBlobContainer);
		}

		singletons.add(new AuthResource(dbl));
		singletons.add(new UsersResource(dbl));
		singletons.add(new HousesResource(dbl));
		singletons.add( new MediaResource(dbl));
	}

	@Override
	public Set<Class<?>> getClasses() {
		return resources;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
