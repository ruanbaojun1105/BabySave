package mybaby.ui.more;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.umeng.analytics.MobclickAgent;

import org.xmlrpc.android.XMLRPCException;

import me.hibb.mybaby.android.R;
import mybaby.models.community.UserPushSetting;
import mybaby.rpc.BaseRPC.Callback;
import mybaby.rpc.UserRPC;
import mybaby.rpc.UserRPC.PushSettingListCallback;
import mybaby.ui.widget.CheckSwitchButton;
import mybaby.util.ActionBarUtils;

public class MessagePageActivity extends Activity{
	
	
	private CheckSwitchButton[] cbs;
	private String[] arrSettingKey = {"push_reply","push_like","push_friends_activity","push_follow","push_system"};
	
	private SharedPreferences sp;
	private boolean isSetting;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what==0) {
				//Toast.makeText(MessagePageActivity.this, "网络错误！请检查网络是否连接", Toast.LENGTH_SHORT).show();
				for (int i = 0; i < 5; i++) {
					cbs[i].setChecked(true);
					setOnCheckedChange(i);
				}
				return;
			}
			UserPushSetting[] arrSetting = (UserPushSetting[]) msg.obj;
			for (int i = 0; i < 5; i++) {
				isSetting = arrSetting[i].isSettingValue();
				cbs[i].setChecked(isSetting);
				setOnCheckedChange(i);
			}
		}
	};
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.tixingshezhi)); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.tixingshezhi)); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_page);
		sp = getSharedPreferences("push_message_state", MODE_PRIVATE);
		ActionBarUtils.initActionBar("消息提醒",MessagePageActivity.this);
		initView();
		if(!sp.getBoolean("isPushState", true)){
			loadData();
		}else{
			fillData();
		}
	}

	
	
	private void fillData() {
		for (int i = 0; i < 5; i++) {
			isSetting = sp.getBoolean("cbs"+i, true);
			cbs[i].setChecked(isSetting);
			setOnCheckedChange(i);
		}
	}

	private void setOnCheckedChange(final int i) {
		cbs[i].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				setStateChangeToNet(isChecked,i);
			}

			
		});
	}
	
	private void setStateChangeToNet(final boolean isChecked, final int i) {
		UserRPC.setPushSetting(arrSettingKey[i], isChecked,new Callback() {
			
			@Override
			public void onSuccess() {
				Editor editor = sp.edit();
				editor.putBoolean("cbs"+i, isChecked);
				editor.commit();
			}
			
			@Override
			public void onFailure(long id, XMLRPCException error) {
			}
		});
	}

	private void loadData() {
		UserRPC.getPushSetting(new PushSettingListCallback() {
			
			@Override
			public void onSuccess(UserPushSetting[] arrSetting) {
				Message msg = Message.obtain();
				msg.obj = arrSetting;
				handler.sendMessage(msg);
				Editor editor = sp.edit();
				editor.putBoolean("isPushState", true);
				editor.commit();
				
			}
			
			@Override
			public void onFailure(long id, XMLRPCException error) {
				if (handler==null)
					return;
				handler.sendEmptyMessage(0);
			}
		});
	}

	private void initView() {
		cbs = new CheckSwitchButton[5];
		cbs[0] = (CheckSwitchButton) findViewById(R.id.push_reply);
		cbs[1] = (CheckSwitchButton) findViewById(R.id.push_like);
		cbs[2] = (CheckSwitchButton) findViewById(R.id.push_friends_activity);
		cbs[3] = (CheckSwitchButton) findViewById(R.id.push_follow);
		cbs[4] = (CheckSwitchButton) findViewById(R.id.push_system);
	}

}
