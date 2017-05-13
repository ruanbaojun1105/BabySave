package mybaby.util;

import android.app.ActionBar;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import me.hibb.mybaby.android.R;
import mybaby.models.User;
import mybaby.ui.MyBabyApp;
import mybaby.ui.Notification.adapter.NotificationCategoryAdapter;
import mybaby.ui.broadcast.UpdateRedTextReceiver;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.main.NotificationCategoryFragment;

public class ActionBarUtils {


	public static void setActionBackRed(TextView textView,int oldUnread){
		if (MyBayMainActivity.activity==null)
			return;
		NotificationCategoryFragment fragment=((MyBayMainActivity)MyBayMainActivity.activity).getNotificationFragment();
		if (fragment!=null){
				//if (MyBabyApp.getSharedPreferences().getInt("unread", 0)>oldUnread)
				//	NotificationCategoryAdapter.setTextBgRedUnread(textView,MyBabyApp.getSharedPreferences().getInt("unread", 0),true);
			int un=fragment.getUnread();
			if (un>oldUnread)
				NotificationCategoryAdapter.setTextBgRedUnread(textView,un,true);
		}//else NotificationCategoryAdapter.setTextBgRedUnread(textView,MyBabyApp.getSharedPreferences().getInt("unread", 0),true);
	}

	public static void initActionBar(String title,Activity activity){
		initActionBar(title,activity,null);
	}
	
	
	/**
	 * 自定义actionBar
	 * @param title  actionBar的标题
	 * @param activity activity对象
	 * @param user     用户（用于判断是否是用户本人）
	 */
	public static void initActionBar(String title,final Activity activity,User user) {
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		View mActionBarView = LayoutInflater.from(activity).inflate(
				R.layout.actionbar_title, null);
		TextView tv_title = (TextView) mActionBarView.findViewById(R.id.actionbar_title);
		new UpdateRedTextReceiver((TextView) mActionBarView.findViewById(R.id.actionbar_back_badge)).regiest();
		TextView actionbar_back = (TextView) mActionBarView.findViewById(R.id.actionbar_back);
		actionbar_back.setTypeface(MyBabyApp.fontAwesome);
		actionbar_back.setText(activity.getResources().getString(R.string.fa_angle_left));
		if(user!=null){
			if (user.isSelf()) {
				tv_title.setText(title);
			} else {
				tv_title.setText(user.getName());
			}
		}else{
			tv_title.setText(title);
		}
		actionbar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (activity==null)
					return;
				activity.finish();
			}
		});
		ActionBar actionBar = activity.getActionBar();
		actionBar.setCustomView(mActionBarView, lp);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
	}
}
