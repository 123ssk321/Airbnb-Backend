package data.dto;

import java.util.Arrays;


public class House {
    private String id;
    private String name;
    private String location;
    private String description;
    private String[] photoIds; // list or array?
    private boolean isAvailable;
    private float price; //per day, week or month?
    private float promotionPrice; //per day, week or month?

    public House(String id, String name, String location, String description, String[] photoIds, boolean isAvailable, float price, float promotionPrice) {
        super();
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.photoIds = photoIds;
        this.isAvailable = isAvailable;
        this.price = price;
        this.promotionPrice = promotionPrice;
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

    @Override
    public String toString() {
        return "House [" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", photos=" + Arrays.toString(photoIds) +
                ", available=" + isAvailable +
                ", price=" + price +
                ", promotionPrice=" + promotionPrice +
                ']';
    }

}
