package mybaby.ui.main;

import android.view.View;

import com.nineoldandroids.animation.ObjectAnimator;

import java.util.Arrays;
import java.util.Random;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.cache.CacheDataTask;
import mybaby.rpc.community.CommunityItemRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;

/**
 * Created by LeJi_BJ on 2016/2/19.
 */
public class NCommunityFragment extends HomeBaseFragment {
    @Override
    protected int getLayoutResId() {
        return R.layout.blank_page;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected boolean isSendAsnkRed() {
        return false;
    }

    @Override
    protected void onMediaFileDoneOver(String[] mediaFilePaths) {
        if (Constants.category!=null) {
            CustomAbsClass.starTopicEditIntent(getActivity(), Constants.category.getId(), Constants.category.getTitle(), null, Arrays.asList(mediaFilePaths));
            Constants.category=null;
        }
    }

    @Override
    protected void init() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MyBayMainActivity.hasLoadCommunity=false;
    }

    @Override
    public void onResume() {
        super.onResume();



        /*
        if (!CacheDataTask.booleaRefush(MyBabyApp.getSharedPreferences().getLong("install_time", 0), 60 * 24 * 60))//安装后的60天内不播放动画
            return ;
        if (getActivity() instanceof MyBayMainActivity ) {
            MyBayMainActivity a=(MyBayMainActivity) getActivity();
            Toolbar toolbar = a.getHomeToolbar();
            ViewPager pager=a.getmViewPager();
            if (toolbar==null||pager==null)
                return;
            if (pager.getCurrentItem()==MyBayMainActivity.communityTabIndex)
                starAnim(toolbar.findViewById(R.id.add_topic));
        }*/


    }

    private void starAnim(View v) {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(v, "alpha", 1.0f, 0.2f, 1.0f,0.1f,1.0f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(v, "rotation", 0.0f, 360f,0f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 1.2f,0.8f,0.0F,1.0f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(v, "translationX",0f, -20f, 20f,-20f,20f,-20f,20f,-20f,0);
        ObjectAnimator[] anims={animator1,animator2,animator3,animator4};
        /*AnimatorSet animSet = new AnimatorSet();
        animSet.play(animator1).with(animator2).before(animator3).after(animator4);
        */
        ObjectAnimator a=anims[new Random().nextInt(4)];
        //a.setInterpolator(new CycleInterpolator(1.0f));
        a.setDuration(3000);
        //a.setStartDelay(500);
        a.start();
    }

    @Override
    protected boolean isCreatSatus() {
        return false;
    }

    @Override
    protected String getInitRPC() {
        return CommunityItemRPC.getCommunity();
    }

    @Override
    protected String getPageName() {
        return "社区";
    }

    @Override
    protected String getPageTag() {
        return MyBayMainActivity.communityTabTag;
    }

    @Override
    protected String getCacheKey() {
        return Constants.CacheKey_CommunityActivity;
    }

    @Override
    protected int getPtrBgColor() {
        return 0;
    }

    @Override
    protected int getFragmentAtId() {
        return R.id.content_page;
    }

    @Override
    protected LoadMoreListViewFragment getFragment() {
        return null;
    }
}
