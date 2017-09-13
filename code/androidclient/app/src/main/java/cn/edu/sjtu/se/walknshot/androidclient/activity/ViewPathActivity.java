package cn.edu.sjtu.se.walknshot.androidclient.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import cn.edu.sjtu.se.walknshot.androidclient.R;
import cn.edu.sjtu.se.walknshot.androidclient.util.GlobalVar;
import cn.edu.sjtu.se.walknshot.androidclient.util.MyToast;

public class ViewPathActivity extends MyAppCompatActivity implements
        SeekBar.OnSeekBarChangeListener,
        OnMapReadyCallback,
        View.OnClickListener {

    // City locations for mutable polyline.

    private static final int MAX_WIDTH_PX = 100;
    private static final int MAX_HUE_DEGREES = 360;
    private static final int MAX_ALPHA = 255;

    private Polyline mMutablePolyline;
    private SeekBar mHueBar;
    private SeekBar mAlphaBar;
    private SeekBar mWidthBar;
    private ImageView mBtnSave;
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

        mBtnSave = (ImageView) findViewById(R.id.view_path_btn_save);
        mBtnSave.setOnClickListener(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // start marker
        map.addMarker(new MarkerOptions()
                .position(GlobalVar.mSpots.get(0))
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
                .geodesic(true));
        mMutablePolyline.setPoints(GlobalVar.mSpots);
        mHueBar.setOnSeekBarChangeListener(this);
        mAlphaBar.setOnSeekBarChangeListener(this);
        mWidthBar.setOnSeekBarChangeListener(this);

        Iterator iter = GlobalVar.mPhotos.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap((Bitmap) entry.getValue()))
                    .position((LatLng) entry.getKey()));
        }
        // Move the map so that it is centered on the mutable polyline.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(GlobalVar.mSpots.get(0), 15));
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
        if (v.getId() == R.id.view_path_btn_save) {

            final ViewPathActivity This = this;
            GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                public void onSnapshotReady(final Bitmap snapshot) {
                    // TODO Auto-generated method stub
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.my_view_snapshot,
                            (ViewGroup) findViewById(R.id.my_view_snapshot));
                    ImageView imageView = layout.findViewById(R.id.snapshot_preview);
                    imageView.setImageBitmap(snapshot);
                    new AlertDialog.Builder(This)
                            .setTitle(R.string.snapshot_preview)
                            .setView(layout)
                            .setPositiveButton(R.string.reply_ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            snapshot.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0-100)压缩文件
                                            //此处可以把Bitmap保存到sd卡中
                                            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/walknshot";
                                            File localFile = new File(filePath);
                                            if (!localFile.exists()) {
                                                boolean b = localFile.mkdir();
                                                if (!b) {
                                                    MyToast.makeText(getApplicationContext(), "创建文件夹失败", MyToast.LENGTH_SHORT).show();
                                                }
                                            }
                                            String mFilename = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.US)) + ".jpg";
                                            File picture = new File(filePath + "/" + mFilename);
                                            try {
                                                FileOutputStream out = new FileOutputStream(picture);
                                                out.write(stream.toByteArray());
                                                out.flush();
                                                out.close();
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                MediaStore.Images.Media.insertImage(getContentResolver(), picture.getAbsolutePath(), "title", "description");
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(picture)));
                                            MyToast.makeText(getApplicationContext(), R.string.save_success, MyToast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                            .setNegativeButton(R.string.reply_cancel, null)
                            .show();
                }
            };

            mMap.snapshot(callback);
        }
    }
}
