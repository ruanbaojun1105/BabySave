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
 * 关闭页面
标识符：mybaby_view_controller_close
参数：；
 *
 */
public class TribeSystemManagerAction extends Action implements Serializable{


	public static String actionUrl="mybaby_tribe_system_manager";//关闭页面
	public TribeSystemManagerAction() {
		super();
	}

	@Override
	public Action setData(String url,Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (url.contains(actionUrl)) {
			return new TribeSystemManagerAction();
		}else {
			return null;
		}
		
	}

	@Override
	public Boolean excute(Activity activity, UMSocialService webviewController,
					   WebView webView, ParentingPost parentingPost) {
		// TODO Auto-generated method stub
		CustomAbsClass.starTribeSystemManager(activity);
		return true;
	}



	
	
}
