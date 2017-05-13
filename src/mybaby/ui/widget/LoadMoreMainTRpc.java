package mybaby.ui.widget;

import android.support.v4.app.Fragment;

import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.community.fragment.LoadMoreRecyclerViewFragment;

public interface LoadMoreMainTRpc {
	void toTRpcInternet(int lastId, int userId, int parentId,
							boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception;

	/**
	 * 为了兼容
	 * @param isRefresh
	 * @param lastId
	 * @param userId
	 * @param parentId
	 * @param isRefresh
	 *@param fragment  @throws Exception
	 */
	void toTRpcInternet(int lastId, int userId, int parentId,
						boolean isRefresh, LoadMoreListViewFragment fragment) throws Exception;

	void toTRpcInternet(Object[] objects,int lastId,boolean isRefresh, Fragment fragment) throws Exception;

	}