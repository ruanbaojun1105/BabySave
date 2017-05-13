package mybaby.rpc.community;

import android.text.TextUtils;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import java.util.List;
import java.util.Map;

import mybaby.ui.MyBabyApp;
import mybaby.models.community.Banner;
import mybaby.models.community.item.CommunityAbstractItem;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.BlogRPC;
import mybaby.ui.community.holder.ItemState;
import mybaby.util.MapUtils;
import mybaby.util.Utils;


public class CommunityItemRPC extends BaseRPC {
	private static String prifixError="XMLRPCException-CommunityItemRPC";
	
	public interface ListCallback {
	
		void onSuccess(boolean hasMore, int newLastId, List<ItemState> items, Banner[] banners);
		void onFailure(long id, XMLRPCException error);
	}
	
	public static void getForMain(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{String.valueOf(lastId)});
		getList("bz.xmlrpc.activity.get_for_main.v5",params,callback);
	}
	public static String getForMain(){
		return "bz.xmlrpc.activity.get_for_main.v5";
	}
	public static String getCommunity(){
		return "bz.xmlrpc.activity.get_for_community.v5";
	}
	public static void getCommunity(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{String.valueOf(lastId)});
		getList("bz.xmlrpc.activity.get_for_community.v5",params,callback);
	}
	public static void getForNearby(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{String.valueOf(lastId)});
		getList("bz.xmlrpc.activity.get_for_nearby.v5",params,callback);
	}
	
	public static void getForFriends(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{String.valueOf(lastId)});
		getList("bz.xmlrpc.activity.get_for_friends.v5",params,callback);
	}
	public static void getForNeighbor(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{String.valueOf(lastId)});
		getList("bz.xmlrpc.activity.get_for_neighbor.v5",params,callback);
	}
	/*
     *功能：
     *      获取热门动态
     */
	public static void getForTop(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{String.valueOf(lastId)});
		getList("bz.xmlrpc.activity.get_for_top.v5",params,callback);
	}
	/*
     *功能：
     *      获取特定话题下的全部帖子
     */
	public static void getAllByParent(int parentId,int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{parentId,String.valueOf(lastId)});
		getList("bz.xmlrpc.activity.get_all_by_parent.v5",params,callback);
	}
	/*
     *功能：
     *      获取特定话题下的热门帖子
     */
	public static void getTopByParent(int parentId,int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{String.valueOf(parentId),String.valueOf(lastId)});
		getList("bz.xmlrpc.activity.get_top_by_parent.v5",params,callback);
	}
	
	public static void getByPlace(int placeId,int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{placeId,String.valueOf(lastId)});
		getList("bz.xmlrpc.activity.get_by_place.v5",params,callback);
	}
	
	public static void getByUser(int userId,int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{userId,String.valueOf(lastId)});
		getList("bz.xmlrpc.activity.get_by_user.v5",params,callback);
	}
	
	/**
	 *  增加api:bz.xmlrpc.activity.get_my_join_like 返回我赞过的其他人的动态
 	 *  增加api:bz.xmlrpc.activity.get_my_join_reply 返回我回复过的其他人的动态
 	 *  增加api:bz.xmlrpc.activity.get_my_join 返回我赞过河我回复过的其他人的动态
 	 *  注：根据user_follow_post数据返回即可，不要返回自己的动态
	 */
	public static void getJoin_like(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{String.valueOf(lastId)});
		getList("bz.xmlrpc.activity.get_my_join_like.v5",params,callback);
	}
	public static void getJoin_reply(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{String.valueOf(lastId)});
		getList("bz.xmlrpc.activity.get_my_join_reply.v5",params,callback);
	}

	public static void getJoined(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{String.valueOf(lastId)});
		getList("bz.xmlrpc.activity.get_my_join.v5",params,callback);
	}


	//获取群的动态bz.xmlrpc.activity.get_for_group、、群空间
	/**入口参数：
	 * $post_id 群组id
	* $last_id 同上
	*/
	public static void get_for_groupActivity(int post_id ,int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{post_id,lastId});
		getList("bz.xmlrpc.activity.get_for_group.v5",params,callback);
	}

	/**
	 * 我关注的话题动态
	 * @param lastId
	 * @param callback
	 * 待API
	 * /*
	 *功能：
	 *      获取我关注的动态
	 *调用方法：
	 *      bz.xmlrpc.activity.get.for_follow_topic_categor
	 *入口参数：
	 *      $last_id     同上
	 *返回结果
	 */
	public static void getFollow_topic_categor(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{lastId});
		getList("bz.xmlrpc.activity.get.for_follow.topic_categor.v5",params,callback);
	}
	/**功能：
			* 获取0回复的动态
	*调用方法：
			* bz.xmlrep.activity.get_zero_reply
	*入口参数：
			* $last_id 同上
	*返回结果
	*/
	public static void get_zero_reply(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{lastId});
		getList("bz.xmlrpc.activity.get_zero_reply",params,callback);
	}

	/**
	 * 增加支持根据特定rpc打开动态列表
	 * @param lastId
	 * @param callback
	 */
	public static void getSuitRPCList(String rpc,int lastId,String ext_params,final ListCallback callback){
		Object[] objects=null;
		if (!TextUtils.isEmpty(ext_params)){
			try {
				/*如果用“.”作为分隔的话,必须是如下写法,String.split("\\."),这样才能正确的分隔开,不能用String.split(".");
				如果用“|”作为分隔的话,必须是如下写法,String.split("\\|"),这样才能正确的分隔开,不能用String.split("|");*/
				String[] params=ext_params.split("\\|");
				if (params!=null&&params.length>0) {
                    objects = new Object[1 + params.length];
                    objects[0]=lastId;
                    for (int i=0;i<params.length;i++) {
                        objects[i+1]=params[i];
                    }
                }else objects=new Object[]{lastId};
			} catch (Exception e) {
				objects=new Object[]{lastId,ext_params};
			}
		}else
			objects=new Object[]{lastId};

		Object[] params=extParams(objects);
		getList(rpc,params,callback);
	}
	/**
	 * 返回列表数据回调
	 * @param xmlrpcMethod
	 * @param params
	 * @param callback
	 */
	public static void getList(final String xmlrpcMethod,final Object[] params,final ListCallback callback){
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
            		if (result instanceof Map<?, ?>) {
            			Map<?, ?> map=(Map<?, ?>)result;
                		if(callback != null){
							//Utils.Loge(new JSONObject(map).toString());
								Banner[] banners=null;
								if(map.containsKey("banner"))
									banners=Banner.createByArray(MapUtils.getArray(map, "banner"));
								List<ItemState> itemList=CommunityAbstractItem.createByArray(MapUtils.getArray(map, "data"));
								callback.onSuccess(
										MapUtils.getMapBool(map, "has_more"),
										MapUtils.getMapInt(map, "last_id"),
										itemList,
										banners
								);
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
