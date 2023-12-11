package scc.data.dto;

public class HouseOwner {

    private String id;
    private String name;
    private String ownerId;
    private String location;
    private String photoId;

    public HouseOwner(String id, String name, String ownerId, String location, String photoId) {
        super();
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.location = location;
        this.photoId = photoId;
    }

    public HouseOwner(){}

    public String getId() { return id; }

    public String getName() { return name; }

    public String getOwnerId() { return ownerId; }

    public String getLocation() { return location; }

    public String getPhotoId() { return photoId; }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    @Override
    public String toString() {
        return "HouseList [" +
                "name=" + name +
                ", ownerId=" + ownerId +
                ", location=" + location +
                ", photoId=" + photoId +
                ']';
    }
}
