package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.share.UmengShare;
import mybaby.util.Utils;

/**
 * 
 * @author bj
 * 打开分享菜单
标识符：mybaby_share_to_menu
参数：imageUrl; title; content; link
 *
 */
public class ShareMenuAction extends Action implements Serializable{
	public static String openShareMenu="mybaby_share_to_menu";//打开分享菜单
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
	public ShareMenuAction(String imageUrl, String title, String content,
			String link) {
		super();
		this.imageUrl = imageUrl;
		this.title = title;
		this.content = content;
		this.link = link;
	}
	public ShareMenuAction() {
		super();
	}
@Override
public Action setData(String url,Map<String, Object> map) {
	// TODO Auto-generated method stub
	if (url.contains(openShareMenu)) {
		//Map<String, Object> map=Action.getHeader(getReal_url());
		this.imageUrl=(String) map.get("imageUrl");
		this.title=(String) map.get("title");
		this.content=(String) map.get("content");
		this.link=(String) map.get("link");
		return new ShareMenuAction(imageUrl, title, content, link);
	}else {
		return null;
	}
	
}
@Override
public Boolean excute(Activity activity, UMSocialService webviewController,
				   final WebView webView, ParentingPost parentingPost) {
	// TODO Auto-generated method stub
	new UmengShare().setShareContent(activity,webviewController, UmengShare.setShareBean(
			parentingPost==null?this:parentingPost, activity));
	new UmengShare.OpenShare(activity,webviewController);
	webviewController.registerListener(new SnsPostListener() {

        @Override
        public void onStart() {
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
        	String showText = "分享成功";
            if (eCode != StatusCode.ST_CODE_SUCCESSED) {
                showText = "分享失败";
            }else {
            	if (webView!=null) {
					webView.loadUrl("javascript:mybaby_share_success()");
				}
			}
            //Toast.makeText(context, showText, Toast.LENGTH_SHORT).show();
            Utils.Loge(showText);
        }
    });
	return true;
}
}
