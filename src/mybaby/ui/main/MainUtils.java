package mybaby.ui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Parcelable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;

import org.xmlrpc.android.XMLRPCException;

import java.util.Locale;
import java.util.Map;

import me.hibb.mybaby.android.BuildConfig;
import me.hibb.mybaby.android.R;
import me.hibb.mybaby.android.openIM.LoginOpenImHelper;
import mybaby.Constants;
import mybaby.cache.CacheDataTask;
import mybaby.models.diary.MediaRepository;
import mybaby.models.friend.FriendRepository;
import mybaby.models.notification.NotificationCategory;
import mybaby.notification.RefreshNotificationManager;
import mybaby.rpc.BaseRPC;
import mybaby.service.UpdateService;
import mybaby.ui.MyBabyApp;
import mybaby.ui.Notification.adapter.NotificationCategoryAdapter;
import mybaby.ui.WelcomeActivity;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.user.PersonEditActivity;
import mybaby.ui.user.RegisterActivity;
import mybaby.util.LogUtils;
import mybaby.util.MapUtils;
import mybaby.util.MaterialDialogUtil;
import mybaby.util.Utils;
import me.hibb.mybaby.android.openIM.NotifacationListMerge;

public class MainUtils {
	private static final String USER_ID = "userId";
	private static final String PASSWORD = "password";


	/**
	 * 获取最新版本号，用以自动更新版本
	 */
	public static  void getServerVersionCode(final Activity context) {
		BaseRPC.rpcCallForReturnMaps("获取版本号RPC：", "bz.xmlrpc.system.option.get", BaseRPC.extParams(), new BaseRPC.CallbackForMaps() {
			@Override
			public void onSuccess(Map<?, ?> data) {
				if (data.containsKey("newVersion")) {
					int code = MapUtils.getMapInt(data, "newVersion");
					if (code > MyBabyApp.version) {
						new CustomAbsClass.doSomething(context) {
							@Override
							public void todo() {
								MaterialDialogUtil.showCommDialog(context, "更新提醒", "辣妈说 有一个新版本，是否更新？", "马上更新", null, new MaterialDialogUtil.DialogCommListener() {
									@Override
									public void todosomething() {
										Intent updateIntent = new Intent(context,
												UpdateService.class);
										context.startService(updateIntent);
									}
								});
							}
						};
					} else {
						LogUtils.e("服务器版本：" + code + "---不更新");
					}
				}
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {

			}
		});

	}
	/**
	 * OPENim登陆用户
	 */
	public static void loginIM() {
		new LoginIM() {
			@Override
			public void onLoginFail() {
				LogUtils.e("登录失败");
			}

			@Override
			public void onLoginSuccess() {
				LogUtils.e("完成登录");
			}
		}.loginIM();
	}

	public static YWIMKit getYWIMKit() {
		YWIMKit mIMKit=null;
		if (MyBabyApp.currentBlog==null||MyBabyApp.currentUser==null) {
			return null;
		}
		if (MyBabyApp.currentUser!=null) {
			if (!TextUtils.isEmpty(MyBabyApp.currentUser.getImUserName())) {
				mIMKit = YWAPI.getIMKitInstance(MyBabyApp.currentUser.getImUserName(),Constants.OPENIM_KEY);
			}
		}
			return mIMKit;
		}
	/**
	 * OPENim登陆用户
	 */
	public static abstract class  LoginIM{
		public abstract void onLoginFail();
		public abstract void onLoginSuccess();
		public void loginIM() {
			if (getYWIMKit() != null)
				LoginOpenImHelper.getInstance().login_Sample(MyBabyApp.currentUser.getImUserName(),
						MyBabyApp.currentUser.getimUserPassword(), Constants.OPENIM_KEY, new IWxCallback() {
							@Override
							public void onSuccess(Object... objects) {
								Constants.hasLoginOpenIM = true;
								MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction__OpenIM_Login_Success, null);
								Log.i("im login state", "true login");
							}

							@Override
							public void onError(int i, String s) {
								Constants.hasLoginOpenIM = false;
								Log.i("im login state", "false login");
							}

							@Override
							public void onProgress(int i) {

							}
						});
		}
	}

	public static boolean community_needRefresh=false;
	/**
	 * others节点红点通知
	 */
	public static void updateTabBadgeByNotification(ViewGroup view,final Toolbar homeView) {
		/*int ha=MyBabyApp.currentUser == null ? 0 : (MyBabyApp.currentUser.getBzRegistered()? 0: -1);
		if (ha!=0)
			updateTabBadge(view,MyBayMainActivity.meTabIndex, ha);*/
		updateTabBadge(view, MyBayMainActivity.communityTabIndex, 0);//先清空一遍
		updateTabBadge(view, MyBayMainActivity.shoppingTabIndex, 0);
		updateTabBadge(view, MyBayMainActivity.meTabIndex, 0);


		int noti_unread = 0;
		try {
			TextView badge = (TextView) homeView.findViewById(R.id.notification_redtag);

			noti_unread =  NotifacationListMerge.getInstance().getUnreadCount(MyBayMainActivity.activity);
			if(noti_unread>0) {
				updateTabBadge(view, MyBayMainActivity.communityTabIndex, noti_unread);
				NotificationCategoryAdapter.setTextBgRedUnread(badge, noti_unread,true,R.color.red,R.drawable.badge_bg_while);
			}else{
				NotificationCategoryAdapter.setNotificationTextBgRedUnread(badge, 0,true);
			}
		}catch (Exception e) {
			LogUtils.e("MyBaby", "mb_community_notification：" + e.getLocalizedMessage());
		}

		Object[] otherNcfcs = CacheDataTask.getObjs(MyBabyApp.getContext(), Constants.CacheKey_NotificationOthers);//缓存的Others节点列表通知
		if (otherNcfcs==null||otherNcfcs.length==0) {
			return;
		}
		for (Object obj:otherNcfcs){
			NotificationCategory category= (NotificationCategory) obj;
			switch (category.getKey()){
				//社区红点
				case "mb_community_notification":
					community_needRefresh=false;
					if (noti_unread <= 0) {
						updateTabBadge(view, MyBayMainActivity.communityTabIndex, category.getUnreadCount(), category.isStrongRemind());
						if ((category.getUnreadCount()!=0)&&(category.isStrongRemind()==false)){
							community_needRefresh=true;
						}
					}
					break;
				//辣妈街
				case "mb_shopping_notification":
					if (TextUtils.isEmpty(category.getNewestDesc()))
						updateTabBadge(view, MyBayMainActivity.shoppingTabIndex, category.getUnreadCount(), category.isStrongRemind());
					else
						updateTabBadge(view,MyBayMainActivity.shoppingTabIndex,category.getNewestDesc(),category.isStrongRemind());
					break;
				//我
				case "mb_me_notification":
					updateTabBadge(view,MyBayMainActivity.meTabIndex, category.getUnreadCount(),category.isStrongRemind());
					break;
			}
		}

	}
	public static void updateTabBadge(ViewGroup view,int index, int badgeNumber) {
		updateTabBadge(view,index, badgeNumber,true);
	}
	/**
	 * 更新红点显示
	 * @param index
	 * @param badgeNumber
	 */
	public static void updateTabBadge(ViewGroup view,int index, int badgeNumber,boolean isStrong) {
		updateTabBadge(view,index,String.valueOf(badgeNumber),isStrong);
	}
	public static void updateTabBadge(ViewGroup view,int index, String badgeText,boolean isStrong) {
		try {
			if (view==null)
				return;
			View tabIndicator =view.getChildAt(index);
			TextView badge = (TextView) tabIndicator
					.findViewById(R.id.tab_badge);
			NotificationCategoryAdapter.setTextBgRedUnread(badge, badgeText, isStrong);
		} catch (Exception e) {
			Utils.LogV("MyBaby", "updateTabBadge失败：" + e.getLocalizedMessage());
		}
	}

	public static long exitTime = 0;
	public static void positionAtActoionBarView(final Toolbar homeView, final int position){
		if (null==homeView)
			return;
		RelativeLayout regist_rela= (RelativeLayout) homeView.findViewById(R.id.regist_rela);
		RelativeLayout notification_rela= (RelativeLayout) homeView.findViewById(R.id.notification_rela);

		TextView appname_tv= (TextView) homeView.findViewById(R.id.appname_tv);
		ProgressBar getNewsProgressBar= (ProgressBar) homeView.findViewById(R.id.getNewsProgressBar);
		exitTime=0;
		homeView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ((System.currentTimeMillis() - exitTime) > 500) {
					exitTime = System.currentTimeMillis();
				} else {
					MyBayMainActivity.sendIsNeedBackTopBroadCast(MyBayMainActivity.getCurrentTag(position),null);
				}
			}
		});
		if (!regist_rela.hasOnClickListeners())
			regist_rela.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					CustomAbsClass.starRegister(homeView.getContext());
				}
			});
		if (!notification_rela.hasOnClickListeners())
			notification_rela.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CustomAbsClass.starNotificationCategoryActivity(homeView.getContext());
				}
			});
		//homeView.setTitle(Constants.APP_NAME);
		homeView.setVisibility(View.GONE);
		regist_rela.setVisibility(View.GONE);
		notification_rela.setVisibility(View.GONE);
		getNewsProgressBar.setVisibility(View.GONE);
		switch (position){
			case MyBayMainActivity.meTabIndex:
				appname_tv.setText("我");
				homeView.setVisibility(View.VISIBLE);
				regist_rela.setVisibility(MyBabyApp.currentUser != null && MyBabyApp.currentUser.getBzRegistered() ? View.GONE : View.VISIBLE);
				break;
			case MyBayMainActivity.communityTabIndex:
				appname_tv.setText("社区");
				homeView.setVisibility(View.VISIBLE);
				notification_rela.setVisibility(View.VISIBLE);
				break;
			case MyBayMainActivity.homeTabIndex:
				appname_tv.setText("主页");
				break;
//			case MyBayMainActivity.threeTabIndex:
//				appname_tv.setText("消息");
//				homeView.setVisibility(View.VISIBLE);
//				getNewsProgressBar.setVisibility(RefreshNotificationManager.mNtfcSyncing?View.VISIBLE:(NotificationCategoryFragment.mNtfcSyncing?View.VISIBLE:View.GONE));
//				break;
			case MyBayMainActivity.shoppingTabIndex:
				appname_tv.setText("购物助手");
				homeView.setVisibility(View.VISIBLE);
				try {
					((MyBayMainActivity)MyBayMainActivity.activity).getStreeFragment().perfError();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
		}
	}

	public void ExitApp1()
    {
    	/*Timer tExit = null;
		if (isExit == false) {
			isExit = true;
			Toast.makeText(this, R.string.zaianyici, 0).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false;
				}
			}, 2000);
		} else {
			saveExitTag();
			System.exit(0);
		}*/
    }
	
	//主界面的上下文
	public static Activity getActivity(){
		return MyBayMainActivity.activity;
	}
	
	public void showAddMenu(final Activity activity) {
		// 添加宝宝和家人
		final String[] items = { activity.getString(R.string.baby),
				activity.getString(R.string.my_family) };
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.add);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) { // add baby
					Intent intent = new Intent(activity,
							PersonEditActivity.class);
					intent.putExtra("isBaby", true);
					activity.startActivity(intent);
				} else if (which == 1) { // add family
					Intent intent = new Intent(activity,
							PersonEditActivity.class);
					intent.putExtra("isBaby", false);
					activity.startActivity(intent);
				}
			}
		});
		Dialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
	}
	

	/**
	 * 提醒注册
	 * @param activity
	 * @param alreadyRemindSignUp
	 * @return
	 */
		public static boolean remindSignUp(final Activity activity,boolean alreadyRemindSignUp) {
			if (!alreadyRemindSignUp
					|| (MyBabyApp.currentUser != null && MyBabyApp.currentUser
							.getBzRegistered())
					|| System.currentTimeMillis()
							- MyBabyApp.currentUser.getBzCreateDate() < 1000 * 3600) {// 一小时后提醒注册
				return alreadyRemindSignUp;
			}
			alreadyRemindSignUp = true;

			MaterialDialogUtil.showCommDialog(activity,
					activity.getString(R.string.sign_up),
					activity.getString(R.string.users_can_log),
					activity.getString(R.string.sign_up_now), "下次", new MaterialDialogUtil.DialogCommListener() {
						@Override
						public void todosomething() {
							Intent intentSignUp = new Intent(activity,
									RegisterActivity.class);
							activity.startActivity(intentSignUp);
						}
					});
			return alreadyRemindSignUp;
		}
    //邮件反馈
	public static  void feedback(Context context){
		PackageManager manager;
		PackageInfo info = null;
		manager = MyBabyApp.getContext().getPackageManager();
		try {
			info = manager.getPackageInfo(MyBabyApp.getContext().getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    	
	    Intent data=new Intent(Intent.ACTION_SEND); 
	   // if(Utils.isChina()){
	    	data.putExtra(Intent.EXTRA_EMAIL, new String[]{"service@hibb.me"});
	   // }else{
	    //	data.putExtra(Intent.EXTRA_EMAIL, new String[]{"service.mybaby@hotmail.com"});
	   // }
	    data.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback)); 
	    data.setType("message/rfc822");
	    data.putExtra(Intent.EXTRA_TEXT,
	    			    "--------------------------------------\n"
	    				+ Constants.APP_NAME + ": " + (info==null?"":info.versionCode) + " "
	    					+ MyBabyApp.currentUser.getUserId() + " " + (MyBabyApp.currentUser.getBzRegistered()?1:0) +" "
	    					+ (MyBabyApp.currentUser.getFrdIsOpen()?1:0) +" " + MediaRepository.mediaCount(MyBabyApp.currentUser.getUserId()) + " " 
	    					+ FriendRepository.friendCount() + "\n"
	    				+ (MyBabyApp.currentUser.getUniqueName()==null ? "" : MyBabyApp.currentUser.getUniqueName() + "\n")
	    				+ android.os.Build.DEVICE + " Android " + android.os.Build.VERSION.RELEASE + " " 
	    					+ Locale.getDefault().getLanguage() + " " + Locale.getDefault().getCountry() + "\n"
	    				+"--------------------------------------\n"
	    			); 
	    context.startActivity(data); 
    }

	//创建快捷方式
		public static void createSystemSwitcherShortCut(Context context) {
			if(MyBabyApp.getSharedPreferences().getBoolean("autoInstallShortcutOnce", false)){
				return;
			}
			
			final Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			final Parcelable icon = Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher); // 获取快捷键的图标
			addIntent.putExtra("duplicate", false);
			Intent myIntent=null;
			if (MyBabyApp.currentBlog==null ) {//进入登入
				myIntent = new Intent(context, WelcomeActivity.class);
			} else {
				myIntent = new Intent("mybaby.me.action.SHORTCUT");
			}

			//myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //这句会导致Home键后点击图标返回回到主窗体问题
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, Constants.APP_NAME);// 快捷方式的标题
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);// 快捷方式的图标
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);// 快捷方式的动作
			context.sendBroadcast(addIntent);

			Editor edit=MyBabyApp.getSharedPreferences().edit();
			edit.putBoolean("autoInstallShortcutOnce", true);
			edit.commit();
		}
	
}
