package mybaby.ui.user;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

import me.hibb.mybaby.android.R;
import mybaby.models.User;
import mybaby.models.person.Person;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.util.ImageViewUtil;

/**
 * Created by niubaobei on 2016/1/9.
 */
public class UserListAdapter extends BaseQuickAdapter<User> {


    public UserListAdapter() {
        super(R.layout.item_user, new ArrayList<User>());
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, User user) {
        ViewHolder viewHolder=new ViewHolder(baseViewHolder.convertView);
        bindData(baseViewHolder,viewHolder,user);
    }
    private void bindData(final BaseViewHolder baseViewHolder,final ViewHolder holder, final User user){
        if (user.getAvatarThumbnailUrl() != null
                && !("".equals(user.getAvatarThumbnailUrl().trim()))) {
            ImageViewUtil.displayImage(user.getAvatarThumbnailUrl(), holder.iv_avater, null, false);
        }else{
            holder.iv_avater.setImageResource(user.getNullAvatar());
        }
        holder.tv_name.setText(user.getName());
        holder.tv_age .setText("宝宝" + Person.calAgeText(user.getBabyBirthday(), false));
        if (user.getIsFriend()) {
            holder.tv_hasfollow.setText("相互关注");
        }else{
            holder.tv_hasfollow.setText(null);
        }
        baseViewHolder.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomAbsClass.starUserPage(baseViewHolder.convertView.getContext(), user);
            }
        });
    }

    class ViewHolder {
        ImageView iv_avater;
        TextView tv_name;
        TextView tv_age;
        TextView tv_hasfollow;

        public ViewHolder(View contentView) {
           iv_avater = (ImageView) contentView
                    .findViewById(R.id.iv_avater);
            tv_name = (TextView) contentView
                    .findViewById(R.id.tv_name);
            tv_age = (TextView) contentView
                    .findViewById(R.id.tv_age);
            tv_hasfollow = (TextView) contentView
                    .findViewById(R.id.tv_hasFollow);
        }
    }
}


