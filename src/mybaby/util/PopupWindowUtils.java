package mybaby.util;

import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

public class PopupWindowUtils {

	public static PopupWindow popupWindow;
	private static PopupWindow imageAddressPopupWindow;
	
	//初始化日记popupwindow
	private static void initPopupWindow(View view,final Context mContext,String text,int x,int y,int width,int layoutId,int styleId,float alpha) throws Exception{
		WindowManager windowManager = ((Activity) mContext).getWindowManager();
		Display display = windowManager.getDefaultDisplay();

		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(mContext).inflate(
				layoutId, null);
		//contentView.setBackgroundColor();
		TextView tv_text = (TextView) contentView.findViewById(R.id.tv_text);
		tv_text.setText(text);
		tv_text.setTextColor(0xff505050);//字体颜色设置为灰色
		int mWidth;
		if(width == 0){
			mWidth = LayoutParams.WRAP_CONTENT;
		}else{
			mWidth = width;
		}
		popupWindow = new PopupWindow(contentView, mWidth,
				LayoutParams.WRAP_CONTENT, true);
		// 设置动画效果
		popupWindow.setAnimationStyle(styleId);
		WindowManager.LayoutParams params = ((Activity) mContext).getWindow()
				.getAttributes();
		if(alpha != 0.7f){
			params.alpha = alpha;
		}else{
			params.alpha = 0.7f;
		}
		((Activity) mContext).getWindow().setAttributes(params);
		popupWindow.setTouchable(true);
		popupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				closePopupWindow(mContext);
			}
		});
		
		
		
		popupWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				Log.i("mengdd", "onTouch : ");
				closePopupWindow(mContext);
				return false;
				// 这里如果返回true的话，touch事件将被拦截
				// 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
			}
		});

		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		// 我觉得这里是API的一个bug
		popupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(
				R.drawable.round_pop_transparent));

		// 设置好参数之后再show
		
		popupWindow.showAsDropDown(view, x, -y);
		
	}
	//初始化关注的popupwindow
		private static PopupWindow initPopupWindow1(View view,final Context mContext,String text,int x,int y,int width,int layoutId,int styleId,float alpha) throws Exception{
			WindowManager windowManager = ((Activity) mContext).getWindowManager();
			Display display = windowManager.getDefaultDisplay();

			// 一个自定义的布局，作为显示的内容
			View contentView = LayoutInflater.from(mContext).inflate(
					layoutId, null);
			//contentView.setBackgroundColor();
			TextView tv_text = (TextView) contentView.findViewById(R.id.tv_text);
			tv_text.setText(text);
			tv_text.setTextColor(0xff505050);//字体颜色设置为灰色
			int mWidth;
			if(width == 0){
				mWidth = LayoutParams.WRAP_CONTENT;
			}else{
				mWidth = width;
			}
			PopupWindow popupWindow = new PopupWindow(contentView, mWidth,
					LayoutParams.WRAP_CONTENT, true);
			// 设置动画效果
			popupWindow.setAnimationStyle(styleId);
			WindowManager.LayoutParams params = ((Activity) mContext).getWindow()
					.getAttributes();
			if(alpha != 0.7f){
				params.alpha = alpha;
			}else{
				params.alpha = 0.7f;
			}
			((Activity) mContext).getWindow().setAttributes(params);
			popupWindow.setTouchable(true);
  		    popupWindow.setFocusable(false);
			popupWindow.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss() {
//					closePopupWindow(mContext);
				}
			});
			popupWindow.setTouchInterceptor(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {

					Log.i("mengdd", "onTouch : ");
//					closePopupWindow(mContext);
					return false;
					// 这里如果返回true的话，touch事件将被拦截
					// 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
				}
			});

			// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
			// 我觉得这里是API的一个bug
			popupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(
					R.drawable.round_pop_transparent));

			// 设置好参数之后再show
			
			popupWindow.showAsDropDown(view, x, -y);
			Log.e("Popup显示出来", "Popup显示出来");
			return popupWindow;
			
		}
	
	public PopupWindow getPopupWindow(){
		return popupWindow;
	}
	
	
//显示日记的popupwindow
	public static void showPopupWindow(View view,Context mContext,String text,int x,int y,int width,float alpha) {
		if (popupWindow != null) {
			closePopupWindow(mContext);
		} else {
			try {
				initPopupWindow(view,mContext,text,x,y,width,R.layout.popplace_public,R.style.mypopwindow_anim_style,alpha);
			} catch (Exception e) {
				popupWindow = null;
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * 定时显示提示信息，可随时关闭
	 * @author Administrator
	 *
	 */
	public static class ShowPopTip{
		 PopupWindow popupWindows;
		 Handler handler=new Handler();
		 Runnable runnable=null;
		 /**
			 * 显示普通的popupwindow
			 * @param view  	popupwindow的相对控件
			 * @param mContext  context
			 * @param text  	文本
			 * @param x			位置信息
			 * @param y			位置信息
			 * @param width     popupwindow的宽度
			 * @param tag 0为详情界面布局 1为个人主页界面布局
			 */
		public ShowPopTip(View view,final Context mContext,String text,int x,int y,int width,int tag) throws Exception{
				if (tag==0) 
					popupWindows=initPopupWindow1(view,mContext,text,x,y,width,R.layout.popplace_public_follow,R.style.mypopwindow_follow_anim_style,1.0f);
				else
					popupWindows=initPopupWindow1(view,mContext,text,x,y,width,R.layout.popplace_public_follow_down,R.style.mypopwindow_anim_style,1.0f);
				runnable=new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (popupWindows != null)
							{
								dismiss();
							}
					}
				};
				handler.postDelayed(runnable, 10000);
		}
		public boolean isShow() {
			if (popupWindows!=null&&handler!=null)
				return popupWindows.isShowing();
			return false;
		}
		public void dismiss() {
			Log.e("buweikong", "不为空");
			
			if (popupWindows!=null&&handler!=null) {
				try {
					popupWindows.dismiss();
					if (runnable!=null) {
						handler.removeCallbacks(runnable);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
			
	}
	
	
	public static void showPopupWindow(View view,Context mContext,String text,int x,int y,int width){
		showPopupWindow(view, mContext, text, x, y, width,1.0f);
	}
	
	/**
	 * 显示普通的popupwindow
	 * @param view  	popupwindow的相对控件
	 * @param mContext  context
	 * @param text  	文本
	 * @param x			位置信息
	 * @param y			位置信息
	 */
	public static void showPopupWindow(View view,Context mContext,String text,int x,int y){
		showPopupWindow(view, mContext, text, x, y, 0,0.7f);
	}
	
	//隐藏imageAddressPopupWindow
		private static void closeImageAddressPopupWindow(Context mContext) throws Exception{
			if (imageAddressPopupWindow != null && imageAddressPopupWindow.isShowing()) {
				imageAddressPopupWindow.dismiss();
			}
			imageAddressPopupWindow = null;
			WindowManager.LayoutParams params = ((Activity) mContext)
					.getWindow().getAttributes();
			params.alpha = 1f;
			((Activity) mContext).getWindow().setAttributes(params);
		}
	
	//隐藏popupwindow
	public static void closePopupWindow(Context mContext) {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
		popupWindow = null;
		WindowManager.LayoutParams params = ((Activity) mContext)
				.getWindow().getAttributes();
		params.alpha = 1f;
		((Activity) mContext).getWindow().setAttributes(params);
	}
	
	
	//隐藏关注的popupwindow
//	public static void closePopupWindow1(Context mContext) {
//		if (popupWindow != null && popupWindow.isShowing()) {
////		popupWindow.dismiss();
//		}
//		popupWindow = null;
//		WindowManager.LayoutParams params = ((Activity) mContext)
//				.getWindow().getAttributes();
//		params.alpha = 1f;
//		((Activity) mContext).getWindow().setAttributes(params);
//	}
	public interface MyButtonListener{
		void okButton();
		void changeButton();
	}
	private MyButtonListener myButtonListener;
	
	private static void initImageAddressPop(View view,final Context mContext,final Activity activity,final MyButtonListener myButtonListener ,int layoutId, int y, int styleId){
		WindowManager windowManager = ((Activity) mContext).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		
		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(mContext).inflate(
				layoutId, null);
		
		
		TextView bt_ok = (TextView) contentView.findViewById(R.id.bt_ok);
		TextView bt_change = (TextView) contentView.findViewById(R.id.bt_change);
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try {
					closeImageAddressPopupWindow(mContext);
					myButtonListener.okButton();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		bt_change.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try {
					closeImageAddressPopupWindow(mContext);
					myButtonListener.changeButton();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		imageAddressPopupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		// 设置动画效果
		imageAddressPopupWindow.setAnimationStyle(styleId);
		WindowManager.LayoutParams params = ((Activity) mContext).getWindow()
				.getAttributes();
		params.alpha = 0.7f;
		((Activity) mContext).getWindow().setAttributes(params);
		imageAddressPopupWindow.setTouchable(true);
		imageAddressPopupWindow.setOutsideTouchable(false);
		imageAddressPopupWindow.setFocusable(true);
		
		imageAddressPopupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				try {
					closeImageAddressPopupWindow(mContext);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		// 我觉得这里是API的一个bug
//		popupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_pop_transparent));
		// 设置好参数之后再show
		imageAddressPopupWindow.showAsDropDown(view, MyBabyApp.screenWidth/2 - getWidth(contentView)/2 , y);
	}
	
	/**
	 * 显示带按钮的popupwindow
	 * @param view				popupwindow的相对控件
	 * @param mContext			context
	 * @param activity			Activity
	 * @param myButtonListener	监听回调
	 * @param LayoutId			布局ID
	 * @param y					位置信息
	 * @param styleId			动画的styleID
	 */
	public static void showImageAddressPop(View view,Context mContext,Activity activity,MyButtonListener myButtonListener,int layoutId,int y,int styleId){
		if (imageAddressPopupWindow != null) {
			try {
				closeImageAddressPopupWindow(mContext);
			} catch (Exception e) {
				imageAddressPopupWindow = null;
				showImageAddressPop(view, mContext, activity, myButtonListener, layoutId, y, styleId);
				e.printStackTrace();
			}
		} else {
			initImageAddressPop(view, mContext,activity,myButtonListener,layoutId,y,styleId);
		}
	}
	
	public static int getHeight(View view){
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
        view.measure(w, h);  
        int height =view.getMeasuredHeight();  
        return height;
	}
	
	public static int getWidth(View view){
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
        view.measure(w, h);  
        int width =view.getMeasuredWidth();  
        return width;
	}
	
}
