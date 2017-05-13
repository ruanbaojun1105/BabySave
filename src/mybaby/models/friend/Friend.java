package mybaby.models.friend;

import android.database.Cursor;

public class Friend {
	private int friendshipId;
	private int friendUserId;
	private boolean isNew;
	
	public int getFriendshipId(){
	    return friendshipId;
	}
	public void  setFriendshipId(int friendshipId){
	    this.friendshipId = friendshipId;
	}
	public int getFriendUserId(){
	    return friendUserId;
	}
	public void  setFriendUserId(int friendUserId){
	    this.friendUserId = friendUserId;
	}
	public boolean getIsNew(){
	    return isNew;
	}
	public void  setIsNew(boolean isNew){
	    this.isNew = isNew;
	}
	
	
	
    public Friend(Cursor c){
    	this.setFriendshipId(c.getInt(0));
    	this.setFriendUserId(c.getInt(1));
    	this.setIsNew(c.getInt(2)>0);
    }	
	
    public Friend(	int friendshipId,
    				int friendUserId,
    				boolean isNew) {
    	this.setFriendshipId(friendshipId);
    	this.setFriendUserId(friendUserId);
    	this.setIsNew(isNew);
    }
    
	public Friend() {

	}	
}
