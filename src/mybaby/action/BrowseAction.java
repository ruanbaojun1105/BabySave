package mybaby.action;

import android.app.Activity;
import android.text.TextUtils;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import org.xutils.common.util.LogUtil;

import java.io.Serializable;
import java.util.Map;

import mybaby.Constants;
import mybaby.models.community.ParentingPost;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.parentingpost.ParentingWebViewActivity;
import mybaby.util.MapUtils;

/**
 * 
 * @author bj
 * 打开浏览器
标识符：mybaby_browse_url
参数：url
 *
 */
public class BrowseAction extends Action implements Serializable{
	public static String openWebview="mybaby_browse_url";//打开浏览器
	String openUrl;
	Boolean mbnewpage;

	public String getOpenUrl() {
		return openUrl;
	}

	public void setOpenUrl(String openUrl) {
		this.openUrl = openUrl;
	}

	public Boolean getMbnewpage() {
		return mbnewpage;
	}

	public void setMbnewpage(Boolean mbnewpage) {
		this.mbnewpage = mbnewpage;
	}


	public BrowseAction(String openUrl, Boolean mbnewpage) {
		this.openUrl = openUrl;
		this.mbnewpage = mbnewpage;
	}

	public BrowseAction() {
		super();
	}

@Override
public Action setData(String url,Map<String, Object> map) {
	// TODO Auto-generated method stub
	if (url.contains(openWebview)) {
		//Map<String, Object> map=getHeader(getUrl());
		this.openUrl=MapUtils.getMapStr(map,"url");
		this.mbnewpage= TextUtils.isEmpty(MapUtils.getMapStr(map,"mbnewpage"))?false:true;
		return new BrowseAction(openUrl,mbnewpage);
	}else {
		return null;
	}
	
}


@Override
public Boolean excute(Activity activity, UMSocialService webviewController,
				   WebView webView, ParentingPost parentingPost) {
	// TODO Auto-generated method stub
	if (Constants.WEBVIEW_PAGE_COUNTS_MAX > 0) {
		Constants.WEBVIEW_PAGE_COUNTS_MAX -= 1;
		if (Constants.WEBVIEW_PAGE_COUNTS_MAX <= 0) {
			Constants.WEBVIEW_PAGE_COUNTS_MAX = 0;
		}
		CustomAbsClass.starWebViewIntent(activity, getOpenUrl());
		return true;
	} else {
		if (activity instanceof ParentingWebViewActivity) {
			int mbtopbar = ((ParentingWebViewActivity) activity).getWebviewFragment().getMbtopbar();
			if (mbtopbar > 0) {
				CustomAbsClass.starWebViewIntent(activity, getOpenUrl());
				return true;
			} else {
				LogUtil.e( "已超过开启浏览器新页面最大数1111");
				webView.loadUrl(getOpenUrl());
				//Toast.makeText(activity, "已超过开启浏览器新页面最大数", Toast.LENGTH_SHORT).show();
			}
		} else {
			if (webView==null)
				CustomAbsClass.starWebViewIntent(activity, getOpenUrl());
			else
				webView.loadUrl(getOpenUrl());
			LogUtil.e( "已超过开启浏览器新页面最大数1111");
			//Toast.makeText(activity, "已超过开启浏览器新页面最大数", Toast.LENGTH_SHORT).show();
		}
		return true;//在当前开启的浏览器中处理
	}
}
	
}
