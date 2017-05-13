package mybaby.ui.community.adapter;

import android.app.Activity;
import android.content.Context;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.models.community.item.CommunityBigImageLinkItem;
import mybaby.models.community.item.CommunityBigLinkItem;
import mybaby.models.community.item.CommunityHtmlItem;
import mybaby.models.community.item.CommunityParentingItem;
import mybaby.models.community.item.CommunityPlaceSettingItem;
import mybaby.models.community.item.CommunityShareAppItem;
import mybaby.models.community.item.CommunitySmallLinkItem;
import mybaby.models.community.item.CommunityTopicPublishItem;
import mybaby.models.community.item.CommunityTopicPublishOneItem;
import mybaby.ui.community.holder.ActivityItem;
import mybaby.ui.community.holder.BigImageLinkItem;
import mybaby.ui.community.holder.BigLinkItem;
import mybaby.ui.community.holder.HtmlItem;
import mybaby.ui.community.holder.ItemState;
import mybaby.ui.community.holder.ParentingItem;
import mybaby.ui.community.holder.PlaceSettingItem;
import mybaby.ui.community.holder.ShareAppItem;
import mybaby.ui.community.holder.SmallLinkItem;
import mybaby.ui.community.holder.TopicPublishItem;
import mybaby.ui.community.holder.TopicPublishOneItem;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<ItemState> {
    private Context context;
    private boolean hasVisiPlace=true;//默认显示地理位置
    private HtmlItem.SetWebViewOnTouchListener listener;//浏览器的滑动处理，请不要使用全局变量，防止内存泄露

    public MultipleItemQuickAdapter(Context context, List data, boolean hasVisiPlace, HtmlItem.SetWebViewOnTouchListener listener) {
        super( data);
        this.context=context;
        this.hasVisiPlace = hasVisiPlace;
        this.listener = listener;
        addItemType(ItemState.ActivityHolderTYPE_1, R.layout.community_all_listitem);
        addItemType(ItemState.ParentingHolderTYPE_1, R.layout.community_all_listitem_parenting);
        addItemType(ItemState.PlaceSettingHolderTYPE_2, R.layout.community_all_listitem_place);
        addItemType(ItemState.TopicPublishHolderTYPE_3, R.layout.community_all_listitem_topicsmall);
        addItemType(ItemState.TopicPublishOneHolderTYPE_4, R.layout.community_all_listitem_topic);
        addItemType(ItemState.ShareAppHolderTYPE_5, R.layout.community_all_listitem_share);
        addItemType(ItemState.HtmlHolderTYPE_6, R.layout.community_all_listitem_html);
        addItemType(ItemState.SmallLinkHolderTYPE_7, R.layout.community_activityitem_bigimagelink);
        addItemType(ItemState.BigLinkHolderTYPE_8, R.layout.community_activityitem_biglink);
        addItemType(ItemState.BigImageLinkHolderTYPE_9, R.layout.community_activityitem_bigimagelink);
    }

    @Override
    protected void convert(BaseViewHolder helper, ItemState item) {
        switch (helper.getItemViewType()) {
            case ItemState.ActivityHolderTYPE_1:
                if (item instanceof ActivityItem) {
                    ActivityItem item1 = (ActivityItem) item;
                    item1.bindDatas((Activity) context, item1, false, new ActivityItem.Holeder(helper.convertView), helper.convertView, helper.getLayoutPosition(), hasVisiPlace);
                }
                break;
            case ItemState.ParentingHolderTYPE_1:
                if (item instanceof CommunityParentingItem) {
                    CommunityParentingItem item1 = (CommunityParentingItem) item;
                    item1.bindDatas(context, item1,new ParentingItem.ParentingHolder(helper.convertView));
                }
                break;
            case ItemState.PlaceSettingHolderTYPE_2:
                if (item instanceof CommunityPlaceSettingItem) {
                    CommunityPlaceSettingItem item1 = (CommunityPlaceSettingItem) item;
                    item1.bindDatas(context, item1,new PlaceSettingItem.PlaceSettingHolder(helper.convertView));
                }
                break;
            case ItemState.TopicPublishHolderTYPE_3:
                if (item instanceof CommunityTopicPublishItem) {
                    CommunityTopicPublishItem item1 = (CommunityTopicPublishItem) item;
                    item1.bindDatas(context,  item1,new TopicPublishItem.TopicPublishHolder(helper.convertView));
                }
                break;
            case ItemState.TopicPublishOneHolderTYPE_4:
                if (item instanceof CommunityTopicPublishOneItem) {
                    CommunityTopicPublishOneItem item1 = (CommunityTopicPublishOneItem) item;
                    item1.bindDatas((Activity)context,  item1,new TopicPublishOneItem.TopicPublishOneHolder(helper.convertView));
                }
                break;
            case ItemState.ShareAppHolderTYPE_5:
                if (item instanceof CommunityShareAppItem) {
                    CommunityShareAppItem item1 = (CommunityShareAppItem) item;
                    item1.bindDatas(context,  item1,new ShareAppItem.ShareAppHolder(helper.convertView));
                }
                break;
            case ItemState.HtmlHolderTYPE_6:
                if (item instanceof CommunityHtmlItem) {
                    CommunityHtmlItem item1 = (CommunityHtmlItem) item;
                    item1.bindDatas(context, item1,listener,new HtmlItem.HtmlHolder(helper.convertView));
                }
                break;
            case ItemState.SmallLinkHolderTYPE_7:
                if (item instanceof CommunitySmallLinkItem) {
                    CommunitySmallLinkItem item1 = (CommunitySmallLinkItem) item;
                    item1.bindDatas(context,   item1,new SmallLinkItem.SimallLinkHolder(helper.convertView));
                }
                break;
            case ItemState.BigLinkHolderTYPE_8:
                if (item instanceof CommunityBigLinkItem) {
                    CommunityBigLinkItem item1 = (CommunityBigLinkItem) item;
                    item1.bindDatas(context,   item1,new BigLinkItem.BigHolder(helper.convertView));
                }
                break;
            case ItemState.BigImageLinkHolderTYPE_9:
                if (item instanceof CommunityBigImageLinkItem) {
                    CommunityBigImageLinkItem item1 = (CommunityBigImageLinkItem) item;
                    item1.bindDatas(context, item1,new BigImageLinkItem.BigImageHolder(helper.convertView));
                }
                break;
        }
    }

}