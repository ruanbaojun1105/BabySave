package mybaby.ui.community.holder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import me.hibb.mybaby.android.R;
import mybaby.models.community.ParentingPost;
import mybaby.models.community.item.CommunityParentingItem;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.util.ImageViewUtil;

//育儿百科类型
public class ParentingItem  extends ItemState implements OnClickListener{

	public View view = null;
	public Context context;
	
	public ParentingItem() {
		// TODO Auto-generated constructor stub
	}
	public static class ParentingHolder  extends RecyclerView.ViewHolder{
		public LinearLayout list_parenting;//列表
		public TextView more_btn;

		public ParentingHolder(View view) {
			super(view);
			list_parenting=(LinearLayout) view.findViewById(R.id.parenting_post_row);
			more_btn=(TextView) view.findViewById(R.id.more);
		}
	}
	@Override
	public void onBindViewHolder(Context context, RecyclerView.ViewHolder vHolder, View parentView, int position, boolean hasVisiPlace, HtmlItem.SetWebViewOnTouchListener listener) {
		if(vHolder instanceof ParentingHolder){
			ParentingHolder holder= (ParentingHolder) vHolder;
			bindDatas(context, (CommunityParentingItem) this, holder);
		}
	}

	public static RecyclerView.ViewHolder getHolder(ViewGroup parent) {
		View convertView =LayoutInflater.from(parent.getContext())
				.inflate(R.layout.community_all_listitem_parenting, parent, false);
		ParentingHolder holder = new ParentingHolder(convertView);
		return holder;
	}
	@Override
	public int getStateType() {
		return ItemState.ParentingHolderTYPE_1;
	}

	@Override
	public View getItemView(View convertView, LayoutInflater inflater, Object data, Activity activity, boolean hasVisiPlace, int parentId, HtmlItem.SetWebViewOnTouchListener listener) {
		ParentingHolder holder;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.community_all_listitem_parenting, null);
			holder = new ParentingHolder(convertView);
			convertView.setTag(R.id.activityitem_parenting,holder);
		}else{
			holder = (ParentingHolder)convertView.getTag(R.id.activityitem_parenting);
		}
		if (data instanceof CommunityParentingItem)
			bindDatas(activity, (CommunityParentingItem) data,holder);
		return convertView;
	}

	public void	 bindDatas(Context context ,final CommunityParentingItem obj,ParentingHolder vholder) {
		// TODO Auto-generated method stub
		this.context=context;
		//主视图容器添加view
		if (obj.getParentingPosts()==null) return;
		setLinGreat(obj.getParentingPosts(),vholder.list_parenting);
		vholder.more_btn.setOnClickListener(this);
	}

	private View setData(View itemView,final ParentingPost obj,boolean isHead){
		TextView title=(TextView) itemView.findViewById(R.id.title);
		RoundedImageView featuredImage=(RoundedImageView) itemView.findViewById(R.id.featuredImage);
		title.setTextColor(isHead? Color.WHITE:Color.BLACK);
		title.setText(obj.getTitle());
		itemView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(obj.getDetailUrl())) {
					CustomAbsClass.starParentingIntent(context);
				} else {
					CustomAbsClass.starParentingWebViewIntent(context, obj);
				}
			}
		});
		ImageViewUtil.displayImage(obj.getFeaturedImageUrl(), featuredImage);
		title.bringToFront();
		return itemView;
	}
	private View getItemView(boolean isHead){
		return LayoutInflater.from(context).inflate(isHead?R.layout.community_parenting_item_sp:R.layout.community_parenting_item, null);
	}
	/**
	 * 复用优化
	 * @param posts
	 * @param lin
	 */
	private void setLinGreat(final ParentingPost[] posts,LinearLayout lin){
		if (lin.getChildCount()==0){
			for (int y=0; y < posts.length; y++) {
				lin.addView(getItemView(y==0));
			}
		}else {
			int oldViewCount = lin.getChildCount();
			int newViewCount = posts.length;
			if (oldViewCount > newViewCount) {
				lin.removeViews(newViewCount - 1, oldViewCount - newViewCount);
			} else if (oldViewCount < newViewCount) {
				for (int i = 0; i < newViewCount - oldViewCount; i++) {
					lin.addView(getItemView(false));
				}
			}
		}
		int linCount = lin.getChildCount();
		for (int i = 0; i <linCount; i++) {
			setData(lin.getChildAt(i),posts[i],i==0);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.more:
			CustomAbsClass.starParentingIntent(context);
			break;
		}
	}

}
