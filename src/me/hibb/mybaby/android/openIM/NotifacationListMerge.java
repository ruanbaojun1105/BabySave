package me.hibb.mybaby.android.openIM;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWConversationType;
import com.alibaba.mobileim.conversation.YWP2PConversationBody;
import com.alibaba.mobileim.conversation.YWTribeConversationBody;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.kit.common.IMUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import mybaby.Constants;
import mybaby.action.Action;
import mybaby.action.P2PChattingAction;
import mybaby.action.TribeChattingAciton;
import mybaby.action.TribeSpaceAction;
import mybaby.action.TribeSystemManagerAction;
import mybaby.cache.CacheDataTask;
import mybaby.models.notification.NotificationCategory;
import mybaby.models.notification.TribeNotificationCategory;
import mybaby.ui.AppConfig;
import mybaby.ui.MyBabyApp;
import mybaby.ui.Notification.adapter.SortNotificationList;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.util.ImageHelper;

/**
 * Created by bj
 */
public class NotifacationListMerge {
    private static NotifacationListMerge merge;

    public static NotifacationListMerge getInstance() {
        if (merge == null)
            merge = new NotifacationListMerge();
        return merge;
    }

    /**
     * 删除是从会话转为NotificationCategory的列表项
     */
    public List<NotificationCategory> delConversationList(List<NotificationCategory> itemList) {
        if (itemList!=null&&itemList.size()>0) {
            Iterator ite=itemList.iterator();
            while(ite.hasNext()){
                NotificationCategory category= (NotificationCategory) ite.next();
                if (category.getKey().equals(Constants.Conversation_Key)) {
                    ite.remove();
                }
            }
        }
        return itemList;
    }

    /**
     * 保存缓存并排序
     * @param itemList
     * @return
     */
    public List<NotificationCategory> saveNotificationCategoryList(Context context,List<NotificationCategory> itemList) {
        if (itemList!= null && itemList.size() > 0) {
            CacheDataTask.putCache(context,itemList.toArray(),Constants.CacheKey_CommunityActivity_NotificationCategory,true);
        }
        return  itemList;
    }
    /**
     * 获得最新转换会话后的NotificationCategory的列表项
     */
    public List<NotificationCategory> getNtcLatestList(Context context,List<YWConversation> list,List<NotificationCategory> newCategoryList) {
        List<NotificationCategory> itemList=new ArrayList<>();

        /*if (newCategoryList != null && newCategoryList.size() > 0) {
            for (NotificationCategory category:newCategoryList)
                itemList.add(category);
        }*/
        if (newCategoryList != null)
            itemList.addAll(newCategoryList);//方便拓展
        if (MyBabyApp.getSharedPreferences().getBoolean("tribeHasInvite",false)){
            String title=MyBabyApp.getSharedPreferences().getString("tribeTitle", "群系统消息");
            String url=MyBabyApp.getSharedPreferences().getString("tribeUrl", "");
            long time=MyBabyApp.getSharedPreferences().getLong("tribeTime", System.currentTimeMillis());
            String content=MyBabyApp.getSharedPreferences().getString("tribeContent", "有新消息");
            int tribeUnreadNum=MyBabyApp.getSharedPreferences().getInt("tribeUnreadNum",0);

            Boolean has = false;//有则更新无则加
            NotificationCategory sysCategory=newNifcForTribeManager(url, title, content, time, tribeUnreadNum);
            for (int i = 0; i < itemList.size(); i++) {
                if (itemList.get(i).getKey().equals(Constants.ConversationManager_Key)) {
                    itemList.set(i,sysCategory);
                    has=true;
                    break;
                }
            }
            if (!has)
                itemList.add(sysCategory);
        }


        //本地服务器
        List<NotificationCategory> spaceNtfcList=new ArrayList<>();
        Object[] categories= CacheDataTask.getObjs(context, Constants.CacheKey_NotificationCategory_Self);
        if (categories != null && categories.length > 0) {
            for (Object categorie:categories) {
                NotificationCategory category= (NotificationCategory) categorie;
                if (category.getAction().contains(TribeSpaceAction.TribeSpaceAction))
                    spaceNtfcList.add(category);
                else
                    itemList.add(category);
            }
        }
        //List<NotificationCategory> itemList2=delConversationList(itemList);
        //云旺
        if (list != null && list.size() > 0) {
            for (YWConversation conversation:list) {
                NotificationCategory category=converToNotica(conversation,spaceNtfcList);
                if (!TextUtils.isEmpty(category.getTitle()))
                    itemList.add(category);
            }
        }
        itemList=saveNotificationCategoryList(context,itemList);
        Collections.sort(itemList, new SortNotificationList());
        return itemList;
    }

    /**
     * 處理數據   ：合併數組並且排序
     * @param
     * @return
     */
    public static List<NotificationCategory> getNtcList(Context context) {
        List<NotificationCategory> itemList=new ArrayList<>();
        Object[] categories= CacheDataTask.getObjs(context, Constants.CacheKey_CommunityActivity_NotificationCategory);
        if (categories != null && categories.length > 0) {
            for (Object item:categories){
                NotificationCategory castItem= (NotificationCategory) item;
                boolean isHas=false;
                for (NotificationCategory itme:itemList){
                    if (itme.getTribe_id()!=0&&itme.getTribe_id()==castItem.getTribe_id()){
                        isHas=true;
                        break;
                    }
                }
                if (isHas) continue;
                else itemList.add(castItem);
            }
            Log.e("服务器消息列表缓存数据num", categories.length + "");
        }
        Collections.sort(itemList, new SortNotificationList());
        return  itemList;
    }

    /**
     * 新建一个群系统消息会话
     * @param ImageUrl
     * @param title
     * @param content
     * @param time
     * @return
     */
    public static NotificationCategory newNifcForTribeManager(String ImageUrl,String title,String content,long time,int unread) {
        NotificationCategory category=new NotificationCategory();
        category.setAction(Action.newActionLink(null, TribeSystemManagerAction.actionUrl));
        category.setImageUrl(ImageUrl);
        category.setTitle(title);//"群系统消息"
        category.setKey(Constants.ConversationManager_Key);
        category.setNewestDatetime_gmt(time);
        category.setUnreadCount(unread);
        category.setNewestDesc(content);//"你有新的群邀请"
        category.setStrongRemind(true);//是强提醒   有数字是强提醒1，红点是弱提醒-1
        return category;
    }
    public NotificationCategory converToNotica(YWConversation conversation,List<NotificationCategory> spaceNtfcList) {
        NotificationCategory category=new NotificationCategory();
        category.setKey(Constants.Conversation_Key);

        category.setNewestDatetime_gmt(conversation.getLatestTimeInMillisecond());//或者conversation.getLatestTime()*1000
        category.setUnreadCount(conversation.getUnreadCount());
        //String time1 = IMUtil.getFormatTime(conversation.getLatestTime() * 1000L, WXAPI.getInstance().getServerTime());
            // 获取会话管理类
        if (conversation.getConversationType()== YWConversationType.Tribe) {
            YWTribeConversationBody body= (YWTribeConversationBody) conversation.getConversationBody();
            YWTribe tribe=body.getTribe();
            IYWContact ywContact = IMUtility.getContactProfileInfo(conversation.getLatestMessageAuthorId(), conversation.getLatestMessageAuthorAppKey());
            String [][] aa={{"im_tribeid",tribe.getTribeId()+""},{"tribe_name", tribe.getTribeName()}};
            category.setTribe_id(tribe.getTribeId());
            category.setAction(Action.newActionLink(aa, TribeChattingAciton.TribeChattingAciton));
            category.setImageUrl(TribeHelper.getTribeIconUrl(tribe.getTribeId()));
            category.setTitle(tribe.getTribeName());
            category.setStrongRemind(MyBabyApp.getSharedPreferences().getBoolean("canSendPush"+tribe.getTribeId() + "", true));//根据是否推送选择强弱提醒
            String content=conversation.getLatestContent();
            /*if (content.startsWith("欢迎")||content.startsWith("您已加入群")||content.endsWith("退出了群聊")||content.contains(MyBabyApp.currentUser.getName())||
                    MyBabyApp.currentUser.getImUserName().equals(conversation.getLatestMessageAuthorId())){
            }*/
            ywContact = IMUtility.getContactProfileInfo(conversation.getLatestMessageAuthorId(), conversation.getLatestMessageAuthorAppKey());
            if (ywContact!=null) {
                String name = ywContact.getShowName();
                if(MyBabyApp.currentUser.getImUserName().equals(conversation.getLatestMessageAuthorId())) {
                    if (content.startsWith("您已加入群")){
                        content="欢迎您加入 "+tribe.getTribeName();
                    }else {
                        content = "我: " + content;
                    }
                }else if (content.contains(conversation.getLatestMessageAuthorId())){
                    content.replace(conversation.getLatestMessageAuthorId(), name);
                }else {
                    if (content.startsWith("欢迎新成员")&&content.endsWith("加入群"))
                        content="欢迎新成员\""+name+"\"加入群";
                    else if (!content.startsWith(name))
                        content=name+": "+content;
                }
            }
            category.setNewestDesc(content);
            TribeNotificationCategory tribeNtfc=new TribeNotificationCategory(category);
            for (NotificationCategory spaceItem:spaceNtfcList){
                if (spaceItem.getTribe_id()==tribe.getTribeId()) {
                    tribeNtfc.setSpace_isStrong(spaceItem.isStrongRemind());
                    //tribeNtfc.setStrongRemind(spaceItem.isStrongRemind());
                    tribeNtfc.setNewestDesc(spaceItem.getNewestDesc());//内容更新一下
                    if (spaceItem.isStrongRemind()){
                        if (category.isStrongRemind()==false) {
                            tribeNtfc.setStrongRemind(true);
                            if (MyBabyApp.getSharedPreferences().getBoolean("canSendPush"+tribe.getTribeId() + "", true)==false){
                                tribeNtfc.setStrongRemind(false);
                                tribeNtfc.setSpace_isStrong(false);
                            }
                            tribeNtfc.setUnreadCount(category.getUnreadCount() + spaceItem.getUnreadCount());
                        }else {
                            tribeNtfc.setUnreadCount(category.getUnreadCount()+ spaceItem.getUnreadCount());
                        }
                    }else {
                        if (spaceItem.getUnreadCount()!=0) {
                            tribeNtfc.setStrongRemind(false);
                            tribeNtfc.setUnreadCount(-1);
                        }else tribeNtfc.setStrongRemind(true);
                    }
                    SharedPreferences.Editor edit = MyBabyApp.getSharedPreferences().edit();
                    edit.putBoolean("canSendPush", spaceItem.getUnreadCount()!=0?spaceItem.isStrongRemind():true);
                    edit.commit();
                    tribeNtfc.setSpace_unread(spaceItem.getUnreadCount());

                    /*boolean a1=category.isStrongRemind();
                    boolean a2=MyBabyApp.getSharedPreferences().getBoolean("tribe_space_strong" + id, true);
                    int b1=category.getUnreadCount();
                    int b2=MyBabyApp.getSharedPreferences().getInt("tribe_space_unread" + id, 0);
                    category.setStrongRemind(a1?true:(a2?true:false));
                    category.setUnreadCount(b1+b2);*/
                    break;
                }
            }
            return tribeNtfc;

        }else if(conversation.getConversationType()== YWConversationType.P2P) {
            YWP2PConversationBody p2pBody = (YWP2PConversationBody) conversation.getConversationBody();
            final IYWContact ywContact = IMUtility.getContactProfileInfo(p2pBody.getContact().getUserId(), p2pBody.getContact().getAppKey());
            String username = "";
            String url = "";
            if (ywContact != null) {
                username = ywContact.getShowName();
                url = TextUtils.isEmpty(ywContact.getAvatarPath()) ? ImageHelper.getMotherPicUrl() : ywContact.getAvatarPath();
            } else {
                url = ImageHelper.getMotherPicUrl();
            }
            final String finalUsername = username;
            String[][] aa = {{"im_userid", p2pBody.getContact().getUserId()}, {"username", finalUsername}};
            category.setAction(Action.newActionLink(aa, P2PChattingAction.P2PChattingAction));
            category.setImageUrl(url);
            category.setTitle(username);
            category.setStrongRemind(true);//默认是强提醒
            category.setNewestDesc(conversation.getLatestContent());
        }

        return  category;
    }



    public  int getUnreadCount(Context context){
        IYWConversationService conversationService=MyBayMainActivity.getmIMKit().getConversationService();

        List<NotificationCategory> listNC=getNtcLatestList(context, conversationService == null ? null : conversationService.getConversationList(), null);

        int unread=0;
        for(NotificationCategory category:listNC)
            unread+=category.isStrongRemind() ?category.getUnreadCount():0;

        return unread;
    }

}
