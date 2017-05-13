package mybaby.rpc.community;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import java.util.Map;

import mybaby.models.community.Topic;
import mybaby.rpc.BaseRPC;
import mybaby.util.Utils;

public class TopicRPC extends BaseRPC {
	private static String prifixError="XMLRPCException-TopicRPC";
	public interface ListCallback {
		void onSuccess(Topic topic);
		void onFailure(long id, XMLRPCException error);
	}
	
	public static void add(final Topic topic,final ListCallback callback){
		final String xmlrpcMethod="bz.xmlrpc.topic.add";
	    Object[] params=extParams(new Object[]{ topic.getTitle(),
	    										topic.getContent(),
	    										(topic.getCategory() != null)?topic.getCategory().getId():0,
	    										(topic.getCategory() != null)?topic.getCategory().getTitle():"",
	    										(topic.getPlace() != null)?topic.getPlace().getMap():""	
	    									});
	    
		XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result){
            	if(result != null){
            		if(callback != null){
            			Map<?, ?> map = (Map<?, ?>) result;
            			//暂时没有加入media
            			//Utils.Loge(new JSONObject(map).toString());
                		callback.onSuccess(Topic.createByMap(map));
                	}
            	}else{
            		Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + ": 服务器api返回结果错误");
                	if(callback != null){
                		callback.onFailure(id, null);
                	}
            	}
            }
	    
            @Override
            public void onFailure(long id, XMLRPCException error){
            	Utils.LogV("MyBaby", prifixError + xmlrpcMethod + "-onFailure: " + error.getMessage());
            	if(callback != null){
            		callback.onFailure(id, error);
            	}
            }
	    };
	    
	    try {
	        getClient().callAsync(nCallback,xmlrpcMethod, params);
	    } catch ( Exception e) {
	    	Utils.LogV("MyBaby", prifixError + "-" +  xmlrpcMethod + "-catch: " + e.getMessage());
	    }
	    
	}
	/**
	 * 删除话题
	 * @param topicId
	 * @param callback
	 */
	public static void delete(int topicId,final Callback callback){
    	final String xmlrpcMethod="bz.xmlrpc.topic.delete";
	    Object[] params=extParams(new Object[]{topicId});
	    rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}
	/**
	 * 不感兴趣*（拉黑）
	 * @param callback
	 */
	public static void pullBlack(int userid,final Callback callback){
    	final String xmlrpcMethod="bz.xmlrpc.report.pull_black";
	    Object[] params=extParams(new Object[]{userid+""});
	    rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}
	/**
	 * bz.xmlrpc.activity.top
	 *入口参数：
	 * $_activity_id 动态id
	 *返回结果
	 * 1--成功 0-－失败
	 */
	public static void top(int activityId,final CallbackForBool callback){
		final String xmlrpcMethod="bz.xmlrpc.activity.top";
		Object[] params=extParams(new Object[]{activityId+""});
		rpcCallForReturnInt(prifixError, xmlrpcMethod, params, callback);
	}
	/**
	 * 取消置顶
	 *调用方法：
	 * bz.xmlrpc.activity.cancle_top
	 *入口参数：
	 * $_activity_id 动态id
	 */
	public static void cancle_top(int activityId,final CallbackForBool callback){
		final String xmlrpcMethod="bz.xmlrpc.activity.cancle_top";
		Object[] params=extParams(new Object[]{activityId+""});
		rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}
	/**
	 * 设置精华推荐
	 *调用方法：
	 * bz.xmlrpc.activity.essence
	 *入口参数：
	 * $_activity_id 动态id
	 */
	public static void essence(int activityId,final CallbackForBool callback){
		final String xmlrpcMethod="bz.xmlrpc.activity.essence";
		Object[] params=extParams(new Object[]{activityId});
		rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}
	/**
	 * 取消精华
	 *调用方法：
	 * bz.xmlrpc.activity.cancle_essence
	 *入口参数：
	 * $_activity_id 动态id
	 */
	public static void cancle_essence(int activityId,final CallbackForBool callback){
		final String xmlrpcMethod="bz.xmlrpc.activity.cancle_essence";
		Object[] params=extParams(new Object[]{activityId+""});
		rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}
}
