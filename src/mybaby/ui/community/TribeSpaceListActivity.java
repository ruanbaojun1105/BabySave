package mybaby.ui.community;
/**
 * 群空间
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.BaseFragmentActivity;
import mybaby.Constants;
import mybaby.models.community.Banner;
import mybaby.models.notification.TribeGroup;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.community.CommunityItemRPC;
import mybaby.rpc.notification.TribesRpc;
import mybaby.ui.MediaHelper;
import mybaby.ui.MediaHelper.MediaHelperCallback;
import mybaby.ui.MyBabyApp;
import mybaby.ui.broadcast.MediaUplodingReceiver;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.community.fragment.LoadMoreListViewFragment.TRpc;
import mybaby.ui.community.fragment.LoadMoreRecyclerViewFragment;
import mybaby.ui.community.holder.ItemState;
import mybaby.ui.posts.home.TodayWrite;
import mybaby.util.ActionBarUtils;

@ContentView(R.layout.tribespace_activity)
public class TribeSpaceListActivity extends BaseFragmentActivity implements MediaHelperCallback,OnClickListener{

	@ViewInject(R.id.progress_tribespace)
	private ProgressBar progress_postMediaUpLoad;
	private TextView tv_title,tv_back;
	private ImageView addPicture;
	
	private MediaHelper mMediaHelper;
	private LoadMoreListViewFragment neighborFragment=null;
	private String[] mediaFilePaths;
	private Long tribe_id;
	private int post_id;
	private boolean isNeedRefresh;
	private String space_name;
	private TribeGroup tribeGroup;
	private Context context;

	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("群空间"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
	    MobclickAgent.onResume(this);          //统计时长
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("群空间"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
	    MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		context=this;
		getGroupDetailBytribeid();
	}

	private void getGroupDetailBytribeid() {
		if (getIntent().getExtras()==null)
			return;
		tribe_id=getIntent().getExtras().getLong("tribeId", 0);
		post_id=getIntent().getExtras().getInt("groupid", 0);
		space_name=getIntent().getExtras().getString("space_name", "");
		isNeedRefresh=getIntent().getExtras().getBoolean("isNeedRefresh", false);
		ActionBarUtils.initActionBar("群空间", TribeSpaceListActivity.this);
		initPage();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * 初始化actionbar
	 */
	private void initActionBar(Activity activity) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(activity).inflate(
				R.layout.actionbar_friend, null);
		tv_title = (TextView) view.findViewById(R.id.actionbar_title);
		tv_back = (TextView) view.findViewById(R.id.actionbar_back);
		tv_back.setTypeface(MyBabyApp.fontAwesome);
		tv_back.setOnClickListener(this);
		tv_title.setText("群空间");
		addPicture = (ImageView) view.findViewById(R.id.addPicture);
		addPicture.setOnClickListener(this);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(view);
	}

	/**
	 * 初始化页面
	 */
	private void initPage() {
		// TODO Auto-generated method stub
//                    ActionBarUtils.initActionBar(tribe_name, TribeSpaceListActivity.this);
		initActionBar(this);
		mMediaHelper = new MediaHelper(TribeSpaceListActivity.this, TribeSpaceListActivity.this);
		neighborFragment = new LoadMoreListViewFragment(Constants.CacheKey_CommunityActivity_TribeSpace +tribe_id, 1, true);
		neighborFragment.setOnTRpcInternet(itemRest());
		neighborFragment.setArguments(getIntent().getExtras());
		neighborFragment.setParentId(post_id);
		getSupportFragmentManager().beginTransaction().
				add(R.id.wx_chat_container, neighborFragment).commit();
		//在初始化fragment之后再去注册广播，不然fragment为空不能刷新页面
		new MediaUplodingReceiver(new BaseRPC.CallbackToDo() {
			@Override
			public void todo() {
				try {
					neighborFragment.initView(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, progress_postMediaUpLoad).mediaUplodingReceiver();
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.actionbar_back:
			onBackPressed();
			break;
			case R.id.addPicture:
				new CustomAbsClass.StarEdit() {
					public void todo() {
						// TODO Auto-generated method stub
						mMediaHelper.launchMulPicker(Constants.MAX_PHOTO_PER_POST);
					}
				}.StarPostEdit(this);
				break;
		}
	}
	
	private TRpc itemRest() {
		// TODO Auto-generated method stub
		return new TRpc() {
			@Override
			public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception {

			}

			@Override
			public void toTRpcInternet(final int lastId, int userId, int parentId, final boolean isRefresh, LoadMoreListViewFragment fragment) {
				// TODO Auto-generated method stub
				if (0==post_id){
					getData(lastId,userId,parentId, isRefresh);
				}else getList(lastId,userId,parentId, isRefresh);
			}

			@Override
			public void toTRpcInternet(Object[] objects, int lastId, boolean isRefresh, Fragment fragment) throws Exception {

			}
		};
	}

	private void getData(final int lastId, final int userId, final int parentId,final boolean isGetMore){
			TribesRpc.getGroupDetailBytribeid(tribe_id, new TribesRpc.CallbackForTribeGroup() {
				@Override
				public void onSuccess(TribeGroup tribeGroupa) {
					tribeGroup = tribeGroupa;
					post_id = tribeGroup.getPost_id();
					tribe_id = tribeGroup.getIm_tribe_id();
					if (TextUtils.isEmpty(space_name))
						space_name = tribeGroup.getGroup_name();
					getList(lastId,userId,parentId,isGetMore);

				}

				@Override
				public void onFailure(long id, XMLRPCException error) {
					try {
						neighborFragment.onTFailToList(id, error);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	}

	void getList(final int lastId, int userId,int parentId,final boolean isGetMore){
		CommunityItemRPC.get_for_groupActivity(post_id, lastId, new CommunityItemRPC.ListCallback() {

			@Override
			public void onSuccess(boolean hasMore, int newLastId,
								  List<ItemState> items, Banner[] banners) {
				// TODO Auto-generated method stub
				try {
					neighborFragment.onTSuccessToList(isGetMore, hasMore, newLastId, items);
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
	public void onMediaFileDone(String[] mediaFilePaths) {
		// TODO Auto-generated method stub
		this.mediaFilePaths=mediaFilePaths;
		//打开日记
		TodayWrite.openEditPostActivity(this, mediaFilePaths,true);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		mMediaHelper.onActivityResult(requestCode, resultCode, data);

		super.onActivityResult(requestCode, resultCode, data);
	}

}
