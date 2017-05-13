package mybaby.ui.community;

/**
 * 测试页面
 */

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.umeng.message.proguard.T;

import org.xmlrpc.android.XMLRPCException;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.models.community.Banner;
import mybaby.rpc.community.CommunityItemRPC;
import mybaby.ui.base.BaseOnrefreshAndLoadMoreFragment;
import mybaby.ui.base.MyBabyBaseActivity;
import mybaby.ui.community.adapter.MultipleItemQuickAdapter;
import mybaby.ui.community.holder.ItemState;
import mybaby.ui.widget.BaseTLoadmoreRpc;


public class TestActivity extends MyBabyBaseActivity{

	@Override
	public String getPageName() {
		return "test page";
	}

	@Override
	public String getTopTitle() {
		return "测试页面";
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

	}

	@Override
	public void initView() {
		final BaseOnrefreshAndLoadMoreFragment fragment = new BaseOnrefreshAndLoadMoreFragment() {
			@Override
			public BaseQuickAdapter<T> getBaseAdapter() {
				return new MultipleItemQuickAdapter(getContext(), new ArrayList(), true, getWebViewOnTouch());
			}

			@Override
			public boolean isEnableLoadMore() {
				return true;
			}

			@Override
			public Object[] getStausParamers() {
				return new Object[0];
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
				return new BaseTLoadmoreRpc() {
					@Override
					public void toTRpcInternet(Object[] objects, int lastId, final boolean isRefresh, final BaseOnrefreshAndLoadMoreFragment fragment) throws Exception {
						CommunityItemRPC.getSuitRPCList(CommunityItemRPC.getCommunity(), lastId, null, new CommunityItemRPC.ListCallback() {
							@Override
							public void onSuccess(boolean hasMore, int newLastId, List<ItemState> items, Banner[] banners) {
								try {
									fragment.onRpcSuccess(isRefresh,newLastId,hasMore,null,items);
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

			@Override
			public String getCacheType() {
				return "testactivity_new";
			}

			@Override
			public View getHeaderView() {
				TextView textView=new TextView(getActivity());
				textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1000));
				textView.setText("w我的天");
				return textView;
			}

			@Override
			public void init() {

			}
		};
		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_page, fragment).commit();
		toolbar.postDelayed(new Runnable() {
			@Override
			public void run() {
				fragment.autoRefresh();
			}
		},5000);
	}

	@Override
	public boolean textToolbarType() {
		return true;
	}

	@Override
	public boolean imageToolbarType() {
		return false;
	}

	@Override
	public OnClickListener getRight_click() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		};
	}
}
