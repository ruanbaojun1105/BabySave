package me.hibb.mybaby.android.openIM;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.IYWP2PPushListener;
import com.alibaba.mobileim.IYWTribePushListener;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWChannel;
import com.alibaba.mobileim.YWConstants;
import com.alibaba.mobileim.YWIMCore;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.YWLoginParam;
import com.alibaba.mobileim.channel.LoginParam;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeMember;
import com.alibaba.mobileim.login.IYWConnectionListener;
import com.alibaba.mobileim.login.YWLoginCode;
import com.alibaba.mobileim.login.YWLoginState;
import com.alibaba.mobileim.login.YWPwdType;
import com.alibaba.mobileim.utility.IMAutoLoginInfoStoreUtil;
import com.alibaba.tcms.env.EnvManager;
import com.alibaba.tcms.env.TcmsEnvType;
import com.alibaba.tcms.env.YWEnvManager;
import com.alibaba.tcms.env.YWEnvType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.ui.main.MainUtils;
import mybaby.ui.user.RegisterActivity;
import mybaby.ui.user.SignInUpActivity;

/**
 * SDK 初始化和登录
 *
 * @author
 */
public class LoginOpenImHelper {

    private static LoginOpenImHelper sInstance = new LoginOpenImHelper();

    /*public static LoginOpenImHelper getInstance() {
        return sInstance;
    }*/
    public static LoginOpenImHelper getInstance() {
        if (sInstance==null){
            synchronized (LoginOpenImHelper.class){
                if (sInstance==null){
                    sInstance=new LoginOpenImHelper();
                }
            }
        }
        return sInstance;
    }

    // 应用APPKEY，这个APPKEY是申请应用时获取的
    public static  String APP_KEY = Constants.OPENIM_KEY;

    public static YWEnvType sEnvType = YWEnvType.ONLINE;

    // openIM UI解决方案提供的相关API，创建成功后，保存为全局变量使用

    private YWConnectionListenerImpl mYWConnectionListenerImpl = new YWConnectionListenerImpl();
    private Application mApp;

    private List<Map<YWTribe, YWTribeMember>> mTribeInviteMessages = new ArrayList<Map<YWTribe, YWTribeMember>>();


    public void initIMKit(String userId, String appKey) {
        addPushMessageListener();
        //添加联系人通知和更新监听 todo 在初始化后、登录前添加监听，离线的联系人系统消息才能触发监听器
        addConnectionListener();
    }

    private YWLoginState mAutoLoginState = YWLoginState.idle;

    public YWLoginState getAutoLoginState() {
        return mAutoLoginState;
    }

    public void setAutoLoginState(YWLoginState state) {
        this.mAutoLoginState = state;
    }

    /**
     * 初始化SDK
     *
     * @param context
     */
    public void initSDK(Application context) {
        mApp = context;
        sEnvType = YWEnvManager.getEnv(context);

        //初始化IMKit
		final String userId = IMAutoLoginInfoStoreUtil.getLoginUserId();
		final String appkey = IMAutoLoginInfoStoreUtil.getAppkey();
		if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(appkey)){
			initIMKit(userId, appkey);
			NotificationInitHelper.init();
		}
        UserProfileHelper.initProfileCallback();//自定义头像昵称获取的事件初始化
        TcmsEnvType type = EnvManager.getInstance().getCurrentEnvType(mApp);
        if(type==TcmsEnvType.ONLINE || type == TcmsEnvType.PRE){
            YWAPI.init(mApp, APP_KEY);
        }
        else if(type==TcmsEnvType.TEST){
            YWAPI.init(mApp, APP_KEY);
        }

        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(appkey)){
            LoginOpenImHelper.getInstance().initIMKit(userId, appkey);
        }
        //通知栏相关的初始化
        NotificationInitHelper.init();
        initAutoLoginStateCallback();
    }

    //将自动登录的状态广播出去
    /*private void sendAutoLoginState(YWLoginState loginState) {
        Intent intent = new Intent(LoginActivity.AUTO_LOGIN_STATE_ACTION);
        intent.putExtra("state", loginState.getValue());
        LocalBroadcastManager.getInstance(YWChannel.getApplication()).sendBroadcast(intent);
    }*/

    /**
     * 添加新消息到达监听，该监听应该在登录之前调用以保证登录后可以及时收到消息
     */
    private void addPushMessageListener(){
        if (MainUtils.getYWIMKit() == null) {
            return;
        }

        IYWConversationService conversationService = MainUtils.getYWIMKit().getConversationService();
        //添加单聊消息监听，先删除再添加，以免多次添加该监听
        conversationService.removeP2PPushListener(mP2PListener);
        conversationService.addP2PPushListener(mP2PListener);

        //添加群聊消息监听，先删除再添加，以免多次添加该监听
        conversationService.removeTribePushListener(mTribeListener);
        conversationService.addTribePushListener(mTribeListener);
    }
    private IYWTribePushListener mTribeListener = new IYWTribePushListener() {
        @Override
        public void onPushMessage(YWTribe tribe, YWMessage message) {
            //TODO 收到群消息
        }
    };
    private IYWP2PPushListener mP2PListener = new IYWP2PPushListener() {
        @Override
        public void onPushMessage(IYWContact iywContact, YWMessage ywMessage) {

        }
    };

    /**
     * 登录操作
     *
     * @param userId   用户id
     * @param password 密码
     * @param callback 登录操作结果的回调
     */
    //------------------请特别注意，OpenIMSDK会自动对所有输入的用户ID转成小写处理-------------------
    //所以开发者在注册用户信息时，尽量用小写
    public void login_Sample(String userId, String password, String appKey,
                             IWxCallback callback) {
        YWIMKit mIMKit=MainUtils.getYWIMKit();
        if (mIMKit == null) {
            return;
        }
        YWLoginParam loginParam = YWLoginParam.createLoginParam(userId,
                password);
        if (TextUtils.isEmpty(appKey) || appKey.equals(YWConstants.YWSDKAppKey)
                || appKey.equals(YWConstants.YWSDKAppKeyCnHupan)) {
            loginParam.setServerType(LoginParam.ACCOUNTTYPE_WANGXIN);
            loginParam.setPwdType(YWPwdType.pwd);
        }
        // openIM SDK提供的登录服务
        IYWLoginService mLoginService = mIMKit.getLoginService();

        mLoginService.login(loginParam, callback);
    }

    //设置连接状态的监听
    private void addConnectionListener() {
        YWIMKit mIMKit=MainUtils.getYWIMKit();
        if (mIMKit == null) {
            return;
        }
        YWIMCore imCore = mIMKit.getIMCore();
        imCore.removeConnectionListener(mYWConnectionListenerImpl);
        imCore.addConnectionListener(mYWConnectionListenerImpl);
    }

    private class YWConnectionListenerImpl implements IYWConnectionListener {

        @Override
        public void onReConnecting() {
        }
        @Override
        public void onReConnected() {
//				YWLog.i("LoginOpenImHelper", "onReConnected");
        }
        @Override
        public void onDisconnect(int arg0, String arg1) {
            if (arg0 == YWLoginCode.LOGON_FAIL_KICKOFF) {
                //sendAutoLoginState(YWLoginState.disconnect);
                //在其它终端登录，当前用户被踢下线
                getInstance().setAutoLoginState(YWLoginState.disconnect);
                Toast.makeText(MyBabyApp.getContext(), "被踢下线", Toast.LENGTH_LONG).show();
                YWLog.i("LoginOpenImHelper", "被踢下线");
                if(MyBabyApp.currentUser.getBzRegistered()){
                    Intent intent = new Intent(MyBabyApp.getContext(), SignInUpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    MyBabyApp.getContext().startActivity(intent);
                }else {
                    Intent intent = new Intent(MyBabyApp.getContext(), RegisterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    MyBabyApp.getContext().startActivity(intent);
                }

            }
        }
    }

    /**
     * 登出
     */
    public void loginOut_Sample() {
        YWIMKit mIMKit=MainUtils.getYWIMKit();
        if (mIMKit == null) {
            return;
        }


        // openIM SDK提供的登录服务
        IYWLoginService mLoginService = mIMKit.getLoginService();
        mLoginService.logout(new IWxCallback() {

            @Override
            public void onSuccess(Object... arg0) {

            }

            @Override
            public void onProgress(int arg0) {

            }

            @Override
            public void onError(int arg0, String arg1) {

            }
        });
    }

    /**
     * 开发者不需要关注此方法，纯粹是DEMO自动登录的需要
     */
    private void initAutoLoginStateCallback() {
        YWChannel.setAutoLoginCallBack(new IWxCallback() {
            @Override
            public void onSuccess(Object... result) {
                mAutoLoginState = YWLoginState.success;
                //sendAutoLoginState(mAutoLoginState);
            }

            @Override
            public void onError(int code, String info) {
                mAutoLoginState = YWLoginState.fail;
                //sendAutoLoginState(mAutoLoginState);
            }

            @Override
            public void onProgress(int progress) {
                mAutoLoginState = YWLoginState.logining;
                //sendAutoLoginState(mAutoLoginState);
            }
        });
    }

    public List<Map<YWTribe, YWTribeMember>> getTribeInviteMessages() {
        return mTribeInviteMessages;
    }
}
