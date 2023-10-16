package data.dao;

import data.dto.House;

import java.util.Arrays;

public class HouseDAO {
    private String _rid;
    private String _ts;
    private String id;
    private String name;
    private String ownerId;
    private String location;
    private String description;
    private String[] photoIds;
    private boolean isAvailable;
    private float price; // per day
    private float promotionPrice; // per day

    public HouseDAO() {}

    public HouseDAO(House h) {this(h.getId(), h.getName(), h.getOwnerId(), h.getLocation(), h.getDescription(), h.getPhotoIds(), h.isAvailable(), h.getPrice(), h.getPromotionPrice());}

    public HouseDAO(String id, String name, String ownerId, String location, String description, String[] photoIds, boolean isAvailable, float price, float promotionPrice) {
        super();
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.location = location;
        this.description = description;
        this.photoIds = photoIds;
        this.isAvailable = isAvailable;
        this.price = price;
        this.promotionPrice = promotionPrice;
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
    public String[] getPhotoIds() {
        return photoIds;
    }
    public void setPhotoIds(String[] photoIds) {
        this.photoIds = photoIds;
    }
    public boolean isAvailable() {
        return isAvailable;
    }
    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }
    public float getPromotionPrice() {
        return promotionPrice;
    }
    public void setPromotionPrice(float promotionPrice) {
        this.promotionPrice = promotionPrice;
    }
    public House toHouse() {
        return new House(id, name, ownerId, location, description, photoIds == null? null : Arrays.copyOf(photoIds, photoIds.length),
                isAvailable, price, promotionPrice);
    }
    @Override
    public String toString() {
        return "HouseDAO [_rid=" + _rid +
                ", _ts=" + _ts +
                ", name=" + name +
                ", owner=" + ownerId +
                ", location=" + location +
                ", description='" + description +
                ", photos=" + Arrays.toString(photoIds) +
                ", available=" + isAvailable +
                ", price=" + price +
                ", promotionPrice=" + promotionPrice +
                ']';
    }

}
