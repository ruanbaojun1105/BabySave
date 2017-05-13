package mybaby.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import org.xmlrpc.android.XMLRPCException;

import java.util.List;

import mybaby.Constants;
import mybaby.RxUtils;
import mybaby.models.community.Banner;
import mybaby.rpc.community.CommunityItemRPC;
import mybaby.share.UmengShare;
import mybaby.ui.MediaHelper;
import mybaby.ui.MyBabyApp;
import mybaby.ui.broadcast.BackTopListviewReceiver;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.community.fragment.LoadMoreRecyclerViewFragment;
import mybaby.ui.community.holder.ItemState;
import mybaby.ui.posts.home.HomeTimelineFragment;
import rx.Subscription;

/**
 * Created by Administrator on 2016/6/20 0020.
 */
public abstract class HomeBaseFragment extends Fragment implements MediaHelper.MediaHelperCallback {
    //分享初始化
    public static UMSocialService homeController = UMServiceFactory.getUMSocialService(Constants.DESCRIPTOR);
    //根视图
    private View rootView;// 缓存Fragment view
    //图库回调
    protected MediaHelper mMediaHelper;
    //rx
    private Subscription subscription;
    //主列表
    protected LoadMoreListViewFragment fragment ;
    //广播
    private CommunityReceiver receiver;
    private BackTopListviewReceiver backReceiver;
    //视图ID
    protected abstract int getLayoutResId();
    //视图初始化
    protected abstract void initView();
    //RPC的方法名
    protected abstract String getInitRPC();
    //页面名称
    protected abstract String getPageName();
    //页面标签
    protected abstract String getPageTag();
    //缓存的key
    protected abstract String getCacheKey();
    //下拉刷新背景色
    protected abstract int getPtrBgColor();
    //fragment附着的ID
    protected abstract int getFragmentAtId();
    //如需自己生成初始的列表fragment,可以返回此回调，默认NULL
    protected abstract LoadMoreListViewFragment getFragment();
    //是否初始加载列表数据
    protected abstract boolean isCreatSatus();
    //刷新完成是否刷新
    protected abstract boolean isSendAsnkRed();
    //重写照片回调
    protected abstract void onMediaFileDoneOver(String[] mediaFilePaths);

    protected abstract void init();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutResId(), null);
        new UmengShare().configPlatforms(homeController, getContext());
        mMediaHelper = new MediaHelper(getActivity(), this);

        receiver=new CommunityReceiver().registerInfoReceiver();
        if (!TextUtils.isEmpty(getPageTag()))
            backReceiver=new BackTopListviewReceiver(new BackTopListviewReceiver.ToDoListener() {
                @Override
                public void todo(Bundle bundle) {
                    String tag = bundle.getString("currentTag");
                    if (!TextUtils.isEmpty(tag) && tag.equals(getPageTag())) {
                        HomeTimelineFragment.backListTop(fragment.getListView());
                        if (tag.equals(MyBayMainActivity.communityTabTag))
                            fragment.autoRefresh(MainUtils.community_needRefresh);
                        else
                            fragment.autoRefresh();
                    }
                }
            }).regiest();
        initView();
        if (getFragment()==null)
            fragment = new LoadMoreListViewFragment(getCacheKey(),Constants.CACHE_PAGE_INTERVA,isSendAsnkRed());
        else fragment=getFragment();
        fragment.setOnTRpcInternet(initRpc());
        if (getPtrBgColor()!=0)
            fragment.setPtrBgColor(getPtrBgColor());
        //是否初始加载数据
        Bundle bundle=new Bundle();
        bundle.putBoolean("isLoadSatus",isCreatSatus());
        fragment.setArguments(bundle);
        fragment.setIsInViewPage(true);
        getChildFragmentManager().beginTransaction().replace(getFragmentAtId(), fragment).commitAllowingStateLoss();//commit();可能会报错
        init();
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getPageName()); // 统计页面
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getPageName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        RxUtils.unsubscribeIfNotNull(subscription);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        UMSsoHandler ssoHandler = homeController.getConfig()
                .getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        mMediaHelper.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMediaFileDone(String[] mediaFilePaths) {
        // TODO Auto-generated method stub
        onMediaFileDoneOver(mediaFilePaths);
    }

    /**
     * 初始化刷新数据
     */
    private LoadMoreListViewFragment.TRpc initRpc() {
        // TODO Auto-generated method stub
        return new LoadMoreListViewFragment.TRpc() {

            @Override
            public void toTRpcInternet(int lastId, int userId, int parentId, boolean isRefresh, LoadMoreRecyclerViewFragment fragment) throws Exception {

            }

            @Override
            public void toTRpcInternet(int lastId, int userId, int parentId,
                                       final boolean isRefresh, final LoadMoreListViewFragment fragment) {
                // TODO Auto-generated method stub
                CommunityItemRPC.getSuitRPCList(getInitRPC(),lastId,null, new CommunityItemRPC.ListCallback() {

                    @Override
                    public void onSuccess(boolean hasMore, int newLastId,
                                          List<ItemState> items, final Banner[] banners) {
                        // TODO Auto-generated method stub
                        try {
                            fragment.onTSuccessToList(isRefresh, hasMore, newLastId, items);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(long id, XMLRPCException error) {
                        // TODO Auto-generated method stub
                        try {
                            fragment.onTFailToList(id, error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void toTRpcInternet(Object[] objects, int lastId, boolean isRefresh, Fragment fragment) throws Exception {

            }
        };
    }


    // 广播接收并响应处理 地点设置成功返回刷新
    private class CommunityReceiver extends BroadcastReceiver {
        public CommunityReceiver registerInfoReceiver() {
            IntentFilter filter = new IntentFilter();
            if (!TextUtils.isEmpty(getPageTag()))
                filter.addAction(Constants.BroadcastAction.BroadcastAction_Activity_Main_Need_BackTop);
            filter.addAction(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done);
            LocalBroadcastManager.getInstance(MyBabyApp.getContext())
                    .registerReceiver(this, filter);
            return this;
        }

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done)) {
                if (fragment == null)
                    return;
                try {
                    fragment.autoRefresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
