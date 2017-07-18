package cn.edu.sjtu.se.walknshot.apiclient;

import java.util.List;

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

    // onSuccess: Long spot ID
    // onFailure: null
    void addSpot(Callback callback, double latitude, double longitude);

    // Upload picture with spot = most recently added spot
    // onSuccess: PictureEntry
    // onFailure: null
    void uploadPicture(Callback callback, byte[] file);

    // onSuccess: byte[] image file
    // onFailure: null
    void downloadPicture(Callback callback, String storageName);

    // onSuccess: PGroupDetails
    // onFailure: null
    void uploadPGroup(Callback callback, List<byte[]> pictures);

    // Local calls

    // onSuccess: Boolean always true
    // onFailure: Not used
    // onNetworkFailure: Suppressed
    void logout(Callback callback);
    boolean isLoggedIn();
    Integer getUserId();
}
