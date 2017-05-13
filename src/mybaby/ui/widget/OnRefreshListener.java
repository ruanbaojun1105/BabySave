package mybaby.ui.widget;
/**
 * @author andong
 * 刷新数据的监听事件
 */
public interface OnRefreshListener {

	/**
	 * 下拉刷新数据时回调
	 */
	void onPullDownRefresh();
	
	/**
	 * 加载更多数据时回调
	 */
	void onLoadingMore();
}