package mybaby.ui.main;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.conversation.IYWConversationListener;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeMember;
import com.alibaba.mobileim.gingko.presenter.contact.IContactProfileUpdateListener;
import com.alibaba.mobileim.gingko.presenter.contact.YWContactManagerImpl;
import com.alibaba.mobileim.gingko.presenter.tribe.IYWTribeChangeListener;
import com.alibaba.mobileim.kit.common.IMUtility;
import com.alibaba.mobileim.tribe.IYWTribeService;
import com.alibaba.wxlib.util.SysUtil;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.R;
import me.hibb.mybaby.android.openIM.NotifacationListMerge;
import me.hibb.mybaby.android.openIM.TribeHelper;
import mybaby.Constants;
import mybaby.models.notification.NotificationCategory;
import mybaby.ui.MyBabyApp;
import mybaby.ui.Notification.adapter.NotificationCategoryAdapter;
import mybaby.ui.broadcast.BackTopListviewReceiver;
import mybaby.ui.broadcast.PostMediaTask;
import mybaby.ui.posts.home.HomeTimelineFragment;

public class NotificationCategoryFragment extends Fragment  {

	private ObservableRecyclerView recyclerView;
	private ProgressBar progress_refush;

	private NotificationCategoryAdapter ntfcAdapter;
	//private List<NotificationCategory> ntfcCategoryList=new ArrayList<>();
	private NotifacationListMerge notifacationListMerge;

	public static UMSocialService newsController = UMServiceFactory
			.getUMSocialService(Constants.DESCRIPTOR);
	public static boolean mNtfcSyncing;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.recyclerview_base, null);
		recyclerView = (ObservableRecyclerView) rootView.findViewById(R.id.recycle_view);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		progress_refush = (ProgressBar) rootView.findViewById(R.id.progress_refush);
		progress_refush.setVisibility(View.VISIBLE);
		new LoadDataAsyn().execute();
		return rootView;
	}


	protected class LoadDataAsyn extends AsyncTask<Void, Void, String[]> {
		public LoadDataAsyn() {
			// TODO Auto-generated constructor stub
		}

		@Override
		protected String[] doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		// 完成
		@Override
		protected void onPostExecute(String[] result) {
			// TODO Auto-generated method stub
			MainUtils.loginIM();
			init();
			super.onPostExecute(result);
		}

	}

	/**
	 * 初始化一些云旺会话列表和群列表回调、更新的配置
	 */
	public void init() {
		new UpdateReceiver().registerUpdateReceiver();//注册广播
		new BackTopListviewReceiver(new BackTopListviewReceiver.ToDoListener() {
			@Override
			public void todo(Bundle bundle) {
				String tag=bundle.getString("currentTag", "");
//				if (!TextUtils.isEmpty(tag)&&tag.equals(MyBayMainActivity.threeTabTag)) {
//					HomeTimelineFragment.backListTop(recyclerView);
//				}
			}
		}).regiest();
		mNtfcSyncing=true;
		notifacationListMerge=new NotifacationListMerge();
		ntfcAdapter=new NotificationCategoryAdapter(getActivity(),new ArrayList<NotificationCategory>());
		recyclerView.setAdapter(ntfcAdapter);
		initCache();
		initIMOnclick();
	}

	public void initIMOnclick(){
		mIMKit= MyBayMainActivity.getmIMKit();
		if (mIMKit==null)
			return;
		TribeHelper.initCallbackIMUserClick();//初始化头像点击事件

		tribeService=mIMKit.getTribeService();
		if (tribeService==null)
			return;
		tribeService.addTribeListener(tribeChangeListener);

		YWContactManagerImpl impl= (YWContactManagerImpl) mIMKit.getContactService();
		impl.addProfileUpdateListener(updateListener);

		// 添加会话列表变更监听
		conversationService = mIMKit.getConversationService();
		if (conversationService==null)
			return;
		conversationService.addConversationListener(new IYWConversationListener() {
			@Override
			public void onItemUpdated() {
				refreshNotificationCagoryList();
			}
		});
	}

	private YWIMKit mIMKit;
	private IYWTribeService tribeService;
	private IYWConversationService conversationService;

	private void initCache(){
		refreshAdapter(notifacationListMerge.getNtcList(getActivity()));
	}
	/**
	 * 支持动态新增通知
	 */
	private void refreshNotificationCagoryList() {
		/*if (Constants.hasLoginOpenIM) {
			if (conversationService!=null)
				refreshAdapter(notifacationListMerge.getNtcLatestList(getActivity(), conversationService.getConversationList(), null));
		}else*/

		refreshAdapter(notifacationListMerge.getNtcLatestList(getActivity(), conversationService==null?null:conversationService.getConversationList(), null));
	}
	/**
	 * 获取列表所有未读消息总和
	 */
	public int getUnread() {
		if (ntfcAdapter==null)
			return 0;
		return ntfcAdapter.getUnReadCount();
	}
	private synchronized void refreshAdapter(final List<NotificationCategory> newList){
		mNtfcSyncing=true;
		MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_StargetNewestSummaryFromService);
		recyclerView.post(new Runnable() {
			@Override
			public void run() {
				ntfcAdapter.setNewData(newList);
				progress_refush.setVisibility(View.GONE);
				mNtfcSyncing = false;
				int oldUnread = MyBabyApp.getSharedPreferences().getInt("unread", 0);
				int newUnread = ntfcAdapter.getUnReadCount();
				SharedPreferences.Editor edit = MyBabyApp.getSharedPreferences().edit();
				edit.putInt("unread", newUnread);
				edit.commit();
				if (newUnread > oldUnread && SysUtil.isForeground()) {
					Object objVIBRATOR_SERVICE = getActivity().getSystemService(Service.VIBRATOR_SERVICE);
					if (objVIBRATOR_SERVICE != null) {
						Vibrator vib = (Vibrator) objVIBRATOR_SERVICE;
						vib.vibrate(new long[]{100, 200}, -1);
					}
				}
				PostMediaTask.sendBroadcast(Constants.BroadcastAction.BroadcastAction_Notification_Summary, null);
			}
		});
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("消息"); //统计页面
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("消息");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		UMSsoHandler ssoHandler = newsController.getConfig()
				.getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private IContactProfileUpdateListener updateListener=new IContactProfileUpdateListener() {
		@Override
		public void onProfileUpdate() {
			refreshNotificationCagoryList();
		}

		@Override
		public void onProfileUpdate(String s, String s1) {
			refreshNotificationCagoryList();
		}
	};

	private IYWTribeChangeListener tribeChangeListener= new IYWTribeChangeListener() {
		@Override
		public void onInvite(YWTribe ywTribe, YWTribeMember ywTribeMember) {
			/**
			 * 被邀请加入群
			 * @param tribe 群信息
			 * @param user 邀请发起者
			 */
			//Toast.makeText(getActivity(), "邀请加入群:" + ywTribe.getTribeName(), Toast.LENGTH_SHORT).show();
			MyBayMainActivity.getmIMKit().getTribeService().getMembersFromServer(null, ywTribe.getTribeId());//将群成员信息同步一下本地
			SharedPreferences.Editor edit = MyBabyApp.getSharedPreferences().edit();
			edit.putBoolean("tribeHasInvite", true);
			edit.putString("tribeTitle", "群系统消息");
			edit.putString("tribeContent", "您有新的群邀请");
			edit.putString("tribeUrl", Constants.MY_BABY_InvaidURL);
			edit.putLong("tribeTime", System.currentTimeMillis());
			edit.putInt("tribeUnreadNum", 1);
			edit.commit();

			refreshNotificationCagoryList();
		}

		@Override
		public void onUserJoin(YWTribe ywTribe, YWTribeMember ywTribeMember) {
			/**
			 * 用户加入群
			 * @param tribe
			 * @param userId
			 */
			IMUtility.getContactProfileInfo(ywTribeMember.getUserId(), ywTribeMember.getAppKey());
			refreshNotificationCagoryList();
		}

		@Override
		public void onUserQuit(YWTribe ywTribe, YWTribeMember ywTribeMember) {
			/**
			 * 用户退出群
			 * @param tribe
			 * @param userId
			 */
			//Toast.makeText(getActivity(), "用户退出群", Toast.LENGTH_SHORT).show();
			refreshNotificationCagoryList();
		}

		@Override
		public void onUserRemoved(YWTribe ywTribe, YWTribeMember ywTribeMember) {
			/**
			 * 用户被请出群
			 * @param tribe
			 * @param userId
			 */
			refreshNotificationCagoryList();
			//Toast.makeText(getActivity(), "用户被请出群", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onTribeDestroyed(YWTribe ywTribe) {
			/**
			 * 停用群
			 * @param tribe
			 */
			refreshNotificationCagoryList();
			//Toast.makeText(getActivity(), "停用群", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onTribeInfoUpdated(YWTribe ywTribe) {
			refreshNotificationCagoryList();
			/**
			 * 群信息更新`
			 * @param tribe
			 * @param tribeName
			 * @param announce
			 */

			//Toast.makeText(getActivity(), "群信息更新", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onTribeManagerChanged(YWTribe ywTribe, YWTribeMember ywTribeMember) {
			/**
			 * 群管理员变更
			 * @param tribe
			 * @param newManager
			 */
			//Toast.makeText(getActivity(), "群管理员变更", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onTribeRoleChanged(YWTribe ywTribe, YWTribeMember ywTribeMember) {
			//Toast.makeText(getActivity(), "群角色改变", Toast.LENGTH_SHORT).show();
		}
	};

	// 广播接收并响应处理 刷新
	private class UpdateReceiver extends BroadcastReceiver {
		public void registerUpdateReceiver() {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.BroadcastAction.BroadcastAction__OpenIM_Login_Success);
			filter.addAction(Constants.BroadcastAction.BroadcastAction__OpenIM_Notification_Change);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_NotificationCagetory_Refush);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_MainActivity_Home_TagBadgeUpdate);
			LocalBroadcastManager.getInstance(MyBabyApp.getContext())
					.registerReceiver(this, filter);
		}

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (arg1.getAction().equals(Constants.BroadcastAction.BroadcastAction__OpenIM_Login_Success)||
					arg1.getAction().equals(Constants.BroadcastAction.BroadcastAction__OpenIM_Notification_Change)||
					arg1.getAction().equals(Constants.BroadcastAction.BroadcastAction_NotificationCagetory_Refush)||
					arg1.getAction().equals(Constants.BroadcastAction.BroadcastAction_MainActivity_Home_TagBadgeUpdate)) {
				if (arg1.getAction().equals(Constants.BroadcastAction.BroadcastAction__OpenIM_Login_Success)){
					initIMOnclick();
				}
				refreshNotificationCagoryList();
			}
		}
	}
}
