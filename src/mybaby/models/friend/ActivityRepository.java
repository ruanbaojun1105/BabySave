package mybaby.models.friend;

import mybaby.ui.MyBabyApp;
import mybaby.models.Repository;
import mybaby.models.User;
import mybaby.models.UserRepository;
import android.content.ContentValues;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;

public class ActivityRepository extends Repository {

	   public static Activity load(int activityId) {

	        Cursor c = db().query(table_activity(), null, "activityId=" + activityId, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        Activity activity=null;
	        if (numRows > 0) {
	        	activity=new Activity(c);
	        }
	        c.close();

	        return activity;
	    }
	   
	   public static int save(Activity activity) {
	       int returnValue = -1;
	       if (activity != null) {

	           ContentValues values = new ContentValues();
	           
	           values.put("activityId", activity.getActivityId());
	           values.put("content", activity.getContent());
	           values.put("typeNumber", activity.getTypeNumber());
	           values.put("userId", activity.getUserId());
	           values.put("datetime_gmt", activity.getDatetime_gmt());
	           values.put("itemId", activity.getItemId());
	           values.put("secondaryItemId", activity.getSecondaryItemId());
	           values.put("lastSync", activity.getLastSync());
	           values.put("isMentionMe", activity.getIsMentionMe());

	           if(ActivityRepository.exist(activity.getActivityId()) ){
	           		int rows=db().update(table_activity(), values, "activityId=" + activity.getActivityId(), null);
	           		if(rows>0){
	           			returnValue=activity.getActivityId();
	           		}
	           }else{
	           		db().insert(table_activity(), null, values);
	           		returnValue=activity.getActivityId();
	           }

	       }
	       return (returnValue);
	   } 
	   
	   
	    public static boolean delete(int activityId) {
		      boolean returnValue = false;

		      int result = 0;
		      result = db().delete(table_activity(), "activityId=" + activityId, null);

		      if (result == 1) {
		          returnValue = true;
		      }

		      return returnValue;
		}
	    
	    public static boolean exist(int activityId) {
	        Cursor c = db().query(table_activity(), null, "activityId=" + activityId, null,
	                null, null, null);
	        int numRows = c.getCount();
	        c.close();
			return numRows > 0;
	    }
	    
	   public static Activity loadByPostId(int postId) {

	        Cursor c = db().query(table_activity(), null, "typeNumber=" + Activity.Type_Post + " and itemId=" + postId, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        Activity activity=null;
	        if (numRows > 0) {
	        	activity=new Activity(c);
	        }
	        c.close();

	        return activity;
	    }

	   public static Activity[] loadActivityList(int maxRowCount) {    	
			  Cursor c = db().rawQuery("SELECT * FROM " 
									+ table_activity() 
									+ " WHERE activityId>0 and typeNumber=" + Activity.Type_Post
									+ " order by activityId DESC"
									+ " LIMIT " + maxRowCount, 
									null);

		      int numRows = c.getCount();
		      c.moveToFirst();
		      
		      Activity[] mentions=new Activity[numRows];
		      
		      for (int i = 0; i < numRows; ++i) {
		          if (c.getString(0) != null) {
		        	  mentions[i]=new Activity(c);
		          }
		          c.moveToNext();
		      }
		      c.close();

		      return mentions;
		}
	   
	   public static Activity[] loadMentionList(int maxRowCount) {    	
			  Cursor c = db().rawQuery("SELECT * FROM " 
									+ table_activity() 
									+ " WHERE activityId>0 and isMentionMe=1"
									+ " order by activityId DESC"
									+ " LIMIT " + maxRowCount, 
									null);

		      int numRows = c.getCount();
		      c.moveToFirst();
		      
		      Activity[] mentions=new Activity[numRows];
		      
		      for (int i = 0; i < numRows; ++i) {
		          if (c.getString(0) != null) {
		        	  mentions[i]=new Activity(c);
		          }
		          c.moveToNext();
		      }
		      c.close();

		      return mentions;
		}
	    
	   public static Activity[] loadLikes(int activityId) { 
			  Cursor c = db().rawQuery("SELECT a.*,u.name FROM " 
						+ table_activity() + " a," + table_user() + " u "
						+ " WHERE a.userId=u.userId and a.activityId>0 and a.typeNumber=" + Activity.Type_Like + " and a.itemId=" + activityId
						+ " order by a.activityId",
						null);
			  
		      int numRows = c.getCount();
		      c.moveToFirst();
		      
		      Activity[] likes=new Activity[numRows];
		      
		      for (int i = 0; i < numRows; ++i) {
		          if (c.getString(0) != null) {
		        	  likes[i]=new Activity(c);
		        	  likes[i].setUserName(c.getString(c.getColumnCount()-1));
		          }
		          c.moveToNext();
		      }
		      c.close();

		      return likes;
		}
	   
	   public static Activity[] loadComments(int activityId) {  
			  Cursor c = db().rawQuery("SELECT a.*,u.name FROM " 
						+ table_activity() + " a," + table_user() + " u "
						+ " WHERE a.userId=u.userId and a.activityId>0 and a.typeNumber=" + Activity.Type_Comment + " and a.itemId=" + activityId
						+ " order by a.activityId",
						null);
			  
		      int numRows = c.getCount();
		      c.moveToFirst();
		      
		      Activity[] comments=new Activity[numRows];
		      
		      for (int i = 0; i < numRows; ++i) {
		          if (c.getString(0) != null) {
		        	  comments[i]=new Activity(c);
		        	  comments[i].setUserName(c.getString(c.getColumnCount()-1));
		          }
		          c.moveToNext();
		      }
		      c.close();

		      return comments;
		}
	   
	   
	   public static boolean alreadyLike(int activityId){
		   return getSelfLike(activityId) != null;
	   }
	   
	   public static Activity getSelfLike(int activityId){
	        Cursor c = db().query(table_activity(), null, "typeNumber=" + Activity.Type_Like + " AND itemId=" + activityId + " AND userId=" + MyBabyApp.currentUser.getUserId() , null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        Activity activity=null;
	        if (numRows > 0) {
	        	activity=new Activity(c);
	        }
	        c.close();

	        return activity;
	   }
	   
	    public static int friendActivityCount() {
	        Cursor c = db().query(table_activity(), null, "userId<>" + MyBabyApp.currentUser.getUserId() + " and typeNumber=" + Activity.Type_Post, null, null, null, null);
	        int numRows = c.getCount();
	        c.close();
	        return numRows;
	    }
	    
	    public static int mentionMeCount() {
	        Cursor c = db().query(table_activity(), null, "isMentionMe=1", null, null, null, null);
	        int numRows = c.getCount();
	        c.close();
	        return numRows;
	    }
	    

	    public static long getLastSyncActivityListDate(){
	    	return  MyBabyApp.getSharedPreferences().getLong("activityLastSyncDate",0);
	    }
	    
	    public static void setLastSyncActivityListDate(long time){
	    	Editor edit=MyBabyApp.getSharedPreferences().edit();
	    	edit.putLong("activityLastSyncDate", time);
	    	edit.commit();
	    }
	    
	    public static long getLastSyncMentionMeListDate(){
	    	return  MyBabyApp.getSharedPreferences().getLong("mentionMeLastSyncDate",0);
	    }
	    
	    public static void setLastSyncMentionMeListDate(long time){
	    	Editor edit=MyBabyApp.getSharedPreferences().edit();
	    	edit.putLong("mentionMeLastSyncDate", time);
	    	edit.commit();
	    }
    
		public static int getLastSyncMaxActivityId(){
			return  MyBabyApp.getSharedPreferences().getInt("lastSyncMaxActivityId",0);
		}
		
		public static void setLastSyncMaxActivityId(int activityId){
			Editor edit=MyBabyApp.getSharedPreferences().edit();
			edit.putInt("lastSyncMaxActivityId", activityId);
			edit.commit();
		}
		
		public static int getLastSyncMaxMentionMeActivityId(){
			return  MyBabyApp.getSharedPreferences().getInt("lastSyncMaxMentionMeActivityId",0);
		}
		
		public static void setLastSyncMaxMentionMeActivityId(int activityId){
			Editor edit=MyBabyApp.getSharedPreferences().edit();
			edit.putInt("lastSyncMaxMentionMeActivityId", activityId);
			edit.commit();
		}
		
		public static User getNewActivityUser(){
		    User user=UserRepository.getLastActivityFriendUser();
		    if(user == null){
		        return null;
		    }
		    
		    if(getLastSyncActivityListDate()>=user.getLastActivityTime()){
		        return null;
		    }else{
		        return user;
		    }
		}
		
	    
	    public static Activity loadOrCreateByInfo(  int activityId,
													String content,
										    		int typeNumber,
										    		int userId,
										    		long datetime_gmt,
										    		int itemId,
										    		int secondaryItemId,
										    		long lastSync){
	    	Activity activity=load(activityId);
			if(activity==null){
				activity = new Activity();
			}
			activity.setActivityId(activityId);
			activity.setContent(content);
			activity.setTypeNumber(typeNumber);
			activity.setUserId(userId);
			activity.setDatetime_gmt(datetime_gmt);
			activity.setItemId(itemId);
			activity.setSecondaryItemId(secondaryItemId);
			activity.setLastSync(lastSync);
			
			return activity;
		}
}
