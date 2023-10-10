package server.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import scc.utils.Hash;
import server.service.RestMedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resource for managing media files, such as images.
 */
public class MediaResource implements RestMedia {
	Map<String,byte[]> map = new HashMap<String,byte[]>();

	public String upload(byte[] contents) {
		String key = Hash.of(contents);
		map.put( key, contents); // Lab 1
		// UploadToStorage.upload("house.jpg", contents); // Lab 2
		return key;
	}

	public byte[] download(@PathParam("id") String id) {
		// Lab 1
		var img = map.get(id);
		if (id == null)
			throw new ServiceUnavailableException();
		else
			return img;

		/*//Lab 2
		return DownloadFromStorage.download("house.jpg");*/
	}

	/**
	 * Lists the ids of images stored.
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> list() {
		return new ArrayList<String>( map.keySet());
	}
}
