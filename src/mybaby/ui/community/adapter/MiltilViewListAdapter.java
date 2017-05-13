package mybaby.ui.community.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import mybaby.ui.community.holder.HtmlItem;
import mybaby.ui.community.holder.ItemState;

public class MiltilViewListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<? extends ItemState> mItemBeanList;
	private Context context;
	private boolean hasVisiPlace=true;//默认显示地理位置
	private HtmlItem.SetWebViewOnTouchListener listener;//浏览器的滑动处理，请不要使用全局变量，防止内存泄露
	 

	public MiltilViewListAdapter(List<? extends ItemState> mItemBeanList, Context context, boolean hasVisiPlace, HtmlItem.SetWebViewOnTouchListener listener) {
		this.mInflater = LayoutInflater.from(context);
		this.mItemBeanList = mItemBeanList;
		this.context = context;
		this.hasVisiPlace = hasVisiPlace;
		this.listener = listener;
	}

	@Override
	public int getCount() {
		if (mItemBeanList != null) {
			return mItemBeanList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mItemBeanList != null && position < mItemBeanList.size()
				&& position >= 0) {
			return mItemBeanList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemState itemBean = mItemBeanList.get(position);
		convertView = itemBean.getItemView(convertView, mInflater, itemBean,(Activity)context, hasVisiPlace,position,listener);
		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		if (mItemBeanList == null) {
			return 0;
		}
		if (position < 0 || position >= mItemBeanList.size()) {
			return 0;
		}
		return mItemBeanList.get(position).getStateType();
	}

	@Override
	public int getViewTypeCount() {
		return ItemState.MAX_TYPE_SIZE;
	}

}
