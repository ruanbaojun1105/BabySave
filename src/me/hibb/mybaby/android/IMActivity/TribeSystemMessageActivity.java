package me.hibb.mybaby.android.IMActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.WxLog;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.lib.model.message.SystemMessage;
import com.alibaba.mobileim.tribe.IYWTribeService;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.rpc.BaseRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.broadcast.PostMediaTask;
import mybaby.ui.broadcast.UpdateRedTextReceiver;
import mybaby.ui.main.MyBayMainActivity;

public class TribeSystemMessageActivity extends Activity {

    private YWIMKit mIMKit;
    private TribeSystemMessageAdapter mAdapter;
    private ListView mListView;
    private IYWTribeService mTribeService;
    private List<YWMessage> mList = new ArrayList<YWMessage>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private TextView tv_setShare, tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_message);
        init();
    }

    private void init(){
        SharedPreferences.Editor edit = MyBabyApp.getSharedPreferences().edit();
        edit.putInt("tribeUnreadNum", 0);
        edit.commit();
        PostMediaTask.sendBroadcast(Constants.BroadcastAction.BroadcastAction_NotificationCagetory_Refush, null);

        mIMKit = MyBayMainActivity.getmIMKit();
        mTribeService = mIMKit.getTribeService();
        initTitle();
        mListView = (ListView) findViewById(R.id.message_list);
        mAdapter = new TribeSystemMessageAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(mListView);
        loadSystemMessages();
    }

    private void initTitle() {
        View view = LayoutInflater.from(this).inflate(
                R.layout.actionbar_topic_edit, null);
        tv_title = (TextView) view.findViewById(R.id.actionbar_title);
        tv_title.setText("群系统消息");//如果是浏览器则不要设置头部标签
        ((TextView) view.findViewById(R.id.actionbar_back))
                .setTypeface(MyBabyApp.fontAwesome);
        new UpdateRedTextReceiver((TextView) view.findViewById(R.id.actionbar_back_badge)).regiest();
        view.findViewById(R.id.actionbar_back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
        tv_setShare = (TextView) view.findViewById(R.id.sendtopic);
        tv_setShare.setText("清空");
        tv_setShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTribeService.clearTribeSystemMessages();
                loadSystemMessages();
                SharedPreferences.Editor edit = MyBabyApp.getSharedPreferences().edit();
                edit.putBoolean("tribeHasInvite", false);
                edit.commit();
                PostMediaTask.sendBroadcast(Constants.BroadcastAction.BroadcastAction_NotificationCagetory_Refush, null);
            }
        });
        tv_setShare.setText("");
        tv_setShare.setOnClickListener(null);//去掉清空功能，无奈
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(view);

    }

    public void refreshAdapter(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //Collections.reverse(mList);
                mAdapter.refreshData(mList);
            }
        });
    }

    private void loadSystemMessages(){
        IYWTribeService tribeService = MyBayMainActivity.getmIMKit().getTribeService();
        tribeService.getTribeSystemMessages(new IWxCallback() {
            @Override
            public void onSuccess(Object... result) {
                if (result != null && result.length > 0) {
                    mList = (List<YWMessage>) result[0];
                    refreshAdapter();
                }
            }

            @Override
            public void onError(int code, String info) {
                WxLog.i("test", "result");
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    public void acceptToJoinTribe(final YWMessage message, final BaseRPC.Callback callback) {
        final SystemMessage msg = (SystemMessage) message;
        YWIMKit imKit = MyBayMainActivity.getmIMKit();
        if (imKit != null) {
            IYWTribeService tribeService = imKit.getTribeService();
            if (tribeService != null) {
                tribeService.accept(new IWxCallback() {
                    @Override
                    public void onSuccess(Object... result) {
                        Boolean isSuccess = (Boolean) result[0];
                        if (isSuccess) {
                            msg.setSubType(SystemMessage.SYSMSG_TYPE_AGREE);
                            refreshAdapter();
                            mTribeService.updateTribeSystemMessage(msg);
                            if (callback!=null)
                                callback.onSuccess();
                        }else {
                            if (callback != null)
                                callback.onFailure(0, null);
                        }
                    }

                    @Override
                    public void onError(int code, String info) {
                        if (callback!=null)
                            callback.onFailure(0,null);
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                }, Long.valueOf(msg.getAuthorId()), msg.getRecommender());
            }
        }
    }

    public void refuseToJoinTribe(YWMessage message) {
        final SystemMessage msg = (SystemMessage) message;
        msg.setSubType(SystemMessage.SYSMSG_TYPE_IGNORE);
        refreshAdapter();
        mTribeService.updateTribeSystemMessage(msg);
    }
    public void disJoinTribe(YWMessage message) {
        final SystemMessage msg = (SystemMessage) message;
        msg.setSubType(SystemMessage.SYSMSG_TYPE_REQ);
        refreshAdapter();
        mTribeService.updateTribeSystemMessage(msg);
    }
    public void agreeToJoinTribe(YWMessage message) {
        final SystemMessage msg = (SystemMessage) message;
        msg.setSubType(SystemMessage.SYSMSG_TYPE_AGREE);
        refreshAdapter();
        mTribeService.updateTribeSystemMessage(msg);
    }


}