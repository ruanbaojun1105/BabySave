package mybaby.ui.posts.home;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.person.Baby;
import mybaby.ui.MediaHelper;
import mybaby.ui.MediaHelper.MediaHelperCallback;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.customclass.CustomAbsClass.StarEdit;
import mybaby.ui.posts.edit.EditPostActivity;
import mybaby.util.StringUtils;

public class TodayWrite implements MediaHelperCallback{
	View mTodayWriteView;
    MediaHelper mMediaHelper;
    static FragmentActivity mActivity;
    
    public TodayWrite(final HomeTimelineFragment homeTimelineFragment,Baby ageForBaby){
    	mActivity=homeTimelineFragment.getActivity();
    	
    	mMediaHelper=new MediaHelper(mActivity,this);
    	mTodayWriteView=mActivity.getLayoutInflater().inflate(R.layout.today_write, null);
        
        TextView txtTodayTitle=(TextView)mTodayWriteView.findViewById(R.id.today_title);
        txtTodayTitle.getPaint().setFakeBoldText(true);
        
        
        TextView txtTodayAge=(TextView)mTodayWriteView.findViewById(R.id.today_age);
        String ageText;
        if(ageForBaby != null){
        	ageText=ageForBaby.getAgeText();
        	if(ageText==null){
        		ageText=(new SimpleDateFormat("EEEE")).format(new Date());
        	}
        }else{
        	ageText=(new SimpleDateFormat("EEEE")).format(new Date());
        }
        txtTodayAge.setText(ageText);
        
        TextView txtTodayText=(TextView)mTodayWriteView.findViewById(R.id.today_text);
        txtTodayText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//统计点击次数
				MobclickAgent.onEvent(mActivity, "2");//统计发送的个数

				new StarEdit() {
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						Intent i = new Intent(mActivity, EditPostActivity.class);
						mActivity.startActivityForResult(i, Constants.RequestCode.ACTIVITY_REQUEST_CODE_EDIT_POST_REQUEST);
					}
				}.StarPostEdit(mActivity);
			}
		});


        TextView imgTodayPhoto=(TextView)mTodayWriteView.findViewById(R.id.today_photo);
        imgTodayPhoto.setTypeface(MyBabyApp.fontAwesome);
        imgTodayPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new StarEdit (){
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						//统计点击次数
						MobclickAgent.onEvent(mActivity, "3");//统计发送的个数
						
						mMediaHelper.launchMulPicker(Constants.MAX_PHOTO_PER_POST);
					}
				}.StarPostEdit(mActivity);
			}
        });
    }

    public View getTodayWriteView(){
    	return mTodayWriteView;
    }
    
	@Override
	public void onMediaFileDone(String[] mediaFilePaths) {
		
		openEditPostActivity(mActivity,mediaFilePaths,false);
	}
	
	/**
	 * 预置图片后打开日记页
	 */
	public static void openEditPostActivity(final Activity activity,final String[] mediaFilePaths,final boolean comeByFriendOrNeighbor) {
		new StarEdit() {
			@Override
			public void todo() {
				// TODO Auto-generated method stub
				if(mediaFilePaths.length>0){
					Intent intent = new Intent(activity, EditPostActivity.class);
					intent.putExtra("initImageFileUrls", StringUtils.getFromArray(mediaFilePaths, "|"));

					long lngDatetime=MediaHelper.getDatetimeFromURI(mediaFilePaths[0]);
					intent.putExtra("firstImageDatetime", lngDatetime);
					
					long lngLastDatetime=lngDatetime;
					for(int i=1;i<mediaFilePaths.length-1;i++){
						long t=MediaHelper.getDatetimeFromURI(mediaFilePaths[i]);
						if(t>lngLastDatetime){
							lngLastDatetime=t;
						}
					}
					intent.putExtra("photosLastDatetime", lngLastDatetime);
					intent.putExtra("comeByFriendOrNeighbor", comeByFriendOrNeighbor);		
					activity.startActivityForResult(intent, Constants.RequestCode.ACTIVITY_REQUEST_CODE_EDIT_POST_REQUEST);
				}
			}
		}.StarPostEdit(mActivity);
		
	}
}
