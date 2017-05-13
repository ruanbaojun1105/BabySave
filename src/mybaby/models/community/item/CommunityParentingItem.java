package mybaby.models.community.item;

import java.io.Serializable;

import mybaby.models.community.ParentingPost;
import mybaby.ui.community.holder.ParentingItem;

public class CommunityParentingItem extends ParentingItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ParentingPost[] parentingPosts;
	
	public ParentingPost[] getParentingPosts() {
		return parentingPosts;
	}
	public void setParentingPosts(ParentingPost[] parentingPosts) {
		this.parentingPosts = parentingPosts;
	}

	public CommunityParentingItem() {
	}

	public CommunityParentingItem(Object[] arr ){
		super();
		this.setParentingPosts(ParentingPost.createByArray(arr));
	}
}
