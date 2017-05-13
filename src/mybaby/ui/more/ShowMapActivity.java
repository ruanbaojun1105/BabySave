package mybaby.ui.more;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.CancelableCallback;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.umeng.analytics.MobclickAgent;

import me.hibb.mybaby.android.R;
import mybaby.models.Blog;
import mybaby.models.community.Place;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.customclass.CustomAbsClass.InitPlaceById;
import mybaby.util.ActionBarUtils;
import mybaby.util.Utils;

public class ShowMapActivity extends Activity implements CancelableCallback{

	private static final int SCROLL_BY_PX = 100;
	private MapView mapView;
	private AMap aMap;
	private Place place;
	private int placeId;
	private Marker locationMarker; // 选择的点
	private Activity activity;
	private int distance = Blog.getNearbyDistance();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showmap);
		activity=this;
		place = (Place) getIntent().getExtras().getSerializable("place");
		placeId=getIntent().getExtras().getInt("placeId", 0);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		initActionBar();
		initData();
	}

	 
	private void initActionBar() {
		ActionBarUtils.initActionBar("地图", ShowMapActivity.this);
	}

	/**
	 * 初始化AMap对象
	 */
	@SuppressLint("ShowToast") 
	private void initData() {
		
		//如果没有经纬度则从服务器获取
		if (place!=null&&place.getLatitude()!=0&&place.getLongitude()!=0) {
			initView();
		}else {
			if (place==null&& placeId==0) {
				Toast.makeText(activity, "没有获取到该地点信息", Toast.LENGTH_SHORT);
				onBackPressed();
			}else {
				if (place!=null)
					placeId=place.getPlaceId();//20160114bug fixed
				if (placeId!=0) {
					new InitPlaceById(placeId) {
						
						@Override
						public void succBackPlace(final Place place) {
							// TODO Auto-generated method stub
							ShowMapActivity.this.place=place;
							new CustomAbsClass.doSomething(activity) {
								
								@Override
								public void todo() {
									// TODO Auto-generated method stub
									initView();
								}
							};
						}
						
						@Override
						public void onError() {
							// TODO Auto-generated method stub
							new CustomAbsClass.doSomething(ShowMapActivity.this) {
								@Override
								public void todo() {
									Toast.makeText(activity, "没有获取到该地点信息", Toast.LENGTH_SHORT);
									onBackPressed();
								}
							};
						}
					};
				}
			}
		}
		
	}
	
	@SuppressLint("ResourceAsColor")
	private void initView() {
		// TODO Auto-generated method stub
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		Utils.Loge("lat---"+place.getLatitude() + "-----lng---"+place.getLongitude());
//		aMap.animateCamera(CameraUpdateFactory
//				.newCameraPosition(new CameraPosition(new LatLng(place
//						.getLatitude(), place.getLongitude()), 18, 0, 30)),
//				distance, this);
		
		aMap.moveCamera(CameraUpdateFactory
				.newCameraPosition(new CameraPosition(new LatLng(place
				.getLatitude(), place.getLongitude()), 18, 0, 30)));//地图从当前位置开始
		
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
       mapView.setVisibility(View.VISIBLE);
		
		locationMarker = aMap.addMarker(new MarkerOptions()
				.anchor(0.5f, 1)
				.position(
						new LatLng(place.getLatitude(), place
								.getLongitude()))
				.title(place.getPlace_name()));
		
		
		locationMarker.showInfoWindow();
		
		
		
		
	}
	
	
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("地图"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("地图"); 
		MobclickAgent.onPause(this);
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		activity=null;
	}
	


	@Override
	public void onCancel() {
		
	}

	@Override
	public void onFinish() {
		
	}
}
