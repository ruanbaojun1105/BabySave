package mybaby.action;

import java.io.Serializable;
import java.util.Map;

import com.umeng.socialize.controller.UMSocialService;

import android.app.Activity;
import android.webkit.WebView;
import android.widget.Toast;
import mybaby.Constants;
import mybaby.models.community.ParentingPost;

/**
 * 
 * @author bj
 * 关闭页面
标识符：mybaby_view_controller_close
参数：；
 *
 */
public class ControllerCloseAction extends Action implements Serializable{

	
	public static String openColsePage="mybaby_view_controller_close";//关闭页面
	public ControllerCloseAction() {
		super();
	}

	@Override
	public Action setData(String url,Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (url.contains(openColsePage)) {
			return new ControllerCloseAction();
		}else {
			return null;
		}
		
	}

	@Override
	public Boolean excute(Activity activity, UMSocialService webviewController,
					   WebView webView, ParentingPost parentingPost) {
		// TODO Auto-generated method stub
		if (Constants.WEBVIEW_PAGE_COUNTS_MAX<=5) {
			activity.finish();
		}else {
			Toast.makeText(activity, "browser counts has currented for 5 max.", 0).show();
			activity.finish();
		}
		return true;
	}



	
	
}
