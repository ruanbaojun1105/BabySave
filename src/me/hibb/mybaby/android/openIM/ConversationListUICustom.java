package me.hibb.mybaby.android.openIM;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMConversationListUI;
import com.alibaba.mobileim.conversation.YWConversation;

/**
 * 最近会话界面的定制点(根据需要实现相应的接口来达到自定义会话列表界面)，不设置则使用openIM默认的实现
 * 调用方设置的回调，必须继承BaseAdvice 根据不同的需求实现 不同的 开放的 Advice
 * com.alibaba.mobileim.aop.pointcuts包下开放了不同的Advice.通过实现多个接口，组合成对不同的ui界面的定制
 * 这里设置了自定义会话的定制
 * 1.CustomConversationAdvice 实现自定义会话的ui定制
 * 2.CustomConversationTitleBarAdvice 实现自定义会话列表的标题的ui定制
 * <p/>
 * 另外需要在application中将这个Advice绑定。设置以下代码
 * AdviceBinder.bindAdvice(PointCutEnum.CONVERSATION_FRAGMENT_POINTCUT, CustomChattingAdviceDemo.class);
 *
 * @author jing.huai
 */
public class ConversationListUICustom extends IMConversationListUI {

    public ConversationListUICustom(Pointcut pointcut) {
        super(pointcut);
    }

    /**
     * 返回会话列表的自定义标题
     *
     * @param fragment
     * @param context
     * @param inflater
     * @return
     */
   /* @Override
    public View getCustomConversationListTitle(final Fragment fragment,
                                               final Context context, LayoutInflater inflater) {
        //TODO 重要：必须以该形式初始化customView---［inflate(R.layout.**, new RelativeLayout(context),false)］------，以让inflater知道父布局的类型，否则布局xml**中定义的高度和宽度无效，均被默认的wrap_content替代
        RelativeLayout customView = (RelativeLayout) inflater
                .inflate(R.layout.demo_custom_conversation_title_bar, new RelativeLayout(context),false);
        customView.setBackgroundColor(Color.parseColor("#00b4ff"));
        TextView title = (TextView) customView.findViewById(R.id.title_txt);
        title.setText("消息");
        title.setTextColor(Color.WHITE);
        title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(context, "click ", Toast.LENGTH_SHORT).show();

            }
        });
        TextView backButton = (TextView) customView.findViewById(R.id.left_button);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                fragment.getActivity().finish();
            }
        });
        backButton.setVisibility(View.GONE);
        return customView;
    }*/

    /**
     * 高级功能，通知调用方 消息漫游接收的状态 （可选 ）
     * 可以通过 fragment.getActivity() 拿到Context
     *
     * @param mCustomTitleView 用户设置的自定义标题 View
     * @param isVisible        是否显示“正在接收消息中” true:调用方需要去显示“消息接收中的菊花” false:调方用需要隐藏“消息接收中的菊花”
     */
    @Override
    public void setCustomTitleProgressBar(Fragment fragment,
                                          View mCustomTitleView, boolean isVisible) {

    }

    @Override
    public boolean needHideTitleView(Fragment fragment) {
        return false;
    }

    @Override
    public boolean needHideNullNetWarn(Fragment fragment) {
        return true;
    }

    /**
     * 返回固定的群头像
     * @param fragment
     * @param conversation
     * @return
     */
   /* @Override
    public int getTribeConversationHead(Fragment fragment, YWConversation conversation){
        return R.drawable.aliwx_tribe_head_default;
    }*/

    /**
     * 返回自定义置顶回话的背景色(16进制字符串形式)
     * @return
     */
    @Override
    public String getCustomTopConversationColor() {
        return "#e1f5fe";
    }

    @Override
    public boolean enableSearchConversations(Fragment fragment){
        return true;
    }

    /**
     * 获取自定义会话要展示的自定义View，该方法的实现类完全似于BaseAdapter中getView()方法实现
     * @param context
     * @param conversation
     *          自定义展示View的会话
     * @param convertView
     *          {@link android.widget.BaseAdapter#getView(int, View, ViewGroup)}的第二个参数
     * @param parent
     *          {@link android.widget.BaseAdapter#getView(int, View, ViewGroup)}的第三个参数
     * @return
     */
    /*@Override
    public View getCustomView(Context context, YWConversation conversation, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder = null;
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.demo_conversation_custom_view_item,
                    parent,
                    false);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.conversationName = (TextView)convertView.findViewById(R.id.conversation_name);
        viewHolder.conversationContent = (TextView)convertView.findViewById(R.id.conversation_content);
        CustomViewConversation customViewConversation = (CustomViewConversation)conversation;
        if(TextUtils.isEmpty(customViewConversation.getConversationName()))
            viewHolder.conversationName.setText("可自定义View布局的会话");
        else
            viewHolder.conversationName.setText(customViewConversation.getConversationName());
        viewHolder.conversationContent.setText(customViewConversation.getLatestContent());
        return convertView;
    }*/

    /**
     * {@link ConversationListUICustom#getCustomView(Context, YWConversation, View, ViewGroup)}中使用到的ViewHolder示例
     */
    static class ViewHolder {
        TextView conversationName;
        TextView conversationContent;
    }

    /**
     * 会话列表onDestroy事件
     * @param fragment
     */
    @Override
    public void onDestroy(Fragment fragment) {
        super.onDestroy(fragment);
    }

    /**
     * 会话列表Activity创建事件
     * @param savedInstanceState
     * @param fragment
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState, Fragment fragment) {
        super.onActivityCreated(savedInstanceState, fragment);
    }

    /**
     * 会话列表onResume事件
     * @param fragment
     */
    @Override
    public void onResume(Fragment fragment) {
        super.onResume(fragment);
    }

    /**
     * 该方法可以构造一个会话列表为空时的展示View
     * @return
     *      empty view
     */
    @Override
    public View getCustomEmptyViewInConversationUI(Context context) {
        /** 以下为示例代码，开发者可以按需返回任何view*/
        TextView textView = new TextView(context);
        textView.setText("还没有会话哦，快去找人聊聊吧!");
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        return textView;
    }
}
