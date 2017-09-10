package cn.edu.sjtu.se.walknshot.androidclient.activity;

import android.Manifest;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cn.edu.sjtu.se.walknshot.androidclient.util.MyToast;
import cn.edu.sjtu.se.walknshot.androidclient.util.NoFastClickUtil;
import cn.edu.sjtu.se.walknshot.androidclient.util.PermissionUtils;
import cn.edu.sjtu.se.walknshot.androidclient.R;
import cn.edu.sjtu.se.walknshot.apiclient.*;

public class AddPicturesActivity extends MyAppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GridView mGridView;                   //网格显示缩略图
    private Button mBtnSubmitPic;
    private ImageButton mBtnTakePhoto;
    private ImageButton mBtnOpenAlbum;
    private Bitmap bmp;                               //导入临时图片
    private ArrayList<HashMap<String, Object>> imageItem;
    private ArrayList<Uri> imageUris;
    private SimpleAdapter simpleAdapter;     //适配器

    private double latitude = 0.0;
    private double longitude = 0.0;

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 2;
    private static final int NONE = 0;
    private static final int PHOTO_GRAPH = 1;// 拍照
    private static final int PHOTO_ZOOM = 2; // 缩放
    private static final int PHOTO_RESULT = 3;// 结果
    private static final int PHOTO_BEAUTIFY = 4;// 美化
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private boolean mPermissionDenied = false;
    private String mIntentSource;
    private String mStoragePath;
    private String mFilename = null;
    private String mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mIntentSource = intent.getExtras().getString("source");

        // 锁定屏幕
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // setContentView(R.layout.activity_addpictures);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }

        if ("mapPage".equals(mIntentSource) || "mapPageNotBegun".equals(mIntentSource)) {
            latitude = intent.getExtras().getDouble("latitude");
            longitude = intent.getExtras().getDouble("longitude");
            setContentView(R.layout.activity_photograph);
        } else if ("discovery".equals(mIntentSource)) {
            mImageUri = intent.getExtras().getString("imageUri");
            setContentView(R.layout.activity_addpictures);
        }

        mStoragePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/walknshot";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        initView();
    }

    private void initView() {
        if ("mapPage".equals(mIntentSource)) {
            mBtnTakePhoto = (ImageButton) findViewById(R.id.btn_take_photo);
            mBtnOpenAlbum = (ImageButton) findViewById(R.id.btn_open_album);
            mBtnTakePhoto.setOnClickListener(onClickListener);
            mBtnOpenAlbum.setOnClickListener(onClickListener);
        } else if ("mapPageNotBegun".equals(mIntentSource)) {
            mBtnTakePhoto = (ImageButton) findViewById(R.id.btn_take_photo);
            mBtnOpenAlbum = (ImageButton) findViewById(R.id.btn_open_album);
            mBtnTakePhoto.setOnClickListener(onClickListener);
            mBtnOpenAlbum.setClickable(false);
            mBtnOpenAlbum.setBackgroundColor(getResources().getColor(R.color.lostGrey));
        } else if ("discovery".equals(mIntentSource)) {

            //获取控件对象
            mGridView = (GridView) findViewById(R.id.add_pic_gridview);
            /*
             * 载入默认图片添加图片加号
             * 通过适配器实现
             * SimpleAdapter参数imageItem为数据源 R.layout.gridview_addpic为布局
             */
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic); //加号
            imageItem = new ArrayList<>();
            HashMap<String, Object> map = new HashMap<>();
            map.put("itemImage", bmp);
            imageItem.add(map);
            simpleAdapter = new SimpleAdapter(this,
                    imageItem, R.layout.my_view_addpic,
                    new String[]{"itemImage"}, new int[]{R.id.imageView1});

            imageUris = new ArrayList<>();
            /*
             * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如
             * map.put("itemImage", R.drawable.img);
             * 解决方法:
             *              1.自定义继承BaseAdapter实现
             *              2.ViewBinder()接口实现
             *  参考 http://blog.csdn.net/admin_/article/details/7257901
             */
            simpleAdapter.setViewBinder(new ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    // TODO Auto-generated method stub
                    if (view instanceof ImageView && data instanceof Bitmap) {
                        ImageView i = (ImageView) view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            mGridView.setAdapter(simpleAdapter);

            Uri uri = Uri.parse(mImageUri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap extractBm = null;
            // 获取缩略图
            try {
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri), null, bmOptions);
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = Math.max(bmOptions.outWidth / 400, bmOptions.outHeight / 400);
                extractBm = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri), null, bmOptions);
            } catch (Exception e) {
                MyToast.makeText(getApplicationContext(), R.string.error_upload_fail, MyToast.LENGTH_SHORT).show();
            }
            imageUris.add(uri);
            HashMap<String, Object> map2 = new HashMap<>();
            map2.put("itemImage", extractBm);
            imageItem.add(map2);
            simpleAdapter = new SimpleAdapter(this,
                    imageItem, R.layout.my_view_addpic,
                    new String[]{"itemImage"}, new int[]{R.id.imageView1});
            simpleAdapter.setViewBinder(new ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    // TODO Auto-generated method stub
                    if (view instanceof ImageView && data instanceof Bitmap) {
                        ImageView i = (ImageView) view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            mGridView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
            /*
             * 监听GridView点击事件
             * 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
             */
            mGridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    if (position == 0 && imageItem.size() == 10) { //第一张为默认图片
                        MyToast.makeText(getApplicationContext(), R.string.error_max_photo, MyToast.LENGTH_SHORT).show();
                    } else if (position == 0) { //点击图片位置为+ 0对应0张图片
                        //    指定下拉列表的显示数据
                        final String[] selects = {
                                getString(R.string.action_take_photo),
                                getString(R.string.action_open_album)
                        };
                        //    设置一个下拉的列表选择项
                        new Builder(AddPicturesActivity.this)
                                .setItems(selects, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0: {
                                                String filePath = mStoragePath;
                                                File localFile = new File(filePath);
                                                if (!localFile.exists()) {
                                                    localFile.mkdir();
                                                }
                                                mFilename = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.US)) + ".jpg";
                                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mStoragePath
                                                        , mFilename)));
                                                startActivityForResult(intent, PHOTO_GRAPH);
                                                break;
                                            }
                                            case 1: {
                                                Intent intent = new Intent(Intent.ACTION_PICK, null);
                                                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                                                startActivityForResult(intent, PHOTO_ZOOM);
                                                break;
                                            }
                                        }
                                    }
                                }).show();
                    } else {
                        dialog(position);
                        //Toast.makeText(AddPicturesActivity.this, "点击第" + (position + 1) + " 号图片",
                        //		Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //设置Textview监听事件
            mBtnSubmitPic = (Button) findViewById(R.id.add_pic_btn_submit);
            mBtnSubmitPic.setOnClickListener(onClickListener);
            //shareToWeChat = (TextView) findViewById(R.id.shareToWeChat);
            //shareToWeChat.setOnClickListener(onClickListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPermissionDenied) {
            PermissionUtils.PermissionDeniedDialog
                    .newInstance(true).show(getFragmentManager(), "dialog");
            mPermissionDenied = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
            } else {
                mPermissionDenied = true;
            }
        }
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mBtnOpenAlbum) {
                if (!NoFastClickUtil.isFastClick()) {
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                    startActivityForResult(intent, PHOTO_ZOOM);
                }
            } else if (v == mBtnTakePhoto && !NoFastClickUtil.isFastClick()) {
                String filePath = mStoragePath;
                File localFile = new File(filePath);
                if (!localFile.exists()) {
                    localFile.mkdir();
                }
                mFilename = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.US)) + ".jpg";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mStoragePath
                        , mFilename)));
                startActivityForResult(intent, PHOTO_GRAPH);
            } else if (v == mBtnSubmitPic) { //提交照片
//                final ClientImpl client = ClientImpl.getInstance();
//                for (int i = 1; i < imageItem.size(); i++) {
//                    Bitmap temp = (Bitmap) imageItem.get(i).get("itemImage");
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    temp.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                    byte[] bitmapByte = baos.toByteArray();
//                    final int fi = i;
//
//                    client.uploadPicture(new Callback() {
//                        @Override
//                        public void onNetworkFailure(IOException e) {
//                            MyToast.makeText(getApplicationContext(), R.string.error_network_fail, MyToast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onFailure(Object arg) {
//                            MyToast.makeText(getApplicationContext(), "第" + fi + "张图片上传失败", MyToast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onSuccess(Object arg) {
//                            MyToast.makeText(getApplicationContext(), "图片上传成功", MyToast.LENGTH_SHORT).show();
//                        }
//                    }, bitmapByte);
//                }
//                MyToast.makeText(getApplicationContext(), R.string.upload_success, MyToast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    //获取图片路径 响应startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE)
            return;
        // 拍照
        if (requestCode == PHOTO_GRAPH) {
            // 设置文件保存路径
            String filePath = mStoragePath;
            File localFile = new File(filePath);
            if (!localFile.exists()) {
                boolean b = localFile.mkdir();
                if (!b) {
                    MyToast.makeText(getApplicationContext(), "创建文件夹失败", MyToast.LENGTH_SHORT).show();
                }
            }
            File picture = new File(mStoragePath
                    + "/" + mFilename);
            try {
                MediaStore.Images.Media.insertImage(getContentResolver(), picture.getAbsolutePath(), "title", "description");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(picture)));
            beautifyPhoto(Uri.fromFile(picture));
        }
        if (data == null)
            return;
        // 读取相册缩放图片
        if (requestCode == PHOTO_ZOOM) {
            beautifyPhoto(data.getData());
        }
        // 处理结果
        if (requestCode == PHOTO_RESULT) {
            Uri uri = data.getData();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap extractBm = null;
            // 获取缩略图
            try {
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri), null, bmOptions);
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = Math.max(bmOptions.outWidth / 400, bmOptions.outHeight / 400);
                extractBm = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri), null, bmOptions);
            } catch (Exception e) {
                MyToast.makeText(getApplicationContext(), R.string.error_upload_fail, MyToast.LENGTH_SHORT).show();
            }
            if ("mapPage".equals(mIntentSource) || "mapPageNotBegun".equals(mIntentSource)) {
                extractBm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bis = stream.toByteArray();
                Intent intent = new Intent();
                intent.putExtra("returnbitmap", bis)
                        .putExtra("latitude", latitude)
                        .putExtra("longitude", longitude);
                setResult(RESULT_OK, intent);
                finish();
            } else if ("discovery".equals(mIntentSource)) {
                imageUris.add(uri);
                HashMap<String, Object> map = new HashMap<>();
                map.put("itemImage", extractBm);
                imageItem.add(map);
                simpleAdapter = new SimpleAdapter(this,
                        imageItem, R.layout.my_view_addpic,
                        new String[]{"itemImage"}, new int[]{R.id.imageView1});
                simpleAdapter.setViewBinder(new ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Object data,
                                                String textRepresentation) {
                        // TODO Auto-generated method stub
                        if (view instanceof ImageView && data instanceof Bitmap) {
                            ImageView i = (ImageView) view;
                            i.setImageBitmap((Bitmap) data);
                            return true;
                        }
                        return false;
                    }
                });
                mGridView.setAdapter(simpleAdapter);
                simpleAdapter.notifyDataSetChanged();
                //刷新后释放防止手机休眠后自动添加
            }
        }
    }


    // 美化照片
    public void beautifyPhoto(Uri uri) {
        Intent intent = new Intent(AddPicturesActivity.this, BeautifyPictureActivity.class);
        intent.setData(uri);
        startActivityForResult(intent, PHOTO_RESULT);
    }

    /**
     * 收缩图片
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);

        //返回一个bitmap
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_BEAUTIFY);
    }

    /*
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position) {
        if (position != 1) {
            Builder builder = new Builder(AddPicturesActivity.this);
            builder.setMessage("确认移除已添加图片吗？");
            builder.setTitle("提示");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    imageItem.remove(position);
                    simpleAdapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

}