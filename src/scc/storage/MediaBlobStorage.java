package scc.storage;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import scc.server.resources.MediaResource.BlobType;
import scc.utils.Hash;
import scc.utils.Result;

public class MediaBlobStorage {
    private final BlobContainerClient userBlobContainerClient;
    private final BlobContainerClient houseBlobContainerClient;

    public MediaBlobStorage(BlobContainerClient userBlobContainerClient, BlobContainerClient houseBlobContainerClient) {
        this.userBlobContainerClient = userBlobContainerClient;
        this.houseBlobContainerClient = houseBlobContainerClient;
    }

    public boolean exists(String id, BlobType type){
        return switch (type) {
            case USER -> userBlobContainerClient.getBlobClient(id).exists();
            case HOUSE -> houseBlobContainerClient.getBlobClient(id).exists();
        };
    }

    public String uploadUserPhoto(byte[] contents){
        String id = "user-"+ Hash.of(contents);
        var blob = userBlobContainerClient.getBlobClient(id);
        blob.upload(BinaryData.fromBytes(contents));
        return id;
    }

    public String uploadHousePhoto(byte[] contents){
        String id = "house-"+ Hash.of(contents);
        var blob = houseBlobContainerClient.getBlobClient(id);
        blob.upload(BinaryData.fromBytes(contents));
        return id;
    }

    public byte[] downloadUserPhoto(String mediaId){
        var blob = userBlobContainerClient.getBlobClient(mediaId);
        return blob.downloadContent().toBytes();
    }

    public byte[] downloadHousePhoto(String mediaId){
        var blob = houseBlobContainerClient.getBlobClient(mediaId);
        return blob.downloadContent().toBytes();
    }

}
