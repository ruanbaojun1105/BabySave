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
 *打开特定网页：mybaby_browse_url?url=xxx 不同于网页中的打开新页面，这是在原生代码中唤起网页浏览，用来支持各种h5扩展功能
 *
 */
public class BabySpaceAction extends Action implements Serializable{
	private static final long serialVersionUID = 1L;
	public static String actionUrl="mybaby_my_timeline";//

	public BabySpaceAction() {
		super();
	}

@Override
public Action setData(String url,Map<String, Object> map) {
	// TODO Auto-generated method stub
	if (url.contains(actionUrl)) {
		return new BabySpaceAction();
	}else {
		return null;
	}
}

@Override
public Boolean excute(Activity activity, UMSocialService webviewController,
				   WebView webView, ParentingPost parentingPost) {
	// TODO Auto-generated method stub
	CustomAbsClass.starHomeTimelineActivity(activity);
	return  true;
}


}
