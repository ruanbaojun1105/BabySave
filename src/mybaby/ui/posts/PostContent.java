package mybaby.ui.posts;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.models.community.PlaceRepository;
import mybaby.models.diary.Media;
import mybaby.models.diary.MediaRepository;
import mybaby.models.diary.Post;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.util.ImageViewUtil;

public class PostContent {
	public static void fillContent(final Activity activity,
			final Post post, final TimelineListFragment.PostHolder postHolder) {

		final int COLLAPSIBLE_STATE_SHRINKUP = 1;//收起

		final int COLLAPSIBLE_STATE_SPREAD = 2;//全文|展开
		if (post.getPlaceObjId() == 0) {
			postHolder.rela_place_pic.setVisibility(View.GONE);
		} else {
			postHolder.rela_place_pic.setVisibility(View.VISIBLE);
			postHolder.place_pic.setTypeface(MyBabyApp.fontAwesome);
			postHolder.place_pic.setText(R.string.fa_map_marker);
			postHolder.txtPlace.setText(PlaceRepository.load(
					post.getPlaceObjId()).getPlace_name());
			postHolder.rela_place_pic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					CustomAbsClass.startShowMapActivity(activity,PlaceRepository.load(post.getPlaceObjId()));
				}
			});
		}

		postHolder.txtContent.setText(post.getDescription());
		LayoutParams lp_content =postHolder. txtContent.getLayoutParams();
		postHolder.tv_spread.post(new Runnable() {
			@Override
			public void run() {
				if (postHolder.txtContent.getLineCount() > 8) {
					int  tag;

					if( postHolder.tv_spread.getTag()==null)
					{
						 tag=COLLAPSIBLE_STATE_SPREAD;
					}
					else{
						tag=(Integer) postHolder.tv_spread.getTag();
					}
					
					if(tag==COLLAPSIBLE_STATE_SPREAD)
					{
						postHolder.txtContent.setMaxLines(8);

						postHolder.tv_spread.setText("全文");
					}else if(tag==COLLAPSIBLE_STATE_SHRINKUP){

						postHolder.txtContent.setMaxLines(Integer.MAX_VALUE);
						postHolder.tv_spread.setText("收起");
					}
					postHolder.txtContent.requestLayout();
					postHolder.tv_spread.setVisibility(View.VISIBLE);
					
				} else {
					postHolder.tv_spread.setVisibility(View.GONE);
				}

			}
		});

		postHolder.tv_spread.setOnClickListener(new OnClickListener() {
			 int state=COLLAPSIBLE_STATE_SPREAD;
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
               
				if (state== COLLAPSIBLE_STATE_SPREAD) {
					postHolder.txtContent.setMaxLines(Integer.MAX_VALUE);
					postHolder.tv_spread.setText("收起");
					state= COLLAPSIBLE_STATE_SHRINKUP;
					arg0.setTag(COLLAPSIBLE_STATE_SHRINKUP);//记录这个控件的状态

				} else if (state== COLLAPSIBLE_STATE_SHRINKUP) {
					postHolder.txtContent.setMaxLines(8);
					postHolder.tv_spread.setText("全文");
					state= COLLAPSIBLE_STATE_SPREAD;
					arg0.setTag(COLLAPSIBLE_STATE_SPREAD);
				}

			}
		});
		
		
		
		 if (post.getDescription() == null
		 || post.getDescription().length() == 0) {
		 lp_content.height = 0;
		 }
		 else {
		 lp_content.height = LayoutParams.WRAP_CONTENT;
		 }
		postHolder.txtContent.setLayoutParams(lp_content);



		postHolder.mediasContainer.removeAllViewsInLayout();
		int place_width = showDailyMediaFiles(activity, post.getId(),
				postHolder.mediasContainer,post);
		LayoutParams lp_place = postHolder.rela_place_pic.getLayoutParams();
		lp_place.width = place_width;
		postHolder.rela_place_pic.setLayoutParams(lp_place);
	}

	public static int showDailyMediaFiles(final Activity activity,
			final int pId, FrameLayout mediasContainer, final Post post) {
		final Media[] arrMf = MediaRepository.getForPId(pId);

		int screenWidth = MyBabyApp.screenWidth;
		int imageAreaMaxWidth = screenWidth
				- (int) ((72 + 48) * MyBabyApp.density);

		int containerHeight = LayoutParams.WRAP_CONTENT;
		// int imageWidth = LayoutParams.WRAP_CONTENT;
		int imageWidth = imageAreaMaxWidth;

		for (int i = 0; i < arrMf.length; i++) {
			final Media media=arrMf[i];
			FrameLayout.LayoutParams lp = imageLayoutParams(arrMf, i,
					imageAreaMaxWidth);
			lp.gravity = Gravity.TOP;// android 2.X

			ImageView imageView = ImageViewUtil.showImageByMediaFile(activity,
					media, lp);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setBackgroundColor(activity.getResources().getColor(
					R.color.bg_gray));
			mediasContainer.addView(imageView);

			imageView.setTag(i);

			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (post.getStatus().equals(Post.Status_Draft)){
						PostRow.editPost(activity, post);
					}else
						CustomAbsClass.starGalleryImagePage(activity,media,(Integer) v.getTag());
				}
			});

			containerHeight = lp.topMargin + lp.height;
			imageWidth = lp.leftMargin + lp.width;
		}

		// android 2.x��Ҫ��ȷ���ø߶ȣ�����ֻ�е�һ��ͼ�ĸ߶�
		ViewGroup.LayoutParams lp_container = mediasContainer.getLayoutParams();
		lp_container.height = containerHeight;
		mediasContainer.setLayoutParams(lp_container);
		return imageAreaMaxWidth;
	}

	public static FrameLayout.LayoutParams imageLayoutParams(Media[] arrMf,
			int indexOfImages, int imageAreaMaxWidth) {
		int ImagesPerLine = 3;
		int imageOffset = (int) (2 * MyBabyApp.density);

		// 9��ͼƬʱÿ��ͼ�Ŀ�ߣ���Ϊ����
		int imageBaseHeight = (imageAreaMaxWidth - imageOffset
				* (ImagesPerLine - 1))
				/ ImagesPerLine;

		int x, y, w, h;
		int imageCount = arrMf.length;

		if (imageCount == 1) {
			float whRatio = arrMf[0].imageWHRatio();
			if (whRatio <= 0) {
				whRatio = 4.0f / 3.0f;
			}
			w = (int) (whRatio > 1 ? imageAreaMaxWidth * 1.0
					: imageAreaMaxWidth * 0.9);
			h = (int) (w / whRatio);
			x = 0;
			y = 0;
		} else if (imageCount == 2) {
			w = (imageAreaMaxWidth - imageOffset) / 2;
			h = w;
			x = (w + imageOffset) * indexOfImages;
			y = 0;
		} else if (imageCount == 3) {
			if (indexOfImages == 0) {
				w = imageBaseHeight * 2 + imageOffset;
				h = w;
				x = 0;
				y = 0;
			} else {
				w = imageBaseHeight;
				h = w;
				x = (imageBaseHeight + imageOffset) * 2;
				y = (h + imageOffset) * (indexOfImages - 1);
			}
		} else if (imageCount == 4) {
			w = (imageAreaMaxWidth - imageOffset) / 2;
			h = w;
			x = (w + imageOffset) * (indexOfImages % 2);
			y = (int) ((h + imageOffset) * Math.floor(indexOfImages / 2.0));
		} else if (imageCount == 5) {
			if (indexOfImages < 2) {
				w = imageAreaMaxWidth - imageOffset - imageBaseHeight;// (imageBaseHeight*ImagesPerLine+imageOffset*(ImagesPerLine-1))/2;
				h = (imageBaseHeight * ImagesPerLine + imageOffset
						* (ImagesPerLine - 1)) / 2;// w;
				x = 0;
				y = (h + imageOffset) * indexOfImages;
			} else {
				w = imageBaseHeight;
				h = w;
				x = imageAreaMaxWidth - imageBaseHeight;// (imageBaseHeight*ImagesPerLine+imageOffset*(ImagesPerLine-1))/2+imageOffset;
				y = (h + imageOffset) * (indexOfImages - 2);
			}
		} else if (imageCount == 6) {
			if (indexOfImages == 0) {
				w = imageBaseHeight * 2 + imageOffset;
				h = w;
				x = 0;
				y = 0;
			} else if (indexOfImages < 3) {
				w = imageBaseHeight;
				h = w;
				x = (imageBaseHeight + imageOffset) * 2;
				y = (h + imageOffset) * (indexOfImages - 1);
			} else {
				w = imageBaseHeight;
				h = w;
				x = (w + imageOffset) * (indexOfImages - 3);
				y = (imageBaseHeight + imageOffset) * 2;
			}
		} else {// 7,8,9 ȫ����Сͼ����
			w = imageBaseHeight;
			h = w;
			x = (w + imageOffset) * (indexOfImages % ImagesPerLine);
			y = (int) ((h + imageOffset) * Math.floor(indexOfImages
					/ ImagesPerLine));
		}

		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
		lp.leftMargin = x;
		lp.topMargin = y;
		return lp;
	}

}
