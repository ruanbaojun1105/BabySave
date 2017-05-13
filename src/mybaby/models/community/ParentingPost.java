package mybaby.models.community;

import java.io.Serializable;
import java.util.Map;

import mybaby.util.DateUtils;
import mybaby.util.MapUtils;

public class ParentingPost implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private long datetime_gmt;//日期
	private String title;//标题
	private String excerpt;//摘要
	private String featuredImageUrl;//特色图片Url
	private String detailUrl;//详细内容Url

	public static ParentingPost[] createByArray(Object[] arr) {
		if (arr==null)
			return null;
		ParentingPost[] retArr=new ParentingPost[arr.length];
		
       	for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = createByMap(map);
       	}
       	
		return retArr;
	}

	public long getDatetime(){
	    return DateUtils.gmtTime2LocalTime(getDatetime_gmt());
	}
	public static ParentingPost createByMap(Map<?, ?> map){
		ParentingPost obj=new ParentingPost();
		
		if(map.containsKey("title"))
			obj.setTitle(MapUtils.getMapStr(map, "title"));
		if(map.containsKey("datetime"))
			obj.setDatetime_gmt(MapUtils.getMapDateLong(map, "datetime"));
		if(map.containsKey("excerpt"))
			obj.setExcerpt(MapUtils.getMapStr(map, "excerpt"));
		if(map.containsKey("featuredImageUrl"))
			obj.setFeaturedImageUrl((MapUtils.getMapStr(map, "featuredImageUrl")).trim());
		if(map.containsKey("detailUrl"))
			obj.setDetailUrl(MapUtils.getMapStr(map, "detailUrl"));
		return obj;
	}

	public long getDatetime_gmt() {
		return datetime_gmt;
	}

	public void setDatetime_gmt(long datetime_gmt) {
		this.datetime_gmt = datetime_gmt;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getExcerpt() {
		return excerpt;
	}

	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}

	public String getFeaturedImageUrl() {
		return featuredImageUrl;
	}

	public void setFeaturedImageUrl(String featuredImageUrl) {
		this.featuredImageUrl = featuredImageUrl;
	}

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ParentingPost(long datetime_gmt, String title, String excerpt,
			String featuredImageUrl, String detailUrl) {
		super();
		this.datetime_gmt = datetime_gmt;
		this.title = title;
		this.excerpt = excerpt;
		this.featuredImageUrl = featuredImageUrl;
		this.detailUrl = detailUrl;
	}

	public ParentingPost() {
		super();
	}
	
	
	
}
