package data.dto;

public class Rental {
    private String id;
    private String tenantId;
    private String landlordId;
    private int period;
    private float price;

    public Rental() {}

    public Rental(String id, String tenantId, String landlordId, int period, float price) {
        super();
        this.id = id;
        this.tenantId = tenantId;
        this.landlordId = landlordId;
        this.period = period;
        this.price = price;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public String getLandlordId() {
        return landlordId;
    }
    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }
    public int getPeriod() {
        return period;
    }
    public void setPeriod(int period) {
        this.period = period;
    }
    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Rental [" +
                "rental=" + id +
                ", tenant=" + tenantId +
                ", landlord=" + landlordId +
                ", period=" + period +
                ", price=" + price +
                ']';
    }

}
