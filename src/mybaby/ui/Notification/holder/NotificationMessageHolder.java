package mybaby.ui.Notification.holder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import me.hibb.mybaby.android.R;
import mybaby.models.notification.Notification;
import mybaby.models.notification.Notification.NotificationType;
import mybaby.ui.more.BaseHolder;
import mybaby.ui.more.PersonPageActivity;
import mybaby.ui.widget.RoundImageViewByXfermode;
import mybaby.util.DateUtils;
import mybaby.util.ImageViewUtil;

public class NotificationMessageHolder extends BaseHolder<Notification> {
	
	private RoundImageViewByXfermode riv_avatar;
	private TextView tv_content;
	private TextView tv_time;
	private ImageView iv_image;
	private TextView tv_check;
	private Context context;
	private RelativeLayout old_read_line;

	private int unread;
	private int position;

	public NotificationMessageHolder(Context context,int unread,int position) {
		this.context = context;
		this.unread = unread;
		this.position = position;
	}

	@Override
	public void refreshView() {
		final Notification item = getData();
		
		if(item.getUser().getAvatarThumbnailUrl() != null && !("".equals(item.getUser().getAvatarThumbnailUrl()))){
			ImageViewUtil.displayImage(item.getUser().getAvatarThumbnailUrl(), riv_avatar);
		}else{
			ImageViewUtil.displayImage(item.getUser().getNullAvatarUrl(), riv_avatar);
		}
		if(item.getType() != NotificationType.System){
			riv_avatar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, PersonPageActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("user", item.getUser());
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			});
		}else{
			riv_avatar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
				}
			});
		}

		if (unread==position)
			old_read_line.setVisibility(View.VISIBLE);
		else old_read_line.setVisibility(View.GONE);

		tv_content.setText(item.getUser().getName()+"："+item.getContent());
		tv_time.setText(DateUtils.getCountnumber(item.getDatetime()));
		tv_check.setVisibility(View.GONE);
		iv_image.setVisibility(View.GONE);
		if(item.getType() == NotificationType.Like || item.getType() == NotificationType.Reply || item.getType() == NotificationType.System){
			if(item.getImage() != null){
				iv_image.setVisibility(View.VISIBLE);
				ImageViewUtil.displayImage(item.getImage().getThumbnailUrl(), iv_image);
			}
		}else{
			iv_image.setVisibility(View.GONE);
			tv_check.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public View initView() {
		View view = ((LayoutInflater)context.getSystemService
				(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.notification_message, null);
		riv_avatar = (RoundImageViewByXfermode) view.findViewById(R.id.riv_avatar);
		tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_time = (TextView) view.findViewById(R.id.tv_time);
		iv_image = (ImageView) view.findViewById(R.id.iv_image);
		tv_check = (TextView) view.findViewById(R.id.tv_check);
		old_read_line = (RelativeLayout) view.findViewById(R.id.old_read_line);
		return view;
	}

}
