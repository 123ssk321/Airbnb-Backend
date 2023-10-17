package scc.server.resources;

import scc.storage.DatabaseLayer;
import scc.server.service.RestMedia;


/**
 * Resource for managing media files, such as images.
 */
public class MediaResource extends Resource implements RestMedia {

	public static final String USER_MEDIA = "user";
	public static final String HOUSE_MEDIA = "house";

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
