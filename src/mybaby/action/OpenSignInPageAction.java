package mybaby.action;

import android.app.Activity;
import android.content.Intent;
import android.webkit.WebView;
import android.widget.Toast;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.ui.MyBabyApp;
import mybaby.models.community.ParentingPost;
import mybaby.ui.user.RegisterActivity;

/**
 * 通过Action进入
 * Action格式为：mybaby_sign_in
 * 注：如果用户已注册则不要弹出
 */
public class OpenSignInPageAction extends Action  implements Serializable {
    public static String OpenSignInPageAction="mybaby_sign_in";
    public OpenSignInPageAction()
    {
        super();
    }
    @Override
    public Action setData(String url,Map<String, Object> map) {
        if (url.contains(OpenSignInPageAction)) {
            return new OpenSignInPageAction();
        }else {
            return null;
        }
    }

    @Override
    public Boolean excute(Activity activity, UMSocialService webviewController, WebView webView, ParentingPost parentingPost) {
        if (!MyBabyApp.currentUser.getBzRegistered()) {
            Intent intentSignIn = new Intent(activity, RegisterActivity.class);
            activity.startActivity(intentSignIn);
        }else {
            Toast.makeText(activity, "您已注册！", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
