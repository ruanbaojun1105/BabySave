package mybaby.ui.more;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.common.message.Log;

import org.xmlrpc.android.XMLRPCException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.User;
import mybaby.models.community.Place;
import mybaby.models.community.TopicCategory;
import mybaby.models.community.activity.AbstractMainActivity;
import mybaby.models.diary.PostRepository;
import mybaby.models.person.SelfPerson;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.BaseRPC.Callback;
import mybaby.rpc.BaseRPC.CallbackForMap;
import mybaby.rpc.UserRPC;
import mybaby.rpc.UserRPC.ProfileDetailCallback;
import mybaby.rpc.UserRPC.UserCallback;
import mybaby.rpc.community.ReportRPC.ReportType;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.FriendListActivity;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.more.holder.PersonButtomHolder;
import mybaby.ui.more.holder.PersonMessageHolder;
import mybaby.ui.more.holder.PersonPlaceHolder;
import mybaby.ui.more.holder.PersonTop1ActivityHolder;
import mybaby.ui.more.holder.PersonTopicHolder;
import mybaby.ui.more.personpage.FollowActivity;
import mybaby.ui.more.personpage.FollowerActivity;
import mybaby.ui.more.personpage.PersonTopicActivity;
import mybaby.util.ActionBarUtils;
import mybaby.util.DialogShow;
import mybaby.util.DialogShow.DeleteListener;
import mybaby.util.PopupWindowUtils.ShowPopTip;
/**
 * 个人主页
 * 
 * @author Administrator
 * 
 */
public class PersonPageActivity extends Activity implements OnClickListener {

	private MyPersonPageReceiver receiver;

	private User user;
	private Place[] arrPlace;
	private TopicCategory[] arrTopicCategory;
	private AbstractMainActivity newestActivity;
	private boolean hasFollow;
	private String data;
	private SelfPerson selfPerson;
	private ProgressBar bar;

	private ScrollView user_scrollView;
	// 多个布局
	private ProgressBar progressBar;
	private FrameLayout bottom_layout;// 底部布局
	private FrameLayout person_page_message;
	private FrameLayout person_page_place;
	private FrameLayout person_page_topic;
	private FrameLayout person_page_activity;
	private FrameLayout person_page_reply;
	private FrameLayout person_page_praise;

	// 控件
	private RelativeLayout rl_place;
	private LinearLayout ll_person_message;
	private TextView bt_follow;
	private TextView bt_jubao;
	private TextView bt_sendMessage;
	private RelativeLayout rl_topic;
	private RelativeLayout rl_top1activity;
	private RelativeLayout rl_praise;
	private RelativeLayout rl_reply;
	private LinearLayout ll_praise;
	private LinearLayout ll_reply;
	private TextView tv_follow;
	private TextView tv_follower;
	private TextView tv_enter;
	private TextView tv_praise;
	private TextView tv_reply;

	private PersonMessageHolder personMessageHolder = null;
	private PersonPlaceHolder personPlaceHolder;
	private PersonTopicHolder personTopicHolder;
	private PersonTop1ActivityHolder personTop1ActivityHolder;
	private PersonButtomHolder personButtomHolder;
	private boolean isFirstIn = true;// 是否是第一次进入
	private boolean messageIsChange = false;// 个人信息是否发生变化
	private boolean needReloadOther = false;// 话题、地点等是否发生变化
	private ShowPopTip showPopAtPersonpage;
	private Boolean PcanSend;
	private String PIMuserID;
	private String Pmessage;
	private String im_userId;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_page);
		PcanSend=false;
		isFirstIn = false;
		PIMuserID=null;
		Pmessage=null;
		if (receiver == null) {
			receiver = new MyPersonPageReceiver();
			receiver.registerMyReceiver();
		}
		ActionBarUtils.initActionBar("", PersonPageActivity.this);
		Bundle bundle=getIntent().getExtras();
		if (bundle.containsKey("user"))
			user = (User) bundle.getSerializable("user");
		if (bundle.containsKey("im_userId"))
			im_userId=bundle.getString("im_userId","");

		if (user==null){
			if (!TextUtils.isEmpty(im_userId))
				getUserByIMuserId(im_userId);
		}else {
			initData();
		}
	}

	void initData(){
		/*if (user.isSelf())
			selfPerson = PostRepository.loadSelfPerson(user.getUserId());*/
		ActionBarUtils.initActionBar("个人主页", PersonPageActivity.this, user);
		initView();
		canSendMessage();
		showMessageHolder(user);
		loadDate();
	}
	/**
	 * imuserid转user对象
	 * @param imuserid
	 */
	private void getUserByIMuserId(String imuserid) {
		UserRPC.getUserByIMUserID(imuserid, new BaseRPC.CallbackgetUserByIMuserid() {

			@Override
			public void onSuccess(User user) {
				PersonPageActivity.this.user=user;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						initData();
					}
				});
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {

				android.util.Log.e("error", error + "");
			}
		});
	}

	// 显示个人信息，
	private void showMessageHolder(User user) {
		if (personMessageHolder == null) {
			personMessageHolder = new PersonMessageHolder(
					PersonPageActivity.this);
		}else person_page_message.removeAllViews();
		person_page_message.addView(personMessageHolder.getRootView());
		personMessageHolder.setData(user);
		ll_person_message = (LinearLayout) findViewById(R.id.ll_person_message);
		tv_follow = (TextView) findViewById(R.id.tv_follow);
		tv_follower = (TextView) findViewById(R.id.tv_follower);
		/*if (user.isSelf()) {
			tv_follow.setOnClickListener(this);
			tv_follower.setOnClickListener(this);
			ll_person_message.setOnClickListener(this);
		}*/
	}

	// 显示地点，如果查看的是自己的主页则显示，否则不显示
	private void showPlaceHolder(Place[] arrPlace) {
		person_page_place.setVisibility(View.GONE);
		/*if (arrPlace != null && !("".equals(arrPlace)) && arrPlace.length != 0) {
			if (user.isSelf()) {
				if (personPlaceHolder == null) {
					personPlaceHolder = new PersonPlaceHolder();
					person_page_place.addView(personPlaceHolder.getRootView());
				}
				personPlaceHolder.setData(arrPlace);
				rl_place = (RelativeLayout) findViewById(R.id.rl_place);
				rl_place.setOnClickListener(PersonPageActivity.this);
			} else {
				person_page_place.setVisibility(View.GONE);
			}
		} else {
			person_page_place.setVisibility(View.GONE);
		}*/

	}

	// 显示话题，如果不为空显示，否则不显示
	private void showTopicHolder(TopicCategory[] arrTopicCategory) {
		if (arrTopicCategory == null || "".equals(arrTopicCategory)
				|| arrTopicCategory.length == 0) {
			person_page_topic.setVisibility(View.GONE);
		} else {
			if (personTopicHolder == null) {
				personTopicHolder = new PersonTopicHolder();
				person_page_topic.addView(personTopicHolder.getRootView());
			}
			personTopicHolder.setData(arrTopicCategory);
			rl_topic = (RelativeLayout) findViewById(R.id.rl_topic);
			rl_topic.setOnClickListener(PersonPageActivity.this);
		}
	}

	// 显示动态，如果不为空显示，否则不显示
	private void showTop1ActivityHolder(AbstractMainActivity newestActivity) {
		if (newestActivity != null) {
			if (personTop1ActivityHolder == null) {
				personTop1ActivityHolder = new PersonTop1ActivityHolder();
				person_page_activity.addView(personTop1ActivityHolder
						.getRootView());
			}
			personTop1ActivityHolder.setData(newestActivity);
			rl_top1activity = (RelativeLayout) findViewById(R.id.rl_top1activity);
			rl_top1activity.setOnClickListener(PersonPageActivity.this);
		} else {
			person_page_activity.setVisibility(View.GONE);
		}
	}

	// 显示个人主页最底部 当查看自己时不显示，查看别人时显示
	private void showPersonButtom() {
		if (user.isSelf()) {
			bottom_layout.setVisibility(View.GONE);
		} else {
			bottom_layout.setVisibility(View.VISIBLE);
			personButtomHolder = new PersonButtomHolder();
			personButtomHolder.setData(hasFollow);
			bottom_layout.addView(personButtomHolder.getRootView());
			bar = (ProgressBar) findViewById(R.id.pb_loading);
			//LinearLayout mshowpop = (LinearLayout) findViewById(R.id.mshowpop);
			bt_follow = (TextView) findViewById(R.id.bt_follow);
			bt_jubao = (TextView) findViewById(R.id.bt_jubao);
			bt_jubao.setText("拉黑");
			bt_sendMessage=(TextView) findViewById(R.id.bt_sendMessage);
			if (hasFollow) {
				bt_follow.setText("已关注");
			} else {
				bt_follow.setText("+ 关注");
			}
			bt_follow.setOnClickListener(this);
			bt_jubao.setOnClickListener(this);
			bt_sendMessage.setOnClickListener(this);
			bt_sendMessage.setText("发消息");
			if (data != null && !"".equals(data)) {
				try {
					showPopAtPersonpage=new ShowPopTip(bt_follow,
							PersonPageActivity.this, data, MyBabyApp.dip2px(40),
							MyBabyApp.dip2px(130), MyBabyApp.dip2px(180),1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}

	}
	/**
	 * 赞过和看过    布局
	 */
	
	private void showPersonPraise(){
		person_page_praise.setVisibility(View.GONE);
		/*if (user.isSelf()){
			View view =LayoutInflater.from(this).inflate(
					R.layout.person_page_praise, null);
			ll_praise=(LinearLayout) view.findViewById(R.id.ll_praise);
			ll_praise.setOnClickListener(this);
			tv_praise=(TextView)view.findViewById(R.id.tv_praise);
			tv_enter=(TextView) view.findViewById(R.id.tv_enter);
			tv_praise.setText(R.string.MPraise);
			tv_enter.setTypeface(MyBabyApp.fontAwesome);
			tv_enter.setText(R.string.fa_angle_right);
			LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			person_page_praise.removeAllViews();
			person_page_praise.addView(ll_praise, lp);
		}
		else{
			person_page_praise.setVisibility(View.GONE);
		}*/
		
	}
	private void showPersonReply(){
		person_page_reply.setVisibility(View.GONE);
		/*if (user.isSelf()){
			View view=LayoutInflater.from(this).inflate(R.layout.person_page_reply, null);
			ll_reply=(LinearLayout) view.findViewById(R.id.ll_reply);
			ll_reply.setOnClickListener(this);
			tv_reply=(TextView) view.findViewById(R.id.tv_reply);
			tv_enter=(TextView) view.findViewById(R.id.tv_enter);
			tv_reply.setText(R.string.MReply);
			tv_enter.setTypeface(MyBabyApp.fontAwesome);
			tv_enter.setText(R.string.fa_angle_right);
			LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			person_page_reply.removeAllViews();
			person_page_reply.addView(ll_reply,lp);
		}
		else
		{
			person_page_reply.setVisibility(View.GONE);
		}*/
	}

	private void loadDate() {
		loadHasFollow();
		loadMessage();
	}

	private void loadOthers() {
		UserRPC.getProfileDetail(user.getUserId(), new ProfileDetailCallback() {

			@Override
			public void onSuccess(final Place[] arrPlace,
								  final TopicCategory[] arrTopicCategory,
								  final AbstractMainActivity newestActivity) {
				PersonPageActivity.this.arrPlace = arrPlace;
				PersonPageActivity.this.arrTopicCategory = arrTopicCategory;
				PersonPageActivity.this.newestActivity = newestActivity;
				runOnUiThread(new Runnable() {
					public void run() {
						progressBar.setVisibility(View.GONE);
						showPlaceHolder(arrPlace);
						showTopicHolder(arrTopicCategory);
						showTop1ActivityHolder(newestActivity);
						showPersonButtom();
						showPersonPraise();
						showPersonReply();
					}
				});
				needReloadOther = false;
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				new CustomAbsClass.doSomething(PersonPageActivity.this) {
					@Override
					public void todo() {
						progressBar.setVisibility(View.GONE);
						Toast.makeText(PersonPageActivity.this,
								"网络出错！请检查网络是否连接", Toast.LENGTH_SHORT).show();
					}
				};
			}
		});
	}

	private void loadHasFollow() {
		UserRPC.hasFollow(user.getUserId(), new CallbackForMap() {

			@Override
			public void onSuccess(boolean boolValue, String data) {
				hasFollow = boolValue;
				PersonPageActivity.this.data = data;
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {

			}
		});
	}

	private void loadMessage() {
		UserRPC.getProfile(user.getUserId(), new UserCallback() {

			@Override
			public void onSuccess(final User user) {
				PersonPageActivity.this.user = user;
				new CustomAbsClass.doSomething(PersonPageActivity.this) {
					@Override
					public void todo() {
						showMessageHolder(user);
					}
				};
				// 在个人信息加载完之后 加载其他数据 如果需要同步加载，即与个人信息同级加载即可
				if (!messageIsChange) {
					loadOthers();
				}
				messageIsChange = false;
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				new CustomAbsClass.doSomething(PersonPageActivity.this) {
					@Override
					public void todo() {
						progressBar.setVisibility(View.GONE);
						Toast.makeText(PersonPageActivity.this,
								"网络出错！请检查网络是否连接", Toast.LENGTH_SHORT).show();
					}
				};
			}
		});
	}

	private void follow() {
		UserRPC.follow(user.getUserId(), new Callback() {

			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
						bt_follow.setVisibility(View.VISIBLE);
						bar.setVisibility(View.GONE);
						bt_follow.setText("已关注");
						hasFollow = true;
					}
				});
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				runOnUiThread(new Runnable() {
					public void run() {
						bar.setVisibility(View.GONE);
						bt_follow.setVisibility(View.VISIBLE);
						Toast.makeText(PersonPageActivity.this,
								"网络出错！请检查网络是否连接", Toast.LENGTH_SHORT).show();
					}
				});

			}
		});
	}

	private void unFollow(int id) {
		UserRPC.unfollow(id, new Callback() {

			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
						bt_follow.setVisibility(View.VISIBLE);
						bar.setVisibility(View.GONE);
						bt_follow.setText("+ 关注");
						hasFollow = false;
					}
				});
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				runOnUiThread(new Runnable() {
					public void run() {
						bt_follow.setVisibility(View.VISIBLE);
						bar.setVisibility(View.GONE);
						Toast.makeText(PersonPageActivity.this,
								"网络出错！请检查网络是否连接", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void initView() {
		user_scrollView= (ScrollView) findViewById(R.id.user_scrollView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		bottom_layout = (FrameLayout) findViewById(R.id.bottom_layout);
		person_page_message = (FrameLayout) findViewById(R.id.person_page_message);
		person_page_place = (FrameLayout) findViewById(R.id.person_page_place);
		person_page_topic = (FrameLayout) findViewById(R.id.person_page_topic);
		person_page_activity = (FrameLayout) findViewById(R.id.person_page_activity);
		person_page_praise=(FrameLayout) findViewById(R.id.person_page_praise);
		person_page_reply=(FrameLayout) findViewById(R.id.person_page_reply);

		OverScrollDecoratorHelper.setUpOverScroll(user_scrollView);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		Bundle bundle = null;
		ArrayList<Map<String, Object>> item = null;
		switch (v.getId()) {
		case R.id.ll_person_message:
			CustomAbsClass.starPersionEditActivity(PersonPageActivity.this,selfPerson);
			break;
		case R.id.rl_place:// 跳转到地点activity
			CustomAbsClass.openFollowPlaceActivity(PersonPageActivity.this,arrPlace);
			break;

		case R.id.rl_topic:// 跳转到话题activity
			intent = new Intent(PersonPageActivity.this,
					PersonTopicActivity.class);
			bundle = new Bundle();
			item = new ArrayList<Map<String, Object>>();
			int[] parentIds = new int[arrTopicCategory.length];
			for (int i = 0; i < arrTopicCategory.length; i++) {
				parentIds[i] = arrTopicCategory[i].getId();
			}
			for (int i = 0; i < arrTopicCategory.length; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("item", arrTopicCategory[i]);
				item.add(map);
			}
			bundle.putIntArray("parentIds", parentIds);
			bundle.putSerializable("user", user);
			bundle.putSerializable("item", item);
			intent.putExtras(bundle);

			startActivity(intent);
			break;

		case R.id.rl_top1activity: // 跳转到动态activity
			intent = new Intent(PersonPageActivity.this,
					FriendListActivity.class);
			Bundle id = new Bundle();
			id.putSerializable("userId", user.getUserId());
			intent.putExtras(id);
			startActivity(intent);
			break;

		case R.id.tv_follow:// 跳转到关注activity
			intent = new Intent(PersonPageActivity.this, FollowActivity.class);
			intent.putExtra("userId", user.getUserId());
			startActivity(intent);
			break;

		case R.id.tv_follower:// 跳转到粉丝activity
			intent = new Intent(PersonPageActivity.this, FollowerActivity.class);
			intent.putExtra("userId", user.getUserId());
			startActivity(intent);
			break;

		case R.id.bt_follow:// 关注按钮
			if (hasFollow) {
				DialogShow dialogShow = new DialogShow(PersonPageActivity.this);
				dialogShow.showMakeSureDialog("确定取消关注？", user.getUserId(),
						new DeleteListener() {

							@Override
							public void delete(int id, DialogInterface dialog) {
								unFollow(id);
								bt_follow.setVisibility(View.GONE);
								bar.setVisibility(View.VISIBLE);
							}

						});
			} else {
				follow();
				if (showPopAtPersonpage!=null) {
					showPopAtPersonpage.dismiss();
				}
				bt_follow.setVisibility(View.GONE);
				bar.setVisibility(View.VISIBLE);
			}
			Intent data = new Intent();
			data.setAction(Constants.BroadcastAction.BroadcastAction_PersonPage_Follow);
			LocalBroadcastManager.getInstance(MyBabyApp.getContext())
					.sendBroadcast(data);
			break;
		case R.id.bt_jubao:// 拉黑举报按钮
			DialogShow dialogShow = new DialogShow(PersonPageActivity.this);
			 dialogShow.showDefridendReport(ReportType.User,user.getUserId(),PIMuserID,user.getUserId());
//			dialogShow.showReportDialog(ReportType.User, user.getUserId(),
//					user.getUserId());

			break;
		case R.id.bt_sendMessage://发消息

			new CustomAbsClass.StarEdit() {
				@Override

				public void todo() {
					if(PIMuserID!=null){
						if(PcanSend)
							CustomAbsClass.starP2PChatting(PersonPageActivity.this,PIMuserID,user.getName());
						else{
							Toast.makeText(PersonPageActivity.this, Pmessage,Toast.LENGTH_SHORT).show();
						}
					}else {
						Log.e("网络延时", "网络延时不能聊天呀");
					}
				}
			}.StarTopicEdit(PersonPageActivity.this);
			break;
		case R.id.ll_praise://赞过
			CustomAbsClass.starLikeOrLooked(this, 1);
			break;
		case R.id.ll_reply://
			CustomAbsClass.starLikeOrLooked(this, 2);
			break;
		}
	}

	public void onPause() {
		super.onPause();
		MobclickAgent
				.onPageEnd(user == null ? getString(R.string.yonghugerenzhuye) : (user.isSelf() ? getString(R.string.wodegerenzhuye)
						: getString(R.string.yonghugerenzhuye)));
		if (showPopAtPersonpage!=null) {
			showPopAtPersonpage.dismiss();
		}
	}

	@Override
	protected void onResume() {
		MobclickAgent
				.onPageStart(user==null?getString(R.string.yonghugerenzhuye):(user.isSelf() ? getString(R.string.wodegerenzhuye)
						: getString(R.string.yonghugerenzhuye))); // 统计页面
		/*if (user!=null){
			if (!isFirstIn && user.isSelf() && messageIsChange) {
				loadMessage();
			}
			if (!isFirstIn && user.isSelf() && needReloadOther) {
				loadOthers();
			}
		}*/

		super.onResume();
	}


	public void canSendMessage()
	{
		UserRPC.canSendMessage(user.getUserId(), new BaseRPC.CallbackCanSendMessage() {
			@Override
			public void onSuccess(final Boolean canSend, final String IMuserID,final String message) {
				PcanSend=canSend;
				PIMuserID=IMuserID;
				Pmessage=message;
			}
			public void onFailure(long id, XMLRPCException error) {
			}
		});
	}
	@Override
	protected void onDestroy() {
		if (receiver != null) {
			LocalBroadcastManager.getInstance(this)
					.unregisterReceiver(receiver);
		}
		super.onDestroy();
	}

	private class MyPersonPageReceiver extends BroadcastReceiver {

		public void registerMyReceiver() {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.BroadcastAction.BroadcastAction_PersonPage_Follow);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Person_Edit);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_PersonPlace_Unfollow);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Persontopic_Unfollow);
			LocalBroadcastManager.getInstance(MyBabyApp.getContext())
					.registerReceiver(this, filter);
		}

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(
					Constants.BroadcastAction.BroadcastAction_Person_Edit)) {
				int id = intent.getExtras().getInt("id",0);
				if (selfPerson==null)
					return;
				if (id == selfPerson.getId()) {
					SelfPerson person = PostRepository.loadSelfPerson(user
							.getUserId());
					if (person != null && person.getAvatar() != null) {
						user.setAvatarThumbnailUrl(PostRepository
								.loadSelfPerson(user.getUserId()).getAvatar()
								.getAssetURL());
					}
					user.setName(PostRepository
							.loadSelfPerson(user.getUserId()).getName());
					showMessageHolder(user);
				}
			}
			if (intent
					.getAction()
					.equals(Constants.BroadcastAction.BroadcastAction_PersonPage_Follow)) {
				messageIsChange = true;
			}
			if (intent
					.getAction()
					.equals(Constants.BroadcastAction.BroadcastAction_PersonPlace_Unfollow)
					|| intent
							.getAction()
							.equals(Constants.BroadcastAction.BroadcastAction_Persontopic_Unfollow)) {
				needReloadOther = true;
			}
		}

	}

}
