package me.hibb.mybaby.android.openIM;

import android.content.Intent;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMNotification;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWConversationType;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWP2PConversationBody;
import com.alibaba.mobileim.conversation.YWTribeConversationBody;
import com.alibaba.mobileim.kit.common.IMUtility;
import com.alibaba.mobileim.lib.presenter.conversation.P2PConversation;
import com.alibaba.mobileim.lib.presenter.conversation.TribeConversation;
import com.alibaba.wxlib.util.SysUtil;

import me.hibb.mybaby.android.IMActivity.ChattingFragmentActivity;
import me.hibb.mybaby.android.IMActivity.ChattingTribeFragmentActivity;
import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.ui.main.MainUtils;

/**
 * 通知栏的一些自定义设置
 * @author zhaoxu
 *
 */
public class NotificationInitHelper extends IMNotification{

    private static boolean mNeedQuiet;
    private static boolean mNeedVibrator = true;
    private static boolean mNeedSound = true;

    public NotificationInitHelper(Pointcut pointcut) {
        super(pointcut);
    }

    public static void init(){
		YWIMKit imKit = MainUtils.getYWIMKit();
		if (imKit != null){
            mNeedSound = (SimpleCusKVStore.getNeedSound() == 1);
            mNeedVibrator = (SimpleCusKVStore.getNeedVibration() == 1);
		}
	}

    public void setNeedQuiet(boolean needQuiet){
        mNeedQuiet = needQuiet;
    }

    public void setNeedVibrator(boolean needVibrator){
        mNeedVibrator = needVibrator;
    }

    public void setNeedSound(boolean needSound){
        mNeedSound = needSound;
    }

    /**
     * 是否开启免打扰模式，若开启免打扰模式则收到新消息时不发送通知栏提醒，只在会话列表页面显示未读数
     * 若开启免打扰模式，则声音提醒和震动提醒会失效，即收到消息时不会有震动和提示音
     * @param conversation 会话id
     * @param message 收到的消息
     * @return true:开启， false：不开启
     */
    @Override
    public boolean needQuiet(YWConversation conversation, YWMessage message) {
        if (SysUtil.isForeground()) {//是否在前台
            return true;
        }else {
            return false;
        }
    }

    /**
     * 收到通知栏消息时是否震动提醒，该设置在没有开启免打扰模式的情况下才有效
     * @param conversation 会话id
     * @param message 收到的消息
     * @return true：震动，false：不震动
     */
    @Override
    public boolean needVibrator(YWConversation conversation, YWMessage message) {
//        if (conversation.getConversationType() == YWConversationType.Tribe){
//            return false;
//        }
        return true;
    }


    /**
     * 收到通知栏消息时是否有声音提醒，该设置在没有开启免打扰模式的情况下才有效
     * @param conversation 会话id
     * @param message 收到的消息
     * @return true：有提示音，false：没有提示音
     */
    @Override
    public boolean needSound(YWConversation conversation, YWMessage message) {
         if (SysUtil.isForeground()) {
            if (conversation.getConversationType() == YWConversationType.Tribe) {
                return false;
            } else {
                return true;
            }
        }else return true;
    }

    /**
     * 收到消息时，自定义消息通知栏的提示文案
     * @param conversation
     * @param message
     * @param totalUnReadCount
     * @return，如果返回空，则使SDK默认的文案格式
     */
    @Override
    public String getNotificationTips(YWConversation conversation, YWMessage message, int totalUnReadCount) {
        //return null;
        //return "自定义文案，未读消息数为:" + totalUnReadCount;
        int un=MyBabyApp.getSharedPreferences().getInt("unread", 0);
        if (un==0)
            return "你有新的短消息，请注意查收!";
        return totalUnReadCount%2==0?"当前有 "+un+" 条未读消息!":"你收到了 "+un+" 条未读消息!";
    }

    /**
     * 收到消息时的自定义通知栏点击Intent
     * @param conversation
     *          收到消息的会话
     * @param message
     *          收到的消息
     * @param totalUnReadCount
     *          会话中消息未读数
     * @return
     *          如果返回null，则使用全局自定义Intent
     */
    public Intent getCustomNotificationIntent(YWConversation conversation, YWMessage message, int totalUnReadCount) {
        //以下仅为示例代码，需要Intent开发者根据不同目的自己实现
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_MAIN);
            if (conversation instanceof P2PConversation){
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setClass(MyBabyApp.getContext(), ChattingFragmentActivity.class);
                YWP2PConversationBody p2pBody = (YWP2PConversationBody) conversation.getConversationBody();
                IYWContact ywContact =null;
                ywContact=IMUtility.getContactProfileInfo(p2pBody.getContact().getUserId(), p2pBody.getContact().getAppKey());
                String username = "";
                try {
                    if (ywContact != null)
                        username = ywContact.getShowName();
                    else {
                        ywContact = IMUtility.getContactProfileInfo(p2pBody.getContact().getUserId(), p2pBody.getContact().getAppKey());
                        username = ywContact.getShowName();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ywContact = IMUtility.getContactProfileInfo(p2pBody.getContact().getUserId(), p2pBody.getContact().getAppKey());
                    if (ywContact == null) username = p2pBody.getContact().getUserId();
                    else username = ywContact.getShowName();
                }
                final String finalUsername = username;
                intent.putExtra(ChattingFragmentActivity.TARGET_ID, p2pBody.getContact().getUserId());
                intent.putExtra(ChattingFragmentActivity.USERNAME, finalUsername);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }else if (conversation instanceof TribeConversation){
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setClass(MyBabyApp.getContext(), ChattingTribeFragmentActivity.class);
                YWTribeConversationBody body= (YWTribeConversationBody) conversation.getConversationBody();
                intent.putExtra(ChattingTribeFragmentActivity.TRIBE_ID, body.getTribe().getTribeId());
                intent.putExtra(ChattingTribeFragmentActivity.TRIBE_NAME,body.getTribe().getTribeName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return intent;
    }

    /**
     * 获取通知栏图标Icon
     * @return ResId
     */
    @Override
    public int getNotificationIconResID() {
        return R.drawable.appicon;
    }

    /**
     * 获取通知栏显示Title
     * @return
     */
    @Override
    public String getAppName() {
        return "最新消息：";
    }
}
