package mybaby.models.community.activity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import mybaby.models.User;
import mybaby.models.community.Image;
import mybaby.models.community.Place;
import mybaby.models.community.TopicCategory;
import mybaby.util.MapUtils;

public class ReplyActivity extends AbstractActivity  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int replyId;
	String content; //动态内容

	Image[] images; //图片数组
	User replyUser;
	
	public int getReplyId() {
		return replyId;
	}
	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Image[] getImages() {
		return images;
	}
	public void setImages(Image[] images) {
		this.images = images;
	}
	public User getReplyUser() {
		return replyUser;
	}
	public void setReplyUser(User replyUser) {
		this.replyUser = replyUser;
	}
	
	
	
	
	
	
	public static ReplyActivity[] createByArray(Object[] arr){
		ReplyActivity[] retArr=new ReplyActivity[arr.length];
		
       	for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = createByMap(map);
       	}
       	
		return retArr;
	}
	
	public static ReplyActivity createByMap(Map<?, ?> map){
		ReplyActivity obj=new ReplyActivity();
		
		obj.setId(MapUtils.getMapInt(map, "id"));
		obj.setDatetime_gmt(MapUtils.getMapDateLong(map, "datetime"));
		obj.setReplyId(MapUtils.getMapInt(map, "replyId"));
		obj.setContent(MapUtils.getMapStr(map, "content"));
	    
		Map<?, ?> mapUser = MapUtils.getMap(map,"user");
		Object[] arrImage = MapUtils.getArray(map,"images");
		Map<?, ?> mapReplyUser = MapUtils.getMap(map,"reply_user");
		
	    if(mapUser != null)
	    	obj.setUser(User.createByMap_new(mapUser));
	    
	    if(arrImage != null)
	    	obj.setImages(Image.createByArray(arrImage));
		
	    if(mapReplyUser != null)
	    	obj.setReplyUser(User.createByMap_new(mapReplyUser));

		return obj;
	}
	
}
