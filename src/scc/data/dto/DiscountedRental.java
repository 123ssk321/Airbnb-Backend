package scc.data.dto;

public class DiscountedRental {

    private String id;
    private String name;
    private String ownerId;
    private String location;
    private String photoId;
    private Period period;

    public DiscountedRental(String id, String name, String ownerId, String location, String photoId, Period period) {
        super();
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.location = location;
        this.photoId = photoId;
        this.period = period;
    }

    public DiscountedRental(){}

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public String getLocation() {
        return location;
    }
    public String getPhotoId() {
        return photoId;
    }
    public Period getPeriod() {
        return period;
    }

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

    public void setPeriod(Period period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "DiscountedRental [" +
                "name=" + name +
                ", ownerId=" + ownerId +
                ", location=" + location +
                ", photo=" + photoId +
                ", period=" + period.toString() +
                ']';
    }


}
