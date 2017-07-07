package cn.edu.sjtu.se.walknshot.androidclient;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

//    private MapPageFragment mapPageFragment;
//    private RecordPathFragment recordPathFragment;
//    private PersonalCenterFragment personalCenterFragment;
//
//    private int currentId = R.id.tab_map_page;
//
//    private TextView mMapPage, mRecordPath, mPersonalCenter;
//    private Drawable mapIcon, recordPathIcon, personalCenterIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mMapPage = findViewById(R.id.tab_map_page);
//        mapIcon = getResources().getDrawable(R.drawable.icon_map);
//        mapIcon.setTint(getResources().getColor(R.color.lostGrey));
//        mapIcon.setBounds(0, 0, 24, 24);
//        mMapPage.setCompoundDrawables(null, mapIcon, null, null);
//
//        mRecordPath = findViewById(R.id.tab_record_path);
//        mPersonalCenter = findViewById(R.id.tab_personal_center);
//
//        //默认首页为MapPage
//        mMapPage.setSelected(true);
//        mapPageFragment = new MapPageFragment();
//        getFragmentManager().beginTransaction().add(R.id.main_content, mapPageFragment).commit();
//
//        //为3个tab设置监听
//        mMapPage.setOnClickListener(tabClickListener);
//        mRecordPath.setOnClickListener(tabClickListener);
//        mPersonalCenter.setOnClickListener(tabClickListener);
    }

//    private View.OnClickListener tabClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            //如果当前选中跟上次选中的一样,不需要处理
//            if (v.getId() != currentId) {
//                changeSelect(v.getId());    //改变图标跟文字颜色的选中
//                changeFragment(v.getId());  //切换fragment
//                currentId = v.getId();      //设置选中id
//            }
//        }
//    };
//
//    private void changeFragment(int resId) {
//        //开启一个Fragment事务
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        //隐藏所有fragment
//        hideFragments(transaction);
//
//        switch (resId) {
//            case R.id.tab_map_page: {
//                if (mapPageFragment == null) {
//                    mapPageFragment = new MapPageFragment();
//                    transaction.add(R.id.main_content, mapPageFragment);
//                } else {
//                    transaction.show(mapPageFragment);
//                }
//                break;
//            }
//            case R.id.tab_record_path: {
//                if (recordPathFragment == null) {
//                    recordPathFragment = new RecordPathFragment();
//                    transaction.add(R.id.main_content, recordPathFragment);
//                } else {
//                    transaction.show(recordPathFragment);
//                }
//                break;
//            }
//            case R.id.tab_personal_center: {
//                if (personalCenterFragment == null) {
//                    personalCenterFragment = new PersonalCenterFragment();
//                    transaction.add(R.id.main_content, personalCenterFragment);
//                } else {
//                    transaction.show(personalCenterFragment);
//                }
//                break;
//            }
//        }
//        transaction.commit();
//    }
//
//    /**
//     * 隐藏所有存在的fragment
//     *
//     * @param transaction
//     */
//    private void hideFragments(FragmentTransaction transaction) {
//        if (mapPageFragment != null)//不为空才隐藏,如果不判断第一次会有空指针异常
//            transaction.hide(mapPageFragment);
//        if (recordPathFragment != null)
//            transaction.hide(recordPathFragment);
//        if (personalCenterFragment != null)
//            transaction.hide(personalCenterFragment);
//    }
//
//    /**
//     * 改变选中样式
//     *
//     * @param resId
//     */
//    private void changeSelect(int resId) {
//        mMapPage.setSelected(false);
//        mRecordPath.setSelected(false);
//        mPersonalCenter.setSelected(false);
//
//        switch (resId) {
//            case R.id.tab_map_page:
//                mMapPage.setSelected(true);
//                break;
//            case R.id.tab_record_path:
//                mRecordPath.setSelected(true);
//                break;
//            case R.id.tab_personal_center:
//                mPersonalCenter.setSelected(true);
//                break;
//        }
//    }
}
