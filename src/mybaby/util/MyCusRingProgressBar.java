package mybaby.util;

import me.hibb.mybaby.android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * 自定义环形进度条
 * @author 
 * @Date 2014-4-28
 * @ClassInfo com.zf.pulldown_listview.cusview-MyCusRingProgressBar.java
 * @Description
 */
public class MyCusRingProgressBar extends View {
	/** 圆环画笔 */
	private Paint ringPaint;
	/** 圆环半径 */
	private int ringRadius;
	/** 圆环宽度 */
	private int strokeWidth = 2;
	/** 总进度 */
	private int tolProgress = 100;
	/** 当前进度 */
	private int curProgress;
	/** 所画的圆形区域 */
	private RectF oval;
	/** 圆心坐标 */
	private int center;
	/** 旋转动画 */
	private Animation cycleAnim;

	public MyCusRingProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取旋转动画
		cycleAnim = AnimationUtils.loadAnimation(context,
				R.anim.cus_progress_rotate);
		// 设置圆环画笔
		ringPaint = new Paint();
		ringPaint.setStyle(Paint.Style.STROKE);
		ringPaint.setStrokeWidth(strokeWidth);
		ringPaint.setAntiAlias(true);
		ringPaint.setColor(getResources().getColor(R.color.red));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		center = getWidth() / 2;
		ringRadius = center - strokeWidth / 2;

		if (curProgress > 0) {
			if (oval == null) {
				oval = new RectF();
				oval.left = center - ringRadius;
				oval.top = center - ringRadius;
				oval.right = center + ringRadius;
				oval.bottom = center + ringRadius;
			}
			// 从0度开始画，根据当前进度占总进度的百分比来画角度增量
			canvas.drawArc(oval, 0, ((float) curProgress / tolProgress) * 360,
					false, ringPaint);
		}
	}

	/**设置当前进度*/
	public void setProgress(int progress) {
		curProgress = progress;
		postInvalidate();
	}

	/**
	 * 开始旋转动画
	 * <ul>
	 * <strong>在XML中设置layout_width和layout_height数值时，旋转可能存在问题，未解决！</strong>
	 * </ul>
	 */
	public void startCycleAnim() {
		this.startAnimation(cycleAnim);
	}

	/**停止旋转动画*/
	public void stopCycleAnim() {
		this.clearAnimation();
	}
}
