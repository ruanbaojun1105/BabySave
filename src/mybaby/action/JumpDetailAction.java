package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.alibaba.sdk.android.trade.TradeConstants;
import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.ui.community.parentingpost.util.ShoppingItemUtil;
import mybaby.util.MapUtils;

/**
 * 
 * @author bj
 *打开商品详情
 */
public class JumpDetailAction extends Action implements Serializable{
	private static final long serialVersionUID = 1L;
	public static String actionUrl="mybaby_jump_taobao_item";//
	String item_id;
	String isvcode;
	String pid;

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getIsvcode() {
		return isvcode;
	}

	public void setIsvcode(String isvcode) {
		this.isvcode = isvcode;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public JumpDetailAction() {
		super();
	}

	public JumpDetailAction(String item_id, String isvcode, String pid) {
		this.item_id = item_id;
		this.isvcode = isvcode;
		this.pid = pid;
	}

	@Override
public Action setData(String url,Map<String, Object> map) {
	// TODO Auto-generated method stub
	if (url.contains(actionUrl)) {
		this.pid= MapUtils.getMapStr(map,"pid");
		this.isvcode= MapUtils.getMapStr(map,"isvcode");
		this.item_id= MapUtils.getMapStr(map,"item_id");
		return new JumpDetailAction(item_id,isvcode,pid);
	}else {
		return null;
	}
}

@Override
public Boolean excute(Activity activity, UMSocialService webviewController,
				   WebView webView, ParentingPost parentingPost) {
	// TODO Auto-generated method stub
	/*AppLinkService link = BaseAlibabaSDK.getService(AppLinkService.class);
	HashMap<String,String> params = new HashMap<String, String>();
	params.put(AppLinkService.PARAM_KEY_ISV_CODE,getIsvcode());
	params.put(AppLinkService.PARAM_KEY_PID,getPid());
	//params.put(AppLinkService.PARAM_KEY_BACK_URL,get);
	link.jumpDetail(activity,  getItem_id(), params);*/
	new ShoppingItemUtil(activity,new ShoppingItemDetailAction(getItem_id(), TradeConstants.TAOBAO_NATIVE_VIEW,getPid())).showItemDetailPage(webView);
	return  true;
}


}
