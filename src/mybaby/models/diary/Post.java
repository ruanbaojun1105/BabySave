package mybaby.models.diary;

import android.database.Cursor;

import java.io.Serializable;

import mybaby.ui.MyBabyApp;
import mybaby.models.friend.Activity;
import mybaby.models.friend.ActivityRepository;
import mybaby.models.person.Baby;
import mybaby.models.person.FamilyPerson;
import mybaby.models.person.Person;
import mybaby.models.person.SelfPerson;
import mybaby.util.DateUtils;


public class Post implements Serializable{
	public enum remoteSync{		
		LocalModified,
		SyncError,
		SyncSuccess
	}

	public enum type{
		Post,
		Baby,
		Person
	}

	public enum gender{
		Unknown,
		Male,//男性
		Female//女性
	}

	public enum personType{
		Other,
		Mom,
		Dad
	}

	public enum privacyType{
		Private,
		Friends,
		Public,
	}

	public static final String Status_Draft = "draft";
	public static final String Status_Private = "private";
	public static final String Status_Publish = "publish";
	
	private int id;
	private int postid;
	private long dateCreated;
	private long date_created_gmt;
	private String description="";
	private int userid;
	private int remoteSyncFlag;
	private boolean isLocalDeleted;//
	private int typeNumber;//
	private int bookTheme;
	private long birthday;
	private int genderNumber;
	private boolean isSelf;
	private int personTypeNumber;
	private long timelinePostToBookBeginDate;
	private long timelinePostToBookEndDate;
	private int page;
	private int orderNumber;
	private int privacyTypeNumber;
	private String guid="";
	private String status=Status_Draft;//
	private int placeObjId;
	
	public int getPlaceObjId() {
		return placeObjId;
	}
	public void setPlaceObjId(int placeObjId) {
		this.placeObjId = placeObjId;
	}
	public int getId(){
		return id;
	}
	public void  setId(int id){
		this.id = id;
	}
	public int getPostid(){
		return postid;
	}
	public void  setPostid(int postid){
	    this.postid = postid;
	}
	public long getDateCreated(){
		return DateUtils.gmtTime2LocalTime(date_created_gmt);
	}
	public void  setDateCreated(long dateCreated){
		//全部用gmt时间
	    this.date_created_gmt=DateUtils.localTime2GMTTime(dateCreated);
	}
	public long getDate_created_gmt(){
		return date_created_gmt;
	}
	public void  setDate_created_gmt(long date_created_gmt){
	    this.date_created_gmt = date_created_gmt;
	}
	public String getDescription(){
		return description==null ? "" : description;
	}
	public void  setDescription(String description){
	    this.description = description;
	}
	public int getUserid(){
		if(userid==0 && MyBabyApp.currentUser != null){
			return MyBabyApp.currentUser.getUserId(); //避免自动匿名注册后自己的userId从0改为x后各地方的缓存对象没有更新的问题
		}else{
			return userid;
		}
	}
	public void  setUserid(int userid){
	    this.userid = userid;
	}
	public int getRemoteSyncFlag(){
		return remoteSyncFlag;
	}
	public void  setRemoteSyncFlag(int remoteSyncFlag){
		this.remoteSyncFlag = remoteSyncFlag;
	} 
	public boolean getIsLocalDeleted(){
		return isLocalDeleted;
	}
	public void  setIsLocalDeleted(boolean isLocalDeleted){
	    this.isLocalDeleted = isLocalDeleted;
	}
	public int getTypeNumber(){
		return typeNumber;
	}
	public void  setTypeNumber(int typeNumber){
	    this.typeNumber = typeNumber;
	}
	public int getBookTheme(){
		return bookTheme;
	}
	public void  setBookTheme(int bookTheme){
	    this.bookTheme = bookTheme;
	}
	public long getBirthday(){
		return birthday;
	}
	public void  setBirthday(long birthday){
	    this.birthday = birthday;
	}
	public int getGenderNumber(){
		return genderNumber;
	}
	public void  setGenderNumber(int genderNumber){
	    this.genderNumber = genderNumber;
	}
	public boolean getIsSelf(){
		return isSelf;
	}
	public void  setIsSelf(boolean isSelf){
	    this.isSelf = isSelf;
	}
	public int getPersonTypeNumber(){
		return personTypeNumber;
	}
	public void  setPersonTypeNumber(int personTypeNumber){
	    this.personTypeNumber = personTypeNumber;
	    if(personTypeNumber==personType.Mom.ordinal()){
	    	this.setGenderNumber(gender.Female.ordinal());
	    }else if(personTypeNumber==personType.Dad.ordinal()){
	    	this.setGenderNumber(gender.Male.ordinal());
	    }
	}
	public long getTimelinePostToBookBeginDate(){
		return timelinePostToBookBeginDate;
	}
	public void  setTimelinePostToBookBeginDate(long timelinePostToBookBeginDate){
	    this.timelinePostToBookBeginDate = timelinePostToBookBeginDate;
	}
	public long getTimelinePostToBookEndDate(){
		return timelinePostToBookEndDate;
	}
	public void  setTimelinePostToBookEndDate(long timelinePostToBookEndDate){
	    this.timelinePostToBookEndDate = timelinePostToBookEndDate;
	}
	public int getPage(){
		return page;
	}
	public void  setPage(int page){
	    this.page = page;
	}
	public int getOrderNumber(){
		return orderNumber;
	}
	public void  setOrderNumber(int orderNumber){
	    this.orderNumber = orderNumber;
	}
	public int getPrivacyTypeNumber(){
		return privacyTypeNumber;
	}
	public void  setPrivacyTypeNumber(int privacyTypeNumber){
	    this.privacyTypeNumber = privacyTypeNumber;
	    if(!this.getStatus().equals(Status_Draft)){
		    if(privacyTypeNumber==privacyType.Private.ordinal()){
		    	this.setStatus(Status_Private);
		    	
		        //删除本地缓存的activity
		        if(this.getPostid()>0){
		        	Activity activity=ActivityRepository.loadByPostId(this.getPostid());
		        	if(activity != null){
		        		ActivityRepository.delete(activity.getActivityId());
		        	}
		        }
		    }else{
		    	this.setStatus(Status_Publish);
		    }
	    }
	}
	public String getGuid(){
		return guid;
	}
	public void  setGuid(String guid){
	    this.guid = guid;
	}
	public String getStatus(){
		return status;
	}
	public void  setStatus(String status){
		this.status = status;
	}
	
	public Post(){
		
	}
	
    public Post(Cursor c){
    	this.setId(c.getInt(0));
    	this.setPostid(c.getInt(1));
    	this.setDateCreated(c.getLong(2));
    	this.setDate_created_gmt(c.getLong(3));
    	this.setDescription(c.getString(4));
    	this.setUserid(c.getInt(5));
    	this.setRemoteSyncFlag(c.getInt(6));
    	this.setIsLocalDeleted(c.getInt(7)>0);
    	this.setTypeNumber(c.getInt(8));
    	this.setBookTheme(c.getInt(9));
    	this.setBirthday(c.getLong(10));
    	this.setGenderNumber(c.getInt(11));
    	this.setIsSelf(c.getInt(12)>0);
    	this.setPersonTypeNumber(c.getInt(13));
    	this.setTimelinePostToBookBeginDate(c.getLong(14));
    	this.setTimelinePostToBookEndDate(c.getLong(15));
    	this.setPage(c.getInt(16));
    	this.setOrderNumber(c.getInt(17));
    	this.setPrivacyTypeNumber(c.getInt(18));
    	this.setGuid(c.getString(19));
    	this.setStatus(c.getString(20));
    	
    	this.setPlaceObjId(c.getInt(21));
    }	
	
    public Post(	int postid,
		    		long dateCreated,
		    		long date_created_gmt,
		    		String description,
		    		int userid,
		    		int remoteSyncFlag,
		    		boolean isLocalDeleted,
		    		int typeNumber,
		    		int bookTheme,
		    		long birthday,
		    		int genderNumber,
		    		boolean isSelf,
		    		int personTypeNumber,
		    		long timelinePostToBookBeginDate,
		    		long timelinePostToBookEndDate,
		    		int page,
		    		int orderNumber,
		    		int privacyTypeNumber,
		    		String guid,
		    		String status,
		    		int placeObjId) {
    	this.setPostid(postid);
    	this.setDateCreated(dateCreated);
    	this.setDate_created_gmt(date_created_gmt);
    	this.setDescription(description);
    	this.setUserid(userid);
    	this.setRemoteSyncFlag(remoteSyncFlag);
    	this.setIsLocalDeleted(isLocalDeleted);
    	this.setTypeNumber(typeNumber);
    	this.setBookTheme(bookTheme);
    	this.setBirthday(birthday);
    	this.setGenderNumber(genderNumber);
    	this.setIsSelf(isSelf);
    	this.setPersonTypeNumber(personTypeNumber);
    	this.setTimelinePostToBookBeginDate(timelinePostToBookBeginDate);
    	this.setTimelinePostToBookEndDate(timelinePostToBookEndDate);
    	this.setPage(page);
    	this.setOrderNumber(orderNumber);
    	this.setPrivacyTypeNumber(privacyTypeNumber);
    	this.setGuid(guid);
    	this.setStatus(status);
    	
    	this.setPlaceObjId(placeObjId);
    }
    
    public Post(int postid) {
    	this.setPostid(postid); 	
    }	
	
    public Person createPerson(){
    	if(getTypeNumber()==type.Baby.ordinal()){
    		 return Baby.createByPost(this);
    	}else if(getTypeNumber()==type.Person.ordinal()){
    		if(getIsSelf()){
   		 		return SelfPerson.createByPost(this);
    		}else{
    			return FamilyPerson.createByPost(this);
    		}
    	}else{
    		return null;
    	}
    }
    



}