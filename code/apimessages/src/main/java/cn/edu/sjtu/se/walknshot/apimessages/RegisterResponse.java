package cn.edu.sjtu.se.walknshot.apimessages;

public class RegisterResponse {
    private Boolean success;
    private int userId;
    private String message;

    public RegisterResponse() {
        success = false;
    }

    public RegisterResponse(String failReason) {
        success = false;
        userId = 0;
        message = failReason;
    }

    public RegisterResponse(int userId) {
        success = true;
        this.userId = userId;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
