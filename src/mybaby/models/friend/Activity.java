package mybaby.models.friend;

import mybaby.models.diary.Post;
import mybaby.models.diary.PostRepository;
import mybaby.util.DateUtils;
import android.database.Cursor;

public class Activity {
	
	//用enum死活不对，那就用常量算了
	public static final int Type_Post=0;       //日记对应的activity        itemId=postId
	public static final int Type_PhotoBook=1;  //新建照片书对应的activity   itemId=postId
	public static final int Type_Tag=2;        //新建Tag对应的activity     itemId=postId
	public static final int Type_Comment=3;    //评论对应的activity        itemId=主activityID
	public static final int Type_Like=4;       //赞对应的activity          itemId=主activityID
	public static final int Type_GroupPost=5;   //群组post的activity

	
	private int activityId;
	private String content;
	private int typeNumber;
	private int userId;
	private long datetime_gmt;
	private int itemId;
	private int secondaryItemId;
	private long lastSync;
	private boolean isMentionMe;
	
	public int getActivityId(){
	    return activityId;
	}
	public void  setActivityId(int activityId){
	    this.activityId = activityId;
	}
	public String getContent(){
	    return content;
	}
	public void  setContent(String content){
	    this.content = content;
	}
	public int getTypeNumber(){
	    return typeNumber;
	}
	public void  setTypeNumber(int typeNumber){
	    this.typeNumber = typeNumber;
	}
	public int getUserId(){
	    return userId;
	}
	public void  setUserId(int userId){
	    this.userId = userId;
	}
	public long getDatetime_gmt(){
	    return datetime_gmt;
	}
	public void  setDatetime_gmt(long datetime_gmt){
	    this.datetime_gmt = datetime_gmt;
	}
	public int getItemId(){
	    return itemId;
	}
	public void  setItemId(int itemId){
	    this.itemId = itemId;
	}
	public int getSecondaryItemId(){
	    return secondaryItemId;
	}
	public void  setSecondaryItemId(int secondaryItemId){
	    this.secondaryItemId = secondaryItemId;
	}
	public long getLastSync(){
	    return lastSync;
	}
	public void  setLastSync(long lastSync){
	    this.lastSync = lastSync;
	}
	public boolean getIsMentionMe(){
	    return isMentionMe;
	}
	public void  setIsMentionMe(boolean isMentionMe){
	    this.isMentionMe = isMentionMe;
	}
	
	public long getDatetime(){
	    return DateUtils.gmtTime2LocalTime(getDatetime_gmt());
	}
	
	//增加一个username属性，在查询activity对应的赞和评论列表时快速显示出用户名使用
	private String userName;
	public String getUserName(){
	    return this.userName;
	}
	public void  setUserName(String userName){
	    this.userName = userName;
	}
	
	
    public Activity(Cursor c){
    	this.setActivityId(c.getInt(0));
    	this.setContent(c.getString(1));
    	this.setTypeNumber(c.getInt(2));
    	this.setUserId(c.getInt(3));
    	this.setDatetime_gmt(c.getLong(4));
    	this.setItemId(c.getInt(5));
    	this.setSecondaryItemId(c.getInt(6));
    	this.setLastSync(c.getLong(7));
    	this.setIsMentionMe(c.getInt(8)>0);
    }	
	
    public Activity(	int activityId,
    					String content,
			    		int typeNumber,
			    		int userId,
			    		long datetime_gmt,
			    		int itemId,
			    		int secondaryItemId,
			    		long lastSync,
			    		boolean isMentionMe) {
    	this.setActivityId(activityId);
    	this.setContent(content);
    	this.setTypeNumber(typeNumber);
    	this.setUserId(userId);
    	this.setDatetime_gmt(datetime_gmt);
    	this.setItemId(itemId);
    	this.setSecondaryItemId(secondaryItemId);
    	this.setLastSync(lastSync);
    	this.setIsMentionMe(isMentionMe);
    }
    
	public Activity() {
	}
	
	
	public Activity getRootActivity(){
	    if(this.getTypeNumber()==Type_Like || this.getTypeNumber()==Type_Comment){
	        return  ActivityRepository.load(this.getItemId());
	    }else{
	        return this;
	    }
	}
	
	public Post getPost(){
	    if(this.getTypeNumber()==Type_Post || this.getTypeNumber()==Type_PhotoBook || this.getTypeNumber()==Type_Tag){
	        return  PostRepository.loadByPostId(this.getItemId());
	    }else{
	        return null;
	    }
	}
}
