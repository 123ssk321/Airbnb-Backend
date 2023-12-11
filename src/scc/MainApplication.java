package scc;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.ws.rs.core.Application;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.codecs.BsonArrayCodec;
import scc.mgt.AzureProperties;
import scc.server.auth.AuthResource;
import scc.server.resources.ControlResource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import scc.server.resources.HousesResource;
import scc.server.resources.MediaResource;
import scc.server.resources.UsersResource;
import scc.storage.MediaStorage;
import scc.storage.cosmosdb.CacheDatabaseLayer;
import scc.storage.cosmosdb.DatabaseLayer;
import scc.storage.Database;
import scc.storage.media.BlobStorage;
import scc.storage.media.PersistentVolumeStorage;
import scc.storage.mongodb.MongoDBLayer;
import scc.utils.GenericExceptionMapper;

import static org.bson.codecs.configuration.CodecRegistries.*;
import static org.bson.codecs.pojo.Conventions.ANNOTATION_CONVENTION;

public class MainApplication extends Application
{
	private static final String DATABASE_TYPE = "DATABASE_TYPE";
	private static final String COSMOSDB = "COSMOSDB";
	private static final String MONGODB = "MONGODB";

	private static final String MONGODB_DATABASE = "MONGODB_DATABASE";
	private static final String MONGODB_CONNECTION_STRING = "MONGODB_CONNECTION_STRING";

	private static final String BLOB_STORAGE_TYPE = "BLOB_STORAGE_TYPE";
	private static final String AZURE_BLOB_STORAGE = "AZURE_BLOB_STORAGE";
	private static final String PERSISTENT_VOLUME_CLAIM = "PERSISTENT_VOLUME_CLAIM";

	private static final String PVC_MOUNT_PATH = "PVC_MOUNT_PATH";


	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> resources = new HashSet<Class<?>>();

	public MainApplication() {
		resources.add(ControlResource.class);
		resources.add(GenericExceptionMapper.class);

		MediaStorage mediaStorage;
		if(System.getenv(BLOB_STORAGE_TYPE).equals(AZURE_BLOB_STORAGE)){
			BlobContainerClient userBlobContainer = new BlobContainerClientBuilder()
					.connectionString(System.getenv(AzureProperties.BLOB_KEY))
					.containerName(AzureProperties.USER_BLOB_CONTAINER_NAME)
					.buildClient();

			BlobContainerClient houseBlobContainer = new BlobContainerClientBuilder()
					.connectionString(System.getenv(AzureProperties.BLOB_KEY))
					.containerName(AzureProperties.HOUSE_BLOB_CONTAINER_NAME)
					.buildClient();
			mediaStorage = new BlobStorage(userBlobContainer, houseBlobContainer);
		} else {
			mediaStorage = new PersistentVolumeStorage(System.getenv(PVC_MOUNT_PATH));
		}

		Database dbl;
		if (System.getenv(DATABASE_TYPE).equals(COSMOSDB)){
			CosmosClient cosmosClient = new CosmosClientBuilder()
					.endpoint(System.getenv(AzureProperties.COSMOSDB_URL))
					.key(System.getenv(AzureProperties.COSMOSDB_KEY))
					.directMode()
					.connectionSharingAcrossClientsEnabled(true)
					.contentResponseOnWriteEnabled(true)
					.buildClient();

			if (System.getenv(AzureProperties.USE_CACHE).equals(AzureProperties.USE_CACHE_TRUE)){
				dbl = new CacheDatabaseLayer(cosmosClient,
						System.getenv(AzureProperties.COSMOSDB_DATABASE),
						AzureProperties.USER_COSMOSDB_CONTAINER_NAME,
						AzureProperties.HOUSE_COSMOSDB_CONTAINER_NAME,
						AzureProperties.RENTAL_COSMOSDB_CONTAINER_NAME,
						AzureProperties.QUESTION_COSMOSDB_CONTAINER_NAME,
						mediaStorage);
			} else {
				dbl = new DatabaseLayer(cosmosClient,
						System.getenv(AzureProperties.COSMOSDB_DATABASE),
						AzureProperties.USER_COSMOSDB_CONTAINER_NAME,
						AzureProperties.HOUSE_COSMOSDB_CONTAINER_NAME,
						AzureProperties.RENTAL_COSMOSDB_CONTAINER_NAME,
						AzureProperties.QUESTION_COSMOSDB_CONTAINER_NAME,
						mediaStorage);
			}
		} else {
			ConnectionString connectionString = new ConnectionString(System.getenv(MONGODB_CONNECTION_STRING));

			CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
			CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
			MongoClientSettings clientSettings = MongoClientSettings.builder()
					.applyConnectionString(connectionString)
					.codecRegistry(codecRegistry)
					.build();
			MongoClient mongoClient = MongoClients.create(clientSettings);
			dbl = new MongoDBLayer(mongoClient.getDatabase(System.getenv(MONGODB_DATABASE)), mediaStorage);
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
