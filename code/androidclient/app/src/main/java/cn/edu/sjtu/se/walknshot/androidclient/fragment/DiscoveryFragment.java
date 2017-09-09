package cn.edu.sjtu.se.walknshot.androidclient.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import cn.edu.sjtu.se.walknshot.androidclient.R;

public class DiscoveryFragment extends Fragment {

    private RelativeLayout mBtnPostAll, mBtnPostMine;
    private View selectMarkAll, selectMarkMine;
    private PostListFragment postListFragmentAll, postListFragmentMine;

    private int currentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discovery, container, false);

        mBtnPostAll = rootView.findViewById(R.id.tab_all_post);
        mBtnPostMine = rootView.findViewById(R.id.tab_my_post);
        selectMarkAll = rootView.findViewById(R.id.all_post_chosen);
        selectMarkMine = rootView.findViewById(R.id.my_post_chosen);

        //默认为all path
        currentId = R.id.tab_all_post;
        selectMarkMine.setVisibility(View.INVISIBLE);
        postListFragmentAll = new PostListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("key", "all");
        postListFragmentAll.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.discovery_content, postListFragmentAll).commit();

        //为2个tab设置监听
        mBtnPostAll.setOnClickListener(tabClickListener);
        mBtnPostMine.setOnClickListener(tabClickListener);

        return rootView;
    }

    private View.OnClickListener tabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //如果当前选中跟上次选中的一样,不需要处理
            if (v.getId() != currentId) {
                if (v.getId() == R.id.tab_all_post) {
                    selectMarkAll.setVisibility(View.VISIBLE);
                    selectMarkMine.setVisibility(View.INVISIBLE);
                } else if (v.getId() == R.id.tab_my_post) {
                    selectMarkAll.setVisibility(View.INVISIBLE);
                    selectMarkMine.setVisibility(View.VISIBLE);
                }
                changeFragment(v.getId());  //切换fragment
                currentId = v.getId();      //设置选中id
            }
        }
    };

    private void changeFragment(int resId) {
        //开启一个Fragment事务
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //隐藏所有fragment
        if (postListFragmentAll != null)//不为空才隐藏,如果不判断第一次会有空指针异常
            transaction.hide(postListFragmentAll);
        if (postListFragmentMine != null)
            transaction.hide(postListFragmentMine);

        switch (resId) {
            case R.id.tab_all_post: {
                if (postListFragmentAll == null) {
                    postListFragmentAll = new PostListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("key", "all");
                    postListFragmentAll.setArguments(bundle);
                    transaction.add(R.id.discovery_content, postListFragmentAll);
                } else {
                    transaction.show(postListFragmentAll);
                }
                break;
            }
            case R.id.tab_my_post: {
                if (postListFragmentMine == null) {
                    postListFragmentMine = new PostListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("key", "mine");
                    postListFragmentMine.setArguments(bundle);
                    transaction.add(R.id.discovery_content, postListFragmentMine);
                } else {
                    transaction.show(postListFragmentMine);
                }
                break;
            }
        }
        transaction.commit();
    }
}
