package me.hibb.mybaby.android.openIM;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.IYWContactHeadClickCallback;
import com.alibaba.mobileim.contact.IYWContactProfileCallback;
import com.alibaba.mobileim.contact.IYWContactService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mybaby.ui.MyBabyApp;
import mybaby.ui.main.MainUtils;
import mybaby.ui.more.PersonPageActivity;

/**
 * 用户自定义昵称和头像
 *
 * @author zhaoxu
 */
public class UserProfileHelper {

    private static final String TAG = UserProfileHelper.class.getSimpleName();

    private static List<User> users = new ArrayList<User>();
    private static mybaby.models.User user;

    private static void init() {
        users.clear();

    }

    private static boolean enableUseLocalUserProfile = true;

    //初始化，建议放在登录之前
    public static void initProfileCallback() {
        if (!enableUseLocalUserProfile){
            //目前SDK会自动获取导入到OpenIM的帐户昵称和头像，如果用户设置了回调，则SDK不会从服务器获取昵称和头像
            return;
        }
        init();
        YWIMKit imKit = MainUtils.getYWIMKit();
        if(imKit == null) {
            return;
        }
        IYWContactService contactManager = imKit.getIMCore().getContactService();

        //头像点击的回调（开发者可以按需设置）
        contactManager.setContactHeadClickCallback( new IYWContactHeadClickCallback() {
            @Override
            public Intent onShowProfileActivity(String userId, String appKey) {
                Intent intent = new Intent(MyBabyApp.getContext(), PersonPageActivity.class);
                Bundle bundle = new Bundle();
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

        contactManager.setContactProfileCallback(new IYWContactProfileCallback() {

            @Override
            public Intent onShowProfileActivity(String arg0) {
                return null;
            }

            //此方法会在SDK需要显示头像和昵称的时候，调用。同一个用户会被多次调用的情况。
            //比如显示会话列表，显示聊天窗口时同一个用户都会被调用到。
            @Override
            public IYWContact onFetchContactInfo(String arg0) {
                // 开发者需要根据不同的用户ID显示不同的昵称和头像。
                try {
                    String userId = arg0;
                    return modifyUserInfo(userId);
                } catch (Exception e) {

                }
                return null;
            }
        });
    }

    /**
     * 设定希望显示的用户昵称和头像
     * 头像支持本地路径和URL路径以及资源ID号。重要：头像图片最好小于10K，否则第一次加载可能会有图像压缩的延时,URL路径还有下载的延时
     *
     * 注意本地路径，需要以pic_1_开头
     * @param userId   用户ID
     * @return
     */
    public static UserInfo modifyUserInfo(String userId) {
        UserInfo userInfo = mUserInfo.get(userId);
            mUserInfo.put(userId, userInfo);
        return userInfo;
    }

    // 这个只是个示例，开发者需要自己管理用户昵称和头像
    private static Map<String, UserInfo> mUserInfo = new HashMap<String, UserInfo>();

    private static class UserInfo implements IYWContact {

        private String mUserNick;    // 用户昵称
        private String mAvatarPath;    // 用户头像URL

        private int mLocalResId;//主要用于本地资源


        public UserInfo(String nickName, String avatarPath) {
            this.mUserNick = nickName;
            this.mAvatarPath = avatarPath;
        }

        public UserInfo(String nickName, int resId) {
            this.mUserNick = nickName;
            this.mLocalResId = resId;
        }

        @Override
        public String getAppKey() {
            return null;
        }

        @Override
        public String getAvatarPath() {
            if (mLocalResId != 0) {
                return mLocalResId + "";
            } else {
                return mAvatarPath;
            }
        }

        @Override
        public String getShowName() {
            return mUserNick;
        }

        @Override
        public String getUserId() {
            return null;
        }
    }

    private static class User {
        private String name;
        private String avatar;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
