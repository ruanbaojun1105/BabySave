package mybaby.ui.community.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.models.community.item.CommunityAbstractItem;
import mybaby.models.community.item.CommunityActivityItem;
import mybaby.models.community.item.CommunityHotItem;
import mybaby.models.community.item.CommunityHtmlItem;
import mybaby.models.community.item.CommunityParentingItem;
import mybaby.models.community.item.CommunityPlaceSettingItem;
import mybaby.models.community.item.CommunityShareAppItem;
import mybaby.models.community.item.CommunityTopicPublishItem;
import mybaby.models.community.item.CommunityTopicPublishOneItem;
import mybaby.ui.MediaHelper;
import mybaby.ui.community.holder.ActivityItem;
import mybaby.ui.community.holder.HtmlItem;
import mybaby.ui.community.holder.SmallLinkItem;
import mybaby.ui.community.holder.ParentingItem;
import mybaby.ui.community.holder.PlaceSettingItem;
import mybaby.ui.community.holder.ShareAppItem;
import mybaby.ui.community.holder.ItemState;
import mybaby.ui.community.holder.TopicPublishItem;
import mybaby.ui.community.holder.TopicPublishOneItem;
import mybaby.util.AppUIUtils;



/**
 * 自定义列表适配器
 * 
 * @author baojun
 * 
 */
@Deprecated //已弃用
public class CommunityListAdapter extends BaseAdapter {

	/**
	 * 上下文
	 */
	private Activity activity;
	private List<CommunityAbstractItem> listData ;
	private boolean hasVisiPlace=true;//默认显示地理位置
	private MediaHelper mMediaHelper;
	private int parentId;//话题的动态id
	private HtmlItem.SetWebViewOnTouchListener listener;//浏览器的滑动处理，请不要使用全局变量，防止内存泄露
	/**
	 * 1 2
	 */
/**
 * 显示完全的列表
 * @param listData
 */
	public CommunityListAdapter(Activity activity,
			List<CommunityAbstractItem> listData,MediaHelper mMediaHelper) {
		this.activity = activity;
		this.listData = listData;
		this.mMediaHelper=mMediaHelper;
	}
	
@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}	
	
	/**
	 * 不显示地址信息
	 * @param listData
	 * @param hasVisiPlace true显示 false不显示
	 */
	public CommunityListAdapter(Activity activity,
			List<CommunityAbstractItem> listData ,MediaHelper mMediaHelper,boolean hasVisiPlace,int parentId,HtmlItem.SetWebViewOnTouchListener listener) {
		this.activity = activity;
		this.listData = listData;
		this.hasVisiPlace=hasVisiPlace;
		this.mMediaHelper=mMediaHelper;
		this.parentId=parentId;
		this.listener=listener;
	}

	
	/**
	 * 获取数据总条数
	 */
	@Override
	public int getCount() {
		return listData.size();
	}

	/**
	 * 获取某一位置的数据
	 */
	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	/**
	 * 获取唯一标识
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return ItemState.MAX_TYPE_SIZE;
	}
	

	//此处做一个类型复用
	/*private static ActivityItem activityItem=new ActivityItem();
	private static ParentingItem parentingItem=new ParentingItem();
	private static PlaceSettingItem placeSettingItem=new PlaceSettingItem();
	private static TopicPublishItem topicPublishItem=new TopicPublishItem();
	private static TopicPublishOneItem topicPublishOneItem=new TopicPublishOneItem();
	private static ShareAppItem shareAppItem=new ShareAppItem();
	private static HtmlItem htmlItem=new HtmlItem();*/
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		int type = 0;
		if (getItem(position) instanceof CommunityActivityItem) {
			type= ItemState.ActivityHolderTYPE_1;
		}else if (getItem(position) instanceof CommunityHotItem) {
			type= ItemState.ActivityHolderTYPE_1;
		}else if (getItem(position) instanceof CommunityParentingItem) {
			type= ItemState.ParentingHolderTYPE_1;
		}else if (getItem(position) instanceof CommunityPlaceSettingItem) {
			type= ItemState.PlaceSettingHolderTYPE_2;
		}else if (getItem(position) instanceof CommunityTopicPublishItem) {
			type= ItemState.TopicPublishHolderTYPE_3;
		}else if (getItem(position) instanceof CommunityTopicPublishOneItem) {
			type= ItemState.TopicPublishOneHolderTYPE_4;
		}else if (getItem(position) instanceof CommunityShareAppItem) {
			type= ItemState.ShareAppHolderTYPE_5;
		}else if (getItem(position) instanceof CommunityHtmlItem) {
			type= ItemState.HtmlHolderTYPE_6;
		}else if (getItem(position) instanceof SmallLinkItem) {
			type= ItemState.SmallLinkHolderTYPE_7;
		}
		return type;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		// 先获取到新动态的类型，然后再动态的选择该新动态的模型视图~getclass and instanceof  
		// 测试
		
		ActivityItem.Holeder avtivityHolder = null;
		ParentingItem.ParentingHolder parentingHolder = null;
		PlaceSettingItem.PlaceSettingHolder placeSettingHolder = null;
		TopicPublishItem.TopicPublishHolder topicPublishHolder = null;
		TopicPublishOneItem.TopicPublishOneHolder topicPublishOneHolder = null;
		ShareAppItem.ShareAppHolder shareAppHolder=null;
		HtmlItem.HtmlHolder htmlHolder=null;
		
		int type = getItemViewType(position);
		if (convertView==null) {
			switch (type) {
			case ItemState.ActivityHolderTYPE_1:
				convertView = LayoutInflater.from(activity).inflate(R.layout.community_all_listitem,parent, false);
				//convertView = AppUIUtils.inflate(R.layout.community_all_listitem);
				avtivityHolder=new ActivityItem.Holeder(convertView);
				convertView.setTag(R.id.activityitem_mainitem,avtivityHolder);
				break;
			case ItemState.ParentingHolderTYPE_1:
				convertView = AppUIUtils.inflate(R.layout.community_all_listitem_parenting);
				parentingHolder=new ParentingItem.ParentingHolder(convertView);
				convertView.setTag(R.id.activityitem_parenting,parentingHolder);
				break;
			case ItemState.PlaceSettingHolderTYPE_2:
				convertView = AppUIUtils.inflate(R.layout.community_all_listitem_place);
				placeSettingHolder=new PlaceSettingItem.PlaceSettingHolder(convertView);
				convertView.setTag(R.id.activityitem_placesetting,placeSettingHolder);
				break;
			case ItemState.TopicPublishHolderTYPE_3:
				convertView = AppUIUtils.inflate(R.layout.community_all_listitem_topicsmall);
				topicPublishHolder=new TopicPublishItem.TopicPublishHolder(convertView);
				convertView.setTag(R.id.activityitem_topic_more,topicPublishHolder);
				break;
			case ItemState.TopicPublishOneHolderTYPE_4:
				convertView = AppUIUtils.inflate(R.layout.community_all_listitem_topic);
				topicPublishOneHolder =new TopicPublishOneItem.TopicPublishOneHolder(convertView);
				convertView.setTag(R.id.activityitem_topic_one,topicPublishOneHolder);
				break;
			case ItemState.ShareAppHolderTYPE_5:
				convertView = AppUIUtils.inflate(R.layout.community_all_listitem_share);
				shareAppHolder =new ShareAppItem.ShareAppHolder(convertView);
				convertView.setTag(R.id.activityitem_share,shareAppHolder);
				break;
			case ItemState.HtmlHolderTYPE_6:
				convertView = AppUIUtils.inflate(R.layout.community_all_listitem_html);
				htmlHolder =new HtmlItem.HtmlHolder(convertView);
				convertView.setTag(R.id.activityitem_web,htmlHolder);
				break;
			}
		}
		else {
			switch (type) {
			case ItemState.ActivityHolderTYPE_1:
				avtivityHolder=(ActivityItem.Holeder) convertView.getTag(R.id.activityitem_mainitem);
				break;
			case ItemState.ParentingHolderTYPE_1:
				parentingHolder=(ParentingItem.ParentingHolder) convertView.getTag(R.id.activityitem_parenting);
				break;
			case ItemState.PlaceSettingHolderTYPE_2:
				placeSettingHolder=(PlaceSettingItem.PlaceSettingHolder) convertView.getTag(R.id.activityitem_placesetting);
				break;
			case ItemState.TopicPublishHolderTYPE_3:
				topicPublishHolder=(TopicPublishItem.TopicPublishHolder) convertView.getTag(R.id.activityitem_topic_more);
				break;
			case ItemState.TopicPublishOneHolderTYPE_4:
				topicPublishOneHolder=(TopicPublishOneItem.TopicPublishOneHolder) convertView.getTag(R.id.activityitem_topic_one);
				break;
			case ItemState.ShareAppHolderTYPE_5:
				shareAppHolder=(ShareAppItem.ShareAppHolder) convertView.getTag(R.id.activityitem_share);
				break;
			case ItemState.HtmlHolderTYPE_6:
				htmlHolder=(HtmlItem.HtmlHolder) convertView.getTag(R.id.activityitem_web);
				break;
			}
		}
		if (listData.size()==0)
			return convertView;
		final Object obj=getItem(position);
		switch (type) {
		case ItemState.ActivityHolderTYPE_1:
			new ActivityItem().bindDatas(activity, ((CommunityHotItem) obj).getActivity(), false, type, avtivityHolder, convertView, parentId, hasVisiPlace);
			break;
		case ItemState.ParentingHolderTYPE_1:
			new ParentingItem().bindDatas(activity, (CommunityParentingItem) obj, parentingHolder);
			break;
		case ItemState.PlaceSettingHolderTYPE_2:
			new PlaceSettingItem().bindDatas(activity, (CommunityPlaceSettingItem) obj, placeSettingHolder);
			break;
		case ItemState.TopicPublishHolderTYPE_3:
			new TopicPublishItem().bindDatas(activity, (CommunityTopicPublishItem) obj, topicPublishHolder);
			break;
		case ItemState.TopicPublishOneHolderTYPE_4:
			new TopicPublishOneItem().bindDatas(activity, (CommunityTopicPublishOneItem) obj, topicPublishOneHolder);
			break;
		case ItemState.ShareAppHolderTYPE_5:
			new ShareAppItem().bindDatas(activity, (CommunityShareAppItem) obj, shareAppHolder);
			break;
		case ItemState.HtmlHolderTYPE_6:
				new HtmlItem().bindDatas(activity, (CommunityHtmlItem) obj, listener, htmlHolder);
				break;
		}
		
		return convertView;
		
	}

}
