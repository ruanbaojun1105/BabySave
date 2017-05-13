package mybaby.ui.more;

import java.util.ArrayList;
import java.util.List;
import org.xmlrpc.android.XMLRPCException;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.umeng.analytics.MobclickAgent;
import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.models.Blog;
import mybaby.models.community.Place;
import mybaby.models.community.PlaceRepository;
import mybaby.models.community.UserPlaceSetting;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.UserRPC;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.more.adapter.NormalPlaceAdapter;
import mybaby.ui.more.adapter.TipPlaceAdapter;
import mybaby.ui.posts.edit.EditPostActivity;
import mybaby.ui.widget.OnRefreshListener;
import mybaby.ui.widget.RefreshListView;
import mybaby.util.ActionBarUtils;
import mybaby.util.MaterialDialogUtil;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

/**
 * 搜索地点的activity
 * 
 * @author Administrator
 */
public class PlaceSettingActivity extends Activity implements
		OnPoiSearchListener, OnGeocodeSearchListener, AMapLocationListener {

	private LocationManagerProxy mLocationManagerProxy;
	private RefreshListView lv_place_setting_normal;
	private ListView lv_place_setting_tip;
	private NormalPlaceAdapter normalPlaceAdapter;
	private TipPlaceAdapter tipPlaceAdapter;
	private UserPlaceSetting obj;
	private EditText et_search;
	private TextView tv_errortext;
	private ProgressBar pb_loading;
	private String address;
	private String name;
	private String adcode;
	private String currentCityCode = "";
	private String success_msg = "";

	private LatLonPoint mLatLonPoint;

	private String excludeRegex; // 特定地点正则表达式
	private String mapExcludeRegex; // 普通地点正则表达式
	private int nearbyDistance = Blog.getNearbyDistance();

	private boolean needSendBroadcast; // 判断是否需要发送广播
	private boolean needSavePlace; // 判断是否需要发送广播

	private String pType; // 地点查询的类型
	private String keyWord; // 地点查询的关键字

	private List<Place> places = new ArrayList<Place>();
	private List<Place> tipPlaces;
	private int pageNum = 0;
	private int tipPageNum = 0;
	private int tipPageCount;


	private static final int SHOWPLACENORMAL = 1;
	private static final int SHOWPLACETIP = 2;
	private static final int SEARCHPLACEBYNAME = 3;
	private static final int SETTING_OK = 4;

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.didianxuanze)); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.didianxuanze)); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
		MobclickAgent.onPause(this);
	}

	 
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

				case SETTING_OK:
					pb_loading.setVisibility(View.GONE);
					MaterialDialogUtil.DialogCommListener listener=new MaterialDialogUtil.DialogCommListener() {
						@Override
						public void todosomething() {
							if (needSendBroadcast) {
								Intent intent = new Intent();
								intent.setAction(Constants.BroadcastAction.BroadcastAction_PlaceSetting_IsSuccess);
								intent.putExtra("PlaceSettingIsSuccess", true);
								LocalBroadcastManager.getInstance(
										MyBabyApp.getContext()).sendBroadcast(intent);
							}
							LoadMoreListViewFragment.askRed();
							PlaceSettingActivity.this.finish();
						}
					};
					if (TextUtils.isEmpty(success_msg))
						listener.todosomething();
					else
						MaterialDialogUtil.showCommDialog(PlaceSettingActivity.this, null, success_msg, "确认", null, false, listener, null, listener);
					break;

			case SEARCHPLACEBYNAME:
				Place place = (Place) msg.obj;
				Intent intent = new Intent(PlaceSettingActivity.this,
						EditPostActivity.class);
				Bundle bundle = new Bundle();

				int objId = PlaceRepository.save(place);
				if (obj != null) {
					place.setObjId(objId);
					obj.setSettingValue(place);
					bundle.putSerializable("place", place);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					saveSettingPlace(obj);
				} else {
					bundle.putSerializable("place", place);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				}
				break;

			case SHOWPLACETIP:
				tv_errortext.setVisibility(View.GONE);
				pb_loading.setVisibility(View.GONE);
				lv_place_setting_tip.setVisibility(View.VISIBLE);
				lv_place_setting_normal.setVisibility(View.GONE);
				List<Tip> tips = (List<Tip>) msg.obj;
				showTips(tips);

				break;

			case SHOWPLACENORMAL:
				lv_place_setting_normal.setVisibility(View.VISIBLE);
				tv_errortext.setVisibility(View.GONE);
				pb_loading.setVisibility(View.GONE);
				lv_place_setting_tip.setVisibility(View.GONE);
				final List<Place> places = (List<Place>) msg.obj;
				if (normalPlaceAdapter == null) {
					normalPlaceAdapter = new NormalPlaceAdapter(
							PlaceSettingActivity.this, places);
					lv_place_setting_normal.setAdapter(normalPlaceAdapter);
				} else {
					normalPlaceAdapter.setPlaces(places);
					normalPlaceAdapter.notifyDataSetChanged();
				}
				lv_place_setting_normal.hideFooterView();
				pageNum = pageNum + 1;

				if (places.size()<12)
					poiSearchNear(mLatLonPoint, keyWord, pageNum);

				lv_place_setting_normal.setOnRefreshListener(
						pageNum < pageCount, new OnRefreshListener() {

							@Override
							public void onPullDownRefresh() {
							}

							@Override
							public void onLoadingMore() {
								poiSearchNear(mLatLonPoint, keyWord, pageNum);
							}
						});

				lv_place_setting_normal
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// 应为多了一个header和一个footer 所以要排除掉
								if (position < places.size() + 1) {
									setOnClickPlace(places, position - 1);
								}
							}

						});
				break;

			default:
				break;
			}
		}
	};
	private int pageCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_setting);
		getData();
		initView();
		if (mLatLonPoint != null && mLatLonPoint.getLatitude() != 0
				&& mLatLonPoint.getLongitude() != 0) {
			pageNum = 0;
			poiSearchNear(mLatLonPoint, keyWord, pageNum);
		} else {
			initLoacation();
		}
		ActionBarUtils.initActionBar("地点设置", PlaceSettingActivity.this);
		editTextChange();
	}


	@SuppressWarnings("static-access")
	private void getData() {
		if (getIntent().getExtras()==null)
			finish();
		pType = MyBabyApp.currentBlog.getMapPlaceTypes();
		keyWord = MyBabyApp.currentBlog.getMapPlaceKeywords();
		mapExcludeRegex = MyBabyApp.currentBlog.getMapExcludeRegex();
		obj = (UserPlaceSetting) getIntent().getExtras().get(
				"CommunityPlaceSettingItem");
		needSendBroadcast = getIntent().getExtras().getBoolean(
				"setBackBroadCastTag", false);
		needSavePlace = getIntent().getExtras().getBoolean(
				"needSavePlace", false);//是否需要保存地点信息到服务器
		success_msg=getIntent().getExtras().getString("success_msg","");
		if (obj != null) {
			excludeRegex = obj.getExcludeRegex();
		}
		Double latitude = getIntent().getExtras().getDouble("Latitude");
		Double longitude = getIntent().getExtras().getDouble("Longitude");
		mLatLonPoint = new LatLonPoint(latitude, longitude);
	}

	// 初始化View
	private void initView() {
		lv_place_setting_normal = (RefreshListView) findViewById(R.id.lv_place_setting_normal);
		lv_place_setting_normal.setNeedRefresh(false);
		et_search = (EditText) findViewById(R.id.et_search);
		et_search.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1,
					KeyEvent event) {
				return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
			}
		});
		lv_place_setting_tip = (ListView) findViewById(R.id.lv_place_setting_tip);
		tv_errortext = (TextView) findViewById(R.id.tv_errortext);
		pb_loading = (ProgressBar) findViewById(R.id.pb_loading);

	}

	// 正则表达式验证特定地点
	private boolean isExcludeRegex(String text) {
		return text.matches(excludeRegex);
	}

	// 正则表达式验证普通地点
	private boolean isMapExcludeRegex(String text) {
		return text.matches(mapExcludeRegex);
	}

	// 显示用户发动态等地点搜索结果
	protected void showTips(final List<Tip> tips) {
		// 如果搜索结果为null，则显示无找到界面
		if (tips == null || tips.size() == 0) {
			tv_errortext.setVisibility(View.VISIBLE);
			lv_place_setting_normal.setVisibility(View.GONE);
			pb_loading.setVisibility(View.GONE);
			lv_place_setting_tip.setVisibility(View.GONE);
		} else { // 否则为adapter设置数据
			if (tipPlaceAdapter == null) {
				tipPlaceAdapter = new TipPlaceAdapter(PlaceSettingActivity.this);
				tipPlaceAdapter.setTips(tips);
				lv_place_setting_tip.setAdapter(tipPlaceAdapter);
			} else {
				tipPlaceAdapter.setTips(tips);
				tipPlaceAdapter.notifyDataSetChanged();
			}
			lv_place_setting_tip
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							name = tips.get(position).getName();
							address = tips.get(position).getDistrict();
							adcode = tips.get(position).getAdcode();
							tipPlaces = new ArrayList<Place>();
							tipPageNum = 0;
							poiSearchByName(name, adcode);
						}
					});
		}
	}

	// 通过输入的提示查询地点
	protected void poiSearchByName(String address, String adcode) {
		GeocodeSearch geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		// name表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode
		GeocodeQuery query = new GeocodeQuery(address, adcode);
		geocoderSearch.getFromLocationNameAsyn(query);
	}

	// 编辑栏变化的监听
	private void editTextChange() {
		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 文本发生变化时

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// 文本发生变化之前

			}

			@Override
			public void afterTextChanged(Editable s) {
				// 处理，当界面未加载完成时，输入查询闪退的情况
				if (places != null && places.size() != 0) {
					searchPlace(s);
				} else {
					if (isAllSearch()) {
						searchPlace(s);
					} else {
						tv_errortext.setVisibility(View.VISIBLE);
						lv_place_setting_tip.setVisibility(View.GONE);
						lv_place_setting_normal.setVisibility(View.GONE);
					}
				}

			}
		});
	}

	// 搜索地点
	protected void searchPlace(Editable s) {
		String text = s.toString();
		if (text == null || "".equals(text.trim())) {
			pageNum = pageNum - 1;
			Message msg = Message.obtain();
			msg.what = SHOWPLACENORMAL;
			msg.obj = places;
			handler.sendMessageDelayed(msg, 1000);
		} else {
			Inputtips inputtips = new Inputtips(PlaceSettingActivity.this,
					new InputtipsListener() {

						@Override
						public void onGetInputtips(List<Tip> tips, int arg1) {
							Message msg = Message.obtain();
							msg.what = SHOWPLACETIP;
							msg.obj = tips;
							handler.sendMessage(msg);
						}
					});
			try {
				//第二个参数为城市的区号
				inputtips.requestInputtips(text, currentCityCode);
			} catch (AMapException e) {
				e.printStackTrace();
			}
		}

	}

	// 入口为发布动态时，所选择的地点
	private void setOnClickPlace(List<Place> places, int position) {

		Place place = places.get(position);

		Intent intent = new Intent(PlaceSettingActivity.this,
				EditPostActivity.class);
		Bundle bundle = new Bundle();

		int objId = PlaceRepository.save(place);
		if (obj != null) {
			place.setObjId(objId);
			obj.setSettingValue(place);
			bundle.putSerializable("place", place);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			if (needSavePlace) {
				saveSettingPlace(obj);
			}
			else {
				finish();
			}

		} else {
			bundle.putSerializable("place", place);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	// 保存UserPlaceSetting
	private void saveSettingPlace(UserPlaceSetting obj2) {
		new CustomAbsClass.doSomething(PlaceSettingActivity.this) {
			@Override
			public void todo() {
				pb_loading.setVisibility(View.VISIBLE);
			}
		};
		UserRPC.setPlaceSetting(obj2.getSettingKey(), obj.getSettingValue(),
				new BaseRPC.Callback() {

					@Override
					public void onSuccess() {
						handler.sendEmptyMessage(SETTING_OK);
					}

					@Override
					public void onFailure(long id, XMLRPCException error) {
						new CustomAbsClass.doSomething(PlaceSettingActivity.this) {
							@Override
							public void todo() {
								pb_loading.setVisibility(View.GONE);
								Toast.makeText(PlaceSettingActivity.this, "网络错误！请检查网络是否连接", Toast.LENGTH_SHORT).show();
								PlaceSettingActivity.this.finish();
							}
						};
					}
				});
	}

	/**
	 * 判断入口参数
	 * 
	 * @return true 表示为设置或者修改地点 false 表示为发布话题或者动态时显示的地点
	 */
	private boolean isAllSearch() {
		if (obj == null) {
			return true;
		} else {
			nearbyDistance = obj.getNearbyDistance();
			return false;
		}
	}

	// 将查询结果转化为Place
	private List<Place> fillPlace(List<PoiItem> poiItems, List<Place> places) {
		Place place = null;
		currentCityCode = poiItems.get(0).getCityCode();
		if (pageNum == 0 && obj == null) {
			if (poiItems.get(0).getCityName() != null
					&& !("".equals(poiItems.get(0).getCityName()))) {
				place = new Place();
				place.setLatitude(poiItems.get(0).getLatLonPoint()
						.getLatitude());
				place.setLongitude(poiItems.get(0).getLatLonPoint()
						.getLongitude());
				place.setCountry("中国");
				place.setAdcode(poiItems.get(0).getAdCode());
				place.setState(poiItems.get(0).getProvinceName());
				place.setCity(poiItems.get(0).getCityName());
				place.setDistrict(poiItems.get(0).getCityName());
				place.setAddress(poiItems.get(0).getCityName());
				place.setPlace_name(poiItems.get(0).getCityName());
				places.add(place);
			}
			if (poiItems.get(0).getAdName() != null
					&& !("".equals(poiItems.get(0).getAdName()))) {
				place = new Place();
				place.setLatitude(poiItems.get(0).getLatLonPoint()
						.getLatitude());
				place.setLongitude(poiItems.get(0).getLatLonPoint()
						.getLongitude());
				place.setCountry("中国");
				place.setState(poiItems.get(0).getProvinceName());
				place.setCity(poiItems.get(0).getCityName());
				place.setDistrict(poiItems.get(0).getAdName());
				place.setAdcode(poiItems.get(0).getAdCode());
				place.setAddress(poiItems.get(0).getAdName());
				place.setPlace_name(poiItems.get(0).getAdName());
				places.add(place);
			}
			if (poiItems.get(0).getSnippet() != null
					&& !("".equals(poiItems.get(0).getSnippet()))) {
				place = new Place();
				place.setLatitude(poiItems.get(0).getLatLonPoint()
						.getLatitude());
				place.setLongitude(poiItems.get(0).getLatLonPoint()
						.getLongitude());
				place.setCountry("中国");
				place.setState(poiItems.get(0).getProvinceName());
				place.setCity(poiItems.get(0).getCityName());
				place.setDistrict(poiItems.get(0).getAdName());
				place.setAdcode(poiItems.get(0).getAdCode());
				place.setAddress(poiItems.get(0).getSnippet());
				place.setPlace_name(poiItems.get(0).getSnippet());
				places.add(place);
			}
		}
		for (PoiItem poiItem : poiItems) {
			// 如果是从发动态入口进入 或者是正则表达式没有筛选掉的数据时，将地点加入列表中
			if ((isAllSearch() && !isMapExcludeRegex(poiItem.getTitle()))
					|| (!isAllSearch() && !isExcludeRegex(poiItem.getTitle()))) {
				place = new Place();
				place.setLatitude(poiItem.getLatLonPoint().getLatitude());
				place.setCountry("中国");
				place.setLongitude(poiItem.getLatLonPoint().getLongitude());
				place.setState(poiItem.getProvinceName());
				place.setCity(poiItem.getCityName());
				place.setDistrict(poiItem.getAdName());
				place.setAdcode(poiItem.getAdCode());
				place.setAddress(poiItem.getSnippet());
				place.setPlace_name(poiItem.getTitle());
				places.add(place);
			}
		}
		return places;
	}

	// 根据兴趣点搜索附近
	private void poiSearchNear(LatLonPoint lp, String keyWord, int pageNum) {
		if (!isAllSearch()) {
			keyWord = obj.getKeywords();
		}
		// 根据经纬度查询地点
		PoiSearch.Query query = new PoiSearch.Query(keyWord, pType, null);
		query.setPageSize(30);
		query.setPageNum(pageNum);
		PoiSearch poiSearch = new PoiSearch(PlaceSettingActivity.this, query);
		poiSearch.setOnPoiSearchListener(PlaceSettingActivity.this);// 设置数据返回的监听器
		poiSearch.setBound(new SearchBound(lp, nearbyDistance, true));// 设置周边搜索的中心点以及区域

		poiSearch.searchPOIAsyn();// 开始搜索
	}

	// setOnGeocodeSearchListener需要实现的回调方法
	// 查找搜索返回的回调结果（GEO搜索）
	@Override
	public void onGeocodeSearched(GeocodeResult result, int arg1) {
		Message msg = Message.obtain();
		Place place = new Place();
		place.setLatitude(result.getGeocodeAddressList().get(0)
				.getLatLonPoint().getLatitude());
		place.setLongitude(result.getGeocodeAddressList().get(0)
				.getLatLonPoint().getLongitude());
		place.setCountry("中国");
		place.setState(result.getGeocodeAddressList().get(0).getProvince());
		place.setCity(result.getGeocodeAddressList().get(0).getCity());
		place.setDistrict(result.getGeocodeAddressList().get(0).getDistrict());
		place.setAddress(address);
		place.setPlace_name(name);
		msg.what = SEARCHPLACEBYNAME;
		msg.obj = place;
		handler.sendMessage(msg);

	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {

	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {

	}

	// 兴趣点搜索后返回的回调结果（POI搜索）
	@Override
	public void onPoiSearched(PoiResult result, int code) {
		List<PoiItem> poiItems = result.getPois();
		Message msg = Message.obtain();
		// TODO逻辑在这里修改
		msg.what = SHOWPLACENORMAL;
		pageCount = result.getPageCount();
		places = fillPlace(poiItems, places);
		msg.obj = places;
		handler.sendMessage(msg);
	}

	// 初始化位置信息（定位）
	private void initLoacation() {
		new Thread() {
			public void run() {
				mLocationManagerProxy = LocationManagerProxy
						.getInstance(PlaceSettingActivity.this);
				mLocationManagerProxy.requestLocationData(
						LocationProviderProxy.AMapNetwork, -1, 15,
						PlaceSettingActivity.this);
				mLocationManagerProxy.setGpsEnable(false);
			}
		}.start();
	}

	// 定位之后返回的回调方法结果（经纬度信息）
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			// 获取当前位置信息
			Double geoLat = amapLocation.getLatitude();// 纬度
			Double geoLng = amapLocation.getLongitude();// 经度
			mLatLonPoint = new LatLonPoint(geoLat, geoLng);
			pageNum = 0;
			poiSearchNear(mLatLonPoint, keyWord, pageNum);
		}
	}

	@Override
	public void onLocationChanged(Location arg0) {
	}

	@Override
	public void onProviderDisabled(String arg0) {
	}

	@Override
	public void onProviderEnabled(String arg0) {
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

}
