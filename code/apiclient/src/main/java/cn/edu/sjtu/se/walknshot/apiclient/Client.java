package cn.edu.sjtu.se.walknshot.apiclient;

public interface Client {
    // Async calls with network activities

    // onSuccess: Integer of new UserId
    // onFailure: Integer code describing reason
    //  1: bad username or password
    //  2: duplicate username
    void register(Callback callback, String username, String password);

    // onSuccess: Integer of UserId
    // onFailure: Integer describing reason
    //  1: bad username or password
    //  2: authentication failure
    void login(Callback callback, String username, String password);

    // onSuccess: Boolean describe whether valid
    // onFailure: Not used
    void isLoginValid(Callback callback);

    // Local calls

    // onSuccess: Boolean always true
    // onFailure: Not used
    // onNetworkFailure: Suppressed
    void logout(Callback callback);
    boolean isLoggedIn();
    Integer getUserId();
}
