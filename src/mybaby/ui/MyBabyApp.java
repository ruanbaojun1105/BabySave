package mybaby.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;

import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.contact.IYWContactService;
import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.BaseAlibabaSDK;
import com.alibaba.sdk.android.Environment;
import com.alibaba.wxlib.util.SysUtil;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.taobao.tae.sdk.callback.InitResultCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import dalvik.system.DexClassLoader;
import me.hibb.mybaby.android.BuildConfig;
import me.hibb.mybaby.android.R;
import me.hibb.mybaby.android.openIM.CustomOpenImUIHelper;
import me.hibb.mybaby.android.openIM.LoginOpenImHelper;
import mybaby.Constants;
import mybaby.MyBabyDB;
import mybaby.MyBabyTest;
import mybaby.models.Blog;
import mybaby.models.BlogRepository;
import mybaby.models.User;
import mybaby.models.UserRepository;
import mybaby.push.CrashHandler;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.util.LogUtils;
import mybaby.util.Utils;

public class MyBabyApp extends Application {//MultiDexApplication
	public static int version;
	public static Blog currentBlog;
	public static User currentUser;
	public static MyBabyDB myBabyDB;

	public static SimpleDateFormat sdf_yyyyMMdd;

	public static MediaHelper currentMediaHelper;
	public static Typeface fontAwesome;

	private static Context mContext;

	public static int screenWidth, screenHeight;
	public static float density, scaledDensity;

	public static boolean isAnonymousSignUp;

	public static final String KEY_DEX2_SHA1 = "dex2-SHA1-Digest";

	// 友盟推送

	ApplicationInfo appi;
	private static final String TAG = MyBabyApp.class.getName();
	private PushAgent mPushAgent;

	@Override
	protected void attachBaseContext(Context base) {
		super .attachBaseContext(base);
		LogUtils.d( "loadDex>>>App attachBaseContext ");
		if (!quickStart() && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {//>=5.0的系统默认对dex进行oat优化
			if (needWait(base)){
				waitForDexopt(base);
			}
			MultiDex.install(this);
		} else {
			return;
		}
	}
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}
	@SuppressLint("SimpleDateFormat")
	@Override
	public void onCreate() {
		super.onCreate();
		if (quickStart()) {
			return;
		}
		//mContext = this;
		if(mustRunFirstInsideApplicationOnCreate()){
			//todo 如果在":TCMSSevice"进程中，无需进行openIM和app业务的初始化，以节省内存
			return;
		}
//		if (LeakCanary.isInAnalyzerProcess(this)) {
//			return;
//		}
//		LeakCanary.install(this);
		myBabyDB = new MyBabyDB(this);
		MyBabyApp.currentBlog = BlogRepository.getCurrentBlog();
		if (MyBabyApp.currentBlog != null) {
			MyBabyApp.currentUser = UserRepository.load(MyBabyApp.currentBlog
					.getUserId());
		}
		x.Ext.init(this);
		x.Ext.setDebug(BuildConfig.DEBUG);
		LogUtils.e("MyBaby 执行 Application.MyBaby.onCreate");
		//Cube.onCreate(this);//cube框架初始化
		umengPush();

		sdf_yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Iconify.with(new FontAwesomeModule());//通用字体库，可添加更多字体库https://github.com/JoanZapata/android-iconify
		version = Utils.getAppVersion();
		fontAwesome = Typeface.createFromAsset(getAssets(),
				"fontawesome-webfont.ttf");
		initImageLoader(this);// ImageLoader
		CrashHandler.getInstance().init(this);
		//initCubeImageLoader();
		//ViewTarget.setTagId(R.id.glide_tag);

		//UTAnalytics.getInstance().turnOnDebug();
		//AlibabaSDK.turnOnDebug();
		initOpenIM();
		int envIndex = 1;
		BaseAlibabaSDK.asyncInit(this,new InitResultCallback() {
			@Override
			public void onSuccess() {
				Log.d(TAG,"BaseAlibabaSDK init successed");
			}

			@Override
			public void onFailure(int code, String msg) {
				Log.e(TAG,"BaseAlibabaSDK init failed");
			}
		});
		AlibabaSDK.setEnvironment(Environment.values()[envIndex]);
		AlibabaSDK.asyncInit(this, null/*new InitResultCallback() {
			@Override
			public void onSuccess() {
				//Toast.makeText(MyBabyApp.this, "初始化成功 ", Toast.LENGTH_SHORT).show();
				LoginService loginService = AlibabaSDK.getService(LoginService.class);
				loginService.setSessionListener(new SessionListener() {

					@Override
					public void onStateChanged(Session session) {
						if (session != null) {
							*//*Toast.makeText(MyBabyApp.this,
									"session状态改变" + session.getUserId() + session.getUser() + session.isLogin(),
									Toast.LENGTH_SHORT).show();*//*
						} else {
							//Toast.makeText(MyBabyApp.this, "session is null", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}

			@Override
			public void onFailure(int code, String message) {
				Toast.makeText(MyBabyApp.this, "taobao sdk初始化异常" + message, Toast.LENGTH_SHORT).show();
			}
		}*/);


		// 测试代码
		if (BuildConfig.DEBUG && MyBabyTest.enabled()) {
			MyBabyTest.execTest();
		}	
	}


	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(float dpValue) {  
        final float scale = mContext.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
    
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(float pxValue) {  
        final float scale = mContext.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }
	private boolean mustRunFirstInsideApplicationOnCreate() {
		//必须的初始化
		SysUtil.setApplication(this);
		mContext = getApplicationContext();
		return SysUtil.isTCMSServiceProcess(mContext);
	}

	private void umengPush() {
		mPushAgent = PushAgent.getInstance(this);// 获取友盟推送服务实例
		mPushAgent.setDebugMode(BuildConfig.DEBUG);
		mPushAgent.setNotificaitonOnForeground(false);//是否在前台运行时提示

		UmengMessageHandler messageHandler = new UmengMessageHandler() {
			/**
			 * 参考集成文档的1.6.3 http://dev.umeng.com/push/android/integration#1_6_3
			 * */
			@Override
			public void dealWithCustomMessage(final Context context,
					final UMessage msg) {
				new Handler(getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						// 对自定义消息的处理方式，点击或者忽略
						boolean isClickOrDismissed = true;
					
						if (isClickOrDismissed) {
							// 自定义消息的点击统计
							UTrack.getInstance(getApplicationContext())
									.trackMsgClick(msg);
						} else {
							// 自定义消息的忽略统计
							UTrack.getInstance(getApplicationContext())
									.trackMsgDismissed(msg);
						}
					}
				});
			}

			/**
			 * 参考集成文档的1.6.4 http://dev.umeng.com/push/android/integration#1_6_4
			 * */
			@Override
			public Notification getNotification(Context context, UMessage msg) {
				
				switch (msg.builder_id) {
				case 1:
					NotificationCompat.Builder builder = new NotificationCompat.Builder(
							context);
					RemoteViews myNotificationView = new RemoteViews(
							context.getPackageName(),
							R.layout.notification_view);
					myNotificationView.setTextViewText(R.id.notification_title,
							msg.title);
					myNotificationView.setTextViewText(R.id.notification_text,
							msg.text);
					// 发送广播，让页面接收如果有信息就刷新
					//sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_Main_UpdateTabBadge);
					myNotificationView.setImageViewBitmap(
							R.id.notification_large_icon,
							getLargeIcon(context, msg));
					myNotificationView.setImageViewResource(
							R.id.notification_small_icon,
							getSmallIconId(context, msg));
					builder.setContent(myNotificationView);
					builder.setAutoCancel(true);
					Notification mNotification = builder.build();
					mNotification.flags = Notification.FLAG_AUTO_CANCEL; 
					// 由于Android
					// v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
					mNotification.contentView = myNotificationView;
					return mNotification;
				default:
					// 默认为0，若填写的builder_id并不存在，也使用默认。
					sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_Main_UpdateTabBadge);
					return super.getNotification(context, msg);
				}
			}
		};
		mPushAgent.setMessageHandler(messageHandler);

		/**
		 * 该Handler是在BroadcastReceiver中被调用，故
		 * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK 参考集成文档的1.6.2
		 * http://dev.umeng.com/push/android/integration#1_6_2
		 * */
		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
			@Override
			public void launchApp(final Context context, final UMessage msg) {
				// TODO Auto-generated method stub
				super.launchApp(context, msg);
				Log.e("action", "~~~~~~~~launchApp~~~~~~~~~~~~~~~~~~~~~~~~");
				new Handler(getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						try {
							JSONObject json = new JSONObject(msg.extra);
							Constants.Action_Msg_Notifition = json.getString("mb_action_v2");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						//两种数据
						if (TextUtils.isEmpty(Constants.Action_Msg_Notifition)){
							Constants.Action_Msg_Notifition=msg.custom;
						}

						Intent intent = new Intent(context, MyBayMainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("umeng_action",Constants.Action_Msg_Notifition);
						startActivity(intent);
						// 发送广播，让页面接收如果有信息就刷新
						sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_Main_UpdateTabBadge);
					}
				});
			}
		};
		mPushAgent.setNotificationClickHandler(notificationClickHandler);
	}

	public static void initScreenParams(Activity activity) {
		if (MyBabyApp.screenWidth > 0) {
			return; // 已经初始化的
		}
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		screenWidth = dm.widthPixels;// px 物理像素
		screenHeight = dm.heightPixels;// px 物理像素
		density = dm.density;
		scaledDensity = dm.scaledDensity;
	}

	public static Context getContext() {
		return mContext;
	}
	private void initImageLoader(Context context) {
		/*DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.build();*/
		DisplayImageOptions displayImageOptions=new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true)// 设置下载的资源是否缓存在SD卡中
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY)//EXACTLY  IN_SAMPLE_POWER_OF_2
				.resetViewBeforeLoading(false)  // default 
				//.delayBeforeLoading(500)  //延迟500毫秒开始加载
				.showImageForEmptyUri(R.drawable.btn_bg_gray)
				.showImageOnFail(R.drawable.btn_bg_gray)
				.showImageOnLoading(R.drawable.btn_bg_gray)
				/*.showImageForEmptyUri(new ColorDrawable(Color.parseColor("#f5f5f5")))
				.showImageOnFail(new ColorDrawable(Color.parseColor("#f5f5f5")))
				.showImageOnLoading(new ColorDrawable(Color.parseColor("#f5f5f5")))*/
				.build();

		File cacheDir = StorageUtils.getCacheDirectory(context);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				//.memoryCacheExtraOptions(480, 800) // default = device screen dimensions
				.diskCacheExtraOptions(480, 800, null)
				//.taskExecutor(...)
				//.taskExecutorForCachedImages(...)
				.threadPoolSize(3) // default
				.threadPriority(Thread.NORM_PRIORITY - 2) // default
				.tasksProcessingOrder(QueueProcessingType.FIFO) // default
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(1 * 1024 * 1024))
				.memoryCacheSize(1 * 1024 * 1024)
				//.memoryCacheSizePercentage(12) // 内存的八分之一 左右(int) (Runtime.getRuntime().maxMemory() / 8) default设置最大内存缓存大小（以百分比的可用的应用程序内存)
				.diskCache(new UnlimitedDiskCache(cacheDir)) // default
				.diskCacheSize(50 * 1024 * 1024)
				//.diskCacheFileCount(800)
				//.diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
				//.imageDownloader(new BaseImageDownloader(context)) // default
				//.imageDecoder(new BaseImageDecoder(false)) // default调试日志是否写入logcat
				.defaultDisplayImageOptions(displayImageOptions) // default:DisplayImageOptions.createSimple()
				//.writeDebugLogs()
				.build();
		/*ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
				.defaultDisplayImageOptions(displayImageOptions)
				.threadPoolSize(3)
				//.threadPriority(Thread.NORM_PRIORITY - 1)
				.memoryCache(new UsingFreqLimitedMemoryCache(3* 1024 * 1024)) //最早被添加的对象会被删除
				//.memoryCache(new UnlimitedDiscCache()) //最早被添加的对象会被删除
				//.memoryCacheSizePercentage(13) // default 
				//.diskCacheFileCount(500)
				.memoryCacheSize(5* 1024 * 1024) // 内存缓存的最大值
				.diskCacheSize(20 * 1024 * 1024)
				//.denyCacheImageMultipleSizesInMemory()// 不缓存图片的多种尺寸在内存中
				.build();  //100 Mb sd卡(本地)缓存的最大值 */
		ImageLoader.getInstance().init(config);

	}
	public static SharedPreferences getSharedPreferences() {
		return MyBabyApp.getContext().getSharedPreferences(
				"MyBabySharedPreferences", 0);
	}
	public static void sendLocalBroadCast(String action) {
		sendLocalBroadCast(action,null);
	}
	public static void sendLocalBroadCast(String action,Bundle bundle) {
		Intent bi = new Intent();
		bi.setAction(action);
		if (bundle!=null)
			bi.putExtras(bundle);
		LocalBroadcastManager.getInstance(MyBabyApp.getContext())
				.sendBroadcast(bi);
	}
	private void initOpenIM() {
		if(SysUtil.isMainProcess()){
			Log.i("", "is UI process");
			// ------[todo step1]-------------
			//［IM定制初始化］，如果不需要定制化，可以去掉此方法的调用
			//注意：由于增加全局初始化，该配置需最先执行
			CustomOpenImUIHelper.initCustom();
			// ------[todo step2]-------------
			//SDK初始化
			LoginOpenImHelper.getInstance().initSDK(this);
			//后期将使用Override的方式进行集中配置，请参照YWSDKGlobalConfigSample
			YWAPI.enableSDKLogOutput(false);
			IYWContactService.enableBlackList();
			YWAPI.setEnableAutoLogin2(true);
		}

		/*int pid = android.os.Process.myPid();
		String processName = "";
		ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				processName = appProcess.processName;
				break;
			}
		}
		String packageName = mContext.getPackageName();
		if(processName.equals(packageName)) {//由於使用友盟推送，導致程序有多個進程，所以需要在此判斷是否在主進程中
			//
			//初始化云旺SDK
		} else {
			Log.i("", "is not UI process");
		}*/
	}





	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
											//以下为打包加载dex所用公共方法，别动





	@NonNull
	public boolean quickStart() {
		if (getCurProcessName(this).contains(":mini")) {
			LogUtils.e( "loadDex>>>>:mini start!");
			return true;
		}
		/*if (getCurProcessName(this).contains(":push")) {
			LogUtils.e( "loadDex>>>>:mini start!");
			return true;
		}*///友盟不支持
		return false ;
	}
	//neead wait for dexopt ?
	private boolean needWait(Context context){
		String flag = get2thDexSHA1(context);
		LogUtils.d( "loadDex>>>dex2-sha1 "+flag);
		SharedPreferences sp = context.getSharedPreferences(
				getPackageInfo(context). versionName, MODE_MULTI_PROCESS);
		String saveValue = sp.getString(KEY_DEX2_SHA1, "");
		return !(flag.equals(saveValue));
	}
	public static PackageInfo getPackageInfo(Context context){
		PackageManager pm = context.getPackageManager();
		try {
			return pm.getPackageInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			LogUtils.e(e.getLocalizedMessage());
		}
		return  new PackageInfo();
	}
	/**
	 * Get classes.dex file signature
	 * @param context
	 * @return
	 */
	private String get2thDexSHA1(Context context) {
		ApplicationInfo ai = context.getApplicationInfo();
		String source = ai.sourceDir;
		try {
			JarFile jar = new JarFile(source);
			Manifest mf = jar.getManifest();
			Map<String, Attributes> map = mf.getEntries();
			Attributes a = map.get("classes2.dex");
			return a.getValue("SHA1-Digest");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
	}
	// optDex finish
	public void installFinish(Context context){
		SharedPreferences sp = context.getSharedPreferences(
				getPackageInfo(context).versionName, MODE_MULTI_PROCESS);
		sp.edit().putString(KEY_DEX2_SHA1,get2thDexSHA1(context)).commit();
	}
	public static String getCurProcessName(Context context) {
		try {
			int pid = android.os.Process.myPid();
			ActivityManager mActivityManager = (ActivityManager) context
					.getSystemService(Context. ACTIVITY_SERVICE);
			for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
					.getRunningAppProcesses()) {
				if (appProcess.pid == pid) {
					return appProcess. processName;
				}
			}
		} catch (Exception e) {
			// ignore
		}
		return null ;
	}
	public void waitForDexopt(Context base) {
		Intent intent = new Intent(this,LoadResActivity.class);
		//ComponentName componentName = new ComponentName( "mybaby.ui", LoadResActivity.class.getName());
		//intent.setComponent(componentName);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		base.startActivity(intent);
		long startWait = System.currentTimeMillis ();
		long waitTime = 10 * 1000 ;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1 ) {
			waitTime = 20 * 1000 ;//实测发现某些场景下有些2.3版本有可能10s都不能完成optdex
		}
		while (needWait(base)) {
			try {
				long nowWait = System.currentTimeMillis() - startWait;
				LogUtils.d("loadDex>>>wait ms :" + nowWait);
				if (nowWait >= waitTime) {
					return;
				}
				Thread.sleep(200 );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
