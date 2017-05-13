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
好友动态：mybaby_to_friends_activity_list
 *
 */
public class FriendsActivityAction extends Action implements Serializable{


	public static String actionUrl="mybaby_to_friends_activity_list";
	public FriendsActivityAction() {
		super();
	}

	@Override
	public Action setData(String url,Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (url.contains(actionUrl)) {
			return new FriendsActivityAction();
		}else {
			return null;
		}
		
	}

	@Override
	public Boolean excute(Activity activity, UMSocialService webviewController,
					   WebView webView, ParentingPost parentingPost) {
		// TODO Auto-generated method stub
		CustomAbsClass.openFriendListActivity(activity);
		return true;
	}



	
	
}
