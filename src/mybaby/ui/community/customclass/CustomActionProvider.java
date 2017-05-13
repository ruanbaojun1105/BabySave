package mybaby.ui.community.customclass;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnDismissListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.PopupWindow;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.community.TopicCategory;
import mybaby.ui.community.TopicEditActivity;
import mybaby.ui.community.TopicMoreActivity;

public class CustomActionProvider extends ActionProvider {

    /** Context wrapper. */

    private ContextWrapper mContextWrapper;

    Context context;
    
    PopupMenu mPopupMenu;

    Animation rotate ;
    Animation rotateReverse ;


    public CustomActionProvider(Context context) {

        super(context);
        this.context=context;
        mContextWrapper = (ContextWrapper)context;
        rotate = AnimationUtils.loadAnimation(context,
    			R.anim.arrow_rotate);
        rotateReverse = AnimationUtils.loadAnimation(context,
    			R.anim.arrow_rotate_reverse);
    }



    @Override

    public View onCreateActionView() {
        // Inflate the action view to be shown on the action bar.

        LayoutInflater layoutInflater = LayoutInflater.from(mContextWrapper);

        View view = layoutInflater.inflate(R.layout.actionbar_provider, null);

        ImageView popupView = (ImageView)view.findViewById(R.id.popup_view);

        popupView.setOnClickListener(new View.OnClickListener() {

            @Override

           public void onClick(View v) {

            	//二选一
               //popWindowsAddTopic(context,v);
            	if (Constants.TOPIC_CATEGORIES!=null&&Constants.TOPIC_CATEGORIES.length>0) {
            		v.startAnimation(rotate);
                	v.setFocusable(true);
					showPopup(v,Constants.TOPIC_CATEGORIES,context);
				}else {
					CustomAbsClass.starTopicEditIntent(context, 0, "", null);
				}
           }

        });

        return view;

    }


    /**
     * 弹出popwindows的话题列表
     * @param v 
     */
    	protected void popWindowsAddTopic(Context context, View v ) {
    		
    		//View popView=LayoutInflater.from(context).inflate(R.layout.community_all_listitem, null);
            LinearLayout linearLayout=new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            TopicCategory [] topicCategories=Constants.TOPIC_CATEGORIES;
    			if (topicCategories!=null) {
    				for (int i = 0; i < topicCategories.length; i++) {
    					Button button=new Button(context);
    					button.setText(topicCategories[i].getTitle());
    					button.setTag(topicCategories[i].getCategoryId());
    					linearLayout.addView(button);
    					}
    				}
    		Button buttonMore=new Button(context);
    		buttonMore.setText("更多话题");
    		linearLayout.addView(buttonMore);
            
    		
            final PopupWindow popWindow = new PopupWindow(
            		linearLayout,
            		LayoutParams.WRAP_CONTENT,
            		LayoutParams.WRAP_CONTENT,true);
            popWindow.setBackgroundDrawable(new ColorDrawable(0));
         // 状态栏的高度  
                   /*Rect frame = new Rect();  
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
                    int statusBarHeight = frame.top;  
                    popWindow.showAtLocation(linearLayout, Gravity.RIGHT | Gravity.TOP, 20,  
                            getActionBar().getHeight() + statusBarHeight);  */

            popWindow.showAsDropDown(v, 0, 0);
            popWindow.getContentView().setOnFocusChangeListener(new OnFocusChangeListener() {
    			
    			@Override
    			public void onFocusChange(View arg0, boolean arg1) {
    				// TODO Auto-generated method stub
    				if (!arg1) {
    					popWindow.dismiss();
    				}
    			}
    		});
    	} 
    

    /**

     * show the popup menu.

     *

     * @param v

     */

    public static void showPopup(final View v,TopicCategory [] topicCategories,final Context context) {
    	PopupMenu  mPopupMenu = new PopupMenu(context, v);
        Menu menu=mPopupMenu.getMenu();
        menu.add(Menu.NONE, 0, 0, "发布动态"); 
		if (topicCategories!=null) {
			for (int i = 0; i < topicCategories.length; i++) {
				menu.add(Menu.NONE, topicCategories[i].getCategoryId(), 0, topicCategories[i].getTitle());
				}
			}
		menu.add(Menu.NONE,1,0,"更多话题>");
		
        mPopupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override

            public boolean onMenuItemClick(MenuItem item) {

                // do someting
            	switch (item.getItemId()) {
				case 0:
					context.startActivity(new Intent(context, TopicEditActivity.class));
					break;
				case 1:
					context.startActivity(new Intent(context, TopicMoreActivity.class));
					break;
				default:
					Intent intent=new Intent(context, TopicEditActivity.class);
					intent.putExtra("TopicCategory_id", item.getItemId());
					intent.putExtra("TopicCategory_title", item.getTitle());
					context.startActivity(intent);
					break;
				}
              return false;

           }



        });
        mPopupMenu.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(PopupMenu arg0) {
				// TODO Auto-generated method stub
				v.setFocusable(false);
			}
		});
        /*MenuInflater inflater = mPopupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_up_menu, mPopupMenu.getMenu());*/

        mPopupMenu.show();
        //v.startAnimation(rotateReverse);
    }

}
