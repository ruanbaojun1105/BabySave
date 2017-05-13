/**
 * @author baojun
 * 
 */

package mybaby.ui.community;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.umeng.analytics.MobclickAgent;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.common.util.LogUtil;

import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.community.Banner;
import mybaby.rpc.community.CommunityItemRPC;
import mybaby.rpc.community.CommunityItemRPC.ListCallback;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.community.fragment.LoadMoreListViewFragment.TRpc;
import mybaby.ui.community.fragment.LoadMoreRecyclerViewFragment;
import mybaby.ui.community.holder.ItemState;

/**
 * 
 * @author baojun
 * 
 */
@Deprecated //弃用
public class CommunityMainActivity extends FragmentActivity {

	private int current = 0;
	public static LoadMoreListViewFragment neighborFragment1 = null;
	public static LoadMoreListViewFragment neighborFragment2 = null;
	//private FrameLayout content_page;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blank_page);
		//content_page=(FrameLayout) findViewById(R.id.content_page);
		initPage();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent
				.onPageStart(current == 0 ? getString(R.string.zhudongtailiebiao)
						: getString(R.string.zhoubiandongtailiebiao)); // 统计页面
	}

	public void onPause() {
		super.onPause();
		MobclickAgent
				.onPageEnd(current == 0 ? getString(R.string.zhudongtailiebiao)
						: getString(R.string.zhoubiandongtailiebiao)); // 统计页面
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void initPage() {
		// TODO Auto-generated method stub
		current = getIntent().getExtras().getInt("current", 0);
		if (current==0) {
			neighborFragment1 = new LoadMoreListViewFragment(Constants.CacheKey_CommunityActivity_Main,30,true);
			neighborFragment1.setOnTRpcInternet(initView(neighborFragment1,current));
			neighborFragment1.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.content_page, neighborFragment1).commit();
		}else {
			neighborFragment2 = new LoadMoreListViewFragment(Constants.CacheKey_CommunityActivity_Near,30,true);
			neighborFragment2.setOnTRpcInternet(initView(neighborFragment2,current));
			neighborFragment2.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.content_page, neighborFragment2).commit();
		}
	}

	/**
	 * 初始化刷新数据
	 */
	public  TRpc initView(final LoadMoreListViewFragment neighborFragment,int current) {
		// TODO Auto-generated method stub
		if (current == 0) {
			return new TRpc() {

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception {

				}

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId,
										   final boolean isRefresh, LoadMoreListViewFragment fragment) {
					// TODO Auto-generated method stub
					CommunityItemRPC.getForMain(lastId, new ListCallback() {

						@Override
						public void onSuccess(boolean hasMore, int newLastId,
											  List<ItemState> items, Banner[] banners) {
							// TODO Auto-generated method stub
							try {
								neighborFragment.onTSuccessToList(isRefresh,
										hasMore, newLastId, items);
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
		} else if (current == 1) {
			return new TRpc() {

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception {

				}

				@Override
				public void toTRpcInternet(int lastId, int userId, int parentId,
										   final boolean isRefresh, LoadMoreListViewFragment fragment) {
					// TODO Auto-generated method stub
					CommunityItemRPC.getForNearby(lastId, new ListCallback() {

						@Override
						public void onSuccess(boolean hasMore, int newLastId,
											  List<ItemState> items, Banner[] banners) {
							// TODO Auto-generated method stub
							try {
								neighborFragment.onTSuccessToList(isRefresh,
                                        hasMore, newLastId, items);
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
		return null;
	}

	
	
	//此处重写此方法，否则双击退出无效
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub  防止两次返回截获事件 &&current==0
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0&&(current!=0||current!=1)) {
			// MobclickAgent.onKillProcess(Context context);//如果开发者调用
			// Process.kill 或者 System.exit 之类的方法杀死进程，请务必在此之前调用此方法，用来保存统计数据。
			//MainUtils.ExitApp(this, 0);
			//LogUtils.e("~~~~~~~~~onback");
		}
		return false;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			LogUtil.e("..屏蔽menu事件");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
