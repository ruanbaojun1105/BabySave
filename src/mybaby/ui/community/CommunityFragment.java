package mybaby.ui.community;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import org.xmlrpc.android.XMLRPCException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.community.Banner;
import mybaby.rpc.community.CommunityItemRPC;
import mybaby.share.UmengShare;
import mybaby.ui.MediaHelper;
import mybaby.ui.MediaHelper.MediaHelperCallback;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.adapter.CommunityFragmentPagerAdapter;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.community.fragment.LoadMoreRecyclerViewFragment;
import mybaby.ui.community.holder.ItemState;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.posts.home.HomeTimelineFragment;
import mybaby.ui.widget.DoubleViewPager;

//社区主页
@SuppressWarnings("deprecation")
@Deprecated
public class CommunityFragment extends Fragment implements
MediaHelperCallback{

	private View rootView;// 缓存Fragment view
	private List<Fragment> fragmentList=new ArrayList<>();
	private Context context;
	private DoubleViewPager pager;
	private ActionBar actionBar;
	public static MediaHelper mMediaHelper;
	private int showPager;
	private InfoReceiver receiver;
	public static UMSocialService communityController = UMServiceFactory
			.getUMSocialService(Constants.DESCRIPTOR);
	private long exitTime = 0;
	private mybaby.ui.community.fragment.LoadMoreListViewFragment fragment1 ;
	private mybaby.ui.community.fragment.LoadMoreListViewFragment fragment2 ;
	private CommunityFragmentPagerAdapter adapter;

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("社区"); // 统计页面
		actionBar = MyBayMainActivity.activity.getActionBar();
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("社区");
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		MyBabyApp.initScreenParams(this.getActivity());
	}


	public void setRootView(View rootView) {
		this.rootView = rootView;
	}

	@SuppressLint("ResourceAsColor")
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_community, null);
			initView();
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	private void initView() {
		// TODO Auto-generated method stub
		try {
			actionBar = MyBayMainActivity.activity.getActionBar();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMediaHelper = new MediaHelper(getActivity(), this);
		context = getActivity();
		receiver=new InfoReceiver();
		receiver.registerInfoReceiver();
		initPage(true);
		showPagerBySaveExitTab();
		// 配置需要分享的相关平台
		new UmengShare().configPlatforms(communityController, context);
	}

	//打开应用时，打开上次浏览的页面
	private void showPagerBySaveExitTab() {
		showPager = MyBabyApp.getSharedPreferences().getInt("showPager", 0);
		pager.setCurrentItem(showPager);
		if(showPager == 1){
			Editor edit = MyBabyApp.getSharedPreferences().edit();
			edit.putInt("showPager", 0);
			edit.commit();
		}
	}
	public static final LoadMoreListViewFragment getFragment1() {
		LoadMoreListViewFragment fragment1 =new LoadMoreListViewFragment(Constants.CacheKey_CommunityActivity_Main,30,true);
		Bundle bundle = new Bundle();
		bundle.putString("key", "key");
		fragment1.setArguments(bundle);
		fragment1.setOnTRpcInternet(initView(fragment1, 0));
		return fragment1;
	}

	public static final LoadMoreListViewFragment getFragment2() {
		LoadMoreListViewFragment fragment2 = new LoadMoreListViewFragment(Constants.CacheKey_CommunityActivity_Near, 30, true);
		Bundle bundle = new Bundle();
		bundle.putString("key", "key");
		fragment2.setArguments(bundle);
		fragment2.setOnTRpcInternet(initView(fragment2, 1));
		//fragment1.initData();
		return fragment2;
	}

	public CommunityFragmentPagerAdapter getAdapter() {
		return adapter;
	}

	public CommunityFragmentPagerAdapter creatAdapter() {
		adapter=new CommunityFragmentPagerAdapter(getChildFragmentManager(), new String[]{"全部", "热门"}, fragmentList);
		return adapter;
	}


	public List<Fragment> creatFragmentList() {
		fragmentList.clear();
		fragment1=getFragment1();
		fragment2=getFragment2();

		fragmentList.add(fragment1);
		fragmentList.add(fragment2);
		return fragmentList;
	}

	private void initPage(boolean isInit) {
		// TODO Auto-generated method stub

		fragment1=null;
		fragment2=null;
		adapter=null;

		pager = (DoubleViewPager) rootView.findViewById(R.id.viewpager);
		pager.setOffscreenPageLimit(2);// 预加载

		creatFragmentList();

		if (isInit){
			pager.setAdapter(creatAdapter());
		}else {
			//fragment1.initData();
			//fragment2.initData();
			adapter.notifyDataSetChanged();
		}
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			@SuppressLint({ "ResourceAsColor", "NewApi" })
			@Override
			public void onPageSelected(int position) {
				// 当viewPager发生改变时，同时改变tabhost上面的currentTab
				try {
					/*View view=MyBayMainActivity.titleBar_community;
					TextView t1=(TextView)view.findViewById(R.id.dongtai_tag);
					TextView t1a=(TextView)view.findViewById(R.id.dongtai_icon);
					TextView t2=(TextView)view.findViewById(R.id.zhoubian_tag);
					TextView t2a=(TextView)view.findViewById(R.id.zhoubian_icon);
					MainUtils.setbg(t1, t1a, t2, t2a, position==0);*/
					//LoadMoreListViewFragment.askRed();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//MyBayMainActivity.ActionbarPoint=position;
				//LogUtils.e(MyBayMainActivity.ActionbarPoint+"comm");
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}
	/**
	 * 初始化刷新数据
	 */
	public static LoadMoreListViewFragment.TRpc initView(final LoadMoreListViewFragment neighborFragment,int current) {
		// TODO Auto-generated method stub
		if (current == 0) {
			return new LoadMoreListViewFragment.TRpc() {

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception {

				}

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId,
										   final boolean isRefresh, LoadMoreListViewFragment fragment) {
					// TODO Auto-generated method stub
					CommunityItemRPC.getForMain(lastId, new CommunityItemRPC.ListCallback() {

						@Override
						public void onSuccess(boolean hasMore, int newLastId,
											  List<ItemState> items, Banner[] banners) {
							// TODO Auto-generated method stub
							try {
								neighborFragment.onTSuccessToList(isRefresh,
                                        hasMore, newLastId, items);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(long id, XMLRPCException error) {
							// TODO Auto-generated method stub
							try {
								neighborFragment.onTFailToList(id, error);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}

				@Override
				public void toTRpcInternet(Object[] objects, int lastId, boolean isRefresh, Fragment fragment) throws Exception {

				}
			};
		} else if (current == 1) {
			return new LoadMoreListViewFragment.TRpc() {

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception {

				}

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId,
										   final boolean isRefresh, LoadMoreListViewFragment fragment) {
					// TODO Auto-generated method stub
					CommunityItemRPC.getForNearby(lastId, new CommunityItemRPC.ListCallback() {

						@Override
						public void onSuccess(boolean hasMore, int newLastId,
											  List<ItemState> items, Banner[] banners) {
							// TODO Auto-generated method stub
							try {
								neighborFragment.onTSuccessToList(isRefresh,
                                        hasMore, newLastId, items);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(long id, XMLRPCException error) {
							// TODO Auto-generated method stub
							try {
								neighborFragment.onTFailToList(id, error);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}

				@Override
				public void toTRpcInternet(Object[] objects, int lastId, boolean isRefresh, Fragment fragment) throws Exception {

				}
			};
		}
		return null;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		UMSsoHandler ssoHandler = communityController.getConfig()
				.getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		mMediaHelper.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onMediaFileDone(String[] mediaFilePaths) {
		// TODO Auto-generated method stub
		// 打开日记
		if (Constants.category!=null) {
			CustomAbsClass.starTopicEditIntent(context, Constants.category.getId(), Constants.category.getTitle(), null, Arrays.asList(mediaFilePaths));
			Constants.category=null;
		}
		
	}
	
	// 广播接收并响应处理 地点设置成功返回刷新
	private class InfoReceiver extends BroadcastReceiver {
		public void registerInfoReceiver() {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.BroadcastAction.BroadcastAction_CommunityMain_ImageUpLoading);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_CommunityMain_Refush);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Activity_Main_Need_BackTop);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done);
			LocalBroadcastManager.getInstance(MyBabyApp.getContext())
					.registerReceiver(this, filter);
		}

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done)){
				try {
					fragment1.autoRefresh();
					fragment2.autoRefresh();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Activity_Main_Need_BackTop)) {
				Bundle bundle=intent.getExtras();
				if (bundle!=null) {
					String tag=bundle.getString("currentTag", "");
					if (!TextUtils.isEmpty(tag)&&tag.equals(MyBayMainActivity.communityTabTag)) {
						if (pager.getCurrentItem() == 0) {
							if (fragment1==null)
								return;
							HomeTimelineFragment.backListTop(fragment1.getListView());
							fragment1.autoRefresh();
						} else {
							if (fragment2==null)
								return;
							HomeTimelineFragment.backListTop(fragment2.getListView());
							fragment2.autoRefresh();
						}
					}
				}
			}
		}
	}
}
