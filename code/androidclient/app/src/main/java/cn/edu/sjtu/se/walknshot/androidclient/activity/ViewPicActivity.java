package cn.edu.sjtu.se.walknshot.androidclient.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.sjtu.se.walknshot.androidclient.R;
import cn.edu.sjtu.se.walknshot.androidclient.fragment.PostListFragment;
import cn.edu.sjtu.se.walknshot.androidclient.model.Post;
import cn.edu.sjtu.se.walknshot.androidclient.util.MyToast;
import cn.edu.sjtu.se.walknshot.androidclient.util.PhotoViewPager;
import cn.edu.sjtu.se.walknshot.androidclient.util.ViewPagerAdapter;
import cn.edu.sjtu.se.walknshot.apiclient.*;
import cn.edu.sjtu.se.walknshot.apimessages.*;

public class ViewPicActivity extends MyAppCompatActivity {

    private PhotoViewPager mViewpager;
    private List<byte[]> imgs;
    private int position;
    private int pgroupid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_pic);

        //获取传入参数
        Intent intent = getIntent();
        pgroupid = Integer.parseInt(intent.getStringExtra("groupid"));

        mViewpager = (PhotoViewPager) this.findViewById(R.id.viewpager_imgs);
        mViewpager.setOffscreenPageLimit(2);

        imgs = new ArrayList<>();

        final ViewPicActivity This = this;
        final ClientImpl client = ClientImpl.getInstance();
        client.getPGroupDetails(new Callback() {
            @Override
            public void onNetworkFailure(IOException e) {
                MyToast.makeText(getApplicationContext(), R.string.error_network_fail, MyToast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Object arg) {
                MyToast.makeText(getApplicationContext(), R.string.error_download_fail, MyToast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(Object arg) {
                final PGroupDetails x = (PGroupDetails) arg;
                final int size = x.getPictures().size();
                new Thread(new Runnable() {//创建一个线程内部类
                    @Override
                    public void run() {
                        while (imgs.size() < size) {
                            // wait for 0.5s
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                            }
                        }
                        Message msgMessage = new Message();
                        if (imgs.size() > 0) {
                            msgMessage.arg1 = 1;
                            mHandler.sendMessage(msgMessage);
                        }
                    }
                }).start();
                for (int i = 0; i < x.getPictures().size(); i++) {
                    String storageName = x.getPictures().get(i).getStorageName();
                    client.downloadPicture(new Callback() {
                        @Override
                        public void onNetworkFailure(IOException e) {
                            MyToast.makeText(getApplicationContext(), R.string.error_network_fail, MyToast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Object arg) {
                            MyToast.makeText(getApplicationContext(), R.string.error_download_fail, MyToast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(Object arg) {
                            if (arg != null) {
                                byte[] bis = (byte[]) arg;
                                imgs.add(bis);
                            }
                        }
                    }, storageName);
                }
            }
        }, pgroupid);
    }

    private final ViewPicActivity This = this;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 1:
                    PagerAdapter adapter = new ViewPagerAdapter(This, imgs);
                    mViewpager.setAdapter(adapter);
                    break;
            }
        }
    };

}
