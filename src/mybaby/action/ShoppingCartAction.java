package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.ui.community.parentingpost.util.ShoppingCartUtil;

/*淘宝购物车
mybaby_shopping_taobao_cart?pid=xxx
		*/
public class ShoppingCartAction extends Action implements Serializable{

	public static String ShoppingCartAction="mybaby_shopping_taobao_cart";//.聊天·

	String pid;


	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public ShoppingCartAction(String pid) {
		this.pid = pid;
	}

	public ShoppingCartAction() {
		super();
	}

	@Override
	public Action setData(String url,Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (url.contains(ShoppingCartAction)) {
			this.pid=(String) map.get("pid");
			return new ShoppingCartAction(pid);
		}else {
			return null;
		}

	}

	@Override
	public Boolean excute(Activity activity, UMSocialService webviewController,
					   WebView webView, ParentingPost parentingPost) {
		// TODO Auto-generated method stub
		new ShoppingCartUtil(activity,this).showCart(webView);
		return true;
	}

}
