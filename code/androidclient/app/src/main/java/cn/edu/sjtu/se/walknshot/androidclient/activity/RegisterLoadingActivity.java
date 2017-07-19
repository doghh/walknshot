package cn.edu.sjtu.se.walknshot.androidclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.io.IOException;

import cn.edu.sjtu.se.walknshot.androidclient.R;
import cn.edu.sjtu.se.walknshot.apiclient.*;

public class RegisterLoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        String username = getIntent().getStringExtra("REGISTER_USERNAME");
        String password = getIntent().getStringExtra("REGISTER_PASSWORD");
        Client client = ClientImpl.getInstance();
        client.register(new Callback() {
            @Override
            public void onNetworkFailure(IOException e) {
                Intent intent = new Intent();
                intent.putExtra("REGISTER_STATUS", "FAIL_NETWORK");
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Object arg) {
                Intent intent = new Intent();
                intent.putExtra("REGISTER_STATUS", "FAIL");
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onSuccess(Object arg) {
                Intent intent = new Intent();
                intent.putExtra("REGISTER_STATUS", "SUCCESS");
                setResult(RESULT_OK, intent);
                finish();
            }
        }, username, password);
    }
}
