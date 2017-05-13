package mybaby.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * 获得屏幕相关的辅助类
 * @author asus1
 *
 */
public class ScreenUtils {

	public ScreenUtils(){
		//throw new UnsupportedOperationException("cannot be instantiated");
	}
	
	/**
	 * 获得屏幕宽度
	 * @param context 上下文
	 * @return 屏幕宽度
	 */
	public static int getScreenWidth(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}
	
	/**
	 * 获得屏幕高度
	 * @param context 上下文
	 * @return 屏幕高度
	 */
	public static int getScreenHeight(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}
	
	/**
	 * 获得状态栏的高度
	 * @param context 上下文
	 * @return 状态栏的高度
	 */
	public static int getStatusHeight(Context context){
		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return statusHeight;
	}
	
	/**
	 * 获取当前屏幕截图，包含状态栏
	 * @param activity 当前视图
	 * @return 当前屏幕截图，包含状态栏的Bitmap
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity){
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;
	}
	
	/**
	 * 获取当前屏幕截图，不包含状态栏
	 * @param activity 当前视图
	 * @return 当前屏幕截图，不包含状态栏的Bitmap
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity){
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		
		return bp;
	}
	
	
		 /**
	         * 将px值转换为dip或dp值，保证尺寸大小不变
	         * 
	         * @param pxValue
	         * @param scale
	         *            （DisplayMetrics类中属性density）
	         * @return
	         */ 
	        public static int px2dip(Context context, float pxValue) { 
	            final float scale = context.getResources().getDisplayMetrics().density; 
	            return (int) (pxValue / scale + 0.5f); 
	        } 
	       
	        /**
	         * 将dip或dp值转换为px值，保证尺寸大小不变
	         * 
	         * @param dipValue
	         * @param scale
	         *            （DisplayMetrics类中属性density）
	         * @return
	         */ 
	        public static int dip2px(Context context, float dipValue) { 
	            final float scale = context.getResources().getDisplayMetrics().density; 
	            return (int) (dipValue * scale + 0.5f); 
	        }
	public static int dip2px(float scale, float dipValue) {
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	         * 将px值转换为sp值，保证文字大小不变
	         * 
	         * @param pxValue
	         * @param fontScale
	         *            （DisplayMetrics类中属性scaledDensity）
	         * @return
	         */ 
	        public static int px2sp(Context context, float pxValue) { 
	            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
	            return (int) (pxValue / fontScale + 0.5f); 
	        } 
	       
	        /**
	         * 将sp值转换为px值，保证文字大小不变
	         * 
	         * @param spValue
	         * @param fontScale
	         *            （DisplayMetrics类中属性scaledDensity）
	         * @return
	         */ 
	        public static int sp2px(Context context, float spValue) { 
	            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
	            return (int) (spValue * fontScale + 0.5f); 
	        }
}
