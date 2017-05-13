package mybaby.ui.community.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import me.hibb.mybaby.android.R;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.customclass.PhotoView;
import mybaby.util.DialogShow;
import mybaby.util.ImageViewUtil;

/**
 * Created by LeJi_BJ on 2016/3/10.
 */
public class ImageFragment extends Fragment {

    public static String IMAGE="IMAGEURL";
    public static String ISFILE="ISFILE";

    private String imagepath;
    private boolean isFilePath;
    private View rootView;
    private PhotoView imageView;
    private ProgressBar progressBar;

    public ImageFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*if (rootView == null) {
            rootView = inflater.inflate(R.layout.imagepage_item, null);
            initView();
        }

        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }*/
        rootView = inflater.inflate(R.layout.imagepage_item, null);
        initView();
        return rootView;
    }

    public PhotoView getImageView(){
        return imageView;
    }

    private void initView(){
        if (getArguments()==null)
            return;
        imagepath=getArguments().getString(IMAGE, "");
        isFilePath=getArguments().getBoolean(ISFILE,false);

        if (TextUtils.isEmpty(imagepath))
            return;
        imagepath = (isFilePath ? "file://" : "") + imagepath;
        imageView = (PhotoView) rootView.findViewById(R.id.image_id);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        imageView.enable();
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        progressBar.bringToFront();
        //imageView.animaFrom(imageView.getInfo());
        progressBar.setVisibility(View.VISIBLE);
        // Utils.Loge(images[i]);
        // imageView.resetImage();
        ImageViewUtil.displayImage(imagepath, imageView,
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri,
                                                  View view, Bitmap loadedImage) { // TODO
                        if (loadedImage != null) {
                            /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                            params.addRule(RelativeLayout.CENTER_IN_PARENT);
                            imageView.setLayoutParams(params);
                            Bitmap bitmap;
                            try {
                                if (MyBabyApp.screenWidth == 0)
                                    MyBabyApp.initScreenParams(getActivity());
                                bitmap = ActivityItem.zoomImg(
                                        loadedImage,
                                        MyBabyApp.screenWidth,
                                        ImageHelper.getImageScaleHeight(
                                                loadedImage,
                                                MyBabyApp.screenWidth));
                            } catch (Exception e) {
                                e.printStackTrace();
                                bitmap = loadedImage;

                            }*/
                            new CustomAbsClass.doSomething(getActivity()) {
                                @Override
                                public void todo() {
                                    imageView.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            };
                        }
                    }
                }, false);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    getActivity().onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub

                if (!TextUtils.isEmpty(imagepath)) {
                    final Bitmap bitmap = ImageLoader.getInstance()
                            .loadImageSync(imagepath);
                    DialogShow.savePictureDialog(getActivity(),
                            bitmap);
                }
                return false;
            }
        });
    }
}
