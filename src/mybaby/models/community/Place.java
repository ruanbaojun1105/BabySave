package mybaby.models.community;

import android.database.Cursor;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import mybaby.util.MapUtils;

public class Place  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int objId=0;
	private int placeId=0;
	private Double latitude=0.0;
	private Double longitude=0.0;
	private String country="";
	private String state="";
	private String city="";
	private String district="";
	private String address="";
	private String place_name="";
	private String adcode = "";

	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public int getPlaceId() {
		return placeId;
	}
	public void setPlaceId(int placeId) {
		this.placeId = placeId;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPlace_name() {
		return place_name;
	}
	public void setPlace_name(String place_name) {
		this.place_name = place_name;
	}
	public String getAdcode() {
		return adcode;
	}
	public void setAdcode(String adcode) {
		this.adcode = adcode;
	}
	public Place() {
		super();
	}
	
    public Place(Cursor c){
    	this.setObjId(c.getInt(0));
		this.setPlaceId(c.getInt(1));
		this.setLatitude(c.getDouble(2));
		this.setLongitude(c.getDouble(3));
		this.setCountry(c.getString(4));
		this.setState(c.getString(5));
		this.setCity(c.getString(6));
		this.setDistrict(c.getString(7));
		this.setAddress(c.getString(8));
		this.setPlace_name(c.getString(9));
		this.setAdcode(c.getString(10));
    }
	
	
	//需要判空，否则无法序列化
	public Map<String, Object> getMap(){
		Map<String, Object> map=new HashMap<String, Object>();
		
		map.put("post_id", this.getPlaceId());
		if(this.getLongitude() != 0){
			map.put("longitude", this.getLongitude());
		}
		if(this.getLatitude() != 0){
			map.put("latitude", this.getLatitude());
		}
		if (!this.getPlace_name().isEmpty()) {
			map.put("place_name", this.getPlace_name());
		}
		if (!TextUtils.isEmpty(this.getCountry())) {
			map.put("country", this.getCountry());
		}
		if (!TextUtils.isEmpty(this.getState())) {
			map.put("state", this.getState());
		}
		if (!TextUtils.isEmpty(this.getCity())) {
			map.put("city", this.getCity());
		}
		if (!TextUtils.isEmpty(this.getDistrict())) {
			map.put("district", this.getDistrict());
		}
		if (!TextUtils.isEmpty(this.getAddress())) {
			map.put("address", this.getAddress());
		}
		if(!TextUtils.isEmpty(this.getAdcode())){
			map.put("adcode", this.getAdcode());
		}
		
		
		
		
		
		
		
		return map;
	}
	
	public static Place[] createByArray(Object[] arr){
		if (arr==null)
			return null;
		Place[] retArr=new Place[arr.length];
		
       	for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            try {
				retArr[i] = createByMap(map);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       	}
		return retArr;
	}
	
	public static Place createByMap(Map<?, ?> map) {
		int placeId=MapUtils.getMapInt(map, "post_id",0);
		
		Place obj=PlaceRepository.loadByPlaceId(placeId);
		//Place obj=null;
		if(obj == null){
			obj=new Place();
		}
		
		obj.setPlaceId(placeId);
		if(map.containsKey("latitude"))
			obj.setLatitude(MapUtils.getMapDouble(map, "latitude",0.0));
		if(map.containsKey("longitude"))
			obj.setLongitude(MapUtils.getMapDouble(map, "longitude",0.0));
		if(map.containsKey("country"))
			obj.setCountry(MapUtils.getMapStr(map, "country"));
		if(map.containsKey("state"))
			obj.setState(MapUtils.getMapStr(map, "state"));
		if(map.containsKey("city"))
			obj.setCity(MapUtils.getMapStr(map, "city"));
		if(map.containsKey("district"))
			obj.setDistrict(MapUtils.getMapStr(map, "district"));
		if(map.containsKey("address"))
			obj.setAddress(MapUtils.getMapStr(map, "address"));
		if(map.containsKey("place_name"))
			obj.setPlace_name(MapUtils.getMapStr(map, "place_name"));
		if(map.containsKey("adcode"))
			obj.setAdcode(MapUtils.getMapStr(map, "adcode"));
		PlaceRepository.save(obj);
		
		return obj;
	}
	

}
