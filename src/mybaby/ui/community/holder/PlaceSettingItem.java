package mybaby.ui.community.holder;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import me.hibb.mybaby.android.R;
import mybaby.models.community.item.CommunityPlaceSettingItem;
import mybaby.ui.community.customclass.CustomAbsClass;

//地点类型
public class PlaceSettingItem  extends ItemState {
	public PlaceSettingItem() {
		// TODO Auto-generated constructor stub
	}
	//地点设置
	public static class PlaceSettingHolder extends RecyclerView.ViewHolder{
		public Button setBtn;
		public TextView title,desc,note;

		public PlaceSettingHolder(View view) {
			super(view);
			title=(TextView) view.findViewById(R.id.title);
			desc=(TextView) view.findViewById(R.id.desc);
			note=(TextView) view.findViewById(R.id.note);
			setBtn=(Button) view.findViewById(R.id.button1);
		}
	}
	@Override
	public void onBindViewHolder(Context context, RecyclerView.ViewHolder vHolder, View parentView, int position, boolean hasVisiPlace, HtmlItem.SetWebViewOnTouchListener listener) {
		if(vHolder instanceof PlaceSettingHolder){
			PlaceSettingHolder holder= (PlaceSettingHolder) vHolder;
			bindDatas(context, (CommunityPlaceSettingItem) this, holder);
		}
	}

	public static RecyclerView.ViewHolder getHolder(ViewGroup parent) {
		View convertView =LayoutInflater.from(parent.getContext())
				.inflate(R.layout.community_all_listitem_place, parent, false);
		PlaceSettingHolder holder = new PlaceSettingHolder(convertView);
		return holder;
	}

	@Override
	public int getStateType() {
		return ItemState.PlaceSettingHolderTYPE_2;
	}

	@Override
	public View getItemView(View convertView, LayoutInflater inflater, Object data, Activity activity, boolean hasVisiPlace, int parentId, HtmlItem.SetWebViewOnTouchListener listener) {
		PlaceSettingHolder holder;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.community_all_listitem_place, null);
			holder = new PlaceSettingHolder(convertView);
			convertView.setTag(R.id.activityitem_placesetting,holder);
		}else{
			holder = (PlaceSettingHolder)convertView.getTag(R.id.activityitem_placesetting);
		}
		if (data instanceof CommunityPlaceSettingItem)
			bindDatas(activity, (CommunityPlaceSettingItem) data,holder);
		return convertView;
	}

	public void bindDatas(final Context context,final CommunityPlaceSettingItem obj,PlaceSettingHolder vholder) {
		// TODO Auto-generated method stub
					vholder.title.setText(obj.getTitle());
					vholder.desc.setText(obj.getDesc());
					if (TextUtils.isEmpty(obj.getNote())) {
						vholder.note.setVisibility(View.GONE);
					}else {
						vholder.note.setVisibility(View.VISIBLE);
						vholder.note.setText(obj.getNote());
					}
					vholder.setBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							//TODO Auto-generated method stub
							//统计点击次数
							MobclickAgent.onEvent(context, "18");//地点马上设置按钮

							CustomAbsClass.startPlaceSetting((Activity) context, obj.getSetting(), 0, 0, true);
						}
					});
	}

}
