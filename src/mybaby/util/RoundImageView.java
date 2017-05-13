package mybaby.util;

//���ԣ�http://www.oschina.net/question/783094_132836

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * Բ��ImageView
 * 
 * @author skg
 * 
 * 
 */
public class RoundImageView extends ImageView {

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RoundImageView(Context context) {
		super(context);
		init();
	}
	public RoundImageView(Context context,float sideWidth,int sideColor) {
		super(context);
		this.sideWidth = sideWidth;
		this.varColor = sideColor;
		init();
	}

	private final RectF roundRect = new RectF();
	private float rect_adius = -1;
	private final Paint maskPaint = new Paint();
	private final Paint zonePaint = new Paint();
	float density;
	int varColor=0;
	float sideWidth=0;
	
	private void init() {
		maskPaint.setAntiAlias(true);
		maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		//
		zonePaint.setAntiAlias(true);
		zonePaint.setColor(varColor == 0 ? Color.WHITE : getResources().getColor(varColor));
		zonePaint.setStrokeWidth(sideWidth==0?2:sideWidth);

		//
		density = getResources().getDisplayMetrics().density;
		rect_adius = rect_adius * density;
	}

	public void setRectAdius(float adius) {
		rect_adius = adius;
		invalidate();
	}

	public void setRectColor(int varColor) {
		this.varColor = varColor;
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int w = getWidth();
		int h = getHeight();
		roundRect.set(0, 0, w, h);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
		if(rect_adius<=0){
			canvas.drawRoundRect(roundRect, getWidth()/2*density, getHeight()/2*density, zonePaint);
		}else{
			canvas.drawRoundRect(roundRect, rect_adius, rect_adius, zonePaint);
		}
		canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
		super.draw(canvas);
		canvas.restore();
	}

}




