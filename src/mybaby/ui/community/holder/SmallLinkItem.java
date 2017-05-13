package mybaby.ui.community.holder;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xutils.common.util.LogUtil;

import me.hibb.mybaby.android.R;
import mybaby.action.Action;
import mybaby.models.community.Icon;
import mybaby.models.community.Link;
import mybaby.models.community.item.CommunitySmallLinkItem;
import mybaby.ui.main.HomeFragment;
import mybaby.ui.widget.RoundImageViewByXfermode;

public class SmallLinkItem extends ItemState {
	public SmallLinkItem() {
	}

	@Override
	public void onBindViewHolder(Context context, RecyclerView.ViewHolder vHolder, View parentView, int position, boolean hasVisiPlace, HtmlItem.SetWebViewOnTouchListener listener) {
		if(vHolder instanceof SimallLinkHolder){
			SimallLinkHolder holder= (SimallLinkHolder) vHolder;
			bindDatas(context, (CommunitySmallLinkItem) this, holder);
		}
	}

	public static RecyclerView.ViewHolder getHolder(ViewGroup parent) {
		View convertView =LayoutInflater.from(parent.getContext())
				.inflate(R.layout.community_activityitem_smalllink, parent, false);
		SimallLinkHolder holder = new SimallLinkHolder(convertView);
		return holder;
	}

	@Override
	public int getStateType() {
		return ItemState.SmallLinkHolderTYPE_7;
	}

	@Override
	public View getItemView(View convertView, LayoutInflater inflater, Object data, Activity activity, boolean hasVisiPlace, int parentId, HtmlItem.SetWebViewOnTouchListener listener) {
		if (data instanceof CommunitySmallLinkItem){

			SimallLinkHolder holder=null;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.community_activityitem_smalllink, null);
				holder = new SimallLinkHolder(convertView);
				convertView.setTag(R.id.activityitem_link_small,holder);
			}else{
				holder = (SimallLinkHolder)convertView.getTag(R.id.activityitem_link_small);
			}
			bindDatas(activity, (CommunitySmallLinkItem) data, holder);
			return convertView;

		}else
			return null;
	}

	public void bindDatas(final Context context, final CommunitySmallLinkItem obj, final SimallLinkHolder holder) {

		if (holder==null)
			return;
		final Icon icon=obj.getIcon();
		holder.titile_rela.setVisibility(icon == null ? View.GONE : View.VISIBLE);
		View.OnClickListener onClickListener=null;
		if (icon!=null) {
			ActivityItem.setHeadContent(holder.avatarThumbnailUrl, holder.tv_name, icon.getImage_url(), icon.getName());
			onClickListener=new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Action.createAction(icon.getAction(), icon.getName(), null).excute((Activity)context, HomeFragment.homeController, null, null);
					} catch (Exception e) {
						e.printStackTrace();
						LogUtil.e("action执行异常");
					}
				}
			};
			holder.titile_rela.setOnClickListener(onClickListener);
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
	}
	public static class SimallLinkHolder extends  RecyclerView.ViewHolder{

		RelativeLayout titile_rela;
		RoundImageViewByXfermode avatarThumbnailUrl ;
		TextView tv_name;
		TextView edit_menu_tag;
		TextView tv_content;

		RelativeLayout link_rela;
		TextView link_desc;
		ImageView link_image;

		public SimallLinkHolder(View view) {
			super(view);
			titile_rela=(RelativeLayout) view.findViewById(R.id.titile_rela);
			avatarThumbnailUrl =(RoundImageViewByXfermode) view.findViewById(R.id.avatarThumbnailUrl);
			tv_name= (TextView) view.findViewById(R.id.name);
			tv_content= (TextView) view.findViewById(R.id.content_smalllink);
			edit_menu_tag= (TextView) view.findViewById(R.id.edit_menu_tag);

			link_rela=(RelativeLayout) view.findViewById(R.id.link_item);
			link_image=(ImageView) view.findViewById(R.id.link_image);
			link_desc=(TextView) view.findViewById(R.id.link_desc);
		}
	}

}
