package cn.edu.sjtu.se.walknshot.androidclient;

import android.Manifest;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.List;

public class MapPageFragment extends Fragment {

    private static final String TAG = MapPageFragment.class.getSimpleName();
    private MapView mMap;
    private GoogleMap mGoogleMap;
    private CameraPosition mCameraPosition;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(39.98871, 116.43234);
    private static final int DEFAULT_ZOOM = 15;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final long POLLING_FREQ = 1000 * 30;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
    private static final float ZINDEX_LEVEL_TOP = 4;
    private static final float ZINDEX_LEVEL_3 = 3;
    private static final float ZINDEX_LEVEL_2 = 2;
    private static final float ZINDEX_LEVEL_1 = 1;
    private static final float ZINDEX_LEVEL_BOTTOM = 0;

    public boolean mPermissionDenied = false;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private Marker mMyLocationMarker;
    private Circle mMyLocationCircle;

    // The path
    public boolean mRecordBegun = false;
    private List<LatLng> mSpots = new ArrayList<>();
    private Polyline mPath;

    private ImageButton mBtnMyLocation, mBtnStartRecordPath, mBtnEndRecordPath, mBtnGoPhotograph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_page,
                container, false);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // init views
        mMap = (MapView) rootView.findViewById(R.id.map);
        mMap.onCreate(savedInstanceState);
        initButtonView(rootView);

        return rootView;
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
        mMap.onResume();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getActivity().getFragmentManager(), "dialog");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mGoogleMap != null) {
            mCameraPosition = mGoogleMap.getCameraPosition();
            outState.putParcelable(KEY_CAMERA_POSITION, mCameraPosition);
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
        }
        super.onSaveInstanceState(outState);
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

        // init custom button
        mBtnMyLocation = (ImageButton) view.findViewById(R.id.main_btn_my_location);
        mBtnStartRecordPath = (ImageButton) view.findViewById(R.id.main_btn_record_path_begin);
        mBtnEndRecordPath = (ImageButton) view.findViewById(R.id.main_btn_record_path_end);
        mBtnGoPhotograph = (ImageButton) view.findViewById((R.id.main_btn_photograph));
        // 刚开始隐藏结束按钮
        mBtnEndRecordPath.setVisibility(View.GONE);
        // 设置监听
        mBtnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLastKnownLocation != null) {
                    if (mGoogleMap.getCameraPosition().zoom >= DEFAULT_ZOOM) {
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude())));
                    } else {
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    }
                    mCameraPosition = mGoogleMap.getCameraPosition();
                }
            }
        });
        mBtnStartRecordPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.hint_start_record_path)
                        .setPositiveButton(R.string.reply_go,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // start something
                                        satrtRecordPath();
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
                                        endRecordPath();
                                    }
                                })
                        .setNegativeButton(R.string.reply_cancel, null)
                        .create()
                        .show();
            }
        });
        mBtnGoPhotograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddPicturesActivity.class);
                intent.putExtra("latitude", mLastKnownLocation.getLatitude());
                intent.putExtra("longitude", mLastKnownLocation.getLongitude());
                startActivity(intent);
            }
        });
    }

    public void initGoogleMap() {
        // Enables the My Location layer if the fine location permission has been granted.
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */

        // 谷歌自带的蓝标
        // mGoogleMap.setMyLocationEnabled(true);

        if (mMyLocationMarker == null) {
            mMyLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mDefaultLocation.latitude, mDefaultLocation.longitude))
                    .icon(TransformUtils.vectorToBitmap(getResources(), R.drawable.icon_dot, Color.parseColor("#2979FF")))
                    .anchor(0.5f, 0.5f)
                    .zIndex(ZINDEX_LEVEL_TOP)
                    .flat(true));
        }

        if (mMyLocationCircle == null) {
            mMyLocationCircle = mGoogleMap.addCircle(new CircleOptions()
                    .center(new LatLng(mDefaultLocation.latitude, mDefaultLocation.longitude))
                    .radius(20)
                    .fillColor(Color.parseColor("#3C2979FF"))
                    .strokeColor(Color.parseColor("#B42979FF"))
                    .strokeWidth(3f)
                    .zIndex(ZINDEX_LEVEL_3));
        }

        mLastKnownLocation = LocationServices.FusedLocationApi
                .getLastLocation(((MainActivity) getActivity()).getGoogleApiClient());
        correctLocation();


        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
            mCameraPosition = mGoogleMap.getCameraPosition();
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mCameraPosition = mGoogleMap.getCameraPosition();
        }

    }

    public void buildGoogleMap() {
        // get and init GoogleMap googleMap
        mMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    LocationRequest mLocationRequest = LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(POLLING_FREQ)
                            .setFastestInterval(FASTEST_UPDATE_FREQ);
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                            ((MainActivity) getActivity()).getGoogleApiClient(),
                            mLocationRequest,
                            (MainActivity) getActivity());
                    initGoogleMap();
                }
            }
        });
    }

    public void setLastKnownLocation(Location location) {
        mLastKnownLocation = location;
        correctLocation();
    }

    private void correctLocation() {
        if (mLastKnownLocation != null) {
            LatLng latLng = TransformUtils.transformFromWGSToGCJ
                    (mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            mLastKnownLocation.setLatitude(latLng.latitude);
            mLastKnownLocation.setLongitude(latLng.longitude);
            //change blue dot and its circle
            if (mMyLocationMarker != null) {
                mMyLocationMarker.setPosition(latLng);
            }
            if (mMyLocationCircle != null) {
                mMyLocationCircle.setCenter(latLng);
            }
        }
    }

    public void satrtRecordPath() {
        if (mLastKnownLocation != null) {

            // change button
            mBtnStartRecordPath.setVisibility(View.GONE);
            mBtnEndRecordPath.setVisibility(View.VISIBLE);

            // change flag
            mRecordBegun = true;

            // init spots
            LatLng startPoint = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            mSpots.clear();
            mSpots.add(startPoint);

            mPath = mGoogleMap.addPolyline(new PolylineOptions()
                    .width(25)
                    .color(Color.CYAN)
                    .geodesic(true));

            // start draw path
            final MapPageFragment This = this;
            new Thread(new Runnable() {//创建一个线程内部类
                @Override
                public void run() {
                    This.updatePath(5000);
                }
            }).start();

            // log
            MyToast.makeText(getActivity().getApplicationContext(), R.string.log_start_record_path, Toast.LENGTH_SHORT).show();
        } else {
            MyToast.makeText(getActivity().getApplicationContext(), R.string.error_location_loss, Toast.LENGTH_SHORT).show();
        }
    }

    public void endRecordPath() {
        mRecordBegun = false;
        // 清除polyline
        mPath.remove();
        // log
        MyToast.makeText(getActivity().getApplicationContext(), R.string.log_end_record_path, Toast.LENGTH_SHORT).show();
    }

    public void updatePath(long sec) {
        while (mRecordBegun) {
            LatLng newSpot = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            //LatLng newSpot = new LatLng
            //        (mSpots.get(mSpots.size() - 1).latitude + 0.1, mSpots.get(mSpots.size() - 1).longitude + 0.1);
            if (!newSpot.equals(mSpots.get(mSpots.size() - 1))) {
                mSpots.add(newSpot);
                // send message to update UI
                Message msgMessage = new Message();
                msgMessage.arg1 = 1;
                mHandler.sendMessage(msgMessage);
            }
            // wait for 5s
            try {
                Thread.sleep(sec);
            } catch (InterruptedException e) {
            }
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 1:
                    if (mSpots.size() > 0) {
                        mPath.setPoints(mSpots);
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
