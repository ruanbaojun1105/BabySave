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
 * 我的动态通知：mybaby_my_activity_notification  原消息界面，不显示关注通知、管理员通知
 *
 */
public class NotificationAction extends Action implements Serializable{


	public static String actionUrl="mybaby_my_activity_notification";
	public NotificationAction() {
		super();
	}

	@Override
	public Action setData(String url,Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (url.contains(actionUrl)) {
			return new NotificationAction();
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
		CustomAbsClass.starNotificationList(activity,0,getActiontTitle(),unread);
		return true;
	}



	
	
}
