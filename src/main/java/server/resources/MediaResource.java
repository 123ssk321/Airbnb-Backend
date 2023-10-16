package server.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import storage.DatabaseLayer;
import utils.Hash;
import server.service.RestMedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resource for managing media files, such as images.
 */
public class MediaResource extends Resource implements RestMedia {

	public final static String USER_MEDIA = "user";
	public final static String HOUSE_MEDIA = "house";

	private final DatabaseLayer db;

	public MediaResource(DatabaseLayer db){
		super();
		this.db = db;
	}

	public String uploadUserMedia(byte[] contents) {
		return super.getResult(() -> db.uploadMedia(contents, USER_MEDIA));
	}

	public byte[] downloadUserMedia(String id) {
		return super.getResult(() -> db.downloadMedia(id, USER_MEDIA));
	}

	public String uploadHouseMedia(byte[] contents) {
		return super.getResult(() -> db.uploadMedia(contents, HOUSE_MEDIA));
	}

	public byte[] downloadHouseMedia(String id) {
		return super.getResult(() -> db.downloadMedia(id, HOUSE_MEDIA));
	}

}
