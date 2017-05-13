package mybaby.ui.community;

/**
 * 服务器指定RPC开启的列表
 *
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.hibb.mybaby.android.R;
import mybaby.BaseFragmentActivity;
import mybaby.Constants;
import mybaby.action.Action;
import mybaby.models.community.Banner;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.community.CommunityItemRPC;
import mybaby.rpc.community.CommunityItemRPC.ListCallback;
import mybaby.ui.MediaHelper;
import mybaby.ui.MediaHelper.MediaHelperCallback;
import mybaby.ui.MyBabyApp;
import mybaby.ui.broadcast.MediaUplodingReceiver;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.community.fragment.LoadMoreListViewFragment.TRpc;
import mybaby.ui.community.fragment.LoadMoreRecyclerViewFragment;
import mybaby.ui.community.holder.ItemState;
import mybaby.ui.community.parentingpost.WebviewFragment;
import mybaby.ui.main.NotificationCategoryFragment;
import mybaby.ui.posts.home.HomeTimelineFragment;
import mybaby.ui.posts.home.TodayWrite;
import mybaby.util.MapUtils;

@ContentView(R.layout.blank_page)
public class SuitOpenRPCListActivity extends BaseFragmentActivity implements
		MediaHelperCallback{
	@ViewInject(R.id.progress_postMediaUpLoad)
	private ProgressBar progress_postMediaUpLoad;
	private MediaHelper mMediaHelper;
	private LoadMoreListViewFragment suitFragment = null;
	private String title;
	private String rpc;
	private int right_button;
	private boolean refresh_notification;
	private boolean isNeedRefresh;
	private String ext_params;
	private String bar_color;
	private String icon_url;
	private String right_button_action;

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("服务器指定RPC开启的列表"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("服务器指定RPC开启的列表"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		Bundle bundle=getIntent().getExtras();
		if (bundle==null)
			onBackPressed();
		if (bundle.containsKey("rpcmap")){
			HashMap<String, Object> map= (HashMap<String, Object>) bundle.get("rpcmap");
			this.title= MapUtils.getMapStr(map, "title");
			this.rpc=MapUtils.getMapStr(map,"rpc");
			this.right_button=MapUtils.getMapInt(map, "right_button");
			this.refresh_notification=(MapUtils.getMapInt(map, "refresh_notification")==1);
			this.isNeedRefresh=(MapUtils.getMapInt(map,"need_refresh")==1);
			this.ext_params=MapUtils.getMapStr(map,"ext_params");
			this.bar_color=MapUtils.getMapStr(map,"bar_color");
			this.icon_url=MapUtils.getMapStr(map,"icon_url");
			this.right_button_action=MapUtils.getMapStr(map,"right_button_action");
			initActionBar(this,map);
		}else {
			title = getIntent().getExtras().getString("title", "");
			rpc = getIntent().getExtras().getString("rpc", "");
			right_button = getIntent().getExtras().getInt("right_button");
			refresh_notification = getIntent().getExtras().getBoolean("refresh_notification", false);
			isNeedRefresh = getIntent().getExtras().getBoolean("isNeedRefresh", false);
			ext_params = getIntent().getExtras().getString("ext_params", "");
			bar_color = getIntent().getExtras().getString("bar_color", "");
			icon_url = getIntent().getExtras().getString("icon_url", "");
			initActionBar(this,null);
		}
		if (TextUtils.isEmpty(rpc)){
			Toast.makeText(this, R.string.entertip,Toast.LENGTH_SHORT).show();
			onBackPressed();
		}else
			initPage();
	}

	/**
	 * 初始化actionbar
	 */
	private void initActionBar(final Activity activity,Map<String, Object> map) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(activity).inflate(
				R.layout.actionbar_friend, null);
		TextView tv_title = (TextView) view.findViewById(R.id.actionbar_title);
		TextView tv_back = (TextView) view.findViewById(R.id.actionbar_back);
		TextView right_btn = (TextView) view.findViewById(R.id.right_btn);
		ImageView actionbar_title_image = (ImageView) view.findViewById(R.id.actionbar_title_image);

		tv_back.setTypeface(MyBabyApp.fontAwesome);
		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
		tv_title.setText(title);
		if (!TextUtils.isEmpty(bar_color)){
			view.setBackgroundColor(Color.parseColor("#"+bar_color));
			tv_title.setTextColor(Color.WHITE);
			tv_back.setTextColor(Color.WHITE);
			right_btn.setTextColor(Color.WHITE);
		}
		if (map!=null)
		if (!TextUtils.isEmpty(icon_url))
			WebviewFragment.setCustemTitle(this, map, tv_title, actionbar_title_image);
		if (right_button==0){
			if (map.containsKey("right_button_action")){
				final String title=MapUtils.getMapStr(map,"right_button").toString();
				right_btn.setText(title);
				right_btn.bringToFront();
				right_btn.setVisibility(View.VISIBLE);
				right_btn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							Action.createAction(right_button_action, title, null).excute(activity, NotificationCategoryFragment.newsController, null, null);
						} catch (Exception e) {
							e.printStackTrace();
							LogUtil.e("action执行异常");
						}
					}
				});
			}
		}
		ImageView addPicture = (ImageView) view.findViewById(R.id.addPicture);
		int resId=0;
		switch (right_button){
			case 0:
				resId=0;
				break;
			case 1:
				if (!TextUtils.isEmpty(bar_color))
					resId=R.drawable.add_white;
				else
					resId=R.drawable.add;
				break;
			case 2:
				resId=R.drawable.ic_action_camera;
				break;
		}
		addPicture.setImageResource(resId);
		if (resId==0)
			addPicture.setImageDrawable(null);
		addPicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (right_button){
					case 1:
						CustomAbsClass.starTopicEditIntent(SuitOpenRPCListActivity.this);
						break;
					case 2:
						if (mMediaHelper != null) {
							mMediaHelper.launchMulPicker(Constants.TOPIC_MAX_PHOTO_PER_POST);
						}
						break;
				}
			}
		});
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((System.currentTimeMillis() - exitTime) > 500) {
					//偶尔提示一次可以双击上去
					exitTime = System.currentTimeMillis();
				} else {
					if (suitFragment==null)
						return;
					HomeTimelineFragment.backListTop(suitFragment.getListView());
				}
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(view);
	}
	private long exitTime = 0;
	/**
	 * 初始化页面
	 * 
	 */
	private void initPage() {
		// TODO Auto-generated method stub
		mMediaHelper = new MediaHelper(this, this);
		suitFragment = new LoadMoreListViewFragment(Constants.CacheKey_SuitOpenRPC+rpc+ext_params,1,refresh_notification);
		suitFragment.setOnTRpcInternet(getTopicAllListData());
		suitFragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_page, suitFragment).commit();
		new MediaUplodingReceiver(new BaseRPC.CallbackToDo() {
			@Override
			public void todo() {
				suitFragment.initView(true);
			}
		}, progress_postMediaUpLoad).mediaUplodingReceiver();
	}

	@Override
	public void onMediaFileDone(String[] mediaFilePaths) {
		// TODO Auto-generated method stub
		//打开日记
		TodayWrite.openEditPostActivity(this, mediaFilePaths, true);
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
	private TRpc getTopicAllListData() {
		// TODO Auto-generated method stub
		return new TRpc() {

			@Override
			public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception {

			}

			@Override
			public void toTRpcInternet(int lastId, int userId, int parentId,
									   final boolean isRefresh, LoadMoreListViewFragment fragment) {
				// TODO Auto-generated method stub
				CommunityItemRPC.getSuitRPCList(rpc, lastId,ext_params, new ListCallback() {

					@Override
					public void onSuccess(boolean hasMore, int newLastId,
										  List<ItemState> items, Banner[] banners) {
						// TODO Auto-generated method stub
						try {
							suitFragment.onTSuccessToList(isRefresh,
                                    hasMore, newLastId, items);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(long id, XMLRPCException error) {
						// TODO Auto-generated method stub
						try {
							suitFragment.onTFailToList(id, error);
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
}
