/**
 * Program  : T1Activity.java
 * Author   : qianj
 * Create   : 2012-5-31 下午4:24:32
 *
 * Copyright 2012 by newyulong Technologies Ltd.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of newyulong Technologies Ltd.("Confidential Information").  
 * You shall not disclose such Confidential Information and shall 
 * use it only in accordance with the terms of the license agreement 
 * you entered into with newyulong Technologies Ltd.
 *
 */

package mybaby.ui.community;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.R;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import mybaby.cache.CacheDataTask;
import mybaby.cache.GenCaches;
import mybaby.models.diary.Media;
import mybaby.models.diary.MediaRepository;
import mybaby.ui.MyBabyApp;
import mybaby.ui.broadcast.UpdateRedTextReceiver;
import mybaby.ui.community.adapter.MyFragmentPagerAdapter;
import mybaby.ui.community.customclass.CusViewPager;
import mybaby.ui.community.customclass.PageIndicator;
import mybaby.ui.community.fragment.ImageFragment;
import mybaby.util.DialogShow;
import mybaby.util.ImageViewUtil;
import photopickerlib.beans.Photo;

/**
 * 
 * @author baojun
 * 
 */

public class ImagePageActivity extends SwipeBackActivity implements OnClickListener ,OnPageChangeListener{

	private Context context = null;
	private CusViewPager pager = null;
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
	private int count = 0;
	private int index = 0;
	private List<String> images = new ArrayList<>();
	private List<Integer> mediaIds = new ArrayList<>();
	private Media[] mMediaFiles= null;
	private boolean isEdit = false;
	private boolean hasActionbar = false;
	protected String deleteMediaFileUrl;
	private PageIndicator mIndicator;
	private TextView title_tv;
	private MyFragmentPagerAdapter adapter;
	private boolean isFilePath = false;
	private SwipeBackLayout mSwipeBackLayout;
	public static final String IMAGE_CACHE_TYPE_KEY="image_cache_type";
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(isEdit ? getString(R.string.zhaopianliulan)
				: getString(R.string.zhaopianweihu)); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(isEdit ? getString(R.string.zhaopianliulan)
				: getString(R.string.zhaopianweihu)); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
														// onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
		MobclickAgent.onPause(this);
	}

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.in_imageshow, R.anim.out_imageshow);
		setContentView(R.layout.imagepage);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		initBackLayout();
		initData();
		new LoadDataAsyn().execute();
	}
	private void initBackLayout(){
		mSwipeBackLayout = getSwipeBackLayout();
		/*
		SwipeBackLayout.EDGE_RIGHT
		edgeFlag = SwipeBackLayout.EDGE_LEFT;
		edgeFlag = SwipeBackLayout.EDGE_BOTTOM;
		edgeFlag = SwipeBackLayout.EDGE_ALL;*/
		mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
	}
	/**
	 * 初始化数据
	 */
	@NonNull
	@SuppressLint("NewApi")
	private void initData() {
		// TODO Auto-generated method stub
		context = this;
		Bundle bundle = getIntent().getExtras();
		// Utils.Loge(bundle.getInt("count")+"---"+bundle.getInt("index"));
		if (bundle==null)
			return;
		count = bundle.getInt("count");
		index = bundle.getInt("index");
		images.clear();
		if (bundle.containsKey(IMAGE_CACHE_TYPE_KEY)){
			Object obj= CacheDataTask.getObj(this, IMAGE_CACHE_TYPE_KEY);
			if (obj!=null){
				GenCaches genCaches= (GenCaches) obj;
				List<Photo> photos= (List<Photo>) genCaches.getSerializableObj();
				for (Photo photo:photos){
					images.add(photo.getPath());
				}
			}
		}else if (bundle.containsKey("media")){
			mediaIds.clear();
			List<Integer> filterList=bundle.getIntegerArrayList("filterList");
			Media mf= (Media) bundle.getSerializable("media");
			if (mf==null)
				return;
			if (mf.getPid() <= 0) {
				mMediaFiles = MediaRepository.getForParentId(mf.getParentId());
			} else {
				mMediaFiles = MediaRepository.getForPId(mf.getPid());
			}
			if (mMediaFiles==null)
				return;
			if (mMediaFiles.length==0)
				return;
			count=mMediaFiles.length;
			for (int i = 0; i < mMediaFiles.length; i++) {
				boolean filter=false;
				if (filterList!=null) {
					for (int id : filterList) {
						if (mMediaFiles[i].getId() == id) {
							filter = true;
							break;
						}
					}
				}
				if (filter)
					continue;
				float whRatio=mMediaFiles[i].imageWHRatio();//图片高宽比例
				int height;
				if(whRatio>0){
					height=Math.min((int) (MyBabyApp.screenWidth/whRatio),MyBabyApp.screenHeight);
				}else{
					height=MyBabyApp.screenHeight;
				}
				if ( mf.getId()> 0&&index==0)
					if (mMediaFiles[i].getId() == mf.getId())
						index = i;

				String fileUrl = ImageViewUtil.getFileUrl(mMediaFiles[i], MyBabyApp.screenWidth, height);
				images.add(fileUrl);
				mediaIds.add(mMediaFiles[i].getId());
			}
		}else {
			count = bundle.getInt("count");
			index = bundle.getInt("index");
			String[] ims = bundle.getStringArray("images");
			for (String url:ims){
				images.add(url);
			}
		}
		// 判断是否需要编辑
		isEdit = bundle.getBoolean("isEdit", false);
		hasActionbar = bundle.getBoolean("hasActionbar", false);
		isFilePath = bundle.getBoolean("isFilePath", false);
		View view = LayoutInflater.from(this).inflate(R.layout.actionbar_friend,
				null);
		new UpdateRedTextReceiver((TextView) view.findViewById(R.id.actionbar_back_badge)).regiest();
		ImageView imageView = (ImageView) view.findViewById(R.id.addPicture);
		imageView.setImageDrawable(
				getResources().getDrawable(R.drawable.ic_action_discard));
		imageView.setOnClickListener(this);
		imageView.setVisibility(isEdit?View.VISIBLE:View.GONE);
		view.findViewById(R.id.actionbar_back).setOnClickListener(this);
		((TextView) view.findViewById(R.id.actionbar_back))
				.setTypeface(MyBabyApp.fontAwesome);
		title_tv=((TextView) view.findViewById(R.id.actionbar_title));
		title_tv.setText("返回");
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setCustomView(view);
		if (hasActionbar) {
			getActionBar().show();
			// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			if (isEdit) {
				getActionBar().show();
			} else {
				getActionBar().hide();
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
		}
		pager = (CusViewPager) findViewById(R.id.viewPager);
		mIndicator = (PageIndicator) findViewById(R.id.indicator);
	}


	// 自动加载图片
	private void initView() throws Exception{
		if (images == null ||context==null)
			return;
		try {
			int i=images.size();
			if (!(index + 1 >i))
				title_tv.setText(index + 1 + "/" + i);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (String imagepath:images) {
			ImageFragment fragment=new ImageFragment();
			Bundle bundle=new Bundle();
			bundle.putString(ImageFragment.IMAGE,imagepath);
			bundle.putBoolean(ImageFragment.ISFILE, isFilePath);
			fragment.setArguments(bundle);
			fragmentList.add(fragment);
		}
		String[] arr=new String[images.size()];
		for (int i = 0; i < images.size(); i++) {
			arr[i]=images.get(i);
		}
		adapter=new MyFragmentPagerAdapter(getSupportFragmentManager(),arr,fragmentList);
		pager.setAdapter(adapter);

		if (images.size() == 1) {
			((View) mIndicator).setVisibility(View.GONE);
		}
		adapter.notifyDataSetChanged();
		pager.setCurrentItem(index);

		if (images.size()<20) {
			mIndicator.notifyDataSetChanged();
			mIndicator.setViewPager(pager);
			mIndicator.setCurrentItem(index);
		}
		pager.setOffscreenPageLimit(3);
		pager.setOnPageChangeListener(ImagePageActivity.this);

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		//LogUtils.e(position + ">>>>positionOffset=" + positionOffset + ">>>>>positionOffsetPixels=" + positionOffsetPixels);
	}

	@Override
	public void onPageSelected(int position) {
		try {
			title_tv.setText(position + 1 + "/" + images.size());
			if (images.size()<20) {
				mIndicator.setCurrentItem(position);
				mIndicator.notifyDataSetChanged();
			}
			//((ImageFragment)fragmentList.get(position)).getImageView().reset();
			((ImageFragment)fragmentList.get(position)).getImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	protected class LoadDataAsyn extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		// 完成
		@Override
		protected void onPostExecute(String[] result) {
			// TODO Auto-generated method stub
			try {
				initView();
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}

	}
	private void deleteMedia() {
		new DialogShow.DisLikeDialog(context,
				"去掉这张照片吗？", "确定", "取消",
				new DialogShow.DoSomeThingListener() {

					@Override
					public void todo() {
						// TODO Auto-generated
						int index=pager.getCurrentItem();
						Intent i = new Intent();
						if (mMediaFiles==null){
							i.putExtra("deleteMediaFileUrl", images.get(index));
						}else {
							i.putExtra("deleteMediaFileId", mediaIds.get(index));
						}
						setResult(RESULT_OK, i);
						finish();;
					}
				}, null);
	}
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		//关闭窗体动画显示
		this.overridePendingTransition(R.anim.in_imageshow,R.anim.out_imageshow);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.actionbar_back:
			onBackPressed();
			break;

		case R.id.addPicture:
			deleteMedia();
			break;
		}
	}


}
