package cn.edu.sjtu.se.walknshot.androidclient.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.edu.sjtu.se.walknshot.androidclient.R;

public class SettingActivity extends MyAppCompatActivity {

    Button mBtnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initViews();
    }

    private void initViews() {
        mBtnExit = (Button) findViewById(R.id.setting_btn_exit);
    }

    public void logout(View view) {
        final SettingActivity This = this;
        new AlertDialog.Builder(this)
                .setMessage(R.string.hint_start_exit)
                .setPositiveButton(R.string.reply_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
                        SharedPreferences.Editor mEditor = sharedPreferences.edit();
                        mEditor.putString("token", "");
                        mEditor.apply();

                        Intent intent = new Intent(This, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.reply_cancel, null)
                .show();
    }
}
