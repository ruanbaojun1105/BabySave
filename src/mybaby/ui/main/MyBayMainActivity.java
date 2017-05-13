package mybaby.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.YWIMKit;
import com.umeng.analytics.MobclickAgent;
import com.umeng.common.message.UmengMessageDeviceConfig;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.update.UmengUpdateAgent;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.BuildConfig;
import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.RxUtils;
import mybaby.action.Action;
import mybaby.models.UserRepository;
import mybaby.notification.RefreshNotificationManager;
import mybaby.push.AlwayLiveService;
import mybaby.rpc.BlogRPC;
import mybaby.service.Sync2ServerService;
import mybaby.share.UmengShare;
import mybaby.ui.MyBabyApp;
import mybaby.ui.broadcast.PostMediaTask;
import mybaby.ui.community.adapter.MyFragmentPagerAdapter;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.community.parentingpost.util.WebViewUtil;
import mybaby.ui.user.PersonEditActivity;
import mybaby.ui.widget.NoScrollViewPager;
import mybaby.util.LogUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyBayMainActivity extends AppCompatActivity {
	final public static String homeTabTag = "home";
	final public static String communityTabTag = "community";
	final public static String shoppingTabTag = "shopstree";//购物街
	final public static String meTabTag = "me";
	final public static int homeTabIndex = 0;
	final public static int communityTabIndex = 1;
	final public static int shoppingTabIndex = 2;
	final public static int meTabIndex = 3;
	
	final public static String TAG = MyBayMainActivity.class.getSimpleName();

	private List<Fragment> mFragmentList = new ArrayList<Fragment>();
	private MeFragment moreFragment=new MeFragment();
	private HomeFragment homeFragment =new HomeFragment();
	private NCommunityFragment communityFragment=new NCommunityFragment();
	private NotificationCategoryFragment notificationFragment=new NotificationCategoryFragment();
	//private DiscoveryFragment discoveryFragment =new DiscoveryFragment();
	private ShoppingStreeFragment streeFragment=new ShoppingStreeFragment();

	public ShoppingStreeFragment getStreeFragment() {
		return streeFragment;
	}

	private TabWidget tabWidget;
	private NoScrollViewPager mViewPager;
	public static Activity activity;
	private Toolbar homeToolbar;

	private boolean alreadyRemindSignUp = false;
	private boolean hasRegisterUmeng = false;//是否已注册过友盟ID
	private MyBroadcastReceiver mBroadcastReceiver;
	private PushAgent mPushAgent;// 友盟推送
	private Subscription subscription;
	public static UMSocialService mainShareController = UMServiceFactory.getUMSocialService(Constants.DESCRIPTOR);
	public static boolean hasLoadCommunity=false;//是否加载了社区
	public static boolean hasLoadShopStree=false;//是否加载了辣妈街

	public static YWIMKit getmIMKit() {
		return MainUtils.getYWIMKit();
	}

	public Toolbar getHomeToolbar() {
		return homeToolbar;
	}

	public NoScrollViewPager getmViewPager() {
		return mViewPager;
	}

	public NotificationCategoryFragment getNotificationFragment() {
		return notificationFragment;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		LogUtil.e("onStart_____________________________");
		//初始化友盟统计回调
		initUmengMobclickAgent();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.e("onCreate___________________________________");
		setContentView(R.layout.activity_tablayout);
		activity=this;
		homeToolbar= (Toolbar) findViewById(R.id.toolbar_home);
		setSupportActionBar(homeToolbar);
		//StatusBarUtils.setWindowStatusBarColor(this,R.color.mainThemeColor);
		if (MyBabyApp.myBabyDB == null) {
			Toast.makeText(this, R.string.pleasekill, Toast.LENGTH_LONG).show();
			finish();
		}
		// 广播接收
		mBroadcastReceiver = new MyBroadcastReceiver();
		mBroadcastReceiver.registerMyReceiver();
		MyBabyApp.initScreenParams(this);
		//配置分享
		UmengShare.configPlatforms(mainShareController, activity);
		// 创建通知管理者定时刷新
		RefreshNotificationManager.createRefreshNotificationManager();
		// 启动一下同步服务
		Sync2ServerService.startSync2Server();


		if (MyBabyApp.currentBlog == null ) {//进入登入
			startActivity(new Intent(this, PersonEditActivity.class));
			finish();
		}
		//透明状态栏
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		//透明导航栏
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);


		Observable.OnSubscribe<String> subscribe=new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				if (!subscriber.isUnsubscribed()) {
					try {
						// 获取系统信息
						if (MyBabyApp.currentBlog !=null&&MyBabyApp.currentUser != null)
							BlogRPC.getSystemInfo(null);
						//MainUtils.createSystemSwitcherShortCut(activity);
						//页面属性缓存
						WebViewUtil.getNewOption(activity, null);
						MainUtils.getServerVersionCode(activity);
						subscriber.onNext("ok>next>>>");
						subscriber.onCompleted();
					} catch (Exception e) {
						subscriber.onError(e);
					}
				}
			}
		};
		this.subscription =Observable.create(subscribe)
				.subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
				.observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
				.subscribe(new Subscriber<String>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {
						Toast.makeText(MyBayMainActivity.this,"初始化异常",Toast.LENGTH_SHORT).show();
						MyBayMainActivity.this.finish();
					}

					@Override
					public void onNext(String s) {
						initComponents();
					}
				});

	}

	@Override
	protected void onResume() {
		LogUtil.e("onResume___________________________________");
		activity=this;
		CustomAbsClass.TopicIDList.clear();//话题记录列表
		MobclickAgent.onResume(this); // 统计时长
		PostMediaTask.sendBroadcast(Constants.BroadcastAction.BroadcastAction_Notification_Summary, null);

		if (!hasRegisterUmeng)
			initUmengPush();
		if (MyBabyApp.getSharedPreferences().getInt("look_guide", 0)!=MyBabyApp.version)
			CustomAbsClass.starGuideActivity(this);
		if (!Constants.hasLoginOpenIM)
			MainUtils.loginIM();

		try {
			alreadyRemindSignUp = MainUtils.remindSignUp(activity,
					alreadyRemindSignUp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (getIntent().getExtras()!=null){
			if (getIntent().getExtras().containsKey("umeng_action")){
				String a=getIntent().getExtras().getString("umeng_action");
				Constants.Action_Msg_Notifition=a;
			}
		}
		if (!TextUtils.isEmpty(Constants.Action_Msg_Notifition)){
			LogUtils.e("push send action"+Constants.Action_Msg_Notifition);
			if (BuildConfig.DEBUG)
				Toast.makeText(activity, "push send action:>>>"+Constants.Action_Msg_Notifition, Toast.LENGTH_SHORT).show();
			try {
				getHomeToolbar().postDelayed(new Runnable() {
					@Override
					public void run() {
						try {
							Action.createAction(Constants.Action_Msg_Notifition).excute(MyBayMainActivity.this, mainShareController, null, null);
						} catch (Exception e) {
							e.printStackTrace();
							LogUtil.e("action执行异常");
							if (BuildConfig.DEBUG)
								Toast.makeText(MyBayMainActivity.this,"action执行异常,请检查action链接" , Toast.LENGTH_SHORT).show();
						}
					}
				}, 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Constants.Action_Msg_Notifition="";
		}
		if (mViewPager!=null) {
			MainUtils.positionAtActoionBarView(homeToolbar, mViewPager.getCurrentItem());
			//test
			/*mViewPager.postDelayed(new Runnable() {
				@Override
				public void run() {
					Constants.Action_Msg_Notifition = "mybaby_browse_url?url=http://www.lejimami.com/baike-post?id=100220791&mbspt=%E4%B8%93%E8%BE%91&user_id=3743";
				}
			}, 10000);*/
		}
		super.onResume();
	}

	@Override
	public void onDestroy() {
		LogUtils.e("onDestroy___________________________________");
		RxUtils.unsubscribeIfNotNull(subscription);
		if (mBroadcastReceiver != null)
			LocalBroadcastManager.getInstance(MyBabyApp.getContext())
					.unregisterReceiver(mBroadcastReceiver);
		AlwayLiveService.stopAlwayService();
		AlwayLiveService.startAlwayService();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		LogUtils.e("onPause___________________________________");
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onStop() {
		LogUtils.e("onStop___________________________________");
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		UMSsoHandler ssoHandler = mainShareController.getConfig().getSsoHandler(requestCode) ;
		if(ssoHandler != null){
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

		if (requestCode == Constants.RequestCode.ACTIVITY_REQUEST_CODE_SIGN_UP_REQUEST) {
			if (resultCode == RESULT_CANCELED)
				finish();
			if (resultCode == RESULT_OK)
				LoadMoreListViewFragment.askRed();
		}

		if (MyBabyApp.currentMediaHelper != null)
			MyBabyApp.currentMediaHelper.onActivityResult(requestCode, resultCode,data);

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 友盟回调
	 */
	public Handler umengRegiestHandler = new Handler();
	
	//此处是注册的回调处理
	//参考集成文档的1.7.10
	//http://dev.umeng.com/push/android/integration#1_7_10
	public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {
		
		@Override
		public void onRegistered(String registrationId) {
			// TODO Auto-generated method stub
			umengRegiestHandler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					updateStatus();
				}
			});
		}
	};
	/**
	 * 初始化友盟
	 */
	private void initUmengPush() {
		mPushAgent = PushAgent.getInstance(this);// 获取友盟推送服务实例
		// 应用程序启动统计// http://dev.umeng.com/push/android/integration#1_5_1
		mPushAgent.onAppStart();
		//开启推送并设置注册的回调处理
 	    mPushAgent.enable(mRegisterCallback);
 	    //mPushAgent.setNotificaitonOnForeground(true);
		AlwayLiveService.startAlwayService();//长期在线生命的服务
		LogUtils.e("initUmengPush");
	}
	/**
	 * 初始化友盟统计
	 */
	private void initUmengMobclickAgent(){
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);// 自动更新
		MobclickAgent.openActivityDurationTrack(false);// 禁止默认的页面统计方式，这样将不会再自动统计Activity。
		LogUtils.e("initUmengMobclickAgent--->ok");
	}
	/**
	 * 友盟回调数据
	 */
	private void updateStatus() {
		String pkgName = getApplicationContext().getPackageName();
		String info = String.format("enabled:%s  isRegistered:%s  DeviceToken:%s " + 
				"SdkVersion:%s AppVersionCode:%s AppVersionName:%s",
				mPushAgent.isEnabled(), mPushAgent.isRegistered(),
				mPushAgent.getRegistrationId(), MsgConstant.SDK_VERSION,
				UmengMessageDeviceConfig.getAppVersionCode(this), UmengMessageDeviceConfig.getAppVersionName(this));
		LogUtil.e("Push开关"+mPushAgent.isEnabled()+"---应用包名："+pkgName+"\n"+info);
		
		String device_token = UmengRegistrar.getRegistrationId(activity);
		LogUtil.e(device_token+"如果在测试或其他使用场景中，需要获取设备的Device Token，可以使用下面的方法");
		LogUtil.e(mPushAgent.isEnabled() + "友盟消息推送当前状态");
		
		Log.i("mainactivity____", "updateStatus:" + String.format("enabled:%s  isRegistered:%s",
				mPushAgent.isEnabled(), mPushAgent.isRegistered()));
		Log.i("mainactivity____", "=============================");
		if (mPushAgent.isEnabled()&&mPushAgent.isRegistered()) {
			String uMengAndroidPushId = mPushAgent.getRegistrationId();
			try {
				MyBabyApp.currentUser.setuMengAndroidPushId(uMengAndroidPushId);
				MyBabyApp.currentUser.setAndroidAppVersion(MyBabyApp.version);
				MyBabyApp.currentUser.setBzInfoModified(true);
				UserRepository.save(MyBabyApp.currentUser);
				MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_UMeng_Push_Register_Done);
				hasRegisterUmeng=true;
			} catch (Exception e) {
				e.printStackTrace();
				hasRegisterUmeng=false;
			}
		}
	}
	/**
	 * 主控件的初始化
	 */
	private void initComponents() {
		activity = this;

		tabWidget = (TabWidget) findViewById(R.id.tabWidget_navi);
		mViewPager = (NoScrollViewPager) findViewById(R.id.viewPager);
		mViewPager.setNoScroll(true);

		addTabView("主页", R.drawable.tab_info_home, homeTabTag);
		addTabView("社区", R.drawable.tab_info_community, communityTabTag);
		//addTabView("发现", R.drawable.tab_info_discovery, fourssTabTag);
		//addTabView("消息", R.drawable.tab_info_notification, threeTabTag);
		addTabView("购物", R.drawable.tab_info_shopping, shoppingTabTag);
		addTabView("我", R.drawable.tab_info_others, meTabTag);

		/*Bundle b= new Bundle();
		b.putInt("index", homeTabIndex);
		homeFragment.setArguments(b);

		b.putInt("index", threeTabIndex);
		notificationFragment.setArguments(b);

		b.putInt("index", shoppingTabIndex);
		moreFragment.setArguments(b);*/

		mFragmentList.add(homeFragment);
		mFragmentList.add(communityFragment);
//		mFragmentList.add(notificationFragment);
		mFragmentList.add(streeFragment);
		mFragmentList.add(moreFragment);

		mViewPager.setOffscreenPageLimit(mFragmentList.size());
		//mViewPager.setKeepScreenOn(true);//屏幕常亮
		MyFragmentPagerAdapter myPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),new String[]{"主页","社区","购物","我"},mFragmentList);
		mViewPager.setAdapter(myPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int index) {
				updateTabs();
				MainUtils.positionAtActoionBarView(homeToolbar, index);
			}

			@Override
			public void onPageScrolled(int index, float arg1, int pixes) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		//updateToSaveExitTag();
		updateTabs();
		MainUtils.positionAtActoionBarView(homeToolbar, mViewPager.getCurrentItem());
	}


	/**
	 * 切到顶部的广播
	 */
	public static void sendIsNeedBackTopBroadCast(String tag,@Nullable Bundle bundle) {
		if (bundle==null)
			bundle=new Bundle();
		bundle.putString("currentTag", tag);
		bundle.putBoolean("isNeedBackTop", true);
		MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_Activity_Main_Need_BackTop, bundle);
	}

	/**
	 * 获取上次退出时的他把host位置，并显示位置
	 */
	private void updateToSaveExitTag() {
		String currentTag = MyBabyApp.getSharedPreferences()
				.getString("saveExitTabTag", communityTabTag);//默认跳到社区主页
		if (currentTag.equals(communityTabTag)) {
			mViewPager.setCurrentItem(communityTabIndex,false);
		}else if(currentTag.equals(shoppingTabTag)){
			mViewPager.setCurrentItem(shoppingTabIndex,false);
		}else if(currentTag.equals(meTabTag)){
			mViewPager.setCurrentItem(meTabIndex,false);
		}
		MainUtils.positionAtActoionBarView(homeToolbar, mViewPager.getCurrentItem());
	}


	/**
	 * 
	 */
	public static String getCurrentTag(int index) {
		return index == homeTabIndex ? homeTabTag
				: (index == communityTabIndex ? communityTabTag
						: (index == shoppingTabIndex ? shoppingTabTag : meTabTag));
	}

	public static int getCurrentByTag(String tag) {
		return tag.equals(homeTabTag)? homeTabIndex :
				(tag.equals(communityTabTag)? communityTabIndex :
				(tag.equals(shoppingTabTag)? shoppingTabIndex : meTabIndex));
	}
	/**
	 * 点击事件
	 */
	private OnClickListener mTabClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Bundle bundle=new Bundle();
			String tag=v.getTag().toString();
			int cur=getCurrentByTag(tag);
			if (cur==mViewPager.getCurrentItem()){
				if (mViewPager.getCurrentItem()== shoppingTabIndex)
					bundle.putString("url","");
				sendIsNeedBackTopBroadCast(v.getTag().toString(),bundle);
			}else{
				mViewPager.setCurrentItem(cur,false);
				/*if (v.findViewById(R.id.tab_badge).getVisibility()==View.VISIBLE) {
					sendIsNeedBackTopBroadCast(tag,bundle);
				}*/
			}

			if (v.findViewById(R.id.tab_badge).getVisibility()==View.VISIBLE) {
				if (cur== communityTabIndex &&!hasLoadCommunity&&MainUtils.community_needRefresh){
					hasLoadCommunity=true;
					sendIsNeedBackTopBroadCast(tag,bundle);
				}
				/*if (cur== communityTabIndex){//区分消息提醒和社区提醒，如果有社区提醒才刷新红点
					bundle.putBoolean("community_needRefresh",MainUtils.community_needRefresh);
				}*/
				if (cur== shoppingTabIndex &&!hasLoadShopStree){
					hasLoadShopStree=true;
					bundle.putString("url",Constants.MY_BABY_SHOP_STREE);
					bundle.putBoolean("isSendRed",true);
					sendIsNeedBackTopBroadCast(tag,bundle);
				}
			}else {//处理异常情况
				if (cur== communityTabIndex &&!hasLoadCommunity){
					sendIsNeedBackTopBroadCast(tag,bundle);
					hasLoadCommunity=true;
				}
				if (cur== shoppingTabIndex &&!hasLoadShopStree){
					bundle.putString("url",Constants.MY_BABY_SHOP_STREE);
					sendIsNeedBackTopBroadCast(tag,bundle);
					hasLoadShopStree=true;
				}

			}
		}
	};

	private OnTouchListener onTouchListener=new OnTouchListener() {
		long firClick;
		long secClick;

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mViewPager==null)
						return false;
					firClick = secClick;
					secClick = System.currentTimeMillis();
					if (secClick - firClick < 500) {
						if (tabWidget.getChildAt(mViewPager.getCurrentItem()).equals(v)) {
							//if (mViewPager.getCurrentItem()==shoppingTabIndex)
							//	return false;
							sendIsNeedBackTopBroadCast(getCurrentTag(mViewPager.getCurrentItem()),null);
						}
					}
					break;
			}
			return false;
		}
	};

	/**
	 * 添加tab
	 * 
	 * @param text
	 * @param drawableId
	 * @param keyTag
	 */
	@SuppressLint("InflateParams")
	private void addTabView(String text, int drawableId, String keyTag) {

		View tabIndicator = LayoutInflater.from(this)
				.inflate(R.layout.tab_indicator, null, false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(text);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);
		tabIndicator.setTag(keyTag);
		tabWidget.addView(tabIndicator);
		tabIndicator.setOnClickListener(mTabClickListener);
		//tabIndicator.setOnTouchListener(onTouchListener);
	}


	/**
	 * 更新tab显示,并将事件写在控件VIEW上
	 */
	@SuppressLint("InflateParams")
	private void updateTabs() {
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			View curr_tabIndicator = tabWidget.getChildAt(i);
			TextView curr_title = (TextView) curr_tabIndicator
					.findViewById(R.id.title);
			if (i != mViewPager.getCurrentItem()) {
				curr_title.setTextColor(getResources().getColor(R.color.read_gray));
				curr_tabIndicator.setSelected(false);
			} else {
				curr_title.setTextColor(getResources().getColor(R.color.mainThemeColor));
				curr_tabIndicator.setSelected(true);
			}
		}
	}


	/**
	 * 双击退出
	 */
	public void ExitApp() {
		/*if ((System.currentTimeMillis() - exitTime) > 1200) {
			Toast.makeText(activity, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {*/
			//saveExitTag();
		Intent home = new Intent(Intent.ACTION_MAIN);
		//home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		home.addCategory(Intent.CATEGORY_HOME);
		startActivity(home);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// MobclickAgent.onKillProcess(Context context);//如果开发者调用
			// Process.kill 或者 System.exit 之类的方法杀死进程，请务必在此之前调用此方法，用来保存统计数据。
			ExitApp();
			LogUtil.e("~~~~~~~~~onback");
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 保存退出的标签
	 */
	public  void saveExitTag() {
		if (mViewPager==null)
			return;
		Editor edit = MyBabyApp.getSharedPreferences().edit();
		edit.putString("saveExitTabTag", getCurrentTag(mViewPager.getCurrentItem()));
		edit.commit();
	}


	

	// 广播接收并响应处理
	private class MyBroadcastReceiver extends BroadcastReceiver {
		public void registerMyReceiver() {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.BroadcastAction.BroadcastAction_UMeng_Push_Register_Done);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Post_Add);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Post_Edit);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Post_Delete);

			filter.addAction(Constants.BroadcastAction.BroadcastAction_Person_Add);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Person_Edit);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Person_Delete);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Main_UpdateTabBadge);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_MainActivity_Home_TagBadgeUpdate);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Notification_Summary);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_StargetNewestSummaryFromService);
			LocalBroadcastManager.getInstance(MyBabyApp.getContext())
					.registerReceiver(this, filter);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mViewPager==null)
				return;
			if (intent.getAction()
					.equals(Constants.BroadcastAction.BroadcastAction_UMeng_Push_Register_Done)
					|| intent.getAction()
							.equals(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done)
					|| intent.getAction()
							.equals(Constants.BroadcastAction.BroadcastAction_Post_Add)
					|| intent.getAction()
							.equals(Constants.BroadcastAction.BroadcastAction_Post_Edit)
					|| intent.getAction()
							.equals(Constants.BroadcastAction.BroadcastAction_Post_Delete)
					|| intent.getAction()
							.equals(Constants.BroadcastAction.BroadcastAction_Person_Add)
					|| intent.getAction()
							.equals(Constants.BroadcastAction.BroadcastAction_Person_Edit)
					|| intent.getAction().equals(
							Constants.BroadcastAction.BroadcastAction_Person_Delete)
					) {
				// 启动服务器同步
				Sync2ServerService.startSync2Server();
				if (intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done)){
					MainUtils.positionAtActoionBarView(homeToolbar, mViewPager.getCurrentItem());
				}
			}
			if (intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Main_UpdateTabBadge))
				RefreshNotificationManager.refreshNotification(true);
			if (intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_StargetNewestSummaryFromService))
				MainUtils.positionAtActoionBarView(homeToolbar, mViewPager.getCurrentItem());
			if (intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Notification_Summary)||
					intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_MainActivity_Home_TagBadgeUpdate)) {
				Log.e("", "消息未读总数" + MyBabyApp.getSharedPreferences().getInt("unread", 0));
				//MainUtils.updateTabBadge(tabWidget, meTabIndex, MyBabyApp.currentUser == null ? 0 : (MyBabyApp.currentUser.getBzRegistered() ? 0 : -1));
				//MainUtils.updateTabBadge(tabWidget,threeTabIndex, notificationFragment.getUnread());
				MainUtils.updateTabBadgeByNotification(tabWidget,homeToolbar);
				MainUtils.positionAtActoionBarView(homeToolbar, mViewPager.getCurrentItem());
//				MainUtils.updateTabBadge(tabWidget, communityTabIndex, MyBabyApp.getSharedPreferences().getInt("unread", 0));//确保一下，因为有可能界面销毁导致拿不到数据
//				MainUtils.updateTabBadge(tabWidget, communityTabIndex, notificationFragment.getUnread());
			}
			/*if (intent.getAction().equals(
					Constants.BroadcastAction.BroadcastAction_MainActivity_Home_TagBadgeUpdate)) {
				*//*boolean isNeedUpdateTagBadge = intent
						.getBooleanExtra("isNeedUpdateTagBadge", false);*//*
				//MainUtils.updateTabBadge(tabWidget,meTabIndex, MyBabyApp.currentUser == null ? 0 : (MyBabyApp.currentUser.getBzRegistered()? 0: -1));
				MainUtils.updateTabBadgeByNotification(tabWidget);
				MainUtils.updateTabBadge(tabWidget, threeTabIndex, notificationFragment.getUnread());
			}*/
		}
	}
}
