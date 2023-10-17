package main.java.storage;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

// Java class from lab2
public class UploadToStorage {

	public static void upload(String filename, byte[] contents) {

		// Get connection string in the storage access keys page
		String storageConnectionString = "";

		try {
			BinaryData data = BinaryData.fromBytes(contents);

			// Get container client
			BlobContainerClient containerClient = new BlobContainerClientBuilder()
														.connectionString(storageConnectionString)
														.containerName("images")
														.buildClient();

			// Get client to blob
			BlobClient blob = containerClient.getBlobClient( filename);

			// Upload contents from BinaryData (check documentation for other alternatives)
			blob.upload(data);
			
			System.out.println( "File updloaded : " + filename);
			
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
}
