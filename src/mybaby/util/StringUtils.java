package mybaby.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mybaby.ui.MyBabyApp;


public class StringUtils {
    /**
     * 获得渠道名
     * @return
     */
    public static String getChanel(){
        ApplicationInfo appInfo = null;
        try {
            appInfo = MyBabyApp.getContext().getPackageManager()
                    .getApplicationInfo(MyBabyApp.getContext().getPackageName(),
                            PackageManager.GET_META_DATA);
            String msg=appInfo.metaData.getString("UMENG_CHANNEL");
            Log.i("chanel name:", " msg == " + msg);
            return msg;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

    }


    public static boolean isEmpty(String str){
        return str == null || str.trim().length() == 0;
    }
    
    public static boolean isValidEmail(String emailString)
    {
   	 	String regEx ="^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

        Matcher matcherObj = Pattern.compile(regEx).matcher(emailString);

        return matcherObj.matches();
    }
	
	
    public static String[] mergeStringArrays(String array1[], String array2[]) {
        if (array1 == null || array1.length == 0)
            return array2;
        if (array2 == null || array2.length == 0)
            return array1;
        List<String> array1List = Arrays.asList(array1);
        List<String> array2List = Arrays.asList(array2);
        List<String> result = new ArrayList<String>(array1List);
        List<String> tmp = new ArrayList<String>(array1List);
        tmp.retainAll(array2List);
        result.addAll(array2List);
        return result.toArray(new String[result.size()]);
    }

    public static String convertHTMLTagsForUpload(String source) {

        // bold
        source = source.replace("<b>", "<strong>");
        source = source.replace("</b>", "</strong>");

        // italics
        source = source.replace("<i>", "<em>");
        source = source.replace("</i>", "</em>");

        return source;

    }

    public static String convertHTMLTagsForDisplay(String source) {

        // bold
        source = source.replace("<strong>", "<b>");
        source = source.replace("</strong>", "</b>");

        // italics
        source = source.replace("<em>", "<i>");
        source = source.replace("</em>", "</i>");

        return source;

    }

    public static String addPTags(String source) {
        String[] asploded = source.split("\n\n");
        String wrappedHTML = "";
        if (asploded.length > 0) {
            for (int i = 0; i < asploded.length; i++) {
                if (asploded[i].trim().length() > 0)
                    wrappedHTML += "<p>" + asploded[i].trim() + "</p>";
            }
        } else {
            wrappedHTML = source;
        }
        wrappedHTML = wrappedHTML.replace("<br />", "<br>").replace("<br/>", "<br>");
        wrappedHTML = wrappedHTML.replace("<br>\n", "<br>").replace("\n", "<br>");
        return wrappedHTML;
    }
    
    public static String getMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while (md5.length() < 32)
                md5 = "0" + md5;

            return md5;
        } catch (NoSuchAlgorithmException e) {
            Log.e("MD5", e.getLocalizedMessage());
            return null;
        }
    }

    public static String unescapeHTML(String html) {
        if (html != null)
            return Html.fromHtml(html).toString();
        else
            return "";
    }

    // Wrap an image URL in a photon URL
    // Check out http://developer.wordpress.com/docs/photon/
    public static String getPhotonUrl(String imageUrl, int size) {
        imageUrl = imageUrl.replace("http://", "").replace("https://", "");
        return "http://i0.wp.com/" + imageUrl + "?w=" + size;
    }

    public static String getHost(String url) {
        if (TextUtils.isEmpty(url))
            return "";

        int doubleslash = url.indexOf("//");
        if (doubleslash == -1)
            doubleslash = 0;
        else
            doubleslash += 2;

        int end = url.indexOf('/', doubleslash);
        end = (end >= 0) ? end : url.length();

        return url.substring(doubleslash, end);
    }

    /*
     * returns empty string if passed string is null, otherwise returns passed string
     */
    public static String notNullStr(String s) {
        if (s==null)
            return "";
        return s;
    }
    
    public static String getFromArray(String[] arr,String split){
    	String result="";
    	for(int i=0;i<arr.length;i++){
    		result += arr[i];
    		if(i<arr.length-1){
    			result += split;
    		}
    	}
    	return result;
    }
    
    public static String getFromArray(ArrayList<String> arr,String split){
    	String result="";
    	for(int i=0;i<arr.size();i++){
    		result += arr.get(i);
    		if(i<arr.size()-1){
    			result += split;
    		}
    	}
    	return result;
    }

}
