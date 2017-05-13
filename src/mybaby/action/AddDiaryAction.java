package mybaby.action;

import java.io.Serializable;
import java.util.Map;

import com.umeng.socialize.controller.UMSocialService;

import android.app.Activity;
import android.webkit.WebView;
import mybaby.models.community.ParentingPost;
import mybaby.ui.community.customclass.CustomAbsClass;

/**
 * 
 * @author bj
 * 写日记
标识符：mybaby_add_diary
参数：；
 *
 */
public class AddDiaryAction extends Action implements Serializable{

	public static String openDiarty="mybaby_add_diary";//.写日记·
	public AddDiaryAction() {
		super();
	}

	@Override
	public Action setData(String url,Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (url.contains(openDiarty)) {
			return new AddDiaryAction();
		}else {
			return null;
		}
		
	}

	@Override
	public Boolean excute(Activity activity, UMSocialService webviewController,
			WebView webView, ParentingPost parentingPost) {
		// TODO Auto-generated method stub
		CustomAbsClass.starPostEdit(activity);
		return true;
	}



	
}
