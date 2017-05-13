package mybaby.util;

import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MyToastUtil {
//	/**
//	 * 自定义的土司，把归属地显示在界面上。
//	 * 
//	 * @param address
//	 */
//	public void showMyToast(String address) {
//		View view = View.inflate(this, R.layout.toast_showaddress, null);
//		
//		int which = sp.getInt("which", 0);
//		// "半透明","活力橙","卫士蓝","金属灰","苹果绿"
//		view.setBackgroundResource(bgs[which]);
//		TextView tv_address = (TextView) view
//				.findViewById(R.id.tv_toast_address);
//		tv_address.setText(address);
//		mParams = new WindowManager.LayoutParams();
//		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//		//修改完左上角对其
//		mParams.gravity = Gravity.LEFT+Gravity.TOP;
//		mParams.x = sp.getInt("lastx", 0);
//		mParams.y = sp.getInt("lasty", 0);
//		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
////				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE   自定义的土司需要用户触摸
//				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//		mParams.format = PixelFormat.TRANSLUCENT;
////		mParams.type = WindowManager.LayoutParams.TYPE_TOAST; 土司窗体天生不响应触摸事件
//		mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
//		// 把view添加到手机窗体上。
//		windowManager.addView(view, mParams);
//
//		// 给view对象设置一个触摸事件。
//		view.setOnTouchListener(new OnTouchListener() {
//			int startX  ;
//			int startY  ;
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//					Log.i(TAG,"手指按到了控件上");
//					startX = (int) event.getRawX();
//					startY = (int) event.getRawY();
//					Log.i(TAG,"开始位置："+startX+","+startY);
//					break;
//				case MotionEvent.ACTION_MOVE:
//					Log.i(TAG,"手指在控件上移动");
//					int newX = (int) event.getRawX();
//					int newY = (int) event.getRawY();
//					Log.i(TAG,"新的位置："+newX+","+newY);
//					int dx = newX - startX;
//					int dy = newY - startY;
//					Log.i(TAG,"偏移量："+dx+","+dy);
//					Log.i(TAG,"更新控件在屏幕上的位置");
//					mParams.x +=dx;
//					mParams.y +=dy;
//					if(mParams.x<0){
//						mParams.x = 0;
//					}
//					if(mParams.y<0){
//						mParams.y = 0;
//					}
//					if(mParams.x>(windowManager.getDefaultDisplay().getWidth()-view.getWidth())){
//						mParams.x=(windowManager.getDefaultDisplay().getWidth()-view.getWidth());
//					}
//					if(mParams.y>(windowManager.getDefaultDisplay().getHeight()-view.getHeight())){
//						mParams.y=(windowManager.getDefaultDisplay().getHeight()-view.getHeight());
//					}
//					windowManager.updateViewLayout(view, mParams);
//					startX = (int) event.getRawX();
//					startY = (int) event.getRawY();
//					break;
//					
//				case MotionEvent.ACTION_UP:
//					Log.i(TAG,"手指离开屏幕");
//					Editor editor = sp.edit();
//					editor.putInt("lastx", mParams.x);
//					editor.putInt("lasty", mParams.y);
//					editor.commit();
//					break;
//				}
//				return true;
//			}
//		});
//	}
}
