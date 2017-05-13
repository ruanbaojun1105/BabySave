package me.hibb.mybaby.android.IMActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.util.YWLog;

import org.xmlrpc.android.XMLRPCException;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.User;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.UserRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.more.PersonPageActivity;

/**
 *
 */
public class ChattingFragmentActivity extends FragmentActivity{

    private static final String TAG = "ChattingActivity";
    public static final String TARGET_ID = "targetId";
    public static final  String USERNAME="username";

    private Fragment mCurrentFrontFragment;
    private TextView title_tv;
    private String username;
    private String targetId;
    private User chattingUser;
    private YWIMKit mIMKit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tribespace_activity);
        Intent intent = getIntent();
        targetId = intent.getStringExtra(TARGET_ID);
        username =intent.getExtras().getString(USERNAME);
        this.closeOptionsMenu();
        getUserByIMuserId();
        YWLog.i(TAG, "onCreate");
        initActionBar(ChattingFragmentActivity.this, username);
        createFragment();
    }
    private void getUserByIMuserId() {
        UserRPC.getUserByIMUserID(targetId, new BaseRPC.CallbackgetUserByIMuserid() {

            @Override
            public void onSuccess(User user) {
                chattingUser=user;
            }

            @Override
            public void onFailure(long id, XMLRPCException error) {

                Log.e("error", error + "");
            }
        });
    }

    public  void initActionBar(final Activity activity,String username) {//设置anctionbar
        View mActionBarView = LayoutInflater.from(activity).inflate(
                R.layout.actionbarchatting_title, null);
        title_tv = (TextView) mActionBarView.findViewById(R.id.actionbar_title);
        TextView actionbar_back = (TextView) mActionBarView.findViewById(R.id.actionbar_back);
        //new UpdateRedTextReceiver((TextView) mActionBarView.findViewById(R.id.actionbar_back_badge)).regiest();
        ImageView actionbar_detail=(ImageView)mActionBarView.findViewById(R.id.image_button);
        actionbar_detail.setImageResource(R.drawable.person);
        actionbar_detail.setVisibility(View.VISIBLE);
        actionbar_back.setTypeface(MyBabyApp.fontAwesome);
        actionbar_back.setText(activity.getResources().getString(R.string.fa_angle_left));
        title_tv.setText(username);
        actionbar_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chattingUser==null)
                    return;
                Intent intent = new Intent(ChattingFragmentActivity.this, PersonPageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", chattingUser);
                intent.putExtras(bundle);
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
    private void createFragment(){
        mIMKit = MyBayMainActivity.getmIMKit();
        mCurrentFrontFragment = mIMKit.getChattingFragment(targetId, Constants.OPENIM_KEY);
        getSupportFragmentManager().beginTransaction().replace(R.id.wx_chat_container, mCurrentFrontFragment).commit();
        mCurrentFrontFragment.setHasOptionsMenu(false);//百川重写了菜单，不使用
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentFrontFragment.setHasOptionsMenu(false);//百川重写了菜单，不使用
    }

    /**
     * 必须实现该方法，且该方法的实现必须跟以下示例代码完全一致！
     * 因为拍照和选择照片的时候会回调该方法，如果没有按以下方式覆写该方法会导致拍照和选择照片时应用crash
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
}
