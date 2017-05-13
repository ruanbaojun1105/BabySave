package mybaby.models.notification;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.User;
import mybaby.models.community.Image;
import mybaby.util.DateUtils;
import mybaby.util.MapUtils;


public class Notification  implements Serializable {

	private static final long serialVersionUID = 1L;
	
////通知类别: 1-－有人关注我，2-－有人给我的动态点赞，3-－有人回复了我的动态，4-－系统通知
	public enum NotificationType{
			Follow,
		    Like,
		    Reply,
		    System		
	}

	int id;
	NotificationType type;
	Long datetime_gmt;
	User user;
	String content;
	Image image;
	int sourceId;//消息相关的id, 目前type=2,3时会返回赞和回复的动态id
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public NotificationType getType() {
		return type;
	}
	public void setType(NotificationType type) {
		this.type = type;
	}
	public Long getDatetime_gmt() {
		return datetime_gmt;
	}
	public void setDatetime_gmt(Long datetime_gmt) {
		this.datetime_gmt = datetime_gmt;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public int getSourceId() {
		return sourceId;
	}
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}
	
	
	public Notification() {
		super();
	}
	
	public long getDatetime(){
	    return DateUtils.gmtTime2LocalTime(getDatetime_gmt());
	}
	
	public static Notification[] createByArray(Object[] arr){
		if (arr==null)
			return null;
		Notification[] retArr=new Notification[arr.length];
		
       	for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = createByMap(map);
            //Utils.Loge(map.toString());
       	}
       	
		return retArr;
	}
	
	public static Notification createByMap(Map<?, ?> map){
		Notification obj=new Notification();
		
		Map<?, ?> mapUser = MapUtils.getMap(map,"user");
		Map<?, ?> mapImage = MapUtils.getMap(map,"image");
				
		obj.setId(MapUtils.getMapInt(map, "id"));
		obj.setType(NotificationType.values()[MapUtils.getMapInt(map, "type")-1]);//从1开始的所以减1
		obj.setDatetime_gmt(MapUtils.getMapDateLong(map, "datetime"));
		obj.setUser(User.createByMap_new(mapUser));
		obj.setContent(MapUtils.getMapStr(map, "content"));
		
		if(mapImage != null)
			obj.setImage(Image.createByMap(mapImage));
		if(map.containsKey("source_id"))
			obj.setSourceId(MapUtils.getMapInt(map, "source_id"));
		
		return obj;
	}
}
