package cn.edu.sjtu.se.walknshot.apiclient;

import okhttp3.*;

import java.io.IOException;

public abstract class CallbackForward implements okhttp3.Callback {
    private Callback callback;

    CallbackForward(Callback callback) {
        super();
        this.callback = callback;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        callback.onNetworkFailure(e);
    }

}
