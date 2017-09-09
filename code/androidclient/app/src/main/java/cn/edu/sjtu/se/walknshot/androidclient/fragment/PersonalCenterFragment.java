package cn.edu.sjtu.se.walknshot.androidclient.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.sjtu.se.walknshot.androidclient.R;
import cn.edu.sjtu.se.walknshot.androidclient.activity.SettingActivity;
import cn.edu.sjtu.se.walknshot.androidclient.activity.ViewPathActivity;
import cn.edu.sjtu.se.walknshot.androidclient.util.MyToast;

public class PersonalCenterFragment extends Fragment
        implements AdapterView.OnItemClickListener {

    private ImageView mBtnGoSetting;
    private ListView mListView;
    private SimpleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_center, container, false);

        initView(inflater, view);

        return view;
    }

    private void initView(LayoutInflater inflater, View view) {
        mBtnGoSetting = view.findViewById(R.id.persenal_center_btn_setting);
        mBtnGoSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });


        mListView = view.findViewById(R.id.list_view);
        mListView.setDividerHeight(0);
        mListView.setPadding(10, 10, 10, 10);

        //生成动态数组，并且转载数据
        ArrayList<HashMap<String, String>> pathList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("date", "2017-09-10");
            map.put("start_time", "21:17");
            pathList.add(map);
        }
        for (int i = 0; i < 3; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("date", "2017-09-11");
            map.put("start_time", "10:03");
            pathList.add(map);
        }

        adapter = new SimpleAdapter(getActivity(),
                pathList,   //数据来源
                R.layout.item_path, //ListItem的XML实现
                new String[]{"date", "start_time"}, //动态数组与ListItem对应的子项
                new int[]{R.id.date, R.id.time});   //ListView的XML文件里面的两个TextView ID

        mListView.addHeaderView(inflater.inflate(R.layout.item_head_personal_center, null, false));
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    /**
     * 响应ListView中item的点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
        if (position == 0) {
            mBtnGoSetting.performClick();
        } else {
            @SuppressWarnings("unchecked")
            HashMap<String, String> map = (HashMap<String, String>) adapter.getItem(position - 1);
   //         String string = "date: " + map.get("date") + "   time: " + map.get("start_time");
            Intent intent = new Intent(getActivity(), ViewPathActivity.class);
            startActivity(intent);
        }
    }
}
