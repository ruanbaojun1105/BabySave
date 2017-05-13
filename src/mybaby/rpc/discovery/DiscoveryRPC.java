package mybaby.rpc.discovery;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import java.util.Map;

import mybaby.models.community.activity.LikeActivity;
import mybaby.models.discovery.Discovery;
import mybaby.models.discovery.DiscoveryObjs;
import mybaby.rpc.BaseRPC;
import mybaby.util.MapUtils;
import mybaby.util.Utils;

/**
 * Created by LeJi_BJ on 2016/2/29.
 */
public class DiscoveryRPC extends BaseRPC{
    private static String prifixError="XMLRPCException-DiscoverysRPC";

    public interface DiscoverysCallback {
        void onSuccess(DiscoveryObjs[] items);
        void onFailure();
    }
    /**功能：
            * 获取发现页的具体内容
    *调用方法：
            * bz.xmlrpc.discovery.get_items
    *入口参数：
            * 不需传任何参数
    *返回结果
    * array(
            * array(
            * 'icon' 图标,
            * 'title' 标题,
            * 'action' 动作,
            * 'noti_cat_key' 通知分类key
            * )*/
    public static void getDiscoverys(final DiscoverysCallback callback){
        final String xmlrpcMethod="bz.xmlrpc.discovery.get_items";
        Object[] params=extParams();

        XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result){
                if(result != null&&result instanceof Object[]){
                    //Map<?, ?> map=(Map<?, ?>)result;
                    if(callback != null){
                        callback.onSuccess(DiscoveryObjs.createByArray((Object[]) result));
                    }
                }else{
                    Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + ": 服务器api返回结果错误");
                    if(callback != null){
                        callback.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(long id, XMLRPCException error){
                Utils.LogV("MyBaby", prifixError + xmlrpcMethod + "-onFailure: " + error.getMessage());
                if(callback != null){
                    callback.onFailure();
                }
            }
        };

        try {
            getClient().callAsync(nCallback,xmlrpcMethod, params);
        } catch ( Exception e) {
            if(callback != null){
                callback.onFailure();
            }
            Utils.LogV("MyBaby", prifixError + "-" +  xmlrpcMethod + "-catch: " + e.getMessage());
        }
    }
}
