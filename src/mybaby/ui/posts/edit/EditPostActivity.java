package mybaby.ui.posts.edit;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.community.Place;
import mybaby.models.community.PlaceRepository;
import mybaby.models.community.UserPlaceSetting;
import mybaby.models.diary.Media;
import mybaby.models.diary.MediaRepository;
import mybaby.models.diary.MediaRepository.MediasCreateDone;
import mybaby.models.diary.Post;
import mybaby.models.diary.Post.privacyType;
import mybaby.models.diary.PostRepository;
import mybaby.models.diary.PostTag;
import mybaby.models.diary.PostTagRepository;
import mybaby.models.person.Person;
import mybaby.ui.MediaHelper;
import mybaby.ui.MediaHelper.MediaHelperCallback;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.more.PlaceSettingActivity;
import mybaby.ui.posts.home.HomeTimelineFragment;
import mybaby.ui.posts.home.NewPhotos;
import mybaby.util.DateUtils;
import mybaby.util.DialogShow;
import mybaby.util.DialogShow.PlaceSettingClick;
import mybaby.util.ImageAddressUtil;
import mybaby.util.ImageAddressUtil.ImageAddressListener;
import mybaby.util.ImageViewUtil;
import mybaby.util.MaterialDialogUtil;
import mybaby.util.PopupWindowUtils;
import mybaby.util.PopupWindowUtils.MyButtonListener;
import mybaby.util.Utils;

@SuppressLint("ResourceAsColor")
public class EditPostActivity extends Activity implements MediaHelperCallback {

	private Post mPost;
	// Used to restore post content if 'Discard' is chosen when leaving the
	// editor.

	private Post mOriginalPost;
	private PostTag[] mOriginalPostTags;
	//private List<ImageView> imageViewList;

	private ScrollView post_scroll;
	private TextView mAddPictureButton;
	private EditText mContentEditText;
	private FrameLayout mMediaContainer;
	private RelativeLayout rl_pubDate;
	private TextView mPubDateIcon;
	private TextView mPubDateText;
	private TextView tv_enter_pubDate;
	private RelativeLayout rl_privacyType;
	private TextView mPrivacyTypeIcon;
	private TextView mPrivacyTypeText;
	private TextView tv_enter_privacyType;
	private RelativeLayout rl_tags;
	private TextView mTagsText;
	private TextView mTagsIcon;
	private TextView tv_enter_tags;
	private ProgressBar pb_loading;

	private RelativeLayout showplace;
	private TextView placename;
	private TextView mapMarker;
	private TextView tv_enter_place;

	private boolean mIsNew = false;

	private java.text.DateFormat mDateFormat;

	private MediaHelper mMediaHelper;
	private long mLngPhotosLastDatetime = 0;

	private ProgressDialog mProgressDialog;
	private MyBroadcastReceiver mBroadcastReceiver;
	private String[] mCreateMediasDone_mediaFilePaths;
	private Media[] mCreateMediasDone_medias;
	private ArrayList<Integer> prepareDelIdList =new ArrayList<>();//预删除
	private ArrayList<Integer> prepareAddIdList =new ArrayList<>();//预添加
	private ArrayList<Integer> draftIdList=new ArrayList<>();//草稿列表
	private ArrayList<Integer> normalIdList=new ArrayList<>();
	//private List<Integer> filterIdList =new ArrayList<>();
	private int placeObjId;
	private Place place;

	//private ArrayList<Media> mediaList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 注册广播接收
		mBroadcastReceiver = new MyBroadcastReceiver();
		mBroadcastReceiver.registerMyReceiver();
		initActionBar("日记", "保存");
		initControls();
		initData();
		setTipCount(MyBabyApp.getSharedPreferences().getInt("tipCount", 0));
	}

	public static void setTipCount(int tipCount) {
		Editor edit = MyBabyApp.getSharedPreferences().edit();
		edit.putInt("tipCount", tipCount);
		edit.commit();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("写日记"); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("写日记");
		MobclickAgent.onResume(this); // 统计时长
	}

	@SuppressLint("NewApi")
	private void initControls() {

		setContentView(R.layout.activity_post_edit);

		post_scroll=(ScrollView)findViewById(R.id.post_scroll);
		showplace = (RelativeLayout) findViewById(R.id.showplace);
		placename = (TextView) findViewById(R.id.placename);
		mapMarker = (TextView) findViewById(R.id.map_marker);
		tv_enter_place = (TextView) findViewById(R.id.tv_enter_place);
		mContentEditText = (EditText) findViewById(R.id.post_content);
		mMediaContainer = (FrameLayout) findViewById(R.id.mediaList);
		mAddPictureButton = (TextView) findViewById(R.id.addPictureButton);
		rl_pubDate = (RelativeLayout) findViewById(R.id.rl_pubDate);
		tv_enter_pubDate = (TextView) findViewById(R.id.tv_enter_pubDate);
		mPubDateText = (TextView) findViewById(R.id.pubDate);
		mPubDateIcon = (TextView) findViewById(R.id.pubDateIcon);
		rl_privacyType = (RelativeLayout) findViewById(R.id.rl_privacyType);
		tv_enter_privacyType = (TextView) findViewById(R.id.tv_enter_privacyType);
		mPrivacyTypeIcon = (TextView) findViewById(R.id.privacyTypeIcon);
		mPrivacyTypeText = (TextView) findViewById(R.id.privacyTypeDate);
		rl_tags = (RelativeLayout) findViewById(R.id.rl_tags);
		tv_enter_tags = (TextView) findViewById(R.id.tv_enter_tags);
		mTagsText = (TextView) findViewById(R.id.tags);
		mTagsIcon = (TextView) findViewById(R.id.tagsIcon);
		pb_loading = (ProgressBar) findViewById(R.id.pb_loading);

		mAddPictureButton.setTypeface(MyBabyApp.fontAwesome);
		mPubDateIcon.setTypeface(MyBabyApp.fontAwesome);
		tv_enter_pubDate.setTypeface(MyBabyApp.fontAwesome);
		mPrivacyTypeIcon.setTypeface(MyBabyApp.fontAwesome);
		tv_enter_privacyType.setTypeface(MyBabyApp.fontAwesome);
		mTagsIcon.setTypeface(MyBabyApp.fontAwesome);
		tv_enter_tags.setTypeface(MyBabyApp.fontAwesome);
		mapMarker.setTypeface(MyBabyApp.fontAwesome);
		tv_enter_place.setTypeface(MyBabyApp.fontAwesome);

		OverScrollDecoratorHelper.setUpOverScroll(post_scroll);

		mDateFormat = DateFormat.getDateFormat(this);
		mMediaHelper = new MediaHelper(this, this);

		if (MyBabyApp.currentUser.isAdmin())
			mContentEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.MAX_VALUE)});

		mAddPictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMediaHelper.launchMulPicker(Constants.MAX_PHOTO_PER_POST
						- imageCount());
			}
		});
		rl_pubDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 统计点击次数
				MobclickAgent.onEvent(EditPostActivity.this, "8");// 统计发送的个数

				showDateDialog();
			}
		});
		rl_privacyType.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// 统计点击次数
				MobclickAgent.onEvent(EditPostActivity.this, "9");// 统计发送的个数
//				if (place != null && place.getPlace_name() != null) {
//					String text = "设置了地点的记录是公开的，清除地点后才能修改";
//					PopupWindowUtils.showPopupWindow(view,
//							EditPostActivity.this, text,
//							MyBabyApp.screenWidth / 3, MyBabyApp.dip2px(110));
//				} else {
					Intent intent = new Intent(EditPostActivity.this,
							LabelActivity.class);
					startActivityForResult(intent,Constants.RequestCode.ACTIVITY_REQUEST_CODE_SETLABEL);
					// showPrivacyTypeDialog();
//				}
			}
		});
		rl_tags.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 统计点击次数
				MobclickAgent.onEvent(EditPostActivity.this, "10");// 统计发送的个数

				showTagsDialog();
			}
		});
		showplace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 统计点击次数
				MobclickAgent.onEvent(EditPostActivity.this, "7");// 统计发送的个数

				if (place != null && place.getPlace_name() != null) {
					showPlaceDialog();
				} else {
					startPlaceSettingActivity(new LatLonPoint(0, 0));
				}
			}
		});
	}

	private void showPlaceDialog() {
		DialogShow dialogShow = new DialogShow(this);
		dialogShow.showEditPostPlaceDialog(new PlaceSettingClick() {

			@Override
			public void delete(UserPlaceSetting placeSetting) {
				place = null;
				placeObjId = 0;
				mPost.setPlaceObjId(placeObjId);
				placename.setText("你在哪儿？");
				mapMarker.setTextColor(getResources().getColor(R.color.mainThemeColor));
			}

			@Override
			public void change(UserPlaceSetting placeSetting) {
				startPlaceSettingActivity(new LatLonPoint(place.getLatitude(),
						place.getLongitude()));
			}
		});
	}

	public void startPlaceSettingActivity(LatLonPoint lp) {
		Intent intent = new Intent(EditPostActivity.this,
				PlaceSettingActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("CommunityPlaceSettingItem", null);
		bundle.putDouble("Latitude", lp.getLatitude());
		bundle.putDouble("Longitude", lp.getLongitude());
		intent.putExtras(bundle);
		startActivityForResult(intent,
				Constants.RequestCode.ACTIVITY_REQUEST_CODE_SHOWPLACE);
	}

	@SuppressLint("NewApi")
	private void initData() {
		Bundle extras = getIntent().getExtras();
		int id = 0;
		int personId = 0;
		String initImageFileUrls = null;
		long firstImageDatetime = 0;
		if (extras != null) {
			id = extras.getInt("id", 0);
			personId = extras.getInt("personId", 0);
			initImageFileUrls = extras.getString("initImageFileUrls", null);
			firstImageDatetime = extras.getLong("firstImageDatetime", 0);
			mLngPhotosLastDatetime = extras.getLong("photosLastDatetime", 0);
		}

		mIsNew = (id == 0);
		if (!mIsNew) {
			mPost = PostRepository.load(id);
			mOriginalPost = PostRepository.load(id);
			mOriginalPostTags = PostTagRepository.load(id);
			// 设置私密公开
			setPrivacyTypeDisplay(mPost.getPrivacyTypeNumber());
			//setImages(MediaRepository.getForPId(mPost.getId()),firstImageDatetime);
			Media[] draftMedias=MediaRepository.getForPId(mPost.getId());
			if (mPost.getStatus().equals(mPost.Status_Draft)) {//草稿状态
				setImages(new Media[0], firstImageDatetime);
				if (draftMedias!=null&&draftMedias.length>0) {
					for (int i = 0; i < draftMedias.length; i++) {
						draftIdList.add(draftMedias[i].getId());
					}
				}
			}else {
				if (draftMedias!=null&&draftMedias.length>0) {
					for (int i = 0; i < draftMedias.length; i++) {
						normalIdList.add(draftMedias[i].getId());
					}
					prepareAddIdList.addAll(normalIdList);
					setImages(draftMedias, firstImageDatetime);
				}
			}
		} else {
			String[] images;
			if (initImageFileUrls == null) {
				images = new String[0];
			} else {
				images = initImageFileUrls.split("\\|");
			}

			Person[] persons;
			if (personId > 0) {
				persons = new Person[] { PostRepository.load(personId)
						.createPerson() };
			} else {
				persons = PostRepository.loadBabies(MyBabyApp.currentUser
						.getUserId());
			}

			mPost = PostRepository.createDiary(null, null,
					System.currentTimeMillis(),
					MyBabyApp.currentUser.getFrdPrivacyTypeNumber(), persons);
			setImages(new Media[0], firstImageDatetime);
			onMediaFileDone(images);// 异步创建照片
			setPrivacyTypeDisplay(privacyType.Private.ordinal());
		}

		// ��ʾ��������
		if (mPost.getStatus().equals(Post.Status_Draft)&&(!mIsNew)) {//如果是草稿的话
			mContentEditText.setText("");
			mContentEditText.setHint(mPost.getDescription());//默认文字//判断是否草稿
		}else mContentEditText.setText(mPost.getDescription());
		setPubDateDisplay(mPost.getDateCreated());
		setPrivacyTypeDisplay(mPost.getPrivacyTypeNumber());
		if (mPost.getPlaceObjId() != 0) {
			place = PlaceRepository.load(mPost.getPlaceObjId());
		}
		if (place != null && place.getPlace_name() != null) {
			placename.setText(place.getPlace_name());
//			setPrivacyTypeDisplay(Post.privacyType.Public.ordinal());
		}
		setTagsDisplay(mPost.getId());

	}

	@SuppressLint("NewApi")
	private void initActionBar(String title, String rightText) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.actionbar_topic_edit, null);
		((TextView) view.findViewById(R.id.actionbar_title)).setText(title);
		((TextView) view.findViewById(R.id.sendtopic)).setText(rightText);
		((TextView) view.findViewById(R.id.actionbar_back))
				.setTypeface(MyBabyApp.fontAwesome);
		view.findViewById(R.id.actionbar_back)
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						onBackPressed();
					}
				});
		view.findViewById(R.id.sendtopic).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mPost.getStatus().equals(Post.Status_Draft) && !mIsNew) {//草稿必须要写内容/而且必须不是新建的状态
					if (mContentEditText.getText().toString().equals("")) {
						Toast.makeText(EditPostActivity.this,
								R.string.please_fill_out_all_the_fields, Toast.LENGTH_SHORT)
								.show();
					} else {
						savePost();
					}
				} else {
					savePost();
				}
			}
		});
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(view);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mBroadcastReceiver != null)
			LocalBroadcastManager.getInstance(MyBabyApp.getContext())
					.unregisterReceiver(mBroadcastReceiver);
	}

	@Override
	public void onBackPressed() {
		showCancelAlert(false);
	}

	private void showCancelAlert(final boolean isUpPress) {
		if (mIsNew && mContentEditText.getText().toString().equals("")
				&& imageCount() == 0) {
			PostRepository.deletePost(mPost);
			draftIdList.clear();
			prepareDelIdList.clear();
			finish();
			return;
		}

		MaterialDialogUtil.showCommDialog(EditPostActivity.this, "确定返回吗？", null, null, new MaterialDialogUtil.DialogCommListener() {
			@Override
			public void todosomething() {
				for (int prepareDelId : prepareAddIdList) {
					boolean has = false;
					for (int id : normalIdList) {
						if (prepareDelId==id){
							has=true;
							break;
						}
					}
					if (!has)
						removeMediaId(prepareDelId);
				}

				if (mIsNew) {
					PostRepository.deletePost(mPost);
				} else {
					PostRepository.save(mOriginalPost);
					PostTagRepository.save(mOriginalPostTags);
					Bundle bundle = new Bundle();
					bundle.putInt("id", mPost.getId());
					MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_Post_Edit, bundle);
				}
				finish();
			}
		});
	}
	/*public void onEventMainThread(CustomEventPicture event) {

		String msg = "onEventMainThread收到了删除图片的消息：" + event.getDeleteMediaFileId();
		Log.d("harvic", msg);
		if (event.getDeleteMediaFileId()<0)
			return;
		removeImageForId(event.getDeleteMediaFileId());
	}*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// requestCode = requestCode & 0xffff;//android bug:
		// https://groups.google.com/forum/#!msg/android-developers/NiM_dAOtXQU/ufi_FYqMGxIJ
		super.onActivityResult(requestCode, resultCode, data);

		mMediaHelper.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_CANCELED)
			return;

		switch (requestCode) {
		case Constants.RequestCode.ACTIVITY_REQUEST_CODE_GALLERY_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				int mfId = data.getIntExtra("deleteMediaFileId", 0);
				if (mfId > 0) {
					removeImageForId(mfId);
				}
			}
			break;
		case Constants.RequestCode.ACTIVITY_REQUEST_CODE_SHOWPLACE:
			if (resultCode == Activity.RESULT_OK) {
//				if (MyBabyApp.getSharedPreferences().getInt("tipCount", 0) == 0) {
//					setTipCount(1);
//					String text = "设置了地点的记录是公开的，如果涉及隐私内容请清除地点后修改记录状态为“私密”";
//					PopupWindowUtils.showPopupWindow(rl_privacyType,
//							EditPostActivity.this, text, MyBabyApp.dip2px(5),
//							MyBabyApp.dip2px(130));
//				}
				place = (Place) data.getExtras().get("place");
				placeObjId = place.getObjId();
				placename.setText(place.getPlace_name());

//				setPrivacyTypeDisplay(Post.privacyType.Public.ordinal());
				mapMarker.setTextColor(getResources().getColor(R.color.gray_1));
//				mPrivacyTypeIcon.setTextColor(getResources().getColor(R.color.mainThemeColor));
			}
			break;
		case Constants.RequestCode.ACTIVITY_REQUEST_CODE_SETLABEL:
			if (resultCode == Activity.RESULT_OK) {
				int privacyType = data.getExtras().getInt("privacyType");
				setPrivacyTypeDisplay(privacyType);
			}
			break;
		}
	}

	private void savePost() {
		String content = mContentEditText.getText().toString();

		if (content.equals("") && imageCount() == 0) {
			Toast.makeText(EditPostActivity.this,
					R.string.please_fill_out_all_the_fields, Toast.LENGTH_SHORT)
					.show();
		} else {
			mPost.setDescription(content);
			if (mPost.getStatus().equals(mPost.Status_Draft)) {//草稿状态
				for (int prepareDelId : draftIdList) {
					removeMediaId(prepareDelId);
				}
			}
			for (int id:prepareDelIdList)
				removeMediaId(id);//去掉删掉的

			if (mPost.getPrivacyTypeNumber() == privacyType.Private.ordinal()) {
				mPost.setStatus(Post.Status_Private);
			} else {
				mPost.setStatus(Post.Status_Publish);
			}
			mPost.setRemoteSyncFlag(Post.remoteSync.LocalModified.ordinal());
			if (placeObjId != 0) {
				mPost.setPlaceObjId(placeObjId);
//				setPrivacyTypeDisplay(Post.privacyType.Public.ordinal());
			}
			PostRepository.save(mPost);

			NewPhotos.setPhotosLastDatetime(mLngPhotosLastDatetime);

			Bundle bundle=new Bundle();
			bundle.putInt("id", mPost.getId());
			MyBabyApp.sendLocalBroadCast(mIsNew?Constants.BroadcastAction.BroadcastAction_Post_Add:Constants.BroadcastAction.BroadcastAction_Post_Edit, bundle);
			
			// 日记发布，在社区中显示图片上传进度
			if (getIntent().getBooleanExtra("comeByFriendOrNeighbor", false)) {
				HomeTimelineFragment.needRefush = true;// 将主页刷新标签设为需要刷新
				Intent intent = new Intent();
				intent.setAction(Constants.BroadcastAction.BroadcastAction_Post_Media_Uploding);
				intent.putExtra("postId", mPost.getId());
				intent.putExtra("needRefush", TextUtils.isEmpty(mPost.getDescription()) ? false : true);
				LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
			}

			finish();
		}

	}

	private void showDateDialog() {
		final Calendar cal = Calendar.getInstance();
		DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.MONTH, monthOfYear);
				cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

				// if (cal.getTimeInMillis() > System.currentTimeMillis()) {
				// cal.setTimeInMillis(System.currentTimeMillis());
				// }
				mPost.setDateCreated(cal.getTimeInMillis());
				mPubDateText.setText(mDateFormat.format(cal.getTime()));
				mPubDateIcon.setTextColor(getResources().getColor(R.color.gray_1));

			}
		};

		try {
			Date date = mDateFormat.parse(mPubDateText.getText().toString());
			cal.setTime(date);
		} catch (Exception e) {

		}

		DatePickerDialog datePickDialog = new DatePickerDialog(this,
				mDateSetListener, cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		datePickDialog.getDatePicker().setMaxDate(System.currentTimeMillis());// 设置时间的范围，最大时期为当前日期
		// datePickDialog.getDatePicker().setMinDate(minDate);
		datePickDialog.setCanceledOnTouchOutside(true);
		datePickDialog.show();
	}

	private void showTagsDialog() {
		final Person[] persons = PostRepository.loadPersons(MyBabyApp.currentUser
				.getUserId());

		final boolean[] tagsCheck = new boolean[persons.length];
		final String[] items = new String[persons.length];

		PostTag[] postTags = PostTagRepository.load(mPost.getId());

		for (int i = 0; i < persons.length; i++) {
			items[i] = persons[i].getName();
			tagsCheck[i] = false;
			for (int j = 0; j < postTags.length; j++) {
				if (postTags[j].getTagPid() == persons[i].getId()) {
					tagsCheck[i] = true;
					break;
				}
			}
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this).setCancelable(true);
		builder.setTitle(R.string.tags);
		builder.setMultiChoiceItems(items, tagsCheck,
				new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton, boolean isChecked) {
						tagsCheck[whichButton] = isChecked;
					}
				});
		builder.setPositiveButton(getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						PostTagRepository.delete(mPost.getId());
						for (int i = 0; i < tagsCheck.length; i++) {
							if (tagsCheck[i]) {
								PostTagRepository.save(new PostTag(mPost
										.getId(), persons[i].getId()));
							}
						}
						setTagsDisplay(mPost.getId());
					}
				});
		builder.setNegativeButton(getString(R.string.cancel), null);
		Dialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
	}

	@Override
	public void onMediaFileDone(final String[] mediaFilePaths) {
		mProgressDialog = ProgressDialog.show(this, null, null);
		//mCreateMediasDone_mediaFilePaths = mediaFilePaths;
		//MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_Edit_Post_Create_Medias_Done);
		MediaRepository.asyncCreateMedias(mPost.getId(), mediaFilePaths,
				new MediasCreateDone() {
					@Override
					public void onDone(Media[] medias) {
						mCreateMediasDone_mediaFilePaths = mediaFilePaths;
						mCreateMediasDone_medias = medias;
						MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_Edit_Post_Create_Medias_Done);
					}
				});
	}

	private void mediasCreateDone() {
		mProgressDialog.dismiss();
		//final ArrayList<MediaBean> list = new ArrayList<MediaBean>();
		if(mCreateMediasDone_mediaFilePaths==null)
			return;
		for (int i = 0; i < mCreateMediasDone_mediaFilePaths.length; i++) {
			long lngDatetime = MediaHelper
					.getDatetimeFromURI(mCreateMediasDone_mediaFilePaths[i]);
			if (lngDatetime > mLngPhotosLastDatetime) {
				mLngPhotosLastDatetime = lngDatetime;
			}
			// TODO
			if (place == null) {
				pb_loading.setVisibility(View.VISIBLE);
			}
			prepareAddIdList.add(mCreateMediasDone_medias[i].getId());
			try {
				setImageAddress(mCreateMediasDone_medias[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			addImageViewForMediaFile(mCreateMediasDone_medias[i], lngDatetime);
			
		}
		
	}

	private void setPubDateDisplay(long lngPubDate) {
		if (lngPubDate == 0)
			lngPubDate = System.currentTimeMillis();
		mPubDateText.setText(mDateFormat.format(lngPubDate));
	}

	private void setPrivacyTypeDisplay(int privacyType) {
		mPost.setPrivacyTypeNumber(privacyType);
		if (privacyType == Post.privacyType.Private.ordinal()) {
			mPrivacyTypeIcon.setText(getString(R.string.fa_globe));
			mPrivacyTypeIcon.setTextColor(getResources().getColor(R.color.gray_1));
			mPrivacyTypeText.setText("谁可以看：只给自己看");
		} else if (privacyType == Post.privacyType.Friends.ordinal()) {
			mPrivacyTypeIcon.setText(getString(R.string.fa_globe));
			mPrivacyTypeIcon.setTextColor(getResources().getColor(R.color.gray_1));
			mPrivacyTypeText.setText("谁可以看：只给好友看");
		} else {
			mPrivacyTypeIcon.setText(getString(R.string.fa_globe));
			mPrivacyTypeText.setText("谁可以看：公开");
			mPrivacyTypeIcon.setTextColor(getResources().getColor(R.color.gray_1));
		}
	}

	private void setTagsDisplay(int pid) {
		String tagsString = "";
		PostTag[] postTags = PostTagRepository.load(pid);
		for (int i = 0; i < postTags.length; i++) {
			Post tag = PostRepository.load(postTags[i].getTagPid());
			if (tag != null) {
				if (tag.getTypeNumber() == Post.type.Baby.ordinal()
						|| tag.getTypeNumber() == Post.type.Person.ordinal()) {
					tagsString += tag.createPerson().getName();
					if (i + 1 < postTags.length) {
						tagsString += ", ";
					}
				}
			}
		}
		mTagsText.setText(tagsString);
	}

	// ////////////////////////////////////////////////////////////////////////////////
	private void setImages(Media[] arrMf, long firstImageDatetime) {
		for (int i = 0; i < arrMf.length; i++) {
			addImageViewForMediaFile(arrMf[i], firstImageDatetime);
		}

		if (arrMf.length == 0) {
			FrameLayout.LayoutParams lp_btn = imageLayoutParams(0);
			mAddPictureButton.setLayoutParams(lp_btn);
		}
	}

	private void addImageViewForMediaFile(final Media mf, long lngDatetime) {
		int index = imageCount();

		FrameLayout.LayoutParams lp = imageLayoutParams(index);

		ImageView imageView = ImageViewUtil.showImageByMediaFile(this, mf, lp);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		//imageView.setTag(mf.getId());
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mf == null)
					return;
				Utils.Loge("图片pId:" + mf.getPid());
				ArrayList<Integer> filters=new ArrayList<Integer>();
				for (int id:draftIdList) {
					filters.add(id);
				}
				for (int id:prepareDelIdList) {
					filters.add(id);
				}
				CustomAbsClass.starImagePage(EditPostActivity.this, mf,filters, 0,true, true, false);
				/*Intent intent = new Intent(MyBabyApp.getContext(),
						PostGallery.class);
				intent.putExtra("pId", mf.getPid());
				intent.putExtra("mediaFileId", mf.getId());
				intent.putExtra("isEditStatus", true);
				startActivityForResult(intent,Constants.RequestCode.ACTIVITY_REQUEST_CODE_GALLERY_REQUEST);
				overridePendingTransition(R.anim.in_imageshow, R.anim.out_imageshow);*/
			}
		});
		addImageLayout(mf, imageView, lp);
		// addPictureButton
		if (index + 1 >= Constants.MAX_PHOTO_PER_POST) {
			FrameLayout.LayoutParams lp_btn = new FrameLayout.LayoutParams(0, 0);
			mAddPictureButton.setLayoutParams(lp_btn);
		} else {
			FrameLayout.LayoutParams lp_btn = imageLayoutParams(index + 1);
			mAddPictureButton.setLayoutParams(lp_btn);
		}

		// 新建时根据第一张照片自动设置日期
		if (mIsNew && imageCount() == 0 && lngDatetime > 0) {
			if (!DateUtils.isSameDate(lngDatetime, mPost.getDateCreated())) {
				mPubDateIcon.setTextColor(getResources().getColor(R.color.mainThemeColor));
			}

			mPost.setDateCreated(lngDatetime);
			mPubDateText.setText(mDateFormat.format(lngDatetime));
		}

	}

	private void addImageLayout(final Media mf, ImageView imageView,
			FrameLayout.LayoutParams lp) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final RelativeLayout imageLayout = (RelativeLayout) inflater.inflate(
				R.layout.photo_delete_header, null);
		LinearLayout  photo_header_badge = (LinearLayout) imageLayout.findViewById(R.id.photo_header_badge);
		imageLayout.setTag(mf.getId());
		imageLayout.addView(imageView);
		photo_header_badge.bringToFront();
		imageLayout.setLayoutParams(lp);
		photo_header_badge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				removeImageForId(mf.getId());
			}
		});
		mMediaContainer.addView(imageLayout);
	}

	private TextView TextView(int photoDeleteHeader) {
		// TODO Auto-generated method stub
		return null;
	}

	private void showImageAddressPopDown(int LayoutId, int y, int styleId) {
		PopupWindowUtils.showImageAddressPop(showplace, EditPostActivity.this,
				EditPostActivity.this, new MyButtonListener() {

					@Override
					public void okButton() {
//						if (MyBabyApp.getSharedPreferences().getInt("tipCount", 0) == 0) {
//							setTipCount(1);
//							String text = "设置了地点的记录是公开的，如果涉及隐私内容请清除地点后修改记录状态为“私密”";
//							PopupWindowUtils.showPopupWindow(rl_privacyType,
//									EditPostActivity.this, text,
//									MyBabyApp.dip2px(5), MyBabyApp.dip2px(130));
//						}
					}

					@Override
					public void changeButton() {
						Intent intent = new Intent(EditPostActivity.this,
								PlaceSettingActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("CommunityPlaceSettingItem",
								null);
						bundle.putDouble("Latitude", place.getLatitude());
						bundle.putDouble("Longitude", place.getLongitude());
						intent.putExtras(bundle);
						startActivityForResult(
								intent,
								Constants.RequestCode.ACTIVITY_REQUEST_CODE_SHOWPLACE);
					}
				}, LayoutId, y, styleId);
	}

	public void setImageAddress(Media mf) {
		ImageAddressUtil addressUtil = new ImageAddressUtil();
		addressUtil.getLatLonPoint(mf.getAssetURL(),
				EditPostActivity.this, new ImageAddressListener() {
					@Override
					public void callback(Place place) {
						if (EditPostActivity.this.place == null
								&& place != null) {
							EditPostActivity.this.place = place;
							placeObjId = place.getObjId();
							pb_loading.setVisibility(View.GONE);
							placename.setText(place.getPlace_name());
//							setPrivacyTypeDisplay(privacyType.Public
//									.ordinal());
							mapMarker.setTextColor(getResources().getColor(R.color.gray_1));
//							mPrivacyTypeIcon.setTextColor(getResources().getColor(R.color.mainThemeColor));
//							if (getShowPlaceLocation(showplace) < MyBabyApp.screenHeight / 2) {
//								showImageAddressPopDown(
//										R.layout.popplace_image_down,
//										MyBabyApp.dip2px(5),
//										R.style.suredownpopwindow_anim_style);
//							} else {
//								showImageAddressPopDown(
//										R.layout.popplace_image_up,
//										-MyBabyApp.dip2px(132),
//										R.style.sureuppopwindow_anim_style);
//							}
						} else if (place == null) {
							pb_loading.setVisibility(View.GONE);
						}
					}
				});
	}

	public static int getShowPlaceLocation(View view) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		return location[1];
	}

	private FrameLayout.LayoutParams imageLayoutParams(int indexOfImages) {
		int ImagesPerLine = 4;
		int imageOffset = Math.round(6 * MyBabyApp.density);

		int x, y, w, h;

		w = (MyBabyApp.screenWidth - imageOffset * (ImagesPerLine + 1))
				/ ImagesPerLine;
		h = w;
		x = imageOffset + (indexOfImages % ImagesPerLine) * (w + imageOffset);
		y = imageOffset + (int) Math.floor(indexOfImages / ImagesPerLine)
				* (h + imageOffset);

		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
		lp.leftMargin = x;
		lp.topMargin = y;
		lp.gravity = Gravity.TOP;// android 2.X

		// android 2.x��Ҫ��ȷ���ø߶ȣ�����ֻ�е�һ��ͼ�ĸ߶�
		ViewGroup.LayoutParams lp_container = mMediaContainer.getLayoutParams();
		lp_container.height = lp.height + lp.topMargin + imageOffset;
		mMediaContainer.setLayoutParams(lp_container);

		return lp;

	}
	private void removeMediaId(int mediaFileId) {

		Media mf = MediaRepository.load(mediaFileId);
		if (mf==null)
			return;
		MediaRepository.delete(mf);

	}

	private void removeImageForId(int mediaFileId) {
		prepareDelIdList.add(mediaFileId);
		//removeMediaId(mediaFileId);

		RelativeLayout at = (RelativeLayout) mMediaContainer
				.findViewWithTag(mediaFileId);
		mMediaContainer.removeView(at);

		int newIndex = 0;
		for (int i = 0; i < mMediaContainer.getChildCount(); i++) {
			if (getMediaFileIdFromImageView(mMediaContainer.getChildAt(i)) > 0) {
				FrameLayout.LayoutParams lp = imageLayoutParams(newIndex);
				mMediaContainer.getChildAt(i).setLayoutParams(lp);
				newIndex++;
			}
		}

		// addPictureButton
		FrameLayout.LayoutParams lp_btn = imageLayoutParams(imageCount());
		mAddPictureButton.setLayoutParams(lp_btn);
	}

	private int imageCount() {
		return mMediaContainer.getChildCount() - 1;
	}

	private int getMediaFileIdFromImageView(View imageView) {
		if (imageView.getTag() == null) {
			return 0;
		} else {
			return Integer.parseInt(imageView.getTag().toString());
		}
	}

	// 广播接收并响应处理
	private class MyBroadcastReceiver extends BroadcastReceiver {
		public void registerMyReceiver() {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Edit_Post_Create_Medias_Done);
			LocalBroadcastManager.getInstance(MyBabyApp.getContext())
					.registerReceiver(this, filter);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent
					.getAction()
					.equals(Constants.BroadcastAction.BroadcastAction_Edit_Post_Create_Medias_Done)) {
				mediasCreateDone();
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int downX = 0, downY = 0, moveX, moveY;
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			downX = (int) ev.getRawX();
			downY = (int) ev.getRawY();
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			moveX = (int) ev.getRawX();
			moveY = (int) ev.getRawY();
			int disX = moveX - downX;
			int disY = moveY - downY;
			if (disY > 40) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				View v = getCurrentFocus();
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}
		return onTouchEvent(ev);
	}

	public boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			return !(event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom);
		}
		return false;
	}

}