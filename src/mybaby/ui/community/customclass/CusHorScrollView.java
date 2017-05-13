package mybaby.ui.community.customclass;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import mybaby.ui.community.holder.HtmlItem;

public class CusHorScrollView extends HorizontalScrollView{

	private HtmlItem.SetWebViewOnTouchListener listener;

	public CusHorScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public CusHorScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		}

	public void setListener(HtmlItem.SetWebViewOnTouchListener listener) {
		this.listener = listener;
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
	public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
		// TODO Auto-generated method stub
		float oldX=0;
		float oldY=0;
		int action = motionEvent.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				oldX=motionEvent.getX();
				oldY=motionEvent.getY();
				if (listener != null)
					listener.onLeftOrRight(true);
				break;
			case MotionEvent.ACTION_MOVE:
				if (listener != null) {
					float ax=Math.abs(motionEvent.getX() - oldX);
					if (ax<30){
						float ay=Math.abs(motionEvent.getY() - oldY);
						listener.onLeftOrRight(ay<50);
					}else listener.onLeftOrRight(true);
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if (listener != null)
					listener.onLeftOrRight(false);
				break;
		}
		return super.onInterceptTouchEvent(motionEvent);
	}
}
