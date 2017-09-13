package cn.edu.sjtu.se.walknshot.androidclient.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;

import java.util.ArrayList;

import cn.edu.sjtu.se.walknshot.androidclient.R;
import cn.edu.sjtu.se.walknshot.androidclient.activity.AddPicturesActivity;
import cn.edu.sjtu.se.walknshot.androidclient.activity.CommentActivity;
import cn.edu.sjtu.se.walknshot.androidclient.activity.MainActivity;
import cn.edu.sjtu.se.walknshot.androidclient.activity.ViewPicActivity;
import cn.edu.sjtu.se.walknshot.androidclient.model.Post;
import cn.edu.sjtu.se.walknshot.androidclient.util.PostAdapter;
import me.rawn_hwang.library.widgit.SmartLoadingLayout;

public class PostListFragment extends Fragment implements
        //      AdapterView.OnItemClickListener,
        //      AdapterView.OnItemLongClickListener,
        PostAdapter.Callback,
        View.OnTouchListener {

    private SmartLoadingLayout mSmartLoadingLayout;
    private ListView mListView;
    private ImageView mBtnShare, mBtnRefresh;
    ArrayList<Post> mPosts = new ArrayList<>();
    PostAdapter mAdapter;

    private int mTouchSlop;
    private float mFirstY;
    private float mCurrentY;

    private final int EMPTY = 0;
    private final int DONE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);

        mBtnShare = rootView.findViewById(R.id.post_btn_share);
        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddPicturesActivity.class);
                intent.putExtra("source", "discovery");
                startActivity(intent);
            }
        });

        mListView = rootView.findViewById(R.id.list_view);
        mListView.setDividerHeight(0);
        mListView.setPadding(10, 10, 10, 10);
        mListView.setOnTouchListener(this);
        mAdapter = new PostAdapter(getActivity(), this);
        //     mListView.addHeaderView(inflater.inflate(R.layout.item_head_post, null, false));
        //     mListView.setEmptyView();
        mListView.setAdapter(mAdapter);
        //      mListView.setOnItemClickListener(this);
        ///     mListView.setOnItemLongClickListener(this);

        mSmartLoadingLayout = SmartLoadingLayout.createDefaultLayout(getActivity(), mListView);
        mSmartLoadingLayout.onLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 下载数据的耗时过程
                downloadData();
            }
        }).start();

        mBtnRefresh = rootView.findViewById(R.id.post_btn_refresh);
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSmartLoadingLayout.onLoading();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 下载数据的耗时过程
                        downloadData();
                    }
                }).start();
            }
        });

        mTouchSlop = ViewConfiguration.get(getActivity()).getScaledTouchSlop();

        return rootView;
    }

//    /**
//     * 响应ListView中item的点击事件
//     */
//    @Override
//    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
//    }
//
//    @Override
//    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
//        return true;
//    }

    /**
     * 接口方法，响应ListView按钮点击事件
     */
    @Override
    public void click(View v, int type) {
        switch (type) {
//            case PostAdapter.LIKE: {
//                MyToast.makeText(
//                        getActivity().getApplicationContext(),
//                        "listview的内部的按钮被点击了！，位置是-->" + (Integer) v.getTag() + ",内容是-->"
//                                + mPosts.get(((Integer) v.getTag()) / 4).getBody(),
//                        MyToast.LENGTH_SHORT).show();
//                break;
//            }
            case PostAdapter.COMMENT: {
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                // v.getTag() 得到的是不包括Header的内部position
                // ID = posts.get(((Integer) v.getTag()) / 4).getPostId();
                startActivity(intent);
                break;
            }
            case PostAdapter.MORE: {
                // ID = posts.get(((Integer) v.getTag()) / 4).getPostId();
                Bitmap pic = BitmapFactory.decodeResource(getResources(), R.drawable.bg_welcome);

                WXImageObject imageObject = new WXImageObject(pic);
                //这个构造方法中自动把传入的bitmap转化为2进制数据,或者你直接传入byte[]也行
                //注意传入的数据不能大于10M,开发文档上写的

                WXMediaMessage msg = new WXMediaMessage();  //这个对象是用来包裹发送信息的对象
                msg.mediaObject = imageObject;
                //msg.mediaObject实际上是个IMediaObject对象,
                //它有很多实现类,每一种实现类对应一种发送的信息,
                //比如WXTextObject对应发送的信息是文字,想要发送文字直接传入WXTextObject对象就行

                Bitmap thumbBitmap = Bitmap.createScaledBitmap(pic, 150, 150, true);

                //msg.thumbData = bitmap2ByteArray(thumbBitmap);
                //在这设置缩略图
                //官方文档介绍这个bitmap不能超过32kb
                //如果一个像素是8bit的话换算成正方形的bitmap则边长不超过181像素,边长设置成150是比较保险的
                //或者使用msg.setThumbImage(thumbBitmap);省去自己转换二进制数据的过程
                msg.setThumbImage(thumbBitmap);
                //如果超过32kb则抛异常

                SendMessageToWX.Req req = new SendMessageToWX.Req();    //创建一个请求对象
                req.message = msg;  //把msg放入请求对象中
                //req.scene = SendMessageToWX.Req.WXSceneTimeline;    //设置发送到朋友圈
                req.scene = SendMessageToWX.Req.WXSceneSession;   //设置发送给朋友
                req.transaction = "test";  //这个tag要唯一,用于在回调中分辨是哪个分享请求
                boolean b = ((MainActivity) getActivity()).wxApi.sendReq(req);   //如果调用成功微信,会返回true
                break;
            }
            case PostAdapter.IMGS: {
                // open view photo page
                // ID = posts.get(((Integer) v.getTag()) / 4).getPostId();
                Intent intent = new Intent(getActivity(), ViewPicActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFirstY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentY = event.getY();
                if (mCurrentY - mFirstY > mTouchSlop) {
                    mBtnRefresh.setVisibility(View.VISIBLE);
                } else if (mFirstY - mCurrentY > mTouchSlop) {
                    mBtnRefresh.setVisibility(View.GONE);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

    private final PostListFragment This = this;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case EMPTY:
                    mSmartLoadingLayout.onEmpty();
                    break;
                case DONE:
                    mSmartLoadingLayout.onDone();
                    mAdapter.setData(mPosts);
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    mSmartLoadingLayout.onError();
                    break;
            }
        }
    };

    public void downloadData() {
        String type = getArguments().getString("key");
        if (type == "all") {
            //获取所有post并add到mPosts中
            Bitmap bbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_welcome);
            Bitmap bitmap = ThumbnailUtils.extractThumbnail(bbitmap, 1000, 1000);
            mPosts.add(new Post(1, "title1", "This is News 1.", bitmap));
            mPosts.add(new Post(2, "title2", "This is News 2.", bitmap));
            mPosts.add(new Post(3, "title3", "This is News 3.", bitmap));
            mPosts.add(new Post(4, "title4", "This is News 4.", bitmap));
        } else if (type == "mine") {
            //获取用户的post并add到mPosts中
            Bitmap bbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_welcome);
            Bitmap bitmap = ThumbnailUtils.extractThumbnail(bbitmap, 1000, 1000);
            mPosts.add(new Post(1, "title1", "This is News 1.", bitmap));
        }

        Message msgMessage = new Message();

        if (mPosts.size() <= 0) {
            msgMessage.arg1 = EMPTY;
            mHandler.sendMessage(msgMessage);
        } else {
            msgMessage = new Message();
            msgMessage.arg1 = DONE;
            mHandler.sendMessage(msgMessage);
        }
    }
}
