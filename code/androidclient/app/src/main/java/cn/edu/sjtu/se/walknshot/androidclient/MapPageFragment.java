package cn.edu.sjtu.se.walknshot.androidclient;

import android.Manifest;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapPageFragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MapPageFragment.class.getSimpleName();
    private MapView mMap;
    private GoogleMap mGoogleMap;
    private CameraPosition mCameraPosition;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(39.98871, 116.43234);
    private static final int DEFAULT_ZOOM = 15;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    private ImageButton mBtnStartRecordPath, mBtnEndRecordPath, mBtnGoPhotograph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_page,
                container, false);

        // init views
        initButtonView(rootView);
        mMap = (MapView) rootView.findViewById(R.id.map);
        mMap.onCreate(savedInstanceState);

        // get and init GoogleMap googleMap
        mMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                initGoogleMap();
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMap.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMap.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
        mMap.onResume();
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getFragmentManager(), "dialog");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mGoogleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mGoogleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
        mMap.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMap.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMap.onStop();
    }

    private void initButtonView(View view) {
        mBtnStartRecordPath = (ImageButton) view.findViewById(R.id.main_btn_record_path_begin);
        mBtnEndRecordPath = (ImageButton) view.findViewById(R.id.main_btn_record_path_end);
        mBtnGoPhotograph = (ImageButton) view.findViewById((R.id.main_btn_photograph));
        // 刚开始隐藏结束按钮
        mBtnEndRecordPath.setVisibility(View.GONE);
        // 设置监听
        mBtnStartRecordPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.hint_start_record_path)
                        .setPositiveButton(R.string.reply_go,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mBtnStartRecordPath.setVisibility(View.GONE);
                                        mBtnEndRecordPath.setVisibility(View.VISIBLE);
                                        // start something
                                    }
                                })
                        .setNegativeButton(R.string.reply_cancel, null)
                        .create()
                        .show();
            }
        });
        mBtnEndRecordPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.hint_end_record_path)
                        .setPositiveButton(R.string.reply_ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mBtnEndRecordPath.setVisibility(View.GONE);
                                        mBtnStartRecordPath.setVisibility(View.VISIBLE);
                                        // stop something
                                    }
                                })
                        .setNegativeButton(R.string.reply_cancel, null)
                        .create()
                        .show();
            }
        });
    }

    private void initGoogleMap() {
        mGoogleMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mGoogleMap != null) {
            // Access to the location has been granted to the app.
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        MyToast.makeText(getActivity().getApplicationContext(), "MyLocation button clicked", MyToast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

}
