package mybaby.models.diary;


import android.database.Cursor;

import java.io.Serializable;

import mybaby.ui.MyBabyApp;


public class Media implements Serializable{

	public enum remoteSync{		
		LocalModified,
		SyncError,
		SyncSuccess
	}

	private int id;
	private int pid;
	private String filePath;
	private String fileName;
	private String title;
	private String description;
	private String caption;
	private int width;
	private int height;
	private String mimeType;
	private boolean isVideo;
	private String fileURL;
	private int mediaId;
	private long date_created_gmt;
	private int remoteSyncFlag;
	private boolean isLocalDeleted;
	private String imageThumbnailRemoteURL;
	private String imageMediumRemoteURL;
	private String imageLargeRemoteURL;
	private int imageThumbnailWidth;
	private int imageMediumWidth;
	private int imageLargeWidth;
	private int imageThumbnailHeight;
	private int imageMediumHeight;
	private int imageLargeHeight;
	private int mediaOrder;
	private String assetURL="";
	private int userId;
	  
	private int parentId;



	
	public int getId(){
	    return id;
	}
	public void  setId(int id){
	    this.id = id;
	}
	public int getPid(){
	    return pid;
	}
	public void  setPid(int pid){
	    this.pid = pid;
	}
	public String getFilePath(){
		return filePath;
	}
	public void  setFilePath(String filePath){
	    this.filePath = filePath;
	}
	public String getFileName(){
	    return fileName;
	}
	public void  setFileName(String fileName){
	    this.fileName = fileName;
	}
	public String getTitle(){
	    return title;
	}
	public void  setTitle(String title){
	    this.title = title;
	}
	public String getDescription(){
	    return description;
	}
	public void  setDescription(String description){
	    this.description = description;
	}
	public String getCaption(){
	    return caption;
	}
	public void  setCaption(String caption){
	    this.caption = caption;
	}
	public int getWidth(){
	    return width;
	}
	public void  setWidth(int width){
	    this.width = width;
	}
	public int getHeight(){
	    return height;
	}
	public void  setHeight(int height){
	    this.height = height;
	}
	public String getMimeType(){
	    return mimeType;
	}
	public void  setMimeType(String mimeType){
	    this.mimeType = mimeType;
	}
	public boolean getIsVideo(){
	    return isVideo;
	}
	public void  setIsVideo(boolean isVideo){
	    this.isVideo = isVideo;
	}
	public String getFileURL(){
	    return fileURL;
	}
	public void  setFileURL(String fileURL){
	    this.fileURL = fileURL;
	}
	public int getMediaId(){
	    return mediaId;
	}
	public void  setMediaId(int mediaId){
	    this.mediaId = mediaId;
	}
	public long getDate_created_gmt(){
	    return date_created_gmt;
	}
	public void  setDate_created_gmt(long date_created_gmt){
	    this.date_created_gmt = date_created_gmt;
	}
	public int getRemoteSyncFlag(){
		return remoteSyncFlag;
	}
	public void  setRemoteSyncFlag(int remoteSyncFlag){
		this.remoteSyncFlag = remoteSyncFlag;
	}
	public boolean getIsLocalDeleted(){
		return isLocalDeleted;
	}
	public void  setIsLocalDeleted(boolean isLocalDeleted){
		this.isLocalDeleted = isLocalDeleted;
	}
	public String getImageThumbnailRemoteURL(){
	    return imageThumbnailRemoteURL;
	}
	public void  setImageThumbnailRemoteURL(String imageThumbnailRemoteURL){
	    this.imageThumbnailRemoteURL = imageThumbnailRemoteURL;
	}
	public String getImageMediumRemoteURL(){
	    return imageMediumRemoteURL;
	}
	public void  setImageMediumRemoteURL(String imageMediumRemoteURL){
	    this.imageMediumRemoteURL = imageMediumRemoteURL;
	}
	public String getImageLargeRemoteURL(){
	    return imageLargeRemoteURL;
	}
	public void  setImageLargeRemoteURL(String imageLargeRemoteURL){
	    this.imageLargeRemoteURL = imageLargeRemoteURL;
	}
	public int getImageThumbnailWidth(){
	    return imageThumbnailWidth;
	}
	public void  setImageThumbnailWidth(int imageThumbnailWidth){
	    this.imageThumbnailWidth = imageThumbnailWidth;
	}
	public int getImageMediumWidth(){
	    return imageMediumWidth;
	}
	public void  setImageMediumWidth(int imageMediumWidth){
	    this.imageMediumWidth = imageMediumWidth;
	}
	public int getImageLargeWidth(){
	    return imageLargeWidth;
	}
	public void  setImageLargeWidth(int imageLargeWidth){
	    this.imageLargeWidth = imageLargeWidth;
	}
	public int getImageThumbnailHeight(){
	    return imageThumbnailHeight;
	}
	public void  setImageThumbnailHeight(int imageThumbnailHeight){
	    this.imageThumbnailHeight = imageThumbnailHeight;
	}
	public int getImageMediumHeight(){
	    return imageMediumHeight;
	}
	public void  setImageMediumHeight(int imageMediumHeight){
	    this.imageMediumHeight = imageMediumHeight;
	}
	public int getImageLargeHeight(){
	    return imageLargeHeight;
	}
	public void  setImageLargeHeight(int imageLargeHeight){
	    this.imageLargeHeight = imageLargeHeight;
	}
	public int getMediaOrder(){
	    return mediaOrder;
	}
	public void  setMediaOrder(int mediaOrder){
	    this.mediaOrder = mediaOrder;
	}
	public String getAssetURL(){
	    return assetURL;
	}
	public void  setAssetURL(String assetURL){
	    this.assetURL = assetURL;
	}	  
	public int getUserId(){
		if(userId==0 && MyBabyApp.currentUser != null){
			return MyBabyApp.currentUser.getUserId(); //避免自动匿名注册后自己的userId从0改为x后各地方的缓存对象没有更新的问题
		}else{
			return userId;
		}
	}
	public void  setUserId(int userId){
		this.userId = userId;
	}
	
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
	
	public Media(){
		
	}
	
    public Media(Cursor c){
    	this.setId(c.getInt(0));
    	this.setPid(c.getInt(1));
    	this.setFilePath(c.getString(2));
    	this.setFileName(c.getString(3));
    	this.setTitle(c.getString(4));
    	this.setDescription(c.getString(5));
    	this.setCaption(c.getString(6));
    	this.setWidth(c.getInt(7));
    	this.setHeight(c.getInt(8));
    	this.setMimeType(c.getString(9));
    	this.setIsVideo(c.getInt(10)>0);
    	this.setFileURL(c.getString(11));
    	this.setMediaId(c.getInt(12));
    	this.setDate_created_gmt(c.getLong(13));
    	this.setRemoteSyncFlag(c.getInt(14));
    	this.setIsLocalDeleted(c.getInt(15)>0);
    	this.setImageThumbnailRemoteURL(c.getString(16));
    	this.setImageMediumRemoteURL(c.getString(17));
    	this.setImageLargeRemoteURL(c.getString(18));
    	this.setImageThumbnailWidth(c.getInt(19));
    	this.setImageMediumWidth(c.getInt(20));
    	this.setImageLargeWidth(c.getInt(21));
    	this.setImageThumbnailHeight(c.getInt(22));
    	this.setImageMediumHeight(c.getInt(23));
    	this.setImageLargeHeight(c.getInt(24));
    	this.setMediaOrder(c.getInt(25));
    	this.setAssetURL(c.getString(26));
    	this.setUserId(c.getInt(27));
    	this.setParentId(c.getInt(28));
    }	
	
    public Media(	int id,
		    		int pid,
		    		String filePath,
		    		String fileName,
		    		String title,
		    		String description,
		    		String caption,
		    		int width,
		    		int height,
		    		String mimeType,
		    		boolean isVideo,
		    		String fileURL,
		    		int mediaId,
		    		long date_created_gmt,
		    		int remoteSyncFlag,
		    		boolean isLocalDeleted,
		    		String imageThumbnailRemoteURL,
		    		String imageMediumRemoteURL,
		    		String imageLargeRemoteURL,
		    		int imageThumbnailWidth,
		    		int imageMediumWidth,
		    		int imageLargeWidth,
		    		int imageThumbnailHeight,
		    		int imageMediumHeight,
		    		int imageLargeHeight,
		    		int mediaOrder,
		    		String assetURL,
		    		int userId,
		    		int parentId) {
    	this.setId(id);
    	this.setPid(pid);
    	this.setFilePath(filePath);
    	this.setFileName(fileName);
    	this.setTitle(title);
    	this.setDescription(description);
    	this.setCaption(caption);
    	this.setWidth(width);
    	this.setHeight(height);
    	this.setMimeType(mimeType);
    	this.setIsVideo(isVideo);
    	this.setFileURL(fileURL);
    	this.setMediaId(mediaId);
    	this.setDate_created_gmt(date_created_gmt);
    	this.setRemoteSyncFlag(remoteSyncFlag);
    	this.setIsLocalDeleted(isLocalDeleted);
    	this.setImageThumbnailRemoteURL(imageThumbnailRemoteURL);
    	this.setImageMediumRemoteURL(imageMediumRemoteURL);
    	this.setImageLargeRemoteURL(imageLargeRemoteURL);
    	this.setImageThumbnailWidth(imageThumbnailWidth);
    	this.setImageMediumWidth(imageMediumWidth);
    	this.setImageLargeWidth(imageLargeWidth);
    	this.setImageThumbnailHeight(imageThumbnailHeight);
    	this.setImageMediumHeight(imageMediumHeight);
    	this.setImageLargeHeight(imageLargeHeight);
    	this.setMediaOrder(mediaOrder);
    	this.setAssetURL(assetURL);
    	this.setUserId(userId);
    	
    	this.setParentId(parentId);
    }
	
	


    

	public float imageWHRatio(){  //ͼƬ��߱�
		if(this.height !=0 && this.width !=0){
			return this.width*1f/this.height;
		}
		if(this.imageThumbnailHeight !=0 && this.imageThumbnailWidth !=0){
			return this.imageThumbnailWidth*1f/this.imageThumbnailHeight;
		}
		if(this.imageMediumHeight !=0 && this.imageMediumWidth !=0){
			return this.imageMediumWidth*1f/this.imageMediumHeight;
		}
		if(this.imageLargeHeight !=0 && this.imageLargeWidth !=0){
			return this.imageLargeWidth*1f/this.imageLargeHeight;
		}	
		return -1;
	}	


}

