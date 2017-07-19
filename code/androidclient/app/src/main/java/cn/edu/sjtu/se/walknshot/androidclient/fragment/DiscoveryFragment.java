package cn.edu.sjtu.se.walknshot.androidclient.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import cn.edu.sjtu.se.walknshot.androidclient.R;
import cn.edu.sjtu.se.walknshot.androidclient.model.Post;
import cn.edu.sjtu.se.walknshot.androidclient.util.MyToast;
import cn.edu.sjtu.se.walknshot.androidclient.util.PostAdapter;
import cn.edu.sjtu.se.walknshot.androidclient.util.TransformUtils;

public class DiscoveryFragment extends Fragment implements
        AdapterView.OnItemClickListener,
        PostAdapter.Callback {

    private static final int LIKE = 0;
    private static final int COMMENT = 1;
    private static final int MORE = 2;

    private ListView mListView;
    ArrayList<Post> mPosts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discovery, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mListView.setDividerHeight(0);
        mListView.setPadding(10, 10, 10, 10);
        Bitmap bbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_welcome);
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(bbitmap, 1000, 1000);
        mPosts.add(new Post(1, "title1", "This is News 1.", bitmap));
        mPosts.add(new Post(2, "title2", "This is News 2.", bitmap));
        mPosts.add(new Post(3, "title3", "This is News 3.", bitmap));
        mPosts.add(new Post(4, "title4", "This is News 4.", bitmap));
        PostAdapter adapter = new PostAdapter(getActivity(), this);
        adapter.addAll(TransformUtils.postToItems(mPosts));
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);

        return rootView;
    }

    /**
     * 响应ListView中item的点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
        MyToast.makeText(getActivity().getApplicationContext(), "listview的item被点击了！，点击的位置是-->" + position,
                MyToast.LENGTH_SHORT).show();
    }

    /**
     * 接口方法，响应ListView按钮点击事件
     */
    @Override
    public void click(View v, int type) {
        switch (type) {
            case LIKE: {
                MyToast.makeText(
                        getActivity().getApplicationContext(),
                        "listview的内部的按钮被点击了！，位置是-->" + (Integer) v.getTag() + ",内容是-->"
                                + mPosts.get(((Integer) v.getTag()) / 4).getBody(),
                        MyToast.LENGTH_SHORT).show();
                break;
            }
            case COMMENT: {
                // open comment page
                // ID = posts.get(((Integer) v.getTag()) / 4).getPostId();
                break;
            }
            case MORE:{
                // open view photo page
                // ID = posts.get(((Integer) v.getTag()) / 4).getPostId();
                break;
            }
            default:
                break;
        }
    }
}
