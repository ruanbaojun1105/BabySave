package mybaby.ui.community.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

import me.hibb.mybaby.android.R;
import mybaby.models.community.activity.LikeActivity;
import mybaby.util.DateUtils;
import mybaby.util.ImageViewUtil;

public class LikeAdapter extends BaseQuickAdapter<LikeActivity> {
	public LikeAdapter(){
		super(R.layout.detail_like_list_item,new ArrayList<LikeActivity>());

	}

	@Override
	protected void convert(BaseViewHolder baseViewHolder, LikeActivity likeActivity) {
		ImageViewUtil.displayImage(likeActivity.getUser().getAvatarThumbnailUrl(), (ImageView) baseViewHolder.getView(R.id.image));
		baseViewHolder.setText(R.id.name,likeActivity.getUser().getName());
		baseViewHolder.setText(R.id.time, DateUtils.showDate(likeActivity.getDatetime()));
	}

}
