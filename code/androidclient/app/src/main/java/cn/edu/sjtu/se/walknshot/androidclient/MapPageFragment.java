package cn.edu.sjtu.se.walknshot.androidclient;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

public class MapPageFragment extends android.app.Fragment {

    private MapView mMap;
    private GoogleMap googleMap;
    //   private Bundle args = null;
//    private String LoginID = null;
    //   private SharedPreferences preferences;
    //   private ArrayList<SiteMap> mSiteMaps;
    //  private AlertDialog.Builder builder;
//    private static final String LOG_TAG = SiteMapFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map,
                container, false);
        mMap = (MapView) view.findViewById(R.id.map);
        mMap.onCreate(savedInstanceState);
        mMap.onResume();
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        int errorCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this.getActivity());

        if (ConnectionResult.SUCCESS != errorCode) {
            GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), 0).show();
        } else {
            googleMap = mMap.getMap();
            if (googleMap != null) {
                initVolley();
                LatLng india = new LatLng(23, 77);
                googleMap.setMyLocationEnabled(true);
                googleMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(india, 5));
            }
        }
*/
        return view;
    }

}
