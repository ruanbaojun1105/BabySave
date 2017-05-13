package mybaby.push;

import android.content.Context;
import android.os.AsyncTask;

import org.xutils.common.util.LogUtil;

import com.umeng.message.PushAgent;
import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.models.UserRepository;

public class UMengPushManager {
	private static PushAgent mPushAgent;

	public static void registerDevice(Context context) {
		if (MyBabyApp.currentUser == null) {
			return;
		}
		mPushAgent = PushAgent.getInstance(context);// 获取友盟推送服务实例
		// 应用程序启动统计
		// 参考集成文档的1.5.1.2
		// http://dev.umeng.com/push/android/integration#1_5_1
		mPushAgent.onAppStart();
		// mPushAgent.disable();
		// 开启推送
		mPushAgent.enable();
		/*if (StringUtils.isEmpty(MyBabyApp.currentUser.getuMengAndroidPushId())
				|| Utils.getAppVersion() != MyBabyApp.currentUser
						.getAndroidAppVersion()) {
			registerInBackground();
		} else {
			Utils.LogI("MyBaby", "No valid UMengPush APK found.");
		}*/
	}

	private static void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				if (mPushAgent == null) {
					mPushAgent = PushAgent.getInstance(MyBabyApp.getContext());// 获取友盟推送服务实例
					mPushAgent.onAppStart();
					// mPushAgent.disable();
					// 开启推送
					mPushAgent.enable();
				}
				String uMengAndroidPushId = mPushAgent.getRegistrationId();
				msg = "Device registered, registration ID="
						+ uMengAndroidPushId;

				MyBabyApp.currentUser.setuMengAndroidPushId(uMengAndroidPushId);
				MyBabyApp.currentUser.setAndroidAppVersion(MyBabyApp.version);
				MyBabyApp.currentUser.setBzInfoModified(true);
				UserRepository.save(MyBabyApp.currentUser);

				MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_UMeng_Push_Register_Done);
				LogUtil.e(msg);
				return msg;
			}
		}.execute(null, null, null);
	}
}
