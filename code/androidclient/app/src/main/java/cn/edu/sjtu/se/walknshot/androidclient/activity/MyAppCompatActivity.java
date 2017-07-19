package cn.edu.sjtu.se.walknshot.androidclient.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cn.edu.sjtu.se.walknshot.androidclient.R;

/**
 * Created by Dell on 2017/7/5.
 */

public class MyAppCompatActivity extends AppCompatActivity {

    Toolbar mToolbar;

    public MyAppCompatActivity() {
        super();
    }

    protected void initToolbar(int id) {
        mToolbar = (Toolbar) findViewById(id);
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.icon_back);
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);
            }
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initToolbar(R.id.toolbar);
    }

}
