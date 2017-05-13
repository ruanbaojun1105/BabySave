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
 * 单聊
标识符：mybaby_user_chat?im_user_id=xxx
参数：；
 *
 */
public class P2PChattingAction extends Action implements Serializable{

	public static String P2PChattingAction="mybaby_user_chat";//.聊天·

	String im_userid;
	String username;

	public String getIm_userid() {
		return im_userid;
	}

	public void setIm_userid(String im_userid) {
		this.im_userid = im_userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public P2PChattingAction(String im_userid, String username) {
		this.im_userid = im_userid;
		this.username = username;
	}

	public P2PChattingAction() {
		super();
	}

	@Override
	public Action setData(String url,Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (url.contains(P2PChattingAction)) {
			//Map<String, Object> map=Action.getHeader(getReal_url());
			this.im_userid=(String) map.get("im_userid");
			this.username=(String) map.get("username");
			return new P2PChattingAction(im_userid,username);
		}else {
			return null;
		}
		
	}

	@Override
	public Boolean excute(Activity activity, UMSocialService webviewController,
					   WebView webView, ParentingPost parentingPost) {
		// TODO Auto-generated method stub
		CustomAbsClass.starP2PChatting(activity,im_userid,username);
		return true;
	}

}
