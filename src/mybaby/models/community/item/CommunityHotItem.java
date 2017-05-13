package mybaby.models.community.item;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.activity.AbstractMainActivity;
import mybaby.ui.community.holder.ActivityItem;

public class CommunityHotItem extends ActivityItem implements Serializable{

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

	public CommunityHotItem() {
	}

	public CommunityHotItem(Map<?, ?> mapItem ){
		super();
		this.setActivity(AbstractMainActivity.createByMap(mapItem));
	}
	public static CommunityHotItem creatByMap(Map<?, ?> mapItem){
		if (null==mapItem)
			return null;
		CommunityHotItem item=new CommunityHotItem();
		item.setActivity(AbstractMainActivity.createByMap(mapItem));
		return item;
	}
}
