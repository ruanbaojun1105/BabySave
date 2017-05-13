package mybaby.rpc.notification;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import mybaby.ui.MyBabyApp;
import mybaby.models.community.TopicCategory;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.BlogRPC;
import mybaby.util.Utils;

public class TopicCategoryRPC extends BaseRPC {
	private static String prifixError="XMLRPCException-TopicCategoryRPC";
	
	public interface ListCallback {
		void onSuccess(TopicCategory[] arrCategory);
		void onFailure(long id, XMLRPCException error);
	}
	
	public static void follow(int categoryId,final Callback callback){
    	final String xmlrpcMethod="bz.xmlrpc.topic_category.follow";
	    Object[] params=extParams(new Object[]{categoryId});
	    rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}
	
	public static void unfollow(int categoryId,final Callback callback){
    	final String xmlrpcMethod="bz.xmlrpc.topic_category.unfollow";
	    Object[] params=extParams(new Object[]{categoryId});
	    rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}
	
	public static void hasFollow(int categoryId,final CallbackForBool callback){
    	final String xmlrpcMethod="bz.xmlrpc.topic_category.has_follow";
	    Object[] params=extParams(new Object[]{categoryId});
	    rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}
	
	public static void getByUser(int userId,final ListCallback callback){
    	final String xmlrpcMethod="bz.xmlrpc.topic_category.get_by_user";
	    Object[] params=extParams(new Object[]{userId});
	    getList(xmlrpcMethod,params,callback);
	}
	
	public static void getByTitle(String title,final ListCallback callback){
    	final String xmlrpcMethod="bz.xmlrpc.topic_category.get_by_title";
	    Object[] params=extParams(new Object[]{title});
	    getList(xmlrpcMethod,params,callback);
	}
	
	public static void getRecommend(final ListCallback callback){
    	final String xmlrpcMethod="bz.xmlrpc.topic_category.get_recommend";
	    Object[] params=extParams();
	    getList(xmlrpcMethod,params,callback);
	}
	
	private static void getList(final String xmlrpcMethod, final Object[] params,final ListCallback callback){ 
		if(MyBabyApp.currentUser.getUserId()<=0){ 
			BlogRPC.anonymousSignUp(new XMLRPCCallback() {
	            @Override
	            public void onSuccess(long id, Object result){
	            	getList(xmlrpcMethod,params,callback);
	            }
	            @Override
	            public void onFailure(long id, XMLRPCException error){
	            	if(callback != null){
	            		callback.onFailure(id, error);
	            	}
	            }
	        });
			return;
		}
		
		
	    XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result){
            	if(result != null){
            		if (result instanceof Object[]) {
            			Object[] rpcArray=(Object[])result;
                		
                		if(callback != null&&rpcArray!=null){
                    		callback.onSuccess( TopicCategory.createByArray(rpcArray) );
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
			if(callback != null){
				callback.onFailure(0,null);
			}
	    	Utils.LogV("MyBaby", prifixError + "-" +  xmlrpcMethod + "-catch: " + e.getMessage());
	    }
	}
}
