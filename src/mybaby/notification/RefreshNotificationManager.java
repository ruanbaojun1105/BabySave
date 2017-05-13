package mybaby.notification;

import android.annotation.SuppressLint;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.BuildConfig;
import mybaby.Constants;
import mybaby.cache.CacheDataTask;
import mybaby.models.community.TopicCategory;
import mybaby.models.notification.NotificationCategory;
import mybaby.rpc.BlogRPC;
import mybaby.rpc.notification.NotificationRPC;
import mybaby.rpc.notification.NotificationRPC.SummaryCallback;
import mybaby.rpc.notification.TopicCategoryRPC;
import mybaby.rpc.notification.TopicCategoryRPC.ListCallback;
import mybaby.ui.MyBabyApp;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.util.LogUtils;
import mybaby.util.Utils;

/**
 * 管理
 * 定时刷新通知和刷新推荐话题
 */
public class RefreshNotificationManager {

	private static boolean mIsSyncing = false;
	public static boolean mNtfcSyncing = false;
	private static long mLastSyncingTime = 0;

	private static final long delayTime1 = BuildConfig.DEBUG ? 1000 * 60  : 1000 * 60 * 30; // 推荐话题  调试模式1分钟，发布模式30分钟
	private static final long delayTime2 = BuildConfig.DEBUG ? 60000:1000 * 60 * 5; // 通知调试模式30秒，发布模式5分钟

	static int recLen = 0;
	static MyThread myrun;
	static Thread thread;

	/**
	 * 90秒一次循环
	 */
	@SuppressLint("HandlerLeak")
	static Handler handler = new Handler(){			// handle
		@SuppressLint("ResourceAsColor")
		public void handleMessage(Message msg){
			switch (msg.what) {
				case 1:
					if (recLen*1000%20000==0) {
						System.gc();
					}
					//LogUtils.e("reclen--->" + recLen);
					if (recLen*1000%delayTime1==0) {
						refushRecommend();
						if (BuildConfig.DEBUG)
							LogUtils.e("delayTime1--->"+delayTime1);
					}
					if (recLen*1000%delayTime2==0) {
						getNewestSummaryFromService(false);
						if (recLen*1000/delayTime2>10)
							recLen=0;
						if (BuildConfig.DEBUG)
							LogUtils.e("delayTime2--->"+delayTime2);
					}
					recLen+=5;
			}
			super.handleMessage(msg);
		}
	};

	public static class MyThread implements Runnable{		// thread
		@Override
		public void run(){
			if(handler==null)
				return;
			while(true) {
				try {
					Thread.sleep(5000);        // sleep 1000ms
					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);
				} catch (Exception e) {
				}
			}
		}
	}

	public static void createRefreshNotificationManager() {
		LogUtils.e("MyBaby  creat a refresh notification manager");
		recLen=0;
		if (thread!=null) {
			if (handler!=null&&myrun!=null)
				handler.removeCallbacks(myrun);
			myrun=null;
			thread=null;
		}
		myrun=new MyThread();
		thread=new Thread(myrun);
		thread.start();
	}
	public static void refreshNotification(boolean atOnce) {
		LogUtils.e(atOnce+"atOnce refresh notification");
		getNewestSummaryFromService(atOnce);
	}

	/**
	 * 初始化加载推荐话题数据
	 */
	public static void refushRecommend() {
		if(MyBabyApp.currentUser == null || MyBabyApp.currentUser.getUserId() <= 0){
			return;
		}

		TopicCategoryRPC.getRecommend(new ListCallback() {

			@SuppressLint("NewApi")
			@Override
			public void onSuccess(TopicCategory[] arrCategory) {
				// TODO Auto-generated method stub

				Constants.TOPIC_CATEGORIES = arrCategory;
				LogUtils.e("已设定全局推荐话题");
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub
			}
		});

	}


	/**
	 * @param atOnce 是否立刻更新
	 */
	private static void getNewestSummaryFromService(boolean atOnce) {
		if (!atOnce) {
			if (mIsSyncing
					|| MyBabyApp.currentUser == null
					|| MyBabyApp.currentUser.getUserId() <= 0
					|| (mLastSyncingTime > 0 && System.currentTimeMillis()
					- mLastSyncingTime < 1000 * 3)) { // 不可超过10秒一次
				return;
			}
			mIsSyncing = true;
			BlogRPC.userLogSend(null);// 顺便在这里写日志
		}
		mNtfcSyncing =true;
		MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_StargetNewestSummaryFromService);
		LogUtils.e("MyBaby 开始执行：NewestInfoManager.getNewestInfoFromServer");
		NotificationRPC.getSummary(CacheDataTask.getObj(MyBayMainActivity.activity,Constants.CacheKey_NotificationCategory_Self)==null?0:1,new SummaryCallback() {
			@Override
			public void onSuccess(long lastPostTime, boolean gencom_has_new, boolean community_has_new, boolean nearby_has_new,
								  NotificationCategory[] notificationCategorys, NotificationCategory[] discoveryNcfcs,NotificationCategory[] otherNcfcs) {
				setLastPostTime(lastPostTime);
				setCommon_notification_has_new(gencom_has_new);
				setCommunity_has_new(nearby_has_new == true ? true : community_has_new);
				setNearby_has_new(nearby_has_new);

				List<NotificationCategory> data=new ArrayList<NotificationCategory>();
				//服务器数据与本地数据合并
				Object[] categories= CacheDataTask.getObjs(MyBabyApp.getContext(), Constants.CacheKey_NotificationCategory_Self);
				if (categories != null && categories.length > 0) {
					NotificationCategory[] a=new NotificationCategory[categories.length];
					for (int i = 0; i < categories.length; i++) {
						NotificationCategory category= (NotificationCategory) categories[i];
						category.setUnreadCount(0);//将缓存的先全部置零
						category.setStrongRemind(true);
						a[i] =category;
					}
					if (notificationCategorys != null && notificationCategorys.length > 0) {
						for (NotificationCategory b : notificationCategorys) {
							boolean hasV = false;//因为本地缓存的和从服务器拿的相同，所以不再次添加本地的就内容
							for (int i = 0; i < a.length; i++) {
								if (b.getKey().equals(a[i].getKey())) {
									a[i] = b;//将已在缓存中的数据用最新数据替换
									hasV = true;
									break;//循环中有一个相同就停止不
								}//不可在此置0
							}
							if (hasV)
								continue;
							else data.add(b);
						}
					}
					for (NotificationCategory c:a){
						data.add(c);
					}
				}else {
					if (notificationCategorys != null && notificationCategorys.length > 0) {
						for (NotificationCategory d : notificationCategorys) {
							data.add(d);
						}
					}
				}



				CacheDataTask.putCache(MyBabyApp.getContext(),data.toArray(),Constants.CacheKey_NotificationCategory_Self,true);//缓存消息列表通知
				CacheDataTask.putCache(MyBabyApp.getContext(),discoveryNcfcs,Constants.CacheKey_Discovery_ntfc,true);//缓存发现列表通知
				CacheDataTask.putCache(MyBabyApp.getContext(), otherNcfcs, Constants.CacheKey_NotificationOthers, true);//缓存发现列表通知
				LogUtil.e("服务器数据缓存已缓存总数,num" + data.size());
				sendSummaryNotificationBroadCast();
				mLastSyncingTime = System.currentTimeMillis();
				mIsSyncing = false;
				mNtfcSyncing =false;
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				mIsSyncing = false;
				mNtfcSyncing =false;
			}
		});
	}

	private static void sendSummaryNotificationBroadCast() {
		MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_Notification_Summary);
		MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_MainActivity_Home_TagBadgeUpdate);
		Utils.LogV("MyBaby", "发送广播：BroadcastAction_Notification_Summary");
	}


	public static void setLastPostTime(long lastPostTime) {
		Editor edit = MyBabyApp.getSharedPreferences().edit();
		edit.putLong("lastPostTime", lastPostTime);
		edit.commit();
	}


	public static void setCommon_notification_has_new(boolean common_notification_has_new) {
		Editor edit = MyBabyApp.getSharedPreferences().edit();
		edit.putBoolean("gencommon_has_new", common_notification_has_new);
		edit.commit();
	}

	public static void setCommunity_has_new(boolean community_has_new) {
		Editor edit = MyBabyApp.getSharedPreferences().edit();
		edit.putBoolean("community_has_new", community_has_new);
		edit.commit();
	}
	public static void setNearby_has_new(boolean nearby_has_new) {
		Editor edit = MyBabyApp.getSharedPreferences().edit();
		edit.putBoolean("nearby_has_new", nearby_has_new);
		edit.commit();
	}
	public static long getLastPostTime() {
		return MyBabyApp.getSharedPreferences().getLong("lastPostTime", 0);
	}

	public static Boolean getGencommon_has_new() {
		return MyBabyApp.getSharedPreferences().getBoolean("gencommon_has_new", false);
	}

	public static Boolean getCommunity_has_new() {
		return MyBabyApp.getSharedPreferences().getBoolean("community_has_new", false);
	}

	public static Boolean getNearby_has_new() {
		return MyBabyApp.getSharedPreferences().getBoolean("nearby_has_new", false);
	}

	/*public static long getLocalLastPostTime() {
		return MyBabyApp.getSharedPreferences().getLong("localLastPostTime", 0);
	}

	public static void setLocalLastPostTime(long lngPostTime) {
		Editor edit = MyBabyApp.getSharedPreferences().edit();
		edit.putLong("localLastPostTime", lngPostTime);
		edit.commit();
	}*/

}
