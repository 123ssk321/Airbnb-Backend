package scc.storage.media;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import scc.server.resources.MediaResource;
import scc.storage.MediaStorage;
import scc.utils.Hash;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class PersistentVolumeStorage implements MediaStorage {
    private static final String PERSISTENT_VOLUME_HOUSES = "houses/";
    private static final String PERSISTENT_VOLUME_USERS = "users/";

    private static Logger logger = Logger.getLogger(PersistentVolumeStorage.class.getName());

    private final String mountPath;
    
    public PersistentVolumeStorage(String mountPath) {
        this.mountPath = mountPath;
        try {
            Files.createDirectories(Path.of(mountPath+PERSISTENT_VOLUME_USERS));
            Files.createDirectories(Path.of(mountPath+PERSISTENT_VOLUME_HOUSES));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(String id, MediaResource.BlobType type) {
        return switch (type) {
            case USER -> new File(mountPath+PERSISTENT_VOLUME_USERS+id).isFile();
            case HOUSE -> new File(mountPath+PERSISTENT_VOLUME_HOUSES+id).isFile();
        };
    }

    @Override
    public String uploadUserPhoto(byte[] contents) {
        try {
            var filename = "user-"+ Hash.of(contents);
            uploadPhoto(contents, mountPath+PERSISTENT_VOLUME_USERS, filename);
            return filename;
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String uploadHousePhoto(byte[] contents) {
        try {
            var filename = "house-"+ Hash.of(contents);
            uploadPhoto(contents, mountPath+PERSISTENT_VOLUME_HOUSES, filename);
            return filename;
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public byte[] downloadUserPhoto(String mediaId) {
        try {
            return downloadPhoto(mountPath+PERSISTENT_VOLUME_USERS+mediaId);
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public byte[] downloadHousePhoto(String mediaId) {
        try {
            return downloadPhoto(mountPath+PERSISTENT_VOLUME_HOUSES+mediaId);
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private void uploadPhoto(byte[] contents, String path, String filename) throws IOException {
        File persistentFile = new File(path+filename);
        persistentFile.createNewFile();
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
