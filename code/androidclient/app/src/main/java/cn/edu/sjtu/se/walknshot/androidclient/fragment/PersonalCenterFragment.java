package cn.edu.sjtu.se.walknshot.androidclient.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.sjtu.se.walknshot.androidclient.R;
import cn.edu.sjtu.se.walknshot.androidclient.activity.LoginActivity;
import cn.edu.sjtu.se.walknshot.androidclient.activity.SettingActivity;
import cn.edu.sjtu.se.walknshot.androidclient.activity.ViewPathActivity;
import cn.edu.sjtu.se.walknshot.androidclient.util.MyToast;

import static android.content.Context.MODE_PRIVATE;

public class PersonalCenterFragment extends Fragment
        implements AdapterView.OnItemClickListener {

    private ImageView mBtnGoSetting;
    private ListView mListView;
    private SimpleAdapter adapter;
    private String FEEDBACK;
    private String HELP;
    private String CHANGE_PSW;
    private String EXIT;

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

        FEEDBACK = getResources().getString(R.string.title_feedback);
        HELP = getResources().getString(R.string.title_help);
        CHANGE_PSW = getResources().getString(R.string.title_change_psw);
        EXIT = getResources().getString(R.string.action_exit);

        HashMap<String, String> map1 = new HashMap<>();
        map1.put("date", CHANGE_PSW);
        map1.put("start_time", "");
        pathList.add(map1);
        HashMap<String, String> map2 = new HashMap<>();
        map2.put("date", HELP);
        map2.put("start_time", "");
        pathList.add(map2);
        HashMap<String, String> map3 = new HashMap<>();
        map3.put("date", FEEDBACK);
        map3.put("start_time", "");
        pathList.add(map3);
        HashMap<String, String> map4 = new HashMap<>();
        map4.put("date", EXIT);
        map4.put("start_time", "");
        pathList.add(map4);

        adapter = new SimpleAdapter(getActivity(),
                pathList,   //数据来源
                R.layout.item_path, //ListItem的XML实现
                new String[]{"date", "start_time"}, //动态数组与ListItem对应的子项
                new int[]{R.id.date, R.id.time});   //ListView的XML文件里面的两个TextView ID

        View layout = inflater.inflate(R.layout.item_head_personal_center,
                (ViewGroup) view.findViewById(R.id.head_personal_center));
        TextView textView = layout.findViewById(R.id.personal_center_username);
        textView.setText(getActivity().getSharedPreferences("info", MODE_PRIVATE).
                getString("username", "Usernameee"));
        mListView.addHeaderView(layout);
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
            String btn = map.get("date");

            if (btn.equals(FEEDBACK) || btn.equals(HELP) || btn.equals(CHANGE_PSW)) {
                MyToast.makeText(getActivity().getApplicationContext(), R.string.error_invalid_function, MyToast.LENGTH_SHORT).show();
            } else if (btn.equals(EXIT)) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.hint_start_exit)
                        .setPositiveButton(R.string.reply_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("info", MODE_PRIVATE);
                                SharedPreferences.Editor mEditor = sharedPreferences.edit();
                                mEditor.putString("token", "");
                                mEditor.apply();

                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.reply_cancel, null)
                        .show();
            }
        }
    }
}
