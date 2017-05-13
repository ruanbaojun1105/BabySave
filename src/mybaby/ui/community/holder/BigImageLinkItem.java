package mybaby.ui.community.holder;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import org.xutils.common.util.LogUtil;

import me.hibb.mybaby.android.R;
import mybaby.action.Action;
import mybaby.models.community.item.CommunityBigImageLinkItem;
import mybaby.ui.main.HomeFragment;
import mybaby.util.ImageViewUtil;

public class BigImageLinkItem extends ItemState {
	public BigImageLinkItem() {
	}
	@Override
	public void onBindViewHolder(Context context, RecyclerView.ViewHolder vHolder, View parentView, int position, boolean hasVisiPlace, HtmlItem.SetWebViewOnTouchListener listener) {
		if(vHolder instanceof BigImageHolder){
			BigImageHolder holder= (BigImageHolder) vHolder;
			bindDatas(context, (CommunityBigImageLinkItem) this, holder);
		}

	}

	public static RecyclerView.ViewHolder getHolder(ViewGroup parent) {
		View convertView =LayoutInflater.from(parent.getContext())
				.inflate(R.layout.community_activityitem_bigimagelink, parent, false);
		BigImageHolder holder = new BigImageHolder(convertView);
		return holder;
	}
	@Override
	public int getStateType() {
		return ItemState.BigImageLinkHolderTYPE_9;
	}

	@Override
	public View getItemView(View convertView, LayoutInflater inflater, Object data, Activity activity, boolean hasVisiPlace, int position, HtmlItem.SetWebViewOnTouchListener listener) {
		BigImageHolder holder=null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.community_activityitem_bigimagelink, null);
			holder = new BigImageHolder(convertView);
			convertView.setTag(R.id.activityitem_image_big,holder);
		}else{
			holder = (BigImageHolder)convertView.getTag(R.id.activityitem_image_big);
		}
		bindDatas(activity, (CommunityBigImageLinkItem) data, holder);
		return convertView;
	}

	public void bindDatas(final Context context, final CommunityBigImageLinkItem obj, final BigImageHolder holder) {
		if (holder==null)
			return;
		/*Transformation transformation = new RoundedTransformationBuilder()
				.borderColor(Color.BLUE)
				.borderWidth(2)
				.cornerRadiusDp(10)
				.oval(false)
				.build();

		Picasso.with(context)
				.load(obj.getImage_url())
				.fit()
				.placeholder(R.drawable.btn_bg_gray)
				.transform(transformation)
				.into(holder.bigimage_image);*/
		ImageViewUtil.displayImage(obj.getImage_url(),holder.bigimage_image,null);
		holder.bigimage_title.setText(obj.getTitle());
		holder.bigimage_title.setVisibility(TextUtils.isEmpty(obj.getTitle()) ? View.GONE : View.VISIBLE);
		holder.bigimage_desc.setText(obj.getDesc());
		holder.bigimage_desc.setVisibility(TextUtils.isEmpty(obj.getDesc()) ? View.GONE : View.VISIBLE);
		holder.bigimage_lin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Action.createAction(obj.getAction(), obj.getTitle(), null).excute((Activity)context, HomeFragment.homeController, null, null);
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.e("action执行异常");
				}
			}
		});
	}
	public static class BigImageHolder extends RecyclerView.ViewHolder{
		LinearLayout bigimage_lin;
		RoundedImageView bigimage_image;
		TextView bigimage_title;
		TextView bigimage_desc;

		public BigImageHolder(View view) {
			super(view);
			bigimage_lin=(LinearLayout) view.findViewById(R.id.bigimage_lin);
			bigimage_image=(RoundedImageView ) view.findViewById(R.id.bigimage_image);
			bigimage_title=(TextView) view.findViewById(R.id.bigimage_title);
			bigimage_desc=(TextView) view.findViewById(R.id.bigimage_desc);
		}
	}

}
