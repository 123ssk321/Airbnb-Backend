package scc.server.resources;

import scc.storage.Database;
import scc.server.service.RestMedia;

/**
 * Resource for managing media files, such as images.
 */
public class MediaResource extends Resource implements RestMedia {

	public enum BlobType {USER, HOUSE}

	private final Database db;

	public MediaResource(Database db){
		super();
		this.db = db;
	}

	public String uploadUserMedia(byte[] contents) {
		return super.getResult(() -> db.uploadMedia(contents, BlobType.USER));
	}

	public byte[] downloadUserMedia(String id) {
		return super.getResult(() -> db.downloadMedia(id, BlobType.USER));
	}

	public String uploadHouseMedia(byte[] contents) {
		return super.getResult(() -> db.uploadMedia(contents, BlobType.HOUSE));
	}

	public byte[] downloadHouseMedia(String id) {
		return super.getResult(() -> db.downloadMedia(id, BlobType.HOUSE));
	}

}
