package mybaby.ui.Notification.holder;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.ui.more.BaseHolder;
import mybaby.util.AppUIUtils;

public class NewFriendHolder extends BaseHolder<Integer> {
	
	private TextView summary_badge;
	private TextView tv_enter;
	private RelativeLayout item_tag;
	private Context context;
	
	public NewFriendHolder(Context context) {
		this.context = context;
	}

	@Override
	public void refreshView() {
		int newFriendsActivityCount = getData();
		tv_enter.setTypeface(MyBabyApp.fontAwesome);
		tv_enter.setText(context.getString(R.string.fa_angle_right));
		if(newFriendsActivityCount > 0){
			summary_badge.setVisibility(View.VISIBLE);
			summary_badge.setText(""+newFriendsActivityCount);
		}else{
			summary_badge.setVisibility(View.INVISIBLE);
		}
		
	}

	@Override
	public View initView() {
		View view = AppUIUtils.inflate(R.layout.notification_newfriend);
		summary_badge = (TextView) view.findViewById(R.id.summary_badge);
		tv_enter = (TextView) view.findViewById(R.id.tv_enter);
		item_tag=(RelativeLayout) view.findViewById(R.id.tag_friend);
		return view;
	}

}
