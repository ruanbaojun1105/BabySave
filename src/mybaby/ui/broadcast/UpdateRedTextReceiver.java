package mybaby.ui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.util.ActionBarUtils;

/**
 * Created by LeJi_BJ on 2016/3/23.
 */
public class UpdateRedTextReceiver extends BroadcastReceiver {

    private TextView textView;
    private int unread=0;


    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public UpdateRedTextReceiver(TextView textView) {
        this.textView = textView;
        unread=MyBabyApp.getSharedPreferences().getInt("unread", 0);
    }

    public void regiest() {
        //ActionBarUtils.setActionBackRed(textView);//初始化时不要显示红点  只有广播更新的时候才显示红点

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BroadcastAction.BroadcastAction_Notification_Summary);
        LocalBroadcastManager.getInstance(MyBabyApp.getContext()).registerReceiver(this, filter);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction()
                .equals(Constants.BroadcastAction.BroadcastAction_Notification_Summary)) {
            if (textView!=null)
                ActionBarUtils.setActionBackRed(textView,unread);
        }
    }
}
