package mybaby.ui.Notification.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

import me.hibb.mybaby.android.R;
import mybaby.models.notification.Notification;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.widget.RoundImageViewByXfermode;
import mybaby.util.DateUtils;
import mybaby.util.ImageViewUtil;

public class NotificationAdapter extends BaseQuickAdapter<Notification> {

	private Context context;
	private int unread;

	public NotificationAdapter(Context context, int unread) {
		super(R.layout.notification_message,new ArrayList<Notification>());
		this.context = context;
		this.unread = unread;
	}

	@Override
	protected void convert(BaseViewHolder baseViewHolder, Notification notification) {
		ViewHolders viewHolder=new ViewHolders(baseViewHolder.convertView);
		bindData(baseViewHolder,viewHolder,notification);
	}
	private void bindData(BaseViewHolder baseViewHolder,final ViewHolders viewHolder, final Notification item){
		viewHolder.old_read_line.setVisibility(unread==baseViewHolder.getAdapterPosition()?(unread==0?View.GONE:View.VISIBLE):View.GONE);

		viewHolder.riv_avatar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (item.getType() != Notification.NotificationType.System) {
					CustomAbsClass.starUserPage(context, item.getUser());
				}
			}
		});

		viewHolder.tv_content.setText(item.getUser().getName()+"："+item.getContent());
		viewHolder.tv_time.setText(DateUtils.getCountnumber(item.getDatetime()));
		viewHolder.tv_check.setText("");
		viewHolder.iv_image.setVisibility(View.GONE);
		if(item.getType() == Notification.NotificationType.Like || item.getType() == Notification.NotificationType.Reply || item.getType() == Notification.NotificationType.System){
			viewHolder.iv_image.setVisibility(item.getImage() != null?View.VISIBLE:View.GONE);
			if(item.getImage() != null){
				ImageViewUtil.displayImage(item.getImage().getThumbnailUrl(), viewHolder.iv_image);
			}
		}else{
			viewHolder.iv_image.setVisibility(View.GONE);
			viewHolder.tv_check.setText("查看");
		}

		RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(MyBabyApp.dip2px(50),MyBabyApp.dip2px(50));
		int mar=MyBabyApp.dip2px(5);
		lp.setMargins(mar,mar,mar,mar);
		viewHolder.riv_avatar.setLayoutParams(lp);

		if(item.getUser().getAvatarThumbnailUrl() != null && !("".equals(item.getUser().getAvatarThumbnailUrl()))){
			ImageViewUtil.displayImage(item.getUser().getAvatarThumbnailUrl(), viewHolder.riv_avatar);
		}else{
			ImageViewUtil.displayImage(item.getUser().getNullAvatarUrl(), viewHolder.riv_avatar);
		}
	}

	static class ViewHolders{
		public RoundImageViewByXfermode riv_avatar;
		public TextView tv_content;
		public TextView tv_time;
		public ImageView iv_image;
		public TextView tv_check;
		public Context context;
		public RelativeLayout old_read_line;

		public ViewHolders(View convertView) {
			 riv_avatar = (RoundImageViewByXfermode) convertView.findViewById(R.id.riv_avatar);
			 tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			 tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			 iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
			 tv_check = (TextView) convertView.findViewById(R.id.tv_check);
			 old_read_line = (RelativeLayout) convertView.findViewById(R.id.old_read_line);
		}
	}
}
