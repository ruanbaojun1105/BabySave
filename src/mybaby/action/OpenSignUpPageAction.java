package mybaby.action;

import java.io.Serializable;
import java.util.Map;

import com.umeng.socialize.controller.UMSocialService;

import android.app.Activity;
import android.content.Intent;
import android.webkit.WebView;
import android.widget.Toast;
import mybaby.ui.MyBabyApp;
import mybaby.models.community.ParentingPost;
import mybaby.ui.user.SignInUpActivity;

/**
 * 
 * @author bj
 * 打开登录界面
标识符：mybaby_to_signup
参数：；
 *
 */
public class OpenSignUpPageAction extends Action implements Serializable{

	public static String openResgust="mybaby_to_signup";//打开登录界面
	public OpenSignUpPageAction() {
		super();
	}

	@Override
	public Action setData(String url,Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (url.contains(openResgust)) {
			return new OpenSignUpPageAction();
		}else {
			return null;
		}
	}

	@Override
	public Boolean excute(Activity activity, UMSocialService webviewController,
					   WebView webView, ParentingPost parentingPost) {
		// TODO Auto-generated method stub
		if (!MyBabyApp.currentUser.getBzRegistered()) {
			Intent intentSignUp = new Intent(activity, SignInUpActivity.class);
			activity.startActivity(intentSignUp);
		}else {
			Toast.makeText(activity, "您已注册！", 0).show();
		}
		return true;
	}

	
}
