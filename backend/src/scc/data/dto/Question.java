package scc.data.dto;

public class Question {
    private String id;
    private String userId;
    private String message;
    private Reply reply;

    public Question(String id, String userId, String message) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.reply = null;
    }

    public Question(String id, String userId, String message, Reply reply) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.reply = reply;
    }

    public Question(){}


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public Reply getReply() {
        return reply;
    }
    public void setReply(Reply reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return "Question [" +
                "id=" + id +
                ", user=" + userId +
                ", message=" + message +
                ", reply=" + reply +
                ']';
    }

}
