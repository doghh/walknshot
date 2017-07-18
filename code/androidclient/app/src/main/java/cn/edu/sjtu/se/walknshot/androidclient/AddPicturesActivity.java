package cn.edu.sjtu.se.walknshot.androidclient;

import android.Manifest;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cn.edu.sjtu.se.walknshot.apiclient.*;

public class AddPicturesActivity extends MyAppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GridView mGridView;                   //网格显示缩略图
    private Button mBtnSubmitPic;
    private TextView shareToWeChat;
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
    private String mStoragePath;
    private String filename = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpictures);
        //锁定屏幕
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_addpictures);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }

        mStoragePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/walknshot";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        Intent intent =getIntent();
        latitude=intent.getExtras().getDouble("latitude");
        longitude=intent.getExtras().getDouble("longitude");


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
                imageItem, R.layout.gridview_addpic,
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

        /*
         * 监听GridView点击事件
         * 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
         */
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == 0 && imageItem.size() == 10) { //第一张为默认图片
                    Toast.makeText(AddPicturesActivity.this, "照片数最多为9张", Toast.LENGTH_SHORT).show();
                } else if (position == 0) { //点击图片位置为+ 0对应0张图片
                    Builder builder = new Builder(AddPicturesActivity.this);
                    builder.setTitle("选择照片");
                    //    指定下拉列表的显示数据
                    final String[] selects = {"拍取照片", "打开相册"};
                    //    设置一个下拉的列表选择项
                    builder.setItems(selects, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: {
                                    String filePath = mStoragePath;
                                    File localFile = new File(filePath);
                                    if (!localFile.exists()) {
                                        localFile.mkdir();
                                    }
                                    filename = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.US)) + ".jpg";
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mStoragePath
                                            , filename)));
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
                            ;
                        }
                    });
                    builder.show();
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
//        shareToWeChat = (TextView) findViewById(R.id.shareToWeChat);
//        shareToWeChat.setOnClickListener(onClickListener);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
            if (v == mBtnSubmitPic) { //提交照片
                final Client client = ClientImpl.getInstance();
                client.addSpot(new Callback() {
                    @Override
                    public void onNetworkFailure(IOException e) {
                        Toast.makeText(AddPicturesActivity.this,"网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Object arg) {
                        Toast.makeText(AddPicturesActivity.this,"图片上传失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Object arg) {
                        for (int i = 1 ; i < imageItem.size() ; i++ ){
                            Bitmap temp = (Bitmap) imageItem.get(i).get("itemImage");
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            temp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte [] bitmapByte = baos.toByteArray();
                            final int fi=i;

                            client.uploadPicture(new Callback() {
                                @Override
                                public void onNetworkFailure(IOException e) {
                                    Toast.makeText(AddPicturesActivity.this,"网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Object arg) {
                                    Toast.makeText(AddPicturesActivity.this,"第"+fi+"张图片上传失败", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess(Object arg) {
                                    Toast.makeText(AddPicturesActivity.this,"图片上传成功", Toast.LENGTH_SHORT).show();
                                }
                            }, bitmapByte);
                        }
                        Toast.makeText(AddPicturesActivity.this,"图片上传成功", Toast.LENGTH_SHORT).show();
                    }
                }, latitude, longitude);

            } else if (v == shareToWeChat) { //微信分享
                Builder builder = new Builder(AddPicturesActivity.this);
                builder.setTitle("分享");
                //    指定下拉列表的显示数据
                final String[] selects = {"微信朋友圈", "微信好友"};
                //    设置一个下拉的列表选择项
                builder.setItems(selects, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: { //分享到微信朋友圈
                                /*
                                Intent intent = new Intent("android.intent.action.VIEW");
                                ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setComponent(comp);
                                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                intent.setType("image/*");
                                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                                intent.putExtra("Kdescription", "wwwwwwwwwwwwwwwwwwww");
                                startActivity(intent);*/
                                break;
                            }
                            case 1: { //分享给微信好友
                                break;
                            }
                        }
                    }
                });
                builder.show();
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
                    Toast.makeText(AddPicturesActivity.this, "创建文件夹失败", Toast.LENGTH_SHORT).show();
                }
            }
            File picture = new File(mStoragePath
                    + "/" + filename);
            try {
                MediaStore.Images.Media.insertImage(getContentResolver(), picture.getAbsolutePath(), "title", "description");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(picture)));
            startPhotoZoom(Uri.fromFile(picture));
        }
        if (data == null)
            return;
        // 读取相册缩放图片
        if (requestCode == PHOTO_ZOOM) {
            startPhotoZoom(data.getData());
        }
        // 美化照片
        if (requestCode == PHOTO_BEAUTIFY) {
            Intent intent = new Intent(AddPicturesActivity.this, BeautifyPictureActivity.class);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bundle extras = data.getExtras();
            Bitmap newbmp;
            if (extras != null) {
                newbmp = extras.getParcelable("data");
                newbmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }
            byte[] bitmapByte = baos.toByteArray();
            intent.putExtra("bitmap", bitmapByte);
            startActivityForResult(intent, PHOTO_RESULT);
        }
        // 处理结果
        if (requestCode == PHOTO_RESULT) {
            byte[] bis = data.getByteArrayExtra("returnbitmap");
            Bitmap addbmp = BitmapFactory.decodeByteArray(bis, 0, bis.length);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            addbmp.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0-100)压缩文件
            //此处可以把Bitmap保存到sd卡中
            String filePath = mStoragePath;
            File localFile = new File(filePath);
            if (!localFile.exists()) {
                boolean b = localFile.mkdir();
                if (!b) {
                    Toast.makeText(AddPicturesActivity.this, "创建文件夹失败", Toast.LENGTH_SHORT).show();
                }
            }
            filename = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.US)) + ".jpg";
            File picture = new File(mStoragePath + "/" + filename);
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
            imageUris.add(Uri.fromFile(picture));


            HashMap<String, Object> map = new HashMap<>();
            map.put("itemImage", addbmp);
            imageItem.add(map);
            simpleAdapter = new SimpleAdapter(this,
                    imageItem, R.layout.gridview_addpic,
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
        super.onActivityResult(requestCode, resultCode, data);
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