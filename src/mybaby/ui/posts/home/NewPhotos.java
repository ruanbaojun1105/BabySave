package mybaby.ui.posts.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import me.hibb.mybaby.android.BuildConfig;
import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.cache.CacheDataTask;
import mybaby.ui.MediaHelper;
import mybaby.ui.MediaHelper.MediaHelperCallback;
import mybaby.ui.broadcast.PostMediaTask;
import mybaby.ui.community.customclass.CustomAbsClass.StarEdit;
import mybaby.ui.posts.edit.EditPostActivity;
import mybaby.util.DateUtils;
import mybaby.util.ImageViewUtil;
import mybaby.util.StringUtils;

public class NewPhotos implements MediaHelperCallback{
	private LinearLayout mParentContainer;
	private View mNewestPhotosView;
	private Activity mActivity;
	private View mPhotosView;
	private ArrayList<Uri> mNewestPhotos;
	private long mLngLastDatetime;
	
	final private int PhotosLimit=6;//最多6张照片，显示5.5张
	
	public NewPhotos(Activity activity){
		mActivity=activity;
	}
	
	public NewPhotos(Activity activity,LinearLayout parentContainer){
		mActivity=activity;
		mParentContainer=parentContainer;
	}
	
	public void refreshView(){
		if(mNewestPhotosView != null){
			mParentContainer.removeView(mNewestPhotosView);
			mNewestPhotosView=null;
		}
		
		mNewestPhotos=getNewestPhotos(getPhotosLastDatetime());
		
		if(mLngLastDatetime>0 && mLngLastDatetime>getPhotosLastDatetime() && mNewestPhotos.size()>0){
			sendHomeTagBadgeBoardCast(true);
			mNewestPhotosView=createNewPhotosView();
			mParentContainer.addView(mNewestPhotosView);
		}else{
			sendHomeTagBadgeBoardCast(false);
		}
		
	}
	
	private View createNewPhotosView(){
		int photoViewWH = (int) ((MyBabyApp.screenWidth-12*MyBabyApp.density-2*MyBabyApp.density*(PhotosLimit-1))/(PhotosLimit-0.5));
		mPhotosView=mActivity.getLayoutInflater().inflate(R.layout.new_photos, null);
		
		LinearLayout photosContainer=(LinearLayout)mPhotosView.findViewById(R.id.photos_container);
		TextView btnClose=(TextView)mPhotosView.findViewById(R.id.close_button);
		TextView btnAdd=(TextView)mPhotosView.findViewById(R.id.add_button);
		
		//根据新照片创建照片内容
		for(int i=0;i<mNewestPhotos.size();i++){
			final int fi=i;
			RelativeLayout photoCell=new RelativeLayout(mActivity);
			photosContainer.addView(photoCell);
			photoCell.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					addPhotos(mNewestPhotos.get(fi));
					//以下逻辑为打开选择照片列表
//					selectPhotos(fi);
				}
	        });
			
			ImageView photoView=new ImageView(mActivity);
			photoView.setScaleType(ScaleType.CENTER_CROP);
			photoCell.addView(photoView);
			LayoutParams lp_photoView=new LayoutParams(photoViewWH,photoViewWH);
			if(i>0){
				lp_photoView.leftMargin=(int) (2*MyBabyApp.density);//rightMargin再ZTE上无效，只能这么用了
			}
			photoView.setLayoutParams(lp_photoView);
			
			TextView textCheck=new TextView(mActivity);
			textCheck.setText(R.string.fa_check_circle);
			textCheck.setTypeface(MyBabyApp.fontAwesome);
			textCheck.setTextSize(14);
			textCheck.setTextColor(Color.LTGRAY);
			photoCell.addView(textCheck);
			
			LayoutParams lp_check=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			//lp_check.addRule(RelativeLayout.ALIGN_PARENT_TOP);  不知为何乱了
			//lp_check.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lp_check.leftMargin=(int) (6*MyBabyApp.density);
			lp_check.topMargin=(int) (3*MyBabyApp.density);
			
			textCheck.setLayoutParams(lp_check);

			
			String photoUri = mNewestPhotos.get(i).toString();
			if(!photoUri.contains("content://")) {
				photoUri = Uri.fromFile(new File(photoUri)).toString();
    		}
			ImageViewUtil.displayImage(photoUri, photoView);
			
		}
		
		//关闭
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendHomeTagBadgeBoardCast(false);
				setPhotosLastDatetime(mLngLastDatetime);
				mParentContainer.removeView(mNewestPhotosView);
				mNewestPhotosView = null;
			}
		});
		
		//添加
		btnAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addPhotos(mNewestPhotos.get(0));
			}
		});
		
		return mPhotosView;
	}
	//发送广播通知主页Home页面更新小红点
	public void sendHomeTagBadgeBoardCast(boolean isNeedUpdataTagBadge){
		Bundle bundle=new Bundle();
		bundle.putBoolean("isNeedUpdateTagBadge", isNeedUpdataTagBadge);
		PostMediaTask.sendBroadcast(Constants.BroadcastAction.BroadcastAction_MainActivity_Home_TagBadgeUpdate, bundle);
	}
	
	public int getNewPhotosCount(long lngPhotosLastDateTime){
		return getNewestPhotos(lngPhotosLastDateTime).size();
	}

	public ArrayList<Uri> getPhotos(long lngPhotosLastDateTime){
		if (mNewestPhotos==null)
			mNewestPhotos=getNewestPhotos(lngPhotosLastDateTime);
		return mNewestPhotos;
	}

	private ArrayList<Uri> getNewestPhotos(long lngPhotosLastDateTime){
        Cursor imageCursor = null;
    	ArrayList<Uri> newestPhotos=new ArrayList<Uri>();

		if (!BuildConfig.DEBUG) {
			if (!CacheDataTask.booleaRefush(MyBabyApp.getSharedPreferences().getLong("install_time", 0), 60 * 24))//安装后的24小时内不提示有新照片
				return newestPhotos;
		}
		long lngLastDatetime=0;
    	
        try {
        	final String[] columns = {MediaStore.Images.Media.DATA,MediaStore.Images.Media.DATE_ADDED};
        	final String where=MediaStore.Images.Media.DATE_ADDED + ">" + lngPhotosLastDateTime/1000;//系统数据存储用的是秒
            final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
            imageCursor = mActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, where, null, orderBy);
            while (imageCursor.moveToNext()) {
            	if(lngLastDatetime==0){
            		lngLastDatetime = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))*1000;//系统数据存储用的是秒，转换为毫秒
            	}
                
                Uri uri = Uri.parse(imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));


                if(uri.toString().contains("/DCIM/Camera/"))
                {
                	newestPhotos.add(uri);
//                	Log.e("有用的图片路径",uri.toString());
                }
               
                if(newestPhotos.size()>=PhotosLimit){
                	break;
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(imageCursor != null && !imageCursor.isClosed()) {
				imageCursor.close();	
			}	
		}
        mLngLastDatetime=lngLastDatetime;
		return newestPhotos;
	}
	
	//选择一张照片 并打开照片墙
	private void selectPhotos(int position){
		MediaHelper mMediaHelper = new MediaHelper(mActivity, this);
//		addPhotos(photoUri)
//		mMediaHelper.launchMulPicker(Constants.MAX_PHOTO_PER_POST,position);
	}
	long lngLastDatetime=0;
	public void addPhotos(Uri photoUri) {
		Cursor imageCursor = null;
		final ArrayList<String> photos = new ArrayList<String>();
		lngLastDatetime = 0;
		final long lngPhotoTime = MediaHelper.getDatetimeFromURI(photoUri.toString());

		long beginPhotoTime = Math.max(DateUtils.lngDatetime2Date(lngPhotoTime), getPhotosLastDatetime()) / 1000;
		long endPhotoTime = lngPhotoTime + 60 * 60 * 24;

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
			if (ContextCompat.checkSelfPermission(mActivity , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				//申请CAMERA的权限
				ActivityCompat.requestPermissions(mActivity , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
			}
			return;
		}
		try {
			final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED};
			final String where = MediaStore.Images.Media.DATE_ADDED + ">" + beginPhotoTime + " and " + MediaStore.Images.Media.DATE_ADDED + "<" + endPhotoTime;
			final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
			imageCursor = mActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, where, null, orderBy);
			while (imageCursor.moveToNext()) {
				if (lngLastDatetime == 0) {
					lngLastDatetime = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)) * 1000;//系统数据存储用的是秒，转换为毫秒
				}
				Uri uri = Uri.parse(imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
				if (uri.toString().contains("/DCIM/Camera/")) {
					if (uri.toString().equals(photoUri.toString())) {
						photos.add(0, uri.toString());  //点击的那张在最前面
					} else {
						photos.add(uri.toString());
					}
					if (photos.size() >= Constants.MAX_PHOTO_PER_POST) {
						break;
					}
//                	Log.e("选中有用的图片", uri.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (imageCursor != null && !imageCursor.isClosed()) {
				imageCursor.close();
			}
		}


		if (photos.size() > 0) {
			new StarEdit() {
				@Override
				public void todo() {
					// TODO Auto-generated method stub
					Intent i = new Intent(mActivity, EditPostActivity.class);
					i.putExtra("initImageFileUrls", StringUtils.getFromArray(photos, "|"));
					i.putExtra("firstImageDatetime", lngPhotoTime);
					i.putExtra("photosLastDatetime", lngLastDatetime);
					mActivity.startActivityForResult(i, Constants.RequestCode.ACTIVITY_REQUEST_CODE_EDIT_POST_REQUEST);
				}
			}.StarPostEdit(mActivity);
		}
	}
	
	public static  long getPhotosLastDatetime(){
		return MyBabyApp.getSharedPreferences().getLong("photosLastDatetime", 0);
	}
	public static void setPhotosLastDatetime(long lngPostTime){
		if(lngPostTime<=getPhotosLastDatetime()){
			return;
		}
		
		Editor edit=MyBabyApp.getSharedPreferences().edit();
		if(lngPostTime>System.currentTimeMillis()){
			edit.putLong("photosLastDatetime", System.currentTimeMillis());
		}else{
			edit.putLong("photosLastDatetime", lngPostTime);
		}
		edit.commit();
	}

	public static  long getHomePhotosLastDatetime(){
		return MyBabyApp.getSharedPreferences().getLong("homePhotosLastDatetime", 0);
	}
	public static void setHomePhotosLastDatetime(long lngPostTime){
		if(lngPostTime<=getHomePhotosLastDatetime()){
			return;
		}

		Editor edit=MyBabyApp.getSharedPreferences().edit();
		if(lngPostTime>System.currentTimeMillis()){
			edit.putLong("homePhotosLastDatetime", System.currentTimeMillis());
		}else{
			edit.putLong("homePhotosLastDatetime", lngPostTime);
		}
		edit.commit();
	}

	@Override
	public void onMediaFileDone(String[] mediaFilePaths) {
		TodayWrite.openEditPostActivity(mActivity, mediaFilePaths, false);
	}
	
}
