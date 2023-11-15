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
