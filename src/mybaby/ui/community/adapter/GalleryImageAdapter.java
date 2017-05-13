package mybaby.ui.community.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import me.hibb.mybaby.android.R;
import mybaby.util.ImageViewUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
/**
 * 读取相册返回的适配器
 * @author baojun
 *
 */
public class GalleryImageAdapter extends BaseAdapter{

	Context context;
	@SuppressWarnings("rawtypes")
	List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
	public  GalleryImageAdapter(Context context,
			List<Map<String, Object>> listData) {
		this.context = context;
		this.listData = listData;
	}
	/**
	 * 获取数据总条数
	 */
	@Override
	public int getCount() {
		return listData.size();
	}

	/**
	 * 获取某一位置的数据
	 */
	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	/**
	 * 获取唯一标识
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("WrongViewCast") @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final String imageUrl= (String) listData.get(position).get("imageUrl");
		ViewHodler viewHodler;
		if (convertView==null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.image_item_notification_in_community, null);
			viewHodler=new ViewHodler();
			viewHodler.image=(ImageView) convertView.findViewById(R.id.image);
			viewHodler.deletImageView=(ImageView) convertView.findViewById(R.id.delect);
			convertView.setTag(viewHodler);
		}else {
			viewHodler=(ViewHodler) convertView.getTag();
		}
		ImageViewUtil.displayImage(imageUrl, viewHodler.image);
		
        return convertView; 
		
	}

	static class ViewHodler{
		ImageView image,deletImageView;
		
	}
}

