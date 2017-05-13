package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.alibaba.sdk.android.trade.TradeConstants;
import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.ui.community.parentingpost.util.ShoppingItemUtil;

/*淘宝商品详情页
		mybaby_shopping_taobao_item_detail?item_id=xxx&isvcode=xxx&view_type=x&pid=xxx
		item_id:表示商品id或混淆id
		view_type:1-淘宝h5详情页 2-百川h5详情页 3-淘宝app详情页
		pid:淘宝客id
		*/
public class ShoppingItemDetailAction extends Action implements Serializable{

	public static String ShoppingItemDetailAction="mybaby_shopping_taobao_item_detail";//.聊天·

	String item_id;
	String view_type;
	String pid;

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getView_type() {
		return view_type;
	}

	public void setView_type(String view_type) {
		this.view_type = view_type;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public ShoppingItemDetailAction(String item_id, String view_type, String pid) {
		this.item_id = item_id;
		this.view_type = view_type;
		this.pid = pid;
	}

	public ShoppingItemDetailAction() {
		super();
	}

	@Override
	public Action setData(String url,Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (url.contains(ShoppingItemDetailAction)) {
			switch (Integer.parseInt((String) map.get("view_type"))){
				case 1:this.view_type= TradeConstants.TAOBAO_H5_VIEW;
					break;
				case 2:this.view_type= TradeConstants.BAICHUAN_H5_VIEW;
					break;
				case 3:this.view_type= TradeConstants.TAOBAO_NATIVE_VIEW;
					break;
			}
			this.item_id=(String) map.get("item_id");
			this.pid=(String) map.get("pid");
			return new ShoppingItemDetailAction(item_id,view_type,pid);
		}else {
			return null;
		}
	}

	@Override
	public Boolean excute(Activity activity, UMSocialService webviewController,
					   WebView webView, ParentingPost parentingPost) {
		// TODO Auto-generated method stub
		new ShoppingItemUtil(activity,this).showItemDetailPage(webView);
		return true;
	}

}
