package mybaby.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.share.UmengShare;
import mybaby.ui.MediaHelper;
import mybaby.ui.broadcast.UpdateRedTextReceiver;

/**
 * @date 15.9.2
 * @author baojun
 * 浏览网页
 */
public abstract class MyBabyBaseActivity extends AppCompatActivity implements
		MediaHelper.MediaHelperCallback,View.OnClickListener {

	protected Toolbar toolbar;
	protected MediaHelper mMediaHelper;
	protected UMSocialService shareController = UMServiceFactory.getUMSocialService(Constants.DESCRIPTOR);

	protected TextView right_tv,badge_red_tv,title_tv;
	protected ImageView right_img,actionbar_title_image;
	protected IconTextView back_tv;
	protected UpdateRedTextReceiver updateRedTextReceiver;
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getPageName()); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getPageName()); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
		MobclickAgent.onPause(this);
	}

	//页面名称
	public abstract String getPageName();
	public abstract String getTopTitle();
	public abstract String getRightText();
	public abstract void onMediaFileDoneOver();
	public abstract int getContentViewId();
	public abstract int getToolbarId();
	public abstract int getRightImgId();
	public abstract void initData();
	public abstract void initView();
	public abstract boolean textToolbarType();
	public abstract boolean imageToolbarType();
	public abstract View.OnClickListener getRight_click();

	public void setToolBar(String right_text,TextView title_tv,TextView right_tv){
		if (!TextUtils.isEmpty(right_text))
			right_tv.setText(right_text);
		right_tv.setVisibility(View.VISIBLE);
		if (getRight_click()!=null)
			right_tv.setOnClickListener(getRight_click());
	}

	public void setToolBar(int res_id,TextView title_tv,ImageView right_img){
		if (res_id!=0)
			right_img.setImageResource(res_id);
		right_img.setVisibility(View.VISIBLE);
		if (getRight_click()!=null)
			right_img.setOnClickListener(getRight_click());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(getContentViewId());
		mMediaHelper = new MediaHelper(this, this);
		new UmengShare().configPlatforms(shareController, this);
		initData();
		toolbar= (Toolbar) findViewById(getToolbarId());
		toolbar.addView(LayoutInflater.from(this).inflate(R.layout.actionbar_base_toolbar, null));
		title_tv = (TextView) toolbar.findViewById(R.id.actionbar_title);
		right_tv = (TextView) toolbar.findViewById(R.id.right_btn);
		badge_red_tv = (TextView) toolbar.findViewById(R.id.actionbar_back_badge);
		right_img = (ImageView) toolbar.findViewById(R.id.right_image);
		actionbar_title_image = (ImageView) toolbar.findViewById(R.id.actionbar_title_image);
		back_tv = (IconTextView) toolbar.findViewById(R.id.actionbar_back);
		back_tv.setOnClickListener(this);
		updateRedTextReceiver=new UpdateRedTextReceiver(badge_red_tv);
		updateRedTextReceiver.regiest();
		if (!TextUtils.isEmpty(getTopTitle()))
			title_tv.setText(getTopTitle());
		if (textToolbarType())
			setToolBar(getRightText(),title_tv,right_tv);
		else {
			if (imageToolbarType())
				setToolBar( getRightImgId(), title_tv, right_img);
		}
		setSupportActionBar(toolbar);
		initView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		UMSsoHandler ssoHandler = shareController.getConfig().getSsoHandler(requestCode) ;
		if(ssoHandler != null){
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		mMediaHelper.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onMediaFileDone(String[] mediaFilePaths) {
		// TODO Auto-generated method stub
		onMediaFileDoneOver();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.actionbar_back:
				onBackPressed();
				break;
			/*case R.id.right_btn:
				break;
			case R.id.right_image:
				break;*/
		}
	}
}
