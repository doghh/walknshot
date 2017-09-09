package cn.edu.sjtu.se.walknshot.androidclient.activity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import cn.edu.sjtu.se.walknshot.androidclient.R;

public class ViewPathActivity extends MyAppCompatActivity implements
        SeekBar.OnSeekBarChangeListener,
        OnMapReadyCallback,
        View.OnClickListener {

    // City locations for mutable polyline.
    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);
    private static final LatLng DARWIN = new LatLng(-12.4258647, 130.7932231);
    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
    private static final LatLng PERTH = new LatLng(-31.95285, 115.85734);

    private static final int MAX_WIDTH_PX = 100;
    private static final int MAX_HUE_DEGREES = 360;
    private static final int MAX_ALPHA = 255;

    private Polyline mMutablePolyline;
    private SeekBar mHueBar;
    private SeekBar mAlphaBar;
    private SeekBar mWidthBar;
    private ImageView mBtnShare;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_path);

        mHueBar = (SeekBar) findViewById(R.id.hueSeekBar);
        mHueBar.setMax(MAX_HUE_DEGREES);
        mHueBar.setProgress(0);

        mAlphaBar = (SeekBar) findViewById(R.id.alphaSeekBar);
        mAlphaBar.setMax(MAX_ALPHA);
        mAlphaBar.setProgress(MAX_ALPHA);

        mWidthBar = (SeekBar) findViewById(R.id.widthSeekBar);
        mWidthBar.setMax(MAX_WIDTH_PX);
        mWidthBar.setProgress(MAX_WIDTH_PX / 10);

        mBtnShare = (ImageView) findViewById(R.id.view_path_btn_share);
        mBtnShare.setOnClickListener(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // start marker
        map.addMarker(new MarkerOptions()
                .position(MELBOURNE)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        // A simple polyline across Australia. This polyline will be mutable.
        int color = Color.HSVToColor(
                mAlphaBar.getProgress(), new float[]{mHueBar.getProgress(), 1, 1});
        mMutablePolyline = map.addPolyline(new PolylineOptions()
                .startCap(new RoundCap())
                .endCap(new RoundCap())
                .jointType(JointType.ROUND)
                .color(color)
                .width(mWidthBar.getProgress())
                .geodesic(true)
                .add(MELBOURNE, ADELAIDE, PERTH, DARWIN));
        mHueBar.setOnSeekBarChangeListener(this);
        mAlphaBar.setOnSeekBarChangeListener(this);
        mWidthBar.setOnSeekBarChangeListener(this);

        // Move the map so that it is centered on the mutable polyline.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(MELBOURNE, 3));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mMutablePolyline == null) {
            return;
        }

        if (seekBar == mHueBar) {
            mMutablePolyline.setColor(Color.HSVToColor(
                    Color.alpha(mMutablePolyline.getColor()), new float[]{progress, 1, 1}));
        } else if (seekBar == mAlphaBar) {
            float[] prevHSV = new float[3];
            Color.colorToHSV(mMutablePolyline.getColor(), prevHSV);
            mMutablePolyline.setColor(Color.HSVToColor(progress, prevHSV));
        } else if (seekBar == mWidthBar) {
            mMutablePolyline.setWidth(progress);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.view_path_btn_share) {

            final ViewPathActivity This = this;
            GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                public void onSnapshotReady(Bitmap snapshot) {
                    // TODO Auto-generated method stub
                    Bitmap bitmap = snapshot;
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.my_view_snapshot,
                            (ViewGroup) findViewById(R.id.my_view_snapshot));
                    ImageView imageView = layout.findViewById(R.id.snapshot_preview);
                    imageView.setImageBitmap(bitmap);
                    new AlertDialog.Builder(This)
                            .setTitle(R.string.snapshot_preview)
                            .setView(layout)
                            .setPositiveButton(R.string.reply_ok, null)
                            .setNegativeButton(R.string.reply_cancel, null)
                            .show();
                }
            };

            mMap.snapshot(callback);
        }
    }
}
