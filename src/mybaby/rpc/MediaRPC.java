package mybaby.rpc;


import android.content.Context;

import org.xmlrpc.android.XMLRPCException;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mybaby.ui.MyBabyApp;
import mybaby.models.UserRepository;
import mybaby.models.diary.Media;
import mybaby.models.diary.MediaRepository;
import mybaby.models.diary.Post;
import mybaby.models.diary.PostRepository;
import mybaby.util.MapUtils;
import mybaby.util.Utils;

public class MediaRPC extends BaseRPC {
	
	public static boolean uploadMedia(final Media media){
		boolean backTag=false;
		if(media.getMediaId()>0 
				|| media.getFilePath()==null || media.getFilePath().equals("")
				|| media.getRemoteSyncFlag() == Media.remoteSync.SyncSuccess.ordinal()
				|| media.getIsLocalDeleted()
				|| (media.getPid()<=0 && media.getParentId()<=0)){
			return backTag;
		}
		final Post post=PostRepository.load(media.getPid());
		if (media.getParentId()<=0) {
			if(post.getPostid()<=0 ){
	        	media.setRemoteSyncFlag(Media.remoteSync.SyncError.ordinal());
	        	MediaRepository.save(media);
				return backTag;
				}
		}
			
		
		
	    Map<String, Object> data = new HashMap<String, Object>();
	    data.put("name", media.getFileName());
	    data.put("type", media.getMimeType());
	    
	    byte[] bytes=getJPEGBytes(media.getFilePath());
	    if(bytes==null){
        	media.setRemoteSyncFlag(Media.remoteSync.SyncError.ordinal());
        	MediaRepository.save(media);
			return backTag;
	    }
	    data.put("bits", bytes);

	    
	    //0--普通 1--背景(废) 2--头像(废) 3--自己的头像 4--宝宝的头像
	    	int bz_type=0;
	    	if (post!=null) {
				if(post.getIsSelf()){
		    	bz_type=3;
		    }else if(post.getTypeNumber()==Post.type.Baby.ordinal()){
		    	bz_type=4;
		    		}
			}
		    
	    
	    //  2015-07-20 增加处理parentId
	    int bz_postid=0;
	    if (post!=null) {
	    	bz_postid=post.getPostid();
		}
	    if(bz_postid<=0){
	        if(media.getParentId()>0){
	        	bz_postid=media.getParentId();
	        }else{
	        	bz_postid=0;
	        }
	    }
	    
	    data.put("bz_type",bz_type );
	    data.put("bz_postid",bz_postid);
	    data.put("bz_order",media.getMediaOrder());
	    data.put("bz_assetURL",media.getAssetURL() );

		
	    Object[] params=extParams(new Object[]{data});
	    String xmlrpcMethod="wp.uploadFile";//xmlrpc中写死了这个名称，不能用别的
	    
	    try {
	    	Object obj=null;
			try {
				obj = getClient().call(xmlrpcMethod, params,createTempFile());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return backTag;
			}
	    	if (obj!=null) {
				backTag=true;
				
				//下面的东西有问题 ，暂不修复
				Map<?, ?> contentHash = (Map<?, ?>)obj;
				media.setRemoteSyncFlag(Media.remoteSync.SyncSuccess.ordinal());
	        	media.setMediaId(MapUtils.getMapInt(contentHash, "id"));
	        	media.setFileURL(MapUtils.getMapStr(contentHash, "url"));
	        	
	        	media.setImageThumbnailRemoteURL(MapUtils.getMapStr(contentHash, "imageThumbnailUrl"));
	        	media.setImageThumbnailHeight(MapUtils.getMapInt(contentHash, "imageThumbnailHeight"));
	        	media.setImageThumbnailWidth(MapUtils.getMapInt(contentHash, "imageThumbnailWidth"));
	        	
	        	media.setImageMediumRemoteURL(MapUtils.getMapStr(contentHash, "imageMediumUrl"));
	        	media.setImageMediumHeight(MapUtils.getMapInt(contentHash, "imageMediumHeight"));
	        	media.setImageLargeWidth(MapUtils.getMapInt(contentHash, "imageMediumWidth"));
	        	
	        	media.setImageLargeRemoteURL(MapUtils.getMapStr(contentHash, "imageLargeUrl"));
	        	media.setImageLargeHeight(MapUtils.getMapInt(contentHash, "imageLargeHeight"));
	        	media.setImageLargeWidth(MapUtils.getMapInt(contentHash, "imageLargeWidth"));
	        	
	        	MediaRepository.save(media);
	        	
	        	//同步自己对应的user头像  如果自己没有头像，则同步宝宝的头像
	            if(post.getIsSelf()
	               || (post.getTypeNumber()==Post.type.Baby.ordinal() 
	               		&&  MediaRepository.getForPId(PostRepository.loadSelfPerson(MyBabyApp.currentUser.getUserId()).getId()).length==0)){
	                MyBabyApp.currentUser.setAvatarThumbnailUrl(media.getImageThumbnailRemoteURL());
	                MyBabyApp.currentUser.setAvatarUrl(media.getImageLargeRemoteURL());
	                UserRepository.save(MyBabyApp.currentUser);
	            }
			}
            
            return backTag;
            
	    } catch ( Exception e) {
        	media.setRemoteSyncFlag(Media.remoteSync.SyncError.ordinal());
        	MediaRepository.save(media);
        	Utils.LogV("MyBaby", "XMLRPCException-MediaRPC-uploadMedia: " + e.getMessage());
	        return backTag;
	    }
	}	
	
	
	private static byte[] getJPEGBytes(String strJPEGFile){
		byte[] bytes;
		File file=new File(strJPEGFile);
        try {
            bytes = new byte[(int) file.length()];
        } catch (OutOfMemoryError er) {
            return null;
        }

        DataInputStream in = null;
        try {
            in = new DataInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            in.readFully(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return bytes;
	}
	
    // create temp file for media upload
	private static File createTempFile(){
        String tempFileName = "mb-" + System.currentTimeMillis();
        try {
        	MyBabyApp.getContext().openFileOutput(tempFileName, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            return null;
        }
        return MyBabyApp.getContext().getFileStreamPath(tempFileName);
	}
	
	
	public static boolean deleteMedia(final Media media) throws Exception{
		if(media.getMediaId()<=0 
				|| media.getRemoteSyncFlag() == Media.remoteSync.SyncSuccess.ordinal()
				|| !media.getIsLocalDeleted()){
			return false;
		}
		
	    
	    try {
	    
			Object[] params=extParams(new Object[]{media.getMediaId()});
			String xmlrpcMethod="bz.xmlrpc.deleteMediaObject";
			extracted(params, xmlrpcMethod);
			media.setMediaId(0);
			MediaRepository.save(media);
			
			MediaRepository.delete(media);
			
	        return true;
	    } catch ( Exception e) {
				media.setRemoteSyncFlag(Media.remoteSync.SyncError.ordinal());
				MediaRepository.save(media);
        	Utils.LogV("MyBaby", "XMLRPCException-MediaRPC-deleteMedia: " + e.getMessage());
	        return false;
	    } finally {
		}
	}


	private static void extracted(Object[] params, String xmlrpcMethod)
			throws XMLRPCException {
		getClient().call(xmlrpcMethod, params);
	}
}
