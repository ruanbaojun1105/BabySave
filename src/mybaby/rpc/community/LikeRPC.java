package mybaby.rpc.community;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import java.util.Map;

import mybaby.models.community.activity.LikeActivity;
import mybaby.rpc.BaseRPC;
import mybaby.util.MapUtils;
import mybaby.util.Utils;

public class LikeRPC extends BaseRPC {
	private static String prifixError="XMLRPCException-LikeRPC";

	public interface ListCallback {
		void onSuccess(boolean hasMore, int newLastId, LikeActivity[] items);
		void onFailure(long id, XMLRPCException error);
	}
	
	public static void like(int parentPostId,final CallbackForId callback){
    	final String xmlrpcMethod="bz.xmlrpc.like.like";
	    Object[] params=extParams(new Object[]{parentPostId});
	    rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}
	
	public static void unlike(int parentPostId,final Callback callback){
    	final String xmlrpcMethod="bz.xmlrpc.like.unlike";
	    Object[] params=extParams(new Object[]{parentPostId});
	    rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}
	
	public static void getActivities(int parentPostId,int lastId,final ListCallback callback){
    	final String xmlrpcMethod="bz.xmlrpc.like.get_by_post";
	    Object[] params=extParams(new Object[]{parentPostId,lastId});
	    
	    XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result){
            	if(result != null&&result instanceof Map<?, ?>){
            		Map<?, ?> map=(Map<?, ?>)result;
            		if(callback != null){
                		callback.onSuccess( MapUtils.getMapBool(map, "has_more"),
                						    MapUtils.getMapInt(map, "last_id"),
                						    LikeActivity.createByArray(MapUtils.getArray(map, "data"))
                						  );
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
}
