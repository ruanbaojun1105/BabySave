package mybaby.ui.Notification;

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

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.tribe.IYWTribeService;

import org.xmlrpc.android.XMLRPCException;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.R;
import me.hibb.mybaby.android.openIM.TribeHelper;
import mybaby.models.User;
import mybaby.rpc.UserRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.broadcast.UpdateRedTextReceiver;
import mybaby.ui.community.adapter.MyFragmentPagerAdapter;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.widget.GenRpcWithData;

@Deprecated
public class ContactsListActivity extends AppCompatActivity {
	private ViewPager mPager;
	private int initIndex;// 初始页卡编号
	private TabLayout tabLayout;
	private List<Fragment> fragmentList;
	private ContactsListFragment fragment3 = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.praise_reply);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contacts);
		setSupportActionBar(toolbar);
		initActionBar( ContactsListActivity.this,toolbar);
		InitViewPager();
		}

	public void initActionBar(final Activity activity,Toolbar mActionBarView) {
		TextView actionbar_back = (TextView) mActionBarView.findViewById(R.id.actionbar_back);
		new UpdateRedTextReceiver((TextView) mActionBarView.findViewById(R.id.actionbar_back_badge)).regiest();
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
		fragmentList=new ArrayList<>();
		/*fragment1= new TribesGroupListFragment(initRPC(0),MyBabyApp.currentUser.getUserId(),Constants.CacheKey_Trinbes);
		fragment2 = new UserListFragment(initRPC(1),MyBabyApp.currentUser.getUserId(), Constants.CacheKey_Friend);*/
		fragment3 = new ContactsListFragment();

		tabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
		//tabLayout.setTabTextColors(Color.GRAY, Color.GREEN);//设置文本在选中和为选中时候的颜色
		//tabLayout.addTab(tabLayout.newTab().setText("好友"));//添加tab选项卡

		/*fragmentList.add(fragment1);
		fragmentList.add(fragment2);*/
		fragmentList.add(fragment3);
		MyFragmentPagerAdapter adapter=new MyFragmentPagerAdapter(getSupportFragmentManager(), new String[]{"群组","好友","关注/粉丝"},fragmentList);
		mPager.setAdapter(adapter);
		mPager.setOffscreenPageLimit(3);
		mPager.setCurrentItem(initIndex);// 设置当前显示标签页为第一页

		// 给ViewPager设置适配器
		tabLayout.setupWithViewPager(mPager);
		//关联 TabLayout viewpager
		tabLayout.setTabsFromPagerAdapter(adapter);
	}

	/**
	 * CHU初始化RPC
	 * @param current
	 * @return
	 */
	public GenRpcWithData initRPC(int current) {
		// TODO Auto-generated method stub
		if (current == 0) {
			return new GenRpcWithData() {
				@Override
				public void toTRpcInternet(int lastId, int userId, final int parentId, boolean isRefushList) {
					IYWTribeService tribeService;
					YWIMKit mIMKit= MyBayMainActivity.getmIMKit();
					if (mIMKit==null){
						try {
							//fragment1.onRpcFail();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					tribeService=mIMKit.getTribeService();
					tribeService.getAllTribesFromServer(new TribeHelper.MyIWxCallback() {
						@Override
						public void onWxSuccess(List<YWTribe> list) {
							try {
								//fragment1.onRpcSuccess(TribeHelper.getTribeGroupList(list));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onWxFail() {
							try {
								//fragment1.onRpcFail();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.getIWxCallback(ContactsListActivity.this));//获取群列表
				}
			};
		} else if (current == 1) return new GenRpcWithData() {
			@Override
			public void toTRpcInternet(int lastId, int userId, int parentId, final boolean isRefushList) {
				UserRPC.setUserList(userId, lastId, new UserRPC.ListCallback() {
					@Override
					public void onSuccess(int lastId, Boolean hasMore, User[] arrUser) {
						try {
							//fragment2.onRpcSuccess(isRefushList, lastId, hasMore, arrUser);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(long id, XMLRPCException error) {
						try {
							//fragment2.onRpcFail();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		};
		return null;
	}
}
