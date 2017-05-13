package mybaby.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.widget.ImageView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.umeng.analytics.MobclickAgent;

import me.hibb.mybaby.android.R;
import mybaby.ui.community.adapter.GuidePagerAdapter;
import mybaby.ui.community.customclass.PageIndicator;

/**
 * 引导页
 */

public class GuideActivity extends Activity {

	private ViewPager pager;
	private ImageView trim_guide;
	private PageIndicator mIndicator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		trim_guide= (ImageView) findViewById(R.id.trim_guide);
		trim_guide.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				saveLookVersion();
				onBackPressed();
			}
		});

		saveLookVersion();
		initPager();
	}
	private void saveLookVersion(){
		SharedPreferences.Editor edit = MyBabyApp.getSharedPreferences().edit();
		edit.putInt("look_guide", MyBabyApp.version);
		edit.putLong("install_time", System.currentTimeMillis());
		edit.commit();
	}

	public void initPager(){
		final int[] dataArray={R.drawable.guide1,R.drawable.guide2,R.drawable.guide3,R.drawable.guide4,R.drawable.guide5};
		mIndicator = (PageIndicator) findViewById(R.id.indicator);
		pager= (ViewPager) findViewById(R.id.guide_pager);
		GuidePagerAdapter adapter=new GuidePagerAdapter(dataArray, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pager.setCurrentItem(pager.getCurrentItem() + 1);
			}
		},touchListener);
		pager.setAdapter(adapter);
		mIndicator.setViewPager(pager);
		/*pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				trim_guide.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});*/
		adapter.notifyDataSetChanged();
		pager.setCurrentItem(0);
		pager.setOffscreenPageLimit(3);
	}


	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("引导页"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("引导页"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
		MobclickAgent.onPause(this);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return false;
	}
	private float downx=0;
	private View.OnTouchListener touchListener=new View.OnTouchListener() {
		@Override
		public boolean onTouch(final View v, MotionEvent motionEvent) {
			if (pager.getCurrentItem() != 4) {
				v.setX(0);
				return false;
			}
			//LogUtils.e("x>>>"+motionEvent.getX()+"y>>>>>"+motionEvent.getY()+"pagerx:"+pager.getX()+"pagery:"+pager.getY());
			//LogUtils.e("view x>>>"+v.getX()+"view y>>>>>"+v.getY());
			switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					downx = motionEvent.getX();
					break;
				case MotionEvent.ACTION_MOVE: {
					float ordX = motionEvent.getX() - downx;
					if (ordX + v.getX() > 0) {
						v.setX(0);
						pager.requestDisallowInterceptTouchEvent(false);
					} else {
						pager.requestDisallowInterceptTouchEvent(true);
						//float x = motionEvent.getX() - downx ;// 是相对view的x，y
						//float y = motionEvent.getY() - v.getHeight() / 2;
						v.setX((ordX + v.getX()) * 13 / 10);
					}
					break;
				}
				//case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					if (v.getX() < 0) {
						if (Math.abs(v.getX()) > (MyBabyApp.screenWidth / 3)) {
							ObjectAnimator animator = ObjectAnimator.ofFloat(v, "translationX", v.getX(), -MyBabyApp.screenWidth);
							animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
								@Override
								public void onAnimationUpdate(ValueAnimator animation) {
									float currentValue = (float) animation.getAnimatedValue();
									if (currentValue == -MyBabyApp.screenWidth) {
										GuideActivity.this.finish();
									}
								}
							});
							animator.setDuration(800);
							animator.setInterpolator(new AnticipateInterpolator(4.0f));
							animator.start();
							v.setOnTouchListener(null);
						} else v.setX(0);
					} else v.setX(0);
					break;
			}
			return true;
		}
	};
}
