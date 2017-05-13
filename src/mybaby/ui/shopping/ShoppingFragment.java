package mybaby.ui.shopping;

import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ShoppingFragment extends ListFragment {
	   private View rootView;//缓存Fragment view
	   
		@Override
	    public void onCreate(Bundle icicle) {
	        super.onCreate(icicle);
	        
	        MyBabyApp.initScreenParams(this.getActivity());

		}
		
	   @Override
	   public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) { 
			  if(rootView==null){  
		            rootView=inflater.inflate(R.layout.fragment_shopping, null);
		       }  
			  //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。  
		       ViewGroup parent = (ViewGroup) rootView.getParent();  
		       if (parent != null) {  
		           parent.removeView(rootView);  
		       }   
		       return rootView; 
	   }
	   
	   @Override
		public void onStart() {
	        super.onStart();

	    }
	   
	    @Override
		public void onResume() {
	        super.onResume();
	        
	    }
}
