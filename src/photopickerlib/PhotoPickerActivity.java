package photopickerlib;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.hibb.mybaby.android.R;
import mybaby.util.LogUtils;
import photopickerlib.adapters.FloderAdapter;
import photopickerlib.adapters.PhotoAdapter;
import photopickerlib.beans.Photo;
import photopickerlib.beans.PhotoFloder;
import photopickerlib.utils.OtherUtils;
import photopickerlib.utils.PhotoUtils;

/**
 * @Class: PhotoPickerActivity
 * @Description: 照片选择界面
 * @author: lling(www.liuling123.com)
 * @Date: 2015/11/4
 */
public class PhotoPickerActivity extends AppCompatActivity implements PhotoAdapter.PhotoClickCallBack {

    public final static String TAG = "PhotoPickerActivity";

    public final static String KEY_RESULT = "picker_result";
    public final static int REQUEST_CAMERA = 1;

    /** 是否显示相机 */
    public final static String EXTRA_SHOW_CAMERA = "is_show_camera";
    /** 是否直接开启相机*/
    public final static String EXTRA_OPEN_CAMERA = "is_open_camera";
    /** 拍照完成之后是否需要系统剪切*/
    public final static String EXTRA_OPEN_CAMERA_CUT = "is_open_camera_cut";
    /** 照片选择模式 */
    public final static String EXTRA_SELECT_MODE = "select_mode";
    /** 最大选择数量 */
    public final static String EXTRA_MAX_MUN = "max_num";
    /** 单选 */
    public final static int MODE_SINGLE = 0;
    /** 多选 */
    public final static int MODE_MULTI = 1;
    /** 默认最大选择数量 */
    public final static int DEFAULT_NUM = 9;

    private final static String ALL_PHOTO = "所有图片";
    /** 是否直接开启相机，默认不开启*/
    private boolean mIsOpenCamera = false;
    /** 是否拍照完成之后是否需要系统剪切，默认不需要切剪 */
    private boolean mIsOpenCameraCut = false;
    /** 是否显示相机，默认不显示 */
    private boolean mIsShowCamera = false;
    /** 照片选择模式，默认是单选模式 */
    private int mSelectMode = 0;
    /** 最大选择数量，仅多选模式有用 */
    private int mMaxNum;

    private GridView mGridView;
    private Map<String, PhotoFloder> mFloderMap;
    private List<Photo> mPhotoLists = new ArrayList<Photo>();
    private ArrayList<String> mSelectList = new ArrayList<String>();
    private PhotoAdapter mPhotoAdapter;
    private ProgressDialog mProgressDialog;
    private ListView mFloderListView;

    private TextView mPhotoNumTV;
    private TextView mPhotoNameTV;
    private Button mCommitBtn;
    /** 文件夹列表是否处于显示状态 */
    boolean mIsFloderViewShow = false;
    /** 文件夹列表是否被初始化，确保只被初始化一次 */
    boolean mIsFloderViewInit = false;

    /** 拍照时存储拍照结果的临时文件 */
    private File mTmpFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker_main);
        initIntentParams();
        initView();
        if (!OtherUtils.isExternalStorageAvailable()) {
            Toast.makeText(this, "No SD card!", Toast.LENGTH_SHORT).show();
            return;
        }
        getPermissionImage();
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.photo_show_grid);
        mPhotoNumTV = (TextView) findViewById(R.id.photo_num);
        mPhotoNameTV = (TextView) findViewById(R.id.floder_name);
        ((RelativeLayout) findViewById(R.id.bottom_tab_bar)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //消费触摸事件，防止触摸底部tab栏也会选中图片
                return true;
            }
        });
        (findViewById(R.id.actionbar_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.actionbar_title)).setText("图片");
    }

    /**
     * 初始化选项参数
     */
    private void initIntentParams() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_photo_picker);
        setSupportActionBar(toolbar);
        mIsShowCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, false);
        mSelectMode = getIntent().getIntExtra(EXTRA_SELECT_MODE, MODE_SINGLE);
        mMaxNum = getIntent().getIntExtra(EXTRA_MAX_MUN, DEFAULT_NUM);
        mIsOpenCamera= getIntent().getBooleanExtra(EXTRA_OPEN_CAMERA,false);
        mIsOpenCameraCut= getIntent().getBooleanExtra(EXTRA_OPEN_CAMERA_CUT,false);
        if (mIsOpenCamera){
            getPermissionCamera();
        }
        if(mSelectMode == MODE_MULTI) {
            //如果是多选模式，需要将确定按钮初始化以及绑定事件
            mCommitBtn = (Button) findViewById(R.id.commit);
            mCommitBtn.setVisibility(View.VISIBLE);
            mCommitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectList.addAll(mPhotoAdapter.getmSelectedPhotos());
                    returnData();
                }
            });
        }
    }

    private void getPhotosSuccess() {
        mProgressDialog.dismiss();
        mPhotoLists.addAll(mFloderMap.get(ALL_PHOTO).getPhotoList());

        mPhotoNumTV.setText(OtherUtils.formatResourceString(getApplicationContext(),
                R.string.photos_num, mPhotoLists.size()));

        mPhotoAdapter = new PhotoAdapter(this, mPhotoLists);
        mPhotoAdapter.setIsShowCamera(mIsShowCamera);
        mPhotoAdapter.setSelectMode(mSelectMode);
        mPhotoAdapter.setMaxNum(mMaxNum);
        mPhotoAdapter.setPhotoClickCallBack(this);

        mGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), Build.VERSION.SDK_INT < 19 ? true : false, Build.VERSION.SDK_INT > 20 ? false : true));
        mGridView.setAdapter(mPhotoAdapter);

        Set<String> keys = mFloderMap.keySet();
        final List<PhotoFloder> floders = new ArrayList<PhotoFloder>();
        for (String key : keys) {
            if (ALL_PHOTO.equals(key)) {
                PhotoFloder floder = mFloderMap.get(key);
                floder.setIsSelected(true);
                floders.add(0, floder);
            }else {
                floders.add(mFloderMap.get(key));
            }
        }
        mPhotoNameTV.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                toggleFloderList(floders);
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPhotoAdapter.isShowCamera() && position == 0) {
                    getPermissionCamera();
                    return;
                }
                selectPhoto(mPhotoAdapter.getItem(position));
            }
        });
    }

    /**
     * 点击选择某张照片
     * @param photo
     */
    private void selectPhoto(Photo photo) {
        LogUtils.e(TAG, "selectPhoto");
        if(photo == null) {
            return;
        }
        String path = photo.getPath();
        if(mSelectMode == MODE_SINGLE) {
            if (mIsOpenCameraCut){
                mTmpFile=new File(path);
                showCrop(Uri.fromFile(mTmpFile));
            }else {
                mSelectList.add(path);
                returnData();
            }

        }
    }

    @Override
    public void onPhotoClick() {
        LogUtils.e(TAG, "onPhotoClick");
        List<String> list = mPhotoAdapter.getmSelectedPhotos();
        if(list != null && list.size()>0) {
            mCommitBtn.setEnabled(true);
            mCommitBtn.setText(OtherUtils.formatResourceString(getApplicationContext(),
                    R.string.commit_num, list.size(), mMaxNum));
        } else {
            mCommitBtn.setEnabled(false);
            mCommitBtn.setText(R.string.commit);
        }
    }

    /**
     * 返回选择图片的路径
     */
    private void returnData() {
        // 返回已选择的图片数据
        Intent data = new Intent();
        data.putStringArrayListExtra(KEY_RESULT, mSelectList);
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * 显示或者隐藏文件夹列表
     * @param floders
     */
    private void toggleFloderList(final List<PhotoFloder> floders) {
        //初始化文件夹列表
        if(!mIsFloderViewInit) {
            ViewStub floderStub = (ViewStub) findViewById(R.id.floder_stub);
            floderStub.inflate();
            View dimLayout = findViewById(R.id.dim_layout);
            mFloderListView = (ListView) findViewById(R.id.listview_floder);
            final FloderAdapter adapter = new FloderAdapter(this, floders);
            mFloderListView.setAdapter(adapter);
            mFloderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    for (PhotoFloder floder : floders) {
                        floder.setIsSelected(false);
                    }
                    PhotoFloder floder = floders.get(position);
                    floder.setIsSelected(true);
                    adapter.notifyDataSetChanged();

                    mPhotoLists.clear();
                    mPhotoLists.addAll(floder.getPhotoList());
                    if (ALL_PHOTO.equals(floder.getName())) {
                        mPhotoAdapter.setIsShowCamera(mIsShowCamera);
                    } else {
                        mPhotoAdapter.setIsShowCamera(false);
                    }
                    //这里重新设置adapter而不是直接notifyDataSetChanged，是让GridView返回顶部
                    mGridView.setAdapter(mPhotoAdapter);
                    mPhotoNumTV.setText(OtherUtils.formatResourceString(getApplicationContext(),
                            R.string.photos_num, mPhotoLists.size()));
                    mPhotoNameTV.setText(floder.getName());
                    toggle();
                }
            });
            dimLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mIsFloderViewShow) {
                        toggle();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            initAnimation(dimLayout);
            mIsFloderViewInit = true;
        }
        toggle();
    }

    /**
     * 弹出或者收起文件夹列表
     */
    private void toggle() {
        if(mIsFloderViewShow) {
            outAnimatorSet.start();
            mIsFloderViewShow = false;
        } else {
            inAnimatorSet.start();
            mIsFloderViewShow = true;
        }
    }


    /**
     * 初始化文件夹列表的显示隐藏动画
     */
    AnimatorSet inAnimatorSet = new AnimatorSet();
    AnimatorSet outAnimatorSet = new AnimatorSet();
    private void initAnimation(View dimLayout) {
        ObjectAnimator alphaInAnimator, alphaOutAnimator, transInAnimator, transOutAnimator;
        //获取actionBar的高
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        /**
         * 这里的高度是，屏幕高度减去上、下tab栏，并且上面留有一个tab栏的高度
         * 所以这里减去3个actionBarHeight的高度
         */
        int height = OtherUtils.getHeightInPx(this) - 3*actionBarHeight;
        alphaInAnimator = ObjectAnimator.ofFloat(dimLayout, "alpha", 0f, 0.7f);
        alphaOutAnimator = ObjectAnimator.ofFloat(dimLayout, "alpha", 0.7f, 0f);
        transInAnimator = ObjectAnimator.ofFloat(mFloderListView, "translationY", height , 0);
        transOutAnimator = ObjectAnimator.ofFloat(mFloderListView, "translationY", 0, height);

        LinearInterpolator linearInterpolator = new LinearInterpolator();

        inAnimatorSet.play(transInAnimator).with(alphaInAnimator);
        inAnimatorSet.setDuration(300);
        inAnimatorSet.setInterpolator(linearInterpolator);
        outAnimatorSet.play(transOutAnimator).with(alphaOutAnimator);
        outAnimatorSet.setDuration(300);
        outAnimatorSet.setInterpolator(linearInterpolator);
    }

    /**
     * 选择文件夹
     * @param photoFloder
     */
    public void selectFloder(PhotoFloder photoFloder) {
        mPhotoAdapter.setDatas(photoFloder.getPhotoList());
        mPhotoAdapter.notifyDataSetChanged();
    }

    /**
     * 获取照片的异步任务
     */
    private AsyncTask getPhotosTask = new AsyncTask() {
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(PhotoPickerActivity.this, null, "loading...");
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                mFloderMap = PhotoUtils.getPhotos(
                        PhotoPickerActivity.this.getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
                mFloderMap=new HashMap<>();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            getPhotosSuccess();
        }
    };
    public final static int MY_PERMISSIONS_CAMERA=1;
    public final static int MY_PERMISSIONS_REQUEST_READ=2;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCamera();
                } else {
                    Toast.makeText(this, "请打开相机权限", Toast.LENGTH_LONG).show();
                }
                break;
            case MY_PERMISSIONS_REQUEST_READ:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPhotosTask.execute();
                } else {
                    Toast.makeText(this, "请打开读取权限", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void getPermissionCamera() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            showCamera();
        }else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //申请CAMERA的权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
            } else {
                showCamera();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            getPhotosTask.execute();
        }else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //申请CAMERA的权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ);
            } else {
                getPhotosTask.execute();
            }
        }
    }


    /**
     * 选择相机
     */
    private void showCamera() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getPackageManager()) != null){
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = OtherUtils.createFile(getApplicationContext());
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }else{
            Toast.makeText(getApplicationContext(),
                    R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }

    }

    private void clearData(boolean isRight){
        if (isRight){
            if (mTmpFile != null) {
                mSelectList.add(mTmpFile.getAbsolutePath());
                returnData();
            }
        }else{
            if(mTmpFile != null && mTmpFile.exists()){
                mTmpFile.delete();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 相机拍照完成后，返回图片路径
        if(requestCode == REQUEST_CAMERA){
            if(resultCode == Activity.RESULT_OK) {
                if (mIsOpenCameraCut)
                    showCrop(Uri.fromFile(mTmpFile));
                else {
                    clearData(true);
                }
            }else{
                 if (!mIsOpenCameraCut){
                     clearData(false);
                 }
            }
        }else if (requestCode== OpenCutImage){
            clearData(resultCode == Activity.RESULT_OK);
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mSelectList.add(resultUri.getPath());
                returnData();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //Exception error = result.getError();
                Toast.makeText(this,"返回错误！",Toast.LENGTH_SHORT).show();
            }
        }
    }
    final static int OpenCutImage=0x100;
    private void showCrop(Uri imageUri) {
        String state = android.os.Environment.getExternalStorageState();
        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this,"文件读取失败！",Toast.LENGTH_SHORT).show();
        } else {
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                    .start(this);


            /*Intent intent = new Intent();

            intent.setAction("com.android.camera.action.CROP");
            intent.setDataAndType(imageUri, "image*//*");
            intent.putExtra("crop", "false");
            intent.putExtra("aspectX", 1);// �ü������
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", ScreenUtils.dip2px(this,100));
            intent.putExtra("outputY", ScreenUtils.dip2px(this,100));
            intent.putExtra("scaleUpIfNeeded", true);//如果返回bitmap尺寸小于要求输出大小，则将返回的位图以corp形式拉到最大填充整个视图
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);//是否关闭人脸识别

            this.startActivityForResult(intent,OpenCutImage);*/
        }
    }


}
