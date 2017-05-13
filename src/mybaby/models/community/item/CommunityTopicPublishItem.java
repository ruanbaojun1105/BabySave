package mybaby.models.community.item;

import java.io.Serializable;

import mybaby.models.community.TopicCategory;
import mybaby.ui.community.holder.TopicPublishItem;

public class CommunityTopicPublishItem extends TopicPublishItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TopicCategory[] categories;
	
	public TopicCategory[] getCategories() {
		return categories;
	}
	public void setCategories(TopicCategory[] categories) {
		this.categories = categories;
	}

	public CommunityTopicPublishItem() {
	}

	public CommunityTopicPublishItem(Object[] arr ){
		super();
		this.setCategories(TopicCategory.createByArray(arr));
	}
}
