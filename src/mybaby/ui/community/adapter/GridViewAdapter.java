package mybaby.ui.community.adapter;

import java.util.List;

import mybaby.util.ImageViewUtil;
import mybaby.util.Utils;

import me.hibb.mybaby.android.R;
import mybaby.models.community.Image;
import mybaby.ui.community.ImagePageActivity;
import mybaby.ui.community.holder.ActivityItem;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
public class GridViewAdapter extends BaseAdapter{

	Context context;
	@SuppressWarnings("rawtypes")
	List<String> restPaths;
	public  GridViewAdapter(Context context,
			List<String> restPaths) {
		this.context = context;
		this.restPaths = restPaths;
	}
	/**
	 * 获取数据总条数
	 */
	@Override
	public int getCount() {
		return restPaths.size();
	}

	/**
	 * 获取某一位置的数据
	 */
	@Override
	public Object getItem(int position) {
		return restPaths.get(position);
	}

	/**
	 * 获取唯一标识
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolders viewHolder;
        if (convertView == null) { 
        	viewHolder=new ViewHolders();
        	convertView=LayoutInflater.from(context).inflate(R.layout.gridview_item, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        }  
        else { 
            viewHolder = (ViewHolders) convertView.getTag(); 
        } 
        ImageViewUtil.displayImage("file://"+restPaths.get(position), viewHolder.imageView);
        viewHolder.imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Object[] imagStrings= restPaths.toArray();
				Intent intent = new Intent(context,ImagePageActivity.class);
				intent.putExtra("count", imagStrings.length);
				intent.putExtra("index", position);
				intent.putExtra("images", imagStrings);
				context.startActivity(intent);
			}
		});
        return convertView; 
		
	}

	static class ViewHolders{
		ImageView imageView; 
		
	}
}
