package mybaby.models;

import java.text.StringCharacterIterator;

import android.content.ContentValues;
import android.database.Cursor;

public class BlogRepository extends Repository {

	   public static Blog load(int id) {

	        Cursor c = db().query(table_blog(), null, "id=" + id, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        Blog blog=null;
	        if (numRows > 0) {
	        	blog=new Blog(c);
	        }
	        c.close();

	        return blog;
	    }
	   
	   public static int save(Blog blog) {
	       int returnValue = -1;
	       if (blog != null) {

	           ContentValues values = new ContentValues();

	           values.put("username", blog.getUsername());
	           values.put("password", blog.getPassword());
	           values.put("runService", blog.getRunService());
	           values.put("blogTypeNumber", blog.getBlogTypeNumber());
	           values.put("userId", blog.getUserId());

	           if(blog.getId()>0){
	           		int rows=db().update(table_blog(), values, "id=" + blog.getId(), null);
	           		if(rows>0){
	           			returnValue=blog.getId();
	           		}
	           }else{
	           		returnValue =(int)db().insert(table_blog(), null, values);
	           		if(returnValue>0){
	           			blog.setId(returnValue);
	           		}
	           }

	       }
	       return (returnValue);
	   } 
	   
	   
	    public static boolean delete(Blog blog) {
		      boolean returnValue = false;

		      int result = 0;
		      result = db().delete(table_blog(), "id=" + blog.getId(), null);

		      if (result == 1) {
		          returnValue = true;
		      }

		      return returnValue;
		  }


	   public static Blog getCurrentBlog() {

	        Cursor c = db().query(table_blog(), null, "blogTypeNumber=" + Blog.type.CurrUser.ordinal(), null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        Blog blog=null;
	        if (numRows > 0) {
	        	blog=new Blog(c);
	        }
	        c.close();

	        return blog;
	    }    
	   
	    public static boolean clear() {
		      db().delete(table_blog(), null, null);
		      return true;
		}
	    

    public static String addSlashes(String text) {
        final StringBuffer sb = new StringBuffer(text.length() * 2);
        final StringCharacterIterator iterator = new StringCharacterIterator(
                text);

        char character = iterator.current();

        while (character != StringCharacterIterator.DONE) {
            if (character == '"')
                sb.append("\\\"");
            else if (character == '\'')
                sb.append("\'\'");
            else if (character == '\\')
                sb.append("\\\\");
            else if (character == '\n')
                sb.append("\\n");
            else if (character == '{')
                sb.append("\\{");
            else if (character == '}')
                sb.append("\\}");
            else
                sb.append(character);

            character = iterator.next();
        }

        return sb.toString();
    }


    

}
