package mybaby.ui.community.holder;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import org.xutils.common.util.LogUtil;

import me.hibb.mybaby.android.R;
import mybaby.action.Action;
import mybaby.models.community.Icon;
import mybaby.models.community.Link;
import mybaby.models.community.item.CommunityBigLinkItem;
import mybaby.ui.main.HomeFragment;
import mybaby.ui.widget.RoundImageViewByXfermode;

public class BigLinkItem extends ItemState {
	public BigLinkItem() {
	}
	@Override
	public void onBindViewHolder(Context context, RecyclerView.ViewHolder vHolder, View parentView, int position, boolean hasVisiPlace, HtmlItem.SetWebViewOnTouchListener listener) {
		if(vHolder instanceof BigHolder){
			BigHolder holder= (BigHolder) vHolder;
			bindDatas(context, (CommunityBigLinkItem) this, holder);
		}

	}

	public static RecyclerView.ViewHolder getHolder(ViewGroup parent) {
		View convertView =LayoutInflater.from(parent.getContext())
				.inflate(R.layout.community_activityitem_biglink, parent, false);
		BigHolder holder = new BigHolder(convertView);
		return holder;
	}
	@Override
	public int getStateType() {
		return ItemState.BigLinkHolderTYPE_8;
	}

	@Override
	public View getItemView(View convertView, LayoutInflater inflater, Object data, Activity activity, boolean hasVisiPlace, int parentId, HtmlItem.SetWebViewOnTouchListener listener) {
		BigHolder holder=null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.community_activityitem_biglink, null);
			holder = new BigHolder(convertView);
			convertView.setTag(R.id.activityitem_link_big,holder);
		}else{
			holder = (BigHolder)convertView.getTag(R.id.activityitem_link_big);
		}
		bindDatas(activity, (CommunityBigLinkItem) data, holder);
		return convertView;
	}

	public void bindDatas(final Context context, final CommunityBigLinkItem obj, final BigHolder holder) {
		if (holder==null)
			return;
		final Icon icon=obj.getIcon();
		holder.titile_rela.setVisibility(icon == null ? View.GONE : View.VISIBLE);
		if (icon!=null) {
			ActivityItem.setHeadContent(holder.avatarThumbnailUrl, holder.tv_name, icon.getImage_url(), icon.getName());
			holder.titile_rela.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Action.createAction(icon.getAction(), icon.getName(), null).excute((Activity)context, HomeFragment.homeController, null, null);
					} catch (Exception e) {
						e.printStackTrace();
						LogUtil.e("action执行异常");
					}
				}
			});
		}
		final Link link = obj.getLink();
		holder.link_rela.setVisibility(link == null ? View.GONE : View.VISIBLE);
		if (link!=null) {
			ActivityItem.madeLinksLin((Activity) context, link, holder.link_rela, holder.link_image, holder.link_desc);
			holder.tv_content.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Action.createAction(link.getAction(), link.getDesc(), null).excute((Activity)context, HomeFragment.homeController, null, null);
					} catch (Exception e) {
						e.printStackTrace();
						LogUtil.e("action执行异常");
					}
				}
			});
		}
		holder.tv_content.setText(obj.getContent());
		holder.edit_menu_tag.setText("");
		holder.line_parenting.setVisibility(View.GONE);
	}

	public static class BigHolder extends RecyclerView.ViewHolder {

		RelativeLayout titile_rela;
		RoundImageViewByXfermode avatarThumbnailUrl ;
		TextView tv_name;
		TextView edit_menu_tag;
		TextView tv_content;

		LinearLayout link_rela;
		TextView link_desc;
		RoundedImageView link_image;
		View line_parenting;

		public BigHolder(View view) {
			super(view);
			titile_rela=(RelativeLayout) view.findViewById(R.id.titile_rela);
			avatarThumbnailUrl =(RoundImageViewByXfermode) view.findViewById(R.id.avatarThumbnailUrl);
			tv_name= (TextView) view.findViewById(R.id.name);
			tv_content= (TextView) view.findViewById(R.id.content_smalllink);
			edit_menu_tag= (TextView) view.findViewById(R.id.edit_menu_tag);

			link_rela=(LinearLayout) view.findViewById(R.id.parenting_item_rela);
			link_image=(RoundedImageView) view.findViewById(R.id.featuredImage);
			link_desc=(TextView) view.findViewById(R.id.title);
			line_parenting=view.findViewById(R.id.line_parenting);
		}
	}
}
