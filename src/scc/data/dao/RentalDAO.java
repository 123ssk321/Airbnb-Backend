package scc.data.dao;

import scc.data.dto.Period;
import scc.data.dto.Rental;

public class RentalDAO {
    private String _rid;
    private String _ts;
    private String id;
    private String houseId;
    private String tenantId;
    private String landlordId;
    private Period period;

    public RentalDAO() {}

    public RentalDAO(Rental r) {this(r.getId(), r.getHouseId(), r.getTenantId(), r.getLandlordId(), r.getPeriod());}

    public RentalDAO(String id, String houseId, String tenantId, String landlordId, Period period) {
        super();
        this.id = id;
        this.houseId = houseId;
        this.tenantId = tenantId;
        this.landlordId = landlordId;
        this.period = period;
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
    public Rental toRental() {
        return new Rental(id, houseId, tenantId, landlordId, period);
    }
    @Override
    public String toString() {
        return "Rental [_rid=" + _rid +
                ", _ts=" + _ts +
                ", id=" + id +
                ", house=" + houseId +
                ", tenant=" + tenantId +
                ", landlord=" + landlordId +
                ", period=" + period +
                ']';
    }

}
