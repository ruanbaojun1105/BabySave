package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.util.MapUtils;

/*增加动态详情的action:
		mybaby_activity_detail?id=xxx*/
public class ActivityDetailAction extends Action implements Serializable{
	private static final long serialVersionUID = 1L;
	public static String actionUrl="mybaby_activity_detail";//
	int activityId;

	public ActivityDetailAction(int activityId) {
		this.activityId = activityId;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public ActivityDetailAction() {
		super();
	}

@Override
public Action setData(String url,Map<String, Object> map) {
	// TODO Auto-generated method stub
	if (url.contains(actionUrl)) {
		//Map<String, Object> map=Action.getHeader(getReal_url());
		this.activityId= MapUtils.getMapInt(map,"id",0);
		Action action=new ActivityDetailAction(activityId);
		return action;
	}else {
		return null;
	}
}

@Override
public Boolean excute(Activity activity, UMSocialService webviewController,
				   WebView webView, ParentingPost parentingPost) {
	// TODO Auto-generated method stub
	CustomAbsClass.openDetailPageByActivityId(activity,getActivityId());
	return  true;
}


}
