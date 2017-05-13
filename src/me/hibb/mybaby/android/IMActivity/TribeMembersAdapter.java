package me.hibb.mybaby.android.IMActivity;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.WXAPI;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.IYWContactProfileCallback;
import com.alibaba.mobileim.contact.YWOnlineContact;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeMember;
import com.alibaba.mobileim.kit.common.YWAsyncBaseAdapter;
import com.alibaba.mobileim.kit.contact.YWContactHeadLoadHelper;

import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.ui.widget.CircleImageView;
import mybaby.util.ImageHelper;
import mybaby.util.ImageViewUtil;

public class TribeMembersAdapter extends YWAsyncBaseAdapter {
    private static final String TAG = "TribeMembersAdapter";
    private Context context;
    private int max_visible_item_count;
    private LayoutInflater inflater;
    private YWContactHeadLoadHelper mContactHeadLoadHelper;
    private List<YWTribeMember> mList;
    public TribeMembersAdapter(Activity context, List<YWTribeMember> list) {
        this.context = context;
        this.mList = list;
        mContactHeadLoadHelper = new YWContactHeadLoadHelper(context, this);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder {
        CircleImageView headView;
        TextView nick;
        TextView role;
    }

    public void refreshData(List<YWTribeMember> list) {
        mList = list;
        notifyDataSetChangedWithAsyncLoad();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < mList.size()) {
            return mList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void loadAsyncTask() {
        mContactHeadLoadHelper.setMaxVisible(max_visible_item_count);
        mContactHeadLoadHelper.loadAyncHead();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout.LayoutParams roleParams;
        RelativeLayout.LayoutParams headParams;
        RelativeLayout.LayoutParams nickParams;
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.tribe_members_item, null);
            holder = new ViewHolder();
            holder.headView = (CircleImageView) convertView.findViewById(R.id.head);
            holder.nick = (TextView) convertView
                    .findViewById(R.id.nick);
            holder.role = (TextView) convertView.findViewById(R.id.role);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mList != null) {
            final YWTribeMember user = mList.get(position);
            if (user != null) {
//                String name = user.getShowName();
                String name="";
                String url="";
                IYWContactProfileCallback callback = WXAPI.getInstance()
                        .getContactProfileCallback();

                holder.headView.setTag(R.id.head, position);
                if (callback != null && callback.onFetchContactInfo(user.getUserId()) != null) {
                    IYWContact contact = callback.onFetchContactInfo(user.getUserId());

                    if (contact != null) {
                        name =contact.getShowName();
                        url =contact.getAvatarPath();
                        //mContactHeadLoadHelper.setHeadView(holder.headView, user.getUserId(), user.getAppKey(), true);
                        ImageViewUtil.displayImage(TextUtils.isEmpty(url)?ImageHelper.getMotherPicUrl():url,holder.headView);
                        holder.nick.setText(TextUtils.isEmpty(name)?contact.getShowName():name);
                    }
                }
                else {
                    IYWContact contact = (IYWContact)WXAPI.getInstance().getWXIMContact(user.getUserId());
                    name =contact.getShowName();
                    url =contact.getAvatarPath();
                    ImageViewUtil.displayImage(TextUtils.isEmpty(url)?ImageHelper.getMotherPicUrl():url,holder.headView);
                    //mContactHeadLoadHelper.setHeadView(holder.headView, user.getUserId(),user.getAppKey(), true);
                    holder.nick.setText(name);
                }

                holder.nick.setVisibility(View.VISIBLE);
                holder.role.setVisibility(View.VISIBLE);
                int role = user.getTribeRole();
                if (role == YWTribeMember.ROLE_HOST) {
                    holder.role.setText("群主");
                    holder.role.setBackgroundResource(R.drawable.btn_rect_graybg);
                } else if (role == YWTribeMember.ROLE_MANAGER) {
                    holder.role.setText("管理员");
                    holder.role.setBackgroundResource(R.drawable.btn_rect_littlegray);
                } else {
                    holder.role.setVisibility(View.GONE);
                }
            }

        }
        headParams = (RelativeLayout.LayoutParams)holder.headView.getLayoutParams();
        roleParams = (RelativeLayout.LayoutParams)holder.role.getLayoutParams();
        nickParams = (RelativeLayout.LayoutParams)holder.nick.getLayoutParams();
        holder.nick.setMaxWidth(convertView.getWidth()
                - (holder.headView.getWidth() + headParams.leftMargin + headParams.rightMargin)
                - (holder.role.getWidth() + roleParams.leftMargin + roleParams.rightMargin)
                - (nickParams.rightMargin + nickParams.leftMargin)
        );
        return convertView;
    }

    public void setMax_visible_item_count(int max_visible_item_count) {
        this.max_visible_item_count = max_visible_item_count;
    }


    public static class ContactImpl implements IYWContact {
        private String userid = "", appKey = "", avatarPath = "", showName = "";
        private int status = YWOnlineContact.ONLINESTATUS_ONLINE;

        public ContactImpl(String showName, String userid, String avatarPath, String signatures, String appKey) {
            this.showName = showName;
            this.userid = userid;
            this.avatarPath = avatarPath;
            this.appKey = appKey;
        }
        public void setOnlineStatus(int status) {
            this.status = status;
        }
        @Override
        public String getUserId() {
            return userid;
        }

        @Override
        public String getAppKey() {
            return appKey;
        }

        @Override
        public String getAvatarPath() {
            return avatarPath;
        }

        @Override
        public String getShowName() {
            return showName;
        }

        public int getOnlineStatus() {
            return status;
        }

    }

}
