package main.java.data.dto;

public class Reply {
    private String userId;
    private String message;

    public Reply(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    @Override
    public String toString() {
        return "Reply [" +
                "user=" + userId +
                ", message=" + message +
                ']';
    }

}
