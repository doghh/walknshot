package cn.edu.sjtu.se.walknshot.androidclient.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

import cn.edu.sjtu.se.walknshot.androidclient.util.MyToast;
import cn.edu.sjtu.se.walknshot.androidclient.R;


public class LoginActivity extends Activity implements OnClickListener {

    private TextView mBtnLogin, mBtnGoRegister, mBtnForgetPsw;

    private EditText mName, mPsw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarUpperAPI21();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setStatusBarUpperAPI19();
        }
        initView();
    }

    private void initView() {
        mBtnLogin = (TextView) findViewById(R.id.login_btn_login);
        mBtnGoRegister = (TextView) findViewById(R.id.login_btn_register);
        mBtnForgetPsw = (TextView) findViewById(R.id.login_btn_forget_pw);
        mName = findViewById(R.id.login_input_name);
        mPsw = findViewById(R.id.login_input_psw);

        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        mName.setText(sharedPreferences.getString("username", ""));
        mPsw.setText(sharedPreferences.getString("password", ""));

        mBtnLogin.setOnClickListener(this);
        mBtnGoRegister.setOnClickListener(this);
        mBtnForgetPsw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.login_btn_login: {

                String name = mName.getText().toString();
                String psw = mPsw.getText().toString();

                // blank username or password
                if ("".equals(name) || "".equals(psw)) {
                    MyToast.makeText(getApplicationContext(), R.string.error_invalid_blank, MyToast.LENGTH_SHORT).show();
                }
                // invalid username
                else if (!Pattern.matches("([a-z][a-z0-9_]{3,30})", name)) {
                    MyToast.makeText(getApplicationContext(), R.string.error_invalid_username, MyToast.LENGTH_SHORT).show();
                }
                // invalid password
                else if (psw.length() < 6) {
                    MyToast.makeText(getApplicationContext(), R.string.error_invalid_password, MyToast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, LoginLoadingActivity.class);
                    intent.putExtra("USERNAME", name);
                    intent.putExtra("PASSWORD", psw);
                    startActivityForResult(intent, 1);
                }

                break;
            }

            case R.id.login_btn_register: {
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.login_btn_forget_pw: {
                MyToast.makeText(getApplicationContext(), "呵呵，密码都能忘，可以说非常豹笑了", MyToast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String loginStatus = data.getStringExtra("LOGIN_STATUS");
                    if ("FAIL_NETWORK".equals(loginStatus)) {
                        MyToast.makeText(getApplicationContext(), R.string.error_network_fail, MyToast.LENGTH_SHORT).show();
                    } else if ("FAIL".equals(loginStatus)) {
                        MyToast.makeText(getApplicationContext(), R.string.error_login_fail, MyToast.LENGTH_SHORT).show();
                    } else if ("SUCCESS".equals(loginStatus)) {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        MyToast.makeText(getApplicationContext(), R.string.login_success, MyToast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
        }
    }

    @TargetApi(21)
    private void setStatusBarUpperAPI21() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        ViewGroup mContentView = findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, true);
        }
    }

    @TargetApi(19)
    private void setStatusBarUpperAPI19() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ViewGroup mContentView = findViewById(Window.ID_ANDROID_CONTENT);
        int statusBarHeight = getStatusBarHeight();
        int statusColor = getResources().getColor(R.color.colorPrimary);

        View mTopView = mContentView.getChildAt(0);
        if (mTopView != null && mTopView.getLayoutParams() != null &&
                mTopView.getLayoutParams().height == statusBarHeight) {
            //避免重复添加 View
            mTopView.setBackgroundColor(statusColor);
            return;
        }
        //使 ChildView 预留空间
        if (mTopView != null) {
            ViewCompat.setFitsSystemWindows(mTopView, true);
        }
        //添加假 View
        mTopView = new View(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        mTopView.setBackgroundColor(statusColor);
        mContentView.addView(mTopView, 0, lp);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = getResources().getDimensionPixelSize(resId);
        }
        return result;
    }
}

