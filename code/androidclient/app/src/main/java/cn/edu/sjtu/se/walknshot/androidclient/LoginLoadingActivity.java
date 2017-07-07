package cn.edu.sjtu.se.walknshot.androidclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

import cn.edu.sjtu.se.walknshot.apiclient.*;

public class LoginLoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        TextView hint = findViewById(R.id.loading_hint);
        hint.setText(R.string.loading_login);
        final String username = getIntent().getStringExtra("USERNAME");
        final String password = getIntent().getStringExtra("PASSWORD");
        Client client = ClientImpl.getInstance();
        client.login(new Callback() {
            @Override
            public void onNetworkFailure(IOException e) {
                Intent intent = new Intent();
                intent.putExtra("LOGIN_STATUS", "FAIL_NETWORK");
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Object arg) {
                Intent intent = new Intent();
                intent.putExtra("LOGIN_STATUS", "FAIL");
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onSuccess(Object arg) {

                // 本地保存用户名和密码
                SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
                SharedPreferences.Editor mEditor = sharedPreferences.edit();
                mEditor.putString("username", username);
                mEditor.putString("password", password);
                mEditor.putString("login", "yes");
                mEditor.commit();

                Intent intent = new Intent();
                intent.putExtra("LOGIN_STATUS", "SUCCESS");
                setResult(RESULT_OK, intent);
                finish();
            }
        }, username, password);
    }
}
