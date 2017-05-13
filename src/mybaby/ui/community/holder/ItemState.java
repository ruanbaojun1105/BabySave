package mybaby.ui.community.holder;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public abstract class ItemState extends MultiItemEntity  {
//在使用ListView时，如果使用了getItemViewType， 记得他的值一定要是从0开始计数的。 且要覆盖getViewTypeCount方法。
//并且让getViewTypeCount>getItemViewType
//否则会有数组越界异常：
	public static final int MAX_TYPE_SIZE = 11; //总布局数
	public static final int ActivityHolderTYPE_1 = 0; //类型1 动态/热门 类型合并
	public static final int ParentingHolderTYPE_1 = 1; //类型3 育儿
	public static final int PlaceSettingHolderTYPE_2 = 2; //类型4 地点设置
	public static final int TopicPublishHolderTYPE_3 = 3; //类型5 多个话题推荐
	public static final int TopicPublishOneHolderTYPE_4 = 4; //类型6 大话题单个
	public static final int ShareAppHolderTYPE_5 = 5; //类型7 分享
	public static final int HtmlHolderTYPE_6 =6; //类型8 html5支持
	public static final int SmallLinkHolderTYPE_7 = 7; //类型9 link//合并链接形式类型返回
	public static final int BigLinkHolderTYPE_8 = 8; //类型9 link//合并链接形式类型返回
	public static final int BigImageLinkHolderTYPE_9 = 9; //类型9 link//合并链接形式类型返回

	public abstract void onBindViewHolder(Context context,final RecyclerView.ViewHolder vHolder, View parentView,int position,boolean hasVisiPlace, HtmlItem.SetWebViewOnTouchListener listener);
	//RecyclerView.ViewHolder getHolder(ViewGroup parent);
	public abstract int getStateType();
	@Deprecated
	public abstract View getItemView(View convertView, LayoutInflater inflater, Object data, Activity activity, boolean hasVisiPlace, int position, HtmlItem.SetWebViewOnTouchListener listener);
}


