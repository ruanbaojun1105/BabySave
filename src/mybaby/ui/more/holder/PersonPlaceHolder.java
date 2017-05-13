package mybaby.ui.more.holder;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.models.community.Place;
import mybaby.ui.more.BaseHolder;
import mybaby.util.AppUIUtils;
/**
 * 个人主页 地点的布局
 * @author Administrator
 *
 */
public class PersonPlaceHolder extends BaseHolder<Place[]> {

	private TextView tv_place_number;
	private TextView[] mTV;
	private View view;
	private Place[] arrPlace;
	private int number;
	private LinearLayout ll_second;
	private TextView tv_enter;

	@Override
	public void refreshView() {
		arrPlace = getData();
		tv_enter.setTypeface(MyBabyApp.fontAwesome);
		tv_enter.setText(R.string.fa_angle_right);
		if(arrPlace == null || "".equals(arrPlace)){
			number = 0;
		}else{
			number = arrPlace.length;
		}
		tv_place_number.setText("地点（" + number + "）");
		if (number == 0) {
			ll_second.setVisibility(View.GONE);
			mTV[0].setText("地点");
		} 
		else if (number >= 1 && number <= 2) {
			for (int i = 0; i < number; i++) {
				ll_second.setVisibility(View.GONE);
				mTV[i].setText(arrPlace[i].getPlace_name());
			}
		} else if(number >= 3 && number <= 4){
			for(int i=0;i<number;i++){
				ll_second.setVisibility(View.VISIBLE);
				mTV[i].setText(arrPlace[i].getPlace_name());
			}
		}
		else {
			for (int i = 0; i < 4; i++) {
				ll_second.setVisibility(View.VISIBLE);
				mTV[i].setText(arrPlace[i].getPlace_name());
			}
		}
	}


	@Override
	public View initView() {
		view = AppUIUtils.inflate(R.layout.person_page_place);
		ll_second = (LinearLayout) view.findViewById(R.id.ll_second);
		mTV = new TextView[4];
		mTV[0] = (TextView) view.findViewById(R.id.tv_place1);
		mTV[1] = (TextView) view.findViewById(R.id.tv_place2);
		mTV[2] = (TextView) view.findViewById(R.id.tv_place3);
		mTV[3] = (TextView) view.findViewById(R.id.tv_place4);
		tv_place_number = (TextView) view.findViewById(R.id.tv_place_number);
		tv_enter = (TextView) view.findViewById(R.id.tv_enter);
		return view;
	}

}
