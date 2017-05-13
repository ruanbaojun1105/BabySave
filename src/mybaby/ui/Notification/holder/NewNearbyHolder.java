package mybaby.ui.Notification.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.models.community.Place;
import mybaby.ui.more.BaseHolder;
import mybaby.util.AppUIUtils;

public class NewNearbyHolder extends BaseHolder<Integer> {
	
	private TextView summary_badge;
	private TextView tv_enter;
	private Context context;
	private TextView tv_nearby_title;
	
	public NewNearbyHolder(Context context) {
		this.context = context;
	}
	

	@Override
	public void refreshView() {
		int newFriendsActivityCount = getData();
		Place place=null;//且看後續怎麼修改
		tv_enter.setTypeface(MyBabyApp.fontAwesome);
		tv_enter.setText(context.getString(R.string.fa_angle_right));
		if(place != null && place.getPlace_name() != null){
			tv_nearby_title.setText(place.getPlace_name());
		}else{
			tv_nearby_title.setText(R.string.defaultNearbyName);
		}
		if(newFriendsActivityCount > 0){
			summary_badge.setVisibility(View.VISIBLE);
			summary_badge.setText(""+newFriendsActivityCount);
		}else{
			summary_badge.setVisibility(View.INVISIBLE);
		}
		
	}

	@Override
	public View initView() {
		View view = AppUIUtils.inflate(R.layout.notification_newnewaby);
		summary_badge = (TextView) view.findViewById(R.id.summary_badge);
		tv_enter = (TextView) view.findViewById(R.id.tv_enter);
		tv_nearby_title = (TextView) view.findViewById(R.id.tv_nearby_title);
		return view;
	}

}
