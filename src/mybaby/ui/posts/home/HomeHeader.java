package mybaby.ui.posts.home;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.User;
import mybaby.models.person.Baby;
import mybaby.models.person.FamilyPerson;
import mybaby.models.person.Person;
import mybaby.models.person.SelfPerson;
import mybaby.ui.MediaHelper;
import mybaby.ui.MediaHelper.MediaHelperCallback;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.customclass.CusHorScrollView;
import mybaby.ui.community.holder.HtmlItem;
import mybaby.ui.posts.person.PersonAvatar;
import mybaby.ui.posts.person.PersonTimelineActivity;
import mybaby.ui.user.PersonEditActivity;
import mybaby.util.MaterialDialogUtil;


public class HomeHeader implements MediaHelperCallback {
	Person selfPerson;
	Activity activity;
    MediaHelper mMediaHelper;
    PersonAvatar mCameraTouchOnPersonAvatar;



    public HomeHeader(final Activity activity ){
		this.activity=activity;

		mMediaHelper=new MediaHelper(activity,this);
        }

	public View setHeadData( final RecyclerView listView,
							final User user,SelfPerson selfPerson, final Baby[] babies, final FamilyPerson[] familyPersons) {
		FrameLayout mHeaderView= (FrameLayout) activity.getLayoutInflater().inflate(R.layout.home_timeline_header, null);
		FrameLayout headerContainer = (FrameLayout) mHeaderView.findViewById(R.id.home_timeline_header_container);
		final ImageView bgImageView = (ImageView) mHeaderView.findViewById(R.id.home_timeline_header_background);
		LinearLayout headerContent = (LinearLayout) mHeaderView.findViewById(R.id.home_timeline_header_content);
		final CusHorScrollView scrollView = (CusHorScrollView) mHeaderView.findViewById(R.id.home_timeline_header_content_scroll);
		//scrollView.isNestedScrollingEnabled()
		scrollView.setListener(new HtmlItem.SetWebViewOnTouchListener() {
			@Override
			public void onLeftOrRight(boolean disallowInterceptTouchEvent) {
				if (1 + babies.length + familyPersons.length>3)
					listView.requestDisallowInterceptTouchEvent(!disallowInterceptTouchEvent);
			}
		});
		TextView addPersonButton = (TextView) mHeaderView.findViewById(R.id.home_timeline_header_add_person);
		createAddPerson(user, addPersonButton, activity);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MyBabyApp.screenWidth, (int) (MyBabyApp.screenWidth * 0.618));
		headerContainer.setLayoutParams(layoutParams);
		headerContainer.requestLayout();
		bgImageView.requestLayout();

		this.selfPerson=selfPerson;
		HomeTimelineFragment.setHeaderBackgroundImage(bgImageView, selfPerson);
		if (user.isSelf()) {
			//因为被这个空间覆盖了，所以要将点击事件设置在他上面
			headerContent.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mCameraTouchOnPersonAvatar = null;
					mMediaHelper.launchPictureLibraryAndCameraMenu(bgImageView, true);
				}
			});
		}
		headerContent.bringToFront();
		final Person[] persons = new Person[1 + babies.length + familyPersons.length];
		if (persons.length == 3 && babies.length == 2) {//����+�Լ�+����
			persons[0] = babies[0];
			persons[1] = selfPerson;
			persons[2] = babies[1];
		} else {
			if (persons.length > 0) {
				persons[0] = selfPerson;
				for (int i = 0; i < babies.length; i++) {
					persons[i + 1] = babies[i];
				}
				for (int i = 0; i < familyPersons.length; i++) {
					persons[i + 1 + babies.length] = familyPersons[i];
				}
			} else return mHeaderView;
		}

		//�ߴ�
		int avatarWidth = MyBabyApp.screenWidth / 5;
		int smallAvatarWidth = (int) (avatarWidth * 0.8);
		int leftSpacing;
		if (persons.length == 2) {
			leftSpacing = (MyBabyApp.screenWidth - avatarWidth * 2) / 4;
		} else {
			leftSpacing = (MyBabyApp.screenWidth - avatarWidth - smallAvatarWidth * 2) / 6;
		}
		int avatarSpacing = 2 * leftSpacing;

		//����
		for (int i = 0; i < persons.length; i++) {
			int width;

			if (persons.length == 2) {
				width = avatarWidth;
			} else if (persons.length == 3) {
				if (i == 1) {
					width = avatarWidth;
				} else {
					width = smallAvatarWidth;
				}
			} else {
				if (persons[i].getClass() == Baby.class) {
					width = avatarWidth;
				} else {
					width = smallAvatarWidth;
				}
			}

			FrameLayout avatarContainer = new FrameLayout(activity);

			LinearLayout.LayoutParams lp_container = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp_container.leftMargin = i == 0 ? leftSpacing : avatarSpacing;
			lp_container.rightMargin = i == persons.length - 1 ? leftSpacing : 0;
			avatarContainer.setLayoutParams(lp_container);

			headerContent.addView(avatarContainer);

			final PersonAvatar personAvatar = new PersonAvatar(activity, avatarContainer, user, persons[i], true,
					user.isSelf() ? PersonAvatar.CameraShowType.nullAvatar : PersonAvatar.CameraShowType.none);
			personAvatar.setAvatarOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity, PersonTimelineActivity.class);
					intent.putExtra("userId", user.getUserId());
					intent.putExtra("personId", personAvatar.getPerson().getId());
					activity.startActivity(intent);
				}
			});
			personAvatar.setCameraOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mCameraTouchOnPersonAvatar = personAvatar;
					mMediaHelper.launchPictureLibrary(true);
				}
			});
		}
		return mHeaderView;
	}


	@Override
	public void onMediaFileDone(String[] mediaFilePaths) {
		if(mediaFilePaths.length>0){
			Person person;
			
			if(mCameraTouchOnPersonAvatar != null){ //设置头像
				//mCameraTouchOnPersonAvatar.setNewAvatar(mediaFilePaths[0]);  广播通知会更新显示的
				person=mCameraTouchOnPersonAvatar.getPerson();
				person.setAvatar(mediaFilePaths[0]);
			}else{//设置背景
				person=selfPerson;
				person.setBackground(mediaFilePaths[0]);
			}
			
			//广播
	        Intent bi = new Intent();
	        bi.setAction(Constants.BroadcastAction.BroadcastAction_Person_Edit);
	        bi.putExtra("id", person.getId());
	        LocalBroadcastManager.getInstance(MyBabyApp.getContext()).sendBroadcast(bi);
		}
	}
    
    private void createAddPerson(User user,TextView addPersonButton,final Activity activity){
        if(!user.isSelf()){
        	addPersonButton.setVisibility(View.GONE);
        	return;
        }
        
        addPersonButton.setTypeface(MyBabyApp.fontAwesome);
        addPersonButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final String[] items = {activity.getString(R.string.cover), activity.getString(R.string.baby), activity.getString(R.string.my_family)};

					MaterialDialogUtil.showListDialog(activity, "添加", items, new MaterialDialogUtil.DialogListListener() {
						@Override
						public void todosomething(int position) {
							if (position == 0) {  //add cover
								mCameraTouchOnPersonAvatar = null;
								mMediaHelper.launchPictureLibrary(true);
							} else if (position == 1) {  //add baby
								Intent intent = new Intent(activity, PersonEditActivity.class);
								intent.putExtra("isBaby", true);
								activity.startActivity(intent);
							} else if (position == 2) { //add family
								Intent intent = new Intent(activity, PersonEditActivity.class);
								intent.putExtra("isBaby", false);
								activity.startActivity(intent);
							}
						}
					});
				}
	});
    }
	
}
