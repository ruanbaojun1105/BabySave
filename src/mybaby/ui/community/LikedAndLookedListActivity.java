package mybaby.ui.community;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.xmlrpc.android.XMLRPCException;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.community.Banner;
import mybaby.rpc.community.CommunityItemRPC;
import mybaby.rpc.community.CommunityItemRPC.ListCallback;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.adapter.MyFragmentPagerAdapter;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.community.fragment.LoadMoreListViewFragment.TRpc;
import mybaby.ui.community.fragment.LoadMoreRecyclerViewFragment;
import mybaby.ui.community.holder.ItemState;

public class LikedAndLookedListActivity extends AppCompatActivity {
	private ViewPager mPager;
	private List<Fragment> fragmentList;
	private int initIndex;// 初始页卡编号
	private TabLayout tabLayout;

	private LoadMoreListViewFragment fragment1 = null;
	private LoadMoreListViewFragment fragment2 = null;
	private LoadMoreListViewFragment fragment3 = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.praise_reply);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contacts);
		setSupportActionBar(toolbar);
		initActionBar(LikedAndLookedListActivity.this,toolbar);
		InitViewPager();
		}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void initActionBar(final Activity activity,Toolbar mActionBarView) {
		TextView actionbar_back = (TextView) mActionBarView.findViewById(R.id.actionbar_back);
		actionbar_back.setTypeface(MyBabyApp.fontAwesome);
		actionbar_back.setText(activity.getResources().getString(R.string.fa_angle_left));
		actionbar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		tabLayout = (TabLayout) mActionBarView.findViewById(R.id.tablayout);
	}

	/*
	 * 初始化ViewPager
	 */
	public void InitViewPager() {
		
		initIndex=getIntent().getExtras()==null?0:getIntent().getExtras().getInt("initIndex",0);
		mPager = (ViewPager) findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();
		fragment1 = new LoadMoreListViewFragment(
				Constants.CacheKey_Looked_And_More_Likeed,1,false).setIsInViewPage(true);
		fragment1.setOnTRpcInternet(initRPC(0));
		fragment2 = new LoadMoreListViewFragment(
				Constants.CacheKey_More_Likeed,1,false).setIsInViewPage(true);
		fragment2.setOnTRpcInternet(initRPC(1));
		fragment3= new LoadMoreListViewFragment(
				Constants.CacheKey_More_Looked,1,false).setIsInViewPage(true);
		fragment3.setOnTRpcInternet(initRPC(2));

		fragmentList.add(fragment1);
		fragmentList.add(fragment2);
		fragmentList.add(fragment3);

		MyFragmentPagerAdapter adapter=new MyFragmentPagerAdapter(getSupportFragmentManager(), new String[]{"全部", "我赞过", "我回复过"}, fragmentList);
		// 给ViewPager设置适配器
		mPager.setAdapter(adapter);
		mPager.setOffscreenPageLimit(3);
		mPager.setAdapter(adapter);
		tabLayout.setupWithViewPager(mPager);
		tabLayout.setTabsFromPagerAdapter(adapter);

		mPager.setCurrentItem(initIndex);// 设置当前显示标签页为第一页
	}

	/**
	 * CHU初始化RPC
	 * @param current
	 * @return
	 */
	public TRpc initRPC(int current) {
		// TODO Auto-generated method stub
		if (current == 0) {
			return new TRpc() {

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception {

				}

				@Override
				public void toTRpcInternet(int lastId, int userId,
										   int parentId, final boolean isRefresh, LoadMoreListViewFragment fragment) {
					// TODO Auto-generated method stub
					CommunityItemRPC.getJoined( lastId,
							new ListCallback() {

								@Override
								public void onSuccess(boolean hasMore,
													  int newLastId,
													  List<ItemState> items, Banner[] banners) {
									// TODO Auto-generated method stub
									try {
										fragment1.onTSuccessToList(
												isRefresh, hasMore, newLastId,
                                                items);
									} catch (Exception e) {
										e.printStackTrace();
									}

								}

								@Override
								public void onFailure(long id,
										XMLRPCException error) {
									// TODO Auto-generated method stub
									try {
										fragment1.onTFailToList(id, error);
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
			return new TRpc() {

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception {

				}

				@Override
				public void toTRpcInternet(int lastId, int userId,
										   int parentId, final boolean isRefresh, LoadMoreListViewFragment fragment) {
					// TODO Auto-generated method stub
					CommunityItemRPC.getJoin_like(lastId,
							new ListCallback() {

								@Override
								public void onSuccess(boolean hasMore,
													  int newLastId,
													  List<ItemState> items, Banner[] banners) {
									// TODO Auto-generated method stub
									try {
										fragment2.onTSuccessToList(
												isRefresh, hasMore, newLastId,
                                                items);
									} catch (Exception e) {
										e.printStackTrace();
									}

								}

								@Override
								public void onFailure(long id,
										XMLRPCException error) {
									// TODO Auto-generated method stub
									try {
										fragment2.onTFailToList(id, error);
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
		}else if (current == 2) {
			return new TRpc() {

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception {

				}

				@Override
				public void toTRpcInternet(int lastId, int userId,
										   int parentId, final boolean isRefresh, LoadMoreListViewFragment fragment) {
					// TODO Auto-generated method stub
					CommunityItemRPC.getJoin_reply(lastId,
							new ListCallback() {

								@Override
								public void onSuccess(boolean hasMore,
													  int newLastId,
													  List<ItemState> items, Banner[] banners) {
									// TODO Auto-generated method stub
									try {
										fragment3.onTSuccessToList(
												isRefresh, hasMore, newLastId,
                                                items);
									} catch (Exception e) {
										e.printStackTrace();
									}

								}

								@Override
								public void onFailure(long id,
										XMLRPCException error) {
									// TODO Auto-generated method stub
									try {
										fragment3.onTFailToList(id, error);
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
}
