package scc.data.dto;

public class Period {
    private String startDate; // yyyy-mm-dd
    private String endDate; // yyyy-mm-dd
    private int price; // per day
    private int promotionPrice; // per day
    private boolean available;


    public Period(){}

    public Period(String startDate, String endDate, int price, int promotionPrice, boolean available) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.promotionPrice = promotionPrice;
        this.available = available;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getPrice() {
        return price;
    }

    public int getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(int promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Period [startDate=" + startDate +
                ", endDate=" + endDate +
                ", price=" + price +
                ", promotionPrice=" + promotionPrice +
                ", available=" + available +
                ']';
    }
}
