package scc.storage;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import scc.server.resources.MediaResource;
import scc.utils.Hash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class PersistentVolumeStorage {
    private static final String PERSISTENT_VOLUME_HOUSES = "/mnt/vol/houses";
    private static final String PERSISTENT_VOLUME_USERS = "/mnt/vol/users";

    public PersistentVolumeStorage() {
    }

    public boolean exists(String id, MediaResource.BlobType type) {
        return switch (type) {
            case USER -> new File(PERSISTENT_VOLUME_USERS+id).isFile();
            case HOUSE -> new File(PERSISTENT_VOLUME_HOUSES+id).isFile();
        };
    }

    public void uploadPhoto(byte[] contents, MediaResource.BlobType type) {
        try {
            File persistentFile = null;
            switch (type) {
                case USER -> persistentFile = new File(PERSISTENT_VOLUME_USERS + "user-"+ Hash.of(contents));
                case HOUSE -> persistentFile = new File(PERSISTENT_VOLUME_HOUSES + "house-"+ Hash.of(contents));
            }

            FileOutputStream outputStream = new FileOutputStream(persistentFile);
            outputStream.write(contents);
            outputStream.close();
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public byte[] downloadPhoto(String mediaId, MediaResource.BlobType type) {
        try {
            File persistentFile = null;
            switch (type) {
                case USER -> new File(PERSISTENT_VOLUME_USERS+mediaId);
                case HOUSE -> new File(PERSISTENT_VOLUME_HOUSES+mediaId);
            }

            FileInputStream fileInputStream = new FileInputStream(persistentFile);
            byte[] data = fileInputStream.readAllBytes();
            fileInputStream.close();
            return data;
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }



}
