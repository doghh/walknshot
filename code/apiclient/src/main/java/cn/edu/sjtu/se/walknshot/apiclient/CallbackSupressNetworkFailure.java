package cn.edu.sjtu.se.walknshot.apiclient;

import java.io.IOException;

public abstract class CallbackSupressNetworkFailure implements Callback {
    @Override
    public void onNetworkFailure(IOException e) {
        // NOP
    }
}
