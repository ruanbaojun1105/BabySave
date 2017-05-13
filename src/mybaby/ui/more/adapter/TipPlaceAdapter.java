package mybaby.ui.more.adapter;

import java.util.List;

import me.hibb.mybaby.android.R;

import com.amap.api.services.help.Tip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TipPlaceAdapter extends BaseAdapter {

	private Context context;
	private List<Tip> tips;

	public TipPlaceAdapter(Context context) {
		this.context = context;
	}
	
	

	public void setTips(List<Tip> tips) {
		this.tips = tips;
	}



	@Override
	public int getCount() {
		return tips.size();
	}

	@Override
	public Object getItem(int position) {
		return tips.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater = LayoutInflater.from(context);
		TipHolder holder = null;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.item_list, null);
			holder = new TipHolder();
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			convertView.setTag(holder);
		}else{
			holder = (TipHolder) convertView.getTag();
		}
		holder.tv_content.setText(tips.get(position).getName());
		return convertView;
	}

}

class TipHolder {
	
	TextView tv_content;
}
