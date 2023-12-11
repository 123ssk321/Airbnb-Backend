package scc.data.dto;

public class HouseSearch {

    private String id;
    private String name;
    private String ownerId;
    private String description;
    private String photoIds;

    public HouseSearch(String id, String name, String ownerId, String description, String photoIds) {
        super();
        this.id = id;
        this.name = name;
        this.photoIds = photoIds;
        this.description = description;
        this.ownerId = ownerId;
    }

    public HouseSearch(){}

    public String getId() { return id; }

    public String getName() { return name; }

    public String getOwnerId() { return ownerId; }

    public String getDescription() { return description; }

    public String getPhotoIds() { return photoIds; }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotoIds(String photoIds) {
        this.photoIds = photoIds;
    }

    @Override
    public String toString() {
        return "HouseList [" +
                "name=" + name +
                ", ownerId=" + ownerId +
                ", description=" + description +
                ", photoIds=" + photoIds +
                ']';
    }

}
