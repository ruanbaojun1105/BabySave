package mybaby.ui.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.BaseFragmentActivity;
import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.models.community.Banner;
import mybaby.models.community.Place;
import mybaby.rpc.community.CommunityItemRPC;
import mybaby.rpc.community.CommunityItemRPC.ListCallback;
import mybaby.ui.broadcast.UpdateRedTextReceiver;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.customclass.CustomAbsClass.InitPlaceById;
import mybaby.ui.community.customclass.CustomAbsClass.PlaceFollow;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.community.fragment.LoadMoreListViewFragment.TRpc;
import mybaby.ui.community.fragment.LoadMoreRecyclerViewFragment;
import mybaby.ui.community.holder.ItemState;
import mybaby.util.DialogShow;
import mybaby.util.ImageHelper;

@ContentView(R.layout.community_placelist)
public class PlaceListActivity extends BaseFragmentActivity implements OnClickListener {

	@ViewInject(R.id.sendtopic)
	LinearLayout sendtopic;
	@ViewInject(R.id.text_icon)
	TextView text_icon;

	private Context context;
	private TextView tv_placeName;
	private TextView tv_follow;
	private Place place;
	private boolean hasFollowBack = false;
	private int placeId = 0;
	private LoadMoreListViewFragment neighborFragment=null;
	private View placeView=null;
	private ProgressBar follow_loadingBar;
	private PlaceFollow placeFollow;

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.didiandongtailiebiao)); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.didiandongtailiebiao)); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
											// onPageEnd 在onPause 之前调用,因为
											// onPause 中会保存信息
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		initData();
	}

	/**
	 * 初始化
	 */
	@SuppressLint("NewApi")
	private void initData() {
		// TODO Auto-generated method stub

		sendtopic.setVisibility(View.VISIBLE);
		View view = LayoutInflater.from(this).inflate(
				R.layout.actionbar_details, null);
		view.findViewById(R.id.actionbar_back).setOnClickListener(this);
		follow_loadingBar=(ProgressBar) view.findViewById(R.id.progress_image);
		follow_loadingBar.setVisibility(View.VISIBLE);
		new UpdateRedTextReceiver((TextView) view.findViewById(R.id.actionbar_back_badge)).regiest();
		((TextView) view.findViewById(R.id.actionbar_back)).setTypeface(MyBabyApp.fontAwesome);
		((TextView) view.findViewById(R.id.actionbar_title)).setText("地点");
		tv_follow = (TextView) view.findViewById(R.id.follow);
		tv_follow.setText("");
		tv_follow.setOnClickListener(this);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(view);
		
		text_icon.setTypeface(MyBabyApp.fontAwesome);
		Bundle bundle= getIntent().getExtras();
		if (bundle != null) {
			place = (Place) bundle.getSerializable("place");
			placeId=(place==null?placeId= bundle.getInt("placeId",0):place.getPlaceId());
			if (placeId!=0&&place==null) {
				new InitPlaceById(placeId) {
					
					@Override
					public void succBackPlace(Place place) {
						// TODO Auto-generated method stub
						PlaceListActivity.this.place=place;
						placeId=place.getPlaceId();
						initView(place);
					}

					@Override
					public void onError() {
						// TODO Auto-generated method stub
					}
				};
			}else {
				initView(place);
			}
		}
		
	}

	private void initView(final Place place){
		placeView = LayoutInflater.from(context).inflate(
				R.layout.activity_place_item, null);
		BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(
				R.drawable.map_bg);
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, ImageHelper.getImageScaleHeight(bd.getBitmap(), MyBabyApp.screenWidth));
		placeView.setPadding(MyBabyApp.dip2px(10),0,0,0);
		placeView.setLayoutParams(layoutParams);
		placeView.setBackground(bd);
		tv_placeName = (TextView) placeView.findViewById(R.id.placename);
		tv_placeName.setTextColor(getResources().getColor(R.color.blue));
		tv_placeName.setTextSize(16);
		tv_placeName.setText(place.getPlace_name());
		tv_placeName.setSingleLine(false);
		TextView tv_placePic = (TextView) placeView
				.findViewById(R.id.place_pic);
		tv_placePic.setTextColor(getResources().getColor(R.color.blue));
		tv_placePic.setTextSize(16);
		tv_placePic.setTypeface(MyBabyApp.fontAwesome);
		tv_placePic.setText(R.string.fa_map_marker);
		TextView tv_placePic1 = (TextView) placeView
				.findViewById(R.id.place_pica);
		tv_placePic1.setVisibility(View.VISIBLE);
		tv_placePic1.setTextColor(getResources().getColor(R.color.blue));
		tv_placePic1.setTextSize(16);
		tv_placePic1.setTypeface(MyBabyApp.fontAwesome);
		tv_placePic1.setText(R.string.fa_angle_right);
		placeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CustomAbsClass.startShowMapActivity(context, place);
			}
		});
		//
		placeFollow=initFollow(placeId, PlaceListActivity.this);
		placeFollow.getHasFollow();
		sendtopic.setOnClickListener(this);
		initList(placeView);
	}
	
	
	private void initList(View placeView) {
		// TODO Auto-generated method stub
		neighborFragment=new LoadMoreListViewFragment(Constants.CacheKey_CommunityActivity_Place,placeView,false,1);
		neighborFragment.setOnTRpcInternet(backRpc());
		Bundle bundle=new Bundle();
		bundle.putInt("parentId", placeId);
		neighborFragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().
		add(R.id.content_page, neighborFragment).commit();
	}
	
	private TRpc backRpc() {
		// TODO Auto-generated method stub
		return new TRpc() {

			@Override
			public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception {

			}

			@Override
			public void toTRpcInternet(int lastId, int userId, int parentId, final boolean isRefresh, LoadMoreListViewFragment fragment) {
				// TODO Auto-generated method stub
				CommunityItemRPC.getByPlace(parentId, lastId, new ListCallback() {
					
					@Override
					public void onSuccess(boolean hasMore, int newLastId,
										  List<ItemState> items, Banner[] banners) {
						// TODO Auto-generated method stub
						try {
							neighborFragment.onTSuccessToList(isRefresh, hasMore, newLastId, items);
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
			public void toTRpcInternet(Object[] objects, int lastId, boolean isRefresh, Fragment fragment) throws Exception {

			}
		};
	}
	
	/**
	 * 在获得该动态对象之后再开始初始化此操作 初始化 关注
	 */
	private PlaceFollow initFollow(int placeid, Activity activity) {
		PlaceFollow placeFollow = new PlaceFollow(placeid, activity) {

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
					tv_follow.setText("关注地点");
				}
				hasFollowBack = boolValue;
			}

			@Override
			public void Follow() {
				// TODO Auto-generated method stub
				isFollow(true);
				Toast.makeText(context, "关注成功", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void unFollow() {
				isFollow(false);
				Toast.makeText(context, "已取消关注", Toast.LENGTH_SHORT).show();
			}
		};

		return placeFollow;
	}

	
	
	@Event(R.id.sendtopic)
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.actionbar_back:
			onBackPressed();
			break;
		case R.id.follow:
			if (hasFollowBack) {
				new DialogShow.DisLikeDialog(PlaceListActivity.this,
						"是否取消关注？", "确定", "取消",
						new DialogShow.DoSomeThingListener() {

							@Override
							public void todo() {
								// TODO Auto-generated
								tv_follow.setText("");
								follow_loadingBar.setVisibility(View.VISIBLE);
								placeFollow.toUnfollow();
							}
						}, null);
			} else {
				tv_follow.setText("");
				follow_loadingBar.setVisibility(View.VISIBLE);
				placeFollow.toFollow();
			}
			break;
		case R.id.sendtopic:
			CustomAbsClass.starTopicEditIntent(context, 0, "", place);
			break;
		}
	}

}
