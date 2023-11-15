package scc.data.dto;

public class Rental {
    private String id;
    private String houseId;
    private String tenantId;
    private String landlordId;
    private Period period;

    public Rental() {}

    public Rental(String id, String houseId, String tenantId, String landlordId, Period period) {
        super();
        this.id = id;
        this.houseId = houseId;
        this.tenantId = tenantId;
        this.landlordId = landlordId;
        this.period = period;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getHouseId() {
        return houseId;
    }
    public void setHouseId(String houseId) {
        this.houseId = houseId;
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
    public Period getPeriod() {
        return period;
    }
    public void setPeriod(Period period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "Rental [" +
                "rental=" + id +
                ", house=" + houseId +
                ", tenant=" + tenantId +
                ", landlord=" + landlordId +
                ", period=" + period +
                ']';
    }

}
