package mybaby.ui.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.hibb.mybaby.android.R;
import mybaby.BaseActivity;
import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.models.Register;
import mybaby.rpc.BaseRPC.Callback;
import mybaby.rpc.BaseRPC.CallbackForRegisterId;
import mybaby.rpc.UserRPC;
import mybaby.ui.community.DetailsActivity;
import mybaby.ui.broadcast.PostMediaTask;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.util.Utils;

@ContentView(R.layout.register)
public class RegisterActivity extends BaseActivity implements OnClickListener{
	
	@ViewInject(R.id.smscode_et)
	EditText smscode_et;
	@ViewInject(R.id.sendSms_tv)
	TextView sendSms_tv;
	@ViewInject(R.id.password_et)
	EditText password_et;
	@ViewInject(R.id.name_et)
	EditText name_et;
	@ViewInject(R.id.phone_et)
	EditText phone_et;
	@ViewInject(R.id.register_btn)
	Button register_btn;
	@ViewInject(R.id.pb_loading)
	ProgressBar pb_loading;
	@ViewInject(R.id.regist_scroll)
	ScrollView regist_scroll;
	
	private Activity activity;
	private int recLen = 0;
	private MyThread myrun;
	private boolean isIntcurrunt=false;//是否阻断线程
	private boolean isSmsSuccess=false;//是否中途成功
	//private TelephonyManager phoneMgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activity=this;
		initView();
		OverScrollDecoratorHelper.setUpOverScroll(regist_scroll);
		sendSms_tv.setOnClickListener(this);
		register_btn.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		DetailsActivity.dismissText(this,phone_et);
		super.onBackPressed();
	}

	@SuppressLint("InflateParams")
	private void initView() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(this).inflate(R.layout.actionbar_title_holder, null);
		view.findViewById(R.id.actionbar_back).setOnClickListener(this);
		((TextView) view.findViewById(R.id.actionbar_back)).setTypeface(MyBabyApp.fontAwesome);
		view.findViewById(R.id.actionbar_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		((TextView) view.findViewById(R.id.actionbar_title)).setText("注册");
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(view);
		
		
		/*try {
			//phoneMgr=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);//获得本机手机号
			//getLine1Number=phoneMgr.getLine1Number();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		name_et.setHint(MyBabyApp.currentUser.getName());
		phone_et.setHint("请输入手机号码");
	}

	/**
	 * 90秒一次循环
	 */
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){			// handle
		@SuppressLint("ResourceAsColor")
		public void handleMessage(Message msg){
			switch (msg.what) {
			case 1:
				sendSms_tv.setText(recLen+" 's");
				Log.i("reclen", recLen+"");
				if (recLen==0||isSmsSuccess) {
					sendSms_tv.setText("重新发送");
					handler.removeCallbacks(myrun);
					sendSms_tv.setTextColor(activity.getResources().getColor(R.color.mainThemeColor));
					isIntcurrunt=true;
					sendSms_tv.setEnabled(true);
					return;
				}
				recLen--;
			}
			super.handleMessage(msg);
		}
	};
	public class MyThread implements Runnable{		// thread
		@Override
		public void run(){
			while(!isIntcurrunt){
				try{
					Thread.sleep(1000);		// sleep 1000ms
					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);

				}catch (Exception e) {
				}
			}
		}
	}

	@SuppressLint({ "ResourceAsColor", "ShowToast" })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sendSms_tv:
			String phone=phone_et.getText().toString();
			if (TextUtils.isEmpty(phone)) {
				Toast.makeText(activity, "请输入手机号", Toast.LENGTH_SHORT).show();
			}else {
				if (Utils.isMobileNO(phone)) {
					checkPhone(phone);
				}else {
					Toast.makeText(activity, "手机号格式错误！", Toast.LENGTH_SHORT).show();
				}
			}
			
			break;
		case R.id.register_btn:
			Register register=getRegisterInfo();
			if (null!=register) {
				checkSmsCode(register);
			}
			break;
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler=null;
	}
	
	/**
	 * 改变发送短信的按钮状态
	 */
	@SuppressLint("ResourceAsColor")
	private void initSendBtn() {
		sendSms_tv.setEnabled(false);
		smscode_et.setText("");
		sendSms_tv.setTextColor(activity.getResources().getColor(R.color.gray));
		isSmsSuccess=false;//是否中途成功
		isIntcurrunt=false;//线程是否中断
		recLen=90;//倒计时90秒
		myrun=new MyThread();
		new Thread(myrun).start();
		//开始发送验证码
		sendSmsCode(phone_et.getText().toString());
	}
	/**
	 * 获得数据和判断数据正确性
	 */
	private Register getRegisterInfo(){
		Register register=new Register();
		register.setCountryCode(86);
		String name=name_et.getText().toString();
		String phone=phone_et.getText().toString();
		String pass=password_et.getText().toString();
		String code=smscode_et.getText().toString();
		if (TextUtils.isEmpty(name)) {
			name=MyBabyApp.currentUser.getName();
		}
		if (!TextUtils.isEmpty(phone)) {
			if (Utils.isMobileNO(phone)) {
				register.setMobile(phone);
			}else {
				Toast.makeText(activity, "手机号格式错误", Toast.LENGTH_SHORT).show();
				return null;
			}
		}else {
			Toast.makeText(activity, "请输入手机号码", Toast.LENGTH_SHORT).show();
			return null;
		}
		if (TextUtils.isEmpty(code)) {
			Toast.makeText(activity, "验证码不可为空", Toast.LENGTH_SHORT).show();
			return null;
		}else {
			register.setSmscode(code);
		}
		if (!TextUtils.isEmpty(name)) {
			register.setDisplayname(name);
		}else {
			Toast.makeText(activity, "昵称不可为空", Toast.LENGTH_SHORT).show();
			return null;
		}
		if (!TextUtils.isEmpty(pass)) {
			if (pass.length()>5) {
				register.setUserpass(pass);
			}else {
				Toast.makeText(activity, "密码至少是六位字符长度", Toast.LENGTH_SHORT).show();
				return null;
			}
		}else {
			Toast.makeText(activity, "密码不可为空", Toast.LENGTH_SHORT).show();
			return null;
		}
		return register;
		
	}
	
	/**
	 * 发送验证码
	 * @return 
	 */
	private void sendSmsCode(String phoneNumber){
		UserRPC.sendSmsCode(phoneNumber, new Callback() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething(activity) {
					
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						pb_loading.setVisibility(View.GONE);
						sendSms_tv.setVisibility(View.VISIBLE);
						Toast.makeText(activity, "发送成功", Toast.LENGTH_SHORT).show();
					}
				};
			}
			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething(activity) {
					
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						isSmsSuccess=true;
						pb_loading.setVisibility(View.GONE);
						sendSms_tv.setVisibility(View.VISIBLE);
						Toast.makeText(activity, "发送失败", Toast.LENGTH_SHORT).show();
					}
				};
			}
		});
		
	}
	/**
	 * 检查验证码
	 * @return 
	 */
	private void checkSmsCode(final Register register){
		UserRPC.checkSmsCode(register.getSmscode(),register.getMobile(),new Callback() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething(activity) {
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						registerUser(register);	
					}
				};
			}
			
			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething(activity) {
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						Toast.makeText(activity, "验证失败", Toast.LENGTH_SHORT).show();
					}
				};
			}
		});
		
	}
	
	/**
	 * 验证手机号是否已注册
	 */
	private void checkPhone(String phone) {
		new CustomAbsClass.doSomething(activity) {
			@Override
			public void todo() {
				pb_loading.setVisibility(View.VISIBLE);
				sendSms_tv.setVisibility(View.GONE);
			}
		};

		UserRPC.checkPhoneHasRegiest(phone, "86", new Callback() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething(activity) {
					
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						initSendBtn();
					}
				};
			}
			
			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething(activity) {
					
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						pb_loading.setVisibility(View.GONE);
						sendSms_tv.setVisibility(View.VISIBLE);
						Toast.makeText(activity, Utils.isNetworkAvailable()?"此手机号已注册":"请检查网络是否连接", Toast.LENGTH_SHORT).show();
					}
				};
			}
		});
	}
	
	/**
	 * 新用户注册
	 */
	private void registerUser(final Register register){
		UserRPC.toRegister(register, new CallbackForRegisterId() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething(activity) {
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						Toast.makeText(activity, "注册成功", Toast.LENGTH_SHORT).show();
						PostMediaTask.sendBroadcast(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done, null);
    	            	MyBabyApp.getSharedPreferences().edit().putBoolean("isSignUp", true).commit();
    	            	setResult(RESULT_OK);
    	            	finish();
					}
				};
			}
			
			@Override
			public void onHasPhone() {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething(activity) {
					
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						Toast.makeText(activity, "此手机号已注册", Toast.LENGTH_SHORT).show();
					}
				};
			}
			
			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething(activity) {
					
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						Toast.makeText(activity, "注册失败", Toast.LENGTH_SHORT).show();
					}
				};
			}
		});
	}
}
