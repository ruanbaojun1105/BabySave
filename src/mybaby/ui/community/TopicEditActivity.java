package mybaby.ui.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.hibb.mybaby.android.BuildConfig;
import me.hibb.mybaby.android.R;
import mybaby.BaseActivity;
import mybaby.Constants;
import mybaby.models.community.Place;
import mybaby.models.community.Topic;
import mybaby.models.community.TopicCategory;
import mybaby.models.community.UserPlaceSetting;
import mybaby.models.diary.Media;
import mybaby.models.diary.MediaRepository;
import mybaby.models.diary.MediaRepository.MediasCreateDone;
import mybaby.rpc.community.TopicRPC;
import mybaby.rpc.community.TopicRPC.ListCallback;
import mybaby.ui.MediaHelper;
import mybaby.ui.MediaHelper.MediaHelperCallback;
import mybaby.ui.MyBabyApp;
import mybaby.ui.broadcast.PostMediaTask;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.posts.edit.EditPostActivity;
import mybaby.util.DialogShow;
import mybaby.util.DialogShow.DoSomeThingListener;
import mybaby.util.DialogShow.PlaceSettingClick;
import mybaby.util.ImageViewUtil;
import mybaby.util.PopupWindowUtils;
import mybaby.util.PopupWindowUtils.MyButtonListener;
import mybaby.util.Utils;

//话题编辑
@ContentView(R.layout.community_topic_edit)
public class TopicEditActivity extends BaseActivity implements OnClickListener,// OnTouchListener,
		MediaHelperCallback {

	private Context context;
	private MediaHelper mMediaHelper;
	private List<String> listData;

	@ViewInject(R.id.mediaList)
	private FrameLayout mMediaContainer;
	@ViewInject(R.id.addPicture)
	private TextView addPicture;
	@ViewInject(R.id.post_content)
	private EditText post_content;
	@ViewInject(R.id.post_title)
	private EditText post_title;
	//@ViewInject(R.id.topic_category)
	//private TextView topic_category;
	@ViewInject(R.id.place_rela)
	private RelativeLayout placeSet;
	@ViewInject(R.id.placename)
	private TextView placename;
	@ViewInject(R.id.place_pic)
	private TextView place_pic;
	@ViewInject(R.id.icon)
	private TextView icon;
	@ViewInject(R.id.scroll_edit)
	private ScrollView scroll_edit;
	@ViewInject(R.id.scroll_rela)
	private RelativeLayout scroll_rela;
	@ViewInject(R.id.topic_category_lin)
	private LinearLayout topic_category_lin;
	@ViewInject(R.id.loading_place_progressBar)
	private ProgressBar loading_place_progressBar;
	private TextView actionbar_title;
	private int PARENT_ID = 0;
	private Place place ;
	private TopicCategory topicCategory;

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Constants.hasTipReUpImage=false;//初始化“重试上传”标签值
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("发帖"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长

		// 如此解决
		/*scroll_edit.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				scroll_edit.scrollTo(0, post_title.getHeight());
				post_title.clearFocus();
			}
		});*/
		post_title.clearFocus();
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("发帖"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
										// onPageEnd 在onPause 之前调用,因为 onPause
										// 中会保存信息
		MobclickAgent.onPause(this);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		View view = LayoutInflater.from(this).inflate(
				R.layout.actionbar_topic_edit, null);
		actionbar_title=(TextView) view.findViewById(R.id.actionbar_title);
		actionbar_title.setText("发帖");
		((TextView) view.findViewById(R.id.actionbar_back))
				.setTypeface(MyBabyApp.fontAwesome);
		view.findViewById(R.id.actionbar_back)
				.setOnClickListener(this);
		view.findViewById(R.id.sendtopic).setOnClickListener(this);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(view);

		initData();
		OverScrollDecoratorHelper.setUpOverScroll(scroll_edit);
	}

	private int TopicCategory_id = 0;
	private String TopicCategory_title = "";

	@SuppressLint("NewApi")
	private void initData() {

		mMediaHelper = new MediaHelper(this, this);

		//new InternetChangeReceiver().registerDateTransReceiver();

		addPicture.setOnClickListener(this);

		place_pic.setTypeface(MyBabyApp.fontAwesome);

		place_pic.setText(R.string.fa_map_marker);

		icon.setTypeface(MyBabyApp.fontAwesome);

		addPicture.setTypeface(MyBabyApp.fontAwesome);

		icon.setText(R.string.fa_angle_right);

		if (MyBabyApp.currentUser.isAdmin())
			post_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.MAX_VALUE)});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			TopicCategory_title = extras.getString("TopicCategory_title", "");
			if (!TopicCategory_title.equals("")) {
				TopicCategory_id = extras.getInt("TopicCategory_id", 0);
				actionbar_title.setText(TopicCategory_title);
				actionbar_title.setTag(TopicCategory_id);
				topicCategory = new TopicCategory();
				topicCategory.setTitle(TopicCategory_title.substring(1));
				topicCategory.setCategoryId(TopicCategory_id);
				topicCategory.setId(TopicCategory_id);
			} else {
				actionbar_title.setText("发帖");
			}

			if (extras.getSerializable("place") != null) {
				place = (Place) extras.getSerializable("place");
				Utils.Loge(place.getMap().toString());
				placename.setText(place.getPlace_name());
				// placename.setTextColor(getResources().getColor(R.color.blue));
				// place_pic.setTextColor(getResources().getColor(R.color.blue));
			}

			if (extras.getSerializable("listData") != null) {
				listData = new ArrayList<String>();
				listData = (List<String>) extras.getSerializable("listData");
			}
		}

		FrameLayout.LayoutParams lp_btn = imageLayoutParams(0);
		addPicture.setLayoutParams(lp_btn);

		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				LayoutParams.FILL_PARENT, MyBabyApp.screenHeight
						+ post_title.getHeight());
		scroll_rela.setLayoutParams(layoutParams);

		if (listData != null && listData.size() > 0) {
			mediasCreateDone(listData);
		} else {
			listData = new ArrayList<String>();
		}
		
		placeSet.setOnClickListener(this);
		// post_content.setOnTouchListener(this); //
		// 解决scrollView中嵌套EditText导致不能上下滑动的问题

	}

	/*
	 * @Override public boolean onTouch(View v, MotionEvent event) { switch
	 * (v.getId()) { case R.id.post_content: //
	 * 解决scrollView中嵌套EditText导致不能上下滑动的问题
	 * //v.getParent().requestDisallowInterceptTouchEvent(true); switch
	 * (event.getAction() & MotionEvent.ACTION_MASK) { case
	 * MotionEvent.ACTION_UP:
	 * v.getParent().requestDisallowInterceptTouchEvent(false); break; } }
	 * return false; }
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (imageCount() > 0 || post_content.getText().length() > 0) {
				new DialogShow.DisLikeDialog(context, "编辑的内容将不会保存，确定吗？", "确定",
						"取消", new DoSomeThingListener() {

							@Override
							public void todo() {
								// TODO Auto-generated method stub
								onBackPressed();
							}
						}, null);
			} else {
				onBackPressed();
			}
		}
		return false;

	}

	/**
	 * 添加图片
	 */
	@Event({ R.id.place_rela})
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.actionbar_back:
			if (imageCount() > 0 || post_content.getText().length() > 0) {
				new DialogShow.DisLikeDialog(context, "编辑的内容将不会保存，确定吗？", "确定",
						"取消", new DoSomeThingListener() {

							@Override
							public void todo() {
								// TODO Auto-generated method stub
								onBackPressed();
							}
						}, null);
			} else {
				onBackPressed();
			}
			break;
		case R.id.addPicture:
			mMediaHelper.launchMulPicker(Constants.TOPIC_MAX_PHOTO_PER_POST
					- imageCount());
			break;
		case R.id.place_rela:
			// 统计点击次数
			MobclickAgent.onEvent(context, "20");// 发帖点击地点
			if (TextUtils.isEmpty(placename.getText().toString())) {
				CustomAbsClass.startPlaceSetting((Activity) context, null,place==null?0:place.getLatitude(),place==null?0:place.getLongitude());
			} else {
				new DialogShow(this)
						.showEditPostPlaceDialog(new PlaceSettingClick() {

							@Override
							public void delete(UserPlaceSetting placeSetting) {
								// TODO Auto-generated method stub
								place = null;
								placename.setText("");
								placename.setTextColor(getResources().getColor(
										R.color.gray));
								place_pic.setTextColor(getResources().getColor(
										R.color.gray));
							}

							@Override
							public void change(UserPlaceSetting placeSetting) {
								// TODO Auto-generated method stub
								CustomAbsClass.startPlaceSetting(
										(Activity) context, null,place==null?0:place.getLatitude(),place==null?0:place.getLongitude());
							}
						});
			}
			break;
		case R.id.sendtopic:
			checkCanSend();
			break;
		}
	}

	/**
	 * 检查是否可以符合标准，通过则将帖子发布
	 */
	private void checkCanSend() {
		String content = "", title = "";
		boolean canSend = false;
		content = post_content.getText().toString();
		title = post_title.getText().toString();
		if (TextUtils.isEmpty(content.trim())) {
			
				if (imageCount() > 0) {
					canSend = true;
				} else {
					Toast.makeText(context, "请填写内容,或者添加几张图片再发布~", Toast.LENGTH_SHORT).show();
					canSend = false;
				}
			 /*else if (content.length() < 1) {
				String a[] = new String[] { "一个字都不给我~", "写点文字吧!",
						"!!!还没告诉我你要说啥呢！" };
				Toast.makeText(context, a[(int) (Math.random()) * 3], 0).show();
			} */
			
		}else {
			canSend = true;
		}
		
		if (canSend) {
			// 统计点击次数
			MobclickAgent.onEvent(context, "11");// 统计发送的个数
			sendTopicContent(content, title);
			handler.postDelayed(runnable, 1000);
		}
	}

	/**
	 * 6秒钟未发布完，也要取消提示框
	 */
	private int recLen = 0;
	private boolean isKillProgress = false;
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			recLen++;
			if (isKillProgress) {
				handler.removeCallbacks(runnable);
				runnable = null;
				handler = null;
				finish();
			}
			if (recLen < 0) {
				Utils.Loge(recLen + "");
				handler.removeCallbacks(runnable);
				runnable = null;
				handler = null;
				// Toast.makeText(context, "后台发布中", 0).show();
				// finish();
			}
			if (handler != null) {
				handler.postDelayed(this, 1000);
			}
		}
	};

	public void delImageForUrl(String url) {
		LogUtil.e("收到了删除图片的消息："+url);
		if (TextUtils.isEmpty(url))
			return;
		for (int i = 0; i < listData.size(); i++) {
			if (listData.get(i).equals(url)) {
				removeImageForFile(i);
				break;
			}
		}
		//mediasCreateDone(listData);
	}
	private void removeImageForFile(int fileIndex) {

		RelativeLayout at = (RelativeLayout) mMediaContainer
				.findViewWithTag(listData.get(fileIndex));
		mMediaContainer.removeView(at);
		listData.remove(fileIndex);
		int newIndex = 0;
		boolean hasAddBtn=false;
		for (int i = 0; i < mMediaContainer.getChildCount(); i++) {
			FrameLayout.LayoutParams lp = imageLayoutParams(newIndex);
			mMediaContainer.getChildAt(i).setLayoutParams(lp);
			if (mMediaContainer.getChildAt(i) instanceof TextView)
				hasAddBtn=true;
			newIndex++;
		}
		if (listData.size() < Constants.TOPIC_MAX_PHOTO_PER_POST&&!hasAddBtn){
			FrameLayout.LayoutParams lp_btn = imageLayoutParams(listData
					.size());
			addPicture.setLayoutParams(lp_btn);
			mMediaContainer.addView(addPicture);
		}

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// requestCode = requestCode & 0xffff;//android bug:
		// https://groups.google.com/forum/#!msg/android-developers/NiM_dAOtXQU/ufi_FYqMGxIJ
		super.onActivityResult(requestCode, resultCode, data);
		mMediaHelper.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			return;
		}
		switch (requestCode) {
			case Constants.RequestCode.ACTIVITY_REQUEST_CODE_GALLERY_REQUEST:
				if (resultCode == Activity.RESULT_OK) {
					if (data==null)
						return;
					String url = data.getStringExtra("deleteMediaFileUrl");
					delImageForUrl(url);
				}
				break;
			case Constants.RequestCode.ACTIVITY_REQUEST_CODE_SHOWPLACE:
				place = (Place) data.getSerializableExtra("place");
				placename.setText(place.getPlace_name());
				// placename.setTextColor(getResources().getColor(R.color.blue));
				// place_pic.setTextColor(getResources().getColor(R.color.blue));
				break;
		default:
			break;
		}
	}

	private void showImageAddressPopDown(int LayoutId, int y, int styleId) {
		PopupWindowUtils.showImageAddressPop(placeSet, TopicEditActivity.this,
				TopicEditActivity.this, new MyButtonListener() {

					@Override
					public void okButton() {
						if (MyBabyApp.getSharedPreferences().getInt("tipCount", 0) == 0) {
							EditPostActivity.setTipCount(1);
						}
					}

					@Override
					public void changeButton() {
						CustomAbsClass.startPlaceSetting(
								TopicEditActivity.this, null,place==null?0:place.getLatitude(),place==null?0:place.getLongitude());
					}
				}, LayoutId, y, styleId);
	}

	private ProgressDialog dialog;

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		dialog = null;
		switch (id) {
		case 1:
			dialog = new ProgressDialog(context);
			// dialog.setCancelable(true);
			dialog.setMessage("发布中...");
			dialog.show();

			break;

		case 2:
			dialog = new ProgressDialog(context);
			// dialog.setCancelable(true);
			dialog.setMessage("加载中...");
			dialog.show();
			break;
		}
		return dialog;
	}

	@Override
	public void onMediaFileDone(final String[] mediaFilePaths) {
		// TODO Auto-generated method stub
		if (mediaFilePaths != null) {
			for (int i = 0; i < mediaFilePaths.length; i++) {
				listData.add(mediaFilePaths[i]);
			}
			mediasCreateDone(listData);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////
	/**
	 * 发布文字动态，获取ID后如果有图片再上传图片
	 */
	@SuppressWarnings("deprecation")
	private void sendTopicContent(String content, String title) {
		// TODO Auto-generated method stub

		showDialog(1);
		Topic topic = new Topic();
		if (!TextUtils.isEmpty(content)) {
			topic.setContent(post_content.getText().toString());
		} 

		// 此处注意，topic的title不能为null，必须指定为""
		if (!TextUtils.isEmpty(title)) {
			topic.setTitle(post_title.getText().toString());
		} 

		if (topicCategory!=null) {
			topic.setCategory(topicCategory);
		}

		if (place!=null) {
			topic.setPlace(place);
		}
		// topic.setMedias(null);

		TopicRPC.add(topic, new ListCallback() {

			@Override
			public void onSuccess(final Topic topic) {
				// TODO Auto-generated method stub
				// 在此处理图片上传
				new CustomAbsClass.doSomething(TopicEditActivity.this) {

					@Override
					public void todo() {
						// TODO Auto-generated method stub
						isKillProgress = true;
						// 接下来处理图片上传

						if (listData != null && listData.size() > 0) {
							Constants.postTask=new PostMediaTask(context, listData, topic.getId());
							Constants.postTask.execute();
						} else {
							Toast.makeText(context, "发帖成功", Toast.LENGTH_SHORT).show();
							Bundle bundle=new Bundle();
							bundle.putBoolean("needForceRefush",true);
							MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_CommunityMain_Refush,bundle);
						}
						if (handler != null) {
							handler.removeCallbacks(runnable);
						}
						finish();
					}
				};
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub
				if (error != null) {
					new CustomAbsClass.doSomething(TopicEditActivity.this) {

						@Override
						public void todo() {
							// TODO Auto-generated method stub
							Toast.makeText(context, "发布失败", Toast.LENGTH_SHORT).show();
							if (runnable != null) {
								handler.removeCallbacks(runnable);
							}
							try {
								dismissDialog(1);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								Utils.Loge("界面退出或者用户手动取消导致的异常");
								e.printStackTrace();
							}
						}
					};
				}
			}
		});
	}

	
	private int imageCount() {
		return mMediaContainer.getChildCount() - 1;
	}

	/**
	 * 加载图片地理位置
	 */
	/*private void loadAddress() {
		// 加载图片地理位置
		loading_place_progressBar.setVisibility(View.VISIBLE);
		new ImageAddressUtil().getLatLonPoint(listData.get(0),
				TopicEditActivity.this, new ImageAddressListener() {

					@Override
					public void callback(Place place) {
						// TODO Auto-generated method stub
						if (place != null) {
							loading_place_progressBar.setVisibility(View.GONE);
							TopicEditActivity.this.place = place;
							placename.setText(place.getPlace_name());
							// placename.setTextColor(getResources().getColor(
							// R.color.blue));
							// place_pic.setTextColor(getResources().getColor(
							// R.color.blue));
							if (EditPostActivity.getShowPlaceLocation(placeSet) < MyBabyApp.screenHeight / 2) {
								showImageAddressPopDown(
										R.layout.popplace_image_down,
										MyBabyApp.dip2px(5),
										R.style.suredownpopwindow_anim_style);
							} else {
								showImageAddressPopDown(
										R.layout.popplace_image_up,
										-MyBabyApp.dip2px(132),
										R.style.sureuppopwindow_anim_style);
							}
						}else {
							loading_place_progressBar.setVisibility(View.GONE);
						}
					}
				});
	}*/

	@SuppressLint("NewApi")
	private void addImageViewForMediaFile(final Media mf,
			final String[] filepath, final int indexAt) {
		FrameLayout.LayoutParams lp = imageLayoutParams(indexAt);

		ImageView imageView = ImageViewUtil.showImageByMediaFile(this, mf, lp);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		// imageView.setTag(filepath[indexAt]);

		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomAbsClass.starImagePage(context, filepath.length, indexAt, filepath, true, true,true);
			}
		});
		//删除
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final RelativeLayout imageLayout = (RelativeLayout) inflater.inflate(
				R.layout.photo_delete_header, null);
		LinearLayout photo_header_badge = (LinearLayout) imageLayout
				.findViewById(R.id.photo_header_badge);
		imageLayout.setTag(filepath[indexAt]);
		imageLayout.addView(imageView);
		photo_header_badge.bringToFront();
		imageLayout.setLayoutParams(lp);
		photo_header_badge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				delImageForUrl(filepath[indexAt]);
			}
		});
		if (BuildConfig.DEBUG) {
			imageLayout.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					LogUtil.e(imageLayout.getTag().toString());
					return true;
				}
			});
		}
		mMediaContainer.addView(imageLayout);
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

	@SuppressWarnings("deprecation")
	private void mediasCreateDone(final List<String> listData) {
		showDialog(2);
		mMediaContainer.removeAllViews();
		String[] paths = null;
		if (listData != null && listData.size() > 0) {
			paths = new String[listData.size()];
			for (int i = 0; i < listData.size(); i++) {
				paths[i] = listData.get(i);
			}
			final String[] finalPaths = paths;
			MediaRepository.asyncCreateMediasByParentId(PARENT_ID, paths,
					new MediasCreateDone() {
						@Override
						public void onDone(final Media[] medias) {
							// TODO Auto-generated method stub
							new CustomAbsClass.doSomething(TopicEditActivity.this) {
								@SuppressWarnings("deprecation")
								@Override
								public void todo() {
									// TODO Auto-generated method stub
									for (int i = 0; i < medias.length; i++) {
										addImageViewForMediaFile(medias[i],
												finalPaths, i);
									}
									// 添加图片
									if (listData.size() >= Constants.TOPIC_MAX_PHOTO_PER_POST) {
										FrameLayout.LayoutParams lp_btn = new FrameLayout.LayoutParams(
												0, 0);
										addPicture.setLayoutParams(lp_btn);
									} else {
										FrameLayout.LayoutParams lp_btn = imageLayoutParams(listData
												.size());
										addPicture.setLayoutParams(lp_btn);
										mMediaContainer.addView(addPicture);
									}
									dismissDialog(2);
									// 加载完成图片后开始定位
									/*try {
										if (TextUtils.isEmpty(placename
												.getText().toString())) {
											loadAddress();
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}*/
								}
							};
						}
					});
		} else {
			FrameLayout.LayoutParams lp_btn = imageLayoutParams(0);
			addPicture.setLayoutParams(lp_btn);
			mMediaContainer.addView(addPicture);
			dismissDialog(2);
		}

	}

	@Override
	public void onDestroy() {
		System.gc();
		super.onDestroy();
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
