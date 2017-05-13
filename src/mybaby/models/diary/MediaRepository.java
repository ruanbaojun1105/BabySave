package mybaby.models.diary;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import me.hibb.mybaby.android.BuildConfig;
import mybaby.Constants;
import mybaby.models.Repository;
import mybaby.models.person.Person;
import mybaby.ui.MyBabyApp;
import mybaby.util.Utils;


public class MediaRepository extends Repository {

	   public static Media load(int id) {

	        Cursor c = db().query(table_media(), null, "id=" + id, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        Media media=null;
	        if (numRows > 0) {
	        	media=new Media(c);
	        }
	        c.close();

	        return media;
	    }
	   
	   public static void save(Media[] medias) {
		   for(int i=0;i<medias.length;i++){
			   save(medias[i]);
		   }
	   }
	   
	   public static int save(Media media) {
	       int returnValue = -1;
	       if (media != null) {

	           ContentValues values = new ContentValues();

	           values.put("pid", media.getPid());
	           values.put("filePath", media.getFilePath());
	           values.put("fileName", media.getFileName());
	           values.put("title", media.getTitle());
	           values.put("description", media.getDescription());
	           values.put("caption", media.getCaption());
	           values.put("width", media.getWidth());
	           values.put("height", media.getHeight());
	           values.put("mimeType", media.getMimeType());
	           values.put("isVideo", media.getIsVideo());
	           values.put("fileURL", media.getFileURL());
	           values.put("mediaId", media.getMediaId());
	           values.put("date_created_gmt", media.getDate_created_gmt());
	           values.put("remoteSyncFlag", media.getRemoteSyncFlag());
	           values.put("isLocalDeleted", media.getIsLocalDeleted());
	           values.put("imageThumbnailRemoteURL", media.getImageThumbnailRemoteURL());
	           values.put("imageMediumRemoteURL", media.getImageMediumRemoteURL());
	           values.put("imageLargeRemoteURL", media.getImageLargeRemoteURL());
	           values.put("imageThumbnailWidth", media.getImageThumbnailWidth());
	           values.put("imageMediumWidth", media.getImageMediumWidth());
	           values.put("imageLargeWidth", media.getImageLargeWidth());
	           values.put("imageThumbnailHeight", media.getImageThumbnailHeight());
	           values.put("imageMediumHeight", media.getImageMediumHeight());
	           values.put("imageLargeHeight", media.getImageLargeHeight());
	           values.put("mediaOrder", media.getMediaOrder());
	           values.put("assetURL", media.getAssetURL());
	           values.put("userId", media.getUserId());
	           
	           values.put("parentId", media.getParentId());

	           if(media.getId()>0){
	           		int rows=db().update(table_media(), values, "id=" + media.getId(), null);
	           		if(rows>0){
	           			returnValue=media.getId();
	           		}
	           }else{
               		returnValue=(int)db().insert(table_media() , null, values);
               		if(returnValue>0){
               			media.setId(returnValue);
               		}
	           }
	       }
	       return (returnValue);
	   } 
	   

	    public static boolean delete(Media media) {
			if (BuildConfig.DEBUG) {
				if (media.getFilePath() != null) {
					//ɾ�������ļ�
					File file = new File(media.getFilePath());
					if (!file.exists() || !file.delete()) {
						Utils.LogV("MyBaby", "delete media file fail: fileName= " + media.getFilePath());
					}
				}
			}
			if(media.getMediaId()>0){
				media.setIsLocalDeleted(true);
				media.setRemoteSyncFlag(Media.remoteSync.LocalModified.ordinal());
				save(media);
			}else{
				db().delete(table_media(), "id=" + media.getId(), null);
			}
			return true;
		  }
	
	    public static boolean delete(int pid) {
	    	Media[] medias=getForPId(pid);
	    	
	    	for(int i=0;i<medias.length;i++){
	    		delete(medias[i]);
	    	}
	    	
	    	return true;
		  }
	    
	    public static int mediaCount(int userId) {
	        Cursor c = db().query(table_media(), null, "isLocalDeleted=0 and userid="+userId, null, null, null, null);
	        int numRows = c.getCount();
	        c.close();
	        return numRows;
	    }

    public static Media[] getForPId(int pId) {
        Cursor c = db().query(table_media(), null, "isLocalDeleted=0 and pid=" + pId, null,
                null, null, "mediaOrder,id");
        int numRows = c.getCount();
        c.moveToFirst();
        Media[] mediaFiles = new Media[numRows];
        for (int i = 0; i < numRows; i++) {
            mediaFiles[i] = new Media(c);
            c.moveToNext();
        }
        c.close();

        return mediaFiles;
    }
    public static Media[] getForParentId(int parentId) {
        Cursor c = db().query(table_media(), null, "isLocalDeleted=0 and parentId=" + parentId, null,
                null, null, "mediaOrder,id");
        int numRows = c.getCount();
        c.moveToFirst();
        Media[] mediaFiles = new Media[numRows];
        for (int i = 0; i < numRows; i++) {
            mediaFiles[i] = new Media(c);
            c.moveToNext();
        }
        c.close();

        return mediaFiles;
    }
 
    public static int getCountForPId(int pId) {
        Cursor c = db().query(table_media(), null, "isLocalDeleted=0 and pid=" + pId, null,
                null, null, null);
        int numRows = c.getCount();
        c.close();

        return numRows;
    }

    
    public static Media getForMediaId(String mediaId) {
        Cursor c = db().query(table_media(), null, "mediaId=" + mediaId, null,
                null, null, null);
        int numRows = c.getCount();
        if(numRows>0){
        	c.moveToFirst();
            Media mf= new Media(c);
            c.close();
            return mf;
        }else{
        	c.close();
        	return null;
        }
    }

    
    public static int getMaxOrderForPId(int pId) {
        Cursor c = db().rawQuery("SELECT max(mediaOrder) FROM " + table_media() + " WHERE isLocalDeleted=0 and pId=?", new String[] {String.valueOf(pId)});
        int numRows = c.getCount();
        if(numRows>0){
        	c.moveToFirst();
        	int maxOrder=c.getInt(0);
            c.close();
            return maxOrder;
        }else{
        	c.close();
        	return 0;
        }
    }

    public static int getMaxOrderForParentId(int parentId) {
        Cursor c = db().rawQuery("SELECT max(mediaOrder) FROM " + table_media() + " WHERE isLocalDeleted=0 and parentId=?", new String[] {String.valueOf(parentId)});
        int numRows = c.getCount();
        if(numRows>0){
        	c.moveToFirst();
        	int maxOrder=c.getInt(0);
            c.close();
            return maxOrder;
        }else{
        	c.close();
        	return 0;
        }
    }
    
    public static Media loadTop1MediaForUpload(boolean onlyAvatar) {  
  	    Cursor c = db().rawQuery("SELECT m.* FROM " 
			  			    + table_media() + " m," + table_post() + " p "
							+ " WHERE m.pid=p.id And p.postId>0 and p.status<>'" + Post.Status_Draft + "' and m.remoteSyncFlag=? and m.userid=?"
							+ (onlyAvatar ? " and p.typeNumber in (1,2) and m.mediaOrder <> " + Person.Background_Media_Order  : "") //自己，家人和宝宝的头像
			  				+ " order by m.id DESC"
			  				+ " LIMIT 1 ", 
			  				new String[] {String.valueOf(Media.remoteSync.LocalModified.ordinal()), String.valueOf(MyBabyApp.currentUser.getUserId())});
  	  
        int numRows = c.getCount();
        c.moveToFirst();

        Media media=null;
        if (numRows > 0) {
        	media=new Media(c);
        }
        c.close();

        return media;
    }
    
    public static void recoveryUploadError(){
    	 ContentValues values = new ContentValues();
    	 values.put("remoteSyncFlag", Media.remoteSync.LocalModified.ordinal());
    	 
    	 db().update(table_media(), values, "userId=" + MyBabyApp.currentUser.getUserId() + " and remoteSyncFlag = " + Media.remoteSync.SyncError.ordinal(), null);
    }
    
//   
//    /** For a given blogId, get the first media files **/
//    public static Cursor getFirstForUser(int userId) {
//        return db().rawQuery("SELECT id as _id, * FROM " + table_media() + " WHERE userId=? AND " 
//                + "(uploadState IS NULL OR uploadState IN ('uploaded', 'queued', 'failed', 'uploading')) ORDER BY (uploadState=?) DESC, date_created_gmt DESC LIMIT 1", new String[] { Integer.toString(userId) , "uploading" });
//    }
//    
//    /** For a given blogId, get all the media files **/
//    public static Cursor getForUser(int userId) {
//        return db().rawQuery("SELECT id as _id, * FROM " + table_media() + " WHERE userId=? AND "
//                + "(uploadState IS NULL OR uploadState IN ('uploaded', 'queued', 'failed', 'uploading')) ORDER BY (uploadState=?) DESC, date_created_gmt DESC", new String[] { Integer.toString(userId) , "uploading" });
//    }
//
//    /** For a given blogId, get all the media files with searchTerm **/
//    public static Cursor getForUser(int userId, String searchTerm) {
//        // Currently on MyBaby.com, the media search engine only searches the content. 
//        // We'll match this.
//        
//        String term = searchTerm.toLowerCase(Locale.getDefault());
//        return db().rawQuery("SELECT id as _id, * FROM " + table_media() + " WHERE userId=? AND title LIKE ? AND (uploadState IS NULL OR uploadState ='uploaded') ORDER BY (uploadState=?) DESC, date_created_gmt DESC", new String[] { Integer.toString(userId) , "%" + term + "%", "uploading" });
//    }
//    
//    /** For a given blogId, get the media file with the given media_id **/
//    public static Cursor get(int userId, String mediaId) {
//        return db().rawQuery("SELECT * FROM " + table_media() + " WHERE userId=? AND mediaId=?", new String[] { Integer.toString(userId) , mediaId });
//    }
//    
//    public static int getMediaCountAll(int userId) {
//        Cursor cursor = getForUser(userId);
//        int count = cursor.getCount();
//        cursor.close();
//        return count;
//    }
//
//
//    public static Cursor getMediaImagesForUser(int userId) {
//        return db().rawQuery("SELECT id as _id, * FROM " + table_media() + " WHERE userId=? AND "
//                + "(uploadState IS NULL OR uploadState IN ('uploaded', 'queued', 'failed', 'uploading')) AND mimeType LIKE ? ORDER BY (uploadState=?) DESC, date_created_gmt DESC", new String[] { Integer.toString(userId), "image%", "uploading" });
//    }
//    
//    /** Ids in the filteredIds will not be selected **/
//    public static Cursor getMediaImagesForUser(int userId, ArrayList<String> filteredIds) {
//        
//        String mediaIdsStr = "";
//        
//        if (filteredIds != null && filteredIds.size() > 0) {
//            mediaIdsStr = "AND mediaId NOT IN (";
//            for (String mediaId : filteredIds) {
//                mediaIdsStr += "'" + mediaId + "',";
//            }
//            mediaIdsStr = mediaIdsStr.subSequence(0, mediaIdsStr.length() - 1) + ")";
//        }
//        
//        return db().rawQuery("SELECT id as _id, * FROM " + table_media() + " WHERE blogId=? AND "
//                + "(uploadState IS NULL OR uploadState IN ('uploaded', 'queued', 'failed', 'uploading')) AND mimeType LIKE ? " + mediaIdsStr + " ORDER BY (uploadState=?) DESC, date_created_gmt DESC", new String[] { blogId, "image%", "uploading" });
//    }
//
//    public static int getMediaCountImages(String blogId) {
//        return getMediaImagesForBlog(blogId).getCount();
//    }
//
//    public static Cursor getMediaUnattachedForBlog(String blogId) {
//        return db().rawQuery("SELECT id as _id, * FROM " + table_media() + " WHERE blogId=? AND " +
//                "(uploadState IS NULL OR uploadState IN ('uploaded', 'queued', 'failed', 'uploading')) AND pid=0 ORDER BY (uploadState=?) DESC, date_created_gmt DESC", new String[] { blogId, "uploading" });
//    }
//    
//    public static int getMediaCountUnattached(String blogId) {
//        return getMediaUnattachedForBlog(blogId).getCount();
//    }
//    
//    public static Cursor getForBlog(String blogId, long startDate, long endDate) {
//        return db().rawQuery("SELECT id as _id, * FROM " + table_media() + " WHERE blogId=? AND (uploadState IS NULL OR uploadState ='uploaded') AND (date_created_gmt >= ? AND date_created_gmt <= ?) ", new String[] { blogId , String.valueOf(startDate), String.valueOf(endDate) });
//    }
//    
//    /** For a given blogId, get all the media files for upload **/
//    public static Cursor getForUpload(String blogId) {
//        return db().rawQuery("SELECT id as _id, * FROM " + table_media() + " WHERE blogId=? AND uploadState IN ('uploaded', 'queued', 'failed', 'uploading') ORDER BY date_created_gmt ASC", new String[] { blogId });
//    }
//    
//    
//   
//    public static void deleteMediaForPost(Post post) {
//
//        db().delete(table_media(), "blogId='" + post.getBlogID() + "' AND pId=" + post.getId(), null);
//
//    }
//

//    
//    /** Update a media file to a new upload state **/
//    public static void updateMediaUploadState(String blogId, String mediaId, String uploadState) {
//        if (blogId == null || blogId.equals(""))
//            return;
//        
//        ContentValues values = new ContentValues();
//        if (uploadState == null) values.putNull("uploadState");
//        else values.put("uploadState", uploadState);
//        
//        if (mediaId == null) {
//            db().update(table_media(), values, "blogId=? AND (uploadState IS NULL OR uploadState ='uploaded')", new String[] { blogId });
//        } else {
//            db().update(table_media(), values, "blogId=? AND mediaId=?", new String[] { blogId, mediaId });            
//        }
//    }
//    
//    public static void updateMedia(String blogId, String mediaId, String title, String description, String caption) {
//        if (blogId == null || blogId.equals("")) {
//            return;
//        }
//        
//        ContentValues values = new ContentValues();
//        
//        if (title == null || title.equals("")) {
//            values.put("title", "");
//        } else {
//            values.put("title", title);            
//        }
//        
//        if (title == null || title.equals("")) {
//            values.put("description", "");
//        } else {
//            values.put("description", description);
//        }
//        
//        if (caption == null || caption.equals("")) {
//            values.put("caption", "");
//        } else {
//            values.put("caption", caption);
//        }
//        
//        db().update(table_media(), values, "blogId = ? AND mediaId=?", new String[] { blogId, mediaId });
//    }
//
//    /** 
//     * For a given blogId, set all uploading states to failed.
//     * Useful for cleaning up files stuck in the "uploading" state.  
//     **/
//    public static void setMediaUploadingToFailed(String blogId) {
//        if (blogId == null || blogId.equals(""))
//            return; 
//        
//        ContentValues values = new ContentValues();
//        values.put("uploadState", "failed");
//        db().update(table_media(), values, "blogId=? AND uploadState=?", new String[] { blogId, "uploading" });
//    }
//    
//    /** For a given blogId, clear the upload states in the upload queue **/
//    public static void clearMediaUploaded(String blogId) {
//        if (blogId == null || blogId.equals(""))
//            return;
//        
//        ContentValues values = new ContentValues();
//        values.putNull("uploadState");
//        db().update(table_media(), values, "blogId=? AND uploadState=?", new String[] { blogId, "uploaded" });
//    }
//
//    /** Delete a media item from a blog locally **/
//    public static void deleteMediaFile(String blogId, String mediaId) {
//        db().delete(table_media(), "blogId=? AND mediaId=?", new String[] { blogId, mediaId });
//    }
//
//    /** Mark media files for deletion without actually deleting them. **/
//    public static void setMediaMarkedForDelete(String blogId, List<String> ids) {
//        // This is for queueing up files to delete on the server
//        for (String id : ids)
//            updateMediaUploadState(blogId, id, "delete");
//    }
//    
//    /** Mark media files as deleted without actually deleting them **/
//    public static void setMediaMarkedForDeleted(String blogId) {
//        // This is for syncing our files to the server:
//        // when we pull from the server, everything that is still 'deleted' 
//        // was not downloaded from the server and can be removed via deleteFilesMarkedForDeleted()
//        updateMediaUploadState(blogId, null, "deleted");
//    }
//    
//    /** Delete files marked as deleted **/
//    public static void deleteMediasMarkedForDeleted(String blogId) {
//        db().delete(table_media(), "blogId=? AND uploadState=?", new String[] { blogId, "deleted" });
//    }
//    
    

//	  public static Media getMediaUploadQueue(int userId) {
//	        Cursor c = db().rawQuery("SELECT * FROM " + table_media() + " WHERE isLocalDeleted=0 AND remoteSyncFlag=? AND userId=? LIMIT 1", new String[] {String.valueOf(Media.remoteSync.LocalModified.ordinal()),String.valueOf(userId)}); 
//
//	        int numRows = c.getCount();
//	        c.moveToFirst();
//
//	        Media media=null;
//	        if (numRows > 0) {
//	        	media=new Media(c);
//	        }
//	        c.close();
//
//	        return media;
//	  }
//	  
//
//    public static Media getMediaDeleteQueueItem(int userId) {
//        Cursor c = db().rawQuery("SELECT * FROM " + table_media() + " WHERE isLocalDeleted=1 AND userId=? LIMIT 1", new String[] {String.valueOf(userId)}); 
//
//        int numRows = c.getCount();
//        c.moveToFirst();
//
//        Media media=null;
//        if (numRows > 0) {
//        	media=new Media(c);
//        }
//        c.close();
//
//        return media;
//    }
    
    
    public static void asyncCreateMedias(final int pid,final String[] fileUrls,final MediasCreateDone mediasCreateDone){
        new Thread(){
            public void run() {
            	Media[] medias=new Media[fileUrls.length];
            	int maxOrder=MediaRepository.getMaxOrderForPId(pid);
				for(int i=0;i<fileUrls.length;i++){
					maxOrder++;
					medias[i]=MediaRepository.createMedia(pid, fileUrls[i], maxOrder);
				}
				if(mediasCreateDone != null){
					mediasCreateDone.onDone(medias);
				}
            }
		}.start();
    }
    
    public static Media createMedia(int pid,Bitmap bitmap,int order){
    	String targetFileUrl=createMyBabyMediaFile(bitmap);
    	return createMedia(pid,0,"",targetFileUrl,order);
    }
    
    public static Media createMedia(int pid,String fileUrl){
    	return createMedia(pid,fileUrl,0);
    }
    
    public static Media createMedia(int pid,String sourceFileUrl,int order){
    	String targetFileUrl=createMyBabyMediaFile(sourceFileUrl);
    	return createMedia(pid,0,sourceFileUrl,targetFileUrl,order);
    }
    
    public static void asyncCreateMediasByParentId(final int parentId,final String[] fileUrls,final MediasCreateDone mediasCreateDone){
        new Thread(){
            public void run() {
            	Media[] medias=new Media[fileUrls.length];
            	int maxOrder=MediaRepository.getMaxOrderForParentId(parentId);
				for(int i=0;i<fileUrls.length;i++){
					maxOrder++;
					medias[i]=MediaRepository.createMediaByParentId(parentId, fileUrls[i], maxOrder);
				}
				if(mediasCreateDone != null){
					mediasCreateDone.onDone(medias);
				}
            }
		}.start();
    }
    
    public static Media createMediaByParentId(int parentId,Bitmap bitmap,int order){
    	String targetFileUrl=createMyBabyMediaFile(bitmap);
    	return createMedia(0,parentId,"",targetFileUrl,order);
    }
    
    public static Media createMediaByParentId(int parentId,String fileUrl){
    	return createMediaByParentId(parentId,fileUrl,0);
    }
    
    public static Media createMediaByParentId(int parentId,String sourceFileUrl,int order){
    	String targetFileUrl=createMyBabyMediaFile(sourceFileUrl);
    	return createMedia(0,parentId,sourceFileUrl,targetFileUrl,order);
    }
    
    private static Media createMedia(int pid,int parentId,String sourceFileUrl,String targetFileUrl,int order){
    	if(targetFileUrl==null){
    		return null;
    	}
        String fileName = new String(targetFileUrl).replaceAll("^.*/([A-Za-z0-9_-]+)\\.\\w+$", "$1");
        String fileType = new String(targetFileUrl).replaceAll(".*\\.(\\w+)$", "$1").toLowerCase();
        
        Media media = new Media();
        media.setFileName(fileName + "." + fileType);
        media.setFilePath(targetFileUrl);
        media.setAssetURL(sourceFileUrl);
        
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileType);
        if (mimeType.startsWith("image")) {
            // get width and height
            BitmapFactory.Options bfo = new BitmapFactory.Options();
            bfo.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(targetFileUrl, bfo);
            media.setWidth(bfo.outWidth);
            media.setHeight(bfo.outHeight);
        }
        media.setMimeType(mimeType);
        
        media.setUserId(MyBabyApp.currentUser.getUserId());
        media.setPid(pid);
        media.setParentId(parentId);
        media.setMediaOrder(order);
        media.setDate_created_gmt(System.currentTimeMillis());
        
        MediaRepository.save(media);

    	return media;
    }
    

    
    private static String createMyBabyMediaFile(String fileUrl){
    	//��ȡԴͼƬ�ߴ�
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileUrl, bfo);
        
        //����Ŀ��ͼƬ��С����
        int imageMaxSize=Math.max(bfo.outWidth,bfo.outHeight);
    	float screenMaxSize=Math.max(MyBabyApp.screenWidth, MyBabyApp.screenHeight);

    	//改为长边最大1024,不按屏幕尺寸 
        if(imageMaxSize<Constants.PHOTO_MAX_SIZE /*|| imageMaxSize<screenMaxSize*/){
        	bfo.inSampleSize=1;
        }else{
        	bfo.inSampleSize=(int) Math.floor(imageMaxSize/Constants.PHOTO_MAX_SIZE);
        }
        bfo.inJustDecodeBounds = false;
        
        Utils.LogI("MyBaby", "imageMaxSize:" + imageMaxSize + "  screenMaxSize:" + screenMaxSize + " inSampleSize:" + bfo.inSampleSize);
        
        //������ͼƬ
        String newFileUrl=null;
        try { 
        	Bitmap bitmap = BitmapFactory.decodeFile(fileUrl, bfo); 
        	newFileUrl=createMyBabyMediaFile(bitmap);
        }catch (Exception e) {  
        	Utils.LogV("MyBaby", "createMyBabyMediaFile fail: fileName= " + newFileUrl);
        }

    	return newFileUrl;
    }
    
    private static String createMyBabyMediaFile(Bitmap bitmap){
        String newFileUrl=null;
        try {
        	newFileUrl=createNewFileUrl();
			File file = new File(newFileUrl);
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
				out.flush();
				out.close();
			} else {
				Utils.LogV("MyBaby", "saveImage fail: fileName= " + newFileUrl);
			}
        }catch (Exception e) {  
        	Utils.LogV("MyBaby", "saveImage fail: fileName= " + newFileUrl);
        }

    	return newFileUrl;
    }
    
    private static String createNewFileUrl(){
    	String filePath = MyBabyApp.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + UUID.randomUUID().toString() + ".jpg";
		return filePath;
    }
    

    public interface MediasCreateDone {
    	void onDone(Media[] medias);
    }
    
}
