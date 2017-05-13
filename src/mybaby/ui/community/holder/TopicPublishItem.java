package mybaby.ui.community.holder;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.hibb.mybaby.android.R;
import mybaby.models.community.TopicCategory;
import mybaby.models.community.item.CommunityTopicPublishItem;
import mybaby.ui.community.customclass.CustomAbsClass;

public class TopicPublishItem  extends ItemState {

	//多个话题推荐
	public static class TopicPublishHolder extends  RecyclerView.ViewHolder {
		public LinearLayout topicsmall_lin;

		public TopicPublishHolder(View view) {
			super(view);
			topicsmall_lin=(LinearLayout) view.findViewById(R.id.topicsmall_lin);
		}
	}

	@Override
	public void onBindViewHolder(Context context, RecyclerView.ViewHolder vHolder, View parentView, int position, boolean hasVisiPlace, HtmlItem.SetWebViewOnTouchListener listener) {
		if(vHolder instanceof TopicPublishHolder){
			TopicPublishHolder holder= (TopicPublishHolder) vHolder;
			bindDatas(context, (CommunityTopicPublishItem) this, holder);
		}
	}

	public static RecyclerView.ViewHolder getHolder(ViewGroup parent) {
		View convertView =LayoutInflater.from(parent.getContext())
				.inflate(R.layout.community_all_listitem_topicsmall, parent, false);
		TopicPublishHolder holder = new TopicPublishHolder(convertView);
		return holder;
	}

	@Override
	public int getStateType() {
		return ItemState.TopicPublishHolderTYPE_3;
	}

	@Override
	public View getItemView(View convertView, LayoutInflater inflater, Object data, Activity activity, boolean hasVisiPlace, int parentId, HtmlItem.SetWebViewOnTouchListener listener) {
		TopicPublishHolder holder;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.community_all_listitem_topicsmall, null);
			holder = new TopicPublishHolder(convertView);
			convertView.setTag(R.id.activityitem_topic_more,holder);
		}else{
			holder = (TopicPublishHolder)convertView.getTag(R.id.activityitem_topic_more);
		}
		if (data instanceof CommunityTopicPublishItem)
			bindDatas(activity, (CommunityTopicPublishItem) data,holder);
		return convertView;
	}
	
	public void bindDatas(final Context context,CommunityTopicPublishItem obj,TopicPublishHolder topicPublishHolder) {
		// TODO Auto-generated method stub
		final TopicCategory [] Categories=obj.getCategories();
		// 主视图容器添加view
		LayoutInflater inflater = LayoutInflater.from(context);
		View view_text = null;
		view_text = inflater.inflate(R.layout.textline_item, null);
		TextView tView1,tView2,tView3,tView4;
		tView1=(TextView) view_text.findViewById(R.id.textView1);
		tView2=(TextView) view_text.findViewById(R.id.textView2);
		tView3=(TextView) view_text.findViewById(R.id.textView3);
		tView4=(TextView) view_text.findViewById(R.id.textView4);
		
			
		int count=Categories.length;
		if (count>4) {
			count=4;
		}
		TextView []textViews=new TextView[] {tView1,tView2,tView3,tView4};
		for (int i = 0; i < count; i++) {
			final int at=i;
			
			if (Categories[i].getTitle().length()>6) {
				textViews[i].setText(Categories[i].getTitle().substring(0, 6)+"...");
			}else {
				textViews[i].setText(Categories[i].getTitle());
			}
			textViews[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					CustomAbsClass.starTopicEditIntent(context, 
							Categories[at].getId(),Categories[at].getTitle(), null);
				}
			});
		}
		topicPublishHolder.topicsmall_lin.removeAllViews();
		topicPublishHolder.topicsmall_lin.addView(view_text);
		
	}

}
