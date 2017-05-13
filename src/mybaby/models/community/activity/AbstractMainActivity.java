package mybaby.models.community.activity;


import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.User;
import mybaby.models.community.Comment;
import mybaby.models.community.Image;
import mybaby.models.community.Link;
import mybaby.models.community.Place;
import mybaby.models.community.TopicCategory;
import mybaby.util.MapUtils;

public class AbstractMainActivity extends AbstractActivity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int postId;
	private String title; //动态标题
	private String content; //动态内容
	private int likeCount; //赞的数量
	private int replyCount; //回复数量
	private String source; //来源描述：如：‘关注的人’，‘同城’，‘关注的话题’等
	private boolean liked; //是否已经点赞

	private TopicCategory category;
	private Place place;
	private Image[] images; //图片数组
	private Comment[] comments; //评论数组
	private Link link;
	private boolean can_delete;
	private String actionDesc;

	public int getPostId() {
		return postId;
	}
	public void setPostId(int postId) {
		this.postId = postId;
	}
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
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public int getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
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
	public Image[] getImages() {
		return images;
	}
	public void setImages(Image[] images) {
		this.images = images;
	}
	public boolean isLiked() {
		return liked;
	}
	public void setLiked(boolean liked) {
		this.liked = liked;
	}
	public String getActionDesc() {
		return actionDesc;
	}
	public void setActionDesc(String actionDesc) {
		this.actionDesc = actionDesc;
	}
	public AbstractMainActivity() {
		super();
	}
	public Link getLink() {
		return link;
	}
	public void setLink(Link link) {
		this.link = link;
	}
	public Comment[] getComments() {
		return comments;
	}
	public void setComments(Comment[] comments) {
		this.comments = comments;
	}

	public boolean isCan_delete() {
		return can_delete;
	}
	public void setCan_delete(boolean can_delete) {
		this.can_delete = can_delete;
	}

	public static AbstractMainActivity[] createByArray(Object[] arr){
		AbstractMainActivity[] retArr=new AbstractMainActivity[arr.length];
       	for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            LogUtil.e(new JSONObject(map).toString());
            retArr[i] = createByMap(map);
       	}
		return retArr;
	}
	
	public static AbstractMainActivity createByMap(Map<?, ?> map){
		AbstractMainActivity obj = new AbstractMainActivity();
		/*if(postType.equals("mb_topic")){
			obj=new TopicActivity();
		}else if(postType.equals("post")){
			obj=new DiaryActivity();
		}*/
		obj.setActionDesc(MapUtils.getMapStr(map, "actionDesc"));
		obj.setId(MapUtils.getMapInt(map, "id"));
		obj.setDatetime_gmt(MapUtils.getMapDateLong(map, "datetime"));
		obj.setPostId(MapUtils.getMapInt(map, "postId"));
		obj.setTitle(MapUtils.getMapStr(map, "title"));
		obj.setContent(MapUtils.getMapStr(map, "content"));
		obj.setLikeCount(MapUtils.getMapInt(map, "likeCount"));
		obj.setReplyCount(MapUtils.getMapInt(map, "replyCount"));
		obj.setSource(MapUtils.getMapStr(map, "source"));
		obj.setLiked(MapUtils.getMapBool(map, "liked"));
		obj.setCan_delete(MapUtils.getMapBool(map, "can_delete"));
	    
		Map<?, ?> mapCategory = MapUtils.getMap(map,"category");
		Map<?, ?> mapUser = MapUtils.getMap(map, "user");
		Object[] arrImage = MapUtils.getArray(map, "images");
		Map<?, ?> mapPlace = MapUtils.getMap(map, "place");
		Map<?, ?> mapLink = MapUtils.getMap(map,"link");
		Object[] arrComments = MapUtils.getArray(map,"comments");
		
	    if(mapCategory != null)
	    	obj.setCategory(TopicCategory.createByMap(mapCategory));
	    if(mapPlace != null)
	    	obj.setPlace(Place.createByMap(mapPlace));
	    if(mapUser != null)
	    	obj.setUser(User.createByMap_new(mapUser));
		if(mapLink != null)
			obj.setLink(Link.createByMap(mapLink));
	    if(arrImage != null)
	    	obj.setImages(Image.createByArray(arrImage));
		if (arrComments!=null)
			obj.setComments(Comment.createByArray(arrComments));
		return obj;
	}
	
	
}
