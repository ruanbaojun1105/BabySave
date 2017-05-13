package mybaby.models.diary;

import java.util.UUID;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;

import mybaby.ui.MyBabyApp;
import mybaby.models.Repository;
import mybaby.models.person.Baby;
import mybaby.models.person.FamilyPerson;
import mybaby.models.person.Person;
import mybaby.models.person.SelfPerson;


public class PostRepository extends Repository {
	   

	   public static Post load(int id) {

	        Cursor c = db().query(table_post(), null, "id=" + id, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        Post post=null;
	        if (numRows > 0) {
	        	post=new Post(c);
	        }
	        c.close();

	        return post;
	    }

	   
	   public static int save(Post post) {
	       int returnValue = -1;
	       if (post != null) {

	           ContentValues values = new ContentValues();
	           if(post.getPostid()>0){  //避免缓存覆盖上传完成的
	        	   values.put("postid", post.getPostid());
	           }
	           values.put("dateCreated", post.getDateCreated());
	           values.put("date_created_gmt", post.getDate_created_gmt());
	           values.put("description", post.getDescription());
	           if(post.getUserid()>0){ //避免缓存覆盖上传完成的
	        	   values.put("userid", post.getUserid());
	           }
	           values.put("remoteSyncFlag", post.getRemoteSyncFlag());
	           values.put("isLocalDeleted", post.getIsLocalDeleted());
	           values.put("typeNumber", post.getTypeNumber());
	           values.put("bookTheme", post.getBookTheme());
	           values.put("birthday", post.getBirthday());
	           values.put("genderNumber", post.getGenderNumber());
	           values.put("isSelf", post.getIsSelf());
	           values.put("personTypeNumber", post.getPersonTypeNumber());
	           values.put("timelinePostToBookBeginDate", post.getTimelinePostToBookBeginDate());
	           values.put("timelinePostToBookEndDate", post.getTimelinePostToBookEndDate());
	           values.put("page", post.getPage());
	           values.put("orderNumber", post.getOrderNumber());
	           values.put("privacyTypeNumber", post.getPrivacyTypeNumber());
	           values.put("guid", post.getGuid());
	           values.put("status", post.getStatus());
	           
	           values.put("placeObjId", post.getPlaceObjId());

	           if(post.getId()>0){
	           		int rows=db().update(table_post(), values, "id=" + post.getId(), null);
	           		if(rows>0){
	           			returnValue=post.getId();
	           		}
	           }else{
	           		returnValue =(int)db().insert(table_post(), null, values);
	           		if(returnValue>0){
	           			post.setId(returnValue);
	           		}
	           }
	       }
	       return (returnValue);
	   } 
	
  public static boolean deletePost(Post post) {
      if(post.getPostid()>0){
    	  post.setIsLocalDeleted(true);
    	  post.setRemoteSyncFlag(Post.remoteSync.LocalModified.ordinal());
    	  save(post);
      }else{
    	  PostTagRepository.delete(post.getId());
    	  MediaRepository.delete(post.getId());
	      db().delete(table_post(), "id=" + post.getId(), null);
      }

      return true;
  }

  public static Post loadTop1PostForUpload(boolean isPerson) {  
	  Cursor c = db().rawQuery("SELECT * FROM " 
				+ table_post() 
				+ " WHERE status<>'" + Post.Status_Draft + "' and remoteSyncFlag=? and userid=? and typeNumber" + (isPerson ? "<>" : "=") + "?" 
				+ " order by id DESC"
				+ " LIMIT 1 ", 
				new String[] {String.valueOf(Post.remoteSync.LocalModified.ordinal()),String.valueOf(MyBabyApp.currentUser.getUserId()),String.valueOf(Post.type.Post.ordinal())});
	  
	  int numRows = c.getCount();
      c.moveToFirst();

      Post post=null;
      if (numRows > 0) {
      	post=new Post(c);
      }
      c.close();

      return post;
  }

  
  public static void recoveryUploadError(){
 	 ContentValues values = new ContentValues(); 
 	 values.put("remoteSyncFlag", Post.remoteSync.LocalModified.ordinal());

      try {
          db().update(table_post(), values, "userId=" + MyBabyApp.currentUser.getUserId() + " and remoteSyncFlag = " + Media.remoteSync.SyncError.ordinal(), null);
      } catch (Exception e) {
          e.printStackTrace();
      }
  }
  
  public static Post[] loadPostsByUser(int userId) {    	
	  Cursor c = db().query(table_post(),null,
              "userId=" + userId + " and typeNumber=0 and isLocalDeleted=0",
              null, null, null, "date_created_gmt DESC,id DESC");

      int numRows = c.getCount();
      c.moveToFirst();
      
      Post[] posts=new Post[numRows];
      
      for (int i = 0; i < numRows; ++i) {
          if (c.getString(0) != null) {
        	  posts[i]=new Post(c);
          }
          c.moveToNext();
      }
      c.close();

      return posts;
  }
  
  public static Post[] loadPostsByPerson(int personId) {    	
	  Cursor c = db().rawQuery("SELECT p.* FROM " 
			  					+ table_post() + " p," + table_post_tag() + " pt "
			  					+ " WHERE p.id=pt.pid And p.typeNumber=0 and p.isLocalDeleted=0 and pt.tagPid=?"
			  					+ " order by p.date_created_gmt DESC,p.id DESC", 
			  					new String[] {String.valueOf(personId)});

      int numRows = c.getCount();
      c.moveToFirst();
      
      Post[] posts=new Post[numRows];
      
      for (int i = 0; i < numRows; ++i) {
          if (c.getString(0) != null) {
        	  posts[i]=new Post(c);
          }
          c.moveToNext();
      } 
      c.close();

      return posts;
  }



  public static Person[] loadPersons(int userId) {
	  Cursor c = db().query(table_post(),null,
              "userId=" + userId + " and typeNumber In (1,2) and isLocalDeleted=0",
              null, null, null, "typeNumber,orderNumber,id");

      int numRows = c.getCount();
      c.moveToFirst();
      
      Person[] persons=new Person[numRows];
      
      for (int i = 0; i < numRows; ++i) {
          if (c.getString(0) != null) {
        	  persons[i]=(new Post(c)).createPerson();
          }
          c.moveToNext();
      }
      c.close();

      return persons;
  }
  
  public static SelfPerson loadSelfPerson(int userId) {
       Cursor c = db().query(table_post(),null,
               "userId=" + userId + " and typeNumber=2 and isLocalDeleted=0 and isSelf=1",
               null, null, null, null);

       int numRows = c.getCount();
       c.moveToFirst();

       Post post=null;
       if (numRows > 0) {
       		post=new Post(c);
       }
       c.close();
       
       if(post != null){
    	   return SelfPerson.createByPost(post);
       }else{
    	   return null;
       }
   }

  public static Baby[] loadBabies(int userId) {
	  Cursor c = db().query(table_post(),null,
              "userId=" + userId + " and typeNumber=1 and isLocalDeleted=0",
              null, null, null, "orderNumber,id");

      int numRows = c.getCount();
      c.moveToFirst();
      
      Baby[] babies=new Baby[numRows];
      
      for (int i = 0; i < numRows; ++i) {
          if (c.getString(0) != null) {
        	  babies[i]=Baby.createByPost(new Post(c));
          }
          c.moveToNext();
      }
      c.close();

      return babies;
  }
  
  public static FamilyPerson[] loadFamilyPersons(int userId) {
	  Cursor c = db().query(table_post(),null,
              "userId=" + userId + " and typeNumber=2 and isLocalDeleted=0 and isSelf=0",
              null, null, null, "orderNumber,id");

      int numRows = c.getCount();
      c.moveToFirst();
      
      FamilyPerson[] familyPersons=new FamilyPerson[numRows];
      
      for (int i = 0; i < numRows; ++i) {
          if (c.getString(0) != null) {
        	  familyPersons[i]=FamilyPerson.createByPost(new Post(c));
          }
          c.moveToNext();
      }
      c.close();

      return familyPersons;
  }
  /**
   * 
   * @param 返回实际日记总数
   * @return
   */
 public static int countRealPost() {
	  Cursor c = db().query(table_post(),null,
              "typeNumber=? and userId=? and isLocalDeleted=0 and status<>?",
              new String[] {String.valueOf(Post.type.Post.ordinal()),String.valueOf(MyBabyApp.currentUser.getUserId()),Post.Status_Draft},
              null, null, null);
      int numRows = c.getCount();
      c.close();
      return numRows;
  }
  
  public static Post loadByPostId(int postid) {
      Cursor c = db().query(table_post(), null, "postid=" + postid, null, null, null, null);

      int numRows = c.getCount();
      c.moveToFirst();

      Post post=null;
      if (numRows > 0) {
      	post=new Post(c);
      }
      c.close();

      return post;
  }
  
  public static int getIdByPostId(int postid) {
      Cursor c = db().query(table_post(), null, "postid=" + postid, null, null, null, null);

      int numRows = c.getCount();
      c.moveToFirst();
      
      int id=0;
      if(numRows>0){
    	  id=c.getInt(0);
      }
      c.close();

      return id;
  }
  
  public static void deleteUploadedPosts(int userId) {
      db().delete(table_post(), "userId=" + userId + " AND remoteSyncFlag = " + Post.remoteSync.SyncSuccess.ordinal(), null);
  }


  public static void clearPosts(int userId) {
      // delete existing values
      db().delete(table_post(), "userId=" + userId, null);
  }

  public static int validDiaryCount(){
      Cursor c = db().query(table_post(),new String[]{"id"}, "isLocalDeleted=0 and status<>'" + Post.Status_Draft + "' and typeNumber=" + Post.type.Post.ordinal() + " and userId=" + MyBabyApp.currentUser.getUserId(), null, null, null, null);

      int numRows = c.getCount();
      c.close();
      return numRows;
  }
  
  public static Post createDiary(String description,
							  		Object[] images,//fileUrs or Bitmap
							  		long date_created,
							  		int privacyType,Person[] persons){
		Post diary=new Post();
		
		diary.setUserid(MyBabyApp.currentUser.getUserId());
		diary.setGuid(UUID.randomUUID().toString());
		
		diary.setDescription(description);
		diary.setDateCreated(date_created);
		diary.setStatus(Post.Status_Draft);
		diary.setPrivacyTypeNumber(privacyType);
		
		int pid=save(diary);
		
		//media
		if(images != null){
			for(int i=0;i<images.length;i++){
				if(images[i].getClass()==Bitmap.class){
					MediaRepository.createMedia(pid, (Bitmap)images[i],i+1);
				}else{
					MediaRepository.createMedia(pid, (String)images[i],i+1);
				}
			}
		}
		
		//tags
		for(int i=0;i<persons.length;i++){
			PostTag tag=new PostTag(pid,persons[i].getId());
			PostTagRepository.save(tag);
		}
		
		return diary;
  }
}
