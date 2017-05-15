package mybaby.models;

import android.database.Cursor;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.Map;

import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.models.diary.Post;
import mybaby.models.friend.FriendRepository;
import mybaby.models.friend.FriendRequestRepository;
import mybaby.util.DateUtils;
import mybaby.util.MapUtils;

public class User  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int userId;
	private String name="";
	private String uniqueName="";
	private String email="";
	private long birthday;
	private String avatarUrl;
	private String avatarThumbnailUrl;
	private int genderNumber;
	private long lastActivityTime_gmt;
	private boolean bzRegistered;
	private boolean bzInfoModified;
	private long bzPhotoSubscriptionEndDate;
	private long bzCreateDate;
	private String fb_user_id="";
	private String fb_name="";
	private boolean frdIsBindingContacts;
	private boolean frdIsOpen;
	private int frdPrivacyTypeNumber;
	private String frdTelephoneCountryCode="";
	private String frdTelephone="";
	private boolean frdAllowFindMeByPhone;
	private boolean frdAllowFindMeByEmail;
	private boolean frdAllowFindMeByFacebook;
	private long lastPostsSyncTime;
	private String androidGCMRegId;
	private int androidAppVersion;
	private String uMengAndroidPushId="";

	//2015/11/18
	private String imUserName="";
	private String imUserPassword="";

	//2016/3/21 bj
	private boolean isAdmin;


	public boolean isAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getImUserName() {
		return TextUtils.isEmpty(imUserName)?"":imUserName;
	}
	public void setImUserName(String imUserName) {
		this.imUserName = imUserName;
	}
	public String getimUserPassword() {
		return imUserPassword;
	}
	public void setimUserPassword(String imUserPassword) {
		this.imUserPassword = imUserPassword;
	}
	public String getuMengAndroidPushId() {
		return uMengAndroidPushId;
	}
	public void setuMengAndroidPushId(String uMengAndroidPushId) {
		this.uMengAndroidPushId = uMengAndroidPushId;
	}
	public int getUserId(){
		return userId;
	}
	public void  setUserId(int userId){
		this.userId = userId;
	}
	public String getName(){
	    return name;
	}
	public void  setName(String name){
		this.name = name;
	}
	public String getUniqueName(){
	    return uniqueName;
	}
	public void  setUniqueName(String uniqueName){
	    this.uniqueName = uniqueName;
	}
	public String getEmail(){
	    return email;
	}
	public void  setEmail(String email){
	    this.email = email;
	}
	public long getBirthday(){
	    return birthday;
	}
	public void  setBirthday(long birthday){
	    this.birthday = birthday;
	}
	public String getAvatarUrl(){
	    return avatarUrl;
	}
	public void  setAvatarUrl(String avatarUrl){
	    this.avatarUrl = avatarUrl;
	}
	public String getAvatarThumbnailUrl(){
		if (TextUtils.isEmpty(avatarThumbnailUrl))
			return getNullAvatarUrl();
	    return avatarThumbnailUrl;
	}
	public boolean getRealAvatarThumbnailUrlIsNull(){
		return TextUtils.isEmpty(avatarThumbnailUrl);
	}
	public void  setAvatarThumbnailUrl(String avatarThumbnailUrl){
	    this.avatarThumbnailUrl = avatarThumbnailUrl;
	}
	public int getGenderNumber(){
	    return genderNumber;
	}
	public void  setGenderNumber(int genderNumber){
	    this.genderNumber = genderNumber;
	}
	public long getLastActivityTime_gmt(){
	    return lastActivityTime_gmt;
	}
	public void  setLastActivityTime_gmt(long lastActivityTime_gmt){
	    this.lastActivityTime_gmt = lastActivityTime_gmt;
	}
	public boolean getBzRegistered(){
	    return bzRegistered;
	}
	public void  setBzRegistered(boolean bzRegistered){
	    this.bzRegistered = bzRegistered;
	}
	public boolean getBzInfoModified(){
	    return bzInfoModified;
	}
	public void  setBzInfoModified(boolean bzInfoModified){
	    this.bzInfoModified = bzInfoModified;
	}
	public long getBzPhotoSubscriptionEndDate(){
	    return bzPhotoSubscriptionEndDate;
	}
	public void  setBzPhotoSubscriptionEndDate(long bzPhotoSubscriptionEndDate){
	    this.bzPhotoSubscriptionEndDate = bzPhotoSubscriptionEndDate;
	}
	public long getBzCreateDate(){
	    return bzCreateDate;
	}
	public void  setBzCreateDate(long bzCreateDate){
	    this.bzCreateDate = bzCreateDate;
	}
	public String getFb_user_id(){
	    return fb_user_id;
	}
	public void  setFb_user_id(String fb_user_id){
	    this.fb_user_id = fb_user_id;
	}
	public String getFb_name(){
	    return fb_name;
	}
	public void  setFb_name(String fb_name){
	    this.fb_name = fb_name;
	}
	public boolean getFrdIsBindingContacts(){
	    return frdIsBindingContacts;
	}
	public void  setFrdIsBindingContacts(boolean frdIsBindingContacts){
	    this.frdIsBindingContacts = frdIsBindingContacts;
	}
	public boolean getFrdIsOpen(){
	    return frdIsOpen;
	}
	public void  setFrdIsOpen(boolean frdIsOpen){
	    this.frdIsOpen = frdIsOpen;
	}
	public int getFrdPrivacyTypeNumber(){
	    return frdPrivacyTypeNumber;
	}
	public void  setFrdPrivacyTypeNumber(int frdPrivacyTypeNumber){
	    this.frdPrivacyTypeNumber = frdPrivacyTypeNumber;
	}
	public String getFrdTelephoneCountryCode(){
	    return frdTelephoneCountryCode;
	}
	public void  setFrdTelephoneCountryCode(String frdTelephoneCountryCode){
	    this.frdTelephoneCountryCode = frdTelephoneCountryCode;
	}
	public String getFrdTelephone(){
	    return frdTelephone;
	}
	public void  setFrdTelephone(String frdTelephone){
	    this.frdTelephone = frdTelephone;
	}
	public boolean getFrdAllowFindMeByPhone(){
	    return frdAllowFindMeByPhone;
	}
	public void  setFrdAllowFindMeByPhone(boolean frdAllowFindMeByPhone){
	    this.frdAllowFindMeByPhone = frdAllowFindMeByPhone;
	}
	public boolean getFrdAllowFindMeByEmail(){
	    return frdAllowFindMeByEmail;
	}
	public void  setFrdAllowFindMeByEmail(boolean frdAllowFindMeByEmail){
	    this.frdAllowFindMeByEmail = frdAllowFindMeByEmail;
	}
	public boolean getFrdAllowFindMeByFacebook(){
	    return frdAllowFindMeByFacebook;
	}
	public void  setFrdAllowFindMeByFacebook(boolean frdAllowFindMeByFacebook){
	    this.frdAllowFindMeByFacebook = frdAllowFindMeByFacebook;
	}
	public long getLastPostsSyncTime(){
		return lastPostsSyncTime;
	}
	public void  setLastPostsSyncTime(long lastPostsSyncTime){
		this.lastPostsSyncTime = lastPostsSyncTime;
	}
	public long getLastActivityTime(){
	    return  DateUtils.gmtTime2LocalTime(lastActivityTime_gmt);
	}
	public String getAndroidGCMRegId(){
		return androidGCMRegId;
	}
	public void  setAndroidGCMRegId(String androidGCMRegId){
		this.androidGCMRegId = androidGCMRegId;
	}
	public int getAndroidAppVersion(){
		return androidAppVersion;
	}
	public void  setAndroidAppVersion(int androidAppVersion){
		this.androidAppVersion = androidAppVersion;
	}

	
	// 2015-07-12 增加  本地数据库中没有这些内容
	private long babyBirthday;
	private int followCount;
	private int followerCount;
	private boolean isFriend;
	private int activityCount;

	//bj 2016 3 24 本地数据库中没有这些内容
	private int friend_count;


	public long getBabyBirthday() {
		return babyBirthday;
	}
	public void setBabyBirthday(long babyBirthday) {
		this.babyBirthday = babyBirthday;
	}
	public int getFollowCount() {
		return followCount;
	}
	public void setFollowCount(int followCount) {
		this.followCount = followCount;
	}
	public int getFollowerCount() {
		return followerCount;
	}
	public void setFollowerCount(int followerCount) {
		this.followerCount = followerCount;
	}
	public int getActivityCount() {
		return activityCount;
	}
	public void setActivityCount(int activityCount) {
		this.activityCount = activityCount;
	}
	public boolean getIsFriend() {
		return isFriend;
	}
	public void setIsFriend(boolean isFriend) {
		this.isFriend = isFriend;
	}

	public int getFriend_count() {
		return friend_count;
	}

	public void setFriend_count(int friend_count) {
		this.friend_count = friend_count;
	}

	public User(Cursor c){
    	this.setUserId(c.getInt(0));
    	this.setName(c.getString(1));
    	this.setUniqueName(c.getString(2));
    	this.setEmail(c.getString(3));
    	this.setBirthday(c.getLong(4));
    	this.setAvatarUrl(c.getString(5));
    	this.setAvatarThumbnailUrl(c.getString(6));
    	this.setGenderNumber(c.getInt(7));
    	this.setLastActivityTime_gmt(c.getLong(8));
    	this.setBzRegistered(c.getInt(9)>0);
    	this.setBzInfoModified(c.getInt(10)>0);
    	this.setBzPhotoSubscriptionEndDate(c.getLong(11));
    	this.setBzCreateDate(c.getLong(12));
    	this.setFb_user_id(c.getString(13));
    	this.setFb_name(c.getString(14));
    	this.setFrdIsBindingContacts(c.getInt(15)>0);
    	this.setFrdIsOpen(c.getInt(16)>0);
    	this.setFrdPrivacyTypeNumber(c.getInt(17));
    	this.setFrdTelephoneCountryCode(c.getString(18));
    	this.setFrdTelephone(c.getString(19));
    	this.setFrdAllowFindMeByPhone(c.getInt(20)>0);
    	this.setFrdAllowFindMeByEmail(c.getInt(21)>0);
    	this.setFrdAllowFindMeByFacebook(c.getInt(22)>0);
    	this.setLastPostsSyncTime(c.getLong(23));
    	this.setAndroidGCMRegId(c.getString(24));
    	this.setAndroidAppVersion(c.getInt(25));
    	this.setActivityCount(c.getInt(26));
    	this.setuMengAndroidPushId(c.getString(27));
    	this.setImUserName(c.getString(28));
    	this.setimUserPassword(c.getString(29));
		this.setIsAdmin(c.getInt(30)>0);
    	
    	
    }	
	
    public User(	int userId,
		    		String name,
		    		String uniqueName,
		    		String email,
		    		long birthday,
		    		String avatarUrl,
		    		String avatarThumbnailUrl,
		    		int genderNumber,
		    		long lastActivityTime_gmt,
		    		boolean bzRegistered,
		    		boolean bzInfoModified,
		    		long bzPhotoSubscriptionEndDate,
		    		long bzCreateDate,
		    		String fb_user_id,
		    		String fb_name,
		    		boolean frdIsBindingContacts,
		    		boolean frdIsOpen,
		    		int frdPrivacyTypeNumber,
		    		String frdTelephoneCountryCode,
		    		String frdTelephone,
		    		boolean frdAllowFindMeByPhone,
		    		boolean frdAllowFindMeByEmail,
		    		boolean frdAllowFindMeByFacebook,
		    		long lastPostsSyncTime,
		    		String androidGCMRegId,
		    		int androidAppVersion,
		    		String uMengAndroidPushId,
		    		String imUserName,
		    		String imUserPassword,boolean isAdmin) {
    	this.setUserId(userId);
    	this.setName(name);
    	this.setUniqueName(uniqueName);
    	this.setEmail(email);
    	this.setBirthday(birthday);
    	this.setAvatarUrl(avatarUrl);
    	this.setAvatarThumbnailUrl(avatarThumbnailUrl);
    	this.setGenderNumber(genderNumber);
    	this.setLastActivityTime_gmt(lastActivityTime_gmt);
    	this.setBzRegistered(bzRegistered);
    	this.setBzInfoModified(bzInfoModified);
    	this.setBzPhotoSubscriptionEndDate(bzPhotoSubscriptionEndDate);
    	this.setBzCreateDate(bzCreateDate);
    	this.setFb_user_id(fb_user_id);
    	this.setFb_name(fb_name);
    	this.setFrdIsBindingContacts(frdIsBindingContacts);
    	this.setFrdIsOpen(frdIsOpen);
    	this.setFrdPrivacyTypeNumber(frdPrivacyTypeNumber);
    	this.setFrdTelephoneCountryCode(frdTelephoneCountryCode);
    	this.setFrdTelephone(frdTelephone);
    	this.setFrdAllowFindMeByPhone(frdAllowFindMeByPhone);
    	this.setFrdAllowFindMeByEmail(frdAllowFindMeByEmail);
    	this.setFrdAllowFindMeByFacebook(frdAllowFindMeByFacebook);
    	this.setLastPostsSyncTime(lastPostsSyncTime);
    	this.setAndroidGCMRegId(androidGCMRegId);
    	this.setAndroidAppVersion(androidAppVersion);
    	this.setuMengAndroidPushId(uMengAndroidPushId);
    	this.setImUserName(imUserName);
    	this.setimUserPassword(imUserPassword);
		this.setIsAdmin(isAdmin);
    }

    public User(int userId,
    		String name,
    		String uniqueName,
    		String email,
    		long birthday,
    		String avatarUrl,
    		String avatarThumbnailUrl,
    		int genderNumber) {
		this.setUserId(userId);
		this.setName(name);
		this.setUniqueName(uniqueName);
		this.setEmail(email);
		this.setBirthday(birthday);
		this.setAvatarUrl(avatarUrl);
		this.setAvatarThumbnailUrl(avatarThumbnailUrl);
		this.setGenderNumber(genderNumber);
	}
    
    public User(){
    	super();
    }
	
	public boolean isSelf(){
		return this.getUserId() == MyBabyApp.currentUser.getUserId();
	}
	
	public boolean isFriend(){
		return FriendRepository.existByUserId(this.getUserId());
	}
	
	public boolean isFriendRequest(){
		return FriendRequestRepository.existByUserId(this.getUserId());
	}
	
	public int getNullAvatar(){
		if(getGenderNumber()==Post.gender.Male.ordinal()){
			return R.drawable.avatar_male;
		}else if(getGenderNumber()==Post.gender.Female.ordinal()){
			return R.drawable.avatar_female;
		}else{
			return R.drawable.avatar;
		}
	}
	public String getNullAvatarUrl(){
		if(getGenderNumber()==Post.gender.Male.ordinal()){
			return "assets://avatar_male.png";
		}else if(getGenderNumber()==Post.gender.Female.ordinal()){
			return "assets://avatar_female.png";
		}else{
			return "assets://avatar.png";
		}
	}
	
	public int getDrawableNull()
	{
		return R.drawable.avatar_camera_bg;
	}
	
	public boolean hasValidPhoneNumber(){
		return this.getFrdTelephoneCountryCode() != null && this.getFrdTelephoneCountryCode().length() > 0
				&& this.getFrdTelephone() != null && this.getFrdTelephone().length() > 0;
	}
	
	

	public static User[] createByArray_new(Object[] arr){
		if (arr==null)
			return null;
		User[] retArr=new User[arr.length];
		
       	for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = createByMap_new(map);
       	}
       	
		return retArr;
	}

	/**
	 *
	 * @param map
	 * @return
	 */
	public static User createByMap_new(Map<?, ?> map){
		int userId = MapUtils.getMapInt(map, "id");
		
		User obj = UserRepository.load(userId);
		if(obj == null){
			obj = new User();
		}
 		obj.setUserId(userId);
 		if(map.containsKey("name"))
 			obj.setName(MapUtils.getMapStr(map, "name"));
 		if(map.containsKey("avatarThumbnailUrl"))
 			obj.setAvatarThumbnailUrl((MapUtils.getMapStr(map, "avatarThumbnailUrl")).trim());
 		if(map.containsKey("avatarUrl"))
 			obj.setAvatarUrl(MapUtils.getMapStr(map, "avatarUrl"));
 		if(map.containsKey("baby_birthday"))
 			obj.setBabyBirthday(MapUtils.getMapDateLong(map, "baby_birthday"));
		if(map.containsKey("follow_count"))
			obj.setFollowCount(MapUtils.getMapInt(map, "follow_count"));
		if(map.containsKey("follower_count"))
			obj.setFollowerCount(MapUtils.getMapInt(map, "follower_count"));
		if(map.containsKey("friend_count"))
			obj.setFriend_count(MapUtils.getMapInt(map, "friend_count"));
		if(map.containsKey("is_friend"))
			obj.setIsFriend(MapUtils.getMapBool(map, "is_friend"));
		if(map.containsKey( "activity_count"))
			obj.setActivityCount(MapUtils.getMapInt(map, "activity_count"));
		if(map.containsKey( "gender"))
			obj.setGenderNumber(MapUtils.getMapInt(map, "gender"));
		UserRepository.save(obj);
		
		return obj;
	}
	
	/**
	 * 通过返回的map创建user对象
	 * @param contentHash
	 * @return
	 */
	public static User createUserByMap(Map<?, ?> contentHash){
		return new User( MapUtils.getMapInt(contentHash, "userId"),
				MapUtils.getMapStr(contentHash, "name"),
				MapUtils.getMapStr(contentHash, "uniqueName"),
				MapUtils.getMapStr(contentHash, "email"),
				MapUtils.getMapDateLongFrom_yyyyMMdd(contentHash, "birthday"),
				MapUtils.getMapStr(contentHash, "avatarThumbnailUrl"),
				MapUtils.getMapStr(contentHash, "avatarUrl"),
				MapUtils.getMapInt(contentHash, "gender"),
				0,
				MapUtils.getMapBool(contentHash, "bz_registered"),
				false,
				MapUtils.getMapDateLongFrom_yyyyMMdd(contentHash, "bz_photoSubscriptionEndDate"),
				MapUtils.getMapDateLongFrom_yyyyMMdd(contentHash, "bz_createDate"),
				MapUtils.getMapStr(contentHash, "fb_user_id"),
				MapUtils.getMapStr(contentHash, "fb_name"),
				MapUtils.getMapBool(contentHash, "frdIsBindingContacts"),
				MapUtils.getMapBool(contentHash, "frdIsOpen"),
				MapUtils.getMapInt(contentHash, "frdPrivacyType"),
				MapUtils.getMapStr(contentHash, "frdTelephoneCountryCode"),
				MapUtils.getMapStr(contentHash, "frdTelephone"),
				MapUtils.getMapBool(contentHash, "frdAllowFindMeByPhone"),
				MapUtils.getMapBool(contentHash, "frdAllowFindMeByEmail"),
				MapUtils.getMapBool(contentHash, "frdAllowFindMeByFacebook"),
				System.currentTimeMillis(),
				"",
				MyBabyApp.version,
				MapUtils.getMapStr(contentHash, "uMengAndroidPushId"),
				MapUtils.getMapStr(contentHash, "im_userid"),//
				MapUtils.getMapStr(contentHash, "im_userpass"),
				MapUtils.getMapBool(contentHash, "is_admin")
		);
	}
}
