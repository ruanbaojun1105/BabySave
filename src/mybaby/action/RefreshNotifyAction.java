package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.models.community.ParentingPost;
import mybaby.util.MapUtils;

/**
 * 
 * @author bj
 * 提供action使得网页能够触发消息通知的刷新事件(action: mybaby_refresh_notify?unread=1或0
 */
public class RefreshNotifyAction extends Action implements Serializable{

	/**
	 *
	 */
	public static String RefreshNotifyAction="mybaby_refresh_notify";//打开地点列表界面
	private static final long serialVersionUID = 1L;
	int unread;

	public RefreshNotifyAction(int unread) {
		super();
		this.unread = unread;
	}
	public int getUnread() {
		return unread;
	}

	public void setUnread(int unread) {
		this.unread = unread;
	}

	public RefreshNotifyAction() {
		super();
	}
@Override
public Action setData(String url,Map<String, Object> map) {
	// TODO Auto-generated method stub
	if (url.contains(RefreshNotifyAction)) {
		//Map<String, Object> map=Action.getHeader(getReal_url());
		this.unread= MapUtils.getMapInt(map,"unread");
		return new RefreshNotifyAction(unread);
	}else {
		return null;
	}
}
@Override
public Boolean excute(Activity activity, UMSocialService webviewController,
				   WebView webView, ParentingPost parentingPost) {
	// TODO Auto-generated method stub
	//发送广播提示消息页面刷新
	MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_NotificationCagetory_Refush);
	return true;
}
}
