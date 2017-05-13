//�ο��� http://blog.csdn.net/xiaanming/article/details/8966621

package mybaby.ui.posts.gallery;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.util.Arrays;

import me.hibb.mybaby.android.R;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import mybaby.ui.MyBabyApp;
import mybaby.models.diary.Media;
import mybaby.models.diary.MediaRepository;
import mybaby.ui.community.adapter.MyPageAdapter;
import mybaby.ui.community.customclass.PageIndicator;
import mybaby.ui.community.customclass.PhotoView;
import mybaby.util.DialogShow;
import mybaby.util.ImageViewUtil;
import mybaby.util.Utils;

@Deprecated
public class PostGallery extends SwipeBackActivity implements OnPageChangeListener {
	private ViewPager mViewPager;
	private Media[] mMediaFiles;
	private boolean mIsEditStatus;
	private PageIndicator mIndicator;
	private int mCurrentMediaFileId;
	private View[] mImageViews;
	private SwipeBackLayout mSwipeBackLayout;

	private int mDefaultItem = 0;

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(mIsEditStatus ? "日记照片维护" : "主页照片查看"); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(mIsEditStatus ? "日记照片维护" : "主页照片查看");
		MobclickAgent.onResume(this); // 统计时长
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.imagepage);

		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		mSwipeBackLayout = getSwipeBackLayout();
		mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);

		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mIndicator = (PageIndicator) findViewById(R.id.indicator);
		Intent intent = getIntent();
		int pId = intent.getIntExtra("pId", 0);
		Utils.Loge("图片pId:" + pId);
		if (pId <= 0) {
			mMediaFiles = MediaRepository
					.getForParentId(intent.getExtras().getInt("ParentId"));
		} else {
			mMediaFiles = MediaRepository.getForPId(pId);
		}
		mIsEditStatus = intent.getBooleanExtra("isEditStatus", false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setTitle(R.string.photos);
		if (!mIsEditStatus) {
			getActionBar().hide();
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		int mediaFileId = getIntent().getIntExtra("mediaFileId", 0);
		if (mediaFileId > 0) {
			for (int i = 0; i < mMediaFiles.length; i++) {
				if (mMediaFiles[i].getId() == mediaFileId) {
					mDefaultItem = i;
					break;
				}
			}
		} else {
			mDefaultItem = getIntent().getIntExtra("mediaIndex", 0);
		}
		mImageViews = new View[mMediaFiles.length];
		for (int i = 0; i < mImageViews.length; i++) {
			loadImageView(i);
		}
		if (mImageViews.length == 1) {
			((View) mIndicator).setVisibility(View.GONE);
		}
		// Adapter
		mViewPager.setAdapter(new MyPageAdapter(Arrays.asList(mImageViews)));
		mViewPager.setCurrentItem(mDefaultItem);
		mIndicator.setViewPager(mViewPager);
		mIndicator.setOnPageChangeListener(this);
		//mViewPager.setOnPageChangeListener(this);
		mIndicator.setViewPager(mViewPager, mDefaultItem);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (mIsEditStatus) {
			android.view.MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.post_gallery, menu);
		}
		return true;
	}

	// Menu actions
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.menu_post_gallery_delete:
			deleteMedia();
			return true;
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void loadImageView(final int index) {

		RelativeLayout view = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.imagepage_item, null);
		//LinearLayout layout= (LinearLayout) view.findViewById(R.id.image_lin);
		final PhotoView imageView = (PhotoView) view.findViewById(R.id.image_id);
		final ProgressBar progressBar = (ProgressBar) view
				.findViewById(R.id.progress);
		imageView.enable();
		progressBar.bringToFront();
		imageView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		float whRatio=mMediaFiles[index].imageWHRatio();//图片高宽比例
		int height;
        if(whRatio>0){
        	height=Math.min((int) (MyBabyApp.screenWidth/whRatio),MyBabyApp.screenHeight);
        }else{
        	height=MyBabyApp.screenHeight;
        }
		final String fileUrl = ImageViewUtil.getFileUrl(mMediaFiles[index],MyBabyApp.screenWidth,height);
		imageView.enable();
		ImageViewUtil.displayImage(fileUrl, imageView,
				new SimpleImageLoadingListener() {

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) { // TODO Auto-generated method
						if (loadedImage!=null) {
							imageView.setVisibility(View.VISIBLE);
							progressBar.setVisibility(View.GONE);
						}
					}
				}, false);

		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					onBackPressed();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		imageView.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				if (!TextUtils.isEmpty(fileUrl)) {
					DialogShow.savePictureDialog(PostGallery.this,ImageLoader.getInstance()
									.loadImageSync(fileUrl));
				}

				return true;
			}
		});

		mImageViews[index] = view;
	}

	private void deleteMedia() {
		new DialogShow.DisLikeDialog(PostGallery.this,
				"去掉这张照片吗？", "确定", "取消",
				new DialogShow.DoSomeThingListener() {

					@Override
					public void todo() {
						// TODO Auto-generated
						mCurrentMediaFileId = mMediaFiles[mViewPager.getCurrentItem()].getId();
						Intent i = new Intent();
						i.putExtra("deleteMediaFileId", mCurrentMediaFileId);
						setResult(RESULT_OK, i);
						finish();
					}
				}, null);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}
 
	@Override
	public void onPageSelected(int index) {
		try {
			mIndicator.setCurrentItem(index);
			mIndicator.notifyDataSetChanged();
			//((PhotoView) mImageViews[index].findViewById(R.id.image_id)).reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		//关闭窗体动画显示
		this.overridePendingTransition(R.anim.in_imageshow,R.anim.out_imageshow);
	}
}
