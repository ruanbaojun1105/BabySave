package mybaby.models.community.item;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.Icon;
import mybaby.models.community.Link;
import mybaby.ui.community.holder.BigLinkItem;
import mybaby.util.MapUtils;

public class CommunityBigLinkItem extends BigLinkItem implements Serializable {

	public CommunityBigLinkItem() {
	}

	private static final long serialVersionUID = 1L;

	private String content;
	private Icon icon;
	private Link link;


	public static CommunityBigLinkItem creatByMap(Map<?, ?> mapItem){
		if (null==mapItem)
			return null;
		CommunityBigLinkItem item=new CommunityBigLinkItem();
		item.setContent(MapUtils.getMapStr(mapItem, "content"));
		item.setIcon(Icon.creatByMap(MapUtils.getMap(mapItem, "icon")));
		item.setLink(Link.createByMap(MapUtils.getMap(mapItem, "link")));
		return item;
	}

	public CommunityBigLinkItem(Map<?, ?> mapItem ){
		super();
		this.setContent(MapUtils.getMapStr(mapItem, "content"));
		this.setIcon(Icon.creatByMap(MapUtils.getMap(mapItem, "icon")));
		this.setLink(Link.createByMap(MapUtils.getMap(mapItem, "link")));
	}

	public CommunityBigLinkItem(String content, Icon icon, Link link) {
		this.content = content;
		this.icon = icon;
		this.link = link;
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}
}
