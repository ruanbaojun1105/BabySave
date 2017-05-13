package mybaby.ui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import mybaby.Constants;
import mybaby.ui.MyBabyApp;

/**
 * Created by LeJi_BJ on 2016/3/23.
 * 列表双击刷新或者回到顶部
 */
public class BackTopListviewReceiver extends BroadcastReceiver {

    public interface ToDoListener {
        void todo(Bundle bundle);
    }

    private ToDoListener listener;

    public BackTopListviewReceiver(ToDoListener listener) {
        this.listener = listener;
    }

    public BackTopListviewReceiver regiest() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BroadcastAction.BroadcastAction_Activity_Main_Need_BackTop);
        LocalBroadcastManager.getInstance(MyBabyApp.getContext())
                .registerReceiver(this, filter);
        return this;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction()
                .equals(Constants.BroadcastAction.BroadcastAction_Activity_Main_Need_BackTop)) {
            try {
                if (listener!=null&&intent.getExtras()!=null)
                    listener.todo(intent.getExtras());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
