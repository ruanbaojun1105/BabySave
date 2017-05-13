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
我关注的话题动态列表：mybaby_follow_topic_category_activity_list
 *
 */
public class FollowTopicCategoryActivityAction extends Action implements Serializable{


	public static String actionUrl="mybaby_follow_topic_category_activity_list";
	public FollowTopicCategoryActivityAction() {
		super();
	}

	@Override
	public Action setData(String url,Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (url.contains(actionUrl)) {
			return new FollowTopicCategoryActivityAction();
		}else {
			return null;
		}
		
	}

	@Override
	public Boolean excute(Activity activity, UMSocialService webviewController,
					   WebView webView, ParentingPost parentingPost) {
		// TODO Auto-generated method stub
		CustomAbsClass.starMyFollowTopicActivity(activity);
		return true;
	}



	
	
}
