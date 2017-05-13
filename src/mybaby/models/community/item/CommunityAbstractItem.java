package mybaby.models.community.item;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mybaby.ui.community.holder.ItemState;
import mybaby.util.MapUtils;
public class CommunityAbstractItem {

	@SuppressLint("NewApi")
	public static List<ItemState> createByArray(Object[] objects){
		List<ItemState> communityAbstractItemList=new ArrayList<>();
		if (objects==null)
			return communityAbstractItemList;
			/*try {
				Utils.Loge(new JSONArray(objects).toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		for (Object obj:objects) {
			ItemState item = null;
			try {
				item = createByMap((Map<?, ?>) obj);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			if (item == null)
				continue;
			communityAbstractItemList.add(item);
		}
		return communityAbstractItemList;
	}

	public static ItemState createByMap(Map<?, ?> map) throws Exception{
		ItemState obj=null;

		int type=MapUtils.getMapInt(map, "type");
		//1-－普通，2-－热门，3-－育儿知识，4-－发布话题，5-－购物广告，6-－生活广告,
		// 7--地点设置, 8--发布话题(大)，9--邀请好友,10html,11 -- 小链接模式 12 －－大链接模式 --- 13>大图片连接
		if(type==1){
			obj=new CommunityActivityItem(MapUtils.getMap(map,"item"));
			obj.setItemType(ItemState.ActivityHolderTYPE_1);
		}else if(type==2){
			obj=new CommunityHotItem(MapUtils.getMap(map,"item"));
			obj.setItemType(ItemState.ActivityHolderTYPE_1);
		}else if(type==3){
			obj=new CommunityParentingItem(MapUtils.getArray(map,"item"));
			obj.setItemType(ItemState.ParentingHolderTYPE_1);
		}else if(type==4){
			obj=new CommunityTopicPublishItem(MapUtils.getArray(map,"item"));
			obj.setItemType(ItemState.TopicPublishHolderTYPE_3);
		}else if(type==5){

		}else if(type==6){

		}else if(type==7){
			obj=new CommunityPlaceSettingItem(MapUtils.getMap(map,"item"));
			obj.setItemType(ItemState.PlaceSettingHolderTYPE_2);
		}else if(type==8){
			obj=new CommunityTopicPublishOneItem(MapUtils.getMap(map,"item"));
			obj.setItemType(ItemState.TopicPublishOneHolderTYPE_4);
		}else if(type==9){
			obj=new CommunityShareAppItem(MapUtils.getMap(map,"item"));
			obj.setItemType(ItemState.ShareAppHolderTYPE_5);
		}else if (type==10) {
			obj=new CommunityHtmlItem(MapUtils.getMap(map,"item"));
			obj.setItemType(ItemState.HtmlHolderTYPE_6);
		}else if (type==11) {
			obj=new CommunitySmallLinkItem(MapUtils.getMap(map,"item"));
			obj.setItemType(ItemState.SmallLinkHolderTYPE_7);
		}else if (type==12) {
			obj=new CommunityBigLinkItem(MapUtils.getMap(map,"item"));
			obj.setItemType(ItemState.BigLinkHolderTYPE_8);
		}else if (type==13) {
			obj=new CommunityBigImageLinkItem(MapUtils.getMap(map,"item"));
			obj.setItemType(ItemState.BigImageLinkHolderTYPE_9);
		}
		return obj;
	}

}
