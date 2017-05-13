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
 * 打开地点列表界面
标识符：mybaby_to_placelist
参数：id (地点id)
 *通过action唤起发帖、话题／地点列表支持直接传送话题id和title，客户端根据url中的id和title参数创建话题对象和地点对象，然后进入发帖、话题列表、地点列表界面:
http://www.hibb.me/mybaby_add_topic?cid=xxx&ctitle=xxxxx
 http://www.hibb.me/mybaby_to_topiclist?cid=xxx&ctitle=xxxxx
 http://www.hibb.me/mybaby_to_placelist?pid=xxx&ptitle=xxxxx
 */
public class OpenPlaceListAction extends Action implements Serializable{

	/**
	 * 
	 */
	public static String openPlacePage="mybaby_to_placelist";//打开地点列表界面
	private static final long serialVersionUID = 1L;
	int pid;
	String pTitle="";
	
	

	public OpenPlaceListAction(int pid, String pTitle) {
		super();
		this.pid = pid;
		this.pTitle = pTitle;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getpTitle() {
		return pTitle;
	}
	public void setpTitle(String pTitle) {
		this.pTitle = pTitle;
	}
	public OpenPlaceListAction() {
		super();
	}
@Override
public Action setData(String url,Map<String, Object> map) {
	// TODO Auto-generated method stub
	if (url.contains(openPlacePage)) {
		//Map<String, Object> map=Action.getHeader(getReal_url());
		try {
			this.pid=Integer.parseInt((URLDecoder.decode(map.get("pid").toString(), "UTF-8")));
			this.pTitle=URLDecoder.decode(map.get("ptitle").toString(), "UTF-8");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new OpenPlaceListAction(pid,pTitle);
	}else {
		return null;
	}
}
@Override
public Boolean excute(Activity activity, UMSocialService webviewController,
				   WebView webView, ParentingPost parentingPost) {
	// TODO Auto-generated method stub
	CustomAbsClass.openPlaceList(activity, getPid());
	return true;
}
}
