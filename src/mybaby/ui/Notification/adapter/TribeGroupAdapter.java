package mybaby.ui.Notification.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.notification.TribeGroup;
import mybaby.util.ImageViewUtil;

public class TribeGroupAdapter extends BaseQuickAdapter<TribeGroup> {
	public TribeGroupAdapter(){
		super(R.layout.item_tribe,new ArrayList<TribeGroup>());
	}

	@Override
	protected void convert(BaseViewHolder baseViewHolder, TribeGroup item) {
		baseViewHolder.setText(R.id.tv_name, item.getGroup_name());
		ImageViewUtil.displayImage(Constants.MY_BABY_TribeURL + item.getIm_tribe_id(), (ImageView) baseViewHolder.getView(R.id.iv_avater));
	}

	private void bindData(final TribeViewHolder holder, final TribeGroup item) {
		holder.tv_name.setText("");
		holder.tv_name.setText(item.getGroup_name());
	}

	static	class TribeViewHolder {
			ImageView iv_avater;
			TextView tv_name;

			public TribeViewHolder(View contentView) {
				iv_avater = (ImageView) contentView
						.findViewById(R.id.iv_avater);
				tv_name = (TextView) contentView
						.findViewById(R.id.tv_name);
			}
		}
	}