package mybaby.models.community.item;

import java.io.Serializable;
import java.util.Map;

import mybaby.ui.community.holder.BigImageLinkItem;
import mybaby.util.MapUtils;

/**
 * bj
 */
public class CommunityBigImageLinkItem extends BigImageLinkItem implements Serializable{
	private static final long serialVersionUID = 1L;

	private String desc;
	private String image_url;
	private String action;
	private String title;
	public CommunityBigImageLinkItem(Map<?, ?> mapItem){
		super();
		this.setDesc(MapUtils.getMapStr(mapItem, "desc"));
		this.setImage_url(MapUtils.getMapStr(mapItem, "image_url"));
		this.setAction(MapUtils.getMapStr(mapItem, "action"));
		this.setTitle(MapUtils.getMapStr(mapItem, "title"));
	}
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public CommunityBigImageLinkItem() {
	}


}
