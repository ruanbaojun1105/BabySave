package mybaby.ui.more.holder;

import android.view.View;
import android.widget.TextView;
import me.hibb.mybaby.android.R;
import mybaby.models.community.Place;
import mybaby.ui.more.BaseHolder;
import mybaby.util.AppUIUtils;
/**
 * 地点话题的布局
 * @author Administrator
 *
 */
public class PlaceTopicHolder extends BaseHolder<Place> {

	private TextView tv_content;
	
	@Override
	public void refreshView() {
		Place place = getData();
		tv_content.setText(place.getPlace_name());
	}

	@Override
	public View initView() {
		View view = AppUIUtils.inflate(R.layout.item_list);
		tv_content = (TextView) view.findViewById(R.id.tv_content);
		return view;
	}

}
