package mybaby.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.alibaba.wxlib.util.SysUtil;

import java.util.Timer;
import java.util.TimerTask;

import me.hibb.mybaby.android.BuildConfig;
import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.models.BlogRepository;
import mybaby.models.UserRepository;
import mybaby.models.diary.Media;
import mybaby.models.diary.PostRepository;
import mybaby.models.person.Baby;
import mybaby.models.person.SelfPerson;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.util.StringUtils;
import mybaby.util.Utils;

public class NewPictureBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
    	 Utils.LogV("NewPictureBroadcastReceiver", intent.toString());
    	 
 		long oneHour = 60*60*1000;
 		long oneDay =BuildConfig.DEBUG  ? 1000*10 : oneHour*24;
    	 
    	 if(System.currentTimeMillis()-getLastUseTime()<oneDay*3
    			 || System.currentTimeMillis()-getLastNewPictureNotificationTime()<oneDay*3){
    		 return;
    	 }
    	
    	 new Timer().schedule(new TimerTask() { 
             @Override  
    	    public void run() {  
            	 try{
            		 addNotification();
            	 } catch ( Exception e) {  
            		 //目前已知在4.0模拟器中会出现
         	    	Utils.LogV("MyBaby", "NewPictureBroadcastReceiver addNotification Error: " + e.getMessage());
         	    }
    	    }   
    	 }, BuildConfig.DEBUG  ? 1000*30 : 1000*60*10); 
    	
         setLastNewPictureNotificationTime(System.currentTimeMillis());
    }
    
    
    //添加通知
	public static void addNotification(){
		if (SysUtil.isForeground()){
			return;
		}
		//初始化 － 因为程序有可能不在内存，所以不能使用缓存变量，要重新取
		if(MyBabyApp.currentBlog == null){
	        MyBabyApp.currentBlog=BlogRepository.getCurrentBlog();
	        if(MyBabyApp.currentBlog == null){
	        	return;
	        }
	        MyBabyApp.currentUser=UserRepository.load(MyBabyApp.currentBlog.getUserId());
		}
		
		Utils.LogI("NewPictureNotifiaction", MyBabyApp.currentUser.getName());
		
	   	//内容准备
		SelfPerson selfPerson=PostRepository.loadSelfPerson(MyBabyApp.currentUser.getUserId());
		Baby[] babies=PostRepository.loadBabies(MyBabyApp.currentUser.getUserId());
		String contentTitle=babies[0].getName() + " - " + babies[0].getAgeText(System.currentTimeMillis());

		contentTitle += ": ";
		String contentText=String.format("%1$s%2$s",selfPerson.getAppellation()==null?"":selfPerson.getAppellation()+", ",
													MyBabyApp.getContext().getString(R.string.did_you_take_any_photos)	);
		String tickerText=contentTitle + contentText;

	    String largeIconUri=null;
		Media avatar=babies[0].getAvatar();
		if(avatar != null && !StringUtils.isEmpty(avatar.getFilePath())){
			largeIconUri=avatar.getFilePath();
		}
		
		Utils.LogI("NewPictureNotifiaction", tickerText);
		
		//发送通知
        NotificationManager mn= (NotificationManager) MyBabyApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);    
        Notification.Builder builder = new Notification.Builder(MyBabyApp.getContext());
        Intent notificationIntent = new Intent(MyBabyApp.getContext(),MyBayMainActivity.class);//点击跳转位置                
        PendingIntent contentIntent = PendingIntent.getActivity(MyBabyApp.getContext(),0,notificationIntent,0);                
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.ic_launcher);
        if(!StringUtils.isEmpty(largeIconUri)){
            BitmapFactory.Options bfo = new BitmapFactory.Options();
            bfo.inJustDecodeBounds = false;
            bfo.inSampleSize=8;//只要全屏图的1/8
            try { 
            	Bitmap bitmap = BitmapFactory.decodeFile(largeIconUri, bfo);
            	if(bitmap!=null)
            		builder.setLargeIcon(bitmap);
            }catch (Exception e) {  
           	 	Utils.LogV("MyBaby", "获取通知头像错误: " + e.getMessage());
            }
        }
        builder.setTicker(tickerText); //测试通知栏标题
        builder.setContentText(contentText); //下拉通知啦内容
        builder.setContentTitle(contentTitle);//下拉通知栏标题
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        Notification notification = builder.getNotification();//4.0一定要用这个方法，不然报错，不能用 builder.build()
		mn.cancelAll();
        mn.notify((int)System.currentTimeMillis(),notification);
    }

	
	//最后新照片通知时间
	public static long getLastNewPictureNotificationTime(){
		return MyBabyApp.getSharedPreferences().getLong("lastNewPictureNotificationTime", 0);
	}
	public static void setLastNewPictureNotificationTime(long lngLastNewPictureNotificationTime){
		Editor edit=MyBabyApp.getSharedPreferences().edit();
		edit.putLong("lastNewPictureNotificationTime", lngLastNewPictureNotificationTime);
		edit.commit();
	}
	
	//最后使用时间
	public static long getLastUseTime(){
		return MyBabyApp.getSharedPreferences().getLong("lastUseTime", 0);
	}
	public static void setLastUseTime(long lngLastUseTime){
		Editor edit=MyBabyApp.getSharedPreferences().edit();
		edit.putLong("lastUseTime", lngLastUseTime);
		edit.commit();
	}
}
