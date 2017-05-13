package mybaby.util;



import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

import java.util.List;

import mybaby.ui.MyBabyApp;

public class AppUIUtils {
    public static int statusBarHeight(){
    	return (int) (25*MyBabyApp.density);//状态栏
    }
    /**
     * 判断应用是否在后台
     * @param context
     * @return
     */
	public static boolean isBackground(Context context) {
		try {
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
			for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(context.getPackageName())) {
                    if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                        Log.i("后台", appProcess.processName);
                        return true;
                    }else{
                        Log.i("前台", appProcess.processName);
                        return false;
                    }
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
    public static void killBackgroundPage(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        //List<AppTask> list= am.getAppTasks();
       /* if (!tasks.isEmpty()) {
        	for (int i = 0; i < tasks.size(); i++) {
    			if (!tasks.get(i).topActivity.getPackageName().equals(MyBayMainActivity.activity.getPackageName())) {
    				tasks.get(i).describeContents();
    			}else {
    				Log.e("", "主页无需干掉");
    			}
    		}
		}
        if (!tasks.isEmpty()) {
        	for (int i = 0; i < tasks.size(); i++) {
        		ComponentName topActivity = tasks.get(i).topActivity;
        		if (topActivity.getPackageName().equals(MyBayMainActivity.activity.getPackageName())) {
                    
                }else {
                	DetailsActivity.activityDetail.finish();
    			}
			}
        	}*/
        }
    
    public static void slideview(final View view,
    							final float fromXDelta, final float toXDelta, final float fromYDelta, final float toYDelta,
    							long durationMillis,long delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(durationMillis);
        animation.setStartOffset(delayMillis);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                int left = view.getLeft()+(int)(toXDelta-fromXDelta);
                int top = view.getTop()+(int)(toYDelta-fromYDelta);
                int width = view.getWidth();
                int height = view.getHeight();
                view.clearAnimation();
                view.layout(left, top, left+width, top+height);
            }
        });
        view.startAnimation(animation);
    }

    
    public static Context getContext() {
		return MyBabyApp.getContext();
	}


	/**
	 * dip转换px
	 * @param dip 
	 * @return px
	 */
	public static int dip2px(int dip) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	/**
	 * px转换dip
	 * @param px
	 * @return dip
	 */
	public static int px2dip(int px) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	public static View inflate(int resId){
		return LayoutInflater.from(getContext()).inflate(resId,null);
	}

	/** 获取资源 */
	public static Resources getResources() {
		return getContext().getResources();
	}

	/** 获取文字 */
	public static String getString(int resId) {
		return getResources().getString(resId);
	}

	/** 获取文字数组 */
	public static String[] getStringArray(int resId) {
		return getResources().getStringArray(resId);
	}

	/** 获取dimen */
	public static int getDimens(int resId) {
		return getResources().getDimensionPixelSize(resId);
	}

	/** 获取drawable */
	public static Drawable getDrawable(int resId) {
		return getResources().getDrawable(resId);
	}

	/** 获取颜色 */
	public static int getColor(int resId) {
		return getResources().getColor(resId);
	}

	/** 获取颜色选择器 */
	public static ColorStateList getColorStateList(int resId) {
		return getResources().getColorStateList(resId);
	}
	



}
