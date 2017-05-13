package mybaby.ui.community.parentingpost;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.callback.CallbackContext;
import com.alibaba.sdk.android.login.WebViewService;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import java.util.Map;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.action.Action;
import mybaby.models.community.ParentingPost;
import mybaby.share.UmengShare;
import mybaby.ui.MyBabyApp;
import mybaby.ui.broadcast.UpdateRedTextReceiver;
import mybaby.ui.community.parentingpost.util.WebViewUtil;
import mybaby.util.DrawableManager;
import mybaby.util.ImageViewUtil;
import mybaby.util.LogUtils;
import mybaby.util.MapUtils;
import mybaby.util.StringUtils;
import mybaby.util.Utils;

/**
 * @date 15.9.2
 * @author baojun
 * 浏览网页
 *
 *
 * 20160620
 * 此类可以直接使用，但是现在已改为fragment代替
 * 如果fragment有异常无法调试，就先用这个
 */
@Deprecated
public class DesParentingWebViewActivity extends ToolbarControlBaseActivity<ObservableWebView> implements OnClickListener {

	private ObservableWebView webView;

	private View mErrorView;
	private String first_url;
	private ParentingPost parentingPost;
	private Activity  activity;
	private ImageView actionbar_title_image;
	public static UMSocialService webviewController = UMServiceFactory
			.getUMSocialService(Constants.DESCRIPTOR);
	private boolean canChangeTitle = false;
	private int mbtopbar;//
	private boolean isTao=false;

	private boolean hasResume=false;

	public void onResume() {
		super.onResume();
		LogUtils.e("onResume!!!!!!!!!!!!!!!!!!");
		MobclickAgent.onPageStart(canChangeTitle ? "育儿文章内容" : "浏览器页面"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
		if (webView==null)
			return;
		webView.resumeTimers();
		if (isTao){
			webView.goBack();
			setStauToolbar(0);
		}
		webView.postDelayed(new Runnable() {
			@Override
			public void run() {
				hasResume=true;
				LogUtils.e("已重置开关");
			}
		}, 2000);
	}

	public void onPause() {
		super.onPause();
		hasResume=false;
		LogUtils.e("onPause!!!!!!!!!!!!!!!!!!");
		MobclickAgent.onPageEnd(canChangeTitle ? "育儿文章内容" : "浏览器页面"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
		MobclickAgent.onPause(this);
		if (webView==null)
			return;
		webView.pauseTimers();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		LogUtils.e("onDestroy!!!!!!!!!!!!!!!!!!");
		super.onDestroy();
		destroyView();
	}

	protected void destroyView() {
		// TODO Auto-generated method stub
		LogUtils.e("destroyView!!!!!!!!!!!!!!!!!!");
		Constants.WEBVIEW_PAGE_COUNTS_MAX+=1;//在此处增加可开启新浏览器的机会数
		if (Constants.WEBVIEW_PAGE_COUNTS_MAX>=5) {
			Constants.WEBVIEW_PAGE_COUNTS_MAX=5;
		}
		try {
			// 清理cache 和历史记录:
			webView.clearHistory();
			webView.clearCache(true);
			webView.destroy();
			webView=null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void finish() {
		super.finish();
		//overridePendingTransition(R.anim.null_anim, R.anim.null_anim);
	}
	@Override
	protected int getLayoutResId() {
		return R.layout.parentingpost_activity;
	}
	@Override
	protected ObservableWebView createScrollable() {
		webView= (ObservableWebView) findViewById(R.id.scrollable);
		return webView;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LogUtils.e("onCreate!!!!!!!!!!!!!!!!!!");
		super.onCreate(savedInstanceState);
		//setSupportActionBar(toolbar);
		//overridePendingTransition(R.anim.null_anim, R.anim.null_anim);
		init();
		if(null!=savedInstanceState){
			webView.restoreState(savedInstanceState);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (webView!=null)
			webView.saveState(outState);
	}

	/**
	 * chushi初始化
	 */
	@SuppressLint("NewApi")
	private void init() {
		// TODO Auto-generated method stub
		activity = this;
		Bundle bundle = getIntent().getExtras();
		canChangeTitle = bundle == null ? false : bundle.getBoolean(
				"canChangeTitle", false);// 是否可更变title
		first_url = getIntent().getExtras().getString("url", "");
		parentingPost = (ParentingPost) getIntent().getExtras()
				.getSerializable("parentingPost");

		tv_title.setText(canChangeTitle ? "" : "育儿");//如果是浏览器则不要设置头部标签
		tv_back.setTypeface(MyBabyApp.fontAwesome);
		tv_back.setOnClickListener(this);
		actionbar_title_image = (ImageView) toolbar.findViewById(R.id.actionbar_title_image);
		new UpdateRedTextReceiver((TextView) toolbar.findViewById(R.id.actionbar_back_badge)).regiest();

		tv_close.setVisibility(View.GONE);
		tv_close.setOnClickListener(this);
		tv_setShare.setVisibility(parentingPost==null?View.GONE:View.VISIBLE);
		tv_setShare.setOnClickListener(this);

		new UmengShare().configPlatforms(webviewController, activity);

		if (!TextUtils.isEmpty(first_url)) {
			initErrorPage();
			initWebView();
			loadUrl(first_url);
			initErrorPage();
		} else if (!TextUtils.isEmpty(parentingPost==null?"":parentingPost.getDetailUrl())) {
			first_url = parentingPost.getDetailUrl();
			initErrorPage();
			initWebView();
			loadUrl(first_url);
			initErrorPage();
		} else {
			Toast.makeText(activity, "浏览地址无法解析", Toast.LENGTH_SHORT).show();
			onBackPressed();
		}
	}

	public void loadUrl(String url) {
		webView.loadUrl(url);
		String newStr = url.replaceAll("&mbnewpage","");
		LogUtils.e("初始浏览器加载处理后的url：" + newStr);
		WebViewUtil.deilOverUrlAtStr(true, newStr, first_url, toolbar, webView, this);
		Map<String, Object> map = Action.getHeader(webView.getUrl());
		if (null == map) //服务器制定标题
			return;
		if (map.containsKey("mbspt")) {
			tv_title.setText((String) map.get("mbspt"));
		}
		setStauToolbar(MapUtils.getMapInt(map,"mbtopbar"),MapUtils.getMapStr(map,"bar_color"));
	}
	public void setRightBtnTexOnclick(String text, final OnClickListener clickListener){
		tv_setShare.setVisibility(View.VISIBLE);
		tv_setShare.setText(text);
		tv_setShare.setOnClickListener(clickListener);
	}
	public void setMbtopbar(int mbtopbar){
		this.mbtopbar=mbtopbar;
	}
	public int getMbtopbar(){
		return mbtopbar;
	}


	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	private void initWebView() {
		// TODO Auto-generated method stub

		//webView.setScrollViewCallbacks(this);//默认不要开启

		try {
			WebViewService webViewService = AlibabaSDK.getService(WebViewService.class);
			if (webViewService!=null)
                webViewService.bindWebView(webView, new WebViewClient());
		} catch (Exception e) {
			e.printStackTrace();
		}

		webView.requestFocus();
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		// webView.setInitialScale(80);// 浏览器的缩放比例
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setJavaScriptEnabled(true);
		// webView.addJavascriptInterface(this, "aaa");
		webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);//设置滚动条隐藏
		webView.getSettings().setRenderPriority(WebSettings.RenderPriority.LOW);//设置渲染优先级
		webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		if(Build.VERSION.SDK_INT >= 19) {
			webView.getSettings().setLoadsImagesAutomatically(true);
		} else {
			webView.getSettings().setLoadsImagesAutomatically(false);
		}
		webView.getSettings().setUserAgentString(WebViewUtil.getUserAStr(webView.getSettings()));

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				if (Utils.isNetworkAvailable()) {
					hideErrorPage();
				}
				webview_progess.setVisibility(newProgress > 0
						&& newProgress < 100 ? View.VISIBLE : View.GONE);
				webview_progess.setProgress(newProgress);

				super.onProgressChanged(view, newProgress);
			}

			@Override
			public void onReceivedTitle(WebView view, String webtitle) {

				// TODO Auto-generated method stub
				if (canChangeTitle) {
					tv_title.setText(webtitle);
				}
				setCustemTitle(DesParentingWebViewActivity.this, view.getUrl(), tv_title, actionbar_title_image);
				super.onReceivedTitle(view, webtitle);
			}
		});

		webView.setWebViewClient(webViewClient);
		webView.setOnClickListener(null);
		webView.setOnLongClickListener(null);
		// 屏蔽长按事件
		// zhi支持flash
		String temp = "<html><body bgcolor=\"" + "black"
				+ "\"> <br/><embed src=\"" + first_url + "\" width=\"" + "100%"
				+ "\" height=\"" + "90%" + "\" scale=\"" + "noscale"
				+ "\" type=\"" + "application/x-shockwave-flash"
				+ "\"> </embed></body></html>";
		String mimeType = "text/html";
		String encoding = "utf-8";
		webView.loadDataWithBaseURL("null", temp, mimeType, encoding, "");

	}
	public static void setCustemTitle(Activity activity,Map<String, Object> map,TextView tv_title,ImageView actionbar_title_image){
		tv_title.setVisibility(View.VISIBLE);
		if (null == map) //服务器制定标题
			return;
		if (map.containsKey("mbspt")) {
			tv_title.setText((String) map.get("mbspt"));
		}
		boolean ha=map.containsKey("icon_url");
		tv_title.setVisibility(ha?View.GONE:View.VISIBLE);
		actionbar_title_image.setVisibility(ha?View.VISIBLE:View.GONE);
		if (!ha)
			return;
		String url= MapUtils.getMapStr(map, "icon_url");
		if (url.startsWith("http")){
			ImageViewUtil.displayImage(url, actionbar_title_image);
		}else {
			int id= DrawableManager.getDrawableResourceId(activity, url);
			if (id!=0)
				actionbar_title_image.setImageResource(id);
		}
	}
	public static void setCustemTitle(Activity activity,String cur_url,TextView tv_title,ImageView actionbar_title_image){
		setCustemTitle(activity,Action.getHeader(cur_url),tv_title,actionbar_title_image);
	}

	boolean isCut=false;
	private WebViewClient webViewClient=new WebViewClient() {
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.e("开始执行新的连接", "onPageFinished:::>>>>>>" + url);
			/*if (url==null||url.equals(""))
				ParentingWebViewActivity.this.finish();*/

			if (url.equals("about:blank"))
				webView.stopLoading();
			try {
				if(!webView.getSettings().getLoadsImagesAutomatically()) {
                    webView.getSettings().setLoadsImagesAutomatically(true);
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (canChangeTitle) {
				tv_title.setText(view.getTitle());
				tv_close.setVisibility(view.canGoBack() ? View.VISIBLE : View.GONE);
				/*if (!(view.getUrl().contains("mb_right_btn")&&view.getUrl().contains("mb_right_btn_action"))) {
				}*/
			}
			setCustemTitle(DesParentingWebViewActivity.this, url, tv_title, actionbar_title_image);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			Log.e("开始执行新的连接", "onPageStarted:::>>>>>>" + url);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stubk

			Log.e("开始执行新的连接","shouldOverrideUrlLoading:::>>>>>>"+url);
			WebView.HitTestResult result = view.getHitTestResult();
			isCut=(null == result);
			int hitType = result==null?0:result.getType();
			LogUtils.e("点击的类型为："+hitType);
			String hosturl = StringUtils.getHost(url);
			if (hosturl.contains("taobao.com") || hosturl.contains("tmall.com")) {
				isTao=true;
			}else{
				isTao=false;
			}
			if(hitType == 0){//重定向时hitType为0
				if (isTao)
					return false;//不捕获302重定向
			}

			WebViewUtil webViewUtil=new WebViewUtil(DesParentingWebViewActivity.this,url,first_url,webView,toolbar,delayDisplayProgress,parentingPost,webviewController,this);
			return webViewUtil.deilOverUrl();
			/*int type = result.getType();
			switch (type){
				case WebView.HitTestResult.SRC_ANCHOR_TYPE:
					break;
				case WebView.HitTestResult.PHONE_TYPE: // 处理拨号
					break;
				case WebView.HitTestResult.EMAIL_TYPE: // 处理Email
					break;
				case WebView.HitTestResult.GEO_TYPE:
					break;
				case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
					break;
				case WebView.HitTestResult.IMAGE_TYPE:
					Log.d("", "图片");
					break;
			}
			return  false;*/
		}

		@Override
		public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
			try {
				webView.stopLoading();
				showErrorPage();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
			try {
				webView.stopLoading();
				showErrorPage();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			try {
				webView.stopLoading();
				showErrorPage();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
									String description, String failingUrl) {
			Utils.Loge("error by web veiw");
			//if (!Utils.isNetworkAvailable()) {
			try {
				//super.onReceivedError(view, errorCode, description, failingUrl);
				//String errorHtml = "<html><body><h1>Page not find!</h1></body></html>";
				//webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
				webView.stopLoading();
				showErrorPage();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//}
			//super.onReceivedError(view, errorCode, description, failingUrl);
		}
	};


	/**
	 * 显示自定义错误提示页面，用一个View覆盖在WebView
	 */
	protected void showErrorPage() throws Exception{
		webView.stopLoading();
		dealError.bringToFront();
		dealError.setVisibility(View.VISIBLE);
		WebSettings settings = webView.getSettings();
		String ua = settings.getUserAgentString();
		settings.setUserAgentString(ua + " MyBaby/" + MyBabyApp.version + " MBUSER/"
				+ MyBabyApp.currentUser.getUniqueName());
	}

	protected void hideErrorPage() {
		dealError.setVisibility(View.GONE);
	}

	protected void initErrorPage() {
		if (mErrorView == null) {
			mErrorView = View.inflate(this, R.layout.online_error, null);
			TextView button = (TextView) mErrorView
					.findViewById(R.id.online_error_btn_retry);
			button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					//webView.reload();
					webView.destroy();
					onBackPressed();
				}
			});
			mErrorView.setOnClickListener(null);
			dealError.addView(mErrorView, 0);
			dealError.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			LogUtils.e("touch keyback!!!!!!!!!!!!!!!!!!");
			if (hasResume) {
				if (webView != null) {
					if (webView.canGoBack()) {
						webView.goBack();
					} else {
						webView.destroy();
						onBackPressed();
					}
				}
				return true;
			}
		}

		return super.onKeyUp(keyCode, event);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		UMSsoHandler ssoHandler = webviewController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}


		if (resultCode == RESULT_OK) {
			// 返回成功，淘宝将返回正确的信息，信息保存在intent中的result项中.
			//Toast.makeText(this, data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
		} else if (resultCode == RESULT_CANCELED) {
			 //用户主动取消操作.
		} else if (resultCode == -2) {
			// error,淘宝将返回错误码，同样解析intent中的result项，形式如下：
			//Toast.makeText(this, data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
		}
		CallbackContext.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.actionbar_back:
			if (canChangeTitle) {
				if (webView.canGoBack()) {
					webView.goBack();
				}else {
					onBackPressed();
				}
			}else {
				onBackPressed();
			}
			
			break;
		case R.id.share_tv:
			try {
				new UmengShare.OpenShare(activity,
						webviewController, UmengShare.setShareBean(
						parentingPost, activity));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			case R.id.close_tv:
				onBackPressed();
				break;
		}
	}
}
