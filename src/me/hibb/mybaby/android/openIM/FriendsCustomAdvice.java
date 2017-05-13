package me.hibb.mybaby.android.openIM;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.mobileim.aop.BaseAdvice;
import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileimexternal.ui.aop.pointcuts.friends.CustomTitleBarAdvice;

/**
 *
 * @author
 */
public class FriendsCustomAdvice extends BaseAdvice implements CustomTitleBarAdvice {

    public FriendsCustomAdvice(Pointcut pointcut) {
        super(pointcut);
    }

    /**

    /**
     * 返回分组联系人的标题
     *
     * @param fragment
     * @param context
     * @return
     */

    @Override
    public View getCustomTitle(final Fragment fragment, final Context context, LayoutInflater layoutInflater) {
        /*RelativeLayout customView = (RelativeLayout) layoutInflater
                .inflate(R.layout.demo_custom_conversation_title_bar, null);
        customView.setBackgroundColor(Color.parseColor("#00b4ff"));
        TextView title = (TextView) customView.findViewById(R.id.title_txt);
        title.setText("好友");
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
        return customView;*/
    	return null;
    }
}
