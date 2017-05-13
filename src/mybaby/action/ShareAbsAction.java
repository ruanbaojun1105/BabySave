package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;

import mybaby.models.community.ParentingPost;
import mybaby.share.UmengShare;

/**
 * 
 * @author bj
 * 分享继承类
标识符：mybaby_share_to_qq
参数：imageUrl; title; content; link
 *
 */
public abstract class ShareAbsAction extends Action implements Serializable{


	public abstract void toShare(Activity activity, UMSocialService webviewController,
								   WebView webView, ParentingPost parentingPost);
	@Override
	public Boolean excute(Activity activity, UMSocialService webviewController,
					   WebView webView, ParentingPost parentingPost) {
		// TODO Auto-generated method stub
		new UmengShare().setShareContent(activity,webviewController, UmengShare.setShareBean(
				parentingPost==null?this:parentingPost, activity));
		toShare(activity,webviewController,webView,parentingPost);
		return true;
	}
}
