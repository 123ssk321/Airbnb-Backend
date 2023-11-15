package scc.server.auth;

public class LoginDetails {
    private String userId; // nickname
    private String pwd;

    public LoginDetails(String userId, String pwd) {
        this.userId = userId;
        this.pwd = pwd;
    }

    public LoginDetails(){}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return  "LoginDetails [id=" + userId + ", pwd=" + pwd + "]";
    }
}
