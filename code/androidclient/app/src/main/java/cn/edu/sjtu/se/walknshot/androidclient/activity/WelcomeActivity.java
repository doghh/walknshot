package cn.edu.sjtu.se.walknshot.androidclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

import cn.edu.sjtu.se.walknshot.androidclient.R;
import cn.edu.sjtu.se.walknshot.apiclient.*;
import cn.edu.sjtu.se.walknshot.apimessages.*;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 取消导航栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);

        // 启动界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // 背景透明度变化2秒内从0.0变到1.0
        FrameLayout welcomePage = findViewById(R.id.welcome_layout);
        AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
        aa.setDuration(2000);
        welcomePage.startAnimation(aa);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
                String token = sharedPreferences.getString("token", "");
                if (!"".equals(token)) {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    ClientImpl client = ClientImpl.getInstance();
                    client.setToken(Token.fromString(token));
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.schedule(timerTask, 3000);
    }

    // 启动界面禁用Back键退出，避免多次启动login界面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
