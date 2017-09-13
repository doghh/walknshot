package cn.edu.sjtu.se.walknshot.androidclient.util;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalVar {
    public static HashMap<LatLng, Bitmap> mPhotos = new HashMap<>();
    public static List<LatLng> mSpots = new ArrayList<>();
}
