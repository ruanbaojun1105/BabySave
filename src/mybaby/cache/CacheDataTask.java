package mybaby.cache;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.Serializable;
import java.util.Date;

import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.util.LogUtils;
import mybaby.util.Utils;

public class CacheDataTask {
	public static void putCache(Context context,String value,String TypeKey) {
		putCache(context, value, TypeKey, true);
	}
	public static void putCache(Context context,String value,String TypeKey,boolean remove) {
		if (value==null||context==null||context.getCacheDir()==null)
			return;
		ACache mCache = null;
		try {
			mCache = ACache.get(context);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		if (remove) {
			try {
				mCache.remove(TypeKey);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mCache.put(TypeKey,value);
		mCache.put(Constants.CacheKey_Version, MyBabyApp.version);
		mCache.put(TypeKey+Constants.CacheKey_LastTime, new Date());
		Utils.Loge(System.currentTimeMillis() + "加入一条缓存" + TypeKey);
	}


	public static void putCache(Context context,Serializable object,String TypeKey){
		putCache(context, object, TypeKey, true);
	}
	public static void putCache(Context context,Serializable object,String TypeKey,boolean remove) {
		if (object==null||context==null||context.getCacheDir()==null)
			return;
		ACache mCache = null;
		try {
			mCache = ACache.get(context);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		if (remove) {
			try {
				mCache.remove(TypeKey);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mCache.put(TypeKey, object);
		mCache.put(Constants.CacheKey_Version, MyBabyApp.version);
		//mCache.put(Constants.CacheKey_AddLastTime, new Date().toLocaleString(),2*ACache.TIME_HOUR/60);
		mCache.put(TypeKey+Constants.CacheKey_LastTime, new Date());

		Utils.Loge(new Date().toLocaleString() + "加入一条缓存" + TypeKey);
	}






	public static Object[] getObjs(Context context,String TypeKey) {
		if (context==null||context.getCacheDir()==null)
			return null;
		ACache mCache = null;
		try {
			mCache = ACache.get(context);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return (Object[]) mCache.getAsObject(TypeKey);
	}

	public static Object getObj(Context context,String TypeKey) {
		if (context==null||context.getCacheDir()==null)
			return null;
		ACache mCache = null;
		try {
			mCache = ACache.get(context);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return mCache.getAsObject(TypeKey);
	}
	/**
	 * 设置该条缓存时间过期
	 */
	public static void setCacheDateOverdue(Context context,String TypeKey) {
		ACache mCache = ACache.get(context);
		mCache.put(TypeKey+Constants.CacheKey_LastTime, new Date());
	}
	
	public static GenCache getValueCache(Context context,String TypeKey) {
		if (context==null||context.getCacheDir()==null)
			return null;
		ACache mCache = null;
		try {
			mCache = ACache.get(context);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return new GenCache(mCache.getAsString(Constants.CacheKey_Version),
				(Date)mCache.getAsObject(TypeKey+Constants.CacheKey_LastTime),
				mCache.getAsString(TypeKey)
				);
	}


	public static GenCaches getGenObjCache(Context context,String TypeKey) {
		if (context==null||context.getCacheDir()==null)
			return null;
		ACache mCache = null;
		try {
			mCache = ACache.get(context);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return new GenCaches(mCache.getAsObject(TypeKey),
				mCache.getAsString(Constants.CacheKey_Version),
				(Date)mCache.getAsObject(TypeKey+Constants.CacheKey_LastTime));
	}

	public static MainCache getMainCache(Context context,String TypeKey) {
		if (context==null||context.getCacheDir()==null)
			return null;
		ACache mCache = null;
		try {
			mCache = ACache.get(context);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		MainCache mainCache=null;
		if (mCache.getAsObject(TypeKey) instanceof MainCache)
			mainCache=(MainCache)mCache.getAsObject(TypeKey) ;
		return mainCache;
	}
/**
 * 
 * @param lastTime
 * @param intervaTime 间隔分钟
 * @return
 */
	public static boolean booleaRefush(long lastTime,int intervaTime) {
		Utils.Loge(getTwoDay(lastTime)+"");
		long time=getTwoDay(lastTime);
		LogUtils.e("与上次页面间隔时间："+time);
		return time>intervaTime*60*1000?true:false ;
	}
	public static boolean booleaRefush(long lastTime,long intervaTime) {
		Utils.Loge(getTwoDay(lastTime)+"");
		long time=getTwoDay(lastTime);
		LogUtils.e("与上次页面间隔时间："+time);
		return time>intervaTime?true:false ;
	}
	public static boolean booleaRefush(Date lastTime,int intervaTime) {
		if (lastTime==null)
			return true;
		return booleaRefush(lastTime.getTime(),intervaTime);
	}

	public  static long getTwoDay(long lastTime) {
		long day = 0;
		if (lastTime==0)
			return day;
		day=System.currentTimeMillis() - lastTime;
		return day;
	}
	//返回秒
	@SuppressLint("SimpleDateFormat") 
	public  static long getTwoDay(Date lastTime) {
		if (lastTime==null)
			return 0;
       return getTwoDay(lastTime.getTime());
 }
}
