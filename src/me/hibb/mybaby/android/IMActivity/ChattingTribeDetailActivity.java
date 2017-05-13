package me.hibb.mybaby.android.IMActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.constant.YWProfileSettingsConstants;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeMember;
import com.alibaba.mobileim.tribe.IYWTribeService;

import org.xmlrpc.android.XMLRPCException;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.R;
import me.hibb.mybaby.android.openIM.TribeHelper;
import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.notification.TribesRpc;
import mybaby.ui.broadcast.PostMediaTask;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.widget.CheckSwitchButton;
import mybaby.util.ActionBarUtils;
import mybaby.util.MaterialDialogUtil;

import static mybaby.rpc.notification.TribesRpc.set_push_switch;

/**
 * Created by Administrator on 2016/1/13 0013.
 */
public class ChattingTribeDetailActivity extends Activity implements View.OnClickListener {
    private RelativeLayout TribeMember;
    private RelativeLayout Notification;
    private RelativeLayout exitFromTribe;
    private long tribeId;
    private String tribeName;
    private YWIMKit mIMKit;
    private YWTribe mTribe;
    private IYWTribeService mTribeService;
    List<YWTribeMember> mList = new ArrayList<YWTribeMember>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int mTribeMemberCount;
    private TextView member_count;
    private TextView tv_enter;
    private SharedPreferences sp;
    private CheckSwitchButton isNotification;
    private int mMsgRecFlag = YWProfileSettingsConstants.TRIBE_MSG_REJ;
    SharedPreferences.Editor edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_tribe_detail_activity);
        mIMKit = MyBayMainActivity.getmIMKit();
        mTribeService = mIMKit.getTribeService();
        initView();
        initTribeMemberList();
        mTribe=mTribeService.getTribe(tribeId);
    }

    private void initView() {
        Intent intent=getIntent();
        tribeId=intent.getLongExtra("tribeId", 0);
        tribeName=intent.getStringExtra("TribeName");
        ActionBarUtils.initActionBar(tribeName, this);
        isNotification= (CheckSwitchButton) findViewById(R.id.chatting_notification);
        sp=MyBabyApp.getSharedPreferences();
        Boolean flag=sp.getBoolean(tribeId+"", true);
        if(flag)
        isNotification.setChecked(false);
        else
        isNotification.setChecked(true);
        TribeMember=(RelativeLayout)findViewById(R.id.TribeMember);
        tv_enter= (TextView) findViewById(R.id.tv_enter);
        tv_enter.setTypeface(MyBabyApp.fontAwesome);
        TribeMember.setOnClickListener(this);
        Notification=(RelativeLayout)findViewById(R.id.Notification);
        exitFromTribe=(RelativeLayout)findViewById(R.id.exitFromTribe);
        exitFromTribe.setOnClickListener(this);
        member_count= (TextView) findViewById(R.id.member_count);
        isNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                edit = sp.edit();
                if(!isChecked)
                {
                    setMsgRecType(YWProfileSettingsConstants.TRIBE_MSG_REC);
                    set_push_switch(tribeId, 1, null);
                    edit.putBoolean("canSendPush"+tribeId,true);

                }
                else {
                    setMsgRecType(YWProfileSettingsConstants.TRIBE_MSG_REJ);
                    set_push_switch(tribeId,0,null);
                    edit.putBoolean("canSendPush"+tribeId+"",false);
                }
                edit.commit();
            }
        });
    }

    private void initTribeMemberList() {
        getTribeMembersFromLocal();
        getTribeMembersFromServer();
    }

    private void getTribeMembersFromServer() {
            mTribeService.getMembersFromServer(new IWxCallback() {
                @Override
                public void onSuccess(Object... objects) {
                    //获取群成员列表
                    mList.clear();
                    mList.addAll((List<YWTribeMember>) objects[0]);
                    if (mList != null || mList.size() > 0) {
                        mTribeMemberCount = mList.size();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateView();
                            }
                        });
                    }

                    //获取群消息
                    if(mTribe!=null)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMsgRecFlag = mTribe.getMsgRecType();
                                setMsgRecTypeLabel(mMsgRecFlag);
                            }
                        });
                    }
                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onProgress(int i) {

                }
            }, tribeId);
        }

    private void getTribeMembersFromLocal() {
        mTribeService.getMembers(new com.alibaba.mobileim.channel.event.IWxCallback() {
            @Override
            public void onSuccess(Object... objects) {
                mList.clear();
                if (mList != null || mList.size() > 0) {
                    mList.addAll((List<YWTribeMember>) objects[0]);
                    mTribeMemberCount = mList.size();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateView();
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i) {

            }
        }, tribeId);

    }

    private void updateView() {
//        mTribeName.setText(mTribe.getTribeName());
//        mTribeDesc.setText(mTribe.getTribeNotice());
//        mMyTribeNick.setText(getLoginUserTribeNick());
        if (mTribeMemberCount > 0) {
            member_count.setText((mTribeMemberCount>2?mTribeMemberCount-1:mTribeMemberCount) + "人");
        }
//        initMsgRecFlags();
//        if (getLoginUserRole() == YWTribeMember.ROLE_HOST) {
//            mMangeTribeMembers.setText("群成员管理");
//            mEditTribeInfoLayout.setVisibility(View.VISIBLE);
//            mQuiteTribe.setText("解散群");
//            mQuiteTribe.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mTribeService.disbandTribe(new IWxCallback() {
//                        @Override
//                        public void onSuccess(Object... result) {
//                            YWLog.i(TAG, "解散群成功！");
//                            Notification.showToastMsg(TribeInfoActivity.this, "解散群成功！");
//                            openTribeListFragment();
//                        }
//
//                        @Override
//                        public void onError(int code, String info) {
//                            YWLog.i(TAG, "解散群失败， code = " + code + ", info = " + info);
//                            Notification.showToastMsg(TribeInfoActivity.this, "解散群失败, code = " + code + ", info = " + info);
//                        }
//
//                        @Override
//                        public void onProgress(int progress) {
//
//                        }
//                    }, mTribeId);
//                }
//            });
//        } else {
//            mMangeTribeMembers.setText("群成员列表");
//            mEditTribeInfoLayout.setVisibility(View.GONE);
//            mQuiteTribe.setText("退出群");
//            mQuiteTribe.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mTribeService.exitFromTribe(new IWxCallback() {
//                        @Override
//                        public void onSuccess(Object... result) {
//                            YWLog.i(TAG, "退出群成功！");
//                            Notification.showToastMsg(TribeInfoActivity.this, "退出群成功！");
//                            openTribeListFragment();
//                        }
//
//                        @Override
//                        public void onError(int code, String info) {
//                            YWLog.i(TAG, "退出群失败， code = " + code + ", info = " + info);
//                            Notification.showToastMsg(TribeInfoActivity.this, "退出群失败, code = " + code + ", info = " + info);
//                        }
//
//                        @Override
//                        public void onProgress(int progress) {
//
//                        }
//                    }, mTribeId);
//                }
//            });
//        }
//
//        if (!TextUtils.isEmpty(mTribeOp)) {
//            mMangeTribeMembersLayout.setVisibility(View.GONE);
//            mEditTribeInfoLayout.setVisibility(View.GONE);
//            mQuiteTribe.setText("加入群");
//            mQuiteTribe.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mTribeService.joinTribe(new IWxCallback() {
//                        @Override
//                        public void onSuccess(Object... result) {
//                            if (result != null && result.length > 0) {
//                                Integer retCode = (Integer) result[0];
//                                if (retCode == 0) {
//                                    YWLog.i(TAG, "加入群成功！");
//                                    Notification.showToastMsg(TribeInfoActivity.this, "加入群成功！");
//                                    mTribeOp = null;
//                                    mHandler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            updateView();
//                                        }
//                                    });
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onError(int code, String info) {
//                            YWLog.i(TAG, "加入群失败， code = " + code + ", info = " + info);
//                            Notification.showToastMsg(TribeInfoActivity.this, "加入群失败, code = " + code + ", info = " + info);
//                        }
//
//                        @Override
//                        public void onProgress(int progress) {
//
//                        }
//                    }, mTribeId);
//                }
//            });
//        } else {
//            if (getLoginUserRole() == YWTribeMember.ROLE_NORMAL) {
//                mMangeTribeMembersLayout.setVisibility(View.VISIBLE);
//                mEditTribeInfoLayout.setVisibility(View.GONE);
//            } else {
//                mMangeTribeMembersLayout.setVisibility(View.VISIBLE);
//                mEditTribeInfoLayout.setVisibility(View.VISIBLE);
//            }
//        }
    }
    class TribeMsgRecSetCallback implements IWxCallback {

        private Handler uiHandler = new Handler(Looper.getMainLooper());

        public TribeMsgRecSetCallback() {

        }

        @Override
        public void onError(final int code, final String info) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onProgress(int progress) {

        }

        @Override
        public void onSuccess(Object... result) {
            mMsgRecFlag = mTribe.getMsgRecType();
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
//                    initMsgRecFlags();
//                    setMsgRecTypeLabel(mMsgRecFlag);
//                    IMNotificationUtils.showToast("设置成功: atFlag:" + mTribe.getAtFlag() + " flag:" + mTribe.getMsgRecType(), ChattingTribeDetailActivity.this);
                }
            });
        }
    }
    private void setMsgRecType(int msgRecType) {
        switch (msgRecType) {
            case YWProfileSettingsConstants.TRIBE_MSG_REC:
                mTribeService.unblockTribe(mTribe, new TribeMsgRecSetCallback());
                break;
            case YWProfileSettingsConstants.TRIBE_MSG_REJ:
                mTribeService.receiveNotAlertTribeMsg(mTribe, new TribeMsgRecSetCallback());
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.TribeMember:
                Intent intent = new Intent(ChattingTribeDetailActivity.this, TribeMembersActivity.class);
                intent.putExtra("TribeName", tribeName);
                intent.putExtra("tribeId", tribeId);
                startActivity(intent);
                break;
            case R.id.exitFromTribe:
                MaterialDialogUtil.showCommDialog(ChattingTribeDetailActivity.this, "确认退出群？", "确认", "取消", true, new MaterialDialogUtil.DialogCommListener() {
                    @Override
                    public void todosomething() {
                        TribesRpc.quit_for_tribe_id(tribeId, new BaseRPC.CallbackForBool() {
                            @Override
                            public void onSuccess(boolean boolValue) {
                                quitTribe();
                            }

                            @Override
                            public void onFailure(long id, XMLRPCException error) {
                                new CustomAbsClass.doSomething(ChattingTribeDetailActivity.this) {
                                    @Override
                                    public void todo() {
                                        Toast.makeText(ChattingTribeDetailActivity.this, "退出失败", Toast.LENGTH_SHORT).show();
                                    }
                                };
                            }
                        });
                    }
                });
                break;

        }
    }
    private void quitTribe()
    {
        mTribeService.exitFromTribe(new TribeHelper.MyIWxCallback() {
            @Override
            public void onWxSuccess(List<YWTribe> list) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChattingTribeDetailActivity.this, "退群成功！", Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putLong("tribeId", tribeId);
                        PostMediaTask.sendBroadcast(Constants.BroadcastAction.BroadcastAction_CloseTribe, bundle);
                        finish();
                    }
                });
            }

            @Override
            public void onWxFail() {
                //new TribeSystemMessageAdapter.JoinOrExitTribBaby(ChattingTribeDetailActivity.this,tribeId, null).join();
            }
        }.getIWxCallback(ChattingTribeDetailActivity.this), tribeId);
    }
    private void setMsgRecTypeLabel(int flag) {
        if(flag==YWProfileSettingsConstants.TRIBE_MSG_REC)
            isNotification.setChecked(false);
        else
            isNotification.setChecked(true);
//            YWProfileSettingsConstants.TRIBE_MSG_REJ:

    }

}
