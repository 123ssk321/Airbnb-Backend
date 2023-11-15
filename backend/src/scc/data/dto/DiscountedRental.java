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
