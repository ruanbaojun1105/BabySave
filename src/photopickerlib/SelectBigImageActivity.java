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

package photopickerlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.cache.CacheDataTask;
import mybaby.cache.GenCaches;
import mybaby.ui.MyBabyApp;
import photopickerlib.adapters.PhotoPagerAdapter;
import photopickerlib.beans.Photo;

/**
 * 
 * @author baojun
 * 
 */

@Deprecated
public class SelectBigImageActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

	private Context context = null;
	private ViewPager pager = null;
	private int index = 0;
	private List<Photo> photoList=new ArrayList<>();
	private TextView count_number_tv;
	private PhotoPagerAdapter adapter;
	public static final String EXTRA_IMAGE = "SelectBigImageActivity:image";
	private static final String cachetype="sd_images";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.in_imageshow, R.anim.out_imageshow);
		setContentView(R.layout.image_activity);
		initData();
		initIntentParams();
		initView();
	}
	/**
	 * 初始化数据
	 */
	@SuppressLint("NewApi")
	private void initData() {
		// TODO Auto-generated method stub
		context = this;
		Bundle bundle = getIntent().getExtras();
		// Utils.Loge(bundle.getInt("count")+"---"+bundle.getInt("index"));
		if (bundle==null)
			return;
		index = bundle.getInt("index");
		photoList.clear();
		Object obj=CacheDataTask.getObj(this,cachetype);
		if (obj!=null){
			GenCaches genCaches= (GenCaches) obj;
			photoList= (List<Photo>) genCaches.getSerializableObj();
			adapter=new PhotoPagerAdapter(photoList);
		}else {
			index=0;
		}
	}
	/**
	 * 初始化选项参数
	 */
	private void initIntentParams() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_photo_bigimage);
		setSupportActionBar(toolbar);
		(findViewById(R.id.actionbar_back)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		count_number_tv=((TextView) findViewById(R.id.actionbar_title));
		Button mCommitBtn = (Button) findViewById(R.id.commit_btn);
		mCommitBtn.setVisibility(View.GONE);
		RelativeLayout layout= (RelativeLayout) findViewById(R.id.bottom_tab_bar);
		layout.setVisibility(View.GONE);
		count_number_tv= (TextView) findViewById(R.id.actionbar_title);
		count_number_tv.setText(index + 1 + "/" + photoList.size());
		pager = (ViewPager) findViewById(R.id.image_viewpager);
	}

	// 自动加载图片
	private void initView() {
		if (photoList==null)
			return;
		pager.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		pager.setCurrentItem(index);
		pager.setOffscreenPageLimit(5);
		pager.setOnPageChangeListener(this);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		count_number_tv.setText(position+1+"/"+photoList.size());
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		//关闭窗体动画显示
		this.overridePendingTransition(R.anim.in_imageshow,R.anim.out_imageshow);
	}

	public static void launch(Context context, View transitionView, List<Photo> mDatas,int position) {
		//ActivityOptionsCompat options =ActivityOptionsCompat.makeSceneTransitionAnimation(context, transitionView, EXTRA_IMAGE);
		GenCaches cache=new GenCaches(mDatas, String.valueOf(MyBabyApp.version),new Date());
		CacheDataTask.putCache(context, cache, cachetype, true);
		Intent intent = new Intent(context, SelectBigImageActivity.class);
		//intent.putExtra("image_list", (Serializable) mDatas);
		intent.putExtra("index", position);
		//ActivityCompat.startActivity(context, intent, options.toBundle());
		context.startActivity(intent);
		//((Activity)context).overridePendingTransition(R.anim.in_imageshow, R.anim.out_imageshow);
	}
}
