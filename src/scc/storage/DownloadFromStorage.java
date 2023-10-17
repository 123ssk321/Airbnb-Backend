package scc.storage;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

// Java class from lab2
public class DownloadFromStorage {

	public static byte[] download(String filename) {

		// Get connection string in the storage access keys page
		String storageConnectionString = "";

		try {
			// Get container client
			BlobContainerClient containerClient = new BlobContainerClientBuilder()
														.connectionString(storageConnectionString)
														.containerName("images")
														.buildClient();

			// Get client to blob
			BlobClient blob = containerClient.getBlobClient( filename);

			// Download contents to BinaryData (check documentation for other alternatives)
			BinaryData data = blob.downloadContent();
			
			byte[] arr = data.toBytes();
			
			System.out.println( "Blob size : " + arr.length);
			return arr;
		} catch( Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
