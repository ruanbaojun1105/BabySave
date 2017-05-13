package mybaby.util;

import android.app.Activity;
import android.content.Context;

import com.amap.api.location.core.CoordinateConvert;
import com.amap.api.location.core.GeoPoint;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import org.xmlrpc.android.XMLRPCException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mybaby.models.community.Place;
import mybaby.models.community.PlaceRepository;
import mybaby.rpc.community.PlaceRPC;
import mybaby.rpc.community.PlaceRPC.PlaceCallback;
import mybaby.ui.community.customclass.CustomAbsClass;

public class ImageAddressUtil {

	// TODO 逻辑错误
	Place place = null;

	/**
	 * 通过图片路径获取地点信息
	 * 
	 * @param fileName
	 *            图片路径
	 * @param context
	 *            上下文
	 * @param handler
	 * @return 当图片没有位置信息时返回null，有位置信息则返回place对象
	 */
	public void getLatLonPoint(String fileName, final Activity context,
			final ImageAddressListener imageAddressListener) {
		Double Latitude = null;
		Double Longitude = null;
		Metadata metadata;
		try {
			File file = new File(fileName);
			metadata = JpegMetadataReader.readMetadata(file);
			for (Directory directory : metadata.getDirectories()) {
				for (Tag tag : directory.getTags()) {
					String tagName = tag.getTagName();
					String desc = tag.getDescription();
					if (tagName.equals("GPS Latitude")) {
						// 纬度
						Latitude = Double.valueOf(pointToLatlong(desc));
					} else if (tagName.equals("GPS Longitude")) {
						// 经度
						Longitude = Double.valueOf(pointToLatlong(desc));
					}
				}
				/*for (String error : directory.getErrors()) {
					System.err.println("ERROR: " + error);
				}*/
			}
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (Latitude == null && Longitude == null) {
			imageAddressListener.callback(null);
		} else {
			final LatLng ll = getGeoLatLon(Latitude, Longitude);
			PlaceRPC.locate(ll.longitude, ll.latitude, new PlaceCallback() {

				@Override
				public void onSuccess(final Place place) {
					if (place == null) {
						searchLocationByPhone(ll, context, imageAddressListener);
					} else {
						ImageAddressUtil.this.place = place;
						new CustomAbsClass.doSomething(context) {
							@Override
							public void todo() {
								imageAddressListener.callback(place);
							}
						};
					}
				}

				@Override
				public void onFailure(long id, XMLRPCException error) {
					searchLocationByPhone(ll, context, imageAddressListener);
				}

			});

			// return place;
		}

	}

	/**
	 * 将获取的经纬度转化为String类型
	 * 
	 * @param point
	 *            获取的点
	 * @return 经纬度转化的、string
	 */
	public String pointToLatlong(String point) {
		Double du = Double.parseDouble(point.substring(0, point.indexOf("°"))
				.trim());
		Double fen = Double.parseDouble(point.substring(point.indexOf("°") + 1,
				point.indexOf("'")).trim());
		Double miao = Double.parseDouble(point.substring(
				point.indexOf("'") + 1, point.indexOf("\"")).trim());
		Double duStr = du + fen / 60 + miao / 60 / 60;
		return duStr.toString();
	}

	//
	/**
	 * 一个坐标转换 略微有点偏差 将地球坐标转化为火星坐标
	 * 
	 * @param latitude
	 *            地球坐标经度
	 * @param longitude
	 *            地球坐标纬度
	 * @return
	 */
	public LatLng getGeoLatLon(double latitude, double longitude) {
		GeoPoint pos = CoordinateConvert.fromGpsToAMap(latitude, longitude);
		LatLng lp = new LatLng(pos.getLatitudeE6() * 1.E-6,
				pos.getLongitudeE6() * 1.E-6);
		return lp;
	}

	//
	/**
	 * 通过图片的位置信息的经纬度搜索位置
	 * 
	 * @param llp
	 *            转化后的经纬度坐标
	 * @param context
	 *            上下文
	 * @return
	 */
	private void searchLocationByPhone(final LatLng llp, Context context,
			final ImageAddressListener imageAddressListener) {
		GeocodeSearch geocoderSearch = new GeocodeSearch(context);
		geocoderSearch
				.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {

					// 逆地理搜索的回调
					@Override
					public void onRegeocodeSearched(RegeocodeResult result,
							int arg1) {
						place = savePlace(result.getRegeocodeAddress(), llp);
						imageAddressListener.callback(place);
					}

					@Override
					public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

					}
				});
		// latLonPoint参数表示一个Latlng，第二参数表示范围多少米，GeocodeSearch.AMAP表示是国测局坐标系还是GPS原生坐标系
		RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(llp.latitude,
				llp.longitude), 200, GeocodeSearch.AMAP);
		geocoderSearch.getFromLocationAsyn(query);
	}

	// 保存地点信息
	protected Place savePlace(RegeocodeAddress address, LatLng llp) {
		Place place = new Place();
		place.setLatitude(llp.latitude);
		place.setLongitude(llp.longitude);
		place.setCountry("中国");
		place.setState(address.getProvince());
		place.setCity(address.getCity());
		place.setDistrict(address.getDistrict());
		place.setAddress(address.getFormatAddress());
		List<PoiItem> poiItems = new ArrayList<PoiItem>();
		String place_name = "";
		for(int i=0;i<address.getPois().size();i++){
			if(address.getPois().get(i).getDistance() < 100){
				poiItems.add(address.getPois().get(i));
			}
		}
		for(int i =0;i<poiItems.size();i++){
			if(poiItems.get(i).getTypeDes().contains("医疗保健服务") || poiItems.get(i).getTypeDes().contains("风景名胜")
					||poiItems.get(i).getTypeDes().contains("商务住宅") || poiItems.get(i).getTypeDes().contains("科教文化服务")){
				place_name = poiItems.get(i).getTitle();
				break;
			}
		}
		if("".equals(place_name)){
			if (address.getBuilding() != null && !"".equals(address.getBuilding())) {
				place_name = address.getBuilding();
			} else {
				place_name = address.getRoads().get(0).getName();
			}
		}
		place.setPlace_name(place_name);
		int objId = PlaceRepository.save(place);
		place.setObjId(objId);
		return place;
	}

	public interface ImageAddressListener {
		void callback(Place place);
	}

}
