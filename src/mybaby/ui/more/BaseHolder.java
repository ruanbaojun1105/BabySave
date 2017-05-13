package mybaby.ui.more;

import mybaby.models.community.Place;
import android.view.View;


public abstract class BaseHolder<T> {
	public View mRootView;
	private int mPosition;
	public T mData;
	
	public BaseHolder(){
		mRootView = initView();
		mRootView.setTag(this);
	}
	
	
	//设置数据
	public void setData(T mData){
		this.mData = mData;
		refreshView();
	}
	
	//获取数据
	public T getData(){
		return mData;
	}
	
	public View getRootView(){
		return mRootView;
	}


	public int getmPosition() {
		return mPosition;
	}

	public void setmPosition(int mPosition) {
		this.mPosition = mPosition;
	}
	
	public abstract void refreshView();
	public abstract View initView();
}
