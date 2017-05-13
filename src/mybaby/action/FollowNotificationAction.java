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
关注通知：mybaby_follow_notification 原消息界面只显示关注通知
 *
 */
public class FollowNotificationAction extends Action implements Serializable{


	public static String actionUrl="mybaby_follow_notification";//
	public FollowNotificationAction() {
		super();
	}

	@Override
	public Action setData(String url,Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (url.contains(actionUrl)) {
			return new FollowNotificationAction();
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
		CustomAbsClass.starNotificationList(activity,2,getActiontTitle(),unread);
		return true;
	}

}
