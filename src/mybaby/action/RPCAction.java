package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.util.MapUtils;

/**
 * 增加支持根据特定rpc打开动态列表的Action,具体如下：
 mybaby_activity_list?title=xxx&rpc=bz.xmlrpc.xxxxx
 注：服务端rpc方法参数按标准的动态列表参数，返回内容也是标准的动态列表内容
 right_button: 0－表示没有按钮，1－表示发帖按钮(加号)，2－表示写日记按钮(相机)
 *
 */
public class RPCAction extends Action implements Serializable{
	private static final long serialVersionUID = 1L;
	public static String actionUrl="mybaby_activity_list";//
	private String title;
	private String rpc;
	private int right_button;
	private boolean refresh_notification;
	private boolean need_refresh;
	private String ext_params;
	private String bar_color;
	private String icon_url;
	private Map<String, Object> rpcmap;

	public String getIcon_url() {
		return icon_url;
	}

	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}

	public String getBar_color() {
		return bar_color;
	}

	public void setBar_color(String bar_color) {
		this.bar_color = bar_color;
	}

	public boolean getRefresh_notification() {
		return refresh_notification;
	}

	public void setRefresh_notification(boolean refresh_notification) {
		this.refresh_notification = refresh_notification;
	}

	public String getExt_params() {
		return ext_params;
	}

	public void setExt_params(String ext_params) {
		this.ext_params = ext_params;
	}

	public boolean isNeed_refresh() {
		return need_refresh;
	}

	public void setNeed_refresh(boolean need_refresh) {
		this.need_refresh = need_refresh;
	}

	public int getRight_button() {
		return right_button;
	}

	public void setRight_button(int right_button) {
		this.right_button = right_button;
	}

	public String getRpc() {
		return rpc;
	}

	public void setRpc(String rpc) {
		this.rpc = rpc;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, Object> getRpcmap() {
		return rpcmap;
	}

	public void setRpcmap(Map<String, Object> rpcmap) {
		this.rpcmap = rpcmap;
	}

	public RPCAction(String title, String rpc, int right_button, boolean refresh_notification,boolean need_refresh,String ext_params,String bar_color,String icon_url) {
		this.title = title;
		this.rpc = rpc;
		this.right_button = right_button;
		this.refresh_notification = refresh_notification;
		this.need_refresh = need_refresh;
		this.ext_params = ext_params;
		this.bar_color = bar_color;
		this.icon_url = icon_url;
	}

	public RPCAction(Map<String, Object> rpcmap) {
		this.rpcmap = rpcmap;
	}

	public RPCAction() {
		super();
	}

@Override
public Action setData(String url,Map<String, Object> map) {
	// TODO Auto-generated method stub
	if (url.contains(actionUrl)) {
		//Map<String, Object> map=Action.getHeader(getReal_url());
		this.rpcmap=map;
		this.title=MapUtils.getMapStr(map,"title");
		this.rpc=MapUtils.getMapStr(map,"rpc");
		this.right_button=MapUtils.getMapInt(map, "right_button");
		this.refresh_notification=(MapUtils.getMapInt(map, "refresh_notification")==1);
		this.need_refresh=(MapUtils.getMapInt(map,"need_refresh")==1);
		this.ext_params=MapUtils.getMapStr(map,"ext_params");
		this.bar_color=MapUtils.getMapStr(map,"bar_color");
		this.icon_url=MapUtils.getMapStr(map,"icon_url");
		//Action action=new RPCAction(title,rpc,right_button,refresh_notification,need_refresh,ext_params,bar_color,icon_url);
		Action action=new RPCAction(rpcmap);
		action.setActiontTitle(title);
		return action;
	}else {
		return null;
	}
}

@Override
public Boolean excute(Activity activity, UMSocialService webviewController,
				   WebView webView, ParentingPost parentingPost) {
	// TODO Auto-generated method stub
	CustomAbsClass.starSuitOpenRPCIntent(activity,getRpcmap());
	return  true;
}


}
