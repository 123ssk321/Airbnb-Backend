package scc.storage;

import scc.server.resources.MediaResource;

public interface MediaStorage {

    boolean exists(String id, MediaResource.BlobType type);

    String uploadUserPhoto(byte[] contents);

    String uploadHousePhoto(byte[] contents);

    byte[] downloadUserPhoto(String mediaId);

    byte[] downloadHousePhoto(String mediaId);

}
