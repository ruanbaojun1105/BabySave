package mybaby.ui.community.customclass;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.util.List;

import mybaby.models.community.Image;
import mybaby.util.ImageViewUtil;


/**
 * Created by Pan_ on 2015/2/2.
 */
public class NineGridlayout extends ViewGroup {

    /**
     * 图片之间的间隔
     */
    private int gap = 5;
    private int columns;//
    private int rows;//
    private List listData;
    private int totalWidth;

    public NineGridlayout(Context context) {
        super(context);
    }

    public NineGridlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
    private void layoutChildrenView(final List<Image> listsAll){
        final int childrenCount = listData.size();

        int singleWidth = (totalWidth - gap * (3 - 1)) / 3;
        int singleHeight = singleWidth;

        //根据子view数量确定高度
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = singleHeight * rows + gap * (rows - 1);
        requestLayout();
        //setLayoutParams(params);

        for (int i = 0; i < childrenCount; i++) {
            //final CustomImageView childrenView = (CustomImageView) getChildAt(i);
        	final ImageView childrenView = (ImageView) getChildAt(i);
            childrenView.setTag(i);
            final String url=((Image) listData.get(i)).getThumbnailUrl();
            //int height=((ImageBean) listData.get(i)).getHeight()*3;
           // int width=((ImageBean) listData.get(i)).getWidth()*3;//如果能指定图片宽高的情况下
            //childrenView.setImageUrl(url,width*3,height*3);
            childrenView.setScaleType(ScaleType.CENTER_CROP);
            /*Picasso.with(getContext()).load(url)
                    .resize(width, height)
                    .config(Bitmap.Config.RGB_565)
                    //.skipMemoryCache()
                    .centerCrop()
                    .placeholder(new ColorDrawable(Color.parseColor("#f5f5f5")))
                    .into(childrenView);*/
            ImageViewUtil.displayImage(url, childrenView);
            childrenView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                	//Toast.makeText(getContext(),url, 0).show();
                	int index=0;
                	String [] imageUrls=new String[listsAll.size()];
                	String [] imageUrlsLarge=new String[listsAll.size()];
                	for (int j = 0; j < listsAll.size(); j++) {
                		if (url.equals(listsAll.get(j).getThumbnailUrl())) {
							index=j;
						}
						imageUrls[j]= listsAll.get(j).getThumbnailUrl();
						imageUrlsLarge[j]= listsAll.get(j).getLargeUrl();
					}
                	CustomAbsClass.starImagePage(getContext(), childrenCount, index, imageUrlsLarge);
                }
            });
            int[] position = findPosition(i);
            int left = (singleWidth + gap) * position[1];
            int top = (singleHeight + gap) * position[0];
            int right = left + singleWidth;
            int bottom = top + singleHeight;

            childrenView.layout(left, top, right, bottom);
            childrenView.requestLayout();
            childrenView.invalidate();
        }

    }


    private int[] findPosition(int childNum) {
        int[] position = new int[2];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ((i * columns + j) == childNum) {
                    position[0] = i;//行
                    position[1] = j;//列
                    break;
                }
            }
        }
        return position;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }


    public void setImagesData(List<Image> lists,List<Image> listsAll,int countWidth) {
        if (lists == null || lists.isEmpty()) {
            return;
        }
        totalWidth=countWidth;
        //初始化布局
        generateChildrenLayout(lists.size());
        //这里做一个重用view的处理
        if (listData == null) {
            int i = 0;
            while (i < lists.size()) {
                //CustomImageView iv = generateImageView();
            	ImageView iv=new ImageView(getContext());
            	iv.setBackgroundColor(Color.parseColor("#f5f5f5"));
                addView(iv,generateDefaultLayoutParams());
                i++;
            }
        } else {
            int oldViewCount = listData.size();
            int newViewCount = lists.size();
            if (oldViewCount > newViewCount) {
                removeViews(newViewCount - 1, oldViewCount - newViewCount);
            } else if (oldViewCount < newViewCount) {
                for (int i = 0; i < newViewCount - oldViewCount; i++) {
                    //CustomImageView iv = generateImageView();
                	ImageView iv=new ImageView(getContext());
                	//iv.setBackgroundColor(Color.parseColor("#f5f5f5"));
                    addView(iv,generateDefaultLayoutParams()); 
                }
            }
        }
        listData = lists;
        layoutChildrenView(listsAll);
    }


    /**
     * 根据图片个数确定行列数量
     * 对应关系如下
     * num	row	column
     * 1	   1	1
     * 2	   1	2
     * 3	   1	3
     * 4	   2	2
     * 5	   2	3
     * 6	   2	3
     * 7	   3	3
     * 8	   3	3
     * 9	   3	3
     *
     * @param length
     */
    private void generateChildrenLayout(int length) {
        if (length <= 3) {
            rows = 1;
            columns = length;
        } else if (length <= 6) {
            rows = 2;
            columns = 3;
            /*if (length == 4) {
                columns = 2;
            }*/
        } else {
            rows = 3;
            columns = 3;
        }
    }

    private CustomImageView generateImageView() {
        CustomImageView iv = new CustomImageView(getContext());
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setBackgroundColor(Color.parseColor("#f5f5f5"));
        return iv;
    }


}
