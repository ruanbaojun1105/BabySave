package mybaby.ui.Notification.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.common.util.LogUtil;

import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.action.Action;
import mybaby.models.notification.NotificationCategory;
import mybaby.rpc.BaseRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.main.MainUtils;
import mybaby.ui.main.NotificationCategoryFragment;
import mybaby.ui.widget.CircleImageView;
import mybaby.util.DateUtils;
import mybaby.util.ImageViewUtil;

public class NotificationCategoryAdapter extends BaseQuickAdapter<NotificationCategory> {

	private Context context;
	@SuppressWarnings("rawtypes")
	public NotificationCategoryAdapter(Context context,List<NotificationCategory> list) {
		super(R.layout.notification_category,list);
		this.context = context;
	}

	/**
	 * 获取列表所有未读消息总和
	 */
	public int getUnReadCount() {
		int unread=0;
		for(NotificationCategory category:(List<NotificationCategory>)getData())
			unread+=category.isStrongRemind() ?category.getUnreadCount():0;
		//读取完未读后缓存
		return unread;
	}


	@Override
	protected void convert(BaseViewHolder baseViewHolder, final NotificationCategory category) {
		/*ViewHolders viewHolder=new ViewHolders(baseViewHolder.convertView);
		viewHolder.notification_title.setText("");
		viewHolder.notification_badge.setText("");
		viewHolder.notification_content.setText("");
		viewHolder.notification_time.setText("");
		viewHolder.notification_time.setText(DateUtils.getCountnumber(category.getNewestDatetime_gmt()));
		viewHolder.notification_content.setText(content);
		viewHolder.notification_content.setVisibility(TextUtils.isEmpty(category.getNewestDesc()) ? View.GONE : View.VISIBLE);
		viewHolder.notification_title.setText(category.getTitle());
		setTextBgRedUnread(viewHolder.notification_badge, category.getUnreadCount(), category.isStrongRemind());
		ImageViewUtil.displayImage(category.getImageUrl(), viewHolder.notification_image);*/
		String content=category.getNewestDesc();
		if (content.contains("/:"))
			content="[表情]";
		baseViewHolder.setText(R.id.tv_notification_lasttime, DateUtils.getCountnumber(category.getNewestDatetime_gmt()));
		baseViewHolder.setText(R.id.tv_notification_title, category.getTitle());
		baseViewHolder.setText(R.id.tv_notification_content,content);
		baseViewHolder.setVisible(R.id.tv_notification_content, TextUtils.isEmpty(category.getNewestDesc()) ? false : true);
		setTextBgRedUnread((TextView) baseViewHolder.getView(R.id.tv_notification_badge), category.getUnreadCount(), category.isStrongRemind());
		ImageViewUtil.displayImage(category.getImageUrl(),(CircleImageView)baseViewHolder.getView(R.id.riv_notification_image));
		baseViewHolder.convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					System.gc();
					//判断登陆
					if (!Constants.hasLoginOpenIM)
						MainUtils.loginIM();
					//执行action
					Action.createAction(category.getAction(), category.getTitle(), category.getUnreadCount()).excute((Activity)context, NotificationCategoryFragment.newsController, null, null);
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.e("action执行异常");
				}
			}
		});
	}

	static class ViewHolders{
		TextView notification_title,notification_time,notification_content,notification_badge;
		ImageView notification_image;
		RelativeLayout notification_item;

		public ViewHolders(View convertView) {
			 notification_image= (ImageView) convertView.findViewById(R.id.riv_notification_image);
			 notification_title= (TextView) convertView.findViewById(R.id.tv_notification_title);
			 notification_content= (TextView) convertView.findViewById(R.id.tv_notification_content);
			 notification_time= (TextView) convertView.findViewById(R.id.tv_notification_lasttime);
			 notification_badge= (TextView) convertView.findViewById(R.id.tv_notification_badge);
			 notification_item= (RelativeLayout) convertView.findViewById(R.id.notification_item);
		}
	}
	public static void setNotificationTextBgRedUnread(final TextView badge, final int badgeNumber,boolean isStrong){
		setTextBgRedUnread(badge,badgeNumber,isStrong);
	}
	public static void setTextBgRedUnread(final TextView badge, final int badgeNumber, final boolean isStrong, final int textColor, final int resBg){
		badge.bringToFront();
		final BaseRPC.Callback callback=new BaseRPC.Callback() {
			@Override
			public void onSuccess() {
				badge.setVisibility(View.VISIBLE);
				badge.setBackgroundResource(resBg);
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) badge.getLayoutParams();
				params.height=MyBabyApp.dip2px(18);
				params.width=MyBabyApp.dip2px(badgeNumber>99?25:18);
				badge.setText(String.valueOf(badgeNumber));
				badge.setMinWidth(MyBabyApp.dip2px(18));
				badge.setTextColor(badge.getContext().getResources().getColor(textColor));
				//badge.setTextSize(Utils.sp2px(context, 9));
				badge.setPadding(3,3,3,3);
				badge.requestLayout();
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				badge.setVisibility(View.VISIBLE);
				badge.setBackgroundResource(resBg);
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) badge.getLayoutParams();
				params.height=MyBabyApp.dip2px(10);
				params.width=MyBabyApp.dip2px(10);
				badge.setTextColor(badge.getContext().getResources().getColor(textColor));
				badge.setPadding(1,1,1,1);
				badge.setText(null);
				badge.requestLayout();
			}
		};
		badge.post(new Runnable() {
			@Override
			public void run() {
				if (badgeNumber > 0) {
					if (isStrong)
						callback.onSuccess();
					else callback.onFailure(0,null);
				} else if (badgeNumber < 0) {
					callback.onFailure(0,null);
				} else {
					badge.setVisibility(View.GONE);
				}
			}
		});
	}
	public static void setTextBgRedUnread(final TextView badge, final int badgeNumber, final boolean isStrong){
		badge.bringToFront();
		final BaseRPC.Callback callback=new BaseRPC.Callback() {
			@Override
			public void onSuccess() {
				badge.setVisibility(View.VISIBLE);
				badge.setBackgroundResource(R.drawable.badge_bg_red);
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) badge.getLayoutParams();
				params.height=MyBabyApp.dip2px(18);
				params.width=MyBabyApp.dip2px(badgeNumber>99?25:18);
				badge.setText(String.valueOf(badgeNumber));
				badge.setMinWidth(MyBabyApp.dip2px(18));
				//badge.setTextSize(Utils.sp2px(context, 9));
				badge.setPadding(3,3,3,3);
				badge.requestLayout();
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				badge.setVisibility(View.VISIBLE);
				badge.setBackgroundResource(R.drawable.badge_bg_red_radiu);
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) badge.getLayoutParams();
				params.height=MyBabyApp.dip2px(10);
				params.width=MyBabyApp.dip2px(10);
				badge.setPadding(1,1,1,1);
				badge.setText(null);
				badge.requestLayout();
			}
		};
		badge.post(new Runnable() {
			@Override
			public void run() {
				if (badgeNumber > 0) {
					if (isStrong)
						callback.onSuccess();
					else callback.onFailure(0,null);
				} else if (badgeNumber < 0) {
					callback.onFailure(0,null);
				} else {
					badge.setVisibility(View.GONE);
				}
			}
		});
	}
	/**
	 * 同时支持数字和文字
	 */
	public static void setTextBgRedUnread(final TextView badge, final String badgeText,boolean isStrong){
		if (TextUtils.isEmpty(badgeText))
			return;
		if (TextUtils.isDigitsOnly(badgeText)){
			setTextBgRedUnread(badge,Integer.parseInt(badgeText),isStrong);
			return;
		}
		badge.post(new Runnable() {
			@Override
			public void run() {
				badge.bringToFront();
				badge.setLines(1);
				if (badgeText.length()>4)
					badge.setEllipsize(TextUtils.TruncateAt.END);
				badge.setGravity(Gravity.CENTER);
				badge.setEms(badgeText.length()>4?4:(badgeText.length()+1));
				badge.setVisibility(View.VISIBLE);
				badge.setBackgroundResource(R.drawable.badge_bg_red);
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) badge.getLayoutParams();
				params.height=MyBabyApp.dip2px(18);
				//params.width=MyBabyApp.dip2px(badgeText.length()>2?50:30);
				params.width=RelativeLayout.LayoutParams.WRAP_CONTENT;
				badge.setText(badgeText);
				//badge.setMinWidth(MyBabyApp.dip2px(badgeText.length()>2?50:30));
				badge.setMaxWidth(MyBabyApp.dip2px(55));
				badge.requestLayout();
			}
		});
	}
}
