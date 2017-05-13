
package me.hibb.mybaby.android.wxapi;

import mybaby.util.Utils;


import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.umeng.socialize.weixin.view.WXCallbackActivity;
//微信固定回调包，不可修改
public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler{
	
	@Override
	public void onReq(BaseReq req) {
		// TODO Auto-generated method stub
		super.onReq(req);
		Utils.Loge(req.toString());
		
	}
}
