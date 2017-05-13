package mybaby.models.community.item;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.TopicCategory;
import mybaby.ui.community.holder.TopicPublishOneItem;

public class CommunityTopicPublishOneItem extends TopicPublishOneItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TopicCategory category;
	
	public TopicCategory getCategory() {
		return category;
	}
	public void setCategory(TopicCategory category) {
		this.category = category;
	}

	public CommunityTopicPublishOneItem() {
	}

	public CommunityTopicPublishOneItem(Map<?, ?> mapItem ){
		super();
		this.setCategory(TopicCategory.createByMap(mapItem));
	}
}
