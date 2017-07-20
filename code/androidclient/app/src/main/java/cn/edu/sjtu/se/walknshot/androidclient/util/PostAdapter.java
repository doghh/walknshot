package cn.edu.sjtu.se.walknshot.androidclient.util;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sjtu.se.walknshot.androidclient.R;
import cn.edu.sjtu.se.walknshot.androidclient.model.Post;

public class PostAdapter extends BaseAdapter {

    private static final int LIKE = 0;
    private static final int COMMENT = 1;
    private static final int MORE = 2;

    private ArrayList<Post> list;
    private LayoutInflater mInflater;
    private Callback mCallback;

    /**
     * 自定义接口，用于回调按钮点击事件到Activity
     */
    public interface Callback {
        public void click(View v, int type);
    }

    public PostAdapter(Context context, Callback callback) {
        list = new ArrayList<Post>();
        mCallback = callback;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Post getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        int type = getItemViewType(position);
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_post, parent, false);
            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.title);
            viewHolder.mBody = (TextView) convertView.findViewById(R.id.body);
            viewHolder.mImage = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.mBtnLike = (ImageView) convertView.findViewById(R.id.discovery_btn_like);
            viewHolder.mBtnComment = (ImageView) convertView.findViewById(R.id.discovery_btn_comment);
            viewHolder.mBtnMore = (ImageView) convertView.findViewById(R.id.discovery_btn_more);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Post item = list.get(position);
        viewHolder.mTitle.setText(item.getTitle());
        viewHolder.mBody.setText(item.getBody());
        viewHolder.mImage.setImageBitmap(item.getCover());
        viewHolder.mBtnLike.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.click(view, LIKE);
            }
        });
        viewHolder.mBtnComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.click(view, COMMENT);
            }
        });
        viewHolder.mBtnMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.click(view, MORE);
            }
        });
        viewHolder.mBtnLike.setTag(position);
        viewHolder.mBtnComment.setTag(position);
        viewHolder.mBtnMore.setTag(position);
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    public void add(Post item) {
        list.add(item);
    }

    public void addAll(List<Post> items) {
        list.addAll(items);
    }

    static class ViewHolder {
        TextView mTitle;
        TextView mBody;
        ImageView mImage;
        ImageView mBtnLike;
        ImageView mBtnComment;
        ImageView mBtnMore;
    }
}
