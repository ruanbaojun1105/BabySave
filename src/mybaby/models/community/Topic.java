package mybaby.models.community;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.BaseObject;
import mybaby.models.diary.Media;
import mybaby.util.MapUtils;

public class Topic extends BaseObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String title="";
	String content="";
	TopicCategory category;
	Place place;
	Media[] medias;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public TopicCategory getCategory() {
		return category;
	}
	public void setCategory(TopicCategory category) {
		this.category = category;
	}
	public Place getPlace() {
		return place;
	}
	public void setPlace(Place place) {
		this.place = place;
	}
	public Media[] getMedias() {
		return medias;
	}
	public void setMedias(Media[] medias) {
		this.medias = medias;
	}
	
	public Topic() {
		super();
	}
	
	public Topic(TopicCategory category){
		super();
		this.category=category;
	}

	

	public static Topic createByMap(Map<?, ?> map){
		Topic obj=new Topic();
		if (map.containsKey("topicId"))
			obj.setId(MapUtils.getMapInt(map, "topicId"));//为什么只有三个返回 ？！？！？！-!!!{"topicId":100002039,"placeId":0,"categoryId":"0"}
		//{"topicId":100003756,"placeId":"100003754","categoryId":"0"}
		return obj;
	}
	
}
