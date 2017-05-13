package mybaby.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.hibb.mybaby.android.BuildConfig;
import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;

public class Utils {

    public static void sendMessage(int what,Handler handler){
        sendMessage(what,null,handler);
    }
    public static void sendMessage(int what,Object obj,Handler handler){
        if (handler==null)
            return;
        Message msg = Message.obtain();
        msg.what = what;
        if (obj!=null)
            msg.obj = obj;
        handler.sendMessage(msg);
    }
	public static boolean isMobileNO(String mobiles) {
		if (TextUtils.isEmpty(mobiles)) {
			return false;
		}
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
		}
    // logic below based on login in WPActionBarActivity.java
    public static boolean isXLarge(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
    
    public static boolean isLandscape(Context context) {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    public static boolean isTablet() {
        return MyBabyApp.getContext().getResources().getInteger(R.integer.isTablet) == 1;
    }
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(spValue * fontScale + 0.5F);
    }
    public static float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, MyBabyApp.getContext().getResources().getDisplayMetrics());
    }
    
    public static float spToPx(float sp) {
        float scaledDensity = MyBabyApp.getContext().getResources().getDisplayMetrics().scaledDensity;
        return sp * scaledDensity;
   }
    
    public static int getSmallestWidthDP() {
        return MyBabyApp.getContext().getResources().getInteger(R.integer.smallest_width_dp);
    }
    
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MyBabyApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    

    

    public static void threadToast(final Context context,final int resId){
    	threadToast(context,context.getString(resId));
    }
    
    public static void threadToast(final Context context,final String message){
         Looper.prepare();
         Toast.makeText(context, message ,Toast.LENGTH_SHORT).show();
         Looper.loop();
    }

    public static int getAppVersion() {
        try {
            PackageInfo packageInfo = MyBabyApp.getContext().getPackageManager()
                    .getPackageInfo(MyBabyApp.getContext().getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            Log.v("MyBaby","getAppVersion: Could not get package name: " + e);
            return 0;
        }
    }
    
    public static void LogV(String tag,String msg){
    	if(BuildConfig.DEBUG){
    		Log.v(tag, msg);
    	}
    }
    
    public static void LogI(String tag,String msg){
    	if(BuildConfig.DEBUG){
    		Log.i(tag, msg);
    	}
    }
    public static void Loge(String msg){
    	if(BuildConfig.DEBUG){
    		Log.i("", msg);
    	}
    }
    
  /*  public static boolean isChina(){
    	if(Locale.getDefault().getCountry().toLowerCase().equals("cn")){
    		return true;
    	}else{
    		return false;
    	}
    }*/


}


