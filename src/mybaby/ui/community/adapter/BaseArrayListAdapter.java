package mybaby.ui.community.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class BaseArrayListAdapter<T> extends BaseAdapter {

protected ArrayList<T> data;

@Override
public int getCount() {
// TODO Auto-generated method stub
return data == null ? 0 : data.size(); 
}

@Override
public Object getItem(int position) {
// TODO Auto-generated method stub
return position;
}

@Override
public long getItemId(int position) {
// TODO Auto-generated method stub
return position;
}

@Override
public View getView(int position, View convertView, ViewGroup parent) {
// TODO Auto-generated method stub
return getViewHolder(convertView, parent, position).getConvertView();
}
public abstract ViewHolder getViewHolder(View convertView, ViewGroup parent, int position);

static class ViewHolder {
private final SparseArray<View> views;
private View convertView;

private ViewHolder(Context mContext, View convertView) {
this.views = new SparseArray<View>();
this.convertView = convertView;
convertView.setTag(this);
}

public static ViewHolder get(Context mContext, View convertView, ViewGroup parent, int resId) {
if(convertView == null) {
LayoutInflater factory = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
convertView = factory.inflate(resId, parent, false);//
return new ViewHolder(mContext, convertView);
} 
return (ViewHolder) convertView.getTag();
}

@SuppressWarnings("unchecked")
public <T extends View> T findViewById(int resourceId) {
View view = views.get(resourceId);
if (view == null) {
view = convertView.findViewById(resourceId);
views.put(resourceId, view);
}
return (T) view;
}

public View getConvertView() {
return convertView;
}
public void setConvertView(View convertView) {
this.convertView = convertView;
}
}
}