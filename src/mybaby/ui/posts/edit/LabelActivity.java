package mybaby.ui.posts.edit;

import me.hibb.mybaby.android.R;
import mybaby.models.community.PlaceRepository;
import mybaby.models.diary.Post;
import mybaby.ui.more.PlaceSettingActivity;
import mybaby.util.ActionBarUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class LabelActivity extends Activity {
	
	private TextView tv_private;
	private TextView tv_public;
	private TextView tv_friend;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_label);
		ininActionBar();
		initView();
	}
	
	private void initView() {
		tv_private = (TextView) findViewById(R.id.tv_private);
		tv_public = (TextView) findViewById(R.id.tv_public);
		tv_friend = (TextView) findViewById(R.id.tv_friend);
		tv_private.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				clickItemforResult(Post.privacyType.Private.ordinal());
			}
		});
		tv_public.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				clickItemforResult(Post.privacyType.Public.ordinal());
			}
		});
		tv_friend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				clickItemforResult(Post.privacyType.Friends.ordinal());
			}
		});
	}

	private void ininActionBar() {
		ActionBarUtils.initActionBar("谁可以看", LabelActivity.this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	private void clickItemforResult(int privacyType){
		Intent intent = new Intent(LabelActivity.this,
				EditPostActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("privacyType", privacyType);
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		finish();
	}

	
}
