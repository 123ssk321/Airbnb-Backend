package scc.storage.media;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import scc.server.resources.MediaResource;
import scc.storage.MediaStorage;
import scc.utils.Hash;

import java.io.*;

public class PersistentVolumeStorage implements MediaStorage {
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

    @Override
    public String uploadUserPhoto(byte[] contents) {
        try {
            var filename = "user-"+ Hash.of(contents);
            uploadPhoto(contents, PERSISTENT_VOLUME_USERS, filename);
            return filename;
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String uploadHousePhoto(byte[] contents) {
        try {
            var filename = "user-"+ Hash.of(contents);
            uploadPhoto(contents, PERSISTENT_VOLUME_HOUSES, filename);
            return filename;
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public byte[] downloadUserPhoto(String mediaId) {
        try {
            return downloadPhoto(PERSISTENT_VOLUME_USERS+mediaId);
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public byte[] downloadHousePhoto(String mediaId) {
        try {
            return downloadPhoto(PERSISTENT_VOLUME_HOUSES+mediaId);
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private void uploadPhoto(byte[] contents, String path, String filename) throws IOException {
        File persistentFile = new File(path+filename);
        FileOutputStream outputStream = new FileOutputStream(persistentFile);
        outputStream.write(contents);
        outputStream.close();
    }

    public byte[] downloadPhoto(String path) throws IOException {
        File persistentFile = new File(path);
        FileInputStream fileInputStream = new FileInputStream(persistentFile);
        byte[] data = fileInputStream.readAllBytes();
        fileInputStream.close();
        return data;
    }

}
