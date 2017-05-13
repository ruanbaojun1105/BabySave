package mybaby.ui.community;

import org.xmlrpc.android.XMLRPCException;
import me.hibb.mybaby.android.R;
import mybaby.models.community.TopicCategory;
import mybaby.rpc.notification.TopicCategoryRPC;
import mybaby.rpc.notification.TopicCategoryRPC.ListCallback;
import mybaby.ui.community.adapter.TopicMoreListAdapter;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.customclass.CustomAbsClass.doSomething;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class TopicMoreActivity extends Activity implements OnClickListener,OnKeyListener{

	private ListView lv_topicmore_search;
	
	private EditText search_edt;
	
	private Context context;
	
	private View headView;
	
	private TextView search_title;
	
	private View footerView;
	
	boolean hasAddfooter=false;
	
	boolean hasAddhead=false;
	
	private BaseAdapter adapter=null;

	@Override
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("搜索话题"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
	    MobclickAgent.onResume(this);          //统计时长
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("搜索话题"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
	    MobclickAgent.onPause(this);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		lv_topicmore_search= (ListView) findViewById(R.id.lv_topicmore_search);
		setContentView(R.layout.activity_topic_more);
		initData();
	}
	
	/**
	 * 初始化
	 */
	private void initData() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(this).inflate(
				R.layout.actionbar_topic_more_edit, null);
		view.findViewById(R.id.iv_back).setOnClickListener(this);
		search_edt=(EditText) view.findViewById(R.id.search_edt);
		search_edt.setOnKeyListener(this);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(view);
		context=this;
		
		/**
		 * 实时监听输入框
		 */
		search_edt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if (!arg0.toString().equals("")) {
					searchByTitle(arg0.toString(),search_title);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		headView=LayoutInflater.from(context).inflate(
				R.layout.topic_more_item, null);
		footerView=LayoutInflater.from(context).inflate(
				R.layout.footer_cus_listview, null);
		lv_topicmore_search.addFooterView(footerView);
		footerView.setVisibility(View.GONE);
		LinearLayout toedit_lin=(LinearLayout) headView.findViewById(R.id.lin_edittopic);
		toedit_lin.setOnClickListener(this);
		search_title=(TextView) headView.findViewById(R.id.search_name);
	}

	
	/**
	 * item onclick
	 */
	
	
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_back:
			onBackPressed();
			break;
		case R.id.lin_edittopic:
			CustomAbsClass.starTopicEditIntent(context, 0, search_title.getText().toString(), null);
			break;
		}
	}

	/**
	 * 初始化listview
	 * 在头部必添加发表新话题
	 * 根据标题模糊查询话题
	 */
	private void searchByTitle(String title,TextView search_title) {
		// TODO Auto-generated method stub
		
		//开始加载搜索
		if (!hasAddhead) {
			lv_topicmore_search.addHeaderView(headView);
		}
		search_title.setText(title);
		
		if (!hasAddfooter) {
			footerView.setVisibility(View.VISIBLE);
		}else {
			footerView.setVisibility(View.VISIBLE);
		}
		
		lv_topicmore_search.setAdapter(null);
		hasAddhead=true;
		hasAddfooter=true;
		TopicCategoryRPC.getByTitle(title, new ListCallback() {
			
			@Override
			public void onSuccess(final TopicCategory[] arrCategory) {
				// TODO Auto-generated method stub
				new doSomething(TopicMoreActivity.this) {
					
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						
						adapter=new TopicMoreListAdapter(arrCategory,context);
						lv_topicmore_search.setAdapter(adapter);
						if (lv_topicmore_search.getAdapter()!=null) {
							adapter.notifyDataSetChanged();
						}
						footerView.setVisibility(View.GONE);
						hasAddhead=true;
						
						lv_topicmore_search.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								CustomAbsClass.starTopicEditIntent(context, 
										arrCategory[arg2-1].getId(),arrCategory[arg2-1].getTitle(), null);
							}
						});
					}
				};
			}
			
			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub
				new doSomething(TopicMoreActivity.this) {
					
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						footerView.setVisibility(View.GONE);
						hasAddhead=true;
					}
				};
			}
		});
		
	}
	
	
	@Override
	public boolean onKey(View view, int keyCode, KeyEvent arg2) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_ENTER&& arg2.getAction() == KeyEvent.ACTION_DOWN){  
			InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
			if (!search_edt.getText().toString().equals("")) {
				searchByTitle(search_edt.getText().toString(),search_title);
			}else {
				Toast.makeText(context, "搜索内容为空", Toast.LENGTH_SHORT).show();
			}
			 if(imm.isActive()){  
				 imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0 );  
			 }  
			 return true;
		}
		return false;
	}
	
	
}
