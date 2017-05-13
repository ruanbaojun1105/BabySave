package mybaby.ui.widget;

import mybaby.ui.base.BaseOnrefreshAndLoadMoreFragment;

public interface BaseTLoadmoreRpc {
	/**
	 *
	 * @param objects
	 * @param lastId  此处虽然id是int类型，但是最后都是字符串类型来解析
	 * @param isRefresh
	 * @param fragment
	 * @throws Exception
	 */
	void toTRpcInternet(Object[] objects, int lastId, boolean isRefresh, BaseOnrefreshAndLoadMoreFragment fragment) throws Exception;
	}