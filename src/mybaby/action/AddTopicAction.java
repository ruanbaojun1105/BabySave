package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.ui.community.customclass.CustomAbsClass;

/**
 * 
 * @author bj
 * 发帖
标识符：mybaby_add_topic
参数：；
 *
 */
public class AddTopicAction extends Action implements Serializable{
	public static String openAddTopic="mybaby_add_topic";//发帖
	int cid;
	String cTitle="";
	
	
	public int getCid() {
		return cid;
	}


	public void setCid(int cid) {
		this.cid = cid;
	}


	public String getcTitle() {
		return cTitle;
	}


	public void setcTitle(String cTitle) {
		this.cTitle = cTitle;
	}


	public AddTopicAction(int cid, String cTitle) {
		super();
		this.cid = cid;
		this.cTitle = cTitle;
	}


	public AddTopicAction() {
		super();
	}


@Override
public Action setData(String url,Map<String, Object> map) {
	// TODO Auto-generated method stub
	if (url.contains(openAddTopic)) {
		//Map<String, Object> map=Action.getHeader(getReal_url());
		this.cid=0;
		this.cTitle="";
		try {
			this.cid=Integer.parseInt((URLDecoder.decode(map.get("cid").toString(), "UTF-8")));
			this.cTitle= "#"+(URLDecoder.decode(map.get("ctitle").toString(), "UTF-8"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new AddTopicAction(cid,cTitle);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new AddTopicAction(cid,cTitle);
		} catch (Exception e) {
			// TODO: handle exception
			return new AddTopicAction(cid,cTitle);
		}
		return new AddTopicAction(cid,cTitle);
	}else {
		return null;
	}
	
}



@Override
public Boolean excute(Activity activity, UMSocialService webviewController,
				   WebView webView, ParentingPost parentingPost) {
	// TODO Auto-generated method stub
	CustomAbsClass.starTopicEditIntent(activity, cid, cTitle, null);
	return true;
}

	
}
