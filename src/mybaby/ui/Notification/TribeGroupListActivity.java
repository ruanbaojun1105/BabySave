package mybaby.ui.Notification;

import android.view.View;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.tribe.IYWTribeService;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import me.hibb.mybaby.android.R;
import me.hibb.mybaby.android.openIM.TribeHelper;
import mybaby.Constants;
import mybaby.ui.base.BaseOnrefreshAndLoadMoreFragment;
import mybaby.ui.base.MyBabyBaseActivity;
import mybaby.ui.community.adapter.ParentingPostAdapter;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.widget.BaseTLoadmoreRpc;

/**
 * Created by bj 2016 3 24
 */
public class TribeGroupListActivity extends MyBabyBaseActivity {

    @Override
    public String getPageName() {
        return "群组列表";
    }

    @Override
    public String getTopTitle() {
        return "群组";
    }

    @Override
    public String getRightText() {
        return "";
    }

    @Override
    public void onMediaFileDoneOver() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.blank_page_base;
    }

    @Override
    public int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    public int getRightImgId() {
        return 0;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        final BaseOnrefreshAndLoadMoreFragment fragment = new BaseOnrefreshAndLoadMoreFragment() {
            @Override
            public BaseQuickAdapter getBaseAdapter() {
                return new ParentingPostAdapter(getContext());
            }

            @Override
            public boolean isEnableLoadMore() {
                return false;
            }

            @Override
            public Object[] getStausParamers() {
                return new Object[]{(long)0};
            }

            @Override
            public boolean needForceRefush() {
                return true;
            }

            @Override
            public boolean isLoadSatus() {
                return true;
            }

            @Override
            public boolean needSendAsnkRed() {
                return false;
            }

            @Override
            public BaseTLoadmoreRpc getRPC() {
                return new BaseTLoadmoreRpc() {
                    @Override
                    public void toTRpcInternet(Object[] objects, int lastId, final boolean isRefresh, final BaseOnrefreshAndLoadMoreFragment fragment) throws Exception {
                        YWIMKit mIMKit= MyBayMainActivity.getmIMKit();
                        if (mIMKit==null){
                            try {
                                fragment.onRpcFail(0,null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        IYWTribeService tribeService=mIMKit.getTribeService();
                        if (tribeService==null){
                            try {
                                fragment.onRpcFail(0,null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return;
                        }

                        tribeService.getAllTribesFromServer(new TribeHelper.MyIWxCallback() {
                            @Override
                            public void onWxSuccess(List<YWTribe> list) {
                                try {
                                    fragment.onRpcSuccess(isRefresh, 0, false, null, TribeHelper.getTribeGroupList(list));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onWxFail() {
                                try {
                                    fragment.onRpcFail(0,null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.getIWxCallback(TribeGroupListActivity.this));//获取群列表
                    }
                };
            }

            @Override
            public String getCacheType() {
                return  Constants.CacheKey_Trinbes;
            }

            @Override
            public View getHeaderView() {
                return null;
            }

            @Override
            public void init() {

            }
        };
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_page, fragment).commit();
    }

    @Override
    public boolean textToolbarType() {
        return false;
    }

    @Override
    public boolean imageToolbarType() {
        return false;
    }

    @Override
    public View.OnClickListener getRight_click() {
        return null;
    }

}
