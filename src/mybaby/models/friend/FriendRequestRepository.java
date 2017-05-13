package mybaby.models.friend;

import mybaby.models.Repository;
import android.content.ContentValues;
import android.database.Cursor;

public class FriendRequestRepository extends Repository {

	   public static FriendRequest load(int friendshipId) {

	        Cursor c = db().query(table_friend_request(), null, "friendshipId=" + friendshipId, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        FriendRequest friend=null;
	        if (numRows > 0) {
	        	friend=new FriendRequest(c);
	        }
	        c.close();

	        return friend;
	    }
	   
	   public static int save(FriendRequest friendRequest) {
	       int returnValue = -1;
	       if (friendRequest != null) {

	           ContentValues values = new ContentValues();
	           
	           values.put("friendshipId", friendRequest.getFriendshipId());
	           values.put("requestDate_gmt", friendRequest.getRequestDate_gmt());
	           values.put("requestUserId", friendRequest.getRequestUserId());
	           values.put("sayHello", friendRequest.getSayHello());

	           if(FriendRequestRepository.exist(friendRequest.getFriendshipId()) ){
	           		int rows=db().update(table_friend_request(), values, "friendshipId=" + friendRequest.getFriendshipId(), null);
	           		if(rows>0){
	           			returnValue=friendRequest.getFriendshipId();
	           		}
	           }else{
	           		db().insert(table_friend_request(), null, values);
	           		returnValue=friendRequest.getFriendshipId();
	           }
	       }
	       return (returnValue);
	   } 
	   
	   
	    public static boolean delete(int friendshipId) {
		      boolean returnValue = false;

		      int result = 0;
		      result = db().delete(table_friend_request(), "friendshipId=" + friendshipId, null);

		      if (result == 1) {
		          returnValue = true;
		      }

		      return returnValue;
		}
	    
	    public static boolean exist(int friendshipId) {
	        Cursor c = db().query(table_friend_request(), null, "friendshipId=" + friendshipId, null,
	                null, null, null);
	        int numRows = c.getCount();
	        c.close();
			return numRows > 0;
	    }
	    
	    public static int friendRequestCount() {
	        Cursor c = db().query(table_friend_request(), null, null, null, null, null, null);
	        int numRows = c.getCount();
	        c.close();
	        return numRows;
	    }
	    
	    public static int maxFriendRequestId() {
	        Cursor c = db().rawQuery("SELECT max(friendshipId) FROM " + table_friend_request(),null);
	        int numRows = c.getCount();
	        if(numRows>0){
	        	c.moveToFirst();
	        	int result=c.getInt(0);
	            c.close();
	            return result;
	        }else{
	        	c.close();
	        	return 0;
	        }
	    }
	    
	    public static boolean existByUserId(int requestUserId) {
	        Cursor c = db().query(table_friend_request(), null, "requestUserId=" + requestUserId, null,
	                null, null, null);
	        int numRows = c.getCount();
	        c.close();
			return numRows > 0;
	    }
	    
	   public static FriendRequest loadByUserId(int requestUserId) {

	        Cursor c = db().query(table_friend_request(), null, "requestUserId=" + requestUserId, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        FriendRequest friend=null;
	        if (numRows > 0) {
	        	friend=new FriendRequest(c);
	        }
	        c.close();

	        return friend;
	    }
	   
	    public static FriendRequest[] loadAll() {
	        Cursor c = db().query(table_friend_request(), null, null, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        FriendRequest[] friendRequests=new FriendRequest[numRows];
	        for (int i = 0; i < numRows; ++i) {
	            if (c.getString(0) != null) {
	            	friendRequests[i]=new FriendRequest(c);
	            }
	            c.moveToNext();
	        }
	        c.close();

	        return friendRequests;
	    }
	    
	    public static boolean clear() {
		      db().delete(table_friend_request(), null, null);
		      return true;
		}
}
