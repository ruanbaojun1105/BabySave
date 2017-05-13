package mybaby.ui.community.customclass;

import java.text.SimpleDateFormat;

import mybaby.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import me.hibb.mybaby.android.R;
import mybaby.ui.community.DetailsActivity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 自定义拉下刷新ListView，常规箭头旋转，系统进度条，无回弹动画
 * @author 
 * @ClassInfo mybaby.ui.community.adapter.MyCusListView.java
 * @Description
 */
public class MyCusListView extends ListView implements OnScrollListener {
	private static final String TAG = "MyCusListView===>";
	/** 操作状态:下拉刚开始、回退到顶、一次刷新结束 */
	private static final int DONE = 0x1;
	/** 操作状态:松开即可刷新 */
	private final static int RELEASE_TO_REFRESH = 0x2;
	/** 操作状态:下拉可以刷新 */
	private final static int PULL_TO_REFRESH = 0x3;
	/** 操作状态:正在刷新 */
	private final static int REFRESHING = 0x4;
	/** 自定义ListView头布局 */
	private LinearLayout headView;
	//private LinearLayout footerView;
	/** 刷新提示文本 */
	//private TextView txtHeadTip;
	/** 最近刷新时间文本 */
	//private TextView txtLastRefresh;
	/** 下拉箭头图标 */
	private ImageView imgRefreshArrow;
	/** 刷新进度条图标 */
	private ProgressBar pbRefreshRound;
	/** headView宽 */
	private int headContentWidth;
	/** headView高 */
	private int headContentHeight;
	/** headView高 */
	//private int footerHeight;
	/** 下拉时箭头旋转动画 */
	private Animation pullAnim;
	/** 取消时箭头旋转动画 */
	private Animation reserveAnim;
	/** 标识各种刷新状态 */
	private int refreshState;
	/** 首次触摸屏幕设为true,松手时设为false,控制一次触摸事件的记录状态 */
	private boolean isRecored = false;
	/** 手指首次触摸屏幕时Y位置 */
	private int startY;
	/** 手指移动的距离和headView的padding距离的比例,防止移动时headView下拉过长 */
	private final static float RATIO = 1.5f;
	/** 表示已经下拉到可以刷新状态,可以拉回 */
	private boolean isBack = false;
	/** 刷新监听回调接口 */
	private OnRefreshListener refreshListener;
	/** 最后一行开始刷新监听回调接口 */
	private OnRefreshLastListener refreshLastListener;
	/** 列表在屏幕顶端第一个完整可见项的position */
	private int firstItemIndex;
	private boolean needRefushLister = true;
	
	private ImageLoader imageLoader;
	private boolean pauseOnScroll;
	private boolean pauseOnFling;
	
	public MyCusListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 快速滑动不加载图片
	 * @param pauseOnFling
	 */
	public void setTaskHandler(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
		this.imageLoader = imageLoader;
		this.pauseOnScroll = pauseOnScroll;
		this.pauseOnFling = pauseOnFling;
	}
	public static int getScrollY(AbsListView mListView) {
		View c = mListView.getChildAt(0);
		if (c == null) {
			return 0;
		}
		int firstVisiblePosition = mListView.getFirstVisiblePosition();
		int top = c.getTop();
		return -top + firstVisiblePosition * c.getHeight() ;
	}
	public void setNeedHead(boolean needRefushLister) {
		this.needRefushLister=needRefushLister;
	}
	
	/** 初始化 */
	private void init(Context context) {
		Log.i(TAG, "init()...");
		// 获取自定义头view
		headView = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.head_cus_listview, null);
		// 获取headView中控件
		imgRefreshArrow = (ImageView) headView
				.findViewById(R.id.imgRefreshArrow);
		pbRefreshRound = (ProgressBar) headView
				.findViewById(R.id.pbRefreshRound);
		//txtHeadTip = (TextView) headView.findViewById(R.id.txtHeadTip);
		//txtLastRefresh = (TextView) headView.findViewById(R.id.txtLastRefresh);
		// 预估headView宽高
		measureView(headView);
		// 获取headView宽高
		headContentWidth = headView.getMeasuredWidth();
		headContentHeight = headView.getMeasuredHeight();
		Log.i(TAG, "headView宽:[" + headContentWidth + "],高:["
				+ headContentHeight + "]");
		// 设置headView的padding值,topPadding为其本身的负值,达到在屏幕中隐藏的效果
		headView.setPadding(0, -headContentHeight, 0, 0);
		// 重绘headView
		headView.invalidate();
		// 将headView添加到自定义的ListView头部
		this.addHeaderView(headView, null, false);
		
		//footerView= (LinearLayout)LayoutInflater.from(context).inflate(
		//		R.layout.activity_driver, null);
		//this.addFooterView(footerView);
		// 设置ListView的滑动监听
		this.setOnScrollListener(this);
		// 获取箭头旋转动画
		pullAnim = AnimationUtils.loadAnimation(context, R.anim.list_rotate_reverse);
		reserveAnim = AnimationUtils.loadAnimation(context,R.anim.list_rotate);
		refreshState=DONE;
		
	}

	/**
	 * 开始刷新
	 */
	public void starRefush() {
		refreshState = REFRESHING;
		// 保持headView在屏幕顶端显示
		headView.setPadding(0, 0, 0, 0);
		// 显示出进度条
		pbRefreshRound.setVisibility(View.VISIBLE);
		// 隐藏箭头图标
		imgRefreshArrow.clearAnimation();
		imgRefreshArrow.setVisibility(View.GONE);
		//txtHeadTip.setText("加载中...");
		Log.i(TAG, "当前状态:REFRESHING");
		// 初始刷新状态
		onRefreshing();
	}
	/**
	 * 开始刷新
	 */
	public void star() {
		refreshState = REFRESHING;
		// 保持headView在屏幕顶端显示
		headView.setPadding(0, 0, 0, 0);
		// 显示出进度条
		pbRefreshRound.setVisibility(View.VISIBLE);
		// 隐藏箭头图标
		imgRefreshArrow.clearAnimation();
		imgRefreshArrow.setVisibility(View.GONE);
		//txtHeadTip.setText("加载中...");
		Log.i(TAG, "当前状态:REFRESHING");
		// 初始刷新状态
	}
	/**
	 * 预估headView的宽高
	 * 
	 * @param child
	 */
	public static void measureView(View child) {
		Log.i(TAG, "measureView()...");
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {    
		 // 当不滚动时    
		 case OnScrollListener.SCROLL_STATE_IDLE: 
			 
			 // 判断滚动到底部   
			 if (view.getLastVisiblePosition() == (view.getCount() - 1)||view.getLastVisiblePosition() == (view.getCount() - 3)) { 
				//Toast.makeText(getContext(), "滑到底部", 0).show();
				try {
					if (refreshLastListener!=null) {
						refreshLastListener.toRefreshLast();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Utils.Loge("bad:last refush Error!");
				}
			 }    // 判断滚动到顶部    
			 if(view.getFirstVisiblePosition() == 0){   
				 //Toast.makeText(getContext(), "顶部", 0).show();
			 }  
			 if (imageLoader!=null) {
				imageLoader.resume();
			}
			 
			 break;   
		 case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://普通滑动
			 if (imageLoader!=null) {
				  if (pauseOnScroll) {
					imageLoader.pause();
				}
			 }
             break;
         case OnScrollListener.SCROLL_STATE_FLING://快速滑动
        	 if (imageLoader!=null) {
        		 if (pauseOnFling) {
					imageLoader.pause();
				}
        	 }
             break;
			 } 
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 记录滚动时列表第一个完整可见项的position
		firstItemIndex = firstVisibleItem;
	}

	/** 监听触摸事件 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:// 第一次触摸时
			if (firstItemIndex == 0) {
				// 开始记录
				isRecored = true;
				// 获取首次Y位置
				startY = (int) ev.getY();
				//Log.i(TAG, "从首次触摸点就开始记录...");
			} else {
				//Log.i(TAG, "首次触摸时firstItemIndex不为0,不执行下拉刷新");
			}
			//Log.i(TAG, "记录状态isRecored:" + isRecored);
			break;
		case MotionEvent.ACTION_UP:// 松开屏幕时
			// 移除记录
			isRecored = false;
			InputMethodManager imm = (InputMethodManager) getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (DetailsActivity.editReply!=null) {
				imm.hideSoftInputFromWindow(DetailsActivity.editReply.getWindowToken(), 0 );
			}
			
			//Log.i(TAG, "停止记录..." + ",isRecored:" + isRecored);
			if (refreshState == PULL_TO_REFRESH) {
				refreshState = DONE;
				changeHeadView();
				//Log.i(TAG, "PULL_TO_REFRESH状态松手,回到原始状态");
			} else if (refreshState == RELEASE_TO_REFRESH) {
				refreshState = REFRESHING;
				changeHeadView();
				onRefreshing();
				//Log.i(TAG, "RELEASE_TO_REFRESH状态松手,进入REFRESHING状态");
			} else if (refreshState == REFRESHING) {
				if (firstItemIndex == 0) {
					// 保持刷新状态
					headView.setPadding(0, 0, 0, 0);
					//Log.i(TAG, "REFRESHING状态松手,保持该状态,headView仍在顶部");
				} else {
					//Log.i(TAG, "REFRESHING状态松手,保持该状态,headView被推出顶部");
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:// 手势移动时
			// 记录实时的手指移动时在屏幕的Y位置,用于和startY比较
			int curY = (int) ev.getY();

			if (!isRecored && firstItemIndex == 0) {
				isRecored = true;
				//Log.i(TAG, "从移动状态执行下拉刷新,开始记录..." + ",isRecored:" + isRecored);
				startY = curY;
			}

			if (isRecored) {
				// 开始或结束状态
				if (refreshState == DONE) {
					if (curY - startY > 0) {// 表示向下拉了
						// 状态改为下拉刷新
						refreshState = PULL_TO_REFRESH;
						changeHeadView();
					}
				}

				// 下拉刷新状态
				if (refreshState == PULL_TO_REFRESH) {
					setSelection(0);
					// 不断改变headView的高度
					headView.setPadding(0, (int) ((curY - startY) / RATIO
							- headContentHeight), 0, 0);
					// 下拉到RELEASE_TO_REFRESH状态
					if ((curY - startY) / RATIO >= headContentHeight * 1.1) {
						refreshState = RELEASE_TO_REFRESH;
						isBack = true;
						changeHeadView();
					} else if ((curY - startY) <= 0) {
						// 上推到顶
						refreshState = DONE;
						changeHeadView();
					}
				}

				// 松手可以刷新状态
				if (refreshState == RELEASE_TO_REFRESH) {
					setSelection(0);
					// 不断改变headView的高度
					headView.setPadding(0, (int) ((curY - startY) / RATIO
							- headContentHeight), 0, 0);
					// 又往上推
					if ((curY - startY) / RATIO < headContentHeight* 1.8) {
						refreshState = PULL_TO_REFRESH;
						changeHeadView();
					}
				}

				// 正在刷新状态
				if (refreshState == REFRESHING) {
					if (curY - startY > 0) {
						// 只改变padding值,不做其余处理
						headView.setPadding(0, (int) ((curY - startY) / RATIO), 0, 0);
					}
				}
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	
	/** 进入刷新的方法 */
	public void onRefreshing() {
		// 调用回调接口中的刷新方法
		if (refreshListener != null) {
			refreshListener.toRefresh();
			if (!needRefushLister) {
				onRefreshFinished();
			}
		}
	}

	
	/** 使用界面传递给此ListView的回调接口,用于两者间通信 */
	public interface OnRefreshListener {
		void toRefresh();
	}
	/** 使用界面传递给此ListView的回调接口,用于两者间通信 */
	public interface OnRefreshLastListener {
		
		void toRefreshLast();
	}

	/**
	 * 注册一个用于最后一行开始刷新的回调接口
	 * 
	 * @param refreshListener
	 */
	public void setOnRefreshLastListener(OnRefreshLastListener refreshLastListener) {
		// 获取传递过来的回调接口
		this.refreshLastListener = refreshLastListener;
	}
	/**
	 * 注册一个用于刷新的回调接口
	 * 
	 * @param refreshListener
	 */
	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		// 获取传递过来的回调接口
		this.refreshListener = refreshListener;
	}

	/** 使用界面执行完刷新操作时调用此方法 */
	public void onRefreshFinished() {
		refreshState = DONE;
		changeHeadView();
		// 显示最近更新
		//txtLastRefresh.setText("更新于:" + myFormatter.format(new Date()));
	}

	/** 根据下拉的状态改变headView */
	private void changeHeadView() {
		switch (refreshState) {
		case DONE:// 开始或结束状态
			Log.i(TAG, "当前状态:DONE");
			// 回退状态清除
			isBack = false;
			// 回复原始高度
			try {
				headView.setPadding(0, -headContentHeight, 0, 0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 进度条隐藏
			pbRefreshRound.setVisibility(View.GONE);
			// 设置原始箭头图片
			imgRefreshArrow.setImageResource(R.drawable.arrow);
			imgRefreshArrow.setVisibility(View.VISIBLE);
			//txtHeadTip.setText("下拉刷新...");
			break;
		case PULL_TO_REFRESH:// 下拉刷新状态
			//Log.i(TAG, "当前状态:PULL_TO_REFRESH");
			// 从RELEASE_TO_REFRESH回到PULL_TO_REFRESH状态
			//Log.i(TAG, "是否从松开刷新回到下拉刷新...isBack:" + isBack);
			if (isBack) {
				// 设置箭头回转动画
				imgRefreshArrow.startAnimation(reserveAnim);
			}
			//txtHeadTip.setText("下拉刷新...");
			break;
		case RELEASE_TO_REFRESH:
			Log.i(TAG, "当前状态:RELEASE_TO_REFRESH");
			// 设置箭头旋转动画
			imgRefreshArrow.startAnimation(pullAnim);
			//txtHeadTip.setText("松开刷新...");
			break;
		case REFRESHING:
			// 保持headView在屏幕顶端显示
			headView.setPadding(0, 0, 0, 0);
			// 显示出进度条
			pbRefreshRound.setVisibility(View.VISIBLE);
			// 隐藏箭头图标
			imgRefreshArrow.clearAnimation();
			imgRefreshArrow.setVisibility(View.GONE);
			//txtHeadTip.setText("正在刷新...");
			Log.i(TAG, "当前状态:REFRESHING");
			break;
		}
	}
	SimpleDateFormat myFormatter = new SimpleDateFormat("MM-dd HH:mm:ss");
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		//txtLastRefresh.setText("更新于:" + myFormatter.format(new Date()));
	}
}
