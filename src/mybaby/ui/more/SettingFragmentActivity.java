package mybaby.ui.more;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;

import me.hibb.mybaby.android.R;
import mybaby.BaseFragmentActivity;
import mybaby.ui.MyBabyApp;

/**
 * Created by bj 1/5
 */
@ContentView(R.layout.blank_page)
public class SettingFragmentActivity extends BaseFragmentActivity {

    private static final String TAG = "SettingActivity";

    private MoreFragment moreFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createFragment();
        initActionBar("设置", this);
    }
    public static void initActionBar(String title,final Activity activity) {//设置anctionbar
        View mActionBarView = LayoutInflater.from(activity).inflate(
                R.layout.actionbar_title_holder, null);
        TextView tv_title = (TextView) mActionBarView.findViewById(R.id.actionbar_title);
        TextView actionbar_back = (TextView) mActionBarView.findViewById(R.id.actionbar_back);
        actionbar_back.setTypeface(MyBabyApp.fontAwesome);
        actionbar_back.setText(activity.getResources().getString(R.string.fa_angle_left));
        tv_title.setText(title);
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
        moreFragment = new MoreFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content_page, moreFragment).commit();
    }

}
