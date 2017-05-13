package mybaby.ui.community.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;

import org.xmlrpc.android.XMLRPCException;

import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.holder.ItemState;
import mybaby.ui.widget.LoadMoreMainTRpc;

/**
 * 此旧界面为了统一新的打开方式和统一旧接口，只有这样，
 * 新的打开方式可以直接用LoadMoreRecyclerViewFragment，功能详细完全
 * 而且BackUpLoadMoreListViewFragment是比较统一的‘下拉刷新自动加载更多’方式，只能完成此页面小部分需求，但依然可以工作
 *
 */
public class LoadMoreListViewFragment extends Fragment {

	private LoadMoreRecyclerViewFragment fragment;

	//下列属性为初始构造属性
	private int ptrBgColor=0;
	private View headerView = null;
	private boolean isNeedRefush = false;
	private boolean needSendAsnkRed = false ;//需要发送红点通知
	private boolean needForceRefush=false;//需要强制刷新，此属性可忽略正在刷新就不能再刷新的机制
	private boolean isLoadSatus=true;//是否初始加载数据,默认加载
	private int userId;
	private int parentId;
	private int intervaTime;
	private String cacheType;
	private TRpc tRPCLast;
	private boolean hasVisiPlace = true;//是否显示地图标签
	private boolean isInViewPage = false;


	public LoadMoreListViewFragment() {
		super();
	}
	public LoadMoreListViewFragment(String cacheType) {
		super();
		this.cacheType = cacheType;
	}
	/**
	 * @param cacheType 缓存类型
	 * @param intervaTime 读取缓存间隔时间
	 * @param needSendAsnkRed 是否发送红点刷新的通知
	 */
	public LoadMoreListViewFragment(String cacheType,int intervaTime ,boolean needSendAsnkRed) {
		super();
		this.cacheType = cacheType;
		this.intervaTime= intervaTime;
		this.needSendAsnkRed =needSendAsnkRed;
	}
	public LoadMoreListViewFragment(String cacheType,int intervaTime ,boolean needSendAsnkRed, View headView) {
		super();
		this.cacheType = cacheType;
		this.intervaTime= intervaTime;
		this.needSendAsnkRed =needSendAsnkRed;
		this.headerView = headView;
	}

	public LoadMoreListViewFragment(String cacheType, View placeView,boolean hasVisiPlace,int intervaTime) {
		super();
		this.cacheType = cacheType;
		this.headerView = placeView;
		this.hasVisiPlace = hasVisiPlace;
		this.intervaTime=intervaTime;
	}


	public void setPtrBgColor(int ptrBgColor) {
		this.ptrBgColor = ptrBgColor;
	}
	public View getHeaderView() {
		return headerView;
	}
	public LoadMoreListViewFragment setIsInViewPage(boolean isInViewPage) {
		this.isInViewPage = isInViewPage;
		return this;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public void setOnTRpcInternet(TRpc tRpcLast) {
		this.tRPCLast = tRpcLast;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView= inflater.inflate(R.layout.blank_page, null);
		Bundle bundle=getArguments();
		isNeedRefush = (bundle == null ? false : bundle.getBoolean("isNeedRefresh",false));
		userId = bundle == null ? 0 : bundle.getInt("userId", 0);
		parentId = bundle == null ? 0 : bundle.getInt("parentId", 0);
		intervaTime = bundle == null ? (intervaTime>0?intervaTime:0 ):(intervaTime>0? intervaTime:(bundle.getInt("intervaTime",0)));
		isLoadSatus= bundle == null ?true:bundle.getBoolean("isLoadSatus", true);
		fragment=LoadMoreRecyclerViewFragment.newInstance(this,tRPCLast, null, cacheType, headerView,
				hasVisiPlace, needSendAsnkRed, isInViewPage, isNeedRefush, isLoadSatus, userId, parentId, intervaTime);
		fragment.setArguments(getArguments());getChildFragmentManager().beginTransaction().replace(R.id.content_page, fragment).commitAllowingStateLoss();
		return rootView;
	}


	/**
	 * 红点同步广播
	 */
	public static void askRed() {
		Intent data = new Intent();
		data.setAction(Constants.BroadcastAction.BroadcastAction_Main_UpdateTabBadge);
		LocalBroadcastManager.getInstance(MyBabyApp.getContext()).sendBroadcast(
				data);
	}

	public RecyclerView getListView() {
		try {
			return fragment.getmRecyclerView();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void initView( boolean isAutoRefush) {
		try {
			fragment.initView(isAutoRefush);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void autoRefresh(){
		try {
			fragment.autoRefresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void autoRefresh(boolean isSendRed){
		try {
			fragment.autoRefresh(isSendRed);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void onTSuccessToList(Boolean isRefresh, boolean hasMore,
								 int newLastId,List<ItemState> items) {
		try {
			fragment.onTSuccessToList(isRefresh, newLastId, hasMore, items, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onTFailToList(long id, XMLRPCException error){
		try {
			fragment.onTFailToList(id,error);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public interface TRpc extends LoadMoreMainTRpc {
	}


}
