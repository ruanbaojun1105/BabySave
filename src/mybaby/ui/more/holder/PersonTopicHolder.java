package mybaby.ui.more.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.models.community.TopicCategory;
import mybaby.ui.more.BaseHolder;
import mybaby.util.AppUIUtils;

/**
 * 个人主页  话题的布局
 * @author Administrator
 *
 */
public class PersonTopicHolder extends BaseHolder<TopicCategory[]>{
	 
	private TextView tv_topic_number;
	private TextView[] mTV;
	private LinearLayout second_topic;
	int number = 0;
	private TopicCategory[] arrCategory;
	private View view;
	private TextView tv_enter;

	@Override
	public void refreshView() {
		arrCategory = getData();
		tv_enter.setTypeface(MyBabyApp.fontAwesome);
		tv_enter.setText(R.string.fa_angle_right);
		if(arrCategory == null){
			number = 0;
		}else{
			number = arrCategory.length;
		}
		setContent();
	}

	private void setContent() {
		tv_topic_number.setText("关注话题（"+number+"）");
		if(number == 0){
			second_topic.setVisibility(View.GONE);
			mTV[0].setText("关注话题");
		}else if(number >= 1 && number <=2){
			for (int i = 0; i < number; i++) {
				second_topic.setVisibility(View.GONE);
				mTV[i].setText(arrCategory[i].getTitle());
			}
		}else if(number>=3 && number <= 4){
			for(int i = 0;i<number;i++){
				mTV[i].setText(arrCategory[i].getTitle());
			}
		}else{
			for (int i = 0; i < 4; i++) {
				mTV[i].setText(arrCategory[i].getTitle());
			}
		}
	}

	@Override
	public View initView() {
		view = AppUIUtils.inflate(R.layout.person_page_topic);
		mTV = new TextView[4];
		tv_topic_number = (TextView) view.findViewById(R.id.tv_topic_number);
		mTV[0] = (TextView) view.findViewById(R.id.tv_topic1);
		mTV[1] = (TextView) view.findViewById(R.id.tv_topic2);
		mTV[2] = (TextView) view.findViewById(R.id.tv_topic3);
		mTV[3] = (TextView) view.findViewById(R.id.tv_topic4);
		second_topic = (LinearLayout) view.findViewById(R.id.second_topic);
		tv_enter = (TextView) view.findViewById(R.id.tv_enter);
		return view;
	}

}
