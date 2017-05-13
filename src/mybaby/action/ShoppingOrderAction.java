package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.ui.community.parentingpost.util.ShoppingOrderUtil;

/*淘宝订单
mybaby_shopping_taobao_order?pid=xxx&all=1
all: 1-显示所有淘宝订单，0-只显示辣妈说的订单
		*/
public class ShoppingOrderAction extends Action implements Serializable{

	public static String ShoppingOrderAction="mybaby_shopping_taobao_order";//.聊天·

	boolean all;//all: 1-显示所有淘宝订单，0-只显示辣妈说的订单
	String pid;

	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public ShoppingOrderAction(boolean all, String pid) {
		this.all = all;
		this.pid = pid;
	}

	public ShoppingOrderAction() {
		super();
	}

	@Override
	public Action setData(String url,Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (url.contains(ShoppingOrderAction)) {
			this.all=(Integer.parseInt((String) map.get("all"))==1);
			this.pid=(String) map.get("pid");
			return new ShoppingOrderAction(all,pid);
		}else {
			return null;
		}
		
	}

	@Override
	public Boolean excute(Activity activity, UMSocialService webviewController,
					   WebView webView, ParentingPost parentingPost) {
		// TODO Auto-generated method stub
		new ShoppingOrderUtil(activity,this).showMyOrdersPage(webView);
		return true;
	}

}
