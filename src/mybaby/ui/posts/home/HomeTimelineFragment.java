package mybaby.ui.posts.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.umeng.analytics.MobclickAgent;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.diary.Post;
import mybaby.models.diary.PostRepository;
import mybaby.models.person.Baby;
import mybaby.models.person.FamilyPerson;
import mybaby.models.person.SelfPerson;
import mybaby.rpc.PostRPC;
import mybaby.rpc.UserRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.base.BaseOnrefreshAndLoadMoreFragment;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.posts.TimelineListFragment;
import mybaby.ui.posts.person.PersonTimelineActivity;

public class HomeTimelineFragment extends TimelineListFragment {

	private LinearLayout mHeaderContainer;
	private HomeHeader homeHeader;
	private NewPhotos mNewPhotos;
	public static boolean needRefush = false;
	private MyBroadcastReceiver mBroadcastReceiver;

	private boolean mIsSyncing = false;
	private static long exitTime = 0;
	//private View titleBar;
	@Override
	public void onResume() {
		super.onResume();
		if (MyBabyApp.getSharedPreferences().getBoolean("isSignUp", false)) {
			MyBabyApp.getSharedPreferences().edit().putBoolean("isSignUp", false)
					.commit();
			//mPtrFrameLayout.setKeepHeaderWhenRefresh(MyBabyApp.currentUser.getBzRegistered());
		}
		MobclickAgent.onPageStart(getActivity().getString(R.string.rijizhuliebiao)); // 
		if (needRefush) {
			reLoadPost();
			needRefush = false;
		}
		//initTitleBar();
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getActivity().getString(R.string.rijizhuliebiao));
	}
	public static  int getScrollY(AbsListView mListView) {
		View c = mListView.getChildAt(0);
		if (c == null) {
			return 0;
		}
		int firstVisiblePosition = mListView.getFirstVisiblePosition();
		int top = c.getTop();
		return -top + firstVisiblePosition * c.getHeight() ;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		if (!getUser().isSelf()) {
			getActivity().getActionBar().setTitle(getUser().getName());

			if (mUser.getLastActivityTime() > 0
					&& (mUser.getLastPostsSyncTime() == 0
							|| mUser.getLastActivityTime() > mUser
									.getLastPostsSyncTime())) {
				refreshHomeTimeline();
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		if (mNewPhotos != null) {
			mNewPhotos.refreshView();
		}
		NewPhotos.setHomePhotosLastDatetime(System.currentTimeMillis());
		MyBabyApp.sendLocalBroadCast(
				Constants.BroadcastAction.BroadcastAction_Post_Home_Open);
	}

	@Override
	protected void Refush() {
		// TODO Auto-generated method stub
		if (refushOk) {
			if (MyBabyApp.currentUser.getBzRegistered())
				reLoadPost();
			else
				onLoad();
		}else {
			LogUtil.e("on refreshing");
		}
	}

	@Override
	protected void init() {
		mBroadcastReceiver = new MyBroadcastReceiver();
		mBroadcastReceiver.registerMyReceiver();
	}


	@Override
	protected View createHeader() {
		SelfPerson mSelfPerson = PostRepository.loadSelfPerson(mUser.getUserId());
		Baby[] mBabies = PostRepository.loadBabies(mUser.getUserId());
		FamilyPerson[] mFamilyPersons = PostRepository.loadFamilyPersons(mUser.getUserId());

		if (mHeaderContainer==null) {
			mHeaderContainer = new LinearLayout(getActivity());
			mHeaderContainer.setOrientation(LinearLayout.VERTICAL);
		}else
			mHeaderContainer.removeAllViews();

		if (homeHeader==null)
			homeHeader = new HomeHeader(getActivity());
		mHeaderContainer.addView(homeHeader.setHeadData(mRecyclerView,
				mUser, mSelfPerson, mBabies, mFamilyPersons));

		
		if (mUser.isSelf()) {
			TodayWrite todayWrite = new TodayWrite(this, getAgeForBaby());
			mHeaderContainer.addView(todayWrite.getTodayWriteView());

			mNewPhotos = new NewPhotos(getActivity(), mHeaderContainer);
			mNewPhotos.refreshView();
		} else {
			// 间隔
			TextView textView = new TextView(getActivity());
			textView.setHeight((int) (30 * MyBabyApp.density));
			textView.setWidth(MyBabyApp.screenWidth);
			mHeaderContainer.addView(textView);
		}
		return mHeaderContainer ;
	}

	@Override
	protected void reCreateHeader(final Intent intent) {
		if (intent.getAction()
				.equals(Constants.BroadcastAction.BroadcastAction_Person_Add)) {
			int pid = intent.getIntExtra("id", 0);
			Intent i = new Intent(getActivity(), PersonTimelineActivity.class);
			i.putExtra("personId", pid);
			getActivity().startActivity(i);

			final HorizontalScrollView scroll = (HorizontalScrollView) mHeaderContainer
					.findViewById(R.id.home_timeline_header_content_scroll);
			scroll.postDelayed(new Runnable() {
				public void run() {
					try {
						scroll.setSmoothScrollingEnabled(true);
						scroll.scrollTo(scroll.getMaxScrollAmount(), 0);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 500);
		}
	}

	@Override
	protected Baby getAgeForBaby() {
		Baby[] mBabies=PostRepository.loadBabies(mUser.getUserId());
		if (mBabies==null)
			return null;
		if (mBabies.length == 1) {
			return mBabies[0];
		} else {
			return null;
		}
	}

	@Override
	protected List<Post> getPosts() {
		Post[] mPosts=null;
		List<Post> postList=new ArrayList<>();
		try {
			mPosts = PostRepository.loadPostsByUser(mUser.getUserId());
		} catch (Exception e1) {
			return postList;
		}
		if (mPosts!=null)
			for (Post post:mPosts)
				postList.add(post);
		return postList;
	}

	/*@Override
	protected void loadPosts() {
		Post[] mPosts=null;
		try {
			mPosts = PostRepository.loadPostsByUser(mUser.getUserId());
		} catch (Exception e1) {
			return;
		}
		List<Post> postList=new ArrayList<>();
		if (mPosts!=null)
			for (Post post:mPosts)
				postList.add(post);
		super.loadPosts();
	}*/

	private void refreshHomeTimeline() {
		if (mIsSyncing) {
			return;
		}
		if (((ConnectivityManager) getActivity().getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE))
						.getActiveNetworkInfo() == null) {
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getActivity(), R.string.no_connection,
							Toast.LENGTH_SHORT).show();
				}
			});

			return; // 没有连接网络
		}

		mIsSyncing = true;
		getActivity().setProgressBarIndeterminateVisibility(true);

		UserRPC.getUserInfo(mUser, new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				PostRPC.getPosts(mUser, new XMLRPCCallback() {
					@Override
					public void onSuccess(long id, Object result) {
						mIsSyncing = false;
						if (mUser.isSelf()) {
							MyBabyApp.sendLocalBroadCast(
									Constants.BroadcastAction.BroadcastAction_Refresh_Self_HomeTimeline_Success);
						} else {
							MyBabyApp.sendLocalBroadCast(
									Constants.BroadcastAction.BroadcastAction_Refresh_Friend_HomeTimeline_Success);
						}
					}

					@Override
					public void onFailure(long id, XMLRPCException error) {
						mIsSyncing = false;
						MyBabyApp.sendLocalBroadCast(
								Constants.BroadcastAction.BroadcastAction_Refresh_HomeTimeline_Faile);
					}
				});
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				mIsSyncing = false;
				MyBabyApp.sendLocalBroadCast(
						Constants.BroadcastAction.BroadcastAction_Refresh_HomeTimeline_Faile);
			}
		});

	}



	@Override
	public void onDestroy() {
		if (mBroadcastReceiver != null)
			LocalBroadcastManager.getInstance(MyBabyApp.getContext())
					.unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	public static void backListTop(final ListView listView) {
		if (listView==null)
			return;
		if (listView.getFirstVisiblePosition() > 4) {
			listView.setSelection(4);
		}
		listView.smoothScrollToPositionFromTop(0, 0, 150);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (listView.getFirstVisiblePosition() != 0) {
					listView.setSelection(0);
				}
			}
		}, 200);

	}
	public static void backListTop(final RecyclerView mRecyclerView) {
		if (mRecyclerView==null)
			return;
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				BaseOnrefreshAndLoadMoreFragment.scrollToTop(mRecyclerView);
			}
		});

	}

	// 广播接收并响应处理
	private class MyBroadcastReceiver extends BroadcastReceiver {
		public void registerMyReceiver() {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Post_Add);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Post_Edit);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Post_Delete);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Person_Add);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Person_Edit);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Person_Delete);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Refresh_Self_HomeTimeline_Notification);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Refresh_Self_HomeTimeline_Success);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Refresh_Friend_HomeTimeline_Success);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Refresh_HomeTimeline_Faile);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Activity_Main_Need_BackTop);
			LocalBroadcastManager.getInstance(MyBabyApp.getContext())
					.registerReceiver(this, filter);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals(Constants.BroadcastAction.BroadcastAction_Post_Add)
					|| intent.getAction()
							.equals(Constants.BroadcastAction.BroadcastAction_Post_Edit)
					|| intent.getAction()
							.equals(Constants.BroadcastAction.BroadcastAction_Post_Delete)) {
				mListAdapter.setNewData(getPosts());
				if (intent.getAction().equals(
						Constants.BroadcastAction.BroadcastAction_Post_Add)) {
					int pid = intent.getIntExtra("id", 0);
					scrollTo(pid);
				}
			} else if (intent.getAction()
					.equals(Constants.BroadcastAction.BroadcastAction_Person_Add)
					|| intent.getAction()
							.equals(Constants.BroadcastAction.BroadcastAction_Person_Edit)
					|| intent.getAction().equals(
							Constants.BroadcastAction.BroadcastAction_Person_Delete)) {
				reCreateHeader(intent);
				mListAdapter.addHeaderView(createHeader());
				mListAdapter.setNewData(getPosts());
			} else if (intent.getAction().equals(
					Constants.BroadcastAction.BroadcastAction_Refresh_Self_HomeTimeline_Notification)) {
				if (mUser.isSelf()) {
					refreshHomeTimeline();
				}
			} else if (intent.getAction().equals(
					Constants.BroadcastAction.BroadcastAction_Refresh_Self_HomeTimeline_Success)) {
				if (mUser.isSelf()) {
					reCreateHeader(intent);
					mListAdapter.addHeaderView(createHeader());
					mListAdapter.setNewData(getPosts());
				}
			} else if (intent.getAction().equals(
					Constants.BroadcastAction.BroadcastAction_Refresh_Friend_HomeTimeline_Success)) {
				if (!mUser.isSelf()) {
					reCreateHeader(intent);
					mListAdapter.setNewData(getPosts());
				}
			} else if (intent.getAction().equals(
					Constants.BroadcastAction.BroadcastAction_Refresh_HomeTimeline_Faile)) {
			} else if (intent.getAction().equals(
					Constants.BroadcastAction.BroadcastAction_Activity_Main_Need_BackTop)) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					String tag = bundle.getString("currentTag", "");
					if (!TextUtils.isEmpty(tag)
							&& tag.equals(MyBayMainActivity.homeTabTag)) {
						//backListTop(mRefreshListView);
					}
				}
			}else if (intent.getAction().equals(
					Constants.BroadcastAction.BroadcastAction_Sign_Up_Done)) {
				//titleBar.findViewById(R.id.regist_rela).setVisibility(View.GONE);//登陆成功，注册消失
			}
		}
	}
}
