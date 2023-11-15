package scc.data.dto;

public class HouseList {

    private String id;
    private String name;
    private String location;
    private String photoId;
    private Period period;

    public HouseList(String id, String name, String location, String photoId, Period period) {
        super();
        this.id = id;
        this.name = name;
        this.location = location;
        this.photoId = photoId;
        this.period = period;
    }

    public HouseList(){}

    public String getId() { return id; }

    public String getName() { return name; }

    public String getLocation() { return location; }

    public String getPhotoId() { return photoId; }

    public Period getPeriod() { return period; }

    @Override
    public String toString() {
        return "HouseList [" +
                "name=" + name +
                ", location=" + location +
                ", photoId=" + photoId +
                ", period=" + period.toString() +
                ']';
    }
}
