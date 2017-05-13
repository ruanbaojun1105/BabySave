package mybaby.ui.Notification;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.xmlrpc.android.XMLRPCException;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.notification.Notification;
import mybaby.rpc.notification.NotificationRPC;
import mybaby.ui.Notification.adapter.NotificationAdapter;
import mybaby.ui.base.BaseOnrefreshAndLoadMoreFragment;
import mybaby.ui.base.MyBabyBaseActivity;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.widget.BaseTLoadmoreRpc;

/**
 *  各种动态通知的activity
 * 
 * @author Administrator
 * 
 */
public class NotificationActivity extends MyBabyBaseActivity {

	private int caseIndex;
	private int unread;
	private String title="";
	private String PAGE[]=new String[]{"我的通知","我参与的通知","获取我关注通知列表"};

	@Override
	public String getPageName() {
		return PAGE[caseIndex];
	}

	@Override
	public String getTopTitle() {
		return title;
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
		return R.layout.blank_page_base;
	}

	@Override
	public int getToolbarId() {
		return R.id.toolbar;
	}

	@Override
	public int getRightImgId() {
		return 0;
	}

	@Override
	public void initData() {
		if (getIntent().getExtras()==null)
			return;
		title = getIntent().getStringExtra("title");
		unread = getIntent().getIntExtra("unread",0);
		caseIndex = getIntent().getIntExtra("caseRpcIndex", 0);
	}

	@Override
	public void initView() {
		final BaseOnrefreshAndLoadMoreFragment fragment = new BaseOnrefreshAndLoadMoreFragment() {
			@Override
			public BaseQuickAdapter getBaseAdapter() {
				final NotificationAdapter adapter=new NotificationAdapter(getContext(),unread);
				adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
					@Override
					public void onItemClick(View view, int i) {
						// 当类型为回复或者点赞的时候，进入该动态
						Notification notification = adapter.getItem(i);
						if (notification.getType() == Notification.NotificationType.Like
								|| notification.getType() == Notification.NotificationType.Reply) {
							CustomAbsClass.openDetailPage(getActivity(), notification.getSourceId());
						}
						// 当类型为关注的时候，进入用户主页
						if (notification.getType() == Notification.NotificationType.Follow) {
							CustomAbsClass.starUserPage(getContext(), notification.getUser());
						}
						// 点击举报回复
				/*if (allItems.get(notificationMessagePosition).getType() == NotificationType.System) {
				}*/
					}
				});
				return adapter;
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
				return unread>0;
			}

			@Override
			public boolean isLoadSatus() {
				return true;
			}

			@Override
			public boolean needSendAsnkRed() {
				return true;
			}

			@Override
			public BaseTLoadmoreRpc getRPC() {
				BaseTLoadmoreRpc rpc = null;
				switch (caseIndex){
					case 0:rpc=get_my_listRPC();
						break;
					case 1:rpc=get_join_listRPC();
						break;
					case 2:rpc=get_common_listRPC();
						break;
				}
				return rpc;
			}

			@Override
			public String getCacheType() {
				return Constants.CacheKey_CommunityActivity_Notifications+caseIndex;
			}

			@Override
			public View getHeaderView() {
				return null;
			}

			@Override
			public void init() {

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
		return false;
	}

	@Override
	public View.OnClickListener getRight_click() {
		return null;
	}

	//获取我的动态通知列表bz.xmlrpc.notification.get_my_list
	private BaseTLoadmoreRpc get_my_listRPC() {
		return new BaseTLoadmoreRpc() {
			@Override
			public void toTRpcInternet(Object[] objects, int lastId, final boolean isRefresh, final BaseOnrefreshAndLoadMoreFragment fragment) throws Exception {
				NotificationRPC.get_my_list(lastId, new NotificationRPC.ListCallback() {
					@Override
					public void onSuccess(boolean hasMore, int newLastId, Notification[] items) {
						try {
							fragment.onRpcSuccess(isRefresh, newLastId, hasMore, null,items);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(long id, XMLRPCException error) {
						try {
							fragment.onRpcFail(id,error);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		};
	}
	//获取我参与的列表bz.xmlrpc.notification.get_join_list
	private BaseTLoadmoreRpc get_join_listRPC() {
		return new BaseTLoadmoreRpc() {
			@Override
			public void toTRpcInternet(Object[] objects, int lastId, final boolean isRefresh, final BaseOnrefreshAndLoadMoreFragment fragment) throws Exception {
				NotificationRPC.get_join_list(lastId, new NotificationRPC.ListCallback() {
					@Override
					public void onSuccess(boolean hasMore, int newLastId, Notification[] items) {
						try {
							fragment.onRpcSuccess(isRefresh, newLastId, hasMore, null,items);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(long id, XMLRPCException error) {
						try {
							fragment.onRpcFail(id,error);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		};
	}

	//获取我关注通知列表
	private BaseTLoadmoreRpc get_common_listRPC() {
		return new BaseTLoadmoreRpc() {
			@Override
			public void toTRpcInternet(Object[] objects, int lastId, final boolean isRefresh, final BaseOnrefreshAndLoadMoreFragment fragment) throws Exception {
				NotificationRPC.get_follow_list(lastId, new NotificationRPC.ListCallback() {
					@Override
					public void onSuccess(boolean hasMore, int newLastId, Notification[] items) {
						try {
							fragment.onRpcSuccess(isRefresh, newLastId, hasMore, null,items);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(long id, XMLRPCException error) {
						try {
							fragment.onRpcFail(id,error);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		};
	}
}
