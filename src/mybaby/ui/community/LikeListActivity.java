package mybaby.ui.community;

import android.view.View;
import android.view.View.OnClickListener;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.view.annotation.ContentView;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.models.community.activity.LikeActivity;
import mybaby.rpc.community.LikeRPC;
import mybaby.ui.base.BaseOnrefreshAndLoadMoreFragment;
import mybaby.ui.base.MyBabyBaseActivity;
import mybaby.ui.community.adapter.LikeAdapter;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.widget.BaseTLoadmoreRpc;

@ContentView(R.layout.community_all)
public class LikeListActivity  extends MyBabyBaseActivity {

	private List<LikeActivity> likeActivities=new ArrayList<>();
	private int PostId;
	private boolean hasmore=false;
	private int lastId=0;

	@Override
	public String getPageName() {
		return getString(R.string.tiezizanliebiao);
	}

	@Override
	public String getTopTitle() {
		return "赞列表";
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
		PostId=getIntent().getIntExtra("postId", 0);
		likeActivities= (List<LikeActivity>) getIntent().getSerializableExtra("items");
		hasmore=getIntent().getBooleanExtra("hasMore",false);
		lastId=getIntent().getIntExtra("lastId", 0);
	}

	@Override
	public void initView() {
		final BaseOnrefreshAndLoadMoreFragment fragment = new BaseOnrefreshAndLoadMoreFragment() {
			@Override
			public BaseQuickAdapter getBaseAdapter() {
				final LikeAdapter adapter=new LikeAdapter();
				adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
					@Override
					public void onItemClick(View view, int i) {
						try {
							CustomAbsClass.starUserPage(getActivity(), adapter.getItem(i).getUser());
						} catch (Exception e) {
							e.printStackTrace();
						}
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

						LikeRPC.getActivities(PostId, lastId,
								new LikeRPC.ListCallback() {
									@Override
									public void onSuccess(boolean hasMore, int newLastId, LikeActivity[] items) {
										try {
											/*likeActivities.clear();
											for (LikeActivity activity:items){
												likeActivities.add(activity);
											}*/
											onRpcSuccess(isRefresh, newLastId, hasMore, null, items);
										} catch (Exception e) {
											e.printStackTrace();
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
				return null;
			}

			@Override
			public void init() {
				try {
					onRpcSuccess(true, 0, false, null, likeActivities);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
	public OnClickListener getRight_click() {
		return null;
	}

}
