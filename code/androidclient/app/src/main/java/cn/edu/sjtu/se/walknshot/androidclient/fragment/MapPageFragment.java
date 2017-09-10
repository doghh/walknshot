package cn.edu.sjtu.se.walknshot.androidclient.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.ThumbnailUtils;
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

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.sjtu.se.walknshot.androidclient.R;
import cn.edu.sjtu.se.walknshot.androidclient.activity.AddPicturesActivity;
import cn.edu.sjtu.se.walknshot.androidclient.activity.MainActivity;
import cn.edu.sjtu.se.walknshot.androidclient.util.MyToast;
import cn.edu.sjtu.se.walknshot.androidclient.util.PermissionUtils;
import cn.edu.sjtu.se.walknshot.androidclient.util.TransformUtils;

import cn.edu.sjtu.se.walknshot.apiclient.*;

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
    private static final int PHOTO_GRAPH = 1;
    private static final long POLLING_FREQ = 1000 * 5;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
    private static final long SMALLEST_DISPLACEMENT = 5;
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
    private Marker mStartMarker;
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
        ((MainActivity) getActivity()).buildGoogleApiClient();
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
                                        startRecordPath();
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
                if (mLastKnownLocation != null) {
                    Intent intent = new Intent(getActivity(), AddPicturesActivity.class);
                    if (mRecordBegun) {
                        LatLng currentSpot = mSpots.get(mSpots.size() - 1);
                        intent.putExtra("latitude", currentSpot.latitude)
                                .putExtra("longitude", currentSpot.longitude)
                                .putExtra("source", "mapPage");
                    } else {
                        intent.putExtra("latitude", mLastKnownLocation.getLatitude())
                                .putExtra("longitude", mLastKnownLocation.getLongitude())
                                .putExtra("source", "mapPageNotBegun");
                    }
                    getActivity().startActivityForResult(intent, PHOTO_GRAPH);
                }
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
        if (mMyLocationMarker == null) {
            LocationRequest mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setSmallestDisplacement(SMALLEST_DISPLACEMENT)
                    .setInterval(POLLING_FREQ)
                    .setFastestInterval(FASTEST_UPDATE_FREQ);
            LocationServices.FusedLocationApi.requestLocationUpdates
                    (((MainActivity) getActivity()).getGoogleApiClient(), mLocationRequest, (MainActivity) getActivity());
        }

        // 谷歌自带的蓝标
        // mGoogleMap.setMyLocationEnabled(true);

        if (mMyLocationMarker == null) {
            addBlueDot(mDefaultLocation);
        }

        setLastKnownLocation(LocationServices.FusedLocationApi
                .getLastLocation(((MainActivity) getActivity()).getGoogleApiClient()));

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
            // change path if in walking
            if (mRecordBegun) {

            }
        }
    }

    public void startRecordPath() {
        if (mLastKnownLocation != null) {

            // change button
            mBtnStartRecordPath.setVisibility(View.GONE);
            mBtnEndRecordPath.setVisibility(View.VISIBLE);

            // change flag
            mRecordBegun = true;

            // init spots
            LatLng startPoint = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            mSpots.clear();
            mGoogleMap.clear();
            addBlueDot(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
            mSpots.add(startPoint);
            addSpotToServer(startPoint.latitude, startPoint.longitude);

            // start marker
            mStartMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(startPoint)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

            mPath = mGoogleMap.addPolyline(new PolylineOptions()
                    .startCap(new RoundCap())
                    .endCap(new RoundCap())
                    .jointType(JointType.ROUND)
                    .color(Color.CYAN).width(25)
                    .geodesic(true)
                    .zIndex(ZINDEX_LEVEL_2));

            // start draw path
            final MapPageFragment This = this;
            new Thread(new Runnable() {//创建一个线程内部类
                @Override
                public void run() {
                    This.updatePath(5000);
                }
            }).start();

            // log
            //MyToast.makeText(getActivity().getApplicationContext(), R.string.log_start_record_path, MyToast.LENGTH_SHORT).show();
        } else {
            MyToast.makeText(getActivity().getApplicationContext(), R.string.error_location_loss, MyToast.LENGTH_SHORT).show();
        }
    }

    public void endRecordPath() {
        mRecordBegun = false;
        // 清除polyline
        mPath = null;
        mStartMarker = null;
        mGoogleMap.clear();
        addBlueDot(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
        // log
        MyToast.makeText(getActivity().getApplicationContext(), R.string.log_end_record_path, MyToast.LENGTH_SHORT).show();
    }

    public void updatePath(long sec) {
        while (mRecordBegun) {
            LatLng newSpot = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            //LatLng newSpot = new LatLng
            //        (mSpots.get(mSpots.size() - 1).latitude + 0.1, mSpots.get(mSpots.size() - 1).longitude + 0.1);
            if (!newSpot.equals(mSpots.get(mSpots.size() - 1))) {
                mSpots.add(newSpot);
                // send new spot to server
                addSpotToServer(newSpot.latitude, newSpot.longitude);
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
                    if (mSpots.size() > 0 && mPath != null) {
                        mPath.setPoints(mSpots);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void addPhoto(byte[] bis, double lat, double lng) {
        final ClientImpl client = ClientImpl.getInstance();
        client.uploadPicture(new Callback() {
            @Override
            public void onNetworkFailure(IOException e) {
                MyToast.makeText(getActivity().getApplicationContext(), R.string.error_network_fail, MyToast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Object arg) {
                MyToast.makeText(getActivity().getApplicationContext(), R.string.error_upload_fail, MyToast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object arg) {
                MyToast.makeText(getActivity().getApplicationContext(), "图片上传成功", MyToast.LENGTH_SHORT).show();
            }
        }, bis);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
        Bitmap extractBitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        mGoogleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(extractBitmap))
                .position(new LatLng(lat, lng))
                .zIndex(ZINDEX_LEVEL_3));
    }

    private void addBlueDot(LatLng latLng) {
        mMyLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(TransformUtils.vectorToBitmap(getResources(), R.drawable.icon_dot, Color.parseColor("#2979FF")))
                .anchor(0.5f, 0.5f)
                .zIndex(ZINDEX_LEVEL_TOP)
                .flat(true));
        mMyLocationCircle = mGoogleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(20)
                .fillColor(Color.parseColor("#3C2979FF"))
                .strokeColor(Color.parseColor("#B42979FF"))
                .strokeWidth(3f)
                .zIndex(ZINDEX_LEVEL_3));
    }

    private void addSpotToServer(double latitude, double longitude) {
        final ClientImpl client = ClientImpl.getInstance();
        client.addSpot(new Callback() {
            @Override
            public void onNetworkFailure(IOException e) {
                MyToast.makeText(getActivity().getApplicationContext(), R.string.error_network_fail, MyToast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Object arg) {
                MyToast.makeText(getActivity().getApplicationContext(), R.string.error_upload_fail, MyToast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object arg) {
                //MyToast.makeText(getActivity().getApplicationContext(), "addSpot成功", MyToast.LENGTH_SHORT).show();
            }
        }, latitude, longitude);
    }

}
