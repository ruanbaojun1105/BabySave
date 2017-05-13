package mybaby.models.community.item;

import java.io.Serializable;
import java.util.Map;

import mybaby.ui.community.holder.ShareAppItem;
import mybaby.util.MapUtils;

public class CommunityShareAppItem extends ShareAppItem implements Serializable{
	private static final long serialVersionUID = 1L;

	String title;
	String desc;

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

	public CommunityShareAppItem() {
	}

	public CommunityShareAppItem(Map<?, ?> mapItem ){
		super();
		this.setTitle(MapUtils.getMapStr(mapItem, "title"));
		this.setDesc(MapUtils.getMapStr(mapItem, "description"));
	}
	
}
