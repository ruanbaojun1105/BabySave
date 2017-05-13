package me.hibb.mybaby.android.openIM;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContactHeadClickCallback;
import com.alibaba.mobileim.contact.IYWContactService;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeMember;
import com.alibaba.mobileim.gingko.presenter.tribe.IYWTribeChangeListener;

import java.util.List;

import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.models.notification.TribeGroup;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.more.PersonPageActivity;

/**
 * Created by bj on 2016/1/11.
 */
public class TribeHelper {

    public static String getTribeIconUrl(long tribe_id) {
        return  Constants.MY_BABY_TribeURL+tribe_id;
    }

    public static TribeGroup TribeToTribeGroup(YWTribe tribe) {
        TribeGroup tribeGroup=new TribeGroup();
        tribeGroup.setIm_tribe_id(tribe.getTribeId());
        tribeGroup.setGroup_name(tribe.getTribeName());
        return tribeGroup;
    }

    public static TribeGroup[] getTribeGroupList(List<YWTribe> ywTribes) {
        TribeGroup[] tribeGroups=null;
        if (ywTribes!=null&&ywTribes.size()>0){
            tribeGroups=new TribeGroup[ywTribes.size()];
            for (int i = 0; i < ywTribes.size(); i++) {
                tribeGroups[i]=TribeToTribeGroup(ywTribes.get(i));
            }
        }
        return tribeGroups;
    }
    public abstract static class MyIWxCallback{
        public MyIWxCallback() {
            // TODO Auto-generated constructor stub
        }
        public abstract void onWxSuccess(List<YWTribe> list);
        public abstract void onWxFail();
        public IWxCallback getIWxCallback(final Context context) {

            IWxCallback callback = new IWxCallback() {

                @Override
                public void onSuccess(Object... result) {
                    // TODO Auto-generated method stub
                    if (result[0] instanceof List )
                    { List<YWTribe> list = (List<YWTribe>)result[0];
                        Log.e("", list + "");
                        onWxSuccess(list);}else onWxSuccess(null);
                }

                @Override
                public void onProgress(int progress) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onError(int code, String info) {
                    // TODO Auto-generated method stub
                    onWxFail();
                }
            };
            return callback;
        }
    }

    public static void initCallbackIMUserClick(){
        IYWContactService contactManager = MyBayMainActivity.getmIMKit().getIMCore().getContactService();
        contactManager.setContactHeadClickCallback(new IYWContactHeadClickCallback() {
            @Override
            public Intent onShowProfileActivity(String userId, String appKey) {
                Intent intent = new Intent(MyBabyApp.getContext(), PersonPageActivity.class);
                Bundle bundle = new Bundle();
                if (userId.equals(MyBabyApp.currentUser.getImUserName()))
                    bundle.putSerializable("user", MyBabyApp.currentUser);
                else
                    bundle.putString("im_userId", userId);
                intent.putExtras(bundle);
                return intent;
            }

            @Override
            public Intent onDisposeProfileHeadClick(Context context, String s, String s1) {
                Intent intent = new Intent(MyBabyApp.getContext(), PersonPageActivity.class);
                Bundle bundle = new Bundle();
                if (MyBabyApp.currentUser!=null){
                    if (s1.equals(MyBabyApp.currentUser.getImUserName()))
                        bundle.putSerializable("user", MyBabyApp.currentUser);
                }else
                    bundle.putString("im_userId", s1);
                intent.putExtras(bundle);
                return intent;
            }

        });
    }
    /**
     * 群相关变更回调，SDK使用方可以实现此接口来接收群相关的变更通知
     * 所有方法都在UI线程调用
     * @param context
     * @return
     */
    public IYWTribeChangeListener getTribeChangeListener(final Context context){
        return new IYWTribeChangeListener() {
            @Override
            public void onInvite(YWTribe ywTribe, YWTribeMember ywTribeMember) {
                /**
                 * 被邀请加入群
                 * @param tribe 群信息
                 * @param user 邀请发起者
                 */
                Toast.makeText(context, "邀请加入群:"+ywTribe.getTribeName()+ywTribe.getTribeId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserJoin(YWTribe ywTribe, YWTribeMember ywTribeMember) {
                /**
                 * 用户加入群
                 * @param tribe
                 * @param userId
                 */
                //Toast.makeText(context, "用户加入群", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserQuit(YWTribe ywTribe, YWTribeMember ywTribeMember) {
                /**
                 * 用户退出群
                 * @param tribe
                 * @param userId
                 */
                //Toast.makeText(context, "用户退出群", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserRemoved(YWTribe ywTribe, YWTribeMember ywTribeMember) {
                /**
                 * 用户被请出群
                 * @param tribe
                 * @param userId
                 */
                //Toast.makeText(context, "用户被请出群", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTribeDestroyed(YWTribe ywTribe) {
                /**
                 * 停用群
                 * @param tribe
                 */
                //Toast.makeText(context, "停用群", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTribeInfoUpdated(YWTribe ywTribe) {
                /**
                 * 群信息更新`
                 * @param tribe
                 * @param tribeName
                 * @param announce
                 */

                //Toast.makeText(context, "群信息更新", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTribeManagerChanged(YWTribe ywTribe, YWTribeMember ywTribeMember) {
                /**
                 * 群管理员变更
                 * @param tribe
                 * @param newManager
                 */
                //Toast.makeText(context, "群管理员变更", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTribeRoleChanged(YWTribe ywTribe, YWTribeMember ywTribeMember) {
                //Toast.makeText(context, "群角色改变", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
