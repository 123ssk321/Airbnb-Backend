package scc.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;


public class House {
    private String id;
    private String name;
    private String ownerId;
    private String location;
    private String description;
    private String[] photoIds;
    private Period[] periods;

    public House(String id, String name, String ownerId, String location, String description, String[] photoIds, Period[] periods) {
        super();
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.location = location;
        this.description = description;
        this.photoIds = photoIds;
        this.periods = periods;
    }

    public House(){}

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String[] getPhotoIds() {
        return photoIds;
    }
    public void setPhotoIds(String[] photoIds) {
        this.photoIds = photoIds;
    }
    public Period[] getPeriods() {
        return periods;
    }
    public void setPeriods(Period[] periods) {
        this.periods = periods;
    }

    @Override
    public String toString() {
        return "House [" +
                "name=" + name +
                ", ownerId=" + ownerId +
                ", location=" + location +
                ", description=" + description +
                ", photos=" + Arrays.toString(photoIds) +
                ", periods=" + Arrays.toString(periods) +
                ']';
    }

}
