package mybaby.ui.more.holder;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.models.community.activity.AbstractMainActivity;
import mybaby.ui.community.holder.ActivityItem;
import mybaby.ui.more.BaseHolder;
import mybaby.util.AppUIUtils;
import mybaby.util.DateUtils;
import mybaby.util.ImageViewUtil;
/**
 * 个人主页   动态的布局
 * @author Administrator
 *
 */
public class PersonTop1ActivityHolder extends BaseHolder<AbstractMainActivity> {
	private ImageView iv_activity;
	private TextView tv_activity;
	private TextView tv_activity_time;
	private TextView tv_activity_number;
	private View view;
	private TextView tv_enter;

	@Override
	public void refreshView() {
		AbstractMainActivity activity = getData();
		if (activity.getImages()!=null) {
			String url = activity.getImages()[0].getThumbnailUrl();
			ImageViewUtil.displayImage( url,iv_activity);
		}
		if(activity.getCategory()!=null)
		{
		String title=TextUtils.isEmpty(activity.getCategory().getTitle())?"":activity.getCategory().getTitle();
//	    ActivityItem.getSpecilText(UIUtils.getContext(), tv_activity, 0, title, activity.getContent());
//		boolean hastitle = TextUtils.isEmpty(title) ? false : true;
		String sp1=ActivityItem.ToDBC(title+ " " + activity.getContent());
		SpannableString sp = new SpannableString(sp1);
		sp.setSpan(new ForegroundColorSpan(AppUIUtils.getResources().getColor(R.color.blue)),0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv_activity.setText(sp);
		}
		else {
			tv_activity.setText(activity.getContent());
		}
		
		tv_activity_time.setText(DateUtils.datetime2DisplayString(activity
				.getDatetime()));
		tv_activity_number.setText("动态");
		tv_enter.setTypeface(MyBabyApp.fontAwesome);
		tv_enter.setText(R.string.fa_angle_right);
	}

	@Override
	public View initView() {
		view = AppUIUtils.inflate(R.layout.person_page_activity);
		iv_activity = (ImageView) view.findViewById(R.id.iv_activity);
		tv_activity = (TextView) view.findViewById(R.id.tv_activity);
		tv_activity_time = (TextView) view.findViewById(R.id.tv_activity_time);
		tv_activity_number = (TextView) view.findViewById(R.id.tv_activity_number);
		tv_enter = (TextView) view.findViewById(R.id.tv_enter);
		return view;
	}

}
