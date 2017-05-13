package mybaby.ui.community.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import me.hibb.mybaby.android.R;

/**
 * Created by donglua on 15/6/21.
 */
public class GuidePagerAdapter extends PagerAdapter {

  private int [] dataArray;
  private View.OnClickListener clickListener;
  private View.OnTouchListener onTouchListener;
  public GuidePagerAdapter(int [] dataArray,View.OnClickListener clickListener,View.OnTouchListener onTouchListener) {
    this.dataArray = dataArray;
    this.clickListener = clickListener;
    this.onTouchListener = onTouchListener;
  }

  @Override
  public Object instantiateItem(ViewGroup container, final int position) {
    final Context context = container.getContext();
    View view = LayoutInflater.from(context).inflate(R.layout.image_item,  container, false);
    ImageView imageView = (ImageView) view.findViewById(R.id.image);
    Picasso.with(context).load(dataArray[position]).config(Bitmap.Config.RGB_565)/*.resize(MyBabyApp.screenWidth/2,MyBabyApp.screenHeight/2)*/
            .placeholder(new ColorDrawable(Color.parseColor("#f5f5f5")))/*.centerCrop()*/.into(imageView);
    if (position < 4)
      imageView.setOnClickListener(clickListener);
    if (position == 4)
      imageView.setOnTouchListener(onTouchListener);
    container.addView(view);//切记加此句
    return view;
  }

  @Override
  public int getCount() {
    return dataArray.length;
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

  @Override
  public int getItemPosition(Object object) {
    return POSITION_NONE;
  }

}
