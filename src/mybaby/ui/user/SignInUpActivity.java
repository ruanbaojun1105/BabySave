package mybaby.ui.user;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.UserRepository;
import mybaby.models.diary.PostRepository;
import mybaby.rpc.BlogRPC;
import mybaby.rpc.PostRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.broadcast.UpdateRedTextReceiver;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.util.StringUtils;
import mybaby.util.Utils;


public class SignInUpActivity extends Activity implements OnClickListener {
    private ProgressDialog mProgressDialog;
    private ConnectivityManager mSystemService;
    private EditText mEmailEdit;
    private EditText mPasswordEdit;
    private Button mOKButton;
    private Button mGPButton;

    public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart(MyBabyApp.currentBlog==null?"登陆":"注册"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
	    MobclickAgent.onResume(this);          //统计时长
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd(MyBabyApp.currentBlog == null ? "登陆" : "注册"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
	    MobclickAgent.onPause(this);
	}
    private void setCusActionbar(String title){
        View mActionBarView = LayoutInflater.from(this).inflate(
                R.layout.actionbar_title_holder_image, null);
        TextView tv_title = (TextView) mActionBarView.findViewById(R.id.actionbar_title);
        new UpdateRedTextReceiver((TextView) mActionBarView.findViewById(R.id.actionbar_back_badge)).regiest();
        TextView actionbar_back = (TextView) mActionBarView.findViewById(R.id.actionbar_back);
        actionbar_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_title.setText(title);
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(mActionBarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_up);

        setCusActionbar(getString(MyBabyApp.currentBlog!=null?R.string.sign_up:R.string.sign_in));
        mGPButton=(Button) findViewById(R.id.btn_getBack);
        mOKButton = (Button) findViewById(R.id.btn_ok);
        mEmailEdit = (EditText) findViewById(R.id.txt_email);
        mPasswordEdit = (EditText) findViewById(R.id.txt_password);
        mPasswordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {  
            @Override  
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {  
                if (actionId == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE) {  
                	setupBlogs();
                }  
                return true;  
            }  
        });
        
        mSystemService = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String username = extras.getString("username");
            if (username != null) {
                mEmailEdit.setText(username);
            }
        }
        mGPButton.setOnClickListener(this);
        mOKButton.setOnClickListener(this);
        mEmailEdit.requestFocus();
        
        if(MyBabyApp.currentBlog!=null){
        	mOKButton.setText(R.string.sign_up);
        	mGPButton.setVisibility(View.INVISIBLE);
        }else{
        	mOKButton.setText(R.string.sign_in);
        }
    }



    private void configureAccount() {
    	boolean isPhone=false;
    	boolean isEmail=false;
        final String email = mEmailEdit.getText().toString().trim();
        final String password = mPasswordEdit.getText().toString().trim();

        if ( email.equals("") || password.equals("")) {
            mProgressDialog.dismiss();
            Toast.makeText(SignInUpActivity.this, R.string.please_fill_out_all_the_fields,Toast.LENGTH_SHORT).show();
            return;
        }else if(!StringUtils.isValidEmail(email)){
        	if (!Utils.isMobileNO(email)) {
        		mProgressDialog.dismiss();
      		    Toast.makeText(SignInUpActivity.this, R.string.invalid_email_address,Toast.LENGTH_SHORT).show();
                return;
    		}else {
				isPhone=true;
			}
        }else {
			isEmail=true;
		}

        if(MyBabyApp.currentUser==null){//登录
        	if (isEmail) {
        		loginEmail(true,email, password);
			}
        	if (isPhone) {
        		loginEmail(false,email, password);
			}
        }
    }

    
    /**
     * 登陆,支持邮箱和手机号
     * @param 
     */
    private void loginEmail(Boolean isEmail,String email,String password) {
    	BlogRPC.signIn(isEmail,email, password, new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result){
            	PostRPC.getPosts(MyBabyApp.currentUser,  new XMLRPCCallback() {

                    @Override
                    public void onSuccess(long id, Object result){
                    	if(StringUtils.isEmpty(MyBabyApp.currentUser.getFrdTelephoneCountryCode())){
                    		MyBabyApp.currentUser.setFrdTelephoneCountryCode(86+"");
	                 		MyBabyApp.currentUser.setBzInfoModified(true);
	                 		UserRepository.save(MyBabyApp.currentUser);
                    	}
                        LoadMoreListViewFragment.askRed();
                    	mProgressDialog.dismiss();
                        setResult(RESULT_OK);
                        Intent intent = new Intent(SignInUpActivity.this, MyBayMainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    
                    @Override
                    public void onFailure(long id, XMLRPCException error){
                        if (MyBabyApp.currentUser!=null) {
                            PostRepository.clearPosts(MyBabyApp.currentUser.getUserId());
                            UserRepository.clear();
                            MyBabyApp.currentUser = null;
                        }
                    		//MyBaby.currentBlog=null;
                    		signInFaile("登陆失败,请检查用户名和密码!");
                    }
                });
            }
            
            @Override
            public void onFailure(long id, XMLRPCException error){
            		signInFaile("登陆失败");
            }
        });
	}
    private void signInFaile(final String message){
       new CustomAbsClass.doSomething(this) {
           @Override
           public void todo() {
               mProgressDialog.dismiss();
               Toast.makeText(SignInUpActivity.this,message,Toast.LENGTH_SHORT).show();
           }
       };
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // ignore orientation change
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
		case R.id.btn_ok:
			setupBlogs();
			break;
		case R.id.btn_getBack:
			CustomAbsClass.starWebViewIntent(this, Constants.MY_BABY_GPassword_URL);
		default:
			break;
		}
    }

    private void setupBlogs() {
        if (mSystemService.getActiveNetworkInfo() == null) {
        	Toast.makeText(SignInUpActivity.this, R.string.no_connection,Toast.LENGTH_SHORT).show();
        } else {
           mProgressDialog = ProgressDialog.show(SignInUpActivity.this, null, null);
           configureAccount();
        }
    }
}
