package mybaby.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Created by nbradbury on 11/4/13.
 * wrappers for extracting values from a Map object
 */
public class MapUtils {
	
    /*
     * returns a String value for the passed key in the passed map
     * always returns "" instead of null
     */
    public static String getMapStr(final Map<?, ?> map, final String key) {
        if (map==null || key==null || !map.containsKey(key))
            return "";
        return map.get(key).toString();
    }

    /*
     * returns an int value for the passed key in the passed map
     * defaultValue is returned if key doesn't exist or isn't a number
     */
    public static int getMapInt(final Map<?, ?> map, final String key) {
        return getMapInt(map, key, 0);
    }
    public static int getMapInt(final Map<?, ?> map, final String key, int defaultValue) {
        try {
            return Integer.parseInt(getMapStr(map, key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /*
     * long version of above
     */
    public static long getMapLong(final Map<?, ?> map, final String key) {
        return getMapLong(map, key, 0);
    }
    public static long getMapLong(final Map<?, ?> map, final String key, long defaultValue) {
        try {
            return Long.parseLong(getMapStr(map, key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    
    /*
     * double version of above
     */
    public static double getMapDouble(final Map<?, ?> map, final String key) {
        return getMapDouble(map, key, 0);
    }
    public static double getMapDouble(final Map<?, ?> map, final String key, double defaultValue) {
        try {
            return Double.parseDouble(getMapStr(map, key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    

    /*
     * returns a date object from the passed key in the passed map
     * returns null if key doesn't exist or isn't a date
     */
    public static Date getMapDate(final Map<?, ?> map, final String key) {
        if (map==null || key==null || !map.containsKey(key))
            return null;
        try {
            return (Date) map.get(key);
        } catch (ClassCastException e) {
            return null;
        }
    }

    
    public static Date getMapDateFrom_yyyyMMdd(final Map<?, ?> map, final String key) {
        if (map==null || key==null || !map.containsKey(key))
            return null;
        try {
        	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(map.get(key).toString());
        } catch (ParseException e) {
            return null;
        } catch (ClassCastException e) {
            return null;
        }
    }
    
    public static long getMapDateLong(final Map<?, ?> map, final String key) {
        Date date=getMapDate(map,key);
        if(date != null){
        	return date.getTime();
        }else{
        	return 0;
        }
    }

    public static long getMapDateLongFrom_yyyyMMdd(final Map<?, ?> map, final String key) {
        Date date=getMapDateFrom_yyyyMMdd(map,key);
        if(date != null){
        	return date.getTime();
        }else{
        	return 0;
        }
    }
    
    /*
     * returns a boolean value from the passed key in the passed map
     * returns true unless key doesn't exist, or the value is "0" or "false"
     */
    public static boolean getMapBool(final Map<?, ?> map, final String key) {
        String value = getMapStr(map, key);
        if (value.isEmpty())
            return false;
        if (value.startsWith("0")) // handles "0" and "0.0"
            return false;
        if (value.equalsIgnoreCase("false"))
            return false;
        // all other values are assume to be true
        return true;
    }
    
    public static Map<?, ?> getMap(final Map<?, ?> map, final String key) {
        if (map==null || key==null || !map.containsKey(key))
            return null;
        if (map.get(key) instanceof Map<?, ?>)
            return (Map<?, ?>)map.get(key);
        return null;
    }
    
    public static Object[] getArray(final Map<?, ?> map, final String key) {
        if (map==null || key==null || !map.containsKey(key))
            return null;
        if (map.get(key) instanceof Object[])
            return (Object[])map.get(key);
        return null;
    }
}
