package mybaby.models.community.item;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.Icon;
import mybaby.models.community.Link;
import mybaby.ui.community.holder.SmallLinkItem;
import mybaby.util.MapUtils;

/**
 * bj
 */
public class CommunitySmallLinkItem extends SmallLinkItem implements Serializable{
	private static final long serialVersionUID = 1L;

	private String content;
	private Icon icon;
	private Link link;

	public CommunitySmallLinkItem() {
	}

	public static CommunitySmallLinkItem creatByMap(Map<?, ?> mapItem){
		if (null==mapItem)
			return null;
		CommunitySmallLinkItem item=new CommunitySmallLinkItem();
		item.setContent(MapUtils.getMapStr(mapItem, "content"));
		item.setIcon(Icon.creatByMap(MapUtils.getMap(mapItem, "icon")));
		item.setLink(Link.createByMap(MapUtils.getMap(mapItem, "link")));
		return item;
	}

	public CommunitySmallLinkItem(Map<?, ?> mapItem ){
		super();
		this.setContent(MapUtils.getMapStr(mapItem, "content"));
		this.setIcon(Icon.creatByMap(MapUtils.getMap(mapItem, "icon")));
		this.setLink(Link.createByMap(MapUtils.getMap(mapItem, "link")));
	}

	public CommunitySmallLinkItem(String content, Icon icon, Link link) {
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
