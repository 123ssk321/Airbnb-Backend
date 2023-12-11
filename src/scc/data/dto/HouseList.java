package scc.data.dto;

import java.util.List;

public class HouseList {

    private String id;
    private String name;
    private String location;
    private String photoId;
    private List<Period> periods;

    public HouseList(String id, String name, String location, String photoId, List<Period> periods) {
        super();
        this.id = id;
        this.name = name;
        this.location = location;
        this.photoId = photoId;
        this.periods = periods;
    }

    public HouseList(){}

    public String getId() { return id; }

    public String getName() { return name; }

    public String getLocation() { return location; }

    public String getPhotoId() { return photoId; }

    public List<Period> getPeriods() { return periods; }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    @Override
    public String toString() {
        return "HouseList [" +
                "name=" + name +
                ", location=" + location +
                ", photoId=" + photoId +
                ", period=" + periods.toString() +
                ']';
    }
}
