package mybaby.util;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import mybaby.ui.MyBabyApp;

public class DateUtils {
	public String TimeStamp2Date(String timestampString, String formats){
		Long timestamp = Long.parseLong(timestampString)*1000;
		String date = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));
		return date;
	}
	public static long gmtTime2LocalTime(long gmtTime){
    	TimeZone tz=TimeZone.getDefault();
    	return gmtTime+tz.getRawOffset();
    }
    
    public static long localTime2GMTTime(long localTime){
    	TimeZone tz=TimeZone.getDefault();
    	return localTime-tz.getRawOffset();
    }
    
    public static String date2DisplayString(long date){
    	return DateFormat.getDateFormat(MyBabyApp.getContext()).format(date);
    }
    
    public static String datetime2DisplayString(long datetime){
    	return DateFormat.getDateFormat(MyBabyApp.getContext()).format(datetime) 
    			+ " " + DateFormat.getTimeFormat(MyBabyApp.getContext()).format(datetime);
    }
    
    /*public static long lngDatetime2Date(long lngDatetime){
    	try {
			return MyBabyApp.sdf_yyyyMMdd.parse(new SimpleDateFormat("yyyy-MM-dd").format(lngDatetime)).getTime();
		} catch (ParseException e) {
			return lngDatetime;
		}
    }*/
	public static long lngDatetime2Date(long lngDatetime){
		try {
			return MyBabyApp.sdf_yyyyMMdd.parse(MyBabyApp.sdf_yyyyMMdd.format(lngDatetime)).getTime();
		} catch (ParseException e) {
			return lngDatetime;
		}
	}
    
    public static boolean isSameDate(long lngDate1,long lngDate2){
    	return MyBabyApp.sdf_yyyyMMdd.format(lngDate1).equals(MyBabyApp.sdf_yyyyMMdd.format(lngDate2));
    }
	/**
	 * 获取两个日期之间的间隔天数
	 * @return
	 */
	public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();  
        fromCalendar.setTime(startDate);  
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        fromCalendar.set(Calendar.MINUTE, 0);  
        fromCalendar.set(Calendar.SECOND, 0);  
        fromCalendar.set(Calendar.MILLISECOND, 0);  
  
        Calendar toCalendar = Calendar.getInstance();  
        toCalendar.setTime(endDate);  
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        toCalendar.set(Calendar.MINUTE, 0);  
        toCalendar.set(Calendar.SECOND, 0);  
        toCalendar.set(Calendar.MILLISECOND, 0);  
  
        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
	}
	
	
	public static String showDate(long startDate) {
		return getCountnumber(startDate);
	}
	
	/**
	 * 获取两个日期之间的时间差
	 * @return 
	 */
	public static String getCountnumber(long startDate) {
		int tempTime = (int) ((System.currentTimeMillis() - startDate)/1000);
		String dateText = "";
		if(tempTime < 60){
			dateText = "刚刚";
		}else if(tempTime > 60 && tempTime < 60*60){
			int time = tempTime/60;
			dateText = time+"分钟前";
		}else if(tempTime >60*60 && tempTime < 60*60*24){
			int time = tempTime/(60*60);
			dateText = time+"小时前";
		}else {
			//TODO 超过一天 将date设置为具体时间
			SimpleDateFormat sdf = new SimpleDateFormat(isThisYear(startDate)?"MM-dd":"yyyy-MM-dd");
			dateText = sdf.format(new Date(startDate));
		}
        return dateText;
	}
	//判断选择的日期是否是本年
	private static boolean isThisYear(long time) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String param = sdf.format(date);//参数时间
		String now = sdf.format(new Date());//当前时间
		if (param.equals(now)) {
			return true;
		}
		return false;
	}

}
