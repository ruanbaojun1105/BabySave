package mybaby.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.tribe.IYWTribeService;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;

import org.xmlrpc.android.XMLRPCException;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.BuildConfig;
import me.hibb.mybaby.android.R;
import me.hibb.mybaby.android.openIM.TribeHelper;
import mybaby.Constants;
import mybaby.models.User;
import mybaby.models.discovery.Discovery;
import mybaby.models.discovery.DiscoveryObjs;
import mybaby.models.person.SelfPerson;
import mybaby.rpc.UserRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.broadcast.BackTopListviewReceiver;
import mybaby.ui.community.TestActivity;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.discovery.adapter.DiscoverAdapter;
import mybaby.ui.more.MoreFragment;
import mybaby.ui.posts.home.HomeTimelineFragment;
import mybaby.ui.widget.RoundImageViewByXfermode;
import mybaby.util.DrawableManager;
import mybaby.util.TextViewUtil;

/**
 * Created by LeJi_BJ on 2016/3/24
 */
public class MeFragment extends Fragment implements View.OnClickListener{
    private ObservableRecyclerView recyclerView;
    private ProgressBar progress_refush;
    private View rootView;// 缓存Fragment view
    private View headView;
    private DiscoverAdapter adapter;
    private ViewMeHolder viewMeHolder;
    private SelfPerson person;

    private List<DiscoveryObjs> discoveryObjsList=new ArrayList<>();;
    private String[][] titles={
            {"icon_topic",
             "icon_place",
             "icon_activity",
             "icon_like",
             "icon_reply",
             "icon_collection",
             "icon_setting"},
            {"话题","地点","动态","赞过","回复","收藏夹","设置"}
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.recyclerview_base, null);
        initData();
        initView();
        return rootView;
    }

    private View initHeadView(){
        if (headView==null) {
            headView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home_head, null);
            viewMeHolder=new ViewMeHolder(headView);
            headView.setTag(viewMeHolder);
        }else viewMeHolder= (ViewMeHolder) headView.getTag();
        return headView;
    }


    private void initView(){
        recyclerView = (ObservableRecyclerView) rootView.findViewById(R.id.recycle_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));
        //OverScrollDecoratorHelper.setUpOverScroll(recyclerView,OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        progress_refush = (ProgressBar) rootView.findViewById(R.id.progress_refush);
        progress_refush.setVisibility(View.VISIBLE);

        adapter= new DiscoverAdapter(getActivity(), discoveryObjsList, new DiscoverAdapter.OnClickForTitle() {
            @Override
            public void onClick(String title) {
                switch (title) {
                    case "话题":
                        CustomAbsClass.starWebViewIntent(getActivity(), Constants.MY_BABY_Topic_URL);
                        break;
                    case "地点":
                        CustomAbsClass.openFollowPlaceActivity(getActivity());
                        break;
                    case "动态":
                        if (MyBabyApp.currentUser==null)
                            return;
                        CustomAbsClass.openPersionListActivity(getActivity(), MyBabyApp.currentUser.getUserId());
                        break;
                    case "赞过":
                        CustomAbsClass.starLikeOrLooked(getActivity(), 1);
                        break;
                    case "回复":
                        CustomAbsClass.starLikeOrLooked(getActivity(), 2);
                        break;
                    case "收藏夹":
                        CustomAbsClass.starWebViewIntent(getActivity(), Constants.MY_BABY_FAVORITE);
                        break;
                    case "设置":
                        CustomAbsClass.starSettingActivity(getActivity());
                        break;
                    case "测试页面":
                        //CustomAbsClass.starNotificationCategoryActivity(getActivity());
                        Intent intent=new Intent(getActivity(), TestActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        adapter.addHeaderView(headView);
        View view= getActivity().getLayoutInflater().inflate(R.layout.fragment_home_content,null);
        adapter.addFooterView(view);
        recyclerView.setAdapter(adapter);
        progress_refush.setVisibility(View.GONE);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tribe_lin:
                CustomAbsClass.starTribeGroupActivity(getActivity());
                break;
            case R.id.friend_lin:
                CustomAbsClass.starUserFriendListActivity(getActivity());
                break;
            case R.id.follow_lin:
                CustomAbsClass.starPersionFollow(getActivity(), MyBabyApp.currentUser.getUserId());
                break;
            case R.id.follower_lin:
                CustomAbsClass.starPersionFance(getActivity(), MyBabyApp.currentUser.getUserId());
                break;
            case R.id.home_me_head:
                CustomAbsClass.starPersionEditActivity(getActivity(),person);
                break;
        }
    }
    private void initData(){
        Discovery[] discoveries1={
                new Discovery(DrawableManager.getDrawableResourceId(getActivity(),titles[0][0]),titles[1][0]),
                new Discovery(DrawableManager.getDrawableResourceId(getActivity(),titles[0][1]),titles[1][1]),
                new Discovery(DrawableManager.getDrawableResourceId(getActivity(),titles[0][2]),titles[1][2])
        };
        Discovery[] discoveries2={
                new Discovery(DrawableManager.getDrawableResourceId(getActivity(),titles[0][3]),titles[1][3]),
                new Discovery(DrawableManager.getDrawableResourceId(getActivity(),titles[0][4]),titles[1][4])
        };
        Discovery[] discoveries3={
                new Discovery(DrawableManager.getDrawableResourceId(getActivity(),titles[0][5]),titles[1][5])
        };
        Discovery[] discoveries4={
                new Discovery(DrawableManager.getDrawableResourceId(getActivity(),titles[0][6]),titles[1][6])
        };

        discoveryObjsList.add(new DiscoveryObjs(discoveries3));
        discoveryObjsList.add(new DiscoveryObjs(discoveries1));
        discoveryObjsList.add(new DiscoveryObjs(discoveries2));
        discoveryObjsList.add(new DiscoveryObjs(discoveries4));

        if (BuildConfig.DEBUG){
            Discovery[] discoveries5={
                    new Discovery(DrawableManager.getDrawableResourceId(getActivity(),titles[0][6]),"测试页面")
            };
            discoveryObjsList.add(new DiscoveryObjs(discoveries5));
        }
        initHeadView();
        if (MyBabyApp.currentUser==null)
            return;
        new InfoReceiver().registerInfoReceiver();
        new BackTopListviewReceiver(new BackTopListviewReceiver.ToDoListener() {
            @Override
            public void todo(Bundle bundle) {
                String tag=bundle.getString("currentTag", "");
                if (!TextUtils.isEmpty(tag)&&tag.equals(MyBayMainActivity.meTabTag)) {
                    HomeTimelineFragment.backListTop(recyclerView);
                }
            }
        }).regiest();
        TextViewUtil.setTagIcon(viewMeHolder.tagIcon,getContext());
        person= MoreFragment.displayAvauter(viewMeHolder.person_image);
        if (person==null)
            return;
        viewMeHolder.persion_name.setText(person.getName());
        viewMeHolder.tribe_lin.setOnClickListener(this);
        viewMeHolder.follow_lin.setOnClickListener(this);
        viewMeHolder.follower_lin.setOnClickListener(this);
        viewMeHolder.friend_lin.setOnClickListener(this);
        viewMeHolder.home_me_head.setOnClickListener(this);
        UserRPC.getProfile(MyBabyApp.currentUser.getUserId(), new UserRPC.UserCallback() {
            @Override
            public void onSuccess(final User user) {
                new CustomAbsClass.doSomething(getActivity()) {
                    @Override
                    public void todo() {
                        viewMeHolder.persion_follow_num.setText(String.valueOf(user.getFollowCount()));
                        viewMeHolder.persion_follower_num.setText(String.valueOf(user.getFollowerCount()));
                        viewMeHolder.persion_friend_num.setText(String.valueOf(user.getFriend_count()));
                        if (Constants.hasLoginOpenIM)
                            getTribeNum();
                    }
                };
            }
            @Override
            public void onFailure(long id, XMLRPCException error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //if (person!=null)
        //    MoreFragment.displayAvauter(person,viewMeHolder.person_image);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }




    static class ViewMeHolder{
        RoundImageViewByXfermode person_image;
        TextView persion_name;
        TextView tagIcon;

        TextView persion_tribe_num;
        TextView persion_friend_num;
        TextView persion_follow_num;
        TextView persion_follower_num;

        LinearLayout tribe_lin;
        LinearLayout friend_lin;
        LinearLayout follow_lin;
        LinearLayout follower_lin;
        LinearLayout home_me_head;

        public ViewMeHolder(View headView) {
            person_image= (RoundImageViewByXfermode) headView.findViewById(R.id.home_me_image);

            tribe_lin= (LinearLayout) headView.findViewById(R.id.tribe_lin);
            friend_lin= (LinearLayout) headView.findViewById(R.id.friend_lin);
            follow_lin= (LinearLayout) headView.findViewById(R.id.follow_lin);
            follower_lin= (LinearLayout) headView.findViewById(R.id.follower_lin);
            home_me_head= (LinearLayout) headView.findViewById(R.id.home_me_head);

            persion_name= (TextView) headView.findViewById(R.id.home_me_name);
            tagIcon= (TextView) headView.findViewById(R.id.tag);

            persion_tribe_num= (TextView) headView.findViewById(R.id.home_tribe_num);
            persion_friend_num= (TextView) headView.findViewById(R.id.home_friend_num);
            persion_follow_num= (TextView) headView.findViewById(R.id.home_follow_num);
            persion_follower_num= (TextView) headView.findViewById(R.id.home_follower_num);
        }
    }

    private class InfoReceiver extends BroadcastReceiver {
        public void registerInfoReceiver() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.BroadcastAction.BroadcastAction_CommunityMain_Refush);
            filter.addAction(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done);
            filter.addAction(Constants.BroadcastAction.BroadcastAction_Person_Add);
            filter.addAction(Constants.BroadcastAction.BroadcastAction_Person_Edit);
            filter.addAction(Constants.BroadcastAction.BroadcastAction_Person_Delete);
            filter.addAction(Constants.BroadcastAction.BroadcastAction_Notification_Summary);

            LocalBroadcastManager.getInstance(MyBabyApp.getContext())
                    .registerReceiver(this, filter);
        }

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Person_Add)||
                    intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Person_Edit)||
                    intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Person_Delete)
                    ){
                try {
                    if (viewMeHolder!=null) {
                        person = MoreFragment.displayAvauter(viewMeHolder.person_image);
                        viewMeHolder.persion_name.setText(person.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Notification_Summary)){
                if (Constants.hasLoginOpenIM)
                    getTribeNum();
            }else if (intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done)){
                MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_MainActivity_Home_TagBadgeUpdate);
            }
        }
    }
    private void getTribeNum(){
        YWIMKit mIMKit= MyBayMainActivity.getmIMKit();
        if (mIMKit==null)
            return;
        IYWTribeService tribeService=mIMKit.getTribeService();
        if (tribeService==null)
            return;
        tribeService.getAllTribesFromServer(new TribeHelper.MyIWxCallback() {
            @Override
            public void onWxSuccess(List<YWTribe> list) {
                viewMeHolder.persion_tribe_num.setText(list.size()+"");
                list=null;
            }

            @Override
            public void onWxFail() {
            }
        }.getIWxCallback(getActivity()));//获取群列表
    }
}
