package mybaby.ui.community;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.umeng.message.PushAgent;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.hibb.mybaby.android.BuildConfig;
import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.User;
import mybaby.models.community.activity.AbstractMainActivity;
import mybaby.models.community.activity.LikeActivity;
import mybaby.models.community.activity.ReplyActivity;
import mybaby.rpc.BaseRPC.CallbackForId;
import mybaby.rpc.community.ActivityRPC;
import mybaby.rpc.community.ActivityRPC.ActivityCallback;
import mybaby.rpc.community.LikeRPC;
import mybaby.rpc.community.ReplyRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.base.BaseOnrefreshAndLoadMoreFragment;
import mybaby.ui.base.MyBabyBaseActivity;
import mybaby.ui.community.adapter.DetailReplyAdapter;
import mybaby.ui.community.customclass.CusCommentLinearLayout;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.customclass.CustomAbsClass.doSomething;
import mybaby.ui.community.customclass.CustomLinkTextView;
import mybaby.ui.community.holder.ActivityItem;
import mybaby.ui.widget.BaseTLoadmoreRpc;
import mybaby.ui.widget.RoundImageViewByXfermode;
import mybaby.util.DateUtils;
import mybaby.util.ImageViewUtil;
import mybaby.util.LogUtils;
import mybaby.util.ScreenUtils;
import mybaby.util.TextViewUtil;
import mybaby.util.Utils;

// 详情
public class DetailsActivity extends MyBabyBaseActivity implements OnKeyListener {

	private BaseOnrefreshAndLoadMoreFragment fragment;

	private RelativeLayout more_topic_rela;
	private CustomLinkTextView more_topic_text;
	private TextView more_tag;
	private Button addReply;
	private View itemView;
	private LinearLayout comments_lin;
	public static EditText editReply;
	public static TextView comment_tag;

	private LinearLayout headLin;
	private RelativeLayout likeView;
	private TextView textLikeCount;

	private ActivityItem activityItem=new ActivityItem();
	private LinkedList<LikeActivity> likeActivities= new LinkedList<>();
	private AbstractMainActivity abstractMainActivity;

	private boolean isFirst = true;
	boolean hasFollowBack = false;
	private int activityId = 0;
	private int PostId;
	private Bundle bundle;
	private int type;// 动态的类型
	private int likeLastId;
	private boolean likeHasMore;

	private RefushReceiver receiver;

	@Override
	public String getPageName() {
		return getString(R.string.tiezixiangqing);
	}

	@Override
	public String getTopTitle() {
		return "详情";
	}

	@Override
	public String getRightText() {
		return "";
	}

	@Override
	public void onMediaFileDoneOver() {

	}

	@Override
	public int getContentViewId() {
		return R.layout.details;
	}

	@Override
	public int getToolbarId() {
		return R.id.toolbar;
	}

	@Override
	public int getRightImgId() {
		return R.drawable.detail_more;
	}

	@Override
	public void initData() {
		receiver = new RefushReceiver();
		receiver.registRefushReceiver();
		findByIdDetail();
		if (MyBabyApp.currentUser.isAdmin())
			editReply.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.MAX_VALUE)});
	}

	@Override
	public void initView() {
		fragment = new BaseOnrefreshAndLoadMoreFragment() {
			@Override
			public BaseQuickAdapter getBaseAdapter() {
				return new DetailReplyAdapter(DetailsActivity.this, editReply, comment_tag,getmRecyclerView());
			}

			@Override
			public boolean isEnableLoadMore() {
				return true;
			}

			@Override
			public Object[] getStausParamers() {
				return new Object[]{(long)0};
			}

			@Override
			public boolean needForceRefush() {
				return true;
			}

			@Override
			public boolean isLoadSatus() {
				return true;
			}

			@Override
			public boolean needSendAsnkRed() {
				return false;
			}

			@Override
			public BaseTLoadmoreRpc getRPC() {
				return new BaseTLoadmoreRpc() {
					@Override
					public void toTRpcInternet(Object[] objects, int lastId, final boolean isRefresh, final BaseOnrefreshAndLoadMoreFragment fragment) throws Exception {
						ReplyRPC.getActivities(PostId, lastId,
								new ReplyRPC.ListCallback() {
									@Override
									public void onSuccess(boolean hasMore, int newLastId, ReplyActivity[] items) {
										try {
											onRpcSuccess(isRefresh, newLastId, hasMore, null, items);
											if (isRefresh)
												initDataForBundle(bundle,false);
										} catch (Exception e) {
										}
									}

									@Override
									public void onFailure(long id, XMLRPCException error) {
										try {
											onRpcFail(id, error);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
					}
				};
			}

			@Override
			public String getCacheType() {
				return null;
			}

			@Override
			public View getHeaderView() {
				if (headLin==null) {
					headLin = new LinearLayout(getContext());
					headLin.setOrientation(LinearLayout.VERTICAL);
				}

				if (itemView==null) {
					itemView = LayoutInflater.from(getContext()).inflate(
							R.layout.community_all_listitem, null);
					headLin.addView(itemView);
				}

				if (likeView==null) {
					likeView = (RelativeLayout) LayoutInflater.from(DetailsActivity.this).inflate(
							R.layout.detail_likelin, null);
					headLin.addView(likeView);
				}

				likeView.setVisibility(View.GONE);

				getProgress_refush().setVisibility(View.VISIBLE);
				return headLin;
			}

			@Override
			public void init() {
				getmRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
					@Override
					public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
						super.onScrollStateChanged(recyclerView, newState);
						if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
							dismissText(DetailsActivity.this, editReply);
					}
				});
				initDataForBundle(bundle,true);
			}
		};
		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_page, fragment).commit();
	}

	@Override
	public boolean textToolbarType() {
		return false;
	}

	@Override
	public boolean imageToolbarType() {
		return true;
	}

	@Override
	public OnClickListener getRight_click() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (abstractMainActivity!=null){
					try {
						activityItem.showEditDialog(abstractMainActivity, DetailsActivity.this, true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
	}

	@Override
	public void onResume() {
		super.onResume();
		PushAgent.getInstance(DetailsActivity.this).onAppStart();//偶尔调一下激活友盟推送的用户活跃状态
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		headLin=null;
		likeView=null;

		activityItem=null;
		likeActivities.clear();
		abstractMainActivity=null;
	}

	private void findByIdDetail() {
		// TODO Auto-generated method stub
		more_topic_rela= (RelativeLayout) findViewById(R.id.more_topic_rela);
		more_topic_text= (CustomLinkTextView) findViewById(R.id.more_topic_text);
		more_tag= (TextView) findViewById(R.id.tag);
		TextViewUtil.setTagIcon(more_tag,this);

		comment_tag= (TextView) findViewById(R.id.comment_tag);
		bundle = getIntent().getExtras();
		addReply = (Button) findViewById(R.id.add_topic_reply);
		addReply.setOnClickListener(this);
		comments_lin = (LinearLayout) findViewById(R.id.comments_lin);
		// 切记编辑框标签初始化
		editReply = (EditText) findViewById(R.id.edit_topic_reply);
		editReply.setTag("");
		editReply.setHint("说点什么...");
		editReply.setOnClickListener(this);
		editReply.setOnKeyListener(this);
		editReply.clearFocus();
		editReply.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (BuildConfig.DEBUG)
					return;
				if (hasFocus) {
					new CustomAbsClass.StarEdit() {

						@Override
						public void todo() {
							// TODO Auto-generated method stub
							return;
						}
					}.StarTopicEdit(DetailsActivity.this);
					if (!MyBabyApp.currentUser.getBzRegistered()) {
						dismissText(DetailsActivity.this, editReply);
						editReply.clearFocus();
					}
				}
			}
		});
		//editReply.setEnabled(MyBabyApp.currentUser.getBzRegistered());
	}

	/**
	 * 初始化数据变量,加载Item
	 */
	private void initDataForBundle(Bundle bundle,boolean isNeedInitReplyList) {
		// TODO Auto-generated method stub

		// 此处省略了“热门”，因为普通和热门UI上显示没有任何区别，直接加载
		if (null != bundle) {
			type = bundle.getInt("type", 0);
			if (bundle.getSerializable("item") != null) {
				abstractMainActivity = (AbstractMainActivity) bundle.getSerializable("item");
				activityId = abstractMainActivity.getId();
				PostId = abstractMainActivity.getPostId();
				LogUtil.e(isFirst+"isfirst");
				if (isFirst) {
					isFirst = false;
					if (bundle.getBoolean("visiKeyboard",false)) {//初始显示键盘
						editReply.requestFocus();
						openText(editReply, comment_tag, DetailsActivity.this, 1, true);
						bundle.putBoolean("visiKeyboard",false);
					}
					new doSomething(DetailsActivity.this) {
						@Override
						public void todo() {
							initHeadView(abstractMainActivity);
						}
					};
					getLikeList(PostId, DetailsActivity.this, likeView);
					if (isNeedInitReplyList)
						initReplyList();
				} else {
					getActivityItem(activityId,isNeedInitReplyList);
				}

			} else if (bundle.getInt("activityId", 0) != 0) {
				activityId = bundle.getInt("activityId", 0);
				getActivityItem(activityId,isNeedInitReplyList);
			} else if (bundle.getInt("postId", 0) != 0) {
				// 防止反复加载多一次
				if (bundle.getInt("activityId", 0) == 0) {
					PostId = bundle.getInt("postId", 0);
					getActivityByPostidItem(PostId,isNeedInitReplyList);
				}

			} else {
				new CustomAbsClass.doSomething(DetailsActivity.this) {

					@SuppressLint("ShowToast")
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						Toast.makeText(DetailsActivity.this, "此条动态不存在!", Toast.LENGTH_SHORT).show();
						DetailsActivity.this.finish();
					}
				};
			}
		}
	}

	private void initReplyList(){
		if (fragment==null)
			return;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);//延时半秒在加载 如果在加载完赞再加载逻辑太乱
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ReplyRPC.getActivities(PostId, 0,
						new ReplyRPC.ListCallback() {
							@Override
							public void onSuccess(boolean hasMore, int newLastId, ReplyActivity[] items) {
								try {
									fragment.onRpcSuccess(true, newLastId, hasMore, null, items);
								} catch (Exception e) {
								}
							}

							@Override
							public void onFailure(long id, XMLRPCException error) {
								try {
									fragment.onRpcFail(id, error);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
			}
		}).start();
	}

	/**
	 * 获取单个类型的Item
	 */
	private ActivityItem.Holeder avtivityHolder;
	private void initHeadView(final AbstractMainActivity abstractMainActivity) {
		if (avtivityHolder==null)
			avtivityHolder=new ActivityItem.Holeder(itemView);
		if (abstractMainActivity!=null) {
			if (abstractMainActivity.getCategory() != null) {
				boolean hasThisTopic = CustomAbsClass.checkHasTopicById(abstractMainActivity.getCategory().getCategoryId());
				more_topic_rela.setVisibility(hasThisTopic ? View.GONE : View.VISIBLE);
				if (more_topic_rela.getVisibility() == View.VISIBLE) {
					ActivityItem.getSpecilText(this, more_topic_text, abstractMainActivity.getCategory().getCategoryId(), abstractMainActivity.getCategory().getTitle(),
							"的更多话题", "", true, abstractMainActivity.getCategory());
					more_topic_rela.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							CustomAbsClass.openTopicTitleList(DetailsActivity.this, abstractMainActivity.getCategory());
						}
					});
				}
			}
			try {
				fragment.getProgress_refush().setVisibility(View.GONE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			itemView.setVisibility(View.VISIBLE);
			activityItem.bindDatas(this, abstractMainActivity, true,type,avtivityHolder, itemView,0,true);
		}
		avtivityHolder.footer_line.setBackgroundColor(getResources().getColor(R.color.community_driver_gray));
		avtivityHolder.footer_line.setVisibility(View.VISIBLE);
		avtivityHolder.driver_lin.setVisibility(View.VISIBLE);

		/*
		 * avtivityHolder.tv_content.setMaxLines(Integer.MAX_VALUE);
		 * avtivityHolder.tv_content.requestLayout();
		 */
	}

	/**
	 * 根据个人activity id刷新单个动态
	 */

	public void getActivityItem(int activityid, final boolean isNeedInitReplyList) {
		new doSomething(this) {
			@Override
			public void todo() {
				if (abstractMainActivity==null) {
					itemView.setVisibility(View.GONE);
					fragment.getProgress_refush().setVisibility(View.VISIBLE);
				}
			}
		};
		ActivityRPC.getById(activityid, new ActivityCallback() {

			@Override
			public void onSuccess(final AbstractMainActivity activity) {
				// TODO Auto-generated method stub
				if (activity==null)
					LogUtils.e("is null for activityitme");
				activityToLocal(activity,isNeedInitReplyList);
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething(DetailsActivity.this) {

					@SuppressLint("ShowToast")
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						Toast.makeText(DetailsActivity.this, "此条动态不见踪影了!", Toast.LENGTH_SHORT).show();
						DetailsActivity.this.finish();
					}
				};
			}
		});
	}

	/**
	 * 根据个人postid刷新单个动态
	 */

	public void getActivityByPostidItem(int postid, final boolean isNeedInitReplyList) {
		new doSomething(this) {
			@Override
			public void todo() {
				if (abstractMainActivity==null) {
					itemView.setVisibility(View.GONE);
					fragment.getProgress_refush().setVisibility(View.VISIBLE);
				}
			}
		};
		ActivityRPC.getByPostId(postid, new ActivityCallback() {

			@Override
			public void onSuccess(final AbstractMainActivity activity) {
				// TODO Auto-generated method stub
				activityToLocal(activity,isNeedInitReplyList);
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething(DetailsActivity.this) {

					@SuppressLint({ "NewApi", "ShowToast" })
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						try {
							Toast.makeText(DetailsActivity.this, "此条动态不见踪影了!", Toast.LENGTH_SHORT).show();
							DetailsActivity.this.finish();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Utils.Loge("fix");
						}
					}
				};
			}
		});
	}

	/**
	 * 动态对象同步本地动态
	 */
	private void activityToLocal(final AbstractMainActivity activity, final boolean isNeedInitReplyList) {
		// TODO Auto-generated method stub
		new doSomething(DetailsActivity.this) {

			@Override
			public void todo() {
				// TODO Auto-generated method stub//所有布局全部刷新
				abstractMainActivity = activity;
				activityId = activity.getId();
				PostId = activity.getPostId();
				initHeadView(activity);
				getLikeList(PostId,DetailsActivity.this,likeView);
				if (isNeedInitReplyList)
					initReplyList();
			}
		};
	}

	/**
	 * 加载“赞”视图布局
	 */
	private void getLikeView(final Context context, final View parentView,final LikeActivity[] items){
		// TODO Auto-generated method stub
		if (items.length>0){
			parentView.setVisibility(View.VISIBLE);
		}else {
			parentView.setVisibility(View.GONE);
			return;
		}
		parentView.setBackgroundColor(Color.WHITE);
		int with = MyBabyApp.screenWidth;
		int linWidth=with-ScreenUtils.dip2px(DetailsActivity.this, 90);
		int sWidth=ScreenUtils.dip2px(DetailsActivity.this, 30)+10;
		final int num=linWidth/sWidth;
		List<LikeActivity> activities=new ArrayList<>();
		for (int i = 0; i < items.length; i++) {
			if (i==num)
				break;
			activities.add(items[i]);
		}
		textLikeCount = (TextView) parentView.findViewById(R.id.like);
		if (abstractMainActivity!=null)
			textLikeCount.setText(abstractMainActivity.getLikeCount()==0?"赞":"赞(" + abstractMainActivity.getLikeCount() + ")");
		parentView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openLikeActivity();
			}
		});

		CusCommentLinearLayout.setLinGreat(context, (LinearLayout) parentView.findViewById(R.id.like_lin), activities.toArray(), new ViewReUseFaceListener() {
			@Override
			public int backViewRes() {
				return 0;
			}

			@Override
			public View backView() {
				return new RoundImageViewByXfermode(context);
			}

			@Override
			public void justItemToDo(final Object data, View itemView, int position) {
				RoundImageViewByXfermode imageView = (RoundImageViewByXfermode) itemView;
				imageView.setScaleType(ScaleType.CENTER_CROP);
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
				layoutParams.width = layoutParams.height = ScreenUtils.dip2px(DetailsActivity.this, 30);
				layoutParams.setMargins(5, 5, 5, 5);// 4个参数按顺序分别是左上右下
				LikeActivity a=(LikeActivity) data;
				if (a!=null)
					if (a.getUser()!=null) {
						ImageViewUtil.displayImage(a.getUser().getAvatarThumbnailUrl(), imageView);
					}
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CustomAbsClass.starUserPage(context, ((LikeActivity) data).getUser());
					}
				});
			}
		});
	}

	public void openLikeActivity() {
		Intent intent = new Intent(DetailsActivity.this, LikeListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("items", likeActivities);
		bundle.putInt("postId", PostId);
		bundle.putBoolean("hasMore", likeHasMore);
		bundle.putInt("lastId", likeLastId);
		intent.putExtras(bundle);
		startActivity(intent);

	}

	/**
	 * 加载“赞”列表
	 */

	private void getLikeList(int PostId, final Context context, final View parent) {
		LikeRPC.getActivities(PostId, 0,new mybaby.rpc.community.LikeRPC.ListCallback() {

			@SuppressLint("NewApi")
			@Override
			public void onSuccess(boolean hasMore, int newLastId,
								  final LikeActivity[] items) {
				// TODO Auto-generated method stub
				likeLastId = newLastId;
				likeHasMore = hasMore;
				likeActivities.clear();
				for (LikeActivity activity:items){
					likeActivities.add(activity);
				}
				new doSomething(context) {
					@Override
					public void todo() {
						getLikeView(context,parent,items);
					}
				};
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub

			}
		});
	}


	/**
	 * 发表话题评论或向某人评论
	 */
	private void addTopicReply(int PostId, final String content,RecyclerView recyclerView)
			throws Exception {
		final ReplyActivity item = new ReplyActivity();
		item.setContent(content);
		User user = MyBabyApp.currentUser;
		user.setName(MyBabyApp.currentUser.getName());
		user.setAvatarThumbnailUrl(MyBabyApp.currentUser.getAvatarThumbnailUrl());
		item.setDatetime_gmt(DateUtils.localTime2GMTTime(System.currentTimeMillis()));// 获取当前时间,将当前时间转为国际时间(rbj8/31修改：在模型更改)
		item.setUser(user);
		if (PostId != 0) {
			item.setReplyId(PostId);
			User replyUser = new User();
			replyUser.setName(editReply.getHint().toString().trim()
					.substring(2).trim());
			item.setReplyUser(replyUser);
		}
		final DetailReplyAdapter adapter= (DetailReplyAdapter) recyclerView.getAdapter();
		adapter.add(adapter.getData().size() , item);
		// 切到刚才回复的那条
		recyclerView.smoothScrollToPosition(adapter.getData().size() - 1);
		if (PostId == 0) {
			// 如果回复主题需要将id归置
			PostId = abstractMainActivity.getPostId();
		}
		ReplyRPC.add(content, PostId, new CallbackForId() {

			@Override
			public void onSuccess(final int Id) {
				// TODO Auto-generated method stub
				// 更新主线程UI
				new CustomAbsClass.doSomething(DetailsActivity.this) {
					@Override
					public void todo() {
						item.setReplyId(Id);
						adapter.notifyDataSetChanged();
						//Toast.makeText(DetailsActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
					}
				};
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub
			}
		});

	}

	@Override
	public void finish() {
		super.finish();
		try {
			dismissText(DetailsActivity.this, editReply);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void dismissText(Context context,EditText editReply){
		if (editReply!=null) {
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editReply.getWindowToken(), 0);
		}
	}

	/**
	 * 弹出键盘评论 0隐藏1显示
	 */
	public static void openText(EditText editReply, TextView comment_tag,
								Context context, int sign, boolean isInit) {
		// TODO Auto-generated method stub
		try {
			editReply.requestFocus();
			// 隐藏软键盘
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			//imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

			// 强制控制软键盘
			if (sign == 0) {
				imm.hideSoftInputFromWindow(editReply.getWindowToken(), 0);
			} else {
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				//imm.showSoftInput(editReply, InputMethodManager.SHOW_FORCED);
			}

			if (isInit || sign == 0) {
				editReply.setHint("");
				editReply.setTag("");
				// editReply.setText("");
				editReply.setHint("说点什么...");
				comment_tag.setText("评论");
				if (sign == 0) {
					// editReply.clearFocus();
					if (isInit) {
						editReply.setText("");
						editReply.setTag("");
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressLint("ShowToast")
	//@Event({ R.id.add_topic_reply })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
			case R.id.edit_topic_reply:
				new CustomAbsClass.StarEdit() {

					@Override
					public void todo() {
						// TODO Auto-generated method stub
						return;
					}
				};
				break;
			case R.id.like_lin:
				dismissText(DetailsActivity.this, editReply);
				openLikeActivity();
				break;
			case R.id.add_topic_reply:
				isSendReply();
				break;
			case R.id.actionbar_back:
				onBackPressed();
				break;
			case R.id.follow:
			/*tv_follow.setText("");
			if (hasFollowBack) {
				// initFollow(abstractMainActivity.getUser().getUserId(),
				// DetailsActivity.this).toUnfollow();
			} else {
				if (showPopAtPersonpage!=null) {
					showPopAtPersonpage.dismiss();
				}
				initFollow(abstractMainActivity.getUser().getUserId(),tv_follow,
						DetailsActivity.this).toFollow();
				follow_loadingBar.setVisibility(View.VISIBLE);
			}*/
				break;
		}
	}

	// 广播接收并响应处理 刷新页面
	private class RefushReceiver extends BroadcastReceiver {
		public void registRefushReceiver() {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Detail_Refush);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Detail_Like_Add);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Detail_Like_Remove);
			LocalBroadcastManager.getInstance(MyBabyApp.getContext())
					.registerReceiver(this, filter);
		}

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (null == arg1.getExtras())
				return;
			try {
				if (arg1.getAction().equals(Constants.BroadcastAction.BroadcastAction_Detail_Like_Add)) {
                        likeActivities.addFirst((LikeActivity) arg1.getExtras()
                                .getSerializable("likeActivity"));
                        final LikeActivity[] items=new LikeActivity[likeActivities.size()];
                        for (int i = 0; i < likeActivities.size(); i++) {
                            items[i]=likeActivities.get(i);
                        }
                        new doSomething(DetailsActivity.this) {
                            @Override
                            public void todo() {
                                getLikeView(DetailsActivity.this,likeView,items);
                            }
                        };
                } else if (arg1.getAction().equals(Constants.BroadcastAction.BroadcastAction_Detail_Like_Remove)) {
                        for (int i = 0; i < likeActivities.size(); i++) {
                            if (likeActivities.get(i).getUser().getUserId() == MyBabyApp.currentUser
                                    .getUserId()) {
                                likeActivities.remove(i);
                            }
                        }
                        final LikeActivity[] items=new LikeActivity[likeActivities.size()];
                        for (int i = 0; i < likeActivities.size(); i++) {
                            items[i]=likeActivities.get(i);
                        }
                        new doSomething(DetailsActivity.this) {
                            @Override
                            public void todo() {
                                getLikeView(DetailsActivity.this,likeView,items);
                            }
                        };
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 判断是否发送回复后会自动回复
	 */
	private void isSendReply() {
		new CustomAbsClass.StarEdit() {

			@Override
			public void todo() {
				// TODO Auto-generated method stub
				sendReply();
			}
		}.StarTopicEdit(DetailsActivity.this);
	}

	private void sendReply() {
		// TODO Auto-generated method stub
		editReply=(EditText) findViewById(R.id.edit_topic_reply);
		if (editReply==null)
			return;
		if (!TextUtils.isEmpty(editReply.getText().toString().trim())) {
			String tags = editReply.getTag().toString();
			try {
				addTopicReply(TextUtils.isEmpty(tags) ? 0 : Integer.parseInt(tags),
						editReply.getText().toString(),fragment.getmRecyclerView());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Toast.makeText(DetailsActivity.this, "请在详情加载完成之后进行操作", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			openText(editReply, comment_tag, DetailsActivity.this, 0, true);
		} else {
			Toast.makeText(DetailsActivity.this, "还没有输入文字~", Toast.LENGTH_SHORT).show();
		}
	}

	//@Event({ R.id.edit_topic_reply })
	@Override
	public boolean onKey(View view, int keyCode, KeyEvent arg2) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_ENTER
				&& arg2.getAction() == KeyEvent.ACTION_DOWN) {
			isSendReply();
			return true;
		}
		return false;
	}
}
