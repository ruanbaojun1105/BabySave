package mybaby.ui.community.parentingpost.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.login.LoginService;
import com.alibaba.sdk.android.login.WebViewService;
import com.taobao.tae.sdk.callback.LoginCallback;
import com.taobao.tae.sdk.model.Session;
import com.umeng.socialize.controller.UMSocialService;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.action.Action;
import mybaby.cache.CacheDataTask;
import mybaby.cache.GenCaches;
import mybaby.models.Blog;
import mybaby.models.WebViewOption;
import mybaby.models.community.ParentingPost;
import mybaby.rpc.BaseRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.parentingpost.ParentingWebViewActivity;
import mybaby.ui.community.parentingpost.WebviewFragment;
import mybaby.util.LogUtils;
import mybaby.util.MapUtils;
import mybaby.util.StringUtils;

/**
 * Created by LeJi_BJ on 2016/3/9.
 */
public class WebViewUtil {
    Activity activity;
    String url;
    String first_url;
    WebView webView;
    ProgressBar delayDisplayProgress;
    ParentingPost parentingPost;
    UMSocialService webviewController;
    WebViewClient webViewClient;
    View topView;
    TopViewHolder viewHolder;
    boolean isFirstLoad;

    public WebViewUtil(Activity activity, String url, String first_url, WebView webView,@Nullable Toolbar topView,ProgressBar delayDisplayProgress, ParentingPost parentingPost, UMSocialService webviewController, WebViewClient webViewClient) {
        this.activity = activity;
        this.url = url;
        this.first_url = first_url;
        this.webView = webView;
        this.topView = topView;
        if (viewHolder==null) {
            if (topView == null)
                viewHolder = null;
            else
                viewHolder = new TopViewHolder(topView);
        }
        this.delayDisplayProgress = delayDisplayProgress;
        this.parentingPost = parentingPost;
        this.webviewController = webviewController;
        this.webViewClient = webViewClient;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    static class TopViewHolder{
        TextView tv_title;
        TextView tv_setShare;
        TextView tv_back;
        Toolbar topView;
        TextView tv_close;
        public TopViewHolder(Toolbar topView) {
            if (topView==null)
                return;
            tv_title = (TextView) topView.findViewById(R.id.actionbar_title);
            tv_setShare = (TextView) topView.findViewById(R.id.share_tv);
            tv_close = (TextView) topView.findViewById(R.id.close_tv);
            tv_back= (TextView) topView.findViewById(R.id.actionbar_back);
            this.topView=topView;
        }
    }

    /**
     * 淘宝登陆授权
     */
    public void showLogin(final Activity activity, final WebView webView, final WebViewClient webViewClient) {
        //调用getService方法来获取服务
        LoginService loginService = AlibabaSDK.getService(LoginService.class);
        loginService.showLogin(activity, new LoginCallback() {
            @Override
            public void onSuccess(Session session) {
                // 当前是否登录状态           boolean isLogin();
                // 登录授权时间           long getLoginTime();
                // 当前用户ID           String getUserId();
                // 用户其他属性           User getUser();
                //User中：淘宝用户名  淘宝用户ID   淘宝用户头像地址
                WebViewService webViewService = AlibabaSDK.getService(WebViewService.class);
                webViewService.bindWebView(webView, webViewClient);
                //webView.setWebViewClient(webViewClient);
                /*Toast.makeText(activity, "-isLogin-" + session.isLogin() + "-UserId-" +
                        session.getUserId() + "-LoginTime-" + session.getLoginTime() + "[user]:nick=" +
                        session.getUser().nick + "头像" + session.getUser().avatarUrl, Toast.LENGTH_SHORT).show();*/
            }

            @Override
            public void onFailure(int code, String message) {
                Toast.makeText(activity, "授权取消" + code + message,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
    public boolean deilOverUrl(){
        return deilOverUrl(false);
    }
    public boolean deilOverUrl(boolean isCut){
        try {

            if (isCut)
                return true;

            setPageOption(false);

            /*String hosturl = StringUtils.getHost(url);
            if (hosturl.contains("taobao.com") || hosturl.contains("tmall.com")) {
                LoginService loginService = AlibabaSDK.getService(LoginService.class);
                if (!loginService.getSession().isLogin()) {
                    showLogin(activity, webView, webViewClient);
                    //TaoOpen.startOauth(ParentingWebViewActivity.this,Constants.OPENIM_KEY,Constants.OPENIM_SECRET);
                    return true;
                } else {
                    //
                    if (webViewClient!=null) {
                        WebViewService webViewService = AlibabaSDK.getService(WebViewService.class);
                        webViewService.bindWebView(webView, webViewClient);
                    }
                }
            }*/

            LogUtil.e(url);
            Action action=null;
            try {
                action = Action.createAction(url, false);
            } catch (Exception e) {
                e.printStackTrace();
                action=null;
            }
            if (action == null) {
                return deilOverUrlAtStr(false,url,first_url,viewHolder,webView,activity);
            } else {
                return action.excute(activity, webviewController,webView, parentingPost);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * url在非action时参数检测，此方法同时在新启动浏览器加载时开启检测
     * @param isFirst 是否第一次加载
     * @param url
     * @param first_url
     * @param webView
     * @param activity
     * @return
     */
    private static String skipSourArr[]= new String[]{"tel:", "mbnewpage","mbtopbar","mbotherdomain"};
    public static boolean deilOverUrlAtStr(boolean isFirst, String url , String first_url, Toolbar topView, WebView webView, final Activity activity){
        return deilOverUrlAtStr(isFirst,url,first_url,topView==null?null:new TopViewHolder(topView),webView,activity);
    }

    public static boolean deilOverUrlAtStr(boolean isFirst,String url ,String first_url,TopViewHolder holder, final WebView webView, final Activity activity){
        if (url.startsWith(skipSourArr[0])){
            Uri uri = Uri.parse(url);
            Intent it = new Intent(Intent.ACTION_DIAL, uri);
            activity.startActivity(it);
            return true;
        }
        if (url.contains(skipSourArr[1])){
            /*if (first_url.equals(url)||first_url.contains(skipSourArr[1])) {
                if (activity instanceof ParentingWebViewActivity)
                    return false;
            }*/
            if (!isFirst&&(!url.equals(first_url))) {
                String hosturl = StringUtils.getHost(url);
                //没有mbotherdomain这个参数的话不给跳转到其他域名去
                if (!(hosturl.contains("lejimami.com") || hosturl.contains("hibb.me"))&&!url.contains(skipSourArr[3])) {
                    return true;
                 }
                /*if (!(hosturl.contains("taobao.com") || hosturl.contains("tmall.com"))) {
                    setOpenPage(activity, url);
                }*/
                webView.stopLoading();
                String newStr = url.replaceAll("&mbnewpage", "");
                LogUtils.e("处理后的url：" + newStr);
                return setOpenPage(activity,webView, newStr);
            }
            return true;
        }
        if (url.contains(skipSourArr[2])){
            if (activity instanceof ParentingWebViewActivity) {
                ParentingWebViewActivity webViewActivity = (ParentingWebViewActivity) activity;
                Map<String,Object> map=Action.getHeader(url);
                webViewActivity.getWebviewFragment().setMbtopbar(MapUtils.getMapInt(map, "mbtopbar"));
                webViewActivity.getWebviewFragment().setStauToolbar(MapUtils.getMapInt(map,"mbtopbar"), MapUtils.getMapStr(map, "bar_color"));
            }
        }
        if (url.contains("mb_right_btn")&&url.contains("mb_right_btn_action")){
            if (activity instanceof ParentingWebViewActivity) {
                ParentingWebViewActivity webViewActivity = (ParentingWebViewActivity) activity;
                final Map<String,Object> map=Action.getHeader(url);
                webViewActivity.getWebviewFragment().setRightBtnTexOnclick(MapUtils.getMapStr(map, "mb_right_btn"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Action action = Action.createAction(MapUtils.getMapStr(map, "mb_right_btn_action"));
                            action.excute(activity, WebviewFragment.webviewController, webView, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.e("action执行异常");
                        }
                    }
                });
            }
        }
        /*if (!isFirst&&(!url.equals(first_url))) {
            String hosturl = StringUtils.getHost(url);
            if (!(hosturl.contains("lejimami.com") || hosturl.contains("hibb.me")) && !url.contains(skipSourArr[3])) {
                return true;
            }
        }*/

        /*if (url.contains("bar_color")){
            if (holder==null)
                return false;
            Map<String,Object> map=Action.getHeader(url);
            String bgcolor=MapUtils.getMapStr(map,"bar_color");
            if (!TextUtils.isEmpty(bgcolor)) {
                holder.topView.setBackgroundColor(Color.parseColor("#" + bgcolor));
                holder.tv_title.setTextColor(Color.WHITE);
                holder.tv_back.setTextColor(Color.WHITE);
                holder.tv_setShare.setTextColor(Color.WHITE);
                holder.tv_close.setTextColor(Color.WHITE);
                holder.tv_setShare.setBackground(null);
            }else {
                holder.topView.setBackgroundColor(activity.getColor(R.color.bg_gray));
                holder.tv_title.setTextColor(Color.BLACK);
                holder.tv_back.setTextColor(Color.BLACK);
                holder.tv_setShare.setTextColor(Color.BLACK);
                holder.tv_close.setTextColor(Color.BLACK);
            }
        }*/
        return false;
    }


    /**
     * 开启新的浏览器
     * @param activity
     * @param url
     * @return
     */
    public static boolean setOpenPage(Activity activity,WebView webView,String url){
        int mbtopbar=0;
        Map<String,Object> map=Action.getHeader(url);
        if (map.containsKey("mbtopbar"))
            mbtopbar=MapUtils.getMapInt(map, "mbtopbar");
        if (Constants.WEBVIEW_PAGE_COUNTS_MAX>0) {
            Constants.WEBVIEW_PAGE_COUNTS_MAX-=1;
            if (Constants.WEBVIEW_PAGE_COUNTS_MAX<=0) {
                Constants.WEBVIEW_PAGE_COUNTS_MAX=0;
            }
            CustomAbsClass.starWebViewIntent(activity, url);
            return true;
        }else {
            if (activity instanceof ParentingWebViewActivity){
                if (mbtopbar>0){
                    CustomAbsClass.starWebViewIntent(activity, url);
                    return true;
                }else {
                    webView.loadUrl(url);
                    LogUtil.e("已超过开启浏览器新页面最大数");
                    ((ParentingWebViewActivity)activity).getWebviewFragment().setStauToolbar(0);//此处保证一下显示问题
                    return false;/*Toast.makeText(activity, "已超过开启浏览器新页面最大数", Toast.LENGTH_SHORT).show()*/
                }
            }else
                CustomAbsClass.starWebViewIntent(activity, url);
            return false;//在当前开启的浏览器中处理
        }
    }
    /**
     * 所有浏览器控件统一设置setUserAgentString
     * @param settings
     */
    public static String getUserAStr(WebSettings settings) {
        StringBuilder ua = new StringBuilder(settings==null?"":settings.getUserAgentString());
        String name = null;
        if (null!= MyBabyApp.currentUser)
            name=MyBabyApp.currentUser.getUniqueName();
        int width=MyBabyApp.px2dip(MyBabyApp.screenWidth);
        return settings==null?ua.append(" mybaby-android/").append(MyBabyApp.version)
                .append(" " + android.os.Build.DEVICE)
                .append(" " + android.os.Build.VERSION.RELEASE)
                .append(" MBUSER/").append(TextUtils.isEmpty(name) ? "" : name)
                .append(" width/").append(width).toString():
                ua.append(" MyBaby/").append(MyBabyApp.version)
                        .append(" MBUSER/").append(TextUtils.isEmpty(name) ? "" : name)
                        .append(" width/").append(width).toString();
    }


    private static String str1="webviwe_page_option";
    /**
     * 设置页面属性
     * 此处如需DUBUG调试请将清单文件中android:process="me.hibb.mybaby.android.web"去掉，不然无结果
     */
    public void setPageOption(boolean isFirstLoad){
        if (viewHolder==null||delayDisplayProgress==null)
            return;
        this.isFirstLoad=isFirstLoad;
        //String hosturl = StringUtils.getHost(url);
        //if (!(hosturl.contains("lejimami.com") || hosturl.contains("hibb.me"))) {
            GenCaches genCaches=CacheDataTask.getGenObjCache(activity,str1);
            if (genCaches!=null)
                genCaches= (GenCaches) genCaches.getSerializableObj();
            if (genCaches==null)
                getNewOption(activity, new CommListener() {
                    @Override
                    public void todosomething(WebViewOption[] webViewOptions) {
                        try {
                            setWebviewOption(webViewOptions);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            else if(!genCaches.getVersion().equals(Blog.getWebPageAttVer()))
                getNewOption(activity,null);
            else if (CacheDataTask.booleaRefush(
                    genCaches.getLastTime(), 60*24)){
                getNewOption(activity,null);
            }else {
                try {
                    setWebviewOption((WebViewOption[]) genCaches.getSerializableObj());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
      //  }
    }
    /**
     * 普通的提示框
     */
    public interface CommListener {
        void todosomething(WebViewOption[] webViewOptions);
    }
    /**
     * 获得页面属性最新属性并缓存
     */
    public static void getNewOption(final Activity activity,final CommListener listener){
        LogUtils.e("getNewOption");
        BaseRPC.rpcCallForReturnMaps(
                "页面属性webviwe_page_option——RPC",
                "bz.xmlrpc.system.htmlPageAttributes.get",
                BaseRPC.extParams(), new BaseRPC.CallbackForMaps() {
                    @Override
                    public void onSuccess(Map<?, ?> data) {
                        WebViewOption[] webViewOptions=WebViewOption.createByArray((Object[]) data.get("data"));
                        GenCaches caches=new GenCaches(webViewOptions,MapUtils.getMapStr(data, "version"),new Date());
                        CacheDataTask.putCache(activity==null?MyBabyApp.getContext():activity,caches,str1,true);
                        if (listener!=null)
                            listener.todosomething(webViewOptions);
                    }

                    @Override
                    public void onFailure(long id, XMLRPCException error) {

                    }
                });
    }
    /**
     * 执行页面属性
     * @param webViewOptions
     */
    private void setWebviewOption(final WebViewOption[] webViewOptions) throws Exception{
        final List<WebViewOption> optionList=new ArrayList<>();
        new CustomAbsClass.doSomething(activity) {
            @Override
            public void todo() {
                Pattern pattern;
                Matcher match;
                for(WebViewOption viewOption:webViewOptions){
                    pattern = Pattern.compile(viewOption.getUrlRegex());
                    match = pattern.matcher(url);
                    if (match.find()) {
                        optionList.add(viewOption);
                    }
                }
                if (optionList.size()==0)
                    return;
                long deTime=0;
                boolean isNewPage=false;
                for (WebViewOption viewOption:optionList){
                    FrameLayout.LayoutParams lp= (FrameLayout.LayoutParams) webView.getLayoutParams();
                    int height=lp.topMargin;
                    lp.leftMargin=-viewOption.getHideLeftWidth();
                    lp.topMargin=height-viewOption.getHideTopHeight();
                    lp.rightMargin=-viewOption.getHideRightWidth();
                    lp.bottomMargin=-viewOption.getHideBottomHeight();
                    webView.requestLayout();
                    viewHolder.tv_title.setText(viewOption.getTitle());
                    if (!TextUtils.isEmpty(viewOption.getOpenNewPageUrlRegex())) {
                        pattern = Pattern.compile(viewOption.getOpenNewPageUrlRegex());
                        match = pattern.matcher(url);
                        isNewPage = match.find();
                    }
                    deTime=viewOption.getDelayDisplayMillisecond();
                    if ((!TextUtils.isEmpty(viewOption.getHtmlPageScript()))&&(!viewOption.getHtmlPageScript().contains("mbnewpage")))
                        webView.loadUrl(viewOption.getHtmlPageScript());
                    if (!TextUtils.isEmpty(viewOption.getDefault_html())&&activity instanceof ParentingWebViewActivity&&isFirstLoad) {
                        //((ParentingWebViewActivity)activity).getWebviewFragment().loadUrl(viewOption.getDefault_html(),url);
                    }
                    LogUtils.e("页面属性初始URL：" + viewOption.getDefault_html());
                }
                if (!isNewPage) {
                    if (deTime > 0) {
                        delayDisplayProgress.bringToFront();
                        delayDisplayProgress.setVisibility(View.VISIBLE);
                        delayDisplayProgress.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (delayDisplayProgress!=null )
                                delayDisplayProgress.setVisibility(View.GONE);
                            }
                        },deTime);
                    }else delayDisplayProgress.setVisibility(View.GONE);
                }else {
                    //webView.stopLoading();
                    if (!url.contains(skipSourArr[1]))//否者会可能多次执行
                        setOpenPage(activity,webView,url);
                }
                viewHolder.topView.bringToFront();
            }
        };
    }

}
