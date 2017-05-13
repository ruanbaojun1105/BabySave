package mybaby.rpc.notification;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;
import org.xutils.common.util.LogUtil;

import java.util.Map;

import mybaby.ui.MyBabyApp;
import mybaby.models.notification.Notification;
import mybaby.models.notification.NotificationCategory;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.BlogRPC;
import mybaby.util.MapUtils;
import mybaby.util.Utils;

public class NotificationRPC extends BaseRPC {
	private static String prifixError="XMLRPCException-NotificationRPC";
	
	public interface SummaryCallback {
		void onSuccess(long lastPostTime, boolean gencommon_has_new, boolean community_has_new,
					   boolean nearby_has_new, NotificationCategory[] notificationCategorys,
					   NotificationCategory[] discoveryNcfcs,NotificationCategory[] otherNcfcs);
		void onFailure(long id, XMLRPCException error);
	}
	
	public interface ListCallback {
		void onSuccess(boolean hasMore, int newLastId, Notification[] items);
		void onFailure(long id, XMLRPCException error);
	}
	
	public static void getSummary(int unreadcount,final SummaryCallback callback){
		if(MyBabyApp.currentUser.getUserId()<=0){ 
			BlogRPC.anonymousSignUp(new XMLRPCCallback() {
	            @Override
	            public void onSuccess(long id, Object result){
	            	getSummary(0, callback);
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

		/**功能：
		* 检查是否有新的消息v3
				*调用方法：
		* bz.xmlrpc.notification.get_summary.v3
				*入口参数：
		* 'is_unread' : >0－-未读 否则所有的
				*返回结果
				* array(
				* 'lastPostTime' 用户最后写日记上传的时间，用来自动再多个设备上同步日记
				* 'common_notification_has_new' 消息是否有新的通知
				* 'community_has_new' 社区是否有新动态
				* 'nearby_has_new' 周边是否有新动态
				* 'item' : 分别对应的项目详细信息，如下：
		 		//* 'type' : 类别 1－-默认 2－－会话
				* 'category_key' key,
				'image_url' 图标url,
				'title' ,
				'newest_datetime' 最新消息时间,
				'newest_desc' 最新消息摘要,
				'unread_count' 未读消息数量,
				'is_strong_remind' ,
				'action' 动作'
		 		'tribe_id' im的群组d
		 		'discoverys' : 发现页的通知分类, 对别对应的项目详细信息同item
				**/
		final String xmlrpcMethod="bz.xmlrpc.notification.get_summary.v5";
	    Object[] params=extParams(new Object[]{unreadcount});
		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result){
				if(result != null){
					if (result instanceof Map<?, ?>) {
						Map<?, ?> map = (Map<?, ?>)result;
						if(callback != null){
							if (map!=null) {
								try {
									LogUtil.e("userid"+MyBabyApp.currentUser.getUserId() + "");
									callback.onSuccess(
											MapUtils.getMapDateLong(map, "lastPostTime"),
											MapUtils.getMapBool(map, "common_notification_has_new"),
											MapUtils.getMapBool(map, "community_has_new"),
											MapUtils.getMapBool(map, "nearby_has_new"),
											NotificationCategory.createByArray(MapUtils.getArray(map, "item")),
											NotificationCategory.createByArray(MapUtils.getArray(map, "discoverys")),
											NotificationCategory.createByArray(MapUtils.getArray(map, "others"))
									);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
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
		try{
			getClient().callAsync(nCallback,xmlrpcMethod, params);
		} catch ( Exception e) {
			if(callback != null){
				callback.onFailure(0,null);
			}
			Utils.LogV("MyBaby", prifixError + "-" +  xmlrpcMethod + "-catch: " + e.getMessage());
		}

	}



	private static void getCommonList(final String xmlrpcMethod, final Object[] params,final ListCallback callback){

		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result){
				if(result != null){
					if (result instanceof Map<?, ?>) {
						Map<?, ?> map=(Map<?, ?>)result;
						if(callback != null){
							callback.onSuccess( MapUtils.getMapBool(map, "has_more"),
									MapUtils.getMapInt(map, "last_id"),
									Notification.createByArray(MapUtils.getArray(map, "data"))
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

		try{
			getClient().callAsync(nCallback,xmlrpcMethod, params);
		} catch ( Exception e) {
			if(callback != null){
				callback.onFailure(0,null);
			}
			Utils.LogV("MyBaby", prifixError + "-" +  xmlrpcMethod + "-catch: " + e.getMessage());
		}
	}



	/**入口参数：
	 * $last_id 服务器端分页使用，客户端下拉刷新时传0，获取更多时传服务器上页获取数据返回的结果
	 *返回结果
	 * array(
	 'has_more': 是否还有更多信息，0-－没有，1-－有
	 'last_id' : 最后一条信息的id，分页时使用
	 'data' : 获取的数据，如下：
	 array(
	 'id' 通知id
	 'type' : 类别 1-－有人关注我，2-－有人给我的动态点赞，3-－有人回复了我的动态，4-－系统通知
	 'datetime' 动态发布时间
	 'user' 用户简要字典
	 'content' 动态内容
	 'image' like和回复的根信息中的第一张图片，结构如下：
	 'id' 图片id
	 'thumbnailUrl' 预览图url
	 'largeUrl' 大图url
	 'source_id' 消息相关的id, 目前type=2,3时会返回赞和回复的根postid
	 )
	 );
	 **/
	//获取我的动态通知列表bz.xmlrpc.notification.get_my_list
	public static void get_my_list(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{lastId});
		getCommonList("bz.xmlrpc.notification.get_my_list",params,callback);
	}
	//final String xmlrpcMethod="bz.xmlrpc.notification.get_common_list.v2";
	//Object[] params=extParams(new Object[]{lastId});
	//通知列表  所有的
	/*public static void get_common_list(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{lastId});
		getCommonList("bz.xmlrpc.notification.get_common_list.v2",params,callback);
	}*/
	//获取我参与的列表bz.xmlrpc.notification.get_join_list
	//*返回结果 同bz.xmlrpc.notification.get_my_list
	public static void get_join_list(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{lastId});
		getCommonList("bz.xmlrpc.notification.get_join_list",params,callback);
	}

	/**入口参数：
	 * 同bz.xmlrpc.notification.get_my_list
	 *返回结果
	 * 同bz.xmlrpc.notification.get_my_list
	 */
	//获取关注的列表bz.xmlrpc.notification.get_follow_list
	public static void get_follow_list(int lastId,final ListCallback callback){
		Object[] params=extParams(new Object[]{lastId});
		getCommonList("bz.xmlrpc.notification.get_follow_list", params, callback);
	}
}
