package mybaby.models.community;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.BaseObject;
import mybaby.util.MapUtils;

public class TopicCategory extends BaseObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//— 0--普通，1—先选照片
	public enum PublishType{	
		Common,
		Photos
	}

	int categoryId=0;
	String title="";
	PublishType publishType;
	String recommendPublishDesc="";
	String recommendPublishBackgroundUrl="";
	boolean recommendPublishAllowShare;
	
	
	
	
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = "#"+title;
	}
	public PublishType getPublishType() {
		return publishType;
	}
	public void setPublishType(PublishType publishType) {
		this.publishType = publishType;
	}
	public String getRecommendPublishDesc() {
		return recommendPublishDesc;
	}
	public void setRecommendPublishDesc(String recommendPublishDesc) {
		this.recommendPublishDesc = recommendPublishDesc;
	}
	public String getRecommendPublishBackgroundUrl() {
		return recommendPublishBackgroundUrl;
	}
	public void setRecommendPublishBackgroundUrl(
			String recommendPublishBackgroundUrl) {
		this.recommendPublishBackgroundUrl = recommendPublishBackgroundUrl;
	}
	public boolean isRecommendPublishAllowShare() {
		return recommendPublishAllowShare;
	}
	public void setRecommendPublishAllowShare(boolean recommendPublishAllowShare) {
		this.recommendPublishAllowShare = recommendPublishAllowShare;
	}


	public TopicCategory() {
		super();
	}
	
	public static TopicCategory[] createByArray(Object[] arr){
		if (arr==null)
			return null;
		TopicCategory[] retArr=new TopicCategory[arr.length];
		
       	for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = createByMap(map);
       	}
       	
		return retArr;
	}
	
	public static TopicCategory createByMap(Map<?, ?> map){
		TopicCategory obj=new TopicCategory();
		if(map.containsKey("id"))
			obj.setId(MapUtils.getMapInt(map, "id"));
			obj.setCategoryId(MapUtils.getMapInt(map, "id"));
		if(map.containsKey("title"))
			obj.setTitle(MapUtils.getMapStr(map, "title"));
		if(map.containsKey("publish_type"))
			obj.setPublishType(PublishType.values()[MapUtils.getMapInt(map, "publish_type")]);
		if(map.containsKey("recommend_publish_desc"))
		obj.setRecommendPublishDesc(MapUtils.getMapStr(map, "recommend_publish_desc"));
		if(map.containsKey("recommend_publish_background_url"))
		obj.setRecommendPublishBackgroundUrl(MapUtils.getMapStr(map, "recommend_publish_background_url"));
		if(map.containsKey("recommend_publish_allow_share"))
		obj.setRecommendPublishAllowShare(MapUtils.getMapBool(map, "recommend_publish_allow_share"));

		return obj;
	}
	
	
}
