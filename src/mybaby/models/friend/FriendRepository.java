package mybaby.models.friend;

import mybaby.ui.MyBabyApp;
import mybaby.models.Repository;
import android.content.ContentValues;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;

public class FriendRepository extends Repository {

	   public static Friend load(int friendshipId) {

	        Cursor c = db().query(table_friend(), null, "friendshipId=" + friendshipId, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        Friend friend=null;
	        if (numRows > 0) {
	        	friend=new Friend(c);
	        }
	        c.close();

	        return friend;
	    }
	   
	   public static int save(Friend friend) {
	       int returnValue = -1;
	       if (friend != null) {

	           ContentValues values = new ContentValues();
	           
	           values.put("friendshipId", friend.getFriendshipId());
	           values.put("friendUserId", friend.getFriendUserId());
	           values.put("isNew", friend.getIsNew());

	           if(FriendRepository.exist(friend.getFriendshipId()) ){
	           		int rows=db().update(table_friend(), values, "friendshipId=" + friend.getFriendshipId(), null);
	           		if(rows>0){
	           			returnValue=friend.getFriendshipId();
	           		}
	           }else{
	           		db().insert(table_friend(), null, values);
	           		returnValue=friend.getFriendshipId();
	           }

	       }
	       return (returnValue);
	   } 
	   
	   
	    public static boolean delete(Friend friend) {
		      boolean returnValue = false;

		      int result = 0;
		      result = db().delete(table_friend(), "friendshipId=" + friend.getFriendshipId(), null);

		      if (result == 1) {
		          returnValue = true;
		      }

		      return returnValue;
		}
	    
	    public static boolean deleteByUserId(int userId) {
		      boolean returnValue = false;

		      int result = 0;
		      result = db().delete(table_friend(), "friendUserId=" + userId, null);

		      if (result == 1) {
		          returnValue = true;
		      }

		      return returnValue;
		}
	    
	    public static boolean exist(int friendshipId) {
	        Cursor c = db().query(table_friend(), null, "friendshipId=" + friendshipId, null,
	                null, null, null);
	        int numRows = c.getCount();
	        c.close();
			return numRows > 0;
	    }

	    public static boolean existByUserId(int friendUserId) {
	        Cursor c = db().query(table_friend(), null, "friendUserId=" + friendUserId, null,
	                null, null, null);
	        int numRows = c.getCount();
	        c.close();
			return numRows > 0;
	    }	    
	    
	    public static Friend[] loadAll() {
	        Cursor c = db().query(table_friend(), null, null, null, null, null, "isNew DESC,friendShipId");

	        int numRows = c.getCount();
	        c.moveToFirst();

	        Friend[] friends=new Friend[numRows];
	        for (int i = 0; i < numRows; ++i) {
	            if (c.getString(0) != null) {
	            	friends[i]=new Friend(c);
	            }
	            c.moveToNext();
	        }
	        c.close();

	        return friends;
	    }
	    
	    public static int friendCount() {
	        Cursor c = db().query(table_friend(), null, null, null, null, null, null);
	        int numRows = c.getCount();
	        c.close();
	        return numRows;
	    }
	    
	    public static int newFriendCount() {
	        Cursor c = db().query(table_friend(), null, "isNew=1", null, null, null, null);
	        int numRows = c.getCount();
	        c.close();
	        return numRows;
	    }
	    
	    public static boolean clear() {
		      db().delete(table_friend(), null, null);
		      return true;
		}
	    
	    public static Friend loadOrCreateByInfo(	int friendshipId,
													int friendUserId,
													boolean isNew){
			Friend friend=load(friendshipId);
			if(friend==null){
				friend = new Friend();
			}
			friend.setFriendshipId(friendshipId);
			friend.setFriendUserId(friendUserId);
			friend.setIsNew(isNew);
			
			return friend;
		}
	    
	    public static long getLastSyncFriendsDate(){
	    	return  MyBabyApp.getSharedPreferences().getLong("friendsLastSyncDate",0);
	    }
	    
	    public static void setLastSyncFriendsDate(long time){
	    	Editor edit=MyBabyApp.getSharedPreferences().edit();
	    	edit.putLong("friendsLastSyncDate", time);
	    	edit.commit();
	    }

}
