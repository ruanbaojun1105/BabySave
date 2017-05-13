package mybaby.ui.community.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.cache.CacheDataTask;
import mybaby.cache.MainCache;
import mybaby.models.community.Banner;
import mybaby.models.community.activity.AbstractMainActivity;
import mybaby.models.community.item.CommunityActivityItem;
import mybaby.models.community.item.CommunityPlaceSettingItem;
import mybaby.notification.RefreshNotificationManager;
import mybaby.rpc.community.CommunityItemRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.base.BaseOnrefreshAndLoadMoreFragment;
import mybaby.ui.broadcast.PostMediaTask;
import mybaby.ui.community.adapter.MultipleItemQuickAdapter;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.customclass.CustomEventDelActivityId;
import mybaby.ui.community.holder.ActivityItem;
import mybaby.ui.community.holder.HtmlItem;
import mybaby.ui.community.holder.ItemState;
import mybaby.ui.widget.FixedRecycleView;
import mybaby.ui.widget.LoadMoreMainTRpc;
import mybaby.ui.widget.OnMainRpcDataBack;
import mybaby.util.LogUtils;
import mybaby.util.Utils;

public class LoadMoreRecyclerViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
		BaseQuickAdapter.RequestLoadMoreListener,OnMainRpcDataBack,ObservableScrollViewCallbacks {

	private static final String TAG = "LoadMoreRecyclerViewFragment";
	private SwipeRefreshLayout mSwipeRefreshWidget;
	private FixedRecycleView mRecyclerView;
	private FastScrollLinearLayoutManager mLayoutManager;
	private MultipleItemQuickAdapter adapter;

	private LoadMoreListViewFragment backDatafragment;

	private ProgressBar progress_refush;
	private ProgressBar progress_image;

	//下列属性为初始构造属性
	private View headerView ;
	private boolean isNeedRefush = false;//初始是否需要刷新
	private boolean needSendAsnkRed = false ;//需要发送红点通知
	private boolean needForceRefush=false;//需要强制刷新，此属性可忽略正在刷新就不能再刷新的机制
	private boolean isLoadSatus=true;//是否初始加载数据,默认加载
	private int userId;
	private int parentId;
	private int intervaTime;
	private String cacheType;
	private LoadMoreMainTRpc tRPCLast;
	private boolean hasVisiPlace = true;//是否显示地图标签

	private int lastids = 0;
	private boolean hasmores = true;

	private boolean isInViewPage = false;
	private SettingReceiver settingReceiver;
	private final int ONREFUSH=0x111;
	private final int ONGETMORE=0x122;
	private final int ONERROR=0x133;

	public static LoadMoreRecyclerViewFragment newInstance(LoadMoreListViewFragment backDatafragment,LoadMoreMainTRpc rpc_item ,final String rpcStr,String cacheType, View placeView,
														   boolean hasVisiPlace,boolean needSendAsnkRed,boolean isInViewPage,
														   Boolean isNeedRefresh,Boolean isLoadSatus,int userId,int parentId,int intervaTime) {
		//rpc接口方式和api方法直接传入方式皆可，但是不能二者皆空
		if (rpc_item==null) {
			rpc_item = new LoadMoreMainTRpc() {
				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId, final boolean isRefresh, final LoadMoreRecyclerViewFragment fragment) throws Exception {
					CommunityItemRPC.getSuitRPCList(rpcStr, lastId, null, new CommunityItemRPC.ListCallback() {
						@Override
						public void onSuccess(boolean hasMore, int newLastId, List<ItemState> items, Banner[] banners) {
							try {
								fragment.onTSuccessToList(isRefresh, newLastId, hasMore, items, banners);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(long id, XMLRPCException error) {
							try {
								fragment.onTFailToList(id, error);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreListViewFragment fragment) throws Exception {

				}

				@Override
				public void toTRpcInternet(Object[] objects, int lastId, boolean isRefresh, Fragment fragment) throws Exception {

				}


			};
		}
		LoadMoreRecyclerViewFragment fragment = new LoadMoreRecyclerViewFragment(rpc_item,cacheType,placeView,hasVisiPlace,needSendAsnkRed,isInViewPage);
		Bundle args = new Bundle();
		args.putBoolean("isNeedRefresh",isNeedRefresh);
		args.putBoolean("isLoadSatus",isLoadSatus);
		args.putInt("userId", userId);
		args.putInt("parentId", parentId);
		args.putInt("intervaTime", intervaTime);
		fragment.setArguments(args);
		if (backDatafragment!=null)
			fragment.setBackDatafragment(backDatafragment);
		return fragment;
	}

	public static LoadMoreRecyclerViewFragment newInstance(String rpc,String cacheType,boolean needSendAsnkRed){
		return newInstance(null,null,rpc,cacheType,null,true,needSendAsnkRed,false,false,true,0,0,0);
	}

	public LoadMoreRecyclerViewFragment() {
	}

	public LoadMoreRecyclerViewFragment(LoadMoreMainTRpc tRPCLast, String cacheType, View headerView, boolean hasVisiPlace, boolean needSendAsnkRed,boolean isInViewPage) {
		this.tRPCLast = tRPCLast;
		this.cacheType = cacheType;
		this.isInViewPage = isInViewPage;
		this.needSendAsnkRed = needSendAsnkRed;
		this.headerView = headerView;
		this.hasVisiPlace = hasVisiPlace;
	}

	/**
	 * @param cacheType 缓存类型
	 * @param intervaTime 读取缓存间隔时间
	 * @param needSendAsnkRed 是否发送红点刷新的通知
	 */
	public LoadMoreRecyclerViewFragment(String cacheType, int intervaTime,boolean needSendAsnkRed) {
		this.cacheType = cacheType;
		this.intervaTime= intervaTime;
		this.needSendAsnkRed =needSendAsnkRed;
	}

	public void setBackDatafragment(LoadMoreListViewFragment backDatafragment) {
		this.backDatafragment = backDatafragment;
	}


	public void setUserId(int userId) {
		this.userId = userId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public RecyclerView getmRecyclerView() {
		return mRecyclerView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		initData();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			UIhandler=null;
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		isLoadSatus=true;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!isNeedRefush)
			checkCacheToList( intervaTime, false);
		if (cacheType==null)initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView= inflater.inflate(R.layout.recycler_main_list, null);
		initData();
		initId(rootView);
		initView(inflater);
		return rootView;
	}

	/**
	 * 初始化
	 */
	private void initId(View rootView) {
		mRecyclerView = (FixedRecycleView) rootView.findViewById(R.id.recycle_view);
		mSwipeRefreshWidget = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_widget);
		progress_refush = (ProgressBar) rootView.findViewById(R.id.progress_refush);
		progress_image = (ProgressBar) rootView.findViewById(R.id.progress_image);
	}


	private void initData() {
		Bundle bundle=getArguments();
		isNeedRefush = (bundle == null ? false : bundle.getBoolean("isNeedRefresh",false));
		userId = bundle == null ? 0 : bundle.getInt("userId", 0);
		parentId = bundle == null ? 0 : bundle.getInt("parentId", 0);
		intervaTime = bundle == null ? (intervaTime>0?intervaTime:0 ):(intervaTime>0? intervaTime:(bundle.getInt("intervaTime",0)));
		isLoadSatus= bundle == null ?true:bundle.getBoolean("isLoadSatus",true);
	}


	private void initView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		settingReceiver = new SettingReceiver();
		settingReceiver.registerSettingReceiver();
		progress_refush.bringToFront();
		mSwipeRefreshWidget.setColorSchemeResources(R.color.mainThemeColor,
				R.color.blue, R.color.cyan,
				R.color.accent);
		mSwipeRefreshWidget.setOnRefreshListener(this);

		mRecyclerView.setScrollViewCallbacks(this);
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new FastScrollLinearLayoutManager(getActivity());
		mLayoutManager.setAutoMeasureEnabled(true);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.addOnScrollListener(new mybaby.ui.community.fragment.PauseOnScrollListener(ImageLoader.getInstance(), Build.VERSION.SDK_INT < 18 ? true : false, true, null));
		adapter = new MultipleItemQuickAdapter(getContext(),new ArrayList(),hasVisiPlace,getWebViewOnTouch());
		if (headerView != null) {
			adapter.addHeaderView(headerView);
		}
		View customLoading = inflater.inflate(R.layout.footer_cus_listview, (ViewGroup) mRecyclerView.getParent(), false);
		adapter.setLoadingView(customLoading);
		adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
			@Override
			public void onItemClick(View view, int i) {
				LogUtils.e(i+"position");
			}
		});
		adapter.openLoadAnimation();
		adapter.setOnLoadMoreListener(this);
		adapter.openLoadMore(0,false);
		mRecyclerView.setAdapter(adapter);

		checkCacheToList( intervaTime, true);// 读取缓存
	}

	@Override
	public void onLoadMoreRequested() {
		try {
			if (backDatafragment==null)
				tRPCLast.toTRpcInternet(lastids, userId, parentId, false,this);
			else{
				tRPCLast.toTRpcInternet(lastids, userId, parentId, false,backDatafragment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onRefresh() {
		try {
			if (backDatafragment==null)
                tRPCLast.toTRpcInternet(0, userId, parentId, true,this);
            else{
                tRPCLast.toTRpcInternet(0, userId, parentId, true,backDatafragment);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
	}

	@Override
	public void onDownMotionEvent() {
	}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {
	}

	private long toastCount=0;
	private Handler UIhandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			LogUtils.e("是否有更多：" + hasmores);
			if (!isLoadSatus)
				isLoadSatus=true;
			mSwipeRefreshWidget.setRefreshing(false);
			progress_refush.setVisibility(View.GONE);
			switch (msg.what){
				case ONREFUSH:
					List<ItemState> list= (List<ItemState>) msg.obj;
					adapter.setNewData(list);
					adapter.openLoadMore(list.size(), hasmores);
					BaseOnrefreshAndLoadMoreFragment.scrollToTop(mRecyclerView);
					break;
				case ONGETMORE:
					adapter.notifyDataChangedAfterLoadMore((List<ItemState>) msg.obj, hasmores);
					break;
				case ONERROR:
					if (toastCount==0)
						toastCount=System.currentTimeMillis();
					if (CacheDataTask.booleaRefush(toastCount,3000l)) {
						try {
							toastCount=System.currentTimeMillis();
							adapter.notifyDataChangedAfterLoadMore(hasmores);
							if (!Utils.isNetworkAvailable()) {
								Toast.makeText(getActivity(), R.string.net, Toast.LENGTH_SHORT).show();
								if (adapter.getData().size()==0)
									checkCacheToList( intervaTime, true);
							}
							System.gc();
						} catch (Resources.NotFoundException e) {
							e.printStackTrace();
						}
						System.gc();
					}
					break;
			}
		}
	};
	/**
	 * 统一处理滑动拦截
	 * @return
	 */
	public HtmlItem.SetWebViewOnTouchListener getWebViewOnTouch(){
		return new HtmlItem.SetWebViewOnTouchListener() {
			@Override
			public void onLeftOrRight(boolean disallowInterceptTouchEvent) {
				mRecyclerView.requestDisallowInterceptTouchEvent(disallowInterceptTouchEvent);
			}
		};
	}

	/**
	 * 检查当前缓存是否过期
	 * 并加载缓存
	 */
	public void checkCacheToList( int intervaTime,boolean needAddToList) {
		// TODO Auto-generated method stub

		String cacheTypeOver=cacheType;
		if (cacheTypeOver==null)
			cacheTypeOver=Constants.CacheKey_CommunityActivity_Main;
		//由于缓存设置时间需求一直修改，出此下策
		intervaTime=(cacheTypeOver==null?30:(cacheTypeOver.equals(Constants.CacheKey_CommunityActivity_Main)?60:30));
		MainCache mainCache = CacheDataTask.getMainCache(getActivity(), cacheTypeOver
				+ userId + parentId);
		if (mainCache == null) {
			if (!isLoadSatus)
				return;
			initView(true);
		}else {
			try {
				if (needAddToList) {
					List<ItemState> list= (List<ItemState>) mainCache.getItems();//由于MultiItemEntity库中类未实现序列化，所以就这样
					addToList(mainCache.isHasMore(), mainCache.getLastId(), list , true);
				}
				if (CacheDataTask.booleaRefush(mainCache.getLastTime(), intervaTime)) {
					if (!isLoadSatus)
						return;
                    initView(true);
                }else {
                    if (isNeedRefush&&needAddToList) {
						mRecyclerView.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (!isLoadSatus)
									return;
								initView(true);
							}
						}, 150);
					}else {
						LogUtils.e("上次存取时间到现在还没有超过" + intervaTime + "分钟，不需要刷新");
					}
                }
			} catch (Exception e) {
				e.printStackTrace();
				initView(true);
			}
		}

	}

	/**
	 * 首次加载不同页面数据加入列表
	 *
	 * @param hasMore
	 *            是否有更多
	 * @param newLastId
	 *            id
	 * @param items
	 *            对象数组
	 * @param isRefresh
	 *            是否刷新
	 */
	@SuppressLint("NewApi")
	private void addToList(final boolean hasMore, final int newLastId,
						   final List<ItemState> items, final boolean isRefresh){
		// TODO Auto-generated method stub
		lastids = newLastId;
		hasmores = hasMore;
		if (items!=null&&isRefresh) {
			MainCache cache = new MainCache(items, newLastId, 0, hasMore, String.valueOf(MyBabyApp.version), new Date());
			CacheDataTask.putCache(getActivity(), cache, cacheType + userId + parentId, true);
		}
		if (needSendAsnkRed&&isRefresh) {
			try {
				try{
					Class.forName("mybaby.notification.RefreshNotificationManager");
					RefreshNotificationManager.refreshNotification(true);
				}catch(Exception e){
					System.out.println("这个类真的不存在!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Utils.sendMessage(isRefresh ? ONREFUSH : ONGETMORE, items, UIhandler);

	}


	/**
	 * 初始化刷新数据
	 */
	public void initView( final boolean isAutoRefush) {
		try {
			UIhandler.post(new Runnable() {
				@Override
				public void run() {
					progress_refush.setVisibility(isAutoRefush ? View.VISIBLE : View.GONE);
				}
			});
			onRefresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void autoRefresh() {
		if (UIhandler==null)
			return;
		UIhandler.post(new Runnable() {
			@Override
			public void run() {
				mSwipeRefreshWidget.setRefreshing(true);
			}
		});
		onRefresh();
	}

	public void autoRefresh(boolean isSendRed) {
		if (UIhandler==null)
			return;
		needSendAsnkRed=isSendRed;
		UIhandler.post(new Runnable() {
			@Override
			public void run() {
				mSwipeRefreshWidget.setRefreshing(true);
			}
		});
		onRefresh();
	}


	@Override
	public void onTSuccessToList(boolean isRefush, int lastId, Boolean hasMore, List<ItemState> items, Banner[] banners) throws Exception {
		addToList(hasMore, lastId, items, isRefush);
	}

	@Override
	public void onTFailToList(long id, XMLRPCException error) throws Exception {
		Utils.Loge("RPC fail:id---" + id + "\t---XMLRPCException:" + error);
		Utils.sendMessage(ONERROR,UIhandler);
	}


	// 广播接收并响应处理 地点设置成功返回刷新
	private class SettingReceiver extends BroadcastReceiver {
		public void registerSettingReceiver() {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.BroadcastAction.BroadcastAction_PlaceSetting_IsSuccess);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Post_UploadMediaByid_OK);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_CommunityMain_Refush);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Post_Delete);//日记删除广播
			filter.addAction(Constants.BroadcastAction.BroadcastAction_CommunityMain_ImageUpLoadingFinshed);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_CommunityMain_ImageUpLoading);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Netok);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Netbad);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Topic_Delete);
			LocalBroadcastManager.getInstance(MyBabyApp.getContext())
					.registerReceiver(this, filter);
		}

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			// LogUtils.i("over"+arg1.getExtras().toString());
			if (arg1.getAction()
					.equals(Constants.BroadcastAction.BroadcastAction_PlaceSetting_IsSuccess)) {
				removeAllPlaceSet();
			} else if (arg1
					.getAction()
					.equals(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done)||
					arg1
					.getAction()
					.equals(Constants.BroadcastAction.BroadcastAction_CommunityMain_Refush)) {
				if (arg1.getExtras()!=null) {
					needForceRefush=arg1.getExtras().getBoolean("needForceRefush",false);
				}
				if (needForceRefush) {
					initView(true);
					needForceRefush=false;
				}else initView(false);
			} else if (arg1.getAction().equals(
					Constants.BroadcastAction.BroadcastAction_Post_Delete)) {
				if (arg1.getExtras() != null) {
					removeListDataParameter(arg1.getExtras().getInt("postId", 0),true,false);
				}
			} else if (arg1
					.getAction()
					.equals(Constants.BroadcastAction.BroadcastAction_CommunityMain_ImageUpLoadingFinshed)) {
				progress_image.setVisibility(View.GONE);
				if (arg1.getExtras()!=null) {
					if (!arg1.getExtras().getBoolean("isRefush",true)) {
						initView(true);// 刷新
					}
				}else {
					initView(true);// 刷新
				}

			} else if (arg1
					.getAction()
					.equals(Constants.BroadcastAction.BroadcastAction_CommunityMain_ImageUpLoading)) {
				progress_image.setVisibility(View.VISIBLE);
			} else if (arg1
					.getAction()
					.equals(Constants.BroadcastAction.BroadcastAction_Netok)) {

			} else if (arg1
					.getAction()
					.equals(Constants.BroadcastAction.BroadcastAction_Netbad)) {
				progress_image.setVisibility(View.GONE);
				if (cacheType.contains(Constants.CacheKey_CommunityActivity_Near)||
						cacheType.contains(Constants.CacheKey_CommunityActivity_Topic_Top)||
						cacheType.contains(Constants.CacheKey_More_Likeed)||
						cacheType.contains(Constants.CacheKey_More_Looked)) {
					return;
				}
				try {
					PostMediaTask.showReUpload(getActivity());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//DialogShow.showRestImageDialog(context,Constants.postTask.getRestPaths(),Constants.postTask.getPARENT_ID());
			}else if (arg1
					.getAction()
					.equals(Constants.BroadcastAction.BroadcastAction_Topic_Delete)) {
				Bundle delData=arg1.getExtras();
				if (delData==null)
					return;
				onEventMainThread((CustomEventDelActivityId) delData.getSerializable("CustomEventDelActivityId"));
			}
		}
	}


	/**
	 * 接收事件完成删除
	 */
	public void onEventMainThread(CustomEventDelActivityId event) {
		LogUtil.e("准备删除的id:" + event.getId());

		if (event==null)
			return;
		// 当需要关掉详情页则在此强制关掉
		if (event.isKill()) {
			try {
				event.getActivity().finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		removeListDataParameter(event.getId());
	}

	private void removeAllPlaceSet() {
		removeListDataParameter(0,false,true);
	}
	/**
	 * 删除该list中activityId字段的条目
	 * @param id
	 */
	private void removeListDataParameter(int id){
		removeListDataParameter(id,false,false);
	}

	private void removeListDataParameter(final int id, final boolean isPostId, final boolean justRePlaceSet) {
		if (mRecyclerView==null)
			return;
		if (mRecyclerView.getAdapter()==null)
			return;
		try {
			List<ItemState> dataMain = null;
			if (mRecyclerView.getAdapter() instanceof  MultipleItemQuickAdapter)
                dataMain=((MultipleItemQuickAdapter)mRecyclerView.getAdapter()).getData();
			if (mRecyclerView.getAdapter() instanceof  BaseQuickAdapter)
                dataMain=((BaseQuickAdapter)mRecyclerView.getAdapter()).getData();
			final List<ItemState> finalDataMain = dataMain;
			if (finalDataMain==null)
                return;
			new CustomAbsClass.doSomething(getActivity()) {
                @Override
                public void todo() {
                    for (int i = 0; i < finalDataMain.size(); i++){
                        if (justRePlaceSet) {
                            if (finalDataMain.get(i) instanceof CommunityPlaceSettingItem) {
                                if (progress_refush.getVisibility()==View.GONE) {
									adapter.remove(i);
									autoRefresh();
									break;
								}
                            }
                        }else {
                            if (id==0)
                                break;
                            if (finalDataMain.get(i) instanceof CommunityActivityItem) {
                                CommunityActivityItem item= (CommunityActivityItem) finalDataMain.get(i);
                                AbstractMainActivity activity_item=item.getActivity();
                                if (id==(isPostId?activity_item.getPostId():activity_item.getId())){
                                    adapter.remove(i);
									break;
                                }
                            }
                        }
                    }
                }
            };
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
