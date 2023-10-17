package main.java;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import jakarta.ws.rs.core.Application;
import main.java.server.resources.ControlResource;
import main.java.server.resources.HousesResource;
import main.java.server.resources.MediaResource;
import main.java.server.resources.UsersResource;
import main.java.storage.DatabaseLayer;

import java.util.HashSet;
import java.util.Set;

public class MainApplication extends Application
{
	public static final String BLOB_KEY = "BLOB_STORE_CONNECTION";
	public static final String USER_BLOB_CONTAINER_NAME = "USER_BLOB_CONTAINER_NAME";
	public static final String HOUSE_BLOB_CONTAINER_NAME = "HOUSE_BLOB_CONTAINER_NAME";

	public static final String COSMOSDB_KEY = "COSMOSDB_KEY";
	public static final String COSMOSDB_URL = "COSMOSDB_URL";
	public static final String COSMOSDB_DATABASE = "COSMOSDB_DATABASE";
	public static final String USER_COSMOSDB_CONTAINER_NAME = "USER_COSMOSDB_CONTAINER_NAME";
	public static final String HOUSE_COSMOSDB_CONTAINER_NAME = "HOUSE_COSMOSDB_CONTAINER_NAME";
	public static final String RENTAL_COSMOSDB_CONTAINER_NAME = "RENTAL_COSMOSDB_CONTAINER_NAME";
	public static final String QUESTION_COSMOSDB_CONTAINER_NAME = "QUESTION_COSMOSDB_CONTAINER_NAME";

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> resources = new HashSet<Class<?>>();

	public MainApplication() {
		resources.add(ControlResource.class);
		CosmosClient cosmosClient = new CosmosClientBuilder()
				.endpoint(COSMOSDB_URL)
				.key(COSMOSDB_KEY)
				.directMode()
				.connectionSharingAcrossClientsEnabled(true)
				.contentResponseOnWriteEnabled(true)
				.buildClient();

		BlobContainerClient userBlobContainer = new BlobContainerClientBuilder()
				.connectionString(BLOB_KEY)
				.containerName(USER_BLOB_CONTAINER_NAME)
				.buildClient();

		BlobContainerClient houseBlobContainer = new BlobContainerClientBuilder()
				.connectionString(BLOB_KEY)
				.containerName(HOUSE_BLOB_CONTAINER_NAME)
				.buildClient();

		var dbl = new DatabaseLayer(cosmosClient,
				COSMOSDB_DATABASE,
				USER_COSMOSDB_CONTAINER_NAME,
				HOUSE_COSMOSDB_CONTAINER_NAME,
				RENTAL_COSMOSDB_CONTAINER_NAME,
				QUESTION_COSMOSDB_CONTAINER_NAME,
				userBlobContainer,
				houseBlobContainer);
		singletons.add(new UsersResource(dbl));
		//singletons.add(new HousesResource(dbl));
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
