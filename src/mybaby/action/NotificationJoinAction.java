package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.ui.community.customclass.CustomAbsClass;

/**
 * 
 * @author bj
我参与的动态通知：mybaby_join_activity_notification  原消息界面只显示我参与的
 *
 */
public class NotificationJoinAction extends Action implements Serializable{


	public static String actionUrl="mybaby_join_activity_notification";
	public NotificationJoinAction() {
		super();
	}

	@Override
	public Action setData(String url,Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (url.contains(actionUrl)) {
			return new NotificationJoinAction();
		}else {
			return null;
		}
		
	}

	@Override
	public Boolean excute(Activity activity, UMSocialService webviewController,
					   WebView webView, ParentingPost parentingPost) {
		// TODO Auto-generated method stub
		int unread=0;
		if (getSerializable_data() instanceof Integer)
			unread=(Integer) getSerializable_data();
		CustomAbsClass.starNotificationList(activity, 1,getActiontTitle(),unread);
		return true;
	}



	
	
}
