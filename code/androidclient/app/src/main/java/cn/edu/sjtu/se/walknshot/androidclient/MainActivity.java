package cn.edu.sjtu.se.walknshot.androidclient;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        PersonalCenterFragment defaultFragment = new PersonalCenterFragment();
        MapPageFragment defaultFragment = new MapPageFragment();
        getFragmentManager().beginTransaction().replace(R.id.main_content, defaultFragment).commit();
    }
}
