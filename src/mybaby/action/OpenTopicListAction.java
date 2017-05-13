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
 * 根据所分类显示动态列表
标识符：mybaby_to_topiclist
参数：id（分类id）
 *
 */
public class OpenTopicListAction extends Action implements Serializable{

	int cid;
	String cTitle="";
	public static String openTopicList="mybaby_to_topiclist";//根据所分类显示动态列表
	

	public OpenTopicListAction(int cid, String cTitle) {
		super();
		this.cid = cid;
		this.cTitle = cTitle;
	}


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


	public OpenTopicListAction() {
		super();
	}
	


@Override
public Action setData(String url,Map<String, Object> map) {
	// TODO Auto-generated method stub
	if (url.contains(openTopicList)) {
		//Map<String, Object> map=Action.getHeader(getReal_url());
		try {
			this.cid=Integer.parseInt((URLDecoder.decode(map.get("cid").toString(), "UTF-8")));
			this.cTitle= "#"+(URLDecoder.decode(map.get("ctitle").toString(), "UTF-8"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new OpenTopicListAction(cid,cTitle);
	}else {
		return null;
	}
}



@Override
public Boolean excute(Activity activity, UMSocialService webviewController,
				   WebView webView, ParentingPost parentingPost) {
	// TODO Auto-generated method stub
	CustomAbsClass.openTopicTitleList(activity, getCid(), getcTitle());//暂时没有title
	return true;
}


	
}
