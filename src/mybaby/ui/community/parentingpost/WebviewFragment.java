package mybaby.ui.community.parentingpost;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import java.util.Map;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.action.Action;
import mybaby.cache.CacheDataTask;
import mybaby.models.community.ParentingPost;
import mybaby.notification.RefreshNotificationManager;
import mybaby.share.UmengShare;
import mybaby.ui.MyBabyApp;
import mybaby.ui.broadcast.UpdateRedTextReceiver;
import mybaby.ui.community.parentingpost.util.WebViewUtil;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.util.DrawableManager;
import mybaby.util.ImageViewUtil;
import mybaby.util.LogUtils;
import mybaby.util.MapUtils;
import mybaby.util.StringUtils;
import mybaby.util.Utils;

/**
 * Created by Administrator on 2016/6/20 0020.
 */
public class WebviewFragment extends ToolbarControlBaseFragment<ObservableWebView> implements View.OnClickListener{

    private Toolbar toolbar;
    private ObservableWebView webView;
    private View mErrorView;
    private String first_url;
    private ParentingPost parentingPost;
    public static UMSocialService webviewController = UMServiceFactory
            .getUMSocialService(Constants.DESCRIPTOR);
    private int mbtopbar;//
    private boolean canChangeTitle = false;
    private boolean isTao=false;
    private boolean isLoadSatus=true;
    private boolean isSendRedNtfc=false;
    private String pageTag;
    public WebviewFragment(Toolbar toolbar) {
        this.toolbar=toolbar;
    }

    public WebviewFragment() {
        super();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.parentingpost_webview;
    }

    @Override
    protected ObservableWebView createScrollable() {
        webView= (ObservableWebView) rootView.findViewById(R.id.scrollable);
        return webView;
    }
    @Override
    protected Toolbar getToolbar() {
        return toolbar;
    }

    public ObservableWebView getWebView() {
        return webView;
    }

    public void setIsSendRedNtfc(boolean isSendRedNtfc) {
        this.isSendRedNtfc = isSendRedNtfc;
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e("onResume!!!!!!!!!!!!!!!!!!");
        MobclickAgent.onPageStart(canChangeTitle ? "育儿文章内容" : "浏览器页面"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
        if (webView==null)
            return;
        try {
            webView.resumeTimers();
            if (isTao&&!(getActivity() instanceof MyBayMainActivity)){
                webView.goBack();
                setStauToolbar(0);
            }

            if (CacheDataTask.booleaRefush(MyBabyApp.getSharedPreferences().getLong(pageTag, 0), 60)) {
                webView.reload();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void perfError(){
        if (dealError != null&&mErrorView!=null){
            if (dealError.getVisibility()==View.VISIBLE)
                mErrorView.performClick();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.e("onPause!!!!!!!!!!!!!!!!!!");
        SharedPreferences.Editor edit = MyBabyApp.getSharedPreferences().edit();
        edit.putLong(pageTag, System.currentTimeMillis());
        edit.commit();
        MobclickAgent.onPageEnd(canChangeTitle ? "育儿文章内容" : "浏览器页面"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
        try {
            if (webView==null)
                return;
            webView.pauseTimers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyView();
    }
    @Override
    protected void initData() {
        LogUtils.e("initData!!!!!!!!!!!!!!!!!!");
        pageTag=getActivity().getPackageName()+System.currentTimeMillis();
        Bundle bundle = getArguments();
        if (bundle==null)
            setFinishTag(-1);
        canChangeTitle = bundle.getBoolean("canChangeTitle", false);// 是否可更变title
        isLoadSatus= bundle.getBoolean("isLoadSatus", true);// 是否初始加载
        isSendRedNtfc= bundle.getBoolean("isSendRedNtfc", false);// 是否发送红点刷新
        first_url = bundle.getString("url", "");
        parentingPost = (ParentingPost) bundle.getSerializable("parentingPost");
        UmengShare.configPlatforms(webviewController, getContext());
        if (bundle.getBoolean("setZeroMarginHeight", false))
            setWebviewMarginTop(webView);
        if (!(getActivity() instanceof ParentingWebViewActivity)){
            //隐藏滚动条
            webView.setHorizontalScrollBarEnabled(false);//水平不显示
            webView.setVerticalScrollBarEnabled(false); //垂直不显示
            webview_progress_two.setVisibility(View.VISIBLE);
        }
    }
    public void destroyView() {
        // TODO Auto-generated method stub
        LogUtils.e("destroyView!!!!!!!!!!!!!!!!!!");
        try {
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
                /*scrollable_staus.clearHistory();
                scrollable_staus.clearCache(true);
                scrollable_staus.destroy();
                scrollable_staus=null;*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * chushi初始化
     */
    @SuppressLint("NewApi")
    @Override
    protected void initToolbar() {
        // TODO Auto-generated method stub
        LogUtils.e("initToolbar!!!!!!!!!!!!!!!!!!");
        toolbar.bringToFront();
        tv_title.setText(canChangeTitle ? "" : "育儿");//如果是浏览器则不要设置头部标签
        //tv_back.setTypeface(MyBabyApp.fontAwesome);
        tv_back.setOnClickListener(this);
        new UpdateRedTextReceiver((TextView) toolbar.findViewById(R.id.actionbar_back_badge)).regiest();

        tv_close.setVisibility(View.GONE);
        tv_close.setOnClickListener(this);
        tv_setShare.setVisibility(parentingPost == null ? View.GONE : View.VISIBLE);
        tv_setShare.setOnClickListener(this);
    }
    @Override
    protected void star() {
        if (!TextUtils.isEmpty(first_url)) {
            initWebView();
            loadUrl(first_url,true,true);
            initErrorPage();
        } else if (!TextUtils.isEmpty(parentingPost==null?"":parentingPost.getDetailUrl())) {
            first_url = parentingPost.getDetailUrl();
            initWebView();
            loadUrl(first_url,true,true);
            initErrorPage();
        } else {
            Toast.makeText(getActivity(), "浏览地址无法解析", Toast.LENGTH_SHORT).show();
            setFinishTag(-1);
        }
    }
    public void loadUrl(String url) {
        loadUrl(url,false,false);
    }
    public void loadUrl(String url,boolean isPageOption,boolean isFirst) {
        if (!isLoadSatus){
            webView.loadUrl("about:blank");
            return;
        }

        String newStr = url.replaceAll("&mbnewpage","");
        webView.loadUrl(newStr);
        LogUtils.e("初始浏览器加载处理后的url：" + newStr);
        WebViewUtil.deilOverUrlAtStr(true, newStr, first_url, toolbar, webView, getActivity());
        if (toolbar==null)
            return;
        Map<String, Object> map = Action.getHeader(webView.getUrl());
        if (null == map) //服务器制定标题
            return;
        setCustemTitle(getActivity(), map, tv_title, actionbar_title_image);

        setStauToolbar(MapUtils.getMapInt(map, "mbtopbar"), MapUtils.getMapStr(map, "bar_color"));

        if (isPageOption) {
            WebViewUtil webViewUtil = new WebViewUtil(getActivity(), url, first_url, webView, toolbar, delayDisplayProgress, parentingPost, webviewController, webViewClient);
            webViewUtil.setPageOption(isFirst);
        }
    }
    public void setRightBtnTexOnclick(String text, final View.OnClickListener clickListener){
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

    public static void scrollToTop(final ObservableWebView webView){
        if (webView==null)
            return;
        int sy=webView.getCurrentScrollY();
        if (sy!=0) {
            sy = sy > 1500 ? 1500 : sy;
            ValueAnimator animator = ValueAnimator.ofInt(sy, 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        int y = (Integer) animation.getAnimatedValue();
                        webView.scrollVerticallyTo(y);
                        //LogUtils.e(y+"");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(webView, "alpha", 0.1f, 1.0f);
            AnimatorSet animSet = new AnimatorSet();
            animSet.play(animator).with(animator1);
            animSet.setDuration(200);
            animSet.start();
        }
    }

    private void setDetail(WebView webView){
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
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        if(Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
        }
        webView.getSettings().setUserAgentString(WebViewUtil.getUserAStr(webView.getSettings()));
    }
    int cur=0;
    int oldPro=0;

    /**
     * 初始化浏览器
     */
    @SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
    private void initWebView() {
        // TODO Auto-generated method stub
        try {
            WebViewService webViewService = AlibabaSDK.getService(WebViewService.class);
            if (webViewService!=null)
                webViewService.bindWebView(webView, new WebViewClient());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDetail(webView);
        //setDetail(scrollable_staus);
        //scrollable_staus.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, final int newProgress) {
                // TODO Auto-generated method stub
                /*if (Utils.isNetworkAvailable()) {
                    hideErrorPage();
                }*/
                if (newProgress!=oldPro) {
                    oldPro = newProgress;
                    cur+=1;
                }
                /*if (newProgress>60&&newProgress<100) {
                    if (scrollable_staus.getVisibility()==View.VISIBLE&&getActivity() instanceof ParentingWebViewActivity) {
                        scrollable_staus.setVisibility(View.GONE);
                        webView.bringToFront();
                        webview_progess.bringToFront();
                    }
                }*/
                LogUtils.e("newProgress"+newProgress+"__________"+cur);
                if (getActivity() instanceof ParentingWebViewActivity && stauToolbar!=3) {
                    if (newProgress==100&&cur<1){
                        webview_progess.setVisibility(View.VISIBLE);
                    }
                    webview_progress_two.setVisibility(View.GONE);
                    webview_progess.setVisibility(newProgress > 0
                            && newProgress < 100 ? View.VISIBLE : View.GONE);
                    webview_progess.setProgress(newProgress);
                }else {
                    if (newProgress==100&&cur<1){
                        webview_progress_two.setVisibility(View.VISIBLE);
                    }
                    webview_progess.setVisibility(View.GONE);
                    //webview_progress_two.setVisibility(newProgress > 0&& newProgress < 100 ? View.VISIBLE : View.GONE);
                    webview_progress_two.setVisibility((webView.getContentHeight() <500&& newProgress < 100 )? View.VISIBLE : View.GONE);
                    if (newProgress==100)
                        scrollToTop(webView);
                }

                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String webtitle) {

                // TODO Auto-generated method stub
                if (toolbar!=null) {
                    if (canChangeTitle&&!TextUtils.isEmpty(webtitle)) {
                        tv_title.setText(webtitle);
                    }
                    setCustemTitle(getActivity(), view.getUrl(), tv_title, actionbar_title_image);
                }
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
        boolean ha=map.containsKey("icon_url");
        tv_title.setVisibility(ha?View.GONE:View.VISIBLE);
        actionbar_title_image.setVisibility(ha?View.VISIBLE:View.GONE);
        if (!ha) {
            if (map.containsKey("mbspt")) {
                tv_title.setText((String) map.get("mbspt"));
            }
            return;
        }
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
    /**
     * 浏览器的各种回调
     */
    private WebViewClient webViewClient=new WebViewClient() {
        @NonNull
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e("开始执行新的连接", "onPageFinished:::>>>>>>" + url);

			/*if (url==null||url.equals(""))
				ParentingWebViewActivity.this.finish();*/
            try {
                if (isSendRedNtfc) {
                    RefreshNotificationManager.refreshNotification(true);
                    isSendRedNtfc=false;
                }
                if (webView!=null) {
//                    if (url.equals("about:blank"))
//                        webView.stopLoading();
                    if (!webView.getSettings().getLoadsImagesAutomatically()) {
                        webView.getSettings().setLoadsImagesAutomatically(true);
                    }
                }
                if (toolbar==null)
                    return;
                if (canChangeTitle) {
                    tv_close.setVisibility(view.canGoBack() ? View.VISIBLE : View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @NonNull
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
            LogUtils.e("点击的类型为：" + hitType);
            String hosturl = StringUtils.getHost(url);
            if (hosturl.contains("taobao.com") || hosturl.contains("tmall.com")) {
                isTao=true;
            }else{
                isTao=false;
            }
            if(hitType == 0){//重定向时hitType为0
                if (isTao) {
                    LogUtils.e("重定向开始走");
                    view.loadUrl(url);
                    return true;//不捕获302重定向
                }
            }

            WebViewUtil webViewUtil=new WebViewUtil(getActivity(),url,first_url,webView,toolbar,delayDisplayProgress,parentingPost,webviewController,this);
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
        @NonNull
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            try {
                showErrorPage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @NonNull
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            try {
                showErrorPage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @NonNull
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            try {
                showErrorPage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @NonNull
        @Override
        public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
            Utils.Loge("error by web veiw");
            //if (!Utils.isNetworkAvailable()) {
            try {
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
    @NonNull
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
        if (dealError==null)
            return;
        dealError.setVisibility(View.GONE);
        mErrorView.setEnabled(true);
        mErrorView.findViewById(R.id.error_tex).setVisibility(View.VISIBLE);
        mErrorView.findViewById(R.id.progress_error).setVisibility(View.GONE);
    }

    @NonNull
    protected void initErrorPage() {
        if (mErrorView == null) {
            mErrorView = View.inflate(getActivity(), R.layout.online_error, null);
            //TextView button = (TextView) mErrorView.findViewById(R.id.online_error_btn_retry);
            mErrorView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (webView==null)
                        return;
                    if (Utils.isNetworkAvailable()) {
                        webView.post(new Runnable() {
                            @Override
                            public void run() {
                                webView.reload();
                                mErrorView.setEnabled(false);
                                mErrorView.findViewById(R.id.error_tex).setVisibility(View.GONE);
                                mErrorView.findViewById(R.id.progress_error).setVisibility(View.VISIBLE);
                            }
                        });
                        webView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideErrorPage();
                            }
                        },4500);
                    }else {
                        Snackbar.make(webView,"请检查网络连接!",Snackbar.LENGTH_SHORT).setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                    }
                }
            });
            dealError.addView(mErrorView, 0);
            dealError.setVisibility(View.GONE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        UMSsoHandler ssoHandler = webviewController.getConfig().getSsoHandler(
                requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }


        if (resultCode == getActivity().RESULT_OK) {
            // 返回成功，淘宝将返回正确的信息，信息保存在intent中的result项中.
            //Toast.makeText(this, data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
        } else if (resultCode == getActivity().RESULT_CANCELED) {
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
                        getActivity().onBackPressed();
                    }
                }else {
                    getActivity().onBackPressed();
                }

                break;
            case R.id.share_tv:
                try {
                    new UmengShare.OpenShare(getActivity(),
                            webviewController, UmengShare.setShareBean(
                            parentingPost, getActivity()));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case R.id.close_tv:
                getActivity().onBackPressed();
                break;
        }
    }
}
