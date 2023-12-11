package scc.data.dao;

import scc.data.dto.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HouseDAO {
    private String _rid;
    private String _ts;
    private String id;
    private String name;
    private String ownerId;
    private String location;
    private String description;
    private List<String> photoIds;
    private List<Period> periods;
    private long views;


    public HouseDAO() {}

    public HouseDAO(House h) {this(h.getId(), h.getName(), h.getOwnerId(), h.getLocation(), h.getDescription(), new ArrayList<>(List.of(h.getPhotoIds())), new ArrayList<>(List.of(h.getPeriods())), 0L);}

    public HouseDAO(String id, String name, String ownerId, String location, String description, List<String> photoIds, List<Period> periods, long views) {
        super();
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.location = location;
        this.description = description;
        this.photoIds = photoIds;
        this.periods = periods;
        this.views = views;
    }

    public String get_rid() {
        return _rid;
    }

    public void set_rid(String _rid) {
        this._rid = _rid;
    }

    public String get_ts() {
        return _ts;
    }

    public void set_ts(String _ts) {
        this._ts = _ts;
    }
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
    public List<String> getPhotoIds() {
        return photoIds;
    }
    public void setPhotoIds(List<String> photoIds) {
        this.photoIds = photoIds;
    }
    public List<Period> getPeriods() {
        return periods;
    }
    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public House toHouse() {
        return new House(id, name, ownerId, location, description, photoIds == null? null : photoIds.toArray(new String[0]),
                periods == null? null : periods.toArray(new Period[0]), views);
    }

    public HouseList toHouseList(){
        return new HouseList(id, name, location, photoIds.get(0), periods.get(0));
    }

    public DiscountedRental toDiscountedRental(){
        return new DiscountedRental(id, name, ownerId, location, photoIds.get(0), periods.get(0));
    }

    public HouseOwner toHouseOwner(){
        return new HouseOwner(id, name, ownerId, location, photoIds.get(0));
    }


    @Override
    public String toString() {
        return "HouseDAO [_rid=" + _rid +
                ", _ts=" + _ts +
                ", _id=" + id +
                ", name=" + name +
                ", owner=" + ownerId +
                ", location=" + location +
                ", description='" + description +
                ", photos=" + photoIds +
                ", periods=" + periods +
                ", views=" + views +
                ']';
    }

}
