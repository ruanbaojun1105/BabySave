//Manages data for blog settings
//

package mybaby.models;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import mybaby.ui.MyBabyApp;

import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.util.Base64;


public class Blog {
	public enum type{		
		CurrUser,
		OldUser
	}

	private int id;
	private String username;
	private String password;
	private boolean runService;
	private int blogTypeNumber;
	private int userId;
    
	public int getId(){
		return id;
	}
	public void  setId(int id){
		this.id = id;
	}
	public String getUsername(){
		return username;
	}
	public void  setUsername(String username){
	    this.username = username;
	}
	public String getPassword(){
		return password;
	}
	public void  setPassword(String password){
	    this.password = password;
	}
	public boolean getRunService(){
		return runService;
	}
	public void  setRunService(boolean runService){
	    this.runService = runService;
	}
	public int getBlogTypeNumber(){
		return blogTypeNumber;
	}
	public void  setBlogTypeNumber(int blogTypeNumber){
	    this.blogTypeNumber = blogTypeNumber;
	}
	public int getUserId(){
		return userId;
	}
	public void  setUserId(int userId){
	    this.userId = userId;
	}
	
    public Blog(Cursor c){
    	this.setId(c.getInt(0));
    	this.setUsername(c.getString(1));
    	this.setPassword(c.getString(2));
    	this.setRunService(c.getInt(3)>0);
    	this.setBlogTypeNumber(c.getInt(4));
    	this.setUserId(c.getInt(5));
    }	
	
    public Blog(	String username,
		    		String password,
		    		boolean runService,
		    		int blogTypeNumber,
		    		int userId) {
    	this.setUsername(username);
    	this.setPassword(encryptPassword(password));
    	this.setRunService(runService);
    	this.setBlogTypeNumber(blogTypeNumber);
    	this.setUserId(userId);
    }

	

	
	public boolean serverHasAccount(){
		return this.username != null && this.username.length() > 0;
	}
	
	//周边的距离，单位：米   2015-07-17
	public static int getNearbyDistance(){
		return  MyBabyApp.getSharedPreferences().getInt("nearbyDistance",3500);
	}
	public static void setNearbyDistance(int distance){
		Editor edit=MyBabyApp.getSharedPreferences().edit();
		edit.putInt("nearbyDistance", distance);
		edit.commit();
	}
	
	//普通地点选择的类型
	public static String getMapPlaceTypes(){
		return  MyBabyApp.getSharedPreferences().getString("mapPlaceTypes", "汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施");
	}
	public static void setMapPlaceTypes(String mapPlaceTypes){
		Editor edit=MyBabyApp.getSharedPreferences().edit();
		edit.putString("mapPlaceTypes", mapPlaceTypes);
		edit.commit();
	}
	
	//普通地点选择过滤keywords
	public static String getMapPlaceKeywords(){
		return  MyBabyApp.getSharedPreferences().getString("mapPlaceKeywords", "");
	}
	public static void setMapPlaceKeywords(String mapPlaceKeywords){
		Editor edit=MyBabyApp.getSharedPreferences().edit();
		edit.putString("mapPlaceKeywords", mapPlaceKeywords);
		edit.commit();
	}
	
	//普通地点排除正则表达式 如：xxx厕所';
	public static String getMapExcludeRegex(){
		return  MyBabyApp.getSharedPreferences().getString("mapExcludeRegex", "");
	}
	public static void setMapExcludeRegex(String mapExcludeRegex){
		Editor edit=MyBabyApp.getSharedPreferences().edit();
		edit.putString("mapExcludeRegex", mapExcludeRegex);
		edit.commit();
	}
	
	//新版本 更新使用（该属性为IOS中的属性，这里暂时放着待以后修改）
	public static int getNewVersion(){
		return MyBabyApp.getSharedPreferences().getInt("newVersion", 2);
	}
	public static void setNewVersion(int version){
		Editor edit=MyBabyApp.getSharedPreferences().edit();
		edit.putInt("newVersion", version);
		edit.commit();
	}
	
	//是否加入广告   0-不开启广告 1-符合条件的用户开启广告
	public static int getOpenAD(){
		return MyBabyApp.getSharedPreferences().getInt("openAD", 0);
	}
	public static void setOpenAD(int openAD){
		Editor edit=MyBabyApp.getSharedPreferences().edit();
		edit.putInt("openAD", openAD);
		edit.commit();
	}
	
	//0-不必要的用户不上传图片（未购买、未注册） 1-所有用户上传图片
	public static int getMediaUpload(){
		return MyBabyApp.getSharedPreferences().getInt("mediaUpload", 0);
	}
	public static void setMediaUpload(int mediaUpload){
		Editor edit=MyBabyApp.getSharedPreferences().edit();
		edit.putInt("mediaUpload", mediaUpload);
		edit.commit();
	}
	
	//照片书主题的最新版本号  必须为整数 下载目录按2.zip 3.zip ...n.zip 排列
	public static int getBookThemesVersion(){
		return MyBabyApp.getSharedPreferences().getInt("bookThemesVersion", 1);
	}
	public static void setBookThemesVersion(int bookThemesVersion){
		Editor edit=MyBabyApp.getSharedPreferences().edit();
		edit.putInt("bookThemesVersion", bookThemesVersion);
		edit.commit();
	}
	
	//表示bookThemesVersion是基于哪个版本的，前端根据 max(当前版本+1,bookThemesBaseVersion)->bookThemesVersion 按顺序循环下载
	public static int getBookThemesBaseVersion(){
		return MyBabyApp.getSharedPreferences().getInt("bookThemesBaseVersion", 1);
	}
	public static void setBookThemesBaseVersion(int bookThemesBaseVersion){
		Editor edit=MyBabyApp.getSharedPreferences().edit();
		edit.putInt("bookThemesBaseVersion", bookThemesBaseVersion);
		edit.commit();
	}

	//页面属性版本好
	public static void setWebPageAttVer(String webPageAttVer){
		Editor edit=MyBabyApp.getSharedPreferences().edit();
		edit.putString("webPageAttVer", webPageAttVer);
		edit.commit();
	}
	public static String getWebPageAttVer(){
		return MyBabyApp.getSharedPreferences().getString("webPageAttVer","");
	}
	
	
	
	
    //===========================================================================================
    private static final String PASSWORD_SECRET = "MyBaby";
    
    public static String encryptPassword(String clearText) {
        try {
            DESKeySpec keySpec = new DESKeySpec(
                    PASSWORD_SECRET.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            String encrypedPwd = Base64.encodeToString(cipher.doFinal(clearText
                    .getBytes("UTF-8")), Base64.DEFAULT);
            return encrypedPwd;
        } catch (Exception e) {
        }
        return clearText;
    }

    public static String decryptPassword(String encryptedPwd) {
        try {
            DESKeySpec keySpec = new DESKeySpec(
                    PASSWORD_SECRET.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] encryptedWithoutB64 = Base64.decode(encryptedPwd, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64);
            return new String(plainTextPwdBytes);
        } catch (Exception e) {
        }
        return encryptedPwd;
    }

    //===========================================================================================
}
