package mybaby.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PhoneStatReceiver extends BroadcastReceiver {

	private String TAG = "tag";
	private int count = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Log.i(TAG, "手机开机了~~");
			//NativeRuntime.getInstance().startService(context.getPackageName() + "/mybaby.service.HostMonitorr", SysFileUtils.createRootPath());
		} else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
		}
	}

	/*private void putNotification(Context context, String phoneNum) {
		count++;
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification(R.drawable.ic_launcher, "号码拦截提示", System.currentTimeMillis() + 1000);
		n.flags = Notification.FLAG_AUTO_CANCEL;
		n.number = count;
		Intent it = new Intent(context, MyBayMainActivity.class);
		it.putExtra("name", "name:" + count);
		PendingIntent pi = PendingIntent.getActivity(context, count, it, PendingIntent.FLAG_CANCEL_CURRENT);
		n.setLatestEventInfo(context, "号码拦截", "号码:" + phoneNum, pi);
		nm.notify(count, n);
	}*/
}
