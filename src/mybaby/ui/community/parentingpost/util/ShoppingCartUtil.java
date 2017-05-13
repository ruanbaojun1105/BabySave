package mybaby.ui.community.parentingpost.util;

/**
 * Created by LeJi_BJ on 2016/3/9.
 */
import android.app.Activity;
import android.view.View;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.trade.CartService;
import com.alibaba.sdk.android.trade.callback.TradeProcessCallback;
import com.alibaba.sdk.android.trade.model.TradeResult;

import mybaby.action.ShoppingCartAction;

public class ShoppingCartUtil {

    Activity activity;
    ShoppingCartAction cartAction;


    public ShoppingCartUtil(Activity activity, ShoppingCartAction cartAction) {
        this.activity = activity;
        this.cartAction = cartAction;
    }    
    
    public void showCart(View view) {
        AlibabaSDK.getService(CartService.class).showCart(activity, new TradeProcessCallback() {

            @Override
            public void onPaySuccess(TradeResult tradeResult) {
                try {
                    activity.getActionBar().hide();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Toast.makeText(activity, "添加购物车成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String msg) {
                /*if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
                    Toast.makeText(activity, "打开购物车失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "取消购物车失败" + code, Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    /*public void addItem2Cart(View view) {

        AlibabaSDK.getService(CartService.class).addItem2Cart(activity, new TradeProcessCallback() {

            @Override
            public void onPaySuccess(TradeResult tradeResult) {
                Toast.makeText(activity, "添加购物车成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String msg) {
                if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
                    Toast.makeText(activity, "添加购物车失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "取消添加购物车" + code, Toast.LENGTH_SHORT).show();
                }
            }
        }, null, itemDetailAction.getItem_id(), null);
    }*/

    /*public void addTaoKeItem2Cart(View view) {
        Pattern p = Pattern.compile("[0-9]*");
        if (!p.matcher(itemDetailAction.getItem_id()).matches()||!p.matcher(itemDetailAction.getPid()).matches()) {
            Toast.makeText(activity, "input format err", Toast.LENGTH_LONG).show();
            return;
        }
        TaokeParams taokeParams = new TaokeParams();
        taokeParams.pid = itemDetailAction.getPid();
        *//*if (inputDatas.length > 2) {
            taokeParams.unionId = inputDatas[2];
        }
        if (inputDatas.length > 3) {
            taokeParams.subPid = inputDatas[3];
        }*//*
        AlibabaSDK.getService(CartService.class).addTaoKeItem2Cart(activity, new TradeProcessCallback() {
            @Override
            public void onPaySuccess(TradeResult tradeResult) {
                Toast.makeText(activity, "添加淘客商品到购物车成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int code, String msg) {
                if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
                    Toast.makeText(activity, "添加淘客商品到购物车失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "添加淘客商品到购物车取消" + code, Toast.LENGTH_SHORT).show();
                }
            }
        }, null, itemDetailAction.getItem_id(), null, taokeParams);
    }*/
}
