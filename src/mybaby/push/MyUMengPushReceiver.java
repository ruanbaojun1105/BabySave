package mybaby.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyUMengPushReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent arg1) {
		context.startService(new Intent(context, AlwayLiveService.class));
	}
}
