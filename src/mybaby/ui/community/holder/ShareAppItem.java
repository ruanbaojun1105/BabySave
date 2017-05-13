package mybaby.ui.community.holder;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import me.hibb.mybaby.android.R;
import mybaby.models.community.item.CommunityShareAppItem;
import mybaby.share.UmengShare;
import mybaby.ui.community.CommunityFragment;

//分享
public class ShareAppItem  extends ItemState {
	public ShareAppItem() {
	}

	//分享
	public static class ShareAppHolder  extends RecyclerView.ViewHolder{
		public Button setBtn;
		public TextView title,desc;

		public ShareAppHolder(View view) {
			super(view);
			setBtn=(Button) view.findViewById(R.id.setbtn);
			desc=(TextView) view.findViewById(R.id.desc);
			title=(TextView) view.findViewById(R.id.title);
		}
	}

	@Override
	public void onBindViewHolder(Context context, RecyclerView.ViewHolder vHolder, View parentView, int position, boolean hasVisiPlace, HtmlItem.SetWebViewOnTouchListener listener) {
		if(vHolder instanceof ShareAppHolder){
			ShareAppHolder holder= (ShareAppHolder) vHolder;
			bindDatas(context, (CommunityShareAppItem) this, holder);
		}
	}

	public static RecyclerView.ViewHolder getHolder(ViewGroup parent) {
		View convertView =LayoutInflater.from(parent.getContext())
				.inflate(R.layout.community_all_listitem_share, parent, false);
		ShareAppHolder holder = new ShareAppHolder(convertView);
		return holder;
	}

	@Override
	public int getStateType() {
		return ItemState.ShareAppHolderTYPE_5;
	}

	@Override
	public View getItemView(View convertView, LayoutInflater inflater, Object data, Activity activity, boolean hasVisiPlace, int parentId, HtmlItem.SetWebViewOnTouchListener listener) {
		ShareAppHolder holder;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.community_all_listitem_share, null);
			holder = new ShareAppHolder(convertView);
			convertView.setTag(R.id.activityitem_share,holder);
		}else{
			holder = (ShareAppHolder)convertView.getTag(R.id.activityitem_share);
		}
		if (data instanceof CommunityShareAppItem)
			bindDatas(activity, (CommunityShareAppItem) data,holder);
		return convertView;
	}

	public void bindDatas(final Context context,final CommunityShareAppItem obj,final ShareAppHolder shareAppHolder) {
		// TODO Auto-generated method stub
		// 主视图容器添加view
		shareAppHolder.title.setText(obj.getTitle());
		shareAppHolder.desc.setText(obj.getDesc());
		shareAppHolder.setBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//TODO Auto-generated method stub
				//统计点击次数
				MobclickAgent.onEvent(context, "19");//马上邀请好友按钮
				try {
					new UmengShare.OpenShare((Activity) context, CommunityFragment.communityController,UmengShare.setShareBean(context));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});	
					
	}

}
