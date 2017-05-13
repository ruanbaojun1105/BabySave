package mybaby.models.diary;

import mybaby.models.Repository;
import android.content.ContentValues;
import android.database.Cursor;

public class PostTagRepository extends Repository {
	   public static PostTag[] load(int pid) {

	        Cursor c = db().query(table_post_tag(), null, "pid=" + pid, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();
	        
	        PostTag[] postTags=new PostTag[numRows];
	        
	        for (int i = 0; i < numRows; ++i) {
	            if (c.getString(0) != null) {
	            	postTags[i]=new PostTag(c);
	            }
	            c.moveToNext();
	        }
	        c.close();

	        return postTags;
	   }

	   public static void save(PostTag postTag) {
	       if (postTag != null) {
	           db().delete(table_post_tag(), "pid=" + postTag.getPid() + " and tagPid=" + postTag.getTagPid(), null);
	           
	           ContentValues values = new ContentValues();

	           values.put("pid", postTag.getPid());
	           values.put("tagPid", postTag.getTagPid());

	           db().insert(table_post_tag(), null, values);
	       }
	   } 
	   
	   public static void save(PostTag[] postTags) {
    	   for(int i=0;i<postTags.length;i++){
    		   save(postTags[i]);
    	   }
	   } 
	   
	   public static boolean delete(int pid) {
		    db().delete(table_post_tag(), "pid=" + pid , null);
		    return true;
	   }
	   
	   public static boolean delete(int pid,int tagPid) {
		    db().delete(table_post_tag(), "pid=" + pid + " and tagPid=" + tagPid , null);
		    return true;
	   }
}
