package mybaby.models.community.item;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.UserPlaceSetting;
import mybaby.ui.community.holder.PlaceSettingItem;
import mybaby.util.MapUtils;

public class CommunityPlaceSettingItem extends PlaceSettingItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	UserPlaceSetting setting;
	String title;
	String desc;
	String note;

	public UserPlaceSetting getSetting() {
		return setting;
	}
	public void setSetting(UserPlaceSetting setting) {
		this.setting = setting;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

	public CommunityPlaceSettingItem(UserPlaceSetting setting, String title, String desc, String note) {
		this.setting = setting;
		this.title = title;
		this.desc = desc;
		this.note = note;
	}

	public CommunityPlaceSettingItem() {
	}

	public CommunityPlaceSettingItem(Map<?, ?> mapItem ){
		super();
		this.setSetting(UserPlaceSetting.createByMap(mapItem));
		this.setTitle(MapUtils.getMapStr(mapItem, "setting_title"));
		this.setDesc(MapUtils.getMapStr(mapItem, "setting_description"));
		this.setNote(MapUtils.getMapStr(mapItem, "setting_note"));
	}
}
