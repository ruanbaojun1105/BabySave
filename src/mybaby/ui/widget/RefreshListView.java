package mybaby.ui.widget;

import me.hibb.mybaby.android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

public class RefreshListView extends ListView implements OnScrollListener {
	
	private int mFirstVisibleItem = -1; // 滚动时屏幕上第一个显示item的position
	private int downY; // 按下时y轴的偏移量
	private int headerViewHeight; // 头布局的高度
	private View headerView; // 头布局对象

	private final int PULL_DOWN_REFRESH = 0; // 下拉刷新状态
	private final int RELEASE_REFRESH = 1; // 释放刷新
	private final int REFRESHING = 2; // 正在刷新中

	private int currentHeaderState = PULL_DOWN_REFRESH; // 当前头布局的状态, 默认为: 下拉刷新
	private ImageView ivArrow; // 箭头
	private ProgressBar mProgressBar; // 进度条

	private Animation upAnimation; // 向上旋转的动画
	private Animation downAnimation; // 向下旋转的动画
	private Animation releaseAnimation; //释放时的动画

	private OnRefreshListener mOnRefreshListener; // 用户的刷新回调事件
	private View footerView; // 脚布局对象
	private int footerViewHeight; // 脚布局高度
	private boolean isLoadingMore = false; // 是否正在加载更多中
	private boolean isNeedRefresh = true; //是否需要下拉刷新功能
	private boolean hasMore; //是否有更多数据需要加载
	
	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 加头布局
		initHeader();

		// 加脚布局
		initFooter();
		
		setHeaderDividersEnabled(false);
		setFooterDividersEnabled(false);

		setOnScrollListener(this);
	}
	
	/**
	 * 设置是否需要下拉刷新功能
	 * @param isShowHeader true为需要下拉刷新功能，false为不需要。默认为true
	 */
	public void setNeedRefresh(boolean isNeedRefresh) {
		this.isNeedRefresh = isNeedRefresh;
	}

	/**
	 * 初始化脚布局
	 */
	private void initFooter() {
		footerView = View.inflate(getContext(), R.layout.listview_footer, null);

		footerView.measure(0, 0);
		footerViewHeight = footerView.getMeasuredHeight();
		System.out.println("脚布局的高度: " + footerViewHeight);
	}
	

	private void initHeader() {
		headerView = View.inflate(getContext(), R.layout.listview_header, null);
		ivArrow = (ImageView) headerView
				.findViewById(R.id.iv_listview_header_arrow);
		mProgressBar = (ProgressBar) headerView
				.findViewById(R.id.pb_listview_header_progress);

		// 手动出发headerView的测量方法, 让它帮我们测量
		headerView.measure(0, 0);

		// getHeight 只能在布局已经显示在屏幕上之后才可以获取到值.
		// getMeasuredHeight 可以在measure方法执行之后, 得到高度
		headerViewHeight = headerView.getMeasuredHeight();
		System.out.println("头布局高度 : " + headerViewHeight);

		headerView.setPadding(0, -headerViewHeight+1, 0, 0);
		
		addHeaderView(headerView);
		

		initAnimation();
	}

	/**
	 * 初始化头布局的动画
	 */
	private void initAnimation() {
		upAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		upAnimation.setDuration(200);
		upAnimation.setFillAfter(true);

		downAnimation = new RotateAnimation(-180, -360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		downAnimation.setDuration(200);
		downAnimation.setFillAfter(true);

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN: // 按下
			downY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE: // 移动
			// 判断当前是否是正在刷新中
			if (currentHeaderState == REFRESHING) {
				// 当前正在刷新, 直接跳出.
				break;
			}
			int moveY = (int) ev.getY();
			int diff = moveY - downY;
			System.out.println("downY---=" + downY);
			System.out.println("moveY---=" + moveY);
			System.out.println("diff-----=" + diff);
			if (mFirstVisibleItem == 0 && diff > 0 && isNeedRefresh) { // 当前屏幕是在顶部, 并且是向下拖动
				int paddingTop = -headerViewHeight + diff /3;
				releaseAnimation = new TranslateAnimation(0, 0, paddingTop, 0);
				releaseAnimation.setDuration(150);
				releaseAnimation.setFillAfter(true);
				System.out.println("paddingTop-------=" + paddingTop);
				if (paddingTop > 0 && currentHeaderState == PULL_DOWN_REFRESH) {
					// 完全显示, 进入到释放刷新状态
					System.out.println("释放刷新");
					currentHeaderState = RELEASE_REFRESH;
					refreshHeaderState();
				} else if (paddingTop < 0
						&& currentHeaderState == RELEASE_REFRESH) {
					// 隐藏了一部分, 进入到下拉刷新状态
					System.out.println("下拉刷新");
					currentHeaderState = PULL_DOWN_REFRESH;
					refreshHeaderState();
				}
				setSelection(0);
				headerView.setPadding(0, paddingTop, 0, 0);
//				return false; // 自己响应用户的事件  (加入之后滚动会自己响应点击事件)
			}
			break;
		case MotionEvent.ACTION_UP: // 抬起
			if (currentHeaderState == PULL_DOWN_REFRESH) {
				// 当前是下拉刷新抬起的, 隐藏头布局
				headerView.setPadding(0, -headerViewHeight, 0, 0);
			} else if (currentHeaderState == RELEASE_REFRESH) {
				// 当前是释放刷新抬起的, 把头布局的paddingTop置为0, 并且进入到正在刷新中
				startAnimation(releaseAnimation);
				headerView.setPadding(0, 0, 0, 0);
				currentHeaderState = REFRESHING;
				refreshHeaderState();
				

				// 调用用户的回调事件
				if (mOnRefreshListener != null) {
					mOnRefreshListener.onPullDownRefresh();
				}
			}
			break;
		}

		return super.onTouchEvent(ev); // 调用父类默认的滚动效果
	}

	/**
	 * 根据currentHeaderState来刷新头布局的状态
	 */
	private void refreshHeaderState() {
		switch (currentHeaderState) {
		case PULL_DOWN_REFRESH: // 下拉刷新
			ivArrow.startAnimation(downAnimation);
			break;
		case RELEASE_REFRESH: // 释放刷新
			ivArrow.startAnimation(upAnimation);
			break;
		case REFRESHING: // 正在刷新中
			ivArrow.setVisibility(View.INVISIBLE);
			ivArrow.clearAnimation(); // 清除imageView身上的动画
			mProgressBar.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	/**
	 * 当滚动时调用. firstVisibleItem 滚动时屏幕上第一个可见的item的position visibleItemCount
	 * 屏幕上显示了多少条item totalItemCount ListView的总长度
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		// System.out.println("滚动了: " + firstVisibleItem);
	}

	/**
	 * 当滚动状态改变时, 回调此方法 scrollState 当前滚动的状态
	 * 
	 * SCROLL_STATE_IDLE 停滞状态 SCROLL_STATE_TOUCH_SCROLL 手指按下移动的状态
	 * SCROLL_STATE_FLING 猛地一滑
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 当滚动状态是处于停止或者快速滑动时, 并且当前屏幕最后一个显示的item的索引是ListView总条目-1
		if ((scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING)
				&& getLastVisiblePosition() == (getCount() - 1)) {

			if (!isLoadingMore && hasMore) {
				System.out.println("当前滚动到底部了, 可以加载更多");
				isLoadingMore = true;
				// 显示脚布局
				addFooterView(footerView);

				// 滑动到底部, 让脚布局显示出来
				setSelection(getCount() - 1);

				// 调用用户的回调事件, 让用户去加载更多数据
				if (mOnRefreshListener != null) {
					mOnRefreshListener.onLoadingMore();
				}
			}
		}

	}

	public void setOnRefreshListener(boolean hasMore,OnRefreshListener listener) {
		mOnRefreshListener = listener;
		this.hasMore = hasMore;
	}

	/**
	 * 隐藏头布局
	 */
	public void hideHeaderView() {
		headerView.setPadding(0, -headerViewHeight, 0, 0);
		currentHeaderState = PULL_DOWN_REFRESH;
		mProgressBar.setVisibility(View.INVISIBLE);
		ivArrow.setVisibility(View.VISIBLE);
	}
	
	
	public void RemoveHeaderView(){
		removeHeaderView(headerView);
	}
	
	public void addHeaderView(){
		headerView.setPadding(0, -headerViewHeight+1, 0, 0);
		addHeaderView(headerView);
		initAnimation();
	}

	/**
	 * 隐藏脚布局
	 */
	public void hideFooterView() {
		removeFooterView(footerView);
		isLoadingMore = false;
	}
	
	public void showHeaderView(){
		headerView.setPadding(0, 0, 0, 0);
		ivArrow.setVisibility(View.INVISIBLE);
		ivArrow.clearAnimation(); // 清除imageView身上的动画
		mProgressBar.setVisibility(View.VISIBLE);
		currentHeaderState = REFRESHING;
	}
}




