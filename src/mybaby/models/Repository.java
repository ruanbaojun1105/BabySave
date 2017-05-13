package mybaby.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import mybaby.ui.MyBabyApp;
import mybaby.MyBabyDB;

public abstract class Repository {
	
	protected static SQLiteDatabase db(){
		return MyBabyApp.myBabyDB.db;
	}
	
	protected static String table_blog(){
		return MyBabyDB.BLOG_TABLE;
	}
	
	protected static String table_post(){
		return MyBabyDB.POSTS_TABLE;
	}
	
	protected static String table_media(){
		return MyBabyDB.MEDIA_TABLE;
	}
	
	protected static String table_post_tag(){
		return MyBabyDB.POST_TAG_TABLE;
	}
	
	protected static String table_user(){
		return MyBabyDB.USER_TABLE;
	}
	
	protected static String table_activity(){
		return MyBabyDB.ACTIVITY_TABLE;
	}
	
	protected static String table_friend(){
		return MyBabyDB.FRIEND_TABLE;
	}
	
	protected static String table_friend_request(){
		return MyBabyDB.FRIEND_REQUEST_TABLE;
	}
	protected static String table_recommend_friend(){
		return MyBabyDB.RECOMMEND_FRIEND_TABLE;
	}
	protected static String table_phone_contact(){
		return MyBabyDB.PHONE_CONTACT_TABLE;
	}	
	
	protected static String table_place(){
		return MyBabyDB.PLACE_TABLE;
	}
	
	protected int getLastInsertId(String table){
	    Cursor c = db().rawQuery("select last_insert_rowid() from " + table,null);   
	    
	    int id=0;   
        int numRows = c.getCount();
        if(numRows>0){
		    if(c.moveToFirst()) { 
		       id = c.getInt(0);
		    }
        }
	   
	    c.close();
	    return id;
    }
}
