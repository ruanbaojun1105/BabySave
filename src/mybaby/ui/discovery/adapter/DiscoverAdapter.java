package mybaby.ui.discovery.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.action.Action;
import mybaby.models.discovery.Discovery;
import mybaby.models.discovery.DiscoveryObjs;
import mybaby.models.notification.NotificationCategory;
import mybaby.ui.MyBabyApp;
import mybaby.ui.Notification.adapter.NotificationCategoryAdapter;
import mybaby.ui.community.ViewReUseFaceListener;
import mybaby.ui.community.customclass.CusCommentLinearLayout;
import mybaby.ui.main.NotificationCategoryFragment;
import mybaby.ui.widget.RoundImageViewByXfermode;
import mybaby.util.ImageViewUtil;

/**
 * Created by LeJi_BJ on 2016/2/29.
 */
public class DiscoverAdapter extends BaseQuickAdapter<DiscoveryObjs> {
    private OnClickForTitle clickListener;
    private Context context;

    public DiscoverAdapter(Context context,List<DiscoveryObjs> discoveryList){
        super(R.layout.discovery_lin,discoveryList==null?new ArrayList<DiscoveryObjs>():discoveryList);
        this.context=context;
    }

    public DiscoverAdapter(Context context,List<DiscoveryObjs> discoveryList, OnClickForTitle clickListener){
        super(R.layout.discovery_lin,discoveryList==null?new ArrayList<DiscoveryObjs>():discoveryList);
        this.clickListener = clickListener;
        this.context=context;
    }

    public interface OnClickForTitle{
        void onClick(String title);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, DiscoveryObjs discoveryObjs) {
        final Discovery[] discoveries=discoveryObjs.getDiscoveries();
        CusCommentLinearLayout.setLinGreat(context,(LinearLayout) baseViewHolder.getView(R.id.discovery_lin),discoveries , new ViewReUseFaceListener() {
            @Override
            public int backViewRes() {
                return R.layout.fragment_discovery_item;
            }

            @Override
            public View backView() {
                return null;
            }

            @Override
            public void justItemToDo(Object data, View itemView, int position) {
                madeDiscoveryLin(itemView, (Discovery) data, position != discoveries.length - 1);
            }
        });
        //setLinGreat(discoveryList.get(i).getDiscoveries(),viewHolder.discovery_lin);//已改为上面接口实现方式
    }

    /**
     * 复用优化
     * @param discoveries
     * @param discovery_lin
     */
    private void setLinGreat(final Discovery[] discoveries,LinearLayout discovery_lin){
        if (discovery_lin.getChildCount()==0){
            for (int y=0; y < discoveries.length; y++) {
                discovery_lin.addView(getLinItem());
            }
        }else {
            int oldViewCount = discovery_lin.getChildCount();
            int newViewCount = discoveries.length;
            if (oldViewCount > newViewCount) {
                discovery_lin.removeViews(newViewCount - 1, oldViewCount - newViewCount);
            } else if (oldViewCount < newViewCount) {
                for (int i = 0; i < newViewCount - oldViewCount; i++) {
                    discovery_lin.addView(getLinItem());
                }
            }
        }
        int linCount = discovery_lin.getChildCount();
        for (int i = 0; i <linCount; i++) {
            boolean needLine=(i!=linCount-1);
            madeDiscoveryLin(discovery_lin.getChildAt(i),discoveries[i],needLine);
        }
    }
    private View getLinItem(){
        return LayoutInflater.from(context).inflate(R.layout.fragment_discovery_item, null);
    }

    private View madeDiscoveryLin(View convertView,final Discovery discoverie,boolean needLine){
        ImageView discovery_icon = (ImageView) convertView.findViewById(R.id.discovery_icon);
        RoundImageViewByXfermode notification_image = (RoundImageViewByXfermode) convertView.findViewById(R.id.notification_image);
        TextView discovery_title = (TextView) convertView.findViewById(R.id.discovery_title);
        TextView discovery_message = (TextView) convertView.findViewById(R.id.discovery_message);
        TextView discovery_tag = (TextView) convertView.findViewById(R.id.discovery_tag);
        TextView tv_notification_badge = (TextView) convertView.findViewById(R.id.tv_notification_badge);
        View discovery_line=convertView.findViewById(R.id.discovery_line);
        RelativeLayout rl_icon = (RelativeLayout) convertView.findViewById(R.id.rl_icon);
        RelativeLayout notification_item = (RelativeLayout) convertView.findViewById(R.id.notification_item);

        discovery_line.setVisibility(needLine ? View.VISIBLE : View.GONE);
        discovery_title.setText(discoverie.getTitle());
        //ImageViewUtil.displayImage(discoverie.getImageUrl(), discovery_icon);
        if (discoverie.getImage_drawable()==0) {
            Picasso.with(context)
                    .load(discoverie.getImageUrl())
                    .resize(150, 150)
                    .config(Bitmap.Config.RGB_565)
                            //.skipMemoryCache()
                    .placeholder(new ColorDrawable(Color.parseColor("#f5f5f5")))
                    .centerCrop()
                    .into(discovery_icon);
        }
        else{
            Picasso.with(context)
                    .load(discoverie.getImage_drawable())
                    .resize(150, 150)
                    .config(Bitmap.Config.RGB_565)
                            //.skipMemoryCache()
                    .placeholder(new ColorDrawable(Color.parseColor("#f5f5f5")))
                    .centerCrop()
                    .into(discovery_icon);
        }
        final NotificationCategory category=discoverie.getCategory();
        rl_icon.setVisibility(category == null ? View.GONE : View.VISIBLE);
        discovery_message.setText(category == null ? "":category.getTitle());
        if (category!= null) {
            ImageViewUtil.displayImage(category.getImageUrl(), notification_image);
            NotificationCategoryAdapter.setTextBgRedUnread(tv_notification_badge, discoverie.getCategory().getUnreadCount(), discoverie.getCategory().isStrongRemind());
        }
        discovery_tag.setTypeface(MyBabyApp.fontAwesome);
        discovery_tag.setText(context.getString(R.string.fa_angle_right));

        notification_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener==null) {
                    try {
                        Action.createAction(discoverie.getAction(), discoverie.getTitle(), null).excute((Activity) context, NotificationCategoryFragment.newsController, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.e("action执行异常");
                    }
                }else clickListener.onClick(discoverie.getTitle());
            }
        });
        return  convertView;
    }

    static class ViewHolders{
        public LinearLayout discovery_lin;
        /*public RoundImageViewByXfermode discovery_icon;
        public ImageView notification_image;
        public TextView discovery_tag;
        public TextView discovery_message;
        public TextView discovery_title;*/

        public ViewHolders(View view) {
            discovery_lin=(LinearLayout) view.findViewById(R.id.discovery_lin);
        }
    }
}
