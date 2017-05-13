package mybaby.push;

import mybaby.ui.MyBabyApp;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AlwayLiveService extends Service{

	public static void startAlwayService(){
		MyBabyApp.getContext().startService(new Intent(MyBabyApp.getContext(),AlwayLiveService.class));
	}
	
	public static void stopAlwayService(){
		MyBabyApp.getContext().stopService(new Intent(MyBabyApp.getContext(),AlwayLiveService.class));
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		UMengPushManager.registerDevice(MyBabyApp.getContext());
	}
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent intent = new Intent();
		intent.setClass(this, AlwayLiveService.class);
		startService(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		
		UMengPushManager.registerDevice(MyBabyApp.getContext());
		
		return START_STICKY;
		
	}
	
	@Override
	public void onStart(Intent intent, int startId)
	{
	// 再次动态注册广播
	IntentFilter localIntentFilter = new IntentFilter("android.intent.action.USER_PRESENT");
	localIntentFilter.setPriority(Integer.MAX_VALUE);// 整形最大值
	MyUMengPushReceiver searchReceiver = new MyUMengPushReceiver();
	registerReceiver(searchReceiver, localIntentFilter);

	super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	

}
