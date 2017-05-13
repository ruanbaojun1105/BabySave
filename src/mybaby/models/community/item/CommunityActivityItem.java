package mybaby.models.community.item;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.activity.AbstractMainActivity;
import mybaby.ui.community.holder.ActivityItem;

public class CommunityActivityItem extends ActivityItem implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	AbstractMainActivity activity; //动态
	
	public AbstractMainActivity getActivity() {
		return activity;
	}
	public void setActivity(AbstractMainActivity activity) {
		this.activity = activity;
	}

	
	public CommunityActivityItem(AbstractMainActivity activity) {
		super();
		this.activity = activity;
	}

	public CommunityActivityItem() {
	}

	public CommunityActivityItem(Map<?, ?> mapItem ){
		super();
		this.setActivity(AbstractMainActivity.createByMap(mapItem));
	}
	public static CommunityActivityItem creatByMap(Map<?, ?> mapItem){
		if (null==mapItem)
			return null;
		CommunityActivityItem item=new CommunityActivityItem();
		item.setActivity(AbstractMainActivity.createByMap(mapItem));
		return item;
	}

}
