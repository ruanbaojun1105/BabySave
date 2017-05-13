package mybaby.ui.more.adapter;

import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.models.community.Place;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NormalPlaceAdapter extends BaseAdapter {

	private Context context;
	private List<Place> places;

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	public NormalPlaceAdapter(Context context, List<Place> places) {
		this.context = context;
		this.places = places;
	}

	public NormalPlaceAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return places.size();
	}

	@Override
	public Object getItem(int position) {
		return places.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater = LayoutInflater.from(context);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_list, null);
			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_content.setText(places.get(position).getPlace_name());

		return convertView;

	}

}

class ViewHolder {
	TextView tv_content;
}
