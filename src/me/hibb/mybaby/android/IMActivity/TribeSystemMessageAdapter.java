package me.hibb.mybaby.android.IMActivity;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.WXAPI;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeMember;
import com.alibaba.mobileim.kit.common.IMUtility;
import com.alibaba.mobileim.lib.model.message.SystemMessage;

import org.xmlrpc.android.XMLRPCException;

import java.util.List;

import me.hibb.mybaby.android.R;
import me.hibb.mybaby.android.openIM.TribeHelper;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.notification.TribesRpc;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.widget.RoundImageViewByXfermode;
import mybaby.util.ImageViewUtil;
import mybaby.util.MaterialDialogUtil;

/**
 *
 */
public class TribeSystemMessageAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<YWMessage> mMessageList;

    public TribeSystemMessageAdapter(Context context, List<YWMessage> messages) {
        mContext = context;
        mMessageList = messages;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder {
        TextView tribeName;
        TextView agreeButton;
        TextView ignoreButton;
        TextView result;
        RelativeLayout agreeLayout;
        RoundImageViewByXfermode tribeImage;
        ProgressBar progressBar;
    }

    public void refreshData(List<YWMessage> list){
        mMessageList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.system_message_item, parent, false);
            holder = new ViewHolder();
            holder.tribeName = (TextView) convertView.findViewById(R.id.tribe_name);
            holder.agreeButton = (TextView) convertView
                    .findViewById(R.id.agree);
            holder.agreeLayout= (RelativeLayout)convertView.findViewById(R.id.rl_agree);
            holder.ignoreButton = (TextView) convertView.findViewById(R.id.ignore);
            holder.result = (TextView) convertView.findViewById(R.id.result);
            holder.tribeImage = (RoundImageViewByXfermode) convertView.findViewById(R.id.iv_tribe_avater);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setVisibility(View.VISIBLE);
        if (mMessageList != null) {
            final YWMessage msg = mMessageList.get(position);
            final SystemMessage message = (SystemMessage) msg;
            long tid = Long.valueOf(message.getAuthorId());
            final YWTribe tribe = MyBayMainActivity.getmIMKit().getTribeService().getTribe(tid);
            if (tribe==null) {
                convertView.setVisibility(View.GONE);
                return convertView;
            }

            holder.tribeName.setText(tribe.getTribeName());
            //holder.message.setText(message.getMessageBody().getContent());
            //holder.message.setVisibility(View.GONE);
            ImageViewUtil.displayImage(TribeHelper.getTribeIconUrl(tribe.getTribeId()),holder.tribeImage);
            holder.agreeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    MaterialDialogUtil.showCommDialog(mContext, "群守则", "为维护良好的群秩序，" +
                            "群内禁止任何形式的广告骚扰或其他不恰当信息，发现类似情况，请您【长按】垃圾信息内容【举报】，" +
                            "被核实后将会对相关用户进行处理。", "确认加入", null, new MaterialDialogUtil.DialogCommListener() {
                        @Override
                        public void todosomething() {
                            holder.agreeLayout.setEnabled(false);
                            holder.progressBar.bringToFront();
                            holder.progressBar.setVisibility(View.VISIBLE);
                            holder.agreeButton.setText("");
                            addTribe(tribe, msg);
                        }
                    });
                }
            });
            holder.ignoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(mContext,"忽略成功",Toast.LENGTH_SHORT).show();
                    ((TribeSystemMessageActivity)mContext).refuseToJoinTribe(msg);
                }
            });
            holder.agreeButton.setText("确认");
            if (message.isAccepted()){
                holder.agreeLayout.setEnabled(false);
                holder.progressBar.setVisibility(View.GONE);
                holder.agreeButton.setVisibility(View.GONE);
                holder.ignoreButton.setVisibility(View.GONE);
                holder.result.setVisibility(View.VISIBLE);
                holder.result.setText("已同意");

            } else if (message.isIgnored()){
                holder.agreeLayout.setEnabled(false);
                holder.progressBar.setVisibility(View.GONE);
                holder.agreeButton.setVisibility(View.GONE);
                holder.ignoreButton.setVisibility(View.GONE);
                holder.result.setVisibility(View.VISIBLE);
                holder.result.setText("已忽略");
            } else {
                holder.progressBar.setVisibility(View.GONE);
                holder.result.setText("确认");
                holder.agreeLayout.setEnabled(true);
                holder.agreeButton.setVisibility(View.VISIBLE);
                holder.ignoreButton.setVisibility(View.GONE);
                holder.result.setVisibility(View.GONE);
            }
        }
        return convertView;
    }



    private void addTribe(final YWTribe tribe, final YWMessage msg){
        TribesRpc.join_for_tribeid(tribe.getTribeId(), new BaseRPC.CallbackForMap() {
            @Override
            public void onSuccess(final boolean boolValue, final String data) {
                new CustomAbsClass.doSomething(mContext) {
                    @Override
                    public void todo() {
                        Toast.makeText(mContext,boolValue? (TextUtils.isEmpty(data)?"加入成功":data):"群加入失败", Toast.LENGTH_SHORT).show();
                        if (boolValue)
                            ((TribeSystemMessageActivity)mContext).agreeToJoinTribe(msg);
                        else ((TribeSystemMessageActivity)mContext).disJoinTribe(msg);
                    }
                };
                MyBayMainActivity.getmIMKit().getTribeService().getMembersFromServer(new IWxCallback() {
                    @Override
                    public void onSuccess(Object... objects) {
                        if (objects == null)
                            return;
                        List<YWTribeMember> mList = (List<YWTribeMember>) objects[0];
                        if (mList == null)
                            return;
                        for (YWTribeMember member : mList) {
                            IMUtility.getContactProfileInfo(member.getUserId(), member.getAppKey());
                            WXAPI.getInstance().getContactProfileCallback().onFetchContactInfo(member.getUserId());
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }

                    @Override
                    public void onProgress(int i) {

                    }
                }, tribe.getTribeId());
            }

            @Override
            public void onFailure(long id, XMLRPCException error) {
               new CustomAbsClass.doSomething(mContext) {
                   @Override
                   public void todo() {
                       ((TribeSystemMessageActivity)mContext).disJoinTribe(msg);
                       Toast.makeText(mContext, "群加入失败", Toast.LENGTH_SHORT).show();
                   }
               };
            }
        });

    }

}
