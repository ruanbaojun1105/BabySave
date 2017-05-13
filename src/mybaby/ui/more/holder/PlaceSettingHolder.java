package mybaby.ui.more.holder;

import android.view.View;
import android.widget.TextView;
import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.models.community.UserPlaceSetting;
import mybaby.ui.more.BaseHolder;
import mybaby.util.AppUIUtils;
/**
 * 搜索地点的布局
 * @author Administrator
 *
 */
public class PlaceSettingHolder extends BaseHolder<UserPlaceSetting> {
	
	private TextView tv_text;
	private TextView tv_enter;
	@Override
	public void refreshView() {
		UserPlaceSetting setting = getData();
		tv_enter.setTypeface(MyBabyApp.fontAwesome);
		tv_enter.setText(R.string.fa_angle_right);
		tv_text.setText(setting.getSettingName()+":  "+setting.getSettingValue().getPlace_name());
	}

	@Override
	public View initView() {
		View view = AppUIUtils.inflate(R.layout.person_page_place_setting);
		tv_text = (TextView) view.findViewById(R.id.tv_text);
		tv_enter = (TextView) view.findViewById(R.id.tv_enter);
		return view;
	}

}
