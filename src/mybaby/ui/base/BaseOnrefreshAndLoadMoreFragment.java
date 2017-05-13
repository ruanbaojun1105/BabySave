package mybaby.ui.base;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xmlrpc.android.XMLRPCException;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.cache.CacheDataTask;
import mybaby.cache.GenCachesOver;
import mybaby.notification.RefreshNotificationManager;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.fragment.FastScrollLinearLayoutManager;
import mybaby.ui.community.holder.HtmlItem;
import mybaby.ui.widget.BaseTLoadmoreRpc;
import mybaby.ui.widget.FixedRecycleView;
import mybaby.ui.widget.OnRpcDataBaseBack;
import mybaby.util.LogUtils;
import mybaby.util.Utils;

public abstract class BaseOnrefreshAndLoadMoreFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
		BaseQuickAdapter.RequestLoadMoreListener,OnRpcDataBaseBack,ObservableScrollViewCallbacks {

	private static final String TAG = "baseRefreshLoAD";
	private SwipeRefreshLayout mSwipeRefreshWidget;
	private FixedRecycleView mRecyclerView;
	private FastScrollLinearLayoutManager mLayoutManager;
	private BaseQuickAdapter baseAdapter;

	private ProgressBar progress_refush;

	//下列属性为初始构造属性
	private int lastids = 0;
	private boolean hasmores = true;
	private boolean isLoadSatus=true;
	private int intervaTime;
	private final int ONREFUSH=0x111;
	private final int ONGETMORE=0x122;
	private final int ONERROR=0x133;
	private static long exitTime = 0;
	private Object[] objects;

	public BaseOnrefreshAndLoadMoreFragment() {
	}

	public RecyclerView getmRecyclerView() {
		return mRecyclerView;
	}

	public SwipeRefreshLayout getmSwipeRefreshWidget() {
		return mSwipeRefreshWidget;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
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

	public abstract BaseQuickAdapter getBaseAdapter();

	public abstract boolean isEnableLoadMore();

	public abstract Object[] getStausParamers();

	/**
	 * 需要强制刷新，此属性可忽略正在刷新就不能再刷新的机制
	 * @return
	 */
	public abstract boolean needForceRefush();

	/**
	 * 是否初始加载数据,默认加载
	 * @return
	 */
	public abstract boolean isLoadSatus();

	/**
	 * 需要发送红点通知
	 * @return
	 */
	public abstract boolean needSendAsnkRed();

	public abstract BaseTLoadmoreRpc getRPC();

	/**
	 * 切记切记：此乃基类，千万别搞错类型到时候无法转换，缓存的数据类型依据不同key来识别，天啦噜，这是坑，传空则为不使用缓存
	 * @return
	 */
	public abstract String getCacheType();

	public abstract View getHeaderView();

	public abstract void init();

	public ProgressBar getProgress_refush() {
		return progress_refush;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView= inflater.inflate(R.layout.recycler_main_list, null);
		initId(rootView);
		initView(inflater);
		init();
		return rootView;
	}

	/**
	 * 初始化
	 */
	private void initId(View rootView) {
		mRecyclerView = (FixedRecycleView) rootView.findViewById(R.id.recycle_view);
		mSwipeRefreshWidget = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_widget);
		progress_refush = (ProgressBar) rootView.findViewById(R.id.progress_refush);
	}


	public static void setRecyclerStyle(SwipeRefreshLayout style){
		style.setColorSchemeResources(R.color.mainThemeColor,
				R.color.blue, R.color.cyan,
				R.color.accent);
	}

	private void initView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		isLoadSatus=isLoadSatus();
		objects=getStausParamers();
		progress_refush.bringToFront();
		setRecyclerStyle(mSwipeRefreshWidget);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mSwipeRefreshWidget.setOnRefreshListener(this);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setScrollViewCallbacks(this);
		mLayoutManager = new FastScrollLinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.addOnScrollListener(new mybaby.ui.community.fragment.PauseOnScrollListener(ImageLoader.getInstance(), Build.VERSION.SDK_INT < 18 ? true : false, true, null));
		baseAdapter=null;
		baseAdapter =getBaseAdapter();
		if (getHeaderView() != null) {
			baseAdapter.addHeaderView(getHeaderView());
		}

		baseAdapter.openLoadAnimation();
		if (isEnableLoadMore()) {
			View customLoading = inflater.inflate(R.layout.footer_cus_listview, (ViewGroup) mRecyclerView.getParent(), false);
			baseAdapter.setLoadingView(customLoading);
			baseAdapter.setOnLoadMoreListener(this);
			baseAdapter.openLoadMore(0,false);
		}
		mRecyclerView.setAdapter(baseAdapter);

		checkCacheToList(getCacheType(), intervaTime, true);// 读取缓存

		if (getActivity() instanceof  MyBabyBaseActivity){
			((MyBabyBaseActivity)getActivity()).toolbar.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if ((System.currentTimeMillis() - exitTime) > 500) {
						exitTime = System.currentTimeMillis();
					} else {
						scrollToTop(mRecyclerView);
					}
				}
			});
		}

		//OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
	}

	@Override
	public void onLoadMoreRequested() {
		try {
			getRPC().toTRpcInternet(objects, lastids, false, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onRefresh() {
		try {
			getRPC().toTRpcInternet(getStausParamers(), 0, true, this);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				onRpcFail(0,null);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
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
	public void addToList(final boolean hasMore, final int newLastId,
						   final List items, final boolean isRefresh){
		// TODO Auto-generated method stub
		lastids = newLastId;
		hasmores = hasMore;
		if (items!=null&&isRefresh) {
			try {
				if (!TextUtils.isEmpty(getCacheType())) {
					GenCachesOver cache = new GenCachesOver(String.valueOf(MyBabyApp.version), System.currentTimeMillis(), items, objects, hasMore, newLastId);
					CacheDataTask.putCache(getActivity(), cache, getCacheType(), true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (needSendAsnkRed()&&isRefresh) {
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
	public void onrefresh( boolean isAutoRefush) {
		try {
			/*if (mSwipeRefreshWidget.isRefreshing())
				return;
			//if (progress_refush.getVisibility()==View.GONE)
			getRPC().toTRpcInternet(getParamters(), 0, true, this);*/
			onRefresh();
			progress_refush.setVisibility(isAutoRefush ? View.VISIBLE : View.GONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
					baseAdapter.setNewData((List) msg.obj);
					baseAdapter.openLoadMore(((List) msg.obj).size(),hasmores);
					scrollToTop(mRecyclerView);
					break;
				case ONGETMORE:
					baseAdapter.notifyDataChangedAfterLoadMore((List) msg.obj, hasmores);
					break;
				case ONERROR:
					if (toastCount==0)
						toastCount=System.currentTimeMillis();
					if (CacheDataTask.booleaRefush(toastCount,3000l)) {
						try {
							toastCount=System.currentTimeMillis();
							baseAdapter.notifyDataChangedAfterLoadMore(hasmores);
							if (!Utils.isNetworkAvailable()) {
                                Toast.makeText(getActivity(), R.string.net, Toast.LENGTH_SHORT).show();
                                if (baseAdapter.getData().size() == 0)
                                    checkCacheToList(getCacheType(), intervaTime, true);
                            }
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
	 * 检查当前缓存是否过期
	 * 并加载缓存
	 */
	private void checkCacheToList(String cacheType, int intervaTime,boolean needAddToList) {
		// TODO Auto-generated method stub
		if (!isLoadSatus)
			return;
		if (TextUtils.isEmpty(cacheType))
			return;
		//由于缓存设置时间需求一直修改，出此下策
		intervaTime=cacheType.equals(Constants.CacheKey_CommunityActivity_Main)?60:30;
		GenCachesOver genCachesOver = null;
		try {
			genCachesOver = (GenCachesOver) CacheDataTask.getObj(getActivity(), cacheType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (genCachesOver == null) {
			onrefresh( true);
		}else {
			try {
				if (needAddToList) {
					List list= (List) genCachesOver.getSerializableObj();//由于MultiItemEntity库中类未实现序列化，所以就这样
					addToList(genCachesOver.getHasMore(), genCachesOver.getLastId(), list , true);
				}

				if (CacheDataTask.booleaRefush(genCachesOver.getLastTime(), intervaTime)) {
					onrefresh(true);
                }else {
                    if (needAddToList&&needForceRefush())
						mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
							onrefresh(true);
                        }
                    },150);
					else
                    	LogUtils.e("上次存取时间到现在还没有超过" + intervaTime + "分钟，不需要刷新");
                }
			} catch (Exception e) {
				e.printStackTrace();
				onrefresh(true);
			}
		}
	}

	public HtmlItem.SetWebViewOnTouchListener getWebViewOnTouch(){
		return new HtmlItem.SetWebViewOnTouchListener() {
			@Override
			public void onLeftOrRight(boolean disallowInterceptTouchEvent) {
				mRecyclerView.requestDisallowInterceptTouchEvent(disallowInterceptTouchEvent);
			}
		};
	}

	@Override
	public void onRpcSuccess(boolean isRefush, int lastId, Boolean hasMore,Object[] objects, List items) throws Exception {
		this.objects=objects;
		addToList(hasMore,lastId,items,isRefush);
	}

	public void onRpcSuccess(boolean isRefush, int lastId, Boolean hasMore,Object[] objects, Object[] items) throws Exception {
		List<Object> list=new ArrayList<>();
		if (items!=null)
			for (Object obj:items){
				list.add(obj);
			}
		onRpcSuccess(isRefush,lastId,hasMore,objects,list);
	}

	@Override
	public void onRpcFail(long id, XMLRPCException error) throws Exception {
		LogUtils.e("RPC fail:id---" + id + "\t---XMLRPCException:" + error);
		Utils.sendMessage(ONERROR, UIhandler);
	}

	public static void scrollToTop(final RecyclerView recyclerView) {
		/*try {
			recyclerView.smoothScrollToPosition(0);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		if (!(recyclerView instanceof ObservableRecyclerView)) {
			try {
				recyclerView.smoothScrollToPosition(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			int sy = ((ObservableRecyclerView) recyclerView).getCurrentScrollY();
			if (sy != 0) {
				if (sy > 3500)
					sy = 3500;
				else {
					recyclerView.smoothScrollToPosition(0);
					return;
				}
				//sy = sy > 3500 ? 3500 : sy;
				ValueAnimator animator = ValueAnimator.ofInt(sy, 0);
				animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						try {
							int y = (Integer) animation.getAnimatedValue();
							((ObservableRecyclerView) recyclerView).scrollVerticallyTo(y);
							LogUtils.e(y + "recyclerView");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				ObjectAnimator animator1 = ObjectAnimator.ofFloat(recyclerView, "alpha", 0.1f, 1.0f);
				AnimatorSet animSet = new AnimatorSet();
				animSet.play(animator).with(animator1);
				animSet.setDuration(300);
				animSet.start();
			}
		}
	}
}
