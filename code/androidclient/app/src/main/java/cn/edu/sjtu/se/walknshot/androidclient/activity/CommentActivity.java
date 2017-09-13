package cn.edu.sjtu.se.walknshot.androidclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.sjtu.se.walknshot.androidclient.R;
import cn.edu.sjtu.se.walknshot.androidclient.util.MyToast;
import cn.edu.sjtu.se.walknshot.apiclient.*;
import cn.edu.sjtu.se.walknshot.apimessages.*;

/**
 * Created by zhangqiaoyu on 2017/7/20.
 */

public class CommentActivity extends MyAppCompatActivity {

    private GridView cGridView;    //评论显示
    private Button mBtnSubmitCom;   //评论提交按钮
    private EditText mComment;  //评论框

    private String pgroupid;   //图片组id

    private String comment;   //评论
    private ArrayList<HashMap<String, Object>> commentItem;
    private SimpleAdapter simpleAdapter;   //基础适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //获取传入参数
        Intent intent = getIntent();
        pgroupid = intent.getStringExtra("groupid");

        initView();
    }

    private void initView() {

        mBtnSubmitCom = (Button) findViewById(R.id.mBtnSubmitCom);
        mComment = (EditText) findViewById(R.id.myComment);
        mBtnSubmitCom.setOnClickListener(onClickListener);

        //获取控件对象
        cGridView = (GridView) findViewById(R.id.add_com_gridview);

        comment = "没有啦";
        commentItem = new ArrayList<>();

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
                int num = x.getComments().size();
                for (int i = 0; i < num; i++) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("itemComment", x.getComments().get(i).getContent());
                    commentItem.add(0, map);
                    Message msgMessage = new Message();
                    msgMessage.arg1 = 1;
                    mHandler.sendMessage(msgMessage);
                }
            }
        }, Integer.parseInt(pgroupid));
    }

    private final CommentActivity This = this;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 1:
                    simpleAdapter = new SimpleAdapter(This,
                            commentItem, R.layout.my_view_comment,
                            new String[]{"itemComment"}, new int[]{R.id.comment});

                    //自定义ViewBinder()
                    simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                        @Override
                        public boolean setViewValue(View view, Object data,
                                                    String textRepresentation) {
                            // TODO Auto-generated method stub
                            if (view instanceof TextView && data instanceof String) {
                                TextView i = (TextView) view;
                                i.setText((String) data);
                                return true;
                            }
                            return false;
                        }
                    });
                    cGridView.setAdapter(simpleAdapter);
                    break;
            }
        }
    };

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mBtnSubmitCom) {
                //提交评论并刷新activity
                comment = "";
                if (TextUtils.isEmpty(mComment.getText())) {
                    //评论为空
                    MyToast.makeText(getApplicationContext(), "Comment can't be empty", MyToast.LENGTH_SHORT).show();
                } else {
                    comment = mComment.getText().toString();
                    mComment.setText("");

                    /*
                        此处提交获得的评论到服务器，并且获取评论返回值
                     */
                    final ClientImpl client = ClientImpl.getInstance();
                    client.addComment(new Callback() {
                        @Override
                        public void onNetworkFailure(IOException e) {
                            MyToast.makeText(getApplicationContext(), R.string.error_network_fail, MyToast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Object arg) {
                            MyToast.makeText(getApplicationContext(), R.string.error_upload_fail, MyToast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(Object arg) {
                            MyToast.makeText(getApplicationContext(), R.string.upload_success, MyToast.LENGTH_SHORT).show();
                        }
                    }, Integer.parseInt(pgroupid), comment);

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("itemComment", comment);
                    commentItem.add(0, map);

                    simpleAdapter = new SimpleAdapter(getApplicationContext(),
                            commentItem, R.layout.my_view_comment,
                            new String[]{"itemComment"}, new int[]{R.id.comment});

                    //自定义ViewBinder()
                    simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                        @Override
                        public boolean setViewValue(View view, Object data,
                                                    String textRepresentation) {
                            // TODO Auto-generated method stub
                            if (view instanceof TextView && data instanceof String) {
                                TextView i = (TextView) view;
                                i.setText((String) data);
                                return true;
                            }
                            return false;
                        }
                    });
                    cGridView.setAdapter(simpleAdapter);
                    simpleAdapter.notifyDataSetChanged();
                }
            }
        }

    };
}
