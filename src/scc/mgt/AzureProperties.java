package scc.mgt;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AzureProperties
{
	public static final String BLOB_KEY = "BlobStoreConnection";
	public static final String COSMOSDB_KEY = "COSMOSDB_KEY";
	public static final String COSMOSDB_URL = "COSMOSDB_URL";
	public static final String COSMOSDB_DATABASE = "COSMOSDB_DATABASE";
	public static final String USER_COSMOSDB_CONTAINER_NAME = "users";
	public static final String HOUSE_COSMOSDB_CONTAINER_NAME = "houses";
	public static final String RENTAL_COSMOSDB_CONTAINER_NAME = "rentals";
	public static final String QUESTION_COSMOSDB_CONTAINER_NAME = "questions";
	public static final String USER_BLOB_CONTAINER_NAME = "users";
	public static final String HOUSE_BLOB_CONTAINER_NAME = "houses";

	public static final String PROPS_FILE = "azurekeys-westeurope.props";
	private static Properties props;
	
	public static synchronized Properties getProperties() {
		if( props == null) {
			props = new Properties();
			try {
				props.load( new FileInputStream(PROPS_FILE));

			} catch (IOException e) {
				// do nothing
			}
		}
		return props;
	}

}
