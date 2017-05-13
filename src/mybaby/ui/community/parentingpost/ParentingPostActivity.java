package mybaby.ui.community.parentingpost;

import android.view.View;
import android.view.View.OnClickListener;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.xmlrpc.android.XMLRPCException;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.community.ParentingPost;
import mybaby.rpc.community.ParentingPostRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.base.BaseOnrefreshAndLoadMoreFragment;
import mybaby.ui.base.MyBabyBaseActivity;
import mybaby.ui.community.adapter.ParentingPostAdapter;
import mybaby.ui.widget.BaseTLoadmoreRpc;

public class ParentingPostActivity extends MyBabyBaseActivity {

	@Override
	public String getPageName() {
		return "育儿文章列表";
	}

	@Override
	public String getTopTitle() {
		return "育儿";
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
			public BaseQuickAdapter getBaseAdapter() {
				return new ParentingPostAdapter(getContext());
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
				return new BaseTLoadmoreRpc() {
					@Override
					public void toTRpcInternet(Object[] objects, int lastId, final boolean isRefresh, final BaseOnrefreshAndLoadMoreFragment fragment) throws Exception {

						ParentingPostRPC.getParentingList(MyBabyApp.currentUser.getUserId(), "", "", "", (Long) objects[0], new ParentingPostRPC.ParentingListCallback() {

							@Override
							public void onSuccess(ParentingPost[] parentingPosts) {
								// TODO Auto-generated method stub
								long maxTime=0;
								boolean hasMore=false;
								List<ParentingPost> list=new ArrayList<ParentingPost>();
								if (parentingPosts!=null) {
									for (ParentingPost post:parentingPosts){
										list.add(post);
									}
									//list = Arrays.asList(parentingPosts);
									if (list.size()>0){
										maxTime=list.get(list.size()-1).getDatetime();
										hasMore=true;
									}
								}

								try {
									fragment.onRpcSuccess(isRefresh, 0, hasMore, new Object[]{maxTime},list);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onFailure(long id, XMLRPCException error) {
								// TODO Auto-generated method stub
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

			@Override
			public String getCacheType() {
				return Constants.CacheKey_CommunityActivity_ParentPost;
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
	public OnClickListener getRight_click() {
		return null;
	}

}
