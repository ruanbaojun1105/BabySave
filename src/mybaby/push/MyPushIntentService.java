package mybaby.push;

import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import com.umeng.common.message.Log;
import com.umeng.message.UTrack;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

/**
 * Developer defined push intent service. 
 * Remember to call {@link com.umeng.message.PushAgent#setPushIntentServiceClass(Class)}. 
 * @author lucas
 *
 */
//完全自定义处理类
//参考文档的1.6.5
//http://dev.umeng.com/push/android/integration#1_6_5
public class MyPushIntentService extends UmengBaseIntentService{
	private static final String TAG = MyPushIntentService.class.getName();

	@Override
	protected void onMessage(Context context, Intent intent) {
		// 需要调用父类的函数，否则无法统计到消息送达
		super.onMessage(context, intent);
		try {
			//可以通过MESSAGE_BODY取得消息体
			String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
			UMessage msg = new UMessage(new JSONObject(message));
			Log.d(TAG, "message="+message);    //消息体
			Log.d(TAG, "custom="+msg.custom);    //自定义消息的内容
			Log.d(TAG, "title="+msg.title);    //通知标题
			Log.d(TAG, "text="+msg.text);    //通知内容
			
			// code  to handle message here
			// ...
			
			// 对完全自定义消息的处理方式，点击或者忽略
			boolean isClickOrDismissed = true;
			if(isClickOrDismissed) {
				//完全自定义消息的点击统计
				UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
			} else {
				//完全自定义消息的忽略统计
				UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
}
