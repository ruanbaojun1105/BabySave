package photopickerlib.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.customclass.PhotoView;
import mybaby.util.ImageViewUtil;
import photopickerlib.beans.Photo;

/**
 * Created by donglua on 15/6/21.
 */
@Deprecated
public class PhotoPagerAdapter extends PagerAdapter {

  private List<Photo> paths = new ArrayList<>();

  public PhotoPagerAdapter(List<Photo> paths) {
    this.paths = paths;
  }

  private boolean iii=false;
  @Override public Object instantiateItem(ViewGroup container, int position) {
    final Context context = container.getContext();
    View itemView = LayoutInflater.from(context)
        .inflate(R.layout.imagepage_item_picker, container, false);

    final PhotoView imageView = (PhotoView) itemView.findViewById(R.id.image_id);
    final ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
    imageView.enable();
    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    progressBar.bringToFront();
    //imageView.animaFrom(imageView.getInfo());
    progressBar.setVisibility(View.VISIBLE);

    final String path = paths.get(position).getPath();
    ImageViewUtil.displayImage("file://"+path,imageView,new SimpleImageLoadingListener(){
      @Override
      public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if (loadedImage != null) {
          new CustomAbsClass.doSomething((Activity)context) {
            @Override
            public void todo() {
              imageView.setVisibility(View.VISIBLE);
              progressBar.setVisibility(View.GONE);
            }
          };
        }
      }
    });
    imageView.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View arg0) {
        // TODO Auto-generated method stub
        if (context instanceof Activity) {
          if (!((Activity) context).isFinishing()) {
            ((Activity) context).onBackPressed();
          }
        }
      }
    });
    imageView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        return true;
      }
    });

    container.addView(itemView);//切记此句
    return itemView;
  }


  @Override public int getCount() {
    return paths.size();
  }


  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }


  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

  @Override
  public int getItemPosition (Object object) { return POSITION_NONE; }

}
