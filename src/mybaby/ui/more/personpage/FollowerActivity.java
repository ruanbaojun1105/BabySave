package mybaby.ui.more.personpage;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.xmlrpc.android.XMLRPCException;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.User;
import mybaby.rpc.UserRPC;
import mybaby.rpc.UserRPC.ListCallback;
import mybaby.ui.base.BaseOnrefreshAndLoadMoreFragment;
import mybaby.ui.base.MyBabyBaseActivity;
import mybaby.ui.user.UserListAdapter;
import mybaby.ui.widget.BaseTLoadmoreRpc;

/**
 * 粉丝列表的activity
 * 
 * @author Administrator
 * 
 */
public class FollowerActivity extends MyBabyBaseActivity {

	private int userId=0;

	@Override
	public String getPageName() {
		return "个人粉丝列表";
	}

	@Override
	public String getTopTitle() {
		return "我的粉丝";
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
		userId = getIntent().getIntExtra("userId", 0);
	}

	@Override
	public void initView() {
		BaseOnrefreshAndLoadMoreFragment fragment = new BaseOnrefreshAndLoadMoreFragment() {
			@Override
			public BaseQuickAdapter getBaseAdapter() {
				return new UserListAdapter();
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
				return false;
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
				return get_my_listRPC();
			}

			@Override
			public String getCacheType() {
				return Constants.CacheKey_FollowerActivity_follow;
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

				UserRPC.getFollower(userId, lastId, new ListCallback() {
					@Override
					public void onSuccess(int lastId, Boolean hasMore, User[] arrUser) {
						try {
							fragment.onRpcSuccess(isRefresh, lastId, hasMore, null, arrUser);
						} catch (Exception e) {
							e.printStackTrace();
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
		};
	}
}
