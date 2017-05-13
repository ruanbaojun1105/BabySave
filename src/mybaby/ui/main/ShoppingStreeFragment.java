package mybaby.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.ui.base.MyBabyBaseActivity;
import mybaby.ui.broadcast.BackTopListviewReceiver;
import mybaby.ui.community.parentingpost.WebviewFragment;
import mybaby.util.LogUtils;

/**
 * Created by LeJi_BJ on 2016/2/19.
 */
public class ShoppingStreeFragment extends Fragment {
    private View rootView;
    private WebviewFragment webviewFragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtils.e("是否被销毁过"+isDestroyShop);
        rootView = inflater.inflate(R.layout.fragment_shopping, null);
        boolean isInShop=false;
        try {//界面被销毁后直接开启预加载
            if (((MyBayMainActivity)getActivity()).getmViewPager().getCurrentItem()==MyBayMainActivity.shoppingTabIndex){
                isInShop=true;
                MyBayMainActivity.hasLoadShopStree=true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isInShop=true;
            MyBayMainActivity.hasLoadShopStree=true;
        }

        Bundle bundle=new Bundle();
        bundle.putString("url", Constants.MY_BABY_SHOP_STREE);
        bundle.putBoolean("canChangeTitle", false);
        bundle.putBoolean("setZeroMarginHeight", true);
        bundle.putBoolean("isLoadSatus", isInShop?true:false);//初始不加载
        bundle.putInt("mbtopbar", 0);
        webviewFragment=new WebviewFragment(null);
        webviewFragment.setArguments(bundle);
        getChildFragmentManager().beginTransaction().replace(R.id.content_page,webviewFragment).commitAllowingStateLoss();
        new BackTopListviewReceiver(new BackTopListviewReceiver.ToDoListener() {
            @Override
            public void todo(Bundle bundle) {
                if (getActivity()==null)
                    return;
                String tag=bundle.getString("currentTag", "");
                String url=bundle.getString("url", "");
                boolean send=bundle.getBoolean("isSendRed",false);
                if (!TextUtils.isEmpty(tag)&&tag.equals(MyBayMainActivity.shoppingTabTag)) {
                    if (webviewFragment==null)
                        return;
                    if (webviewFragment.getWebView()!=null) {
                        WebviewFragment.scrollToTop(webviewFragment.getWebView());
                        webviewFragment.setIsSendRedNtfc(send);
                        if (!TextUtils.isEmpty(url)) {
                            webviewFragment.getWebview_progress_two().setVisibility(View.VISIBLE);
                            webviewFragment.getWebView().loadUrl(url);
                        }
                    }
                }
            }
        }).regiest();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e("onResume_shop webview");
        if (isDestroyShop&&MyBayMainActivity.hasLoadShopStree){//处理销毁被加载空白页
            isDestroyShop=false;
            new Handler(getActivity().getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (webviewFragment==null)
                        return;
                    if (webviewFragment.getWebView()!=null) {
                        webviewFragment.getWebview_progress_two().setVisibility(View.VISIBLE);
                        webviewFragment.getWebView().loadUrl(Constants.MY_BABY_SHOP_STREE);
                    }
                }
            }, 500);
        }
    }
    public void perfError(){
        new Handler(getActivity().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (webviewFragment==null)
                    return;
                webviewFragment.perfError();
            }
        });
    }

    public static boolean isDestroyShop=false;
    @Override
    public void onDestroyView() {
        //MyBayMainActivity.hasLoadShopStree=false;
        isDestroyShop=true;
        super.onDestroyView();
    }
}
