package mybaby.ui.more.personpage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.xmlrpc.android.XMLRPCException;

import java.util.ArrayList;
import java.util.Map;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.User;
import mybaby.models.community.TopicCategory;
import mybaby.rpc.BaseRPC.Callback;
import mybaby.rpc.notification.TopicCategoryRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.util.ActionBarUtils;
import mybaby.util.AppUIUtils;
import mybaby.util.DialogShow;
import mybaby.util.DialogShow.DeleteListener;

/**
 * 关注话题activity
 * @author Administrator
 *
 */
public class PersonTopicActivity extends Activity {
	
	private ListView iv_topic;
	private MyAdapter adapter;
	private TextView tv_title;
	private TextView tv_back;
	private ArrayList<Map<String, Object>> item;
	private User user;
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.gerenhuatiliebiao)); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.gerenhuatiliebiao)); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
											// onPageEnd 在onPause 之前调用,因为
											// onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic);
		ActionBarUtils.initActionBar("话题",PersonTopicActivity.this);
		iv_topic = (ListView) findViewById(R.id.iv_topic);
		Bundle bundle =getIntent().getExtras();
		user = (User) bundle.getSerializable("user");
		item = (ArrayList<Map<String, Object>>) bundle.getSerializable("item");
		final int[] parentIds = bundle.getIntArray("parentIds");
		adapter = new MyAdapter(this,item);
		iv_topic.setAdapter(adapter);
		//OverScrollDecoratorHelper.setUpOverScroll(iv_topic);
		iv_topic.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				TopicCategory category = (TopicCategory) item.get(position).get("item");
				CustomAbsClass.openTopicTitleList(PersonTopicActivity.this, category);
			}
		});
		if(user.isSelf()){
			iv_topic.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					setOnLongClick(position);
					return true;
				}
			});
		}
	}
	
	
	
	/**
	 * 长点击事件的响应
	 * @param position 点击条目所对应的position
	 */
	private void setOnLongClick(int position) {
		TopicCategory category = (TopicCategory) item.get(
				position).get("item");
		DialogShow dialogShow = new DialogShow(PersonTopicActivity.this);
		dialogShow.showDeleteDialog("取消关注","确定取消关注",true,category.getCategoryId(),new DeleteListener() {
			@Override
			public void delete(int id, DialogInterface dialog) {
				deleteTopicByNet(id, dialog);
			}

		});
	}

	/**
	 * 向服务器发送请求，删除话题
	 * @param topicId  要删除的话题的ID
	 * @param dialog   显示删除的对话框（需要在数据获取返回结果后手动dismiss（无论是否成功））
	 */
	private void deleteTopicByNet(final int topicId, final DialogInterface dialog) {
		TopicCategoryRPC.unfollow(topicId, new Callback() {
			
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
						deleteItemTopic(topicId);
						Toast.makeText(MyBabyApp.getContext(), "已取消该话题", Toast.LENGTH_SHORT).show();
						//dialog.dismiss();
					}
				});
				Intent data = new Intent();
				data.setAction(Constants.BroadcastAction.BroadcastAction_Persontopic_Unfollow);
				LocalBroadcastManager.getInstance(MyBabyApp.getContext()).sendBroadcast(data);
			}
			
			@Override
			public void onFailure(long id, XMLRPCException error) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(PersonTopicActivity.this, "网络错误！请检查网络是否连接", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});
			}
		});
	}
	
	/**
	 * 删除话题后在本地更新数据
	 * @param id TopicID
	 */
	private void deleteItemTopic(int id) {
		for (int i = 0; i < item.size(); i++) {
			TopicCategory category = (TopicCategory) item.get(i).get("item");
			if (category.getCategoryId() == id) {
				item.remove(i);
			}
		}
		adapter.setItem(item);
		iv_topic.setAdapter(adapter);
	}
	
	private class MyAdapter extends BaseAdapter{
		Context context;
		ArrayList<Map<String, Object>> item;
		public void setItem(ArrayList<Map<String, Object>> item) {
			this.item = item;
		}

		public  MyAdapter(Context context,ArrayList<Map<String, Object>> item) {
			this.context=context;
			this.item=item;
		}
		
		@Override
		public int getCount() {
			return item.size();
		}

		@Override
		public Object getItem(int position) {
			return item.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup arg2) {
			ViewHolder holder = null;
			if(contentView == null){
				holder = new ViewHolder();
				LayoutInflater mInflater = LayoutInflater.from(AppUIUtils.getContext());
				contentView = mInflater.inflate(R.layout.item_list, null);
				holder.tv_content = (TextView) contentView.findViewById(R.id.tv_content);
				contentView.setTag(holder);
			}else{
				holder = (ViewHolder) contentView.getTag();
			}
			holder.tv_content.setText(((TopicCategory) item.get(position).get("item")).getTitle());
			return contentView;
		}
		
	}
	
	class ViewHolder{
		TextView tv_content;
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}
