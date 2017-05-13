package mybaby.models.community;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.User;
import mybaby.util.MapUtils;

/**
 * Created by bj on 2016/4/12 0012.
 */
public class Comment implements Serializable{
    private int activityId;
    private int post_id;
    private User commentUser;
    private String commentContent;

    public static Comment[] createByArray(Object[] arr){
        if (arr==null)
            return null;
        Comment[] retArr=new Comment[arr.length];

        for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = creatByMap(map);
        }

        return retArr;
    }

    public static Comment creatByMap(Map<?, ?> mapItem){
        if (null==mapItem)
            return null;
        Comment comment=new Comment();
        comment.setActivityId(MapUtils.getMapInt(mapItem, "id"));
        comment.setPost_id(MapUtils.getMapInt(mapItem, "post_id"));
        comment.setCommentContent(MapUtils.getMapStr(mapItem, "comment"));
        Map<?, ?> mapUser = MapUtils.getMap(mapItem, "user");
        if (mapUser!=null)
            comment.setCommentUser(User.createByMap_new(mapUser));
        return comment;
    }

    public Comment() {
    }

    public Comment(int activityId, int post_id, User user, String commentContent) {
        this.activityId = activityId;
        this.post_id = post_id;
        this.commentUser = user;
        this.commentContent = commentContent;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public User getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(User commentUser) {
        this.commentUser = commentUser;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
