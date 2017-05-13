package mybaby.ui.community.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import me.hibb.mybaby.android.R;
import mybaby.models.community.ParentingPost;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.util.ImageViewUtil;

public class ParentingPostAdapter extends BaseQuickAdapter<ParentingPost> {
	private Context context;
	public ParentingPostAdapter(Context context){
		super(R.layout.parenting_post_row,new ArrayList<ParentingPost>());
		this.context=context;

	}

	@Override
	protected void convert(BaseViewHolder baseViewHolder, ParentingPost parentingPost) {
		ImageItemHolder viewHolder=new ImageItemHolder(baseViewHolder.convertView);
		bindData(viewHolder,parentingPost);
	}

	private void bindData(final ImageItemHolder viewHolder, final ParentingPost parentingPost){
		viewHolder.post_moreIcon.setTypeface(MyBabyApp.fontAwesome);
		viewHolder.post_moreIcon.setText(R.string.fa_angle_right);
		viewHolder.post_title.setText(parentingPost.getTitle());


		if (!TextUtils.isEmpty(parentingPost.getExcerpt())) {
			viewHolder.post_excerpt.setText(parentingPost.getExcerpt());
			viewHolder.post_excerpt.setVisibility(View.VISIBLE);
		}else {
			viewHolder.post_excerpt.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(parentingPost.getFeaturedImageUrl().trim())) {
			ImageViewUtil.displayImage(parentingPost.getFeaturedImageUrl(), viewHolder.imageView,new SimpleImageLoadingListener(){
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					viewHolder.imageView.setVisibility(View.GONE);
					viewHolder.image_sideline.setVisibility(View.GONE);
				}

				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					if (loadedImage==null) {
						viewHolder.imageView.setVisibility(View.GONE);
						viewHolder.image_sideline.setVisibility(View.GONE);
					}
				}
			},false);
			viewHolder.imageView.setVisibility(View.VISIBLE);
			viewHolder.image_sideline.setVisibility(View.VISIBLE);
			viewHolder.image_sideline.bringToFront();
		}else {
			viewHolder.imageView.setVisibility(View.GONE);
			viewHolder.image_sideline.setVisibility(View.GONE);
		}

		viewHolder.parenting_post_lin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(parentingPost.getDetailUrl())) {
					CustomAbsClass.starParentingWebViewIntent(context,parentingPost);
				}
			}
		});
	}


	


    //private static final ColorDrawable TRANSPARENT_DRAWABLE = new ColorDrawable(android.R.color.transparent);

    /*public static void fadeInDisplay(View imageView, Bitmap bitmap) {
        @SuppressWarnings("deprecation")
		final TransitionDrawable transitionDrawable =
                new TransitionDrawable(new Drawable[]{
                        TRANSPARENT_DRAWABLE,new BitmapDrawable(bitmap)
                });
        //imageView.setImageDrawable(transitionDrawable);
        imageView.setBackground(transitionDrawable);
        transitionDrawable.startTransition(500);
    }*/
	static class ImageItemHolder{
		TextView post_title,post_excerpt,post_moreIcon,image_sideline;
		LinearLayout parenting_post_lin;
		ImageView imageView;

		public ImageItemHolder(View view) {
			imageView=(ImageView) view.findViewById(R.id.image_post);
			post_title=(TextView) view.findViewById(R.id.post_title);
			post_moreIcon=(TextView) view.findViewById(R.id.post_more_icon);
			post_excerpt=(TextView) view.findViewById(R.id.post_excerpt);
			image_sideline=(TextView) view.findViewById(R.id.image_sideline);
			parenting_post_lin = (LinearLayout) view.findViewById(R.id.parenting_post_lin);
		}
	}
}
