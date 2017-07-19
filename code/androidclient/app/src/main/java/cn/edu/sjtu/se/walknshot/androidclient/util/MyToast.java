package cn.edu.sjtu.se.walknshot.androidclient.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.edu.sjtu.se.walknshot.androidclient.R;


public class MyToast {

    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;

    Toast toast;
    Context mContext;
    TextView toastTextField;

    public MyToast(Context context) {
        mContext = context;
        toast = new Toast(mContext);
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        // 位置会比原来的Toast偏上一些
        View layout = LayoutInflater.from(context).inflate(R.layout.my_view_toast, null);
        toastTextField = (TextView) layout.findViewById(R.id.toast_text);
        toast.setView(layout);
    }

    public void setDuration(int d) {
        toast.setDuration(d);
    }

    public void setText(CharSequence t) {
        toastTextField.setText(t);
    }

    public void setText(@StringRes int t) {
        toastTextField.setText(t);
    }

    public static MyToast makeText(Context context, CharSequence text, int duration) {
        MyToast mToast = new MyToast(context);
        mToast.setText(text);
        mToast.setDuration(duration);
        return mToast;
    }

    public static MyToast makeText(Context context, @StringRes int text, int duration) {
        MyToast mToast = new MyToast(context);
        mToast.setText(text);
        mToast.setDuration(duration);
        return mToast;
    }

    public void show() {
        toast.show();
    }
}
