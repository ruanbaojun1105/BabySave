package mybaby.ui.more.holder;

/**
 * 个人主页  底部Button布局
 */
import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import me.hibb.mybaby.android.R;
import mybaby.ui.more.BaseHolder;
import mybaby.util.AppUIUtils;

public class PersonButtomHolder extends BaseHolder<Boolean>{
	
	private TextView bt_follow;
	private String data;
	private Activity activity;
	
	
	@Override
	public void refreshView() {
		boolean hasFollow = getData();
		if(hasFollow){
			bt_follow.setText("已关注");
		}else{
			bt_follow.setText("+关注");
		}
	}

	@Override
	public View initView() {
		View view = AppUIUtils.inflate(R.layout.person_page_buttom);
		bt_follow = (TextView) view.findViewById(R.id.bt_follow);
		return view;
	}
	
}
