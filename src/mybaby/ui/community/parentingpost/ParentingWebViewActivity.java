package mybaby.ui.community.parentingpost;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.umeng.analytics.MobclickAgent;

import me.hibb.mybaby.android.R;
import mybaby.util.LogUtils;

/**
 * @date 15.9.2
 * @author baojun
 * 浏览网页
 */
public class ParentingWebViewActivity extends AppCompatActivity{

	private Toolbar toolbar;
	private boolean hasResume=false;
	private WebviewFragment webviewFragment;

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this); // 统计时长
		toolbar.postDelayed(new Runnable() {
			@Override
			public void run() {
				hasResume = true;
				LogUtils.e("已重置开关");
			}
		}, 2000);
	}

	public void onPause() {
		super.onPause();
		hasResume=false;
		MobclickAgent.onPause(this);
	}

	public WebviewFragment getWebviewFragment() {
		return webviewFragment;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		LogUtils.e("onDestroy!!!!!!!!!!!!!!!!!!");
		super.onDestroy();
	}

	@Override
	public void finish() {
		super.finish();
		//overridePendingTransition(R.anim.null_anim, R.anim.null_anim);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parentingpost_activity);
		toolbar= (Toolbar) findViewById(R.id.toolbar_webview);
		setSupportActionBar(toolbar);
		webviewFragment=new WebviewFragment(toolbar);
		webviewFragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction().replace(R.id.content_page,webviewFragment).commitAllowingStateLoss();
	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			LogUtils.e("touch keyback!!!!!!!!!!!!!!!!!!");
			if (hasResume) {
				if (webviewFragment != null) {
					if (webviewFragment.getWebView()!=null) {
						WebView webView=webviewFragment.getWebView();
						if (webView.canGoBack()) {
							webView.goBack();
						} else {
							webView.destroy();
							onBackPressed();
						}
					}
				}
				return true;
			}
		}

		return super.onKeyUp(keyCode, event);

	}

}
