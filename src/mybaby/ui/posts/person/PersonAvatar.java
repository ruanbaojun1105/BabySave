package mybaby.ui.posts.person;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import me.hibb.mybaby.android.R;
import mybaby.models.User;
import mybaby.models.diary.Media;
import mybaby.models.person.Person;
import mybaby.models.person.SelfPerson;
import mybaby.ui.widget.CircleImageView;
import mybaby.util.ImageViewUtil;


public class PersonAvatar{
	CameraShowType mCameraShowType;
	ImageView mAvatarImageView;
	ImageView mCameraImageView;
	ImageView mCameraBgImageView;
	String mNewAvatarFileUrl;
	Person mPerson;

	public ImageView getmCameraImageView() {
		return mCameraImageView;
	}

	public enum CameraShowType{
		none,  //һֱ����ʾ
		already,  //һֱ��ʾ
		nullAvatar //��ͷ��ʱ��ʾ
	}
	public static CircleImageView setImageView(Context context,float sideWidth,int sideColor){
		CircleImageView view= (CircleImageView) LayoutInflater.from(context).inflate(
				R.layout.circle_image, null);
		if (sideColor!=0){
			view.setBorderColorResource(sideColor);
			view.setBorderWidth((int) sideWidth);
		}
		view.setBorderOverlay(true);
		return view;
	}
	public PersonAvatar(Activity activity, FrameLayout container,User user, Person person, boolean includeName,CameraShowType camersShowType){
		init(activity, container, user, person, includeName, camersShowType, 0, 0);
	}
	public PersonAvatar(@Nullable Activity activity, @Nullable FrameLayout container,@Nullable User user, @Nullable Person person,
						boolean includeName,CameraShowType camersShowType,float rect_adius,int sideColor){
		init(activity,container,user,person,includeName,camersShowType,rect_adius,sideColor);
	}

	public void init(Activity activity, FrameLayout container,User user, Person person,
					 boolean includeName,CameraShowType camersShowType,float sideWidth,int sideColor){
		mCameraShowType=camersShowType;
		mPerson=person;

		//ͷ��
		int avatarImage_border_width=1;
		int avatar_container_width=container.getLayoutParams().width;
		int avatar_width=avatar_container_width-2*avatarImage_border_width;

		CircleImageView avatar_border=setImageView(activity,sideWidth,sideColor);
		avatar_border.setLayoutParams(new FrameLayout.LayoutParams(avatar_container_width, avatar_container_width));
		//avatar_border.setBackgroundColor(Color.WHITE);
		container.addView(avatar_border);

		Media mediaAvatar=null;
		if(person==null){//�½���ʱ��
			mAvatarImageView=setImageView(activity,sideWidth,sideColor);
			mAvatarImageView.setLayoutParams(new FrameLayout.LayoutParams(avatar_width,avatar_width));
			mAvatarImageView.setImageResource(R.drawable.avatar);
		}else{
			mediaAvatar = person.getAvatar();
			if(mediaAvatar==null){
				mAvatarImageView=setImageView(activity,sideWidth,sideColor);
				mAvatarImageView.setLayoutParams(new FrameLayout.LayoutParams(avatar_width,avatar_width));
				mAvatarImageView.setImageResource(person.getNullAvatar());
			}else{
				mAvatarImageView= ImageViewUtil.showImageByMediaFile(activity, mediaAvatar, avatar_width,avatar_width,true,sideWidth,sideColor);
			}
		}
		mAvatarImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		//mAvatarImageView.setBackgroundColor(activity.getResources().getColor(R.color.bg_gray));

		container.addView(mAvatarImageView);

		FrameLayout.LayoutParams lp_avatarImage=(FrameLayout.LayoutParams)mAvatarImageView.getLayoutParams();
		lp_avatarImage.leftMargin=avatarImage_border_width;
		lp_avatarImage.topMargin=avatarImage_border_width;
		mAvatarImageView.setLayoutParams(lp_avatarImage);

		//���ͼ��͸������
		mCameraBgImageView=setImageView(activity,sideWidth,sideColor);
		mCameraBgImageView.setLayoutParams(lp_avatarImage);
		mCameraBgImageView.setImageResource(R.drawable.avatar_camera_bg);
		container.addView(mCameraBgImageView);

		//���ͼ��
		int cameraAreaHeight=avatar_width/3;
		int cameraSpacing=cameraAreaHeight/5;
		mCameraImageView=new ImageView(activity);
		FrameLayout.LayoutParams lp_camera=new FrameLayout.LayoutParams(avatar_width,cameraAreaHeight);
		lp_camera.leftMargin=avatarImage_border_width;
		lp_camera.topMargin= (int) (avatar_width-cameraAreaHeight-sideWidth+avatarImage_border_width);
		mCameraImageView.setLayoutParams(lp_camera);
		mCameraImageView.setImageResource(R.drawable.camera_s);
		mCameraImageView.setScaleType(ScaleType.CENTER_INSIDE);
		mCameraImageView.setPadding(cameraSpacing, cameraSpacing, cameraSpacing,cameraSpacing);
		container.addView(mCameraImageView);

		setCameraVisible(mediaAvatar!=null);

		//����
		if(includeName && person != null){
			TextView nameTextView=new TextView(activity);
			nameTextView.setTextColor(Color.WHITE);
			nameTextView.setSingleLine();
			nameTextView.setEllipsize(TextUtils.TruncateAt.END);
			nameTextView.setGravity(Gravity.CENTER_HORIZONTAL);
			nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
			nameTextView.setShadowLayer(1F, 1F,1F, Color.BLACK);
			if(user != null && user.isSelf() && person.getClass().equals(SelfPerson.class) ){
				nameTextView.setText(R.string.me);
			}else{
				nameTextView.setText(person.getName());
			}
			container.addView(nameTextView);

			FrameLayout.LayoutParams lp_name=(FrameLayout.LayoutParams)nameTextView.getLayoutParams();
			lp_name.topMargin=avatar_container_width;
			nameTextView.setLayoutParams(lp_name);
		}
	}

	public void setAvatarOnClickListener(@Nullable View.OnClickListener avatarClickListener){
		mAvatarImageView.setOnClickListener(avatarClickListener);
	}
	
	public void setCameraOnClickListener(@Nullable View.OnClickListener cameraClickListener){
		mCameraImageView.setOnClickListener(cameraClickListener);
	}
	
	public void setNewAvatar(@Nullable String fileUrl){
		mNewAvatarFileUrl=fileUrl;
		//Bitmap bitmap=ImageLoader.getInstance().loadImageSync("file://"+fileUrl);
		mAvatarImageView.setScaleType(ScaleType.CENTER_CROP);
		ImageViewUtil.displayImage("file://"+fileUrl,mAvatarImageView);
		setCameraVisible(true);
	}


	public String getNewAvatar(){
		return mNewAvatarFileUrl;
	}
	
	public Person getPerson(){
		return mPerson;
	}
        
    private void setCameraVisible(boolean hasAvatar){
        if(mCameraShowType==CameraShowType.already 
        		|| (mCameraShowType==CameraShowType.nullAvatar && !hasAvatar)){
        	mCameraImageView.setVisibility(View.VISIBLE);
        	mCameraBgImageView.setVisibility(View.VISIBLE);
        }else{
        	mCameraImageView.setVisibility(View.GONE);
        	mCameraBgImageView.setVisibility(View.GONE);
        }
    }

}
