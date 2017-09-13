package cn.edu.sjtu.se.walknshot.androidclient.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;
import com.github.chrisbanes.photoview.PhotoView;
import cn.edu.sjtu.se.walknshot.androidclient.R;

public class ViewPagerAdapter extends PagerAdapter {

    List<byte[]> imgs;
    Context mContext;

    public ViewPagerAdapter(Context context, List<byte[]> imgs) {
        this.mContext = context;
        this.imgs = imgs;
    }

    @Override
    public int getCount() { // 获得size
        return imgs.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        byte[] imgUrl = imgs.get(position);
        LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.my_view_imgs_browse, null);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.photo_view);
        photoView.setTag(imgUrl);
        //      ImageLoaderUtil.getInstance().displayListItemImage(imgs.get(position), img);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgUrl, 0, imgUrl.length);
        photoView.setImageBitmap(bitmap);
        ((ViewPager) container).addView(view);

        return view;

    }
}
