package mybaby.models.notification;

import java.io.Serializable;
import java.util.Map;

import mybaby.util.DateUtils;
import mybaby.util.MapUtils;

public class NotificationCategory  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	String key;
	String imageUrl;
	String title;
	String newestDesc;
	Long newestDatetime_gmt;
	int unreadCount=0;
	String action;
	boolean isStrongRemind;
	long tribe_id;

	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getNewestDesc() {
		return newestDesc;
	}
	public void setNewestDesc(String newestDesc) {
		this.newestDesc = newestDesc;
	}
	public Long getNewestDatetime_gmt() {
		return newestDatetime_gmt;
	}
	public void setNewestDatetime_gmt(Long newestDatetime_gmt) {
		this.newestDatetime_gmt = newestDatetime_gmt;
	}
	public int getUnreadCount() {
		return unreadCount;
	}
	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public boolean isStrongRemind() {
		return isStrongRemind;
	}
	public void setStrongRemind(boolean isStrongRemind) {
		this.isStrongRemind = isStrongRemind;
	}

	public NotificationCategory() {
		super();
	}
	
	public long getNewestDatetime(){
	    return DateUtils.gmtTime2LocalTime(getNewestDatetime_gmt());
	}


	public long getTribe_id() {
		return tribe_id;
	}

	public void setTribe_id(long tribe_id) {
		this.tribe_id = tribe_id;
	}

	public static NotificationCategory[] createByArray(Object[] arr){
		if (arr==null)
			return null;
		NotificationCategory[] retArr=new NotificationCategory[arr.length];
       	for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = createByMap(map);
       	}
       	
		return retArr;
	}
	
	public static NotificationCategory createByMap(Map<?, ?> map){
		NotificationCategory obj=new NotificationCategory();
		
		obj.setKey(MapUtils.getMapStr(map, "category_key"));
		obj.setImageUrl(MapUtils.getMapStr(map, "image_url"));
		obj.setTitle(MapUtils.getMapStr(map, "title"));
		obj.setNewestDesc(MapUtils.getMapStr(map, "newest_desc"));
		obj.setNewestDatetime_gmt(DateUtils.gmtTime2LocalTime(MapUtils.getMapDateLong(map, "newest_datetime")));
		//将服务器传下来的时间转为本地时间，阿里返回的时间就是本地时间，无需再转
		obj.setUnreadCount(MapUtils.getMapInt(map, "unread_count"));
		obj.setAction(MapUtils.getMapStr(map, "action"));
		obj.setStrongRemind(MapUtils.getMapBool(map, "is_strong_remind"));
		obj.setTribe_id(MapUtils.getMapLong(map, "tribe_id"));
		return obj;
	}

}
