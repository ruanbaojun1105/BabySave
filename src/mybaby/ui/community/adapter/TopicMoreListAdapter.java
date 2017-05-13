package mybaby.ui.community.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import me.hibb.mybaby.android.R;
import mybaby.models.community.TopicCategory;
/**
 * 
 * @author baojun
 * @date 07 31 2015
 *
 */
public class TopicMoreListAdapter extends BaseAdapter{

	private TopicCategory [] topiccategory;
	private Context context;
	public TopicMoreListAdapter(TopicCategory [] topiccategory,Context context){
		this.topiccategory=topiccategory;
		this.context=context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return topiccategory.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return topiccategory[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final viewListHolder vHolder;
		if (arg1==null) {
			vHolder=new viewListHolder();
			arg1=LayoutInflater.from(context).inflate(
					R.layout.topicmore_textitem, null);
			
			vHolder.title=(TextView) arg1.findViewById(R.id.more_item_tv);
			arg1.setTag(vHolder);
		}else {
			vHolder=(viewListHolder) arg1.getTag();
		}
		//vHolder.title.setLayoutParams(layoutParams);
		vHolder.title.setTextSize(20);
		vHolder.title.setTextColor(context.getResources().getColor(R.color.blue));
		if (topiccategory[arg0].getTitle().length()>30) {
			vHolder.title.setText(topiccategory[arg0].getTitle().substring(0, 30)+"...");
		}else {
			vHolder.title.setText(topiccategory[arg0].getTitle());
		}
		
		return arg1;
	}

	static class viewListHolder {
		// TODO Auto-generated method stub
		TextView title;
	}
	
	
}
