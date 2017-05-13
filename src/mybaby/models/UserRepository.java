package mybaby.models;

import android.content.ContentValues;
import android.database.Cursor;

public class UserRepository extends Repository {

	   public static User load(int userId) {

	        Cursor c = db().query(table_user(), null, "userId=" + userId, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        User user=null;
	        if (numRows > 0) {
	        	user=new User(c);
	        }
	        c.close();

	        return user;
	    }
	   
	   public static int save(User user) {
	       int returnValue = -1;
	       if (user != null) {

	           ContentValues values = new ContentValues();
	           values.put("userId", user.getUserId());
	           values.put("name", user.getName());
	           values.put("uniqueName", user.getUniqueName());
	           values.put("email", user.getEmail());
	           values.put("birthday", user.getBirthday());
	           values.put("avatarUrl", user.getAvatarUrl());
	           values.put("avatarThumbnailUrl", user.getAvatarThumbnailUrl());
	           values.put("genderNumber", user.getGenderNumber());
	           values.put("lastActivityTime_gmt", user.getLastActivityTime_gmt());
	           values.put("bzRegistered", user.getBzRegistered());
	           values.put("bzInfoModified", user.getBzInfoModified());
	           values.put("bzPhotoSubscriptionEndDate", user.getBzPhotoSubscriptionEndDate());
	           values.put("bzCreateDate", user.getBzCreateDate());
	           values.put("fb_user_id", user.getFb_user_id());
	           values.put("fb_name", user.getFb_name());
	           values.put("frdIsBindingContacts", user.getFrdIsBindingContacts());
	           values.put("frdIsOpen", user.getFrdIsOpen());
	           values.put("frdPrivacyTypeNumber", user.getFrdPrivacyTypeNumber());
	           values.put("frdTelephoneCountryCode", user.getFrdTelephoneCountryCode());
	           values.put("frdTelephone", user.getFrdTelephone());
	           values.put("frdAllowFindMeByPhone", user.getFrdAllowFindMeByPhone());
	           values.put("frdAllowFindMeByEmail", user.getFrdAllowFindMeByEmail());
	           values.put("frdAllowFindMeByFacebook", user.getFrdAllowFindMeByFacebook());
	           values.put("lastPostsSyncTime", user.getLastPostsSyncTime());
	           values.put("androidGCMRegId", user.getAndroidGCMRegId());
	           values.put("androidAppVersion", user.getAndroidAppVersion());
	           values.put("activityCount", user.getActivityCount());
	           values.put("uMengAndroidPushId", user.getuMengAndroidPushId());


			   values.put("im_userid", user.getImUserName());
			   values.put("im_userpass", user.getimUserPassword());
			   values.put("is_admin", user.isAdmin());

	           if(UserRepository.exist(user.getUserId())){
	           		int rows=db().update(table_user(), values, "userId=" + user.getUserId(), null);
	           		if(rows>0){
	           			returnValue=user.getUserId();
	           		}
	           }else{
	           		db().insert(table_user(), null, values);
	           		returnValue=user.getUserId();
	           }

	       }
	       return (returnValue);
	   } 
	   
	   
	    public static boolean delete(User user) {
		      boolean returnValue = false;

		      int result = 0;
		      result = db().delete(table_user(), "userId=" + user.getUserId(), null);

		      if (result == 1) {
		          returnValue = true;
		      }

		      return returnValue;
		  }
	    
	    public static boolean clear() {
		      db().delete(table_user(), null, null);
		      return true;
		}
	    
	    public static boolean exist(int userId) {
	        Cursor c = db().query(table_user(), null, "userId=" + userId, null,
	                null, null, null);
	        int numRows = c.getCount();
	        c.close();
			return numRows > 0;
	    }
	    
	    public static User loadOrCreateByInfo(int userId,
									    		String name,
									    		String uniqueName,
									    		String email,
									    		long birthday,
									    		String avatarUrl,
									    		String avatarThumbnailUrl,
									    		int genderNumber){
	    	User user=load(userId);
	    	if(user==null){
	    		user = new User();
	    	}
	    	user.setUserId(userId);
	    	user.setName(name);
	    	user.setUniqueName(uniqueName);
	    	user.setEmail(email);
	    	user.setBirthday(birthday);
	    	user.setAvatarUrl(avatarUrl);
	    	user.setAvatarThumbnailUrl(avatarThumbnailUrl);
	    	user.setGenderNumber(genderNumber);
	    	
	    	return user;
	    }
	    
	   public static User[] loadByUserIds(String userIds) {
	        Cursor c = db().query(table_user(), null, "userid IN (" + userIds + ")", null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        User[] users=new User[numRows];
	        for (int i = 0; i < numRows; ++i) {
	            if (c.getString(0) != null) {
	            	users[i]=new User(c);
	            }
	            c.moveToNext();
	        }
	        c.close();

	        return users;
	    }
	    
	    
	    public static User getLastActivityFriendUser(){
	  	  Cursor c = db().rawQuery("SELECT u.* FROM " 
					+ table_user() + " u," + table_friend() + " f "
					+ " WHERE u.userid=f.friendUserid and u.lastActivityTime_gmt>0"
					+ " order by u.lastActivityTime_gmt DESC"
					+ " LIMIT 1 ", null);
		  
		  int numRows = c.getCount();
	      c.moveToFirst();

	      User user=null;
	      if (numRows > 0) {
	      	user=new User(c);
	      }
	      c.close();

	      return user;
	    }
}
