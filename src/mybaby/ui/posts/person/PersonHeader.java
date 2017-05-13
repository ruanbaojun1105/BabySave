package mybaby.ui.posts.person;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.User;
import mybaby.models.diary.PostRepository;
import mybaby.models.person.Baby;
import mybaby.models.person.Person;
import mybaby.models.person.SelfPerson;
import mybaby.ui.MediaHelper;
import mybaby.ui.MediaHelper.MediaHelperCallback;
import mybaby.ui.MyBabyApp;
import mybaby.ui.posts.TimelineListFragment;
import mybaby.ui.user.PersonEditActivity;


public class PersonHeader implements MediaHelperCallback {

	Activity activity;

	View mHeaderView;
	User mUser;
	ImageView mBgImageView;
	Person mPerson;

	MediaHelper mMediaHelper;
	PersonAvatar mPersonAvatar;
	boolean mIsSettingCover;

	public PersonHeader(final Activity activity){
		this.activity=activity;
		mMediaHelper=new MediaHelper(activity,this);
	}

	public View setHeadData(User user,Person person){
		mUser=user;
		mPerson=person;
		View mHeaderView=activity.getLayoutInflater().inflate(R.layout.person_timeline_header, null);
		final FrameLayout innerContainer=(FrameLayout)mHeaderView.findViewById(R.id.inner_container);
		mBgImageView=(ImageView)mHeaderView.findViewById(R.id.person_timeline_header_background);
		FrameLayout headerContent=(FrameLayout)mHeaderView.findViewById(R.id.person_timeline_avatar);
		TextView txtDescription=(TextView)mHeaderView.findViewById(R.id.txt_description);

		int avatarWidth=(int) (MyBabyApp.screenWidth/3.6);
		int imageHeight=(int) (avatarWidth*2.2);
		int avatarTopMargin=imageHeight-avatarWidth/2;
		innerContainer.setLayoutParams(new LinearLayout.LayoutParams(MyBabyApp.screenWidth,LinearLayout.LayoutParams.WRAP_CONTENT));
		//背景图
		mBgImageView.setLayoutParams(new FrameLayout.LayoutParams(MyBabyApp.screenWidth,imageHeight));
		TimelineListFragment.setHeaderBackgroundImage(mBgImageView, person);
		if(user.isSelf()){
			mBgImageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mIsSettingCover=true;
					mMediaHelper.launchPictureLibraryAndCameraMenu(v, true);
				}
			});
		}

		//头像
		FrameLayout.LayoutParams lp_container=(FrameLayout.LayoutParams) headerContent.getLayoutParams();
		lp_container.width=avatarWidth;
		lp_container.topMargin=avatarTopMargin;
		headerContent.setLayoutParams(lp_container);

		mPersonAvatar=new PersonAvatar(activity, headerContent, user,person, false,
				mUser.isSelf() ? PersonAvatar.CameraShowType.nullAvatar : PersonAvatar.CameraShowType.none);
		if(mUser.isSelf()){
			mPersonAvatar.setAvatarOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity, PersonEditActivity.class);
					intent.putExtra("personId", mPersonAvatar.getPerson().getId());
					activity.startActivity(intent);
				}
			});
			mPersonAvatar.setCameraOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mIsSettingCover=false;
					mMediaHelper.launchPictureLibrary(true);
				}
			});

		}

		//名字文字描述
		String description=null;
		if(user.isSelf() && person.getClass().equals(SelfPerson.class) ){
			description=activity.getString(R.string.me);
		}else{
			description=person.getName();
		}
		if(person.getClass().equals(Baby.class)){
			String ageText=person.getAgeText(true);
			if(ageText != null){
				description += "  " + ageText;
			}
		}
		txtDescription.setText(description);
		return mHeaderView;
	}

	@Override
	public void onMediaFileDone(String[] mediaFilePaths) {
		if(mediaFilePaths.length>0){
			if(mIsSettingCover){ //设置封面
				mPerson.setBackground(mediaFilePaths[0]);

			}else{ //设置头像
				// mPersonAvatar.setNewAvatar(mediaFilePaths[0]);  广播通知会更新显示的
				mPersonAvatar.getPerson().setAvatar(mediaFilePaths[0]);
				PostRepository.save(mPersonAvatar.getPerson().getData());//保存编辑信息
			}

			//�㲥
			Intent bi = new Intent();
			bi.setAction(Constants.BroadcastAction.BroadcastAction_Person_Edit);
			bi.putExtra("id", mPersonAvatar.getPerson().getId());
			LocalBroadcastManager.getInstance(MyBabyApp.getContext()).sendBroadcast(bi);
		}
	}

	public void setCoverUsePicuureLibrary(){
		mIsSettingCover=true;
		mMediaHelper.launchPictureLibrary(true);
	}

	public View getView(){
		return mHeaderView;
	}
}

