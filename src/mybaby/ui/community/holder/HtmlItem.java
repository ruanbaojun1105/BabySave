package mybaby.ui.community.holder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import me.hibb.mybaby.android.R;
import mybaby.models.community.item.CommunityHtmlItem;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.parentingpost.util.WebViewUtil;
import mybaby.ui.main.HomeFragment;
import mybaby.ui.widget.CusWebView;
import mybaby.util.LogUtils;

//分享
public class HtmlItem  extends ItemState {
	private WebViewUtil webViewUtil;
	final int RIGHT = 0;
	final int LEFT = 1;
	//private GestureDetectorCompat gestureDetector;

	public HtmlItem() {
	}

	public static class HtmlHolder extends RecyclerView.ViewHolder{
		public CusWebView activity_webView;
		View line_bg;

		public HtmlHolder(View view) {
			super(view);
			activity_webView= (CusWebView) view.findViewById(R.id.activity_webview);
			line_bg=view.findViewById(R.id.line_bg);
		}
	}
	@Override
	public void onBindViewHolder(Context context, RecyclerView.ViewHolder vHolder, View parentView, int position, boolean hasVisiPlace, HtmlItem.SetWebViewOnTouchListener listener) {
		if(vHolder instanceof HtmlHolder){
			HtmlHolder holder= (HtmlHolder) vHolder;
			bindDatas(context, (CommunityHtmlItem) this,listener,holder);
		}

	}

	public static RecyclerView.ViewHolder getHolder(ViewGroup parent) {
		View convertView =LayoutInflater.from(parent.getContext())
				.inflate(R.layout.community_all_listitem_html, parent, false);
		HtmlHolder holder = new HtmlHolder(convertView);
		return holder;
	}

	@Override
	public int getStateType() {
		return ItemState.HtmlHolderTYPE_6;
	}
	@Override
	public View getItemView(View convertView, LayoutInflater inflater, Object data, Activity activity, boolean hasVisiPlace, int parentId, SetWebViewOnTouchListener listener) {
		HtmlHolder holder;
		CommunityHtmlItem obj= (CommunityHtmlItem) data;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.community_all_listitem_html, null);
			holder = new HtmlHolder(convertView);
			initWebView(convertView.getContext(), holder.activity_webView,listener);
			convertView.setTag(R.id.activityitem_web,holder);
		}else{
			holder = (HtmlHolder)convertView.getTag(R.id.activityitem_web);
		}
		bindDatas(activity, obj,listener,holder);
		return convertView;
	}

	public void bindDatas(Context context,final CommunityHtmlItem obj, final SetWebViewOnTouchListener listener,final HtmlHolder holder) {
		// TODO Auto-generated method stub
		// 主视图容器添加view
		LogUtils.e(MyBabyApp.screenWidth + "宽度比例" + obj.getAspect_ratio());
		LinearLayout.LayoutParams lParamsa= (LinearLayout.LayoutParams) holder.activity_webView.getLayoutParams();
		lParamsa.height=(int) (MyBabyApp.screenWidth*obj.getAspect_ratio());
		lParamsa.width=MyBabyApp.screenWidth;
		holder.activity_webView.requestLayout();

		initWebView(context, holder.activity_webView,listener);

		holder.activity_webView.clearMatches();
		//holder.activity_webView.clearView();
		//holder.activity_webView.clearCache(true);
		holder.activity_webView.clearHistory();
		holder.activity_webView.clearFormData();
		holder.activity_webView.resumeTimers();
		holder.activity_webView.post(new Runnable() {
			@Override
			public void run() {
				if (TextUtils.isEmpty(obj.getTop_border_color())){
					holder.line_bg.setBackgroundColor(Color.parseColor("#eaeaea"));
				}else {
					holder.line_bg.setBackgroundColor(Color.parseColor("#" + obj.getTop_border_color()));
				}
				holder.activity_webView.loadDataWithBaseURL(null, obj.getHtml(), "text/html", "utf-8", null);
				holder.activity_webView.requestLayout();
				holder.activity_webView.invalidate();
			}
		});
	}
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView(final Context context,final CusWebView webView, final SetWebViewOnTouchListener listener) {
		// TODO Auto-generated method stub
		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		//webView.setInitialScale(80);// 浏览器的缩放比例
		webView.getSettings().setUseWideViewPort(false);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);//关闭webview中缓存
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);//设置滚动条隐藏
		webView.getSettings().setRenderPriority(RenderPriority.LOW);//设置渲染优先级
		webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webView.getSettings().setUserAgentString(WebViewUtil.getUserAStr(webView.getSettings()));
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
		webView.setScrollContainer(false);

		webView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				return true;
			}
		});
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				try {
					//return ParentingWebViewActivity.filterUrl(context,url)?true:super.shouldOverrideUrlLoading(view, url);
					if (webViewUtil == null)
						webViewUtil = new WebViewUtil((Activity) context, url, "", webView, null, null, null, HomeFragment.homeController, this);
					webViewUtil.setUrl(url);
					return webViewUtil.deilOverUrl();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
		});

	}


	public interface SetWebViewOnTouchListener{
		void onLeftOrRight(boolean disallowInterceptTouchEvent);
	}

}
