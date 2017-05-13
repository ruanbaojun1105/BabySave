package mybaby.ui.community;

/**
 * 我关注的话题列表
 *
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.view.annotation.ContentView;

import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.community.Banner;
import mybaby.rpc.community.CommunityItemRPC;
import mybaby.rpc.community.CommunityItemRPC.ListCallback;
import mybaby.ui.MediaHelper;
import mybaby.ui.MediaHelper.MediaHelperCallback;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.community.fragment.LoadMoreListViewFragment.TRpc;
import mybaby.ui.community.fragment.LoadMoreRecyclerViewFragment;
import mybaby.ui.community.holder.ItemState;
import mybaby.util.ActionBarUtils;

public class MyFollowTopicCategoryListActivity extends FragmentActivity implements
		MediaHelperCallback{

	private MediaHelper mMediaHelper;
	private LoadMoreListViewFragment friendFragment = null;

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("所有关注话题列表"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("所有关注话题列表"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.blank_page);
		ActionBarUtils.initActionBar("所有关注话题",this);
		initPage();
	}

	
	
	/**
	 * 初始化页面
	 * 
	 */
	private void initPage() {
		// TODO Auto-generated method stub
		mMediaHelper = new MediaHelper(this, this);
		friendFragment = new LoadMoreListViewFragment(Constants.CacheKey_CommunityActivity_Follow_Category,1,false);
		friendFragment.setOnTRpcInternet(getTopicAllListData());
		friendFragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_page, friendFragment).commit();
	}

	@Override
	public void onMediaFileDone(String[] mediaFilePaths) {
		// TODO Auto-generated method stub
		// 打开日记
		//TodayWrite.openEditPostActivity(this, mediaFilePaths,true);
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
				CommunityItemRPC.getFollow_topic_categor(lastId, new ListCallback() {

					@Override
					public void onSuccess(boolean hasMore, int newLastId,
										  List<ItemState> items, Banner[] banners) {
						// TODO Auto-generated method stub
						try {
							friendFragment.onTSuccessToList(isRefresh,
                                    hasMore, newLastId, items);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(long id, XMLRPCException error) {
						// TODO Auto-generated method stub
						try {
							friendFragment.onTFailToList(id, error);
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
