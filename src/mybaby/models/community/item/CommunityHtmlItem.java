package mybaby.models.community.item;

import java.io.Serializable;
import java.util.Map;

import mybaby.ui.community.holder.HtmlItem;
import mybaby.util.MapUtils;

public class CommunityHtmlItem extends HtmlItem implements Serializable{
	private static final long serialVersionUID = 1L;

	String top_border_color;
	Double aspect_ratio;//高宽比例
	String html;//html内容 
	public CommunityHtmlItem() {
		super();
	}
	public CommunityHtmlItem(Double aspect_ratio, String html,String top_border_color) {
		super();
		this.aspect_ratio = aspect_ratio;
		this.html = html;
		this.top_border_color = top_border_color;
	}

	public String getTop_border_color() {
		return top_border_color;
	}

	public void setTop_border_color(String top_border_color) {
		this.top_border_color = top_border_color;
	}

	public Double getAspect_ratio() {
		return aspect_ratio;
	}

	public void setAspect_ratio(Double aspect_ratio) {
		this.aspect_ratio = aspect_ratio;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public CommunityHtmlItem(Map<?, ?> mapItem ){
		super();
		this.setAspect_ratio(MapUtils.getMapDouble(mapItem, "aspect_ratio"));
		this.setHtml(MapUtils.getMapStr(mapItem, "html"));
		this.setTop_border_color(MapUtils.getMapStr(mapItem, "top_border_color"));
	}
	
}
