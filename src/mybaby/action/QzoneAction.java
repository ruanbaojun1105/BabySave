package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.share.UmengShare;

/**
 * 
 * @author bj
 * 分享到QQ空间
标识符：mybaby_share_to_qzone
参数：imageUrl; title; content; link
 *
 */
public class QzoneAction extends ShareAbsAction implements Serializable{
	public static String openQzone="mybaby_share_to_qzone";//分享到QQ空间
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
	public QzoneAction(String imageUrl, String title, String content,
			String link) {
		super();
		this.imageUrl = imageUrl;
		this.title = title;
		this.content = content;
		this.link = link;
	}
	public QzoneAction() {
		super();
	}
	
@Override
public Action setData(String url,Map<String, Object> map) {
	// TODO Auto-generated method stub
	if (url.contains(openQzone)) {
		//Map<String, Object> map=Action.getHeader(getReal_url());
		this.imageUrl=(String) map.get("imageUrl");
		this.title=(String) map.get("title");
		this.content=(String) map.get("content");
		this.link=(String) map.get("link");
		return new QzoneAction(imageUrl, title, content, link);
	}else {
		return null;
	}
}

	@Override
	public void toShare(Activity activity, UMSocialService webviewController, WebView webView, ParentingPost parentingPost) {
		try {
			new UmengShare().directShare(activity, webView,webviewController, SHARE_MEDIA.QZONE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
