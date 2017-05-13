package mybaby.rpc.community;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import android.annotation.SuppressLint;
import mybaby.util.Utils;

import mybaby.models.community.activity.ReplyActivity;
import mybaby.rpc.BaseRPC;
import mybaby.util.MapUtils;
import mybaby.util.Utils;

public class ReplyRPC extends BaseRPC {
	private static String prifixError="XMLRPCException-ReplyRPC";
	
	public interface ListCallback {
		void onSuccess(boolean hasMore, int newLastId, ReplyActivity[] items) ;
		void onFailure(long id, XMLRPCException error);
	}
	
	public static void add(String content,int parentPostId,final CallbackForId callback){
    	final String xmlrpcMethod="bz.xmlrpc.reply.add";
	    Object[] params=extParams(new Object[]{content,parentPostId});
	    rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}
	
	public static void delete(int replyId,final Callback callback){
    	final String xmlrpcMethod="bz.xmlrpc.reply.delete";
	    Object[] params=extParams(new Object[]{replyId});
	    rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}
	
	
	public static void getActivities(int rootPostId,int lastId,final ListCallback callback){
    	final String xmlrpcMethod="bz.xmlrpc.reply.get_by_root";
	    Object[] params=extParams(new Object[]{rootPostId,lastId});
	    
	    XMLRPCCallback nCallback = new XMLRPCCallback() {
            @SuppressLint("NewApi") @Override
            public void onSuccess(long id, Object result){
            	if(result != null){
            		Map<?, ?> map=(Map<?, ?>)result;
            		//Utils.Loge(new JSONObject(map).toString());
            		if(callback != null){
//            			try {
//							Utils.Loge(new JSONArray(MapUtils.getArray(map, "data")).toString());
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
                		callback.onSuccess( MapUtils.getMapBool(map, "has_more"),
                						    MapUtils.getMapInt(map, "last_id"),
                						    ReplyActivity.createByArray(MapUtils.getArray(map, "data"))
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
