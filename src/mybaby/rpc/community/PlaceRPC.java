package mybaby.rpc.community;

import java.util.Map;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import mybaby.models.community.Place;
import mybaby.rpc.BaseRPC;
import mybaby.util.Utils;

public class PlaceRPC extends BaseRPC {
	private static String prifixError="XMLRPCException-PlaceRPC";
	
	public interface ListCallback {
		void onSuccess(Place[] arrPlace);
		void onFailure(long id, XMLRPCException error);
	}
	
	public interface PlaceCallback {
		void onSuccess(Place place);
		void onFailure(long id, XMLRPCException error);
	}
	
	
	public static void follow(int placeId,final Callback callback){
    	final String xmlrpcMethod="bz.xmlrpc.place.follow";
	    Object[] params=extParams(new Object[]{placeId});
	    rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}
	
	public static void unfollow(int placeId,final Callback callback){
    	final String xmlrpcMethod="bz.xmlrpc.place.unfollow";
	    Object[] params=extParams(new Object[]{placeId});
	    rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}
	
	public static void hasFollow(int placeId,final CallbackForBool callback){
    	final String xmlrpcMethod="bz.xmlrpc.place.has_follow";
	    Object[] params=extParams(new Object[]{placeId});
	    rpcCallForReturnInt(prifixError,xmlrpcMethod,params,callback);
	}

	/**
	 * 获取医院地点设置元数据字典
	 * @param callback
	 */
	public static void getPlaceHospitalSettingData(final CallbackForMaps callback){
		final String xmlrpcMethod="bz.xmlrep.place.hospital_setting_item_meta";
		rpcCallForReturnMaps(prifixError, xmlrpcMethod, null, callback);
	}

	/**
	 * 获取关注的地点
	 * 调用方法：
	 *      bz.xmlrpc.place.get_by_user
	 * @param userId
	 * @param callback
	 */
	public static void getPlacesByUserId(int userId,final ListCallback callback){
		final String xmlrpcMethod="bz.xmlrpc.place.get_by_user";
		Object[] params=extParams(new Object[]{userId});
		rpcCallForReturnObjs(prifixError, xmlrpcMethod, params, new CallbackForObjs() {
			@Override
			public void onSuccess(Object[] data) {
				callback.onSuccess(Place.createByArray(data));
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				callback.onFailure(id,error);
			}
		});
	}

	public static void getById(int placeId,final PlaceCallback callback){
    	final String xmlrpcMethod="bz.xmlrpc.place.get";
	    Object[] params=extParams(new Object[]{placeId});
	    
	    XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result){
            	if(result != null){
            		if (result instanceof Map<?, ?>) {
            			Map<?, ?> map=(Map<?, ?>)result;
                		
                		if(callback != null){
                    		callback.onSuccess( Place.createByMap(map) );
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
	    }
	}
	
	public static void locate(double longitude,double latitude,final PlaceCallback callback){
		final String xmlrpcMethod="bz.xmlrpc.place.locate";
	    Object[] params=extParams(new Object[]{longitude,latitude});
	    
	    XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result){
            	if(result != null && !"".equals(result)){
            		Map<?, ?> map=(Map<?, ?>)result;
            		
            		if(callback != null){
                		callback.onSuccess( Place.createByMap(map));
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
