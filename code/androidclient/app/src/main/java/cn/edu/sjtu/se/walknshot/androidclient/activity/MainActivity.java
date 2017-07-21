package cn.edu.sjtu.se.walknshot.androidclient.activity;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import cn.edu.sjtu.se.walknshot.androidclient.fragment.DiscoveryFragment;
import cn.edu.sjtu.se.walknshot.androidclient.fragment.MapPageFragment;
import cn.edu.sjtu.se.walknshot.androidclient.fragment.PersonalCenterFragment;
import cn.edu.sjtu.se.walknshot.androidclient.R;

public class MainActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MapPageFragment mapPageFragment;
    private DiscoveryFragment discoveryFragment;
    private PersonalCenterFragment personalCenterFragment;

    private int currentId;

    private LinearLayout mMapPage, mDiscovery, mPersonalCenter;

    private static final int PHOTO_GRAPH = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    public GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapPage = (LinearLayout) findViewById(R.id.tab_map_page);
        mDiscovery = (LinearLayout) findViewById(R.id.tab_dicovery);
        mPersonalCenter = (LinearLayout) findViewById(R.id.tab_personal_center);

        //默认首页为MapPage
        currentId = R.id.tab_map_page;
        mMapPage.setSelected(true);
        mapPageFragment = new MapPageFragment();
        getFragmentManager().beginTransaction().add(R.id.main_content, mapPageFragment).commit();

        //为3个tab设置监听
        mMapPage.setOnClickListener(tabClickListener);
        mDiscovery.setOnClickListener(tabClickListener);
        mPersonalCenter.setOnClickListener(tabClickListener);
    }

    private View.OnClickListener tabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //如果当前选中跟上次选中的一样,不需要处理
            if (v.getId() != currentId) {
                changeSelect(v.getId());    //改变图标跟文字颜色的选中
                changeFragment(v.getId());  //切换fragment
                currentId = v.getId();      //设置选中id
            }
        }
    };

    private void changeFragment(int resId) {
        //开启一个Fragment事务
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //隐藏所有fragment
        hideFragments(transaction);

        switch (resId) {
            case R.id.tab_map_page: {
                if (mapPageFragment == null) {
                    mapPageFragment = new MapPageFragment();
                    transaction.add(R.id.main_content, mapPageFragment);
                } else {
                    transaction.show(mapPageFragment);
                }
                break;
            }
            case R.id.tab_dicovery: {
                if (discoveryFragment == null) {
                    discoveryFragment = new DiscoveryFragment();
                    transaction.add(R.id.main_content, discoveryFragment);
                } else {
                    transaction.show(discoveryFragment);
                }
                break;
            }
            case R.id.tab_personal_center: {
                if (personalCenterFragment == null) {
                    personalCenterFragment = new PersonalCenterFragment();
                    transaction.add(R.id.main_content, personalCenterFragment);
                } else {
                    transaction.show(personalCenterFragment);
                }
                break;
            }
        }
        transaction.commit();
    }

    /**
     * 隐藏所有存在的fragment
     *
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (mapPageFragment != null)//不为空才隐藏,如果不判断第一次会有空指针异常
            transaction.hide(mapPageFragment);
        if (discoveryFragment != null)
            transaction.hide(discoveryFragment);
        if (personalCenterFragment != null)
            transaction.hide(personalCenterFragment);
    }

    /**
     * 改变选中样式
     *
     * @param resId
     */
    private void changeSelect(int resId) {
        mMapPage.setSelected(false);
        mDiscovery.setSelected(false);
        mPersonalCenter.setSelected(false);

        switch (resId) {
            case R.id.tab_map_page:
                mMapPage.setSelected(true);
                break;
            case R.id.tab_dicovery:
                mDiscovery.setSelected(true);
                break;
            case R.id.tab_personal_center:
                mPersonalCenter.setSelected(true);
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mapPageFragment.setLastKnownLocation(location);
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Build the map.
        mapPageFragment.buildGoogleMap();
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mapPageFragment.initGoogleMap();
                }
            } else {
                mapPageFragment.mPermissionDenied = true;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_GRAPH:
                if (resultCode == RESULT_OK) {
                    byte[] bis = data.getByteArrayExtra("returnbitmap");
                    double lat = data.getExtras().getDouble("latitude");
                    double lng = data.getExtras().getDouble("longitude");
                    mapPageFragment.addPhoto(bis, lat, lng);
                }
                break;
            default:
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void buildGoogleApiClient() {
        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */,
                            this /* OnConnectionFailedListener */)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
            mGoogleApiClient.connect();
        }
    }
}
