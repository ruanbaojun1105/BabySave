package mybaby.models.community;

import java.io.Serializable;
import java.util.Map;

import mybaby.util.MapUtils;

public class UserPushSetting  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String settingKey;
	String settingName;
	boolean settingValue;
	
	public String getSettingKey() {
		return settingKey;
	}
	public void setSettingKey(String settingKey) {
		this.settingKey = settingKey;
	}
	public String getSettingName() {
		return settingName;
	}
	public void setSettingName(String settingName) {
		this.settingName = settingName;
	}
	public boolean isSettingValue() {
		return settingValue;
	}
	public void setSettingValue(boolean settingValue) {
		this.settingValue = settingValue;
	}

	public UserPushSetting() {
		super();
	}
	
	public static UserPushSetting[] createByArray(Object[] arr){
		if (arr==null)
			return null;
		UserPushSetting[] retArr=new UserPushSetting[arr.length];
		
       	for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = createByMap(map);
       	}
       	
		return retArr;
	}
	
	public static UserPushSetting createByMap(Map<?, ?> map){
		UserPushSetting obj=new UserPushSetting();
		
		obj.setSettingKey(MapUtils.getMapStr(map, "setting_key"));
		obj.setSettingName(MapUtils.getMapStr(map, "setting_name"));
		obj.setSettingValue(MapUtils.getMapBool(map, "setting_value"));

		return obj;
	}
}
