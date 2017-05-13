package mybaby.ui.community.parentingpost.util;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.ResultCode;
import com.alibaba.sdk.android.trade.ItemService;
import com.alibaba.sdk.android.trade.PromotionService;
import com.alibaba.sdk.android.trade.TradeConstants;
import com.alibaba.sdk.android.trade.TradeService;
import com.alibaba.sdk.android.trade.callback.TradeProcessCallback;
import com.alibaba.sdk.android.trade.model.TaokeParams;
import com.alibaba.sdk.android.trade.model.TradeResult;
import com.alibaba.sdk.android.trade.page.ItemDetailPage;

import java.util.HashMap;
import java.util.Map;

import mybaby.action.ShoppingItemDetailAction;

public class ShoppingItemUtil {

	Activity activity;
	ShoppingItemDetailAction itemDetailAction;


	public ShoppingItemUtil(Activity activity, ShoppingItemDetailAction itemDetailAction) {
		this.activity = activity;
		this.itemDetailAction = itemDetailAction;
	}
	public void showItemDetailPage(View view){
		TradeService tradeService = AlibabaSDK.getService(TradeService.class);
		/**
		 * 商品详情页
		 *
		 * @param itemId
		 *            商品id.支持标准的商品id，eg.37196464781；同时支持openItemId，eg.AAHd5d-HAAeGwJedwSnHktBI；必填，不允许为null；
		 * @param exParams
		 *            特殊业务扩展字段；选填，允许为null；目前支持3个参数：
		 *            1、TradeConstants.ITEM_DETAIL_VIEW_TYPE：启动页面类型，分为BAICHUAN_H5_VIEW、TAOBAO_H5_VIEW(以淘宝H5方式打开详情页)、TAOBAO_NATIVE_VIEW(唤起手机淘宝客户端打开详情页)。
		 *            2、TradeConstants.ISV_CODE(ISV_CODE用法可参看：http://baichuan.taobao.com/doc2/detail.htm?treeId=30&articleId=102596&docType=1)
		 *            3、TradeConstants. TAOBAO_BACK_URL:设置启动手淘native页面后的返回页面
		 */
		Map<String, String> params = new HashMap<String, String>();
		params.put(TradeConstants.ITEM_DETAIL_VIEW_TYPE, itemDetailAction.getView_type());
		params.put(TradeConstants.TAOBAO_BACK_URL, itemDetailAction.getView_type());
		//params.put(TradeConstants.ISV_CODE,"回到辣妈说");
		ItemDetailPage itemDetailPage = new ItemDetailPage(itemDetailAction.getItem_id(), params);
		TaokeParams taokeParams = new TaokeParams(); //若非淘客taokeParams设置为null即可
		taokeParams.pid = itemDetailAction.getPid();
		tradeService.show(itemDetailPage, taokeParams, activity,null, new TradeProcessCallback() {

			@Override
			public void onFailure(int code, String msg) {
				//Toast.makeText(activity, "失败 " + code + msg,Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onPaySuccess(TradeResult tradeResult) {
				try {
					activity.getActionBar().hide();
				} catch (Exception e) {
					e.printStackTrace();
				}
				//Toast.makeText(activity, "成功", Toast.LENGTH_SHORT).show();
			}
		});
	}
	/*public void showItemDetail(View view) {
		if (itemDetailAction==null)
			return;
		Pattern p = Pattern.compile("[0-9]*");
		if (!p.matcher(itemDetailAction.getItem_id()).matches() ) {
			Toast.makeText(activity, "taobao id format err", Toast.LENGTH_LONG).show();
			return;
		}
		if (!TextUtils.isEmpty(itemDetailAction.getPid())){
			openTaokeDetail(view);
			return;
		}
		UiSettings taeWebViewUiSettings = new UiSettings();
		AlibabaSDK.getService(ItemService.class).showItemDetailByOpenItemId(activity, new TradeProcessCallback() {

			@Override
			public void onPaySuccess(TradeResult tradeResult) {
				Toast.makeText(activity,
						"支付成功" + tradeResult.paySuccessOrders + " fail:" + tradeResult.payFailedOrders,
						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onFailure(int code, String msg) {
				if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
					Toast.makeText(activity, "确认交易订单失败", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(activity, "交易取消" + code, Toast.LENGTH_SHORT).show();
				}
			}
		}, taeWebViewUiSettings,itemDetailAction.getItem_id() , itemDetailAction.getView_type(), null);
	}
/*
	public void openTaokeDetail(View view) {
		if (itemDetailAction==null)
			return;
		Pattern p = Pattern.compile("[0-9]*");
		if (!p.matcher(itemDetailAction.getItem_id()).matches() ) {
			Toast.makeText(activity, "taobao id format err", Toast.LENGTH_LONG).show();
			return;
		}

		TaokeParams taokeParams = new TaokeParams();
		taokeParams.pid = itemDetailAction.getPid();
		AlibabaSDK.getService(ItemService.class).showTaokeItemDetailByOpenItemId(activity, new TradeProcessCallback() {

			@Override
			public void onPaySuccess(TradeResult tradeResult) {
				Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onFailure(int code, String msg) {
				if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
					Toast.makeText(activity, "确认交易订单失败", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(activity, "交易取消" + code, Toast.LENGTH_SHORT).show();
				}
			}
		}, null, itemDetailAction.getItem_id(), itemDetailAction.getView_type(), null, taokeParams);
	}*/

	public void showETicketDetail(View view) {
		AlibabaSDK.getService(PromotionService.class).showETicketDetail(activity, 931159680463903l,
				new TradeProcessCallback() {

					@Override
					public void onPaySuccess(TradeResult tradeResult) {
						Toast.makeText(activity,
								"支付成功" + tradeResult.paySuccessOrders + " fail:" + tradeResult.payFailedOrders,
								Toast.LENGTH_SHORT).show();

					}

					@Override
					public void onFailure(int code, String msg) {
						if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
							Toast.makeText(activity, "确认交易订单失败", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(activity, "交易取消" + code, Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	public void showPromotions(View view) {
		AlibabaSDK.getService(ItemService.class).showPage(activity, null, null,
				"http://ff.win.taobao.com?des=promotions&cc=tae&sn=c测试账号0515");
		// TaeSDK.showPromotions(this, "shop", "c测试账号0515", new
		// TradeProcessCallback() {
		//
		// @Override
		// public void onPaySuccess(TradeResult tradeResult) {
		// Toast.makeText(activity,
		// "支付成功" + tradeResult.paySuccessOrders + " fail:" +
		// tradeResult.payFailedOrders,
		// Toast.LENGTH_SHORT).show();
		//
		// }
		//
		// @Override
		// public void onFailure(int code, String msg) {
		// if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
		// Toast.makeText(activity, "确认交易订单失败",
		// Toast.LENGTH_SHORT).show();
		// } else {
		// Toast.makeText(activity, "交易取消" + code,
		// Toast.LENGTH_SHORT).show();
		// }
		// }
		// });
	}

	public void showItemDetailV2(View view) {
		String inputData = "41576306115";// ctv.getText().toString();
		AlibabaSDK.getService(ItemService.class).showItemDetailByItemId(activity, new TradeProcessCallback() {

			@Override
			public void onPaySuccess(TradeResult tradeResult) {
				Toast.makeText(activity,
						"支付成功" + tradeResult.paySuccessOrders + " fail:" + tradeResult.payFailedOrders,
						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onFailure(int code, String msg) {
				if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
					Toast.makeText(activity, "确认交易订单失败", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(activity, "交易取消" + code, Toast.LENGTH_SHORT).show();
				}
			}
		}, null, Long.valueOf(inputData), 1, null);
		// }, null, 37196464781l, 1, null);
	}

	public void showTaokeItemDetailV2(View view) {
		String inputData = "22429824161";// actv.getText().toString();
		TaokeParams taokeParams = new TaokeParams();
		taokeParams.pid =itemDetailAction.getPid();
		AlibabaSDK.getService(ItemService.class).showTaokeItemDetailByItemId(activity, new TradeProcessCallback() {

			@Override
			public void onPaySuccess(TradeResult tradeResult) {
				Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onFailure(int code, String msg) {
				if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
					Toast.makeText(activity, "确认交易订单失败", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(activity, "交易取消" + code, Toast.LENGTH_SHORT).show();
				}
			}
		}, null, Long.valueOf(inputData), 1, null, taokeParams);
	}
}
