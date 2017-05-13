package mybaby.models.friend;

import mybaby.util.DateUtils;
import android.database.Cursor;

public class FriendRequest {
	private int friendshipId;
	private long requestDate_gmt;
	private int requestUserId;
	private String sayHello;
	
	public int getFriendshipId(){
	    return friendshipId;
	}
	public void  setFriendshipId(int friendshipId){
	    this.friendshipId = friendshipId;
	}
	public long getRequestDate_gmt(){
	    return requestDate_gmt;
	}
	public void  setRequestDate_gmt(long requestDate_gmt){
	    this.requestDate_gmt = requestDate_gmt;
	}
	public int getRequestUserId(){
	    return requestUserId;
	}
	public void  setRequestUserId(int requestUserId){
	    this.requestUserId = requestUserId;
	}
	public String getSayHello(){
	    return sayHello;
	}
	public void  setSayHello(String sayHello){
	    this.sayHello = sayHello;
	}
	
	public long getRequestDate(){
	    return DateUtils.gmtTime2LocalTime(getRequestDate_gmt());
	}
	

    public FriendRequest(Cursor c){
    	this.setFriendshipId(c.getInt(0));
    	this.setRequestDate_gmt(c.getLong(1));
    	this.setRequestUserId(c.getInt(2));
    	this.setSayHello(c.getString(3));
    }	
	
    public FriendRequest(	int friendshipId,
				    		long requestDate_gmt,
				    		int requestUserId,
				    		String sayHello) {
    	this.setFriendshipId(friendshipId);
    	this.setRequestDate_gmt(requestDate_gmt);
    	this.setRequestUserId(requestUserId);
    	this.setSayHello(sayHello);
    }		
	
}
