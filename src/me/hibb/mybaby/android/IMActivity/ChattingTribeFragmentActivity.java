package me.hibb.mybaby.android.IMActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.WXAPI;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeMember;
import com.alibaba.mobileim.gingko.presenter.contact.IContactProfileUpdateListener;
import com.alibaba.mobileim.gingko.presenter.contact.YWContactManagerImpl;
import com.alibaba.mobileim.kit.common.IMUtility;
import com.alibaba.mobileim.tribe.IYWTribeService;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.BuildConfig;
import me.hibb.mybaby.android.R;
import me.hibb.mybaby.android.openIM.NotifacationListMerge;
import mybaby.Constants;
import mybaby.cache.CacheDataTask;
import mybaby.models.notification.NotificationCategory;
import mybaby.models.notification.TribeNotificationCategory;
import mybaby.rpc.notification.TribesRpc;
import mybaby.ui.MyBabyApp;
import mybaby.ui.Notification.adapter.NotificationCategoryAdapter;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.widget.RoundImageViewByXfermode;
import mybaby.util.ImageHelper;
import mybaby.util.ImageViewUtil;
import mybaby.util.MaterialDialogUtil;
import mybaby.util.Utils;

/**
 * Created by mayongge on 15-9-18.
 */
public class ChattingTribeFragmentActivity extends FragmentActivity {
    private List<YWTribeMember> mList = new ArrayList<YWTribeMember>();
    private static final String TAG = "ChattingTribeActivity";
    public static final String TRIBE_ID = "tribeId";
    public static final String TRIBE_NAME = "tribeName";
    private Fragment mCurrentFrontFragment;
    private RelativeLayout tipImageBar;
    private LinearLayout imageLin;
    private long tribeId;
    private String tribeName;
    private YWIMKit mIMKit;

    private TextView touch_bar_tv;
    private TextView touch_tv_red;
    private View editBar;
    private Context context;
    private long getTribeid;
    private Receiver receiver;
    private IYWTribeService mTribeService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chattingtribe_activity);
        try {
            context = this;
            Intent intent = getIntent();
            tribeId = intent.getLongExtra(TRIBE_ID, tribeId);
            tribeName = intent.getStringExtra(TRIBE_NAME);
            mIMKit = MyBayMainActivity.getmIMKit();
            mTribeService = mIMKit.getTribeService();
            YWLog.i(TAG, "onCreate");
            initActionBar(tribeName, ChattingTribeFragmentActivity.this);
            initView();
            createFragment();
            getTribeMembersFromLocal();
            getTribeMembersFromServer(tribeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPhotoView(List<String> mList) {
        if (mList==null&&mList.size()<=0){
            tipImageBar.setVisibility(View.GONE);
            return;
        }
        if (mList.size()<3) {
            tipImageBar.setVisibility(View.GONE);
            return;
        }else {
            tipImageBar.setVisibility(View.VISIBLE);
        }
        int paddingWidth=8;//
        int imageWidth= (int) Utils.dpToPx(35);
        RelativeLayout.LayoutParams lParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,imageWidth+paddingWidth*4);
        tipImageBar.setLayoutParams(lParams);
        tipImageBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChattingTribeFragmentActivity.this, TribeMembersActivity.class);
                intent.putExtra("TribeName", tribeName);
                intent.putExtra("tribeId", tribeId);
                startActivity(intent);
            }
        });
        //OverScrollDecoratorHelper.setUpStaticOverScroll(tipImageBar, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        imageLin= (LinearLayout) findViewById(R.id.ll_photoView);
        TextView tv_enter= (TextView) findViewById(R.id.tv_photoenter);
        tv_enter.setTypeface(MyBabyApp.fontAwesome);
        int num=(MyBabyApp.screenWidth-MyBabyApp.dip2px(20+5*2))/(imageWidth+paddingWidth*2);
        LogUtil.e(num + "touch");
        imageLin.removeAllViews();
        /*for (int i=0;i<num-mList.size()+3;i++){//3无意义，保证而已
            mList.add(ImageHelper.getMotherPicUrl());
        }*/
        for (int i=0;i<mList.size();i++){
            if (i==num)
                break;
            RoundImageViewByXfermode imageView = new RoundImageViewByXfermode( context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageWidth, imageWidth);
            layoutParams.setMargins(i==0?paddingWidth*2:paddingWidth, paddingWidth*2, paddingWidth*2, paddingWidth*2);// 4个参数按顺序分别是左上右下
            imageView.setLayoutParams(layoutParams);
            imageView.setTag(i);
            ImageViewUtil.displayImage(mList.get(i), imageView);
            imageLin.addView(imageView);
        }

    }


    private List<String> getPhotoData(List<YWTribeMember> mList) {
        //IYWContactProfileCallback callback = WXAPI.getInstance().getContactProfileCallback();
        List<String> newList = new ArrayList<>();
        for (YWTribeMember member : mList) {
            final IYWContact ywContact = IMUtility.getContactProfileInfo(member.getUserId(), member.getAppKey());
            String path ="";
            String name ="";
            if (ywContact==null){
                IYWContact contact = WXAPI.getInstance().getWXIMContact(member.getUserId());
                path = contact.getAvatarPath();
                name = contact.getShowName();
                if (!TextUtils.isEmpty(path)) {
                    if (member.getTribeRole() == YWTribeMember.ROLE_HOST) {
                        if (name.equals("辣妈小秘书"))
                            continue;
                    }
                    newList.add(path);
                }
            }else {
                path = ywContact.getAvatarPath();
                name = ywContact.getShowName();
                if (!TextUtils.isEmpty(path)){
                    if (member.getTribeRole() == YWTribeMember.ROLE_HOST) {
                        if (name.equals("辣妈小秘书"))
                            continue;
                    }
                    newList.add(path);
                }
            }
            if (newList.size()>10)
                break;//节省内存
        }
        if (newList.size()<8) {
            int a = mList.size() - newList.size()-1;
            if (a<0) a=0;
            for (int i = 0; i < a; i++) {//修复总人数没有显示的多  3/14
                newList.add(ImageHelper.getMotherPicUrl());
            }
        }
        return newList;
    }

    public void initActionBar(String title, final Activity activity) {//设置anctionbar
        View mActionBarView = LayoutInflater.from(activity).inflate(
                R.layout.actionbarchatting_title, null);
        TextView tv_title = (TextView) mActionBarView.findViewById(R.id.actionbar_title);
        TextView actionbar_back = (TextView) mActionBarView.findViewById(R.id.actionbar_back);

        //new UpdateRedTextReceiver((TextView) mActionBarView.findViewById(R.id.actionbar_back_badge)).regiest();

        ImageView actionbar_detail = (ImageView) mActionBarView.findViewById(R.id.image_button);
        tipImageBar=(RelativeLayout)findViewById(R.id.photo_view);
        actionbar_detail.setImageResource(R.drawable.group);
        actionbar_detail.setVisibility(View.VISIBLE);
        actionbar_back.setTypeface(MyBabyApp.fontAwesome);
        actionbar_back.setText(activity.getResources().getString(R.string.fa_angle_left));
        tv_title.setText(title);
        actionbar_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Intent intent = new Intent(ChattingTribeFragmentActivity.this, ChattingTribeDetailActivity.class);
                intent.putExtra("tribeId", tribeId);
                intent.putExtra("TribeName", tribeName);
                startActivity(intent);
            }
        });
        actionbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                activity.finish();
            }
        });
        ActionBar actionBar = activity.getActionBar();
        actionBar.setCustomView(mActionBarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void createFragment() {
        mCurrentFrontFragment = mIMKit.getTribeChattingFragment(tribeId);
        getSupportFragmentManager().beginTransaction().replace(R.id.wx_chat_container, mCurrentFrontFragment).commit();
        if (CacheDataTask.booleaRefush(MyBabyApp.getSharedPreferences().getLong("undate_tribe_member_"+tribeId,0),60*24)) {
            TribesRpc.update_group_member(tribeId, null);
            SharedPreferences.Editor edit = MyBabyApp.getSharedPreferences().edit();
            edit.putLong("undate_tribe_member_"+tribeId, System.currentTimeMillis());
            edit.commit();
        }
    }
    private void getTribeMembersFromLocal() {
        mTribeService.getMembers(new com.alibaba.mobileim.channel.event.IWxCallback() {
            @Override
            public void onSuccess(Object... objects) {
                mList.clear();
                if (mList != null || mList.size() > 0) {
                    mList.addAll((List<YWTribeMember>) objects[0]);
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

    private void getTribeMembersFromServer(long tribeId) {
        if (mTribeService==null)
            return;
        mTribeService.getMembersFromServer(new IWxCallback() {
            @Override
            public void onSuccess(Object... objects) {
                //获取群成员列表
                mList.clear();
                mList.addAll((List<YWTribeMember>) objects[0]);

                for (YWTribeMember member:mList) {
                    IMUtility.getContactProfileInfo(member.getUserId(), member.getAppKey());
                }
                new CustomAbsClass.doSomething(context) {
                    @Override
                    public void todo() {
                        initPhotoView(getPhotoData(mList));
                    }
                };
                YWContactManagerImpl impl= (YWContactManagerImpl) MyBayMainActivity.getmIMKit().getContactService();
                impl.addProfileUpdateListener(new IContactProfileUpdateListener() {
                    @Override
                    public void onProfileUpdate() {
                        new CustomAbsClass.doSomething(context) {
                            @Override
                            public void todo() {
                                initPhotoView(getPhotoData(mList));
                            }
                        };
                    }

                    @Override
                    public void onProfileUpdate(String s, String s1) {

                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i) {

            }
        }, tribeId);
    }

    private int unread=0;
    @Override
    protected void onResume() {
        super.onResume();
        try {
            mCurrentFrontFragment.setHasOptionsMenu(false);//百川重写了菜单，不使用
            for (NotificationCategory category: NotifacationListMerge.getNtcList(this)){
                if (category instanceof TribeNotificationCategory) {
                    TribeNotificationCategory a=(TribeNotificationCategory)category;
                    if (category.getTribe_id() == tribeId) {
                        unread = a.getSpace_unread();
                        boolean isStrong = a.isSpace_isStrong();
                        NotificationCategoryAdapter.setTextBgRedUnread(touch_tv_red, unread, isStrong);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        receiver = new Receiver();
        receiver.registReceiver();
        touch_bar_tv = (TextView) findViewById(R.id.touch_bar_tv);
        touch_tv_red = (TextView) findViewById(R.id.tab_badge);
        touch_bar_tv.setVisibility(View.VISIBLE);

        touch_bar_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomAbsClass.starTribeSpace(context, tribeId, tribeName, unread !=0);
            }
        });
        MaterialDialogUtil.DialogCommListener listener = new MaterialDialogUtil.DialogCommListener() {
            @Override
            public void todosomething() {
                SharedPreferences.Editor edit = MyBabyApp.getSharedPreferences().edit();
                edit.putBoolean("tribeHasOpen" + tribeId, true);
                edit.commit();
            }
        };
        if (!MyBabyApp.getSharedPreferences().getBoolean("tribeHasOpen" + tribeId, false)) {
            MaterialDialogUtil.showCommDialog(ChattingTribeFragmentActivity.this, "群守则", "为维护良好的群秩序，" +
                    "群内禁止任何形式的广告骚扰或其他不恰当信息，发现类似情况，请您【长按】垃圾信息内容【举报】，" +
                    "被核实后将会对相关用户进行处理。", "确认", null, false, listener, null, listener);
        }
        if (!MyBabyApp.currentUser.getBzRegistered()) {
            if (BuildConfig.DEBUG) {
                return;
            }
            editBar = findViewById(R.id.touch_bar_edit);
            editBar.setVisibility(View.VISIBLE);
            editBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CustomAbsClass.StarEdit() {
                        @Override
                        public void todo() {

                        }
                    }.StarTopicEdit(ChattingTribeFragmentActivity.this);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


    /**
     * 必须实现该方法，且该方法的实现必须跟以下示例代码完全一致！
     * 因为拍照和选择照片的时候会回调该方法，如果没有按以下方式覆写该方法会导致拍照和选择照片时应用crash
     *
     * @param arg0
     * @param arg1
     * @param arg2
     */
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        if (mCurrentFrontFragment != null) {
            mCurrentFrontFragment.onActivityResult(arg0, arg1, arg2);
        }
    }

    // 广播接收并响应处理 刷新页面
    private class Receiver extends BroadcastReceiver {
        public void registReceiver() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.BroadcastAction.BroadcastAction_CloseTribe);
            LocalBroadcastManager.getInstance(MyBabyApp.getContext())
                    .registerReceiver(this, filter);
        }

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_CloseTribe)) {
                    getTribeid = intent.getLongExtra("tribeId", 0);
                    if (getTribeid == tribeId)
                        finish();
            }
        }
    }
}
