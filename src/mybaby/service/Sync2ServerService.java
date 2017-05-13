package mybaby.service;

import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.models.diary.Media;
import mybaby.models.diary.MediaRepository;
import mybaby.models.diary.Post;
import mybaby.models.diary.PostRepository;
import mybaby.rpc.BlogRPC;
import mybaby.rpc.MediaRPC;
import mybaby.rpc.PostRPC;
import mybaby.rpc.UserRPC;
import mybaby.util.LogUtils;
import mybaby.util.Utils;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class Sync2ServerService extends Service {

    private static Object mCurrentSyncingObject = null;
    
    //启动服务的静态方法
    public static void startSync2Server(){
		LogUtils.e("startSync2Server");
    	if(MyBabyApp.currentUser == null){
    		Utils.LogV("MyBaby", "Sync2ServerService:未开启，不需要同步");
    		return; 
    	}
    	
    	if(mCurrentSyncingObject != null){
    		Utils.LogV("MyBaby", "Sync2ServerService:正在同步，不需要启动");
    		return; //正在同步，不需要启动
    	}
    	
        //恢复上传错误的
        PostRepository.recoveryUploadError();
        MediaRepository.recoveryUploadError();
	        
    	MyBabyApp.getContext().startService(new Intent(MyBabyApp.getContext(),Sync2ServerService.class));
    }
    
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public  int onStartCommand(Intent intent, int flags, int startId) {
        if (((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE))
        		.getActiveNetworkInfo() == null) {
    		stopSelf(); //没有连接网络
        }else if(MyBabyApp.currentUser.getUserId()<=0){ 
        	//先要匿名创建用户
			BlogRPC.anonymousSignUp(new XMLRPCCallback() {
	            @Override
	            public void onSuccess(long id, Object result){
	            	startSync();
	            }
	            @Override
	            public void onFailure(long id, XMLRPCException error){
	            	stopSelf();
	            }
	        });
		}else{
			startSync();
		}
        return START_NOT_STICKY;
    }

    private void startSync(){
    	//如果用户信息有修改则启动上传
    	if(MyBabyApp.currentUser.getBzInfoModified()){
    		UserRPC.updateUserInfo(null);
    	}
    	//新线程进行post和media的队列循环同步
        new Thread(){
            public void run() {
            	syncNextObject();
            }
		}.start();
    }
    
    
    private void syncNextObject(){
    	mCurrentSyncingObject=getWaitingSyncObject();

        if ( mCurrentSyncingObject != null ) {
        	Utils.LogV("准备同步",mCurrentSyncingObject.toString());
        	
            executeSyncObject(mCurrentSyncingObject);
        } else {
            this.stopSelf();
            Utils.LogV("同步完成", "Ye");
			asnkUpMediaEnd(null);
        }
    }

    private Object getWaitingSyncObject(){
    	Post post=null;
    	//宝宝，自己及家人
    	post=PostRepository.loadTop1PostForUpload(true);
    	if(post != null){
    		return post;
    	}	
    	//日记信息
    	post=PostRepository.loadTop1PostForUpload(false);
    	if(post != null){
    		return post;
    	}
    	
    	Media media=null;
    	//头像 － 不含背景
    	media=MediaRepository.loadTop1MediaForUpload(true);
    	if(media != null){
    		return media;
    	}
    	//照片 － 其他所有
    	if(needUploadMedia()){
	    	media=MediaRepository.loadTop1MediaForUpload(false);
	    	if(media != null){
	    		return media;
	    	}
    	}
    	
    	return null;
    }
    
    private void executeSyncObject(Object object){    	
    	//执行
    	try{
		    if(object.getClass().equals(Post.class)){
	    		Post post=(Post)object;
	    		if(post.getIsLocalDeleted()){
	    			PostRPC.deletePost(post);
	    		}else{
	    			PostRPC.uploadPost(post);
	    		}
	    	}else if(object.getClass().equals(Media.class)){
	    		Media media=(Media)object;
	    		if(media.getIsLocalDeleted()){
	    			MediaRPC.deleteMedia(media);
	    		}else{
	    			MediaRPC.uploadMedia(media);
	    			asnkUpMediaEnd(media);
	    			//star
	    		}
	    	}
		    Utils.LogV("同步一个对象完成", object.toString());
		    syncNextObject();

    	}catch ( Exception e) {
    		Utils.LogV("同步一个对象失败", "Sync2ServerService-executeSyncObject: " + e.getMessage());
    		//TODO 原本是发送谷歌统计异常的信息的  由于谷歌统计不使用 故删除：友盟统计是否需要发送待定
    	}
    }
    
    /**
	 * 单张图片上传完成同步广播
	 */
	public static void asnkUpMediaEnd(Media media) {
		Intent data = new Intent();
		if (null!=media){
			data.putExtra("mediaId", media.getId());
		}
		data.setAction(Constants.BroadcastAction.BroadcastAction_Post_UploadMediaByid_OK);
		LocalBroadcastManager.getInstance(MyBabyApp.getContext()).sendBroadcast(
				data);
	}
	
    public static boolean needUploadMedia(){
		return MyBabyApp.currentUser != null;
    	
    	
   /* 	
    	if(MyBaby.currentUser.getBzRegistered()){
    		return true;
    	}
    	
    	if(MyBaby.currentUser.getFrdIsOpen() && FriendRepository.friendCount()>0){
    		return true;
    	}
    	
    	return false;*/
    }

}
