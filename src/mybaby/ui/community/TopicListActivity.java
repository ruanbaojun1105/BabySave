package mybaby.ui.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.view.annotation.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.community.Banner;
import mybaby.models.community.TopicCategory;
import mybaby.rpc.community.CommunityItemRPC;
import mybaby.rpc.community.CommunityItemRPC.ListCallback;
import mybaby.ui.MediaHelper;
import mybaby.ui.MediaHelper.MediaHelperCallback;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.adapter.MyFragmentPagerAdapter;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.customclass.CustomAbsClass.HasTopicFollow;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.community.fragment.LoadMoreListViewFragment.TRpc;
import mybaby.ui.community.fragment.LoadMoreRecyclerViewFragment;
import mybaby.ui.community.holder.ItemState;
import mybaby.util.DialogShow;
import mybaby.util.Utils;

public class TopicListActivity extends AppCompatActivity implements
		OnClickListener,MediaHelperCallback{
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentList;

	private Context context;
	private TextView tv_title;
	private TextView tv_follow;// 关注该话题
	private TabLayout tabLayout;
	private int parentId;
	private String title;
	private boolean hasFollowBack = false;
	private boolean needAddImage;
	private boolean needPerformClick;//自动点击事件
	//private boolean needRefush = false;
	private LinearLayout sendTopic;
	private TextView send_tv;
	private TextView text_icon;
	private ProgressBar follow_loadingBar;

	private TopicCategory topicCategory;
	private Bundle bundle;
	private MediaHelper mMediaHelper;
	private HasTopicFollow hasTopicFollow;

	private LoadMoreListViewFragment fragment1 = null;
	private LoadMoreListViewFragment fragment2 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.community_topiclist);
		mMediaHelper = new MediaHelper(this, this);
		try {
			initData();
			initViewPager();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		needRefush=true;
	}*/
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//将所有退出了的话题去掉保存
		//CustomAbsClass.cutTopicById(parentId);
	}

	/*
         * 初始化ViewPager
         */
	public void initViewPager() {
		bundle = getIntent().getExtras();
		bundle.putInt("intervaTime", 1);
		bundle.putInt("parentId", parentId);
		tabLayout = (TabLayout) findViewById(R.id.tablayout);
		mPager = (ViewPager) findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();
		fragment1 = new LoadMoreListViewFragment(
				Constants.CacheKey_CommunityActivity_Topic_Main,1,false).setIsInViewPage(true);
		fragment1.setOnTRpcInternet(initRPC(0));
		fragment1.setArguments(bundle);
		fragment2 = new LoadMoreListViewFragment(
				Constants.CacheKey_CommunityActivity_Topic_Top,1,false).setIsInViewPage(true);
		fragment2.setOnTRpcInternet(initRPC(1));
		fragment2.setArguments(bundle);
		fragmentList.add(fragment1);
		fragmentList.add(fragment2);

		MyFragmentPagerAdapter adapter=new MyFragmentPagerAdapter(
				getSupportFragmentManager(), new String[]{"全部", "热门"}, fragmentList);
		mPager.setOffscreenPageLimit(2);
		mPager.setAdapter(adapter);
		mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
		tabLayout.setupWithViewPager(mPager);
		tabLayout.setTabsFromPagerAdapter(adapter);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// TODO Auto-generated method stub
		context = this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.addView(LayoutInflater.from(this).inflate(R.layout.actionbar_details, null));
		setSupportActionBar(toolbar);
		send_tv = (TextView) findViewById(R.id.send_tv);
		text_icon = (TextView) findViewById(R.id.text_icon);
		sendTopic = (LinearLayout) findViewById(R.id.sendtopic);
		Bundle bundle=getIntent().getExtras();
		needAddImage=bundle.getBoolean("needAddImage", false);
		needPerformClick=bundle.getBoolean("needPerformClick", false);
		parentId = bundle.getInt("TopicCategory_id", 0);
		title = bundle.getString("TopicCategory_title", "");
		text_icon.setTypeface(MyBabyApp.fontAwesome);
		text_icon.setText(needAddImage?R.string.fa_camera:R.string.fa_comment);
		send_tv.setText(needAddImage ? "马上参与" : "我有话说");
		

		if (bundle.getSerializable("topicCategory")!=null) {
			topicCategory=(TopicCategory) bundle.getSerializable("topicCategory");
			parentId=topicCategory.getCategoryId();
			title=topicCategory.getTitle();
		}

		Utils.Loge(parentId + title);

		//View view = LayoutInflater.from(this).inflate(R.layout.actionbar_details, null);
		tv_title = (TextView) toolbar.findViewById(R.id.actionbar_title);
		follow_loadingBar=(ProgressBar) toolbar.findViewById(R.id.progress_image);
		follow_loadingBar.setVisibility(View.VISIBLE);
		toolbar.findViewById(R.id.actionbar_back).setOnClickListener(this);
		((TextView) toolbar.findViewById(R.id.actionbar_back))
				.setTypeface(MyBabyApp.fontAwesome);
		tv_title.setText("话题");
		/*if (title.length() < 10) {
			tv_title.setText(title);
		} else {
			tv_title.setText(title.substring(0, 10) + "...");
		}*/
		tv_follow = (TextView) toolbar.findViewById(R.id.follow);
		tv_follow.setOnClickListener(this);

		/*getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(view);*/

		tv_follow.setVisibility(View.GONE);
		// 内部抽象类
		hasTopicFollow=initFollow(parentId, TopicListActivity.this);
		hasTopicFollow.getHasFollow();

		sendTopic.setOnClickListener(this);
		sendTopic.setVisibility(View.VISIBLE);
		if (needPerformClick)
			sendTopic.callOnClick();
	}

	/**
	 * 初始化刷新数据
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
					CommunityItemRPC.getAllByParent(parentId, lastId,
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
					CommunityItemRPC.getTopByParent(parentId, lastId,
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
		}
		return null;
	}

	/**
	 * 初始化该话题是否关注 关注 取消关注
	 */
	private HasTopicFollow initFollow(int parentId, Activity activity) {
		// TODO Auto-generated method stub
		HasTopicFollow hasFollow = new HasTopicFollow(parentId, activity) {

			@Override
			public void isFollow(boolean boolValue) {
				// TODO Auto-generated method stub
				follow_loadingBar.setVisibility(View.GONE);
				tv_follow.setVisibility(View.VISIBLE);
				tv_follow.setEnabled(true);
				if (boolValue) {
					tv_follow.setText("已关注");
					//tv_follow.setTextColor(getResources().getColor(R.color.gray_1));
				} else {
					tv_follow.setText("关注");
				}
				hasFollowBack = boolValue;
			}

			@Override
			public void Follow() {
				isFollow(true);
				// TODO Auto-generated method stub
				//Toast.makeText(context, "关注成功", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void unFollow() {
				isFollow(false);
				//Toast.makeText(context, "已取消关注", Toast.LENGTH_SHORT).show();
			}
		};
		return hasFollow;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
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
	@Event({ R.id.sendtopic })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

		case R.id.actionbar_back:
			onBackPressed();
			break;
		case R.id.follow:
			if (hasFollowBack) {
				new DialogShow.DisLikeDialog(TopicListActivity.this,
						"是否取消关注？", "确定", "取消",
						new DialogShow.DoSomeThingListener() {

							@Override
							public void todo() {
								// TODO Auto-generated
								tv_follow.setText("");
								follow_loadingBar.setVisibility(View.VISIBLE);
								hasTopicFollow.toUnfollow();
							}
						}, null);
			} else {
				tv_follow.setText("");
				follow_loadingBar.setVisibility(View.VISIBLE);
				hasTopicFollow.toFollow();
			}
			break;
		case R.id.sendtopic:
			if (needAddImage) {//如果需要加入图片进入编辑帖子，必须传topicCategory进来
				//这种方式破坏了程序的独立性，本该在根fragment中重写照片返回的回调处理方式
				Constants.category=topicCategory;
				if (mMediaHelper != null) {
					mMediaHelper.launchMulPicker(Constants.TOPIC_MAX_PHOTO_PER_POST);
				}
			} else {
				CustomAbsClass.starTopicEditIntent(context, parentId, title, null);
			}
			break;
		}
	}


}
