package cn.edu.sjtu.se.walknshot.apiclient;

import java.io.IOException;

public interface Callback {
    void onNetworkFailure(IOException e);

    void onFailure(Object arg);

    void onSuccess(Object arg);
}
