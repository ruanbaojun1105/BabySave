package mybaby.ui.community.holder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.widget.IconTextView;
import com.umeng.analytics.MobclickAgent;

import me.hibb.mybaby.android.R;
import mybaby.models.community.TopicCategory;
import mybaby.models.community.TopicCategory.PublishType;
import mybaby.models.community.item.CommunityTopicPublishOneItem;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.util.ImageViewUtil;
import mybaby.util.Utils;

//大话题
public class TopicPublishOneItem  extends ItemState implements OnClickListener {
	public Activity activity;
	private TopicCategory category;
	public TopicPublishOneItem() {
	}
	//大话题单个
	public static class TopicPublishOneHolder extends RecyclerView.ViewHolder{
		public RelativeLayout categor_rl;
		public IconTextView sendtopic_btn,share_btn;
		public LinearLayout sendtopic_lin,share_lin;
		public TextView title_tv;
		public TextView hint_tv;
		public ImageView item_bg;

		public TopicPublishOneHolder(View view) {
			super(view);
			categor_rl = (RelativeLayout) view
					.findViewById(R.id.categor_rl);
			sendtopic_btn = (IconTextView) view
					.findViewById(R.id.sendtopic_btn);
			share_btn = (IconTextView) view.findViewById(R.id.share_btn);
			hint_tv = (TextView) view.findViewById(R.id.hint_tv);
			title_tv = (TextView) view.findViewById(R.id.title);
			item_bg = (ImageView) view.findViewById(R.id.item_bg);
			sendtopic_lin= (LinearLayout) view.findViewById(R.id.sendtopic_lin);
			share_lin= (LinearLayout) view.findViewById(R.id.share_lin);
		}
	}

	@Override
	public void onBindViewHolder(Context context, RecyclerView.ViewHolder vHolder, View parentView, int position, boolean hasVisiPlace, HtmlItem.SetWebViewOnTouchListener listener) {
		if(vHolder instanceof TopicPublishOneHolder){
			TopicPublishOneHolder holder= (TopicPublishOneHolder) vHolder;
			bindDatas((Activity)context, (CommunityTopicPublishOneItem) this, holder);
		}
	}

	public static RecyclerView.ViewHolder getHolder(ViewGroup parent) {
		View convertView =LayoutInflater.from(parent.getContext())
				.inflate(R.layout.community_all_listitem_topic, parent, false);
		TopicPublishOneHolder holder = new TopicPublishOneHolder(convertView);
		return holder;
	}

	@Override
	public int getStateType() {
		return ItemState.TopicPublishOneHolderTYPE_4;
	}

	@Override
	public View getItemView(View convertView, LayoutInflater inflater, Object data, Activity activity, boolean hasVisiPlace, int parentId, HtmlItem.SetWebViewOnTouchListener listener) {
		TopicPublishOneHolder holder;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.community_all_listitem_topic, null);
			holder = new TopicPublishOneHolder(convertView);
			convertView.setTag(R.id.activityitem_topic_one,holder);
		}else{
			holder = (TopicPublishOneHolder)convertView.getTag(R.id.activityitem_topic_one);
		}
		if (data instanceof CommunityTopicPublishOneItem)
			bindDatas(activity, (CommunityTopicPublishOneItem) data,holder);
		return convertView;
	}

	@SuppressLint({ "NewApi", "InflateParams" })
	public void bindDatas(Activity activity,final CommunityTopicPublishOneItem obj,
			final TopicPublishOneHolder vholder) {
		// TODO Auto-generated method stub
		// 主视图容器添加view
		if (obj.getCategory()==null)
			return;
		this.activity=activity;
		category=obj.getCategory();
		vholder.sendtopic_btn.setText(category.getPublishType() == PublishType.Photos ? " 马上参与" : " 我有话说");
		vholder.sendtopic_btn.setCompoundDrawablesWithIntrinsicBounds(new IconDrawable(activity,
				category.getPublishType() == PublishType.Photos ? FontAwesomeIcons.fa_camera : FontAwesomeIcons.fa_comment)
				.colorRes(R.color.white)
				.sizePx(Utils.sp2px(activity, 17)), null, null, null);

		vholder.share_btn.setText("去看看");

		vholder.categor_rl.bringToFront();
		vholder.share_btn.bringToFront();
		vholder.sendtopic_btn.bringToFront();
		vholder.categor_rl.setOnClickListener(this);
		vholder.title_tv.setText(obj.getCategory().getTitle());
		vholder.hint_tv.setText(obj.getCategory().getRecommendPublishDesc());
		
		
		/*Picasso.with(activity).
		load(obj.getCategory().getRecommendPublishBackgroundUrl())
		.resize(MyBabyApp.screenWidth/2, ScreenUtils.dip2px(activity, 200)/2)
		.centerCrop()
		.skipMemoryCache()
		.placeholder(new ColorDrawable(Color.parseColor("#f5f5f5")))
		.config(Bitmap.Config.RGB_565)
		.error(activity.getResources().getDrawable(R.drawable.ic_action_picture))
		.into(vholder.item_bg);*/
		ImageViewUtil.displayImage(category.getRecommendPublishBackgroundUrl(), vholder.item_bg);
		vholder.hint_tv.bringToFront();
		vholder.title_tv.bringToFront();
		vholder.sendtopic_lin.setOnClickListener(this);
		vholder.share_lin.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.categor_rl:
			if (category.getPublishType()==PublishType.Photos) {
				CustomAbsClass.openTopicTitleList(activity, category,true);
			}else {
				CustomAbsClass.openTopicTitleList(activity, category);
			}
			break;
		case R.id.share_lin:
			// 统计点击次数
			MobclickAgent.onEvent(activity, "17");// 去看看按钮
			if (category.getPublishType()==PublishType.Photos) {
				CustomAbsClass.openTopicTitleList(activity, category,true);
			}else {
				CustomAbsClass.openTopicTitleList(activity, category);
			}
			/*Intent intent =new Intent(activity, TestMainActivity.class);
			activity.startActivity(intent);*/
			/*try {
				new UmengShare.OpenShare((Activity) activity,
						MainActivity.mainController);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			/*Intent intent =new Intent(activity, RegisterActivity.class);
			activity.startActivity(intent);*/
			break;
		case R.id.sendtopic_lin:
			// 统计点击次数
			MobclickAgent.onEvent(activity, "16");// 话题马上参与按钮
			if (category.getPublishType()==PublishType.Photos) {
				CustomAbsClass.openTopicTitleList(activity, category,true,true);
			}else {
				CustomAbsClass.openTopicTitleList(activity, category,false,true);
			}
			/*if (category.getPublishType()==PublishType.Photos) {
				new CustomAbsClass.StarEdit() {
					
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						Constants.category=category;
						if (mMediaHelper != null) {
							mMediaHelper.launchMulPicker(Constants.TOPIC_MAX_PHOTO_PER_POST);
						}
					}
				}.StarTopicEdit(activity);

			} else {
				CustomAbsClass.starTopicEditIntent(activity, category.getId(),category.getTitle(), null);
			}*/
			break;

		default:
			break;
		}
	}
}
