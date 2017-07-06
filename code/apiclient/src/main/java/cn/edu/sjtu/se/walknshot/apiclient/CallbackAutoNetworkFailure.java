package cn.edu.sjtu.se.walknshot.apiclient;

import java.io.IOException;

public abstract class CallbackAutoNetworkFailure implements Callback {
    @Override
    public void onNetworkFailure(IOException e) {
        onFailure(null);
    }
}
