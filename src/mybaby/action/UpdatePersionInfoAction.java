package mybaby.action;

import android.app.Activity;
import android.content.Intent;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.ui.user.PersonEditActivity;
import mybaby.util.MapUtils;

/**
 * 
 * @author bj
 * 个人信息维护
 * 参数：id(postid)
 *
 */
public class UpdatePersionInfoAction extends Action implements Serializable{
	public static String actionUrl="mybaby_to_update_personinfo";//关闭页面
	int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public UpdatePersionInfoAction(int id) {
		super();
		this.id = id;
	}

	public UpdatePersionInfoAction() {
		super();
	}
	

@Override
public Action setData(String url,Map<String, Object> map) {
	// TODO Auto-generated method stub
	if (url.contains(actionUrl)){
		//Map<String, Object> map=getHeader(getReal_url());
		this.id= MapUtils.getMapInt(map,"id");
		return new UpdatePersionInfoAction(id);
	}else return null;
}


@Override
public Boolean excute(Activity activity, UMSocialService webviewController,
				   WebView webView, ParentingPost parentingPost) {
	// TODO Auto-generated method stub
	Intent intent = new Intent(activity,
			PersonEditActivity.class);
	intent.putExtra("personId", getId());
	activity.startActivity(intent);
	return true;
}


	
}
