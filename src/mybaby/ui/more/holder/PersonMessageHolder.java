package mybaby.ui.more.holder;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.models.User;
import mybaby.models.person.Person;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.more.BaseHolder;
import mybaby.ui.widget.RoundImageViewByXfermode;
import mybaby.util.ImageViewUtil;
/**
 * 个人主页  个人信息的布局
 * @author Administrator
 *
 */
public class PersonMessageHolder extends BaseHolder<User> {
	private Context context;
	String[] images =null;
	
	public PersonMessageHolder(Context context){
		this.context = context;
	}
	private RoundImageViewByXfermode iv_person;
	private TextView tv_name;
	private TextView tv_age;
	private TextView tv_follow;
	private TextView tv_follower;
	private View view;
	private LinearLayout follow_and_follower;
	private User user;

	@Override
	public void refreshView() {
		user = getData();
		if (user == null)
			return;
		String url=user.getAvatarUrl();
		String realUrl=user.getAvatarThumbnailUrl();
		boolean isNull=user.getRealAvatarThumbnailUrlIsNull();
		if (iv_person.getMeasuredHeight()==0) {
			if (!TextUtils.isEmpty(url)) {
				ImageViewUtil.displayImage(user.getAvatarUrl(), iv_person);
				images = new String[]{TextUtils.isEmpty(realUrl) ? url : realUrl};
			} else if (!TextUtils.isEmpty(realUrl)) {
				ImageViewUtil.displayImage(realUrl, iv_person);
				images = new String[]{realUrl};
			} else {
				isNull=true;
				iv_person.setImageResource(user.getNullAvatar());
			}
		}
		if (/*!user.isSelf() && */!TextUtils.isEmpty(realUrl)&&!isNull) {
			iv_person.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					showAvatar();
				}
			});
		}
		tv_name.setText(user.getName());
		if (user.getBabyBirthday() != 0) {
			tv_age.setText("宝宝"
					+ Person.calAgeText(user.getBabyBirthday(),false));
		}
		follow_and_follower.setVisibility(View.GONE);
		/*if (user.isSelf()) {
			follow_and_follower.setVisibility(View.VISIBLE);
			tv_follow.setText("关注  " + user.getFollowCount());
			tv_follower.setText("粉丝  " + user.getFollowerCount());
		} else {
			follow_and_follower.setVisibility(View.GONE);
		}*/
	}

	@Override
	public View initView() {
		view = LayoutInflater.from(context==null? MyBabyApp.getContext():context).inflate(R.layout.person_page_message,null);
		iv_person = (RoundImageViewByXfermode) view
				.findViewById(R.id.iv_person);
		tv_name = (TextView) view.findViewById(R.id.tv_name);
		tv_age = (TextView) view.findViewById(R.id.tv_age);
		tv_follow = (TextView) view.findViewById(R.id.tv_follow);
		tv_follower = (TextView) view.findViewById(R.id.tv_follower);
		follow_and_follower = (LinearLayout) view
				.findViewById(R.id.follow_and_follower);
		return view;
	}
	
	private void showAvatar() {
		if (images==null)
			return;
		CustomAbsClass.starImagePage(context, 1, 0, images);
	}

}
