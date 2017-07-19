package cn.edu.sjtu.se.walknshot.androidclient.util;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sjtu.se.walknshot.androidclient.R;
import cn.edu.sjtu.se.walknshot.androidclient.model.Item;

public class PostAdapter extends BaseAdapter {

    private static final int LIKE = 0;
    private static final int COMMENT = 1;
    private static final int MORE = 2;

    private ArrayList<Item> list;
    private LayoutInflater mInflater;
    private Callback mCallback;

    /**
     * 自定义接口，用于回调按钮点击事件到Activity
     */
    public interface Callback {
        public void click(View v, int type);
    }

    public PostAdapter(Context context, Callback callback) {
        list = new ArrayList<Item>();
        mCallback = callback;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Item getItem(int position) {
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
            switch (type) {
                case Item.TITLE:
                    convertView = mInflater.inflate(R.layout.item_title, parent, false);
                    viewHolder.mTitle = (TextView) convertView.findViewById(R.id.title);
                    break;
                case Item.BODY:
                    convertView = mInflater.inflate(R.layout.item_body, parent, false);
                    viewHolder.mBody = (TextView) convertView.findViewById(R.id.body);
                    break;
                case Item.IMAGE:
                    convertView = mInflater.inflate(R.layout.item_image, parent, false);
                    viewHolder.mImage = (ImageView) convertView.findViewById(R.id.image);
                    break;
                case Item.BOTTOM:
                    convertView = mInflater.inflate(R.layout.item_bottom, parent, false);
                    viewHolder.mBtnLike =  (ImageButton) convertView.findViewById(R.id.discovery_btn_like);
                    viewHolder.mBtnComment = (ImageButton) convertView.findViewById(R.id.discovery_btn_comment);
                    viewHolder.mBtnMore = (ImageButton) convertView.findViewById(R.id.discovery_btn_more);
                    break;
            }
            if (null != convertView) {
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Item item = list.get(position);
        switch (type) {
            case Item.TITLE:
                viewHolder.mTitle.setText(item.getText());
                break;
            case Item.BODY:
                viewHolder.mBody.setText(item.getText());
                break;
            case Item.IMAGE:
                viewHolder.mImage.setImageBitmap(item.getCover());
                break;
            case Item.BOTTOM:
                ImageButton btnLike = viewHolder.mBtnLike;
                ImageButton btnComment = viewHolder.mBtnComment;
                ImageButton btnMore = viewHolder.mBtnMore;
                btnLike.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setSelected(true);
                        mCallback.click(view, LIKE);
                    }
                });
                btnComment.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.click(view, COMMENT);
                    }
                });
                btnMore.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.click(view, MORE);
                    }
                });
                btnLike.setTag(position);
                btnComment.setTag(position);
                btnMore.setTag(position);
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getStyleType();
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    public void add(Item item) {
        list.add(item);
    }

    public void addAll(List<Item> items) {
        list.addAll(items);
    }

    static class ViewHolder {
        TextView mTitle;
        TextView mBody;
        ImageView mImage;
        ImageButton mBtnLike;
        ImageButton mBtnComment;
        ImageButton mBtnMore;
    }
}
