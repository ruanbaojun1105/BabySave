package mybaby.rpc.notification;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import java.util.Map;

import mybaby.models.notification.TribeGroup;
import mybaby.rpc.BaseRPC;
import mybaby.util.MapUtils;
import mybaby.util.Utils;

/**
 * Created by niubaobei on 2016/1/13.
 */
public class TribesRpc extends BaseRPC {

    private static String prifixError="XMLRPCException-TribesRpc";

    //根据tribe_id获取用户的概要信息bz.xmlrpc.getGroupDetailBytribeid入口参数：$tribe_id
	/*array(
			* post_id,
			* group_name,
			* type, 0--默认 1--小区群 2--同区群 3--同城群 3--同月群 4--同龄群 5--医院群
					* create_time,
			* default_push_switch, 是否推送 0－-否 1-－是
					* max_members_count, 加入群成员的最大值
			* group_key,
			* im_tribe_id
			* )
	*/
    public static void getGroupDetailBytribeid(long tribe_id,final CallbackForTribeGroup callback){
        Object[] params=extParams(new Object[]{tribe_id+""});
        getTribeGroup("bz.xmlrpc.getGroupDetailBytribeid",params,callback);
    }
    /*
     *功能：
     *      根据tribe_id修改群是否推送
     *调用方法：
     *      bz.xmlrpc.group.set_push_switch
     *入口参数：
     *     $tribe_id
     *     $push_switch 0-不推送 1-推送
     *返回结果
     *   $result 0-失败  1-成功
     */
    public static void set_push_switch(long tribe_id,int push_switch ,final CallbackForBool callback){
        Object[] params=extParams(new Object[]{tribe_id,push_switch});
        BaseRPC.rpcCallForReturnInt(prifixError, "bz.xmlrpc.group.set_push_switch", params, callback);
    }

    /*
 *功能：
 *      退出群
 *调用方法：
 *      bz.xmlrpc.group_member.quit_for_tribe_id
 *入口参数：
 *      $tribe_id     im群组id
 *返回结果
 *      $result 0-失败 1-成功
 */
    public static void quit_for_tribe_id(long tribe_id,final CallbackForBool callback){
        Object[] params=extParams(new Object[]{tribe_id+""});
        BaseRPC.rpcCallForReturnInt(prifixError, "bz.xmlrpc.group_member.quit_for_tribe_id", params, callback);
    }
    /*
     *功能：
     *      更新群组成员活跃度
     *调用方法：
     *      bz.xmlrpc.group_member.updateActivity
     *入口参数：
     *      $tribe_id     im群组id
     *返回结果
     *      $result 0-失败 1-成功
     */
    public static void update_group_member(long tribe_id,final CallbackForBool callback){
        Object[] params=extParams(new Object[]{tribe_id});
        BaseRPC.rpcCallForReturnInt(prifixError, "bz.xmlrpc.group_member.updateActivity", params, callback);
    }
    /*
     *功能：
     *      获取用户的群组
     *调用方法：
     *     bz.xmlrpc.group_get_groups
     *入口参数：
     *      $user_id    用户id
     *返回结果
     *    array(
     *              'post_id'             群id
     *              'group_name'          群名
     *              'type'  0--默认 1--小区群 2--同区群 3--同城群 4--同月群 5--同龄群 6--医院群
     *              'create_time'          创建时间
     *              'default_push_switch'  默认是否推送 0-是 1-否
     *              'max_members_count'    群成员最大数
     *              'im_tribe_id'          im的群id
     *    ）
     */
    public static void get_groups(int userId, final CallbackForTribeGroupList callback){
        Object[] params=extParams(new Object[]{userId});
        getGroupsList(prifixError, "bz.xmlrpc.group_get_groups", params, callback);
    }

    /*
 *功能：
 *      加入群
 *调用方法：
 *      bz.xmlrpc.group_member.join_for_tribe_id.v5
 *入口参数：
 *      $tribe_id     im群组id
 *返回结果
 *      $result 0-失败 1-成功
 */
    public static void join_for_tribeid(long tribeId, final CallbackForMap callback) {
        Object[] params=extParams(new Object[]{tribeId+""});
        BaseRPC.rpcCallForReturnMap(prifixError, "bz.xmlrpc.group_member.join_for_tribe_id.v5", params, callback);
    }

    /*群踢出成员
    调用方法：
            * bz.xmlrpc.group_member.expel_by_tribe_id
    *入口参数：
            * $tribe_id im群组id
    * $member_id 踢出的成员id
    * $reason 原因*/
    public static void expel_for_tribeid(long tribeId,String userId,int reason,final CallbackForBool callback) {
        Object[] params=extParams(new Object[]{tribeId+"",userId,reason});
        BaseRPC.rpcCallForReturnInt(prifixError, "bz.xmlrpc.group_member.expel_by_tribe_id", params, callback);
    }


    public interface CallbackForTribeGroupList {
        void onSuccess(TribeGroup[] tribeGroups);

        void onFailure(long id, XMLRPCException error);
    }
    public interface CallbackForTribeGroup {
        void onSuccess(TribeGroup tribeGroup);

        void onFailure(long id, XMLRPCException error);
    }

    private static void getGroupsList(final String prifixError,
                                           final String xmlrpcMethod, Object[] params,
                                           final CallbackForTribeGroupList callback) {
        XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result) {
                if (result != null && result instanceof Map<?, ?>) {
                    Map<?, ?> map = (Map<?, ?>) result;
                    if (callback != null)
                        callback.onSuccess(TribeGroup.createByArray(MapUtils.getArray(map, "data"))
                        );
                }
            }

            @Override
            public void onFailure(long id, XMLRPCException error) {
                Utils.LogV("MyBaby", prifixError + xmlrpcMethod
                        + "-onFailure: " + error.getMessage());
                if (callback != null) {
                    callback.onFailure(id, error);
                }
            }
        };

        try {
            getClient().callAsync(nCallback, xmlrpcMethod, params);
        } catch (Exception e) {
            Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + "-catch: "
                    + e.getMessage());
            if (callback != null) {
                callback.onFailure(0, null);
            }
        }
    }
    private static void getTribeGroup( final String xmlrpcMethod, Object[] params,
                                     final CallbackForTribeGroup callback) {
        XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result) {
                if (result != null && result instanceof Map<?, ?>) {
                    Map<?, ?> map = (Map<?, ?>) result;
                    if (callback != null)
                        callback.onSuccess(TribeGroup.createByMap(map));
                }
            }

            @Override
            public void onFailure(long id, XMLRPCException error) {
                Utils.LogV("MyBaby", prifixError + xmlrpcMethod
                        + "-onFailure: " + error.getMessage());
                if (callback != null) {
                    callback.onFailure(id, error);
                }
            }
        };

        try {
            getClient().callAsync(nCallback, xmlrpcMethod, params);
        } catch (Exception e) {
            Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + "-catch: "
                    + e.getMessage());
            if (callback != null) {
                callback.onFailure(0, null);
            }
        }
    }

}
