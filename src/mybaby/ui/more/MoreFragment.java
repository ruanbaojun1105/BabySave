package mybaby.ui.more;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.diary.PostRepository;
import mybaby.models.person.SelfPerson;
import mybaby.share.UmengShare;
import mybaby.share.UmengShare.OpenShare;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.util.AppUIUtils;
import mybaby.util.ImageViewUtil;

/**
 * layoutCreator自动生成控件代码
 */
public class MoreFragment extends Fragment implements OnClickListener {
    private View rootView;// 缓存Fragment view

    private MyMoreFragmentReceiver receiver;
    public static UMSocialService moreController = UMServiceFactory
            .getUMSocialService(Constants.DESCRIPTOR);
    private TextView tv_enter_push;
    private RelativeLayout rl_setting_push;
    private TextView tv_enter_help;
    private RelativeLayout rl_setting_help;
    private TextView tv_enter_feedback;
    private RelativeLayout rl_setting_feedback;
    private TextView tv_enter_share;
    private RelativeLayout rl_setting_share;
    private TextView tv_setting_signup;
    private TextView tv_enter_signup;
    private RelativeLayout rl_setting_signup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //MyBabyApp.initScreenParams(this.getActivity());
        // 配置需要分享的相关平台
        new UmengShare().configPlatforms(moreController, this.getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMSsoHandler ssoHandler = moreController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_more, null);
        initView();
        //initView();
        rl_setting_push.setOnClickListener(this);
        rl_setting_share.setOnClickListener(this);
        rl_setting_feedback.setOnClickListener(this);
        rl_setting_help.setOnClickListener(this);
        setSignup();
        fillData();
        return rootView;
    }

    private void fillData() {
		/*displayAvauter(iv_person_page);
		if (MyBabyApp.currentUser.isSelf()) {
			tv_person_page.setText("我");
		}*/

        tv_enter_share.setTypeface(MyBabyApp.fontAwesome);
        tv_enter_feedback.setTypeface(MyBabyApp.fontAwesome);
        tv_enter_push.setTypeface(MyBabyApp.fontAwesome);
        tv_enter_signup.setTypeface(MyBabyApp.fontAwesome);
        tv_enter_help.setTypeface(MyBabyApp.fontAwesome);
        tv_enter_share.setText(getString(R.string.fa_angle_right));
        tv_enter_feedback.setText(getString(R.string.fa_angle_right));
        tv_enter_push.setText(getString(R.string.fa_angle_right));
        tv_enter_signup.setText(getString(R.string.fa_angle_right));
        tv_enter_help.setText(getString(R.string.fa_angle_right));
    }

    private void setSignup() {
        if (!MyBabyApp.currentUser.getBzRegistered()) {
            tv_setting_signup.setText("注册");
            rl_setting_signup.setOnClickListener(this);
        } else {
            if (!TextUtils.isEmpty(MyBabyApp.currentUser.getFrdTelephone())) {
                tv_setting_signup.setText("已注册: " + MyBabyApp.currentUser.getFrdTelephone());
            } else {
                tv_setting_signup.setText("已注册: " + MyBabyApp.currentUser.getEmail());
            }

        }
    }

    private void initView() {
        //rl_person_page = (RelativeLayout) rootView.findViewById(R.id.rl_person_page);
        rl_setting_push = (RelativeLayout) rootView
                .findViewById(R.id.rl_setting_push);
        rl_setting_share = (RelativeLayout) rootView
                .findViewById(R.id.rl_setting_share);
        rl_setting_signup = (RelativeLayout) rootView
                .findViewById(R.id.rl_setting_signup);
        rl_setting_feedback = (RelativeLayout) rootView
                .findViewById(R.id.rl_setting_feedback);
        tv_setting_signup = (TextView) rootView
                .findViewById(R.id.tv_setting_signup);
        tv_enter_help = (TextView) rootView.findViewById(R.id.tv_enter_help);
        tv_enter_share = (TextView) rootView.findViewById(R.id.tv_enter_share);
        tv_enter_feedback = (TextView) rootView.findViewById(R.id.tv_enter_feedback);
        tv_enter_push = (TextView) rootView.findViewById(R.id.tv_enter_push);
        tv_enter_signup = (TextView) rootView.findViewById(R.id.tv_enter_signup);
        tv_enter_push = (TextView) rootView.findViewById(R.id.tv_enter_push);
        tv_enter_push.setOnClickListener(this);
        rl_setting_push = (RelativeLayout) rootView.findViewById(R.id.rl_setting_push);
        rl_setting_push.setOnClickListener(this);
        tv_enter_help = (TextView) rootView.findViewById(R.id.tv_enter_help);
        tv_enter_help.setOnClickListener(this);
        rl_setting_help = (RelativeLayout) rootView.findViewById(R.id.rl_setting_help);
        rl_setting_help.setOnClickListener(this);
        tv_enter_feedback = (TextView) rootView.findViewById(R.id.tv_enter_feedback);
        tv_enter_feedback.setOnClickListener(this);
        rl_setting_feedback = (RelativeLayout) rootView.findViewById(R.id.rl_setting_feedback);
        rl_setting_feedback.setOnClickListener(this);
        tv_enter_share = (TextView) rootView.findViewById(R.id.tv_enter_share);
        tv_enter_share.setOnClickListener(this);
        rl_setting_share = (RelativeLayout) rootView.findViewById(R.id.rl_setting_share);
        rl_setting_share.setOnClickListener(this);
        tv_setting_signup = (TextView) rootView.findViewById(R.id.tv_setting_signup);
        tv_setting_signup.setOnClickListener(this);
        tv_enter_signup = (TextView) rootView.findViewById(R.id.tv_enter_signup);
        tv_enter_signup.setOnClickListener(this);
        rl_setting_signup = (RelativeLayout) rootView.findViewById(R.id.rl_setting_signup);
        rl_setting_signup.setOnClickListener(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("更多"); //统计页面
        if (receiver == null) {
            receiver = new MyMoreFragmentReceiver();
            receiver.registerMyReceiver();
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("更多");
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(getActivity())
                    .unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.rl_setting_push:
                intent = new Intent(getActivity(), MessagePageActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_setting_share:
                //统计点击次数
                MobclickAgent.onEvent(getActivity(), "22");//告诉朋友们
                try {
                    new OpenShare(getActivity(), moreController, UmengShare.setShareBean(getActivity()));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case R.id.rl_setting_feedback:
                //统计点击次数
                MobclickAgent.onEvent(getActivity(), "23");//反馈
                //MainUtils.feedback(context);
                CustomAbsClass.starWebViewIntent(getActivity(), Constants.MY_BABY_HLEP_FEEDBACK);
                break;
            case R.id.rl_setting_signup:
                if (!MyBabyApp.currentUser.getBzRegistered()) {
                    CustomAbsClass.starRegister(getActivity());
                }
                break;
            case R.id.rl_setting_help:
                CustomAbsClass.starWebViewIntent(getActivity(), Constants.MY_BABY_HLEP_HELP);
                break;
        }

    }

    private class MyMoreFragmentReceiver extends BroadcastReceiver {

        public void registerMyReceiver() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done);
            LocalBroadcastManager.getInstance(MyBabyApp.getContext()).registerReceiver(this, filter);
        }

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done)) {
                setSignup();
            }
        }

    }

    public static SelfPerson displayAvauter(ImageView iv_person_page) {
        SelfPerson person = PostRepository.loadSelfPerson(MyBabyApp.currentUser
                .getUserId());
        if (person != null) {
            ImageViewUtil.displayImage(person.getAvatarUrl(), iv_person_page);
        } else {
            iv_person_page.setImageDrawable(AppUIUtils.getDrawable(R.drawable.avatar_female));
        }
        return person;
    }

    public static void displayAvauter(SelfPerson person, ImageView iv_person_page) {
        if (person != null) {
            ImageViewUtil.displayImage(person.getAvatarUrl(), iv_person_page);
        } else {
            iv_person_page.setImageDrawable(AppUIUtils.getDrawable(R.drawable.avatar_female));
        }
    }
}
