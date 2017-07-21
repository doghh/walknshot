package cn.edu.sjtu.se.walknshot.androidclient.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sjtu.se.walknshot.androidclient.R;
import cn.edu.sjtu.se.walknshot.androidclient.util.PhotoViewPager;
import cn.edu.sjtu.se.walknshot.androidclient.util.ViewPagerAdapter;

public class ViewPicActivity extends MyAppCompatActivity {

    private PhotoViewPager mViewpager;
    private List<String> imgs;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_pic);

        //     this.position = getIntent().getIntExtra("position", 0);
        //     this.imgs = getIntent().getStringArrayListExtra("imgs");

        mViewpager = (PhotoViewPager) this.findViewById(R.id.viewpager_imgs);
        mViewpager.setOffscreenPageLimit(2);

        imgs = new ArrayList<String>();
        imgs.add("");
        imgs.add("");
        imgs.add("");
        imgs.add("");
        PagerAdapter adapter = new ViewPagerAdapter(this, imgs);
        mViewpager.setAdapter(adapter);
    }

}
