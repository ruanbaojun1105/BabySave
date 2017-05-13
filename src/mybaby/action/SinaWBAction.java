package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;

/**
 * 
 * @author bj
 *分享到微博
标识符：mybaby_share_to_weibo
参数：imageUrl; title; content; link
 *
 */
public class SinaWBAction extends ShareAbsAction implements Serializable{
	public static String openWb="mybaby_share_to_weibo";//分享到微博
	String imageUrl; 
	String title; 
	String content; 
	String link;
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public SinaWBAction(String imageUrl, String title, String content,
			String link) {
		super();
		this.imageUrl = imageUrl;
		this.title = title;
		this.content = content;
		this.link = link;
	}
	public SinaWBAction() {
		super();
	}
@Override
public Action setData(String url,Map<String, Object> map) {
	// TODO Auto-generated method stub
	if (url.contains(openWb)) {//微博
		//Map<String, Object> map=Action.getHeader(getReal_url());
		this.imageUrl=(String) map.get("imageUrl");
		this.title=(String) map.get("title");
		this.content=(String) map.get("content");
		this.link=(String) map.get("link");
		return new SinaWBAction(imageUrl, title, content, link);
	}else {
		return null;
	}
}

	@Override
	public void toShare(Activity activity, UMSocialService webviewController, WebView webView, ParentingPost parentingPost) {
//怎么写
	}
}
