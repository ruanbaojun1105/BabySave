package photopickerlib.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.cache.CacheDataTask;
import mybaby.cache.GenCaches;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.ImagePageActivity;
import mybaby.util.ImageViewUtil;
import photopickerlib.PhotoPickerActivity;
import photopickerlib.beans.Photo;
import photopickerlib.utils.OtherUtils;

/**
 * @Class: PhotoAdapter
 * @Description: 图片适配器
 * @author: lling(www.liuling123.com)
 * @Date: 2015/11/4
 */
public class PhotoAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_PHOTO = 1;

    private List<Photo> mDatas;
    //存放已选中的Photo数据
    private List<String> mSelectedPhotos;
    private Context mContext;
    private int mWidth;
    //是否显示相机，默认不显示
    private boolean mIsShowCamera = false;
    //照片选择模式，默认单选
    private int mSelectMode = PhotoPickerActivity.MODE_SINGLE;
    //图片选择数量
    private int mMaxNum = PhotoPickerActivity.DEFAULT_NUM;

    private View.OnClickListener mOnPhotoClick;
    private PhotoClickCallBack mCallBack;

    public PhotoAdapter(Context context, List<Photo> mDatas) {
        this.mDatas = mDatas;
        this.mContext = context;
        int screenWidth = OtherUtils.getWidthInPx(mContext);
        mWidth = (screenWidth - OtherUtils.dip2px(mContext, 4))/3;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && mIsShowCamera) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PHOTO;
        }
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Photo getItem(int position) {
        if(mIsShowCamera) {
            if(position == 0){
                return null;
            }
            return mDatas.get(position-1);
        }else{
            return mDatas.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return mDatas.get(position).getId();
    }

    public void setDatas(List<Photo> mDatas) {
        this.mDatas = mDatas;
    }

    public void setIsShowCamera(boolean isShowCamera) {
        this.mIsShowCamera = isShowCamera;
    }

    public boolean isShowCamera() {
        return mIsShowCamera;
    }

    public void setMaxNum(int maxNum) {
        this.mMaxNum = maxNum;
    }

    public void setPhotoClickCallBack(PhotoClickCallBack callback) {
        mCallBack = callback;
    }


    /**
     * 获取已选中相片
     * @return
     */
    public List<String> getmSelectedPhotos() {
        return mSelectedPhotos;
    }

    public void setSelectMode(int selectMode) {
        this.mSelectMode = selectMode;
        if(mSelectMode == PhotoPickerActivity.MODE_MULTI) {
            initMultiMode();
        }
    }

    /**
     * 初始化多选模式所需要的参数
     */
    private void initMultiMode() {
        mSelectedPhotos = new ArrayList<String>();
        /*mOnPhotoClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };*/
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(getItemViewType(position) == TYPE_CAMERA) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_camera_layout, null);
            convertView.setTag(null);
            //设置高度等于宽度
            GridView.LayoutParams lp = new GridView.LayoutParams(mWidth, mWidth);
            convertView.setLayoutParams(lp);
        } else {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_photo_layout, null);
                holder.photoImageView = (ImageView) convertView.findViewById(R.id.imageview_photo);
                holder.selectView = (ImageView) convertView.findViewById(R.id.checkmark);
                holder.maskView = convertView.findViewById(R.id.mask);
                //holder.wrapLayout = (FrameLayout) convertView.findViewById(R.id.wrap_layout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //holder.photoImageView.setImageResource(R.drawable.ic_photo_loading);
            Photo photo = getItem(position);
            if(mSelectMode == PhotoPickerActivity.MODE_MULTI) {
                holder.selectView.setTag(photo.getPath());
                holder.selectView.setVisibility(View.VISIBLE);
                holder.selectView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String path = v.getTag().toString();
                        if(mSelectedPhotos.contains(path)) {
                            holder.maskView.setVisibility(View.GONE);
                            holder.selectView.setSelected(false);
                            mSelectedPhotos.remove(path);
                        } else {
                            if(mSelectedPhotos.size() >= mMaxNum) {
                                Toast.makeText(mContext, R.string.msg_maxi_capacity,
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mSelectedPhotos.add(path);
                            holder.maskView.setVisibility(View.VISIBLE);
                            holder.selectView.setSelected(true);
                        }
                        if(mCallBack != null) {
                            mCallBack.onPhotoClick();
                        }
                    }
                });
                if(mSelectedPhotos != null && mSelectedPhotos.contains(photo.getPath())) {
                    holder.selectView.setSelected(true);
                    holder.maskView.setVisibility(View.VISIBLE);
                } else {
                    holder.selectView.setSelected(false);
                    holder.maskView.setVisibility(View.GONE);
                }
                holder.photoImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //SelectBigImageActivity.launch(mContext, v, mDatas, mIsShowCamera ? position - 1 : position);
                        GenCaches cache=new GenCaches(mDatas, String.valueOf(MyBabyApp.version),new Date());
                        CacheDataTask.putCache(mContext, cache, ImagePageActivity.IMAGE_CACHE_TYPE_KEY, true);
                        Intent intent=new Intent(mContext, ImagePageActivity.class);
                        intent.putExtra(ImagePageActivity.IMAGE_CACHE_TYPE_KEY, 0);
                        intent.putExtra("index",mIsShowCamera?position-1:position);
                        intent.putExtra("isEdit",false);
                        intent.putExtra("hasActionbar",true);
                        intent.putExtra("isFilePath",true);
                        mContext.startActivity(intent);
                    }
                });
            } else {
                holder.selectView.setVisibility(View.GONE);
            }
            /*Picasso.with(mContext)
                    .load(photo.getPath())
                    .resize(250, 250)
                    .config(Bitmap.Config.RGB_565)
                            //.skipMemoryCache()
                    .placeholder(new ColorDrawable(Color.parseColor("#f5f5f5")))
                    .centerCrop()
                    .into(holder.photoImageView);*/
            ImageViewUtil.displayImage("file://" + photo.getPath(), holder.photoImageView);
            //ImageLoader.getInstance(mContext).display(photo.getPath(), holder.photoImageView,mWidth, mWidth);
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView photoImageView;
        private ImageView selectView;
        private View maskView;
        //private FrameLayout wrapLayout;
    }

    /**
     * 多选时，点击相片的回调接口
     */
    public interface PhotoClickCallBack {
        void onPhotoClick();
    }
}
