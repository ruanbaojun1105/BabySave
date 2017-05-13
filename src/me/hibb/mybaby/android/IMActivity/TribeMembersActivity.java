package me.hibb.mybaby.android.IMActivity;

/**
 * Created by Administrator on 2016/1/14 0014.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.mobileim.WXAPI;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.fundamental.widget.refreshlist.PullToRefreshListView;
import com.alibaba.mobileim.fundamental.widget.refreshlist.YWPullToRefreshBase;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeMember;
import com.alibaba.mobileim.gingko.presenter.contact.IContactProfileUpdateListener;
import com.alibaba.mobileim.gingko.presenter.contact.YWContactManagerImpl;
import com.alibaba.mobileim.gingko.presenter.tribe.IYWTribeChangeListener;
import com.alibaba.mobileim.kit.common.IMUtility;
import com.alibaba.mobileim.tribe.IYWTribeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.more.PersonPageActivity;
import mybaby.util.ActionBarUtils;
import mybaby.util.Utils;

public class TribeMembersActivity extends Activity implements AdapterView.OnItemLongClickListener {

    private static final int REQUEST_CODE = 1;
    private YWIMKit mIMKit;
    private IYWTribeService mTribeService;
    private IYWTribeChangeListener mTribeChangedListener;

    private long mTribeId;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private List<YWTribeMember> mList;
    private TribeMembersAdapter mAdapter;
    private TextView mAddTribeMembers;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String tribeName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tribe_members);
        init();
    }

    private void init() {
        initTitle();

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.tribe_members_list);
        mPullToRefreshListView.setMode(YWPullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        mPullToRefreshListView.setShowIndicator(false);
        mPullToRefreshListView.setDisableScrollingWhileRefreshing(false);
        mPullToRefreshListView.setRefreshingLabel("同步群成员列表");
        mPullToRefreshListView.setReleaseLabel("松开同步群成员列表");
        mPullToRefreshListView.setDisableRefresh(false);
        mPullToRefreshListView.setOnRefreshListener(new YWPullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTribeMembers();
            }
        });
        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.setOnItemLongClickListener(this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position - mListView.getHeaderViewsCount();
                final YWTribeMember member = mList.get(pos);
                String im_userId;
                if (member!= null) {
                    im_userId= member.getUserId();
                    Intent intent=new Intent(TribeMembersActivity.this, PersonPageActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("im_userId",im_userId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        mList = new ArrayList<YWTribeMember>();
        mAdapter = new TribeMembersAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        mIMKit = MyBayMainActivity.getmIMKit();
        mTribeService = mIMKit.getTribeService();

        getTribeMembers();

        mAddTribeMembers = (TextView) findViewById(R.id.add_tribe_members);
        mAddTribeMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(TribeMembersActivity.this, InviteTribeMemberActivity.class);
//                intent.putExtra(TribeConstants.TRIBE_ID, mTribeId);
//                startActivity(intent);
            }
        });
        mAddTribeMembers.setVisibility(View.GONE);

        initTribeChangedListener();
        mTribeService.addTribeListener(mTribeChangedListener);
    }

    private void initTitle() {
        Intent intent=getIntent();
        mTribeId = intent.getLongExtra("tribeId", 0);
        tribeName=intent.getStringExtra("TribeName");
        ActionBarUtils.initActionBar(tribeName, this);
//        View view = findViewById(R.id.title_bar);
//        view.setVisibility(View.VISIBLE);
//        view.setBackgroundColor(Color.parseColor("#00b4ff"));
//        TextView leftButton = (TextView) view.findViewById(R.id.left_button);
//        TextView titleView = (TextView) view.findViewById(R.id.title_self_title);
//        TextView rightButton = (TextView) view.findViewById(R.id.right_button);

//        leftButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.demo_common_back_btn_white, 0, 0, 0);
//        leftButton.setText("返回");
//        leftButton.setTextColor(Color.WHITE);
//        leftButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        titleView.setText("群成员列表");
//        titleView.setTextColor(Color.WHITE);
//
//        rightButton.setText("管理");
//        rightButton.setTextColor(Color.WHITE);
//        rightButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                YWTribe tribe = mTribeService.getTribe(mTribeId);
//                //群的普通成员没有加入权限，所以因此加入view
//                if (tribe.getTribeType() == YWTribeType.CHATTING_TRIBE && getLoginUserRole() == YWTribeMember.ROLE_NORMAL) {
////                    Notification.showToastMsg(TribeMembersActivity.this, "您不是群管理员，没有管理权限~");
//                } else {
//                    mAddTribeMembers.setVisibility(View.VISIBLE);
//                }
//            }
//        });
    }

    private void getTribeMembers() {
        mTribeService.getMembers(new IWxCallback() {
            @Override
            public void onSuccess(Object... result) {
                onSuccessGetMembers((List<YWTribeMember>) result[0]);
                //同时触发一下向服务器更新列表
                getMembersFromServer();
            }

            @Override
            public void onError(int code, String info) {
                if (isFinishing()) {
                    return;
                }
//                Notification.showToastMsg(TribeMembersActivity.this, "error, code = " + code + ", info = " + info);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshListView.onRefreshComplete(false, false);
                    }
                });
            }

            @Override
            public void onProgress(int progress) {

            }
        }, mTribeId);
    }

    private void getMembersFromServer(){
        mTribeService.getMembersFromServer(new IWxCallback() {
            @Override
            public void onSuccess(Object... result) {
                onSuccessGetMembers((List<YWTribeMember>) result[0]);
            }

            @Override
            public void onError(int code, String info) {

            }

            @Override
            public void onProgress(int progress) {

            }
        }, mTribeId);
    }
    private final int ONREFUSH=0x1;
    private Handler UIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ONREFUSH:
                    if (mPullToRefreshListView == null)
                        return;
                    if (mAdapter == null)
                        return;
                    if (msg.obj != null && mList != null) {
                        List<YWTribeMember> list = (List<YWTribeMember>) msg.obj;
                        mList.clear();
                        mList.addAll(list);
                        refreshAdapter();
                        mPullToRefreshListView.onRefreshComplete(false, true);
                        refreshContact();
                        break;

                    }
            }
        }
    };
    private void onSuccessGetMembers(List<YWTribeMember> members){
        if (members == null || isFinishing()){
            return;
        }
        List<YWTribeMember> newlist=new ArrayList<>();
        for (YWTribeMember member:members){
            IYWContact ywContact=IMUtility.getContactProfileInfo(member.getUserId(), member.getAppKey());
            String name ="";
            if (ywContact==null){
                IYWContact contact = WXAPI.getInstance().getWXIMContact(member.getUserId());
                name = contact.getShowName();
                if (!TextUtils.isEmpty(name)) {
                    if (member.getTribeRole() == YWTribeMember.ROLE_HOST) {
                        if (name.equals("辣妈小秘书"))
                            continue;
                    }
                }
            }else {
                name = ywContact.getShowName();
                if (!TextUtils.isEmpty(name)){
                    if (member.getTribeRole() == YWTribeMember.ROLE_HOST) {
                        if (name.equals("辣妈小秘书"))
                            continue;
                    }
                }
            }
            newlist.add(member);
        }
        Collections.sort(newlist, new SortMemberList());
        Utils.sendMessage(ONREFUSH, newlist, UIHandler);
    }


    class SortMemberList implements Comparator<YWTribeMember> {

        @Override
        public int compare(YWTribeMember member0, YWTribeMember member1) {
            return getLenMember(member1)-getLenMember(member0);
        }
    }

    private int getLenMember(YWTribeMember member0){
        final IYWContact ywContact0 = IMUtility.getContactProfileInfo(member0.getUserId(), member0.getAppKey());
        int a=0;
        String path ="";
        if (ywContact0==null){
            IYWContact contact0 = WXAPI.getInstance().getWXIMContact(member0.getUserId());
            if (contact0!=null)
                path = contact0.getAvatarPath();
            if (!TextUtils.isEmpty(path)) {
                a=path.length();
            }
        }else {
            path = ywContact0.getAvatarPath();
            if (!TextUtils.isEmpty(path)){
                a=path.length();
            }
        }
        return a;
    }




    private void refreshContact() {
        YWContactManagerImpl impl= (YWContactManagerImpl) MyBayMainActivity.getmIMKit().getContactService();
        impl.addProfileUpdateListener(new IContactProfileUpdateListener() {
            @Override
            public void onProfileUpdate() {
                refreshAdapter();
            }

            @Override
            public void onProfileUpdate(String s, String s1) {

            }
        });
    }
    /**
     * 刷新当前列表
     */
    private void refreshAdapter() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    mAdapter.refreshData(mList);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTribeService.removeTribeListener(mTribeChangedListener);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final int pos = position - mListView.getHeaderViewsCount();
        final YWTribeMember member = mList.get(pos);
        YWTribe tribe = mTribeService.getTribe(mTribeId);
        final String[] items = getItems(tribe, member);
        if (items == null) {
            return true;
        }
//        new YWAlertDialog.Builder(this)
//                .setTitle("群成员管理")
//                .setItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (items[which].equals(TribeConstants.TRIBE_SET_MANAGER)){
//                            mTribeService.setTribeManager(null, mTribeId, member.getUserId());
//                        } else if (items[which].equals(TribeConstants.TRIBE_CANCEL_MANAGER)){
//                            mTribeService.cancelTribeManager(null, mTribeId, member.getUserId());
//                        } else if (items[which].equals(TribeConstants.TRIBE_EXPEL_MEMBER)){
//                            mTribeService.expelMember(new IWxCallback() {
//                                @Override
//                                public void onSuccess(Object... result) {
//                                    Notification.showToastMsg(TribeMembersActivity.this, "踢人成功！");
//                                    mList.remove(member);
//                                    refreshAdapter();
//                                }
//
//                                @Override
//                                public void onError(int code, String info) {
//
//                                }
//
//                                @Override
//                                public void onProgress(int progress) {
//
//                                }
//                            }, mTribeId, member.getUserId());
//                        }
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                })
//                .create().show();
        return true;
    }

    /**
     * 判断当前登录用户在群组中的身份
     *
     * @return
     */
    private int getLoginUserRole() {
        int role = YWTribeMember.ROLE_NORMAL;
        String loginUser = mIMKit.getIMCore().getLoginUserId();
        for (YWTribeMember member : mList) {
            if (member.getUserId().equals(loginUser)) {
                role = member.getTribeRole();
            }
        }
        return role;
    }

    private String[] getItems(YWTribe tribe, YWTribeMember member) {
        String[] items = null;
//        if (tribe.getTribeType() == YWTribeType.CHATTING_TRIBE && getLoginUserRole() == YWTribeMember.ROLE_HOST) {
//            if (member.getTribeRole() == YWTribeMember.ROLE_NORMAL) {
//                items = new String[]{TribeConstants.TRIBE_SET_MANAGER, TribeConstants.TRIBE_EXPEL_MEMBER};
//            } else if (member.getTribeRole() == YWTribeMember.ROLE_MANAGER) {
//                items = new String[]{TribeConstants.TRIBE_CANCEL_MANAGER, TribeConstants.TRIBE_EXPEL_MEMBER};
//            }
//        } else if (tribe.getTribeType() == YWTribeType.CHATTING_GROUP && getLoginUserRole() == YWTribeMember.ROLE_HOST && member.getTribeRole() != YWTribeMember.ROLE_HOST) {
//            items = new String[]{TribeConstants.TRIBE_EXPEL_MEMBER};
//        }
        return items;
    }

    private void initTribeChangedListener(){
        mTribeChangedListener = new IYWTribeChangeListener() {
            @Override
            public void onInvite(YWTribe tribe, YWTribeMember user) {

            }

            @Override
            public void onUserJoin(YWTribe tribe, YWTribeMember user) {
                mList.add(user);
                refreshAdapter();
            }

            @Override
            public void onUserQuit(YWTribe tribe, YWTribeMember user) {
                mList.remove(user);
                refreshAdapter();
            }

            @Override
            public void onUserRemoved(YWTribe tribe, YWTribeMember user) {
                //只有被踢出群的用户会收到该回调，即如果收到该回调表示自己被踢出群了
                openTribeListFragment();
            }

            @Override
            public void onTribeDestroyed(YWTribe tribe) {
                openTribeListFragment();
            }

            @Override
            public void onTribeInfoUpdated(YWTribe tribe) {

            }

            @Override
            public void onTribeManagerChanged(YWTribe tribe, YWTribeMember user) {
                for (YWTribeMember member : mList){
                    if (member.getUserId().equals(user.getUserId()) && member.getAppKey().equals(user.getAppKey())){
                        mList.remove(member);
                        mList.add(user);
                        refreshAdapter();
                        break;
                    }
                }
            }

            @Override
            public void onTribeRoleChanged(YWTribe tribe, YWTribeMember user) {
                for (YWTribeMember member : mList){
                    if (member.getUserId().equals(user.getUserId()) && member.getAppKey().equals(user.getAppKey())){
                        mList.remove(member);
                        mList.add(user);
                        refreshAdapter();
                        break;
                    }
                }
            }
        };
    }

    private void openTribeListFragment() {
//        Intent intent = new Intent(this, FragmentTabs.class);
//        intent.putExtra(TribeConstants.TRIBE_OP, TribeConstants.TRIBE_OP);
//        startActivity(intent);
        finish();
    }

}

