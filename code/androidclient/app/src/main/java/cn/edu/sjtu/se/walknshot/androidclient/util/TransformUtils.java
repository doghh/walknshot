package cn.edu.sjtu.se.walknshot.androidclient.util;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import cn.edu.sjtu.se.walknshot.androidclient.R;

public class TransformUtils {

    /**
     * Demonstrates converting a {@link Drawable} to a {@link BitmapDescriptor},
     * for use as a marker icon.
     */
    public static BitmapDescriptor vectorToBitmap(Resources resources, @DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(resources, id, null);
        Drawable vectorDrawableBg = ResourcesCompat.getDrawable(resources, R.drawable.icon_dot_white, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawableBg.getIntrinsicWidth(),
                vectorDrawableBg.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawableBg.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawableBg.draw(canvas);
        vectorDrawable.setBounds(6, 6, canvas.getWidth() - 6, canvas.getHeight() - 6);
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static LatLng transformFromWGSToGCJ(double wgLat, double wgLng) {

        if (!isInChina(wgLng, wgLat)) {
            return new LatLng(wgLat, wgLng);
        }

        // const PI
        double PI = 3.14159265358979324;
        // Krasovsky 1940
        //
        // a = 6378245.0, 1/f = 298.3
        // b = a * (1 - f)
        // ee = (a^2 - b^2) / a^2;
        double a = 6378245.0;
        double ee = 0.00669342162296594323;

        double dLat = transformLat(wgLng - 105.0, wgLat - 35.0, PI);
        double dLng = transformLon(wgLng - 105.0, wgLat - 35.0, PI);
        double radLat = wgLat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
        dLng = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
        return new LatLng(wgLat + dLat, wgLng + dLng);
    }

    private static boolean isInChina(double lng, double lat) {
        double[][] region = new double[6][4];
        region[0] = Rectangle(79.446200, 49.220400, 96.330000, 42.889900);
        region[1] = Rectangle(109.687200, 54.141500, 135.000200, 39.374200);
        region[2] = Rectangle(73.124600, 42.889900, 124.143255, 29.529700);
        region[3] = Rectangle(82.968400, 29.529700, 97.035200, 26.718600);
        region[4] = Rectangle(97.025300, 29.529700, 124.367395, 20.414096);
        region[5] = Rectangle(107.975793, 20.414096, 111.744104, 17.871542);

        double[][] exclude = new double[7][4];
        exclude[0] = Rectangle(119.921265, 25.398623, 122.497559, 21.785006);
        exclude[1] = Rectangle(101.865200, 22.284000, 106.665000, 20.098800);
        exclude[2] = Rectangle(106.452500, 21.542200, 108.051000, 20.487800);
        exclude[3] = Rectangle(109.032300, 55.817500, 119.127000, 50.325700);
        exclude[4] = Rectangle(127.456800, 55.817500, 137.022700, 49.557400);
        exclude[5] = Rectangle(131.266200, 44.892200, 137.022700, 42.569200);
        exclude[6] = Rectangle(113.837108, 22.44151, 114.408397, 22.167709);

        for (int i = 0; i < region.length; i++) {
            if (isInRect(region[i], lng, lat)) {
                for (int j = 0; j < exclude.length; j++) {
                    if (isInRect(exclude[j], lng, lat)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static double[] Rectangle(double lng1, double lat1, double lng2, double lat2) {
        double[] res = new double[4];
        res[0] = Math.min(lng1, lng2);
        res[1] = Math.max(lat1, lat2);
        res[2] = Math.max(lng1, lng2);
        res[3] = Math.min(lat1, lat2);
        return res;
    }

    private static boolean isInRect(double[] rect, double lon, double lat) {
        return rect[0] <= lon && rect[2] >= lon && rect[1] >= lat && rect[3] <= lat;
    }

    private static double transformLat(double x, double y, double PI) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y, double PI) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

}
