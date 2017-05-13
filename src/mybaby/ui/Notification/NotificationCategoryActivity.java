package mybaby.ui.Notification;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;

import me.hibb.mybaby.android.R;
import mybaby.BaseActivity;
import mybaby.BaseFragmentActivity;
import mybaby.ui.MyBabyApp;

import mybaby.ui.base.MyBabyBaseActivity;
import mybaby.ui.broadcast.UpdateRedTextReceiver;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.main.NotificationCategoryFragment;


/**
 * Created by hu on 16/10/6.
 * update 16 10 10
 */
public class NotificationCategoryActivity extends MyBabyBaseActivity {
    private NotificationCategoryFragment mFragment;

    @Override
    public String getPageName() {
        return "消息";
    }

    @Override
    public String getTopTitle() {
        return "消息";
    }

    @Override
    public String getRightText() {
        return null;
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
        updateRedTextReceiver.setTextView(null);
        mFragment = ((MyBayMainActivity)MyBayMainActivity.activity).getNotificationFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_page, mFragment).commit();
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
