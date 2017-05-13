package mybaby.models.community;

import java.io.Serializable;
import java.util.Map;

import mybaby.util.MapUtils;


public class UserPlaceSetting  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String settingKey;
	String keywords;
	String settingName;
	Place settingValue;
	String excludeRegex;
	int nearbyDistance;

	public int getNearbyDistance() {
		return nearbyDistance;
	}
	public void setNearbyDistance(int nearbyDistance) {
		this.nearbyDistance = nearbyDistance;
	}
	public String getExcludeRegex() {
		return excludeRegex;
	}
	public void setExcludeRegex(String excludeRegex) {
		this.excludeRegex = excludeRegex;
	}
	public String getSettingKey() {
		return settingKey;
	}
	public void setSettingKey(String settingKey) {
		this.settingKey = settingKey;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getSettingName() {
		return settingName;
	}
	public void setSettingName(String settingName) {
		this.settingName = settingName;
	}
	public Place getSettingValue() {
		return settingValue;
	}
	public void setSettingValue(Place settingValue) {
		this.settingValue = settingValue;
	}

	public UserPlaceSetting() {
		super();
	}

	public UserPlaceSetting(String settingKey, String keywords, String settingName, Place settingValue, String excludeRegex, int nearbyDistance) {
		this.settingKey = settingKey;
		this.keywords = keywords;
		this.settingName = settingName;
		this.settingValue = settingValue;
		this.excludeRegex = excludeRegex;
		this.nearbyDistance = nearbyDistance;
	}

	public static UserPlaceSetting[] createByArray(Object[] arr){
		if (arr==null)
			return null;
		UserPlaceSetting[] retArr=new UserPlaceSetting[arr.length];
		
       	for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = createByMap(map);
       	}
       	
		return retArr;
	}
	
	public static UserPlaceSetting createByMap(Map<?, ?> map){
		UserPlaceSetting obj=new UserPlaceSetting();
		
		Map<?, ?> mapMeta = MapUtils.getMap(map,"setting_meta");
		Map<?, ?> mapValue = MapUtils.getMap(map,"setting_value");
		
		obj.setExcludeRegex(MapUtils.getMapStr(mapMeta, "meta_exclude_regex"));
		obj.setNearbyDistance((int)(MapUtils.getMapDouble(mapMeta, "meta_nearby_distance")));
		obj.setSettingKey(MapUtils.getMapStr(mapMeta, "meta_key"));
		obj.setKeywords(MapUtils.getMapStr(mapMeta, "meta_keywords"));
		obj.setSettingName(MapUtils.getMapStr(mapMeta, "meta_name"));
		
		
		if(mapValue != null)
			obj.setSettingValue(Place.createByMap(mapValue));

		return obj;
	}
	public static UserPlaceSetting createByMapOrder(Map<?, ?> map){
		UserPlaceSetting obj=new UserPlaceSetting();

		obj.setExcludeRegex(MapUtils.getMapStr(map, "meta_exclude_regex"));
		obj.setNearbyDistance((int)(MapUtils.getMapDouble(map, "meta_nearby_distance")));
		obj.setSettingKey(MapUtils.getMapStr(map, "meta_key"));
		obj.setKeywords(MapUtils.getMapStr(map, "meta_keywords"));
		obj.setSettingName(MapUtils.getMapStr(map, "meta_name"));
		return obj;
	}


	
}
