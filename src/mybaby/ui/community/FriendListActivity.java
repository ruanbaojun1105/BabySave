package mybaby.ui.community;

/**
 * 邻家宝宝
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.xmlrpc.android.XMLRPCException;

import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.community.Banner;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.community.CommunityItemRPC;
import mybaby.rpc.community.CommunityItemRPC.ListCallback;
import mybaby.ui.MediaHelper;
import mybaby.ui.MediaHelper.MediaHelperCallback;
import mybaby.ui.MyBabyApp;
import mybaby.ui.broadcast.MediaUplodingReceiver;
import mybaby.ui.broadcast.UpdateRedTextReceiver;
import mybaby.ui.community.customclass.CustomAbsClass.StarEdit;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.community.fragment.LoadMoreListViewFragment.TRpc;
import mybaby.ui.community.fragment.LoadMoreRecyclerViewFragment;
import mybaby.ui.community.holder.ItemState;
import mybaby.ui.posts.home.TodayWrite;


public class FriendListActivity extends FragmentActivity implements
		MediaHelperCallback, OnClickListener {

	private ProgressBar progress_postMediaUpLoad;
	private TextView tv_title, tv_back;
	private ImageView addPicture;
	private String actionBarTitle;
	private String cacheType;

	private MediaHelper mMediaHelper;
	private Bundle bundle;
	private int userId;
	private boolean isPersion;
	private boolean isHot;
	private boolean isFriend;
	private MediaUplodingReceiver mediaUplodingReceiver;
	private int caseRpcIndex;
	// private List<String> listData ;

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(actionBarTitle); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(actionBarTitle); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
		MobclickAgent.onPause(this);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blank_page);
		progress_postMediaUpLoad= (ProgressBar) findViewById(R.id.progress_postMediaUpLoad);
		try {
			initActionBar(this);
			initPage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化actionbar
	 */
	private void initActionBar(Activity activity) {
		// TODO Auto-generated method stub
		bundle = getIntent().getExtras();
		caseRpcIndex= bundle == null ? 0 : bundle.getInt("caseRpcIndex", 0);
		userId = bundle == null ? 0 : bundle.getInt("userId", 0);
		isHot = bundle == null ? false : bundle.getBoolean("isHot", false);
		isPersion = userId > 0 ? true : false;
		switch (caseRpcIndex){
			case 0:isFriend=true;
				break;
			case 1:isPersion=true;
				break;
			case 2:isHot=true;
				break;
		}

		actionBarTitle = "";
		if (isPersion) {
			actionBarTitle = "个人动态";
			cacheType=Constants.CacheKey_CommunityActivity_Friend;
		} else if (isHot) {
			actionBarTitle = "热门动态";
			cacheType=Constants.CacheKey_CommunityActivity_Hot;
		} else {
			isFriend=true;
			actionBarTitle = "好友动态";
			cacheType=Constants.CacheKey_CommunityActivity_Friend;
		}
		bundle.putInt("intervaTime", 1);//1分钟
		View view = LayoutInflater.from(activity).inflate(R.layout.actionbar_friend, null);
		tv_title = (TextView) view.findViewById(R.id.actionbar_title);
		tv_back = (TextView) view.findViewById(R.id.actionbar_back);
		new UpdateRedTextReceiver((TextView) view.findViewById(R.id.actionbar_back_badge)).regiest();
		((TextView) view.findViewById(R.id.actionbar_back)).setTypeface(MyBabyApp.fontAwesome);
		tv_back.setOnClickListener(this);
		tv_title.setText(actionBarTitle);
		addPicture = (ImageView) view.findViewById(R.id.addPicture);
		addPicture.setOnClickListener(this);
		if (isHot||isPersion) {
			addPicture.setVisibility(View.GONE);
		}
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(view);
		
		
		
	}

	/**
	 * 初始化页面
	 * 
	 */
	private void initPage() {
		// TODO Auto-generated method stub
		mMediaHelper = new MediaHelper(this, this);
		String cacheType;
		if (isHot) {
			cacheType=Constants.CacheKey_CommunityActivity_Hot;
		}else if (isPersion) {
			cacheType=Constants.CacheKey_CommunityActivity_TribeSpace;
		}else {
			cacheType=Constants.CacheKey_CommunityActivity_Friend;
		}
		final LoadMoreListViewFragment friendFragment = new LoadMoreListViewFragment(cacheType,1,isHot||isPersion?false:true);
		friendFragment.setOnTRpcInternet(getTopicAllListData(isHot, isPersion, userId));
		friendFragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.content_page, friendFragment).commit();
		//在初始化fragment之后再去注册广播，不然fragment为空不能刷新页面
		if (!(isHot||isPersion)) {
			mediaUplodingReceiver=new MediaUplodingReceiver(new BaseRPC.CallbackToDo() {
				@Override
				public void todo() {
					friendFragment.initView(true);
				}
			}, progress_postMediaUpLoad);
			mediaUplodingReceiver.mediaUplodingReceiver();
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.actionbar_back:
			onBackPressed();
			break;

		case R.id.addPicture:
			new StarEdit() {
				@Override
				public void todo() {
					// TODO Auto-generated method stub
					mMediaHelper.launchMulPicker(Constants.MAX_PHOTO_PER_POST);
				}
			}.StarPostEdit(this);
			break;
		}
	
	}

	@Override
	public void onMediaFileDone(String[] mediaFilePaths) {
		// TODO Auto-generated method stub
		// 打开日记
		TodayWrite.openEditPostActivity(this, mediaFilePaths,true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		mMediaHelper.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 获取该话题全部列表
	 */
	private TRpc getTopicAllListData(final boolean isHot, final boolean isPersion, final int userId) {
		// TODO Auto-generated method stub
		if (isHot) {
			return new TRpc() {

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception {

				}

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId,
										   final boolean isRefresh, final LoadMoreListViewFragment fragment) {
					// TODO Auto-generated method stub
					CommunityItemRPC.getForTop(lastId, new ListCallback() {

						@Override
						public void onSuccess(boolean hasMore, int newLastId,
											  List<ItemState> items, Banner[] banners) {
							// TODO Auto-generated method stub
							fragment.onTSuccessToList(isRefresh,hasMore, newLastId, items);

						}

						@Override
						public void onFailure(long id, XMLRPCException error) {
							// TODO Auto-generated method stub
							fragment.onTFailToList(id, error);
						}
					});
				}

				@Override
				public void toTRpcInternet(Object[] objects, int lastId, boolean isRefresh, Fragment fragment) throws Exception {

				}
			};
		} else if (isPersion) {
			return new TRpc() {

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception {

				}

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId,
										   final boolean isRefresh, final LoadMoreListViewFragment fragment) {
					// TODO Auto-generated method stub
					CommunityItemRPC.getByUser(userId, lastId,
							new ListCallback() {

								@Override
								public void onSuccess(boolean hasMore,
													  int newLastId,
													  List<ItemState> items, Banner[] banners) {
									// TODO Auto-generated method stub
									fragment.onTSuccessToList(isRefresh, hasMore, newLastId,items);
								}

								@Override
								public void onFailure(long id,
										XMLRPCException error) {
									fragment.onTFailToList(id, error);
								}
							});
				}

				@Override
				public void toTRpcInternet(Object[] objects, int lastId, boolean isRefresh, Fragment fragment) throws Exception {

				}
			};
		} else {
			return new TRpc() {

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception {

				}

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId,
										   final boolean isRefresh, final LoadMoreListViewFragment fragment) {
					// TODO Auto-generated method stub
					CommunityItemRPC.getForFriends(lastId, new ListCallback() {

						@Override
						public void onSuccess(boolean hasMore, int newLastId,
											  List<ItemState> items, Banner[] banners) {
							// TODO Auto-generated method stub
							fragment.onTSuccessToList(isRefresh,
									hasMore, newLastId, items);
						}

						@Override
						public void onFailure(long id, XMLRPCException error) {
							// TODO Auto-generated method stub
							fragment.onTFailToList(id, error);
						}
					});
				}

				@Override
				public void toTRpcInternet(Object[] objects, int lastId, boolean isRefresh, Fragment fragment) throws Exception {

				}
			};
		}

	}
	
}
