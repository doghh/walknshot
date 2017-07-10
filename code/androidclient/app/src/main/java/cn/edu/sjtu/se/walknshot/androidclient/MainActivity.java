package cn.edu.sjtu.se.walknshot.androidclient;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private MapPageFragment mapPageFragment;
    private DiscoveryFragment discoveryFragment;
    private PersonalCenterFragment personalCenterFragment;

    private int currentId = R.id.tab_map_page;

    private LinearLayout mMapPage, mDiscovery, mPersonalCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapPage = findViewById(R.id.tab_map_page);
        mDiscovery = findViewById(R.id.tab_dicovery);
        mPersonalCenter = findViewById(R.id.tab_personal_center);

        //默认首页为MapPage
        mMapPage.setSelected(true);
        mapPageFragment = new MapPageFragment();
        getFragmentManager().beginTransaction().add(R.id.main_content, mapPageFragment).commit();

        //为3个tab设置监听
        mMapPage.setOnClickListener(tabClickListener);
        mDiscovery.setOnClickListener(tabClickListener);
        mPersonalCenter.setOnClickListener(tabClickListener);
    }

    private View.OnClickListener tabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //如果当前选中跟上次选中的一样,不需要处理
            if (v.getId() != currentId) {
                changeSelect(v.getId());    //改变图标跟文字颜色的选中
                changeFragment(v.getId());  //切换fragment
                currentId = v.getId();      //设置选中id
            }
        }
    };

    private void changeFragment(int resId) {
        //开启一个Fragment事务
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //隐藏所有fragment
        hideFragments(transaction);

        switch (resId) {
            case R.id.tab_map_page: {
                if (mapPageFragment == null) {
                    mapPageFragment = new MapPageFragment();
                    transaction.add(R.id.main_content, mapPageFragment);
                } else {
                    transaction.show(mapPageFragment);
                }
                break;
            }
            case R.id.tab_dicovery: {
                if (discoveryFragment == null) {
                    discoveryFragment = new DiscoveryFragment();
                    transaction.add(R.id.main_content, discoveryFragment);
                } else {
                    transaction.show(discoveryFragment);
                }
                break;
            }
            case R.id.tab_personal_center: {
                if (personalCenterFragment == null) {
                    personalCenterFragment = new PersonalCenterFragment();
                    transaction.add(R.id.main_content, personalCenterFragment);
                } else {
                    transaction.show(personalCenterFragment);
                }
                break;
            }
        }
        transaction.commit();
    }

    /**
     * 隐藏所有存在的fragment
     *
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (mapPageFragment != null)//不为空才隐藏,如果不判断第一次会有空指针异常
            transaction.hide(mapPageFragment);
        if (discoveryFragment != null)
            transaction.hide(discoveryFragment);
        if (personalCenterFragment != null)
            transaction.hide(personalCenterFragment);
    }

    /**
     * 改变选中样式
     *
     * @param resId
     */
    private void changeSelect(int resId) {
        mMapPage.setSelected(false);
        mDiscovery.setSelected(false);
        mPersonalCenter.setSelected(false);

        switch (resId) {
            case R.id.tab_map_page:
                mMapPage.setSelected(true);
                break;
            case R.id.tab_dicovery:
                mDiscovery.setSelected(true);
                break;
            case R.id.tab_personal_center:
                mPersonalCenter.setSelected(true);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
