package mybaby.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class DoubleViewPager extends ViewPager implements OnGestureListener{

	/** 手势滑动处理类   **/
	private GestureDetector mDetector;
	
	public DoubleViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		GestureDetector detector = new GestureDetector(context, this);
		mDetector = detector;
	}
	
	public GestureDetector getGestureDetector() {
		return mDetector;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if(listener != null) {
			listener.setOnSimpleClickListenr(getCurrentItem());
		}
		return true;
	}
	
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		boolean b = false;
		try {
			b = super.onInterceptTouchEvent(arg0);
		} catch (Exception e) {

		}
		return b; //网上看的方法是直接返回false，但是会导致ViewPager翻页有BUG
	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}
	
	private onSimpleClickListener listener;

	/** 单击监听接口  **/
	public interface onSimpleClickListener {
		void setOnSimpleClickListenr(int position);
	}
	
	public void setOnSimpleClickListener(onSimpleClickListener listener) {
		this.listener = listener;
	}
}
