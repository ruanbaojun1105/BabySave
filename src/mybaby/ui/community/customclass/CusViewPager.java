package mybaby.ui.community.customclass;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CusViewPager extends ViewPager{

	public CusViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public CusViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		}
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(arg0);
	}
	@Override
	protected void onDraw(Canvas arg0) {
		// TODO Auto-generated method stub
		super.onDraw(arg0);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		/*int action=arg0.getAction();
		if (action==MotionEvent.ACTION_UP) {
			
			return true;
		}*/
		
		return super.onInterceptTouchEvent(arg0);
	}
}
