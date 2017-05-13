package mybaby.ui.community.parentingpost.util;

/**
 * Created by LeJi_BJ on 2016/3/9.
 */
import android.app.Activity;
import android.view.View;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.trade.TradeService;
import com.alibaba.sdk.android.trade.callback.TradeProcessCallback;
import com.alibaba.sdk.android.trade.model.TradeResult;
import com.alibaba.sdk.android.trade.page.MyOrdersPage;

import mybaby.action.ShoppingOrderAction;

public class ShoppingOrderUtil {

    Activity activity;
    ShoppingOrderAction orderAction;


    public ShoppingOrderUtil(Activity activity, ShoppingOrderAction orderAction) {
        this.activity = activity;
        this.orderAction = orderAction;
    }
    public void showMyOrdersPage(View view){
        TradeService tradeService = AlibabaSDK.getService(TradeService.class);
        /**
         * 我的订单页
         *
         * @param status
         *            默认跳转页面；填写：0：全部；1：待付款；2：待发货；3：待收货；4：待评价。若传入的不是这几个数字，则跳转到“全部”页面且“allOrder”失效
         * @param allOrder true：显示全部订单。False：显示当前appKey下的订单
         *
         */
        MyOrdersPage myOrdersPage = new MyOrdersPage(0, orderAction.isAll());
        tradeService.show(myOrdersPage, null, activity, null, new TradeProcessCallback(){

            @Override
            public void onFailure(int code, String msg) {
                //Toast.makeText(activity, "失败 "+code+msg,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPaySuccess(TradeResult tradeResult) {
                try {
                    activity.getActionBar().hide();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Toast.makeText(activity, "成功", Toast.LENGTH_SHORT).show();
            }});
    }

   /* public void showOrder(View view) {

        if (!TextUtils.isEmpty(orderAction.getPid())) {
            showTaokeOrder(view);
            return;
        }

        OrderItem orderItem = new OrderItem();
        orderItem.itemId = orderAction.getPid();
        orderItem.quantity = 1;

        *//*OrderItem orderItem2 = new OrderItem();
        orderItem2.itemId = inputDatas2[0];
        orderItem2.quantity = 1;
        orderItem2.skuId = inputDatas2[1];*//*
        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        orderItems.add(orderItem);
        if(!one)
            orderItems.add(orderItem2);
        AlibabaSDK.getService(OrderService.class).showOrder(activity, new TradeProcessCallback() {

            @Override
            public void onPaySuccess(TradeResult tradeResult) {
                Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String msg) {
                if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
                    Toast.makeText(activity, "确认交易订单失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "交易异常" + code, Toast.LENGTH_SHORT).show();
                }
            }
        }, orderItems);
    }

    public void showTaokeOrder(View view) {
        String[] str=EnvData.ItemID[index].split(",");
        TextView textView = (TextView) this.findViewById(R.id.orderInputData);
        String inputData = textView.getText().toString();
        String[] inputDatas2 = new String[2];
        String[] inputDatas = new String[2];
        if (StringUtils.isEmpty(inputData)) {
            inputDatas[0] =  str[0];
            inputDatas[1] = (String) EnvData.Pid[index];

        }

        else {
            inputDatas = inputData.split(",");
            Pattern p = Pattern.compile("[0-9]*");
            if (inputDatas .length<2||!p.matcher(inputDatas[0]).matches()||!p.matcher(inputDatas[1]).matches()) {
                Toast.makeText(activity, "input format err", Toast.LENGTH_LONG).show();
                return;
            }
        }
        OrderItem orderItem = new OrderItem();
        orderItem.itemId = inputDatas[0];
        orderItem.quantity = 1;
        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        orderItems.add(orderItem);

        TaokeParams taokeParams = new TaokeParams();
        taokeParams.pid = inputDatas[1];
        AlibabaSDK.getService(OrderService.class).showTaokeOrder(activity, new TradeProcessCallback() {

            @Override
            public void onPaySuccess(TradeResult tradeResult) {
                Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String msg) {
                if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
                    Toast.makeText(activity, "确认交易订单失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "交易异常" + code, Toast.LENGTH_SHORT).show();
                }
            }
        }, orderItem, taokeParams);
    }*/

}
