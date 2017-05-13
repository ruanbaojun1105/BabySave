package mybaby.models.friend;

import mybaby.models.Repository;
import android.content.ContentValues;
import android.database.Cursor;

public class RecommendFriendRepository extends Repository {

	   public static RecommendFriend load(int recommendFriendId) {

	        Cursor c = db().query(table_recommend_friend(), null, "recommendFriendId=" + recommendFriendId, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        RecommendFriend recommendFriend=null;
	        if (numRows > 0) {
	        	recommendFriend=new RecommendFriend(c);
	        }
	        c.close();

	        return recommendFriend;
	    }
	   
	   public static int save(RecommendFriend recommendFriend) {
	       int returnValue = -1;
	       if (recommendFriend != null) {

	           ContentValues values = new ContentValues();
	           
	           values.put("recommendFriendId", recommendFriend.getRecommendFriendId());
	           values.put("userId", recommendFriend.getUserId());
	           values.put("sourceTypeNumber", recommendFriend.getSourceTypeNumber());
	           values.put("recommendDescription", recommendFriend.getRecommendDescription());

	           if(RecommendFriendRepository.exist(recommendFriend.getRecommendFriendId()) ){
	           		int rows=db().update(table_recommend_friend(), values, "recommendFriendId=" + recommendFriend.getRecommendFriendId(), null);
	           		if(rows>0){
	           			returnValue=recommendFriend.getRecommendFriendId();
	           		}
	           }else{
	           		db().insert(table_recommend_friend(), null, values);
	           		returnValue=recommendFriend.getRecommendFriendId();
	           }

	       }
	       return (returnValue);
	   } 
	   
	   
	    public static boolean delete(RecommendFriend recommendFriend) {
		      boolean returnValue = false;

		      int result = 0;
		      result = db().delete(table_recommend_friend(), "recommendFriendId=" + recommendFriend.getRecommendFriendId(), null);

		      if (result == 1) {
		          returnValue = true;
		      }

		      return returnValue;
		}
	    
	    public static boolean deleteByUserId(int userId) {
		      db().delete(table_recommend_friend(), "userId=" + userId, null);
		      return true;
		}
	    
	    public static boolean exist(int recommendFriendId) {
	        Cursor c = db().query(table_recommend_friend(), null, "recommendFriendId=" + recommendFriendId, null,
	                null, null, null);
	        int numRows = c.getCount();
	        c.close();
			return numRows > 0;
	    }
	    
	    public static int recommendFriendCount() {
	        Cursor c = db().query(table_recommend_friend(), null, null, null, null, null, null);
	        int numRows = c.getCount();
	        c.close();
	        return numRows;
	    }
	    
	    public static int maxRecommendFriendId() throws Exception{
	        Cursor c = db().rawQuery("SELECT max(recommendFriendId) FROM " + table_recommend_friend(),null);
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
	    
	    public static RecommendFriend loadOrCreateByInfo(	int recommendFriendId,
										    		int userId,
										    		int sourceTypeNumber,
										    		String recommendDescription){
	    	RecommendFriend recommendFriend=load(recommendFriendId);
			if(recommendFriend==null){
				recommendFriend = new RecommendFriend();
			}
			recommendFriend.setRecommendFriendId(recommendFriendId);
			recommendFriend.setUserId(userId);
			recommendFriend.setSourceTypeNumber(sourceTypeNumber);
	    	recommendFriend.setRecommendDescription(recommendDescription);
			
			return recommendFriend;
		}
	    
	    public static RecommendFriend[] loadAll() {
	        Cursor c = db().query(table_recommend_friend(), null, null, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        RecommendFriend[] recommendFriends=new RecommendFriend[numRows];
	        for (int i = 0; i < numRows; ++i) {
	            if (c.getString(0) != null) {
	            	recommendFriends[i]=new RecommendFriend(c);
	            }
	            c.moveToNext();
	        }
	        c.close();

	        return recommendFriends;
	    }
}
