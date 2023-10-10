package data.dao;

import data.dto.Question;

public class QuestionDAO {
    private String _rid;
    private String _ts;
    private String id;
    private String houseId;
    private String userId;
    private String message;

    public QuestionDAO() {}

    public QuestionDAO(Question q) {this(q.getId(), null, q.getUserId(), q.getMessage());}

    public QuestionDAO(String id, String houseId, String userId, String message) {
        this.id = id;
        this.houseId = houseId;
        this.userId = userId;
        this.message = message;
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
        return "Question [_rid=" + _rid +
                ", _ts=" + _ts +
                "id=" + id +
                ", house=" + houseId +
                ", user=" + userId +
                ", message=" + message +
                ']';
    }

}
