package mybaby.rpc.community;

import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.rpc.BaseRPC;
import mybaby.util.MapUtils;
import mybaby.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import android.annotation.SuppressLint;
import mybaby.util.Utils;

public class ParentingPostRPC extends BaseRPC{
		private static String prifixError="XMLRPCException-ParentingPostRPC";

		public interface ParentingListCallback {
			void onSuccess(ParentingPost[] parentingPosts);
			void onFailure(long id, XMLRPCException error);
		}
		
		 /**    $userId             用户id
		 *      $selfPersonInfo     个人信息  可传空
		 *      $babiesPersonInfo   宝宝信息  可传空
		 *      $familiesPersonInfo  家人信息 可传空
		 *      $maxTime             最大时间戳, 默认传0，加载更多时传上一页最后一条的datetime对应的时间戳,以秒计算
		*/
		
		public static void getParentingList(int userId,String selfPersonInfo,String babiesPersonInfo,
				String familiesPersonInfo,Long maxTime,final ParentingListCallback callback){
	    	final String xmlrpcMethod="bz.xmlrpc.getParentingPosts";
		    Object[] params=new Object[]{userId,selfPersonInfo,babiesPersonInfo,familiesPersonInfo,Long.toString(maxTime/1000)};//不知道long为什么传不到服务器
		    
		    XMLRPCCallback nCallback = new XMLRPCCallback() {
	            @SuppressLint("NewApi") @Override
	            public void onSuccess(long id, Object result){
	            	if(result != null){
	            		if(callback != null){
	                		callback.onSuccess(ParentingPost.createByArray((Object[]) result));
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
