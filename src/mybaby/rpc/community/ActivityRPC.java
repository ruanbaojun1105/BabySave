package mybaby.rpc.community;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import java.util.Map;

import mybaby.models.community.activity.AbstractMainActivity;
import mybaby.rpc.BaseRPC;
import mybaby.util.Utils;

public class ActivityRPC extends BaseRPC {
	private static String prifixError="XMLRPCException-ActivityRPCs";

	public interface ActivityCallback {
		void onSuccess(AbstractMainActivity activity);
		void onFailure(long id, XMLRPCException error);
	}
	public static void getById(int Id,final ActivityCallback callback){
    	final String xmlrpcMethod="bz.xmlrpc.activity.get_by_id.v5";
	    Object[] params=extParams(new Object[]{Id});

	    XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result){
            	
            	if(result != null&&result instanceof Map<?, ?> ){
					Map<?, ?> map=(Map<?, ?>)result;
					if(callback != null){
						Utils.Loge(map.toString());
						callback.onSuccess(AbstractMainActivity.createByMap(map));
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
			if(callback != null){
				callback.onFailure(0,null);
			}
	    }
	}
	
	
	public static void getByPostId(int postId,final ActivityCallback callback){ 
    	final String xmlrpcMethod="bz.xmlrpc.activity.get_by_postid.v5";
	    Object[] params=extParams(new Object[]{postId});

	    XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result){
            	
            	if(result != null){
            		if (!result.toString().isEmpty()) {
            			Map<?, ?> map=(Map<?, ?>)result;
            		
            			if(callback != null){
            				callback.onSuccess( AbstractMainActivity.createByMap(map) );
                		}
            		}else {
            			Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + ": 服务器api返回结果错误");
                    	if(callback != null){
                    		callback.onFailure(id, null);
                    	}
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
			if(callback != null){
				callback.onFailure(0,null);
			}
	    }
	}
	
	public static void getTop1Activity(int userId,final ActivityCallback callback){ 
    	final String xmlrpcMethod="bz.xmlrpc.activity.get_top1_by_user";
	    Object[] params=extParams(new Object[]{userId});
		
		XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result) {
				if (result == null || "".equals(result)) {
					if (callback != null) {
						callback.onSuccess(null);
					}
				} else {
					Map<?, ?> map = (Map<?, ?>) result;
					if (callback != null) {
						callback.onSuccess(AbstractMainActivity.createByMap(map));
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
			if(callback != null){
				callback.onFailure(0,null);
			}
	    }
	}
	
}
