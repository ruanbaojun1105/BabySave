package mybaby.ui.user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.umeng.analytics.MobclickAgent;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;
import org.xutils.common.util.LogUtil;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.Blog;
import mybaby.models.BlogRepository;
import mybaby.models.User;
import mybaby.models.UserRepository;
import mybaby.models.community.Place;
import mybaby.models.community.UserPlaceSetting;
import mybaby.models.diary.Post;
import mybaby.models.diary.PostRepository;
import mybaby.models.person.Baby;
import mybaby.models.person.FamilyPerson;
import mybaby.models.person.Person;
import mybaby.models.person.SelfPerson;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.BlogRPC;
import mybaby.ui.MediaHelper;
import mybaby.ui.MediaHelper.MediaHelperCallback;
import mybaby.ui.MyBabyApp;
import mybaby.ui.WelcomeActivity;
import mybaby.ui.broadcast.PostMediaTask;
import mybaby.ui.broadcast.UpdateRedTextReceiver;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.customclass.CustomLinkTextView;
import mybaby.ui.community.holder.ActivityItem;
import mybaby.ui.main.MainUtils;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.posts.person.PersonAvatar;
import mybaby.ui.posts.person.PersonAvatar.CameraShowType;
import mybaby.util.DateUtils;
import mybaby.util.StringUtils;
import mybaby.util.Utils;
import rx.Subscriber;

public class PersonEditActivity extends AppCompatActivity implements MediaHelperCallback{
	private Person mPerson;
	private boolean mIsBaby;

    private MediaHelper mMediaHelper;
	private PersonAvatar mPersonAvatar;

	private java.text.DateFormat mDateFormat;
	private Date mDateBirthday;
	private String mName;

	private EditText mEditName;
	private TextView mTextBirthday;

	private LinearLayout mBirthdayContainer;
	private LinearLayout mBirthdayContainerLine;
	private LinearLayout mPersonTypeContainer;
	private LinearLayout mPersonTypeContainerLine;
	private LinearLayout mBabyTypeContainer;
	private LinearLayout mBabyTypeContainerLine;

	private TextView mMomCheckButton;
	private TextView mDadCheckButton;

	private TextView mGrilCheckButton;
	private TextView mBoyCheckButton;
	private TextView baby_tip;
	private TextView iconBabyType;

	private TextView user_ipc_tv;//用户协议

	private int mPersonType;
	private int mBabyType;
	private boolean hasCheckBabyType;
	private Button btn_ok;
	private String toastTip="请选择宝贝的性别";
	private Toolbar toolbar;
	
	
	private ProgressDialog mProgressDialog;
	

	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart(MyBabyApp.currentBlog == null ? getString(R.string.xinyonghu) : getString(R.string.jiarenxinxiweihu)); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
	    MobclickAgent.onResume(this);          //统计时长
		if (MyBabyApp.getSharedPreferences().getInt("look_guide", 0)!=MyBabyApp.version) {
			CustomAbsClass.starGuideActivity(this);
		}
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd(MyBabyApp.currentBlog == null ? getString(R.string.xinyonghu) : getString(R.string.jiarenxinxiweihu)); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
	    MobclickAgent.onPause(this);
	}
	
	@SuppressLint("NewApi")
	@Override 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_edit);


		//StatusBarUtils.setWindowStatusBarColor(this, MyBabyApp.currentBlog == null ? android.R.color.transparent: R.color.bg_gray);

		/*if (MyBabyApp.currentBlog == null){
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}*/
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.addView(LayoutInflater.from(this).inflate(R.layout.actionbar_title, null));
		setSupportActionBar(toolbar);


        mDateFormat=DateFormat.getDateFormat(this);
        mIsBaby=true;
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            int personId = extras.getInt("personId");
            if(personId>0){
				Post post=PostRepository.load(personId);
				if (post==null) {
					finish();
					return;
				}
            	mPerson=post.createPerson();
            	mIsBaby=(mPerson.getClass()==Baby.class);
            }else{
            	mIsBaby=extras.getBoolean("isBaby");
            }
        }
        
        if(MyBabyApp.currentBlog != null){
        	if(mPerson != null && mPerson.getData().getIsSelf()){
				setCusActionbar("我");
        	}else{
				setCusActionbar(mIsBaby?"宝宝":"家人");
        	}
        }else {
			toolbar.setVisibility(View.GONE);
		}
        
        initControls();
        mMediaHelper=new MediaHelper(this,this);


	}

	private void setCusActionbar(String title){
		TextView tv_title = (TextView) toolbar.findViewById(R.id.actionbar_title);
		new UpdateRedTextReceiver((TextView) toolbar.findViewById(R.id.actionbar_back_badge)).regiest();
		TextView actionbar_back = (TextView) toolbar.findViewById(R.id.actionbar_back);
		tv_title.setText(title);
		TextView rightBtn=(TextView) toolbar.findViewById(R.id.right_button);
		rightBtn.setVisibility(View.VISIBLE);
		rightBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickOK(btn_ok);
			}
		});
		actionbar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (this == null)
					return;
				finish();
			}
		});
	}


	private void initControls(){
		//ͷ��
		int avatarWidth=(int) (MyBabyApp.screenWidth/3.6);//��ͨͷ��
        FrameLayout avatarContainer = (FrameLayout) findViewById(R.id.avatar_container);
        
        RelativeLayout.LayoutParams lp_avatar_container=(RelativeLayout.LayoutParams) avatarContainer.getLayoutParams();
        lp_avatar_container.width=avatarWidth;
        lp_avatar_container.height=avatarWidth;
        avatarContainer.setLayoutParams(lp_avatar_container);

        mPersonAvatar=new PersonAvatar(this, avatarContainer,null, mPerson, false,
        		                     mPerson==null ? CameraShowType.nullAvatar : CameraShowType.already);
        mPersonAvatar.setAvatarOnClickListener(new View.OnClickListener() {
													@Override
													public void onClick(View v) {
														mMediaHelper.launchPictureLibrary(true);
													}
		 										});
        mPersonAvatar.setCameraOnClickListener(new View.OnClickListener() {
													@Override
													public void onClick(View v) {
														mMediaHelper.launchPictureLibrary(true);
													}
												}); 
        //icon
        TextView iconBirthday = (TextView) findViewById(R.id.icon_birthday);
        TextView iconName = (TextView) findViewById(R.id.icon_name);
        TextView iconPersonType = (TextView) findViewById(R.id.icon_person_type);
		iconBabyType = (TextView) findViewById(R.id.icon_baby_type);
		baby_tip = (TextView) findViewById(R.id.baby_tip);
        iconBirthday.setTypeface(MyBabyApp.fontAwesome);
        iconName.setTypeface(MyBabyApp.fontAwesome);
        iconPersonType.setTypeface(MyBabyApp.fontAwesome);
		iconBabyType.setTypeface(MyBabyApp.fontAwesome);

		user_ipc_tv= (TextView) findViewById(R.id.user_ipc);

        //container
    	mBirthdayContainer=(LinearLayout) findViewById(R.id.birthday_container);
    	mBirthdayContainerLine=(LinearLayout) findViewById(R.id.birthday_container_line);
        mPersonTypeContainer=(LinearLayout) findViewById(R.id.person_type_container);
        mPersonTypeContainerLine=(LinearLayout) findViewById(R.id.person_type_container_line);
		mBabyTypeContainer=(LinearLayout) findViewById(R.id.baby_type_container);
		mBabyTypeContainerLine=(LinearLayout) findViewById(R.id.baby_type_container_line);
        
		//名字
		mEditName = (EditText) findViewById(R.id.txt_name);

		//生日
		mTextBirthday = (TextView) findViewById(R.id.txt_birthday);
		mTextBirthday.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDateDialog((TextView)v);
			}
		});
		
    	//person type
        mMomCheckButton=(TextView) findViewById(R.id.person_type_mom);
        mDadCheckButton=(TextView) findViewById(R.id.person_type_dad);
        setPersonType(Post.personType.Mom.ordinal());//默认勾选妈妈
        mMomCheckButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setPersonType(Post.personType.Mom.ordinal());
			}
		});
        mDadCheckButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setPersonType(Post.personType.Dad.ordinal());
			}
		});

		//baby type
		mGrilCheckButton=(TextView) findViewById(R.id.baby_type_gril);
		mBoyCheckButton=(TextView) findViewById(R.id.baby_type_boy);
		//setPersonType(Post.gender.Female.ordinal());//默认勾选女孩
		mGrilCheckButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setBabyType(Post.gender.Female.ordinal());
			}
		});
		mBoyCheckButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setBabyType(Post.gender.Male.ordinal());
			}
		});
		mBabyType=Post.gender.Unknown.ordinal();
        //确定
		btn_ok = (Button) findViewById(R.id.btn_ok);
		RxView.clicks(btn_ok) // RxBinding 代码
				.throttleFirst(1500, TimeUnit.MILLISECONDS) // 设置防抖间隔为 1000ms
				.subscribe(new Subscriber<Void>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(Void aVoid) {
						clickOK(btn_ok);
					}
				});
		/*btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickOK(btn_ok);
			}
		});*/
		//login
		findViewById(R.id.sign_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent signIn = new Intent(PersonEditActivity.this, SignInUpActivity.class);
	            startActivity(signIn);
			}
		});
        
		ImageView bg = (ImageView) findViewById(R.id.main_bg);
        
        //���ֲ�ͬ�������
        if(MyBabyApp.currentBlog==null){
			mEditName.setHint(R.string.nickname_of_baby);
			setPersonType(Post.personType.Mom.ordinal());
			setUser_ipc_tv_blue(this, user_ipc_tv, new OnClickListener() {
				@Override
				public void onClick(View view) {
					CustomAbsClass.starWebViewIntent(PersonEditActivity.this, Constants.MY_BABY_UserPIC_URL);
				}
			});
        }else{
        	findViewById(R.id.sign_btn).setVisibility(View.GONE);
        	btn_ok.setVisibility(View.GONE);
        	bg.setVisibility(View.GONE);
			user_ipc_tv.setVisibility(View.GONE);
			mPersonTypeContainer.setVisibility(View.GONE);
			mPersonTypeContainerLine.setVisibility(View.GONE);

        	if(mIsBaby){
        		mEditName.setHint(R.string.nickname_of_baby);
			}else{
				mBirthdayContainer.setVisibility(View.GONE);
				mBirthdayContainerLine.setVisibility(View.GONE);
				mBabyTypeContainer.setVisibility(View.GONE);
				mBabyTypeContainerLine.setVisibility(View.GONE);
				mEditName.setHint(R.string.name);
				mEditName.setImeOptions(EditorInfo.IME_ACTION_DONE);
			}
        	
        	if(mPerson!=null){
        		mEditName.setText(mPerson.getName());
        		if(mIsBaby){
        			mTextBirthday.setText(mDateFormat.format(mPerson.getBirthday()));
        			mTextBirthday.setTextColor(Color.BLACK);
					boolean a=DateUtils.lngDatetime2Date(mPerson.getBirthday())>System.currentTimeMillis();
					mBabyTypeContainer.setVisibility(a?View.GONE:View.VISIBLE);
					mBabyTypeContainerLine.setVisibility(a?View.GONE:View.VISIBLE);
        		}
				setBabyType(mPerson.getData().getGenderNumber());
    			setPersonType(mPerson.getData().getPersonTypeNumber());
        	}else{
        		setPersonType(Post.personType.Other.ordinal());
        	}
        }
	}

	public void setUser_ipc_tv_blue(Context context,TextView user_ipc_tv,View.OnClickListener titleListener) {
		final ActivityItem.Clickable clickTitle = new ActivityItem.Clickable(titleListener, context,context.getResources().getColor(R.color.blue_light));
		String content = "创建即同意用户协议";
		user_ipc_tv.setText(content);
		SpannableString sp = new SpannableString(content);
		sp.setSpan(new ForegroundColorSpan(context.getResources().
						getColor(R.color.light_gray)),0,5,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		sp.setSpan(clickTitle, 5, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		user_ipc_tv.setText(sp);
		user_ipc_tv.setMovementMethod(CustomLinkTextView.LocalLinkMovementMethod
				.getInstance());

	}

	private void setPersonType(int personType){
		mPersonType=personType;
		mMomCheckButton.setBackgroundResource(mPersonType == Post.personType.Mom.ordinal() ? R.drawable.text_view_button : 0);
		mDadCheckButton.setBackgroundResource(mPersonType == Post.personType.Dad.ordinal() ? 0 : R.drawable.text_view_button);
	}
	private void setBabyType(int babyTypes){
		mBabyType=babyTypes;
		if(mBabyType==Post.gender.Female.ordinal()){
			mGrilCheckButton.setBackgroundResource(R.drawable.text_view_button);
			mBoyCheckButton.setBackgroundResource(0);
			hasCheckBabyType=true;
		}else if(mBabyType==Post.gender.Male.ordinal()){
			mGrilCheckButton.setBackgroundResource(0);
			mBoyCheckButton.setBackgroundResource(R.drawable.text_view_button);
			hasCheckBabyType=true;
		}else{
			mGrilCheckButton.setBackgroundResource(0);
			mBoyCheckButton.setBackgroundResource(0);
		}
		//mGrilCheckButton.setBackgroundResource(mBabyType==Post.gender.Female.ordinal()?R.drawable.text_view_button:0);
		//mBoyCheckButton.setBackgroundResource(mBabyType==Post.gender.Male.ordinal()?0:R.drawable.text_view_button);
	}

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if(MyBabyApp.currentBlog == null){ 
	        //getMenuInflater().inflate(R.menu.main_create, menu);
        }else{
        	getMenuInflater().inflate(R.menu.person_edit, menu);
        }
        return true;
    }
    
    // Menu actions
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
		    case R.id.menu_main_create_sign_in:
	            Intent signIn = new Intent(PersonEditActivity.this, SignInUpActivity.class);
	            startActivityForResult(signIn,R.id.menu_main_create_sign_in);
	            return true;
		    case R.id.menu_person_edit_ok:
		    	clickOK(btn_ok);
	            return true;
	        case android.R.id.home:
	        	setResult(RESULT_CANCELED);
	        	finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}*/
	
	private void clickOK(final Button button){

		mName = mEditName.getText().toString().trim();
		mName= (TextUtils.isEmpty(mName)?"宝宝":mName);
        String strBirthday = mTextBirthday.getText().toString().trim();
        
        if(mIsBaby){
            if(mName.equals("") ){
            	mName=getResources().getString(R.string.baby);
            }
        	
        	if(strBirthday.equals("")){
	        	showDateDialog(mTextBirthday);
	        	return;
        	}
	        try {
	        	mDateBirthday=mDateFormat.parse(strBirthday);
			} catch (ParseException e) {
				showDateDialog(mTextBirthday);
	        	return;
			}
			if(!hasCheckBabyType&&mBabyTypeContainer.getVisibility()==View.VISIBLE){
				Toast.makeText(this, toastTip,Toast.LENGTH_SHORT).show();
				mEditName.requestFocus();
				return;
			}
        }else {
			if(mName.equals("") ){
				Toast.makeText(this, R.string.please_fill_out_all_the_fields,Toast.LENGTH_SHORT).show();
				mEditName.requestFocus();
				return;
			}
		}
		button.setEnabled(false);
    	if(MyBabyApp.currentBlog==null){
			if (!Utils.isNetworkAvailable()) {
				Toast.makeText(this, R.string.neterror, Toast.LENGTH_SHORT).show();
				button.setEnabled(true);
			}else{
				mProgressDialog = ProgressDialog.show(PersonEditActivity.this, null, getString(R.string.loadings));
				//先要匿名创建用户
				createNewUser(this,mName, mDateBirthday, mPersonType,mBabyType,mPersonAvatar.getNewAvatar());
				BlogRPC.anonymousSignUp(new XMLRPCCallback() {
					@Override
					public void onSuccess(long id, Object result) {
						interfaLogin.onLoginSuccess();
					}

					@Override
					public void onFailure(long id, XMLRPCException error) {
						interfaLogin.onLoginFail();
					}
				});
			}
		} else {
			AddOrModifyPerson(mName, mDateBirthday == null ? 0 : mDateBirthday.getTime(), mPersonType,mBabyType);
			setResult(RESULT_OK);
			finish();
    	}
	}
	private MainUtils.LoginIM interfaLogin=new MainUtils.LoginIM() {
		@Override
		public void onLoginFail() {
			try {
				PostRepository.clearPosts(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					btn_ok.setEnabled(true);
					MyBabyApp.currentUser = null;
					MyBabyApp.currentBlog = null;//确保数据不会错乱
					mProgressDialog.dismiss();
					Toast.makeText(PersonEditActivity.this, R.string.inerror, Toast.LENGTH_SHORT).show();
				}
			});
		}

		@Override
		public void onLoginSuccess() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					loginIM();
					LogUtil.e("userid" + MyBabyApp.currentUser.getUserId() + "");
					MyBabyApp.getSharedPreferences().edit().putBoolean("isSignUp", true).commit();
					PostMediaTask.sendBroadcast(Constants.BroadcastAction.BroadcastAction_Sign_Up_Done, null);
					mProgressDialog.dismiss();

					Intent intent = new Intent(PersonEditActivity.this, MyBayMainActivity.class);
					startActivity(intent);
					finish();
				}
			});
		}
	};
	private void AddOrModifyPerson(String strName, long lngBirthday, int personType, int babyType) {
		Post personData;
		boolean isNew=(mPerson==null);
		
		if(mPerson==null){
			if(mIsBaby){
				Baby baby=Baby.createBaby(strName, lngBirthday,babyType);
				personData=baby.getData();
			}else{
				FamilyPerson person=FamilyPerson.createFamilyPerson(strName, personType);
				personData=person.getData();
			}
			PostRepository.save(personData);
			mPerson=personData.createPerson();
		}else{
			personData=mPerson.getData();
			mPerson.setName(strName);
			if(mIsBaby){//如果是宝宝则将所有属性都一并修改
				mPerson.setBirthday(lngBirthday);
				personData.setGenderNumber(babyType);
			}else{
				personData.setPersonTypeNumber(personType);
			}
			personData.setRemoteSyncFlag(Post.remoteSync.LocalModified.ordinal());
			PostRepository.save(personData);
			//UserRepository.save(user);
		}

		mPerson.setAvatar(mPersonAvatar.getNewAvatar());//Ҫ��������ִ������ͷ��
		
		//�㲥
        Intent bi = new Intent();
        if(isNew){
        	bi.setAction(Constants.BroadcastAction.BroadcastAction_Person_Add);
        }else{
        	bi.setAction(Constants.BroadcastAction.BroadcastAction_Person_Edit);
        	
        }
        bi.putExtra("id", mPerson.getId());
        LocalBroadcastManager.getInstance(this).sendBroadcast(bi);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// MobclickAgent.onKillProcess(Context context);//如果开发者调用
			// Process.kill 或者 System.exit 之类的方法杀死进程，请务必在此之前调用此方法，用来保存统计数据。
			if(MyBabyApp.currentBlog == null) {
				finish();
				System.exit(0);
			}else onBackPressed();
		}
		return false;
	}

	public static void createNewUser(Context context,String strName, Date dateBirthday, int personType,int babyType,String avatarUrl) {
		Baby baby=Baby.createBaby(strName, dateBirthday.getTime(),babyType);
		SelfPerson selfPerson;
		FamilyPerson familyPerson=null;
		if(personType==Post.personType.Mom.ordinal()){
			selfPerson=SelfPerson.createSelfPerson(baby.getName() + "妈", Post.personType.Mom.ordinal());
			familyPerson=FamilyPerson.createFamilyPerson(context.getResources().getString(R.string.dad), Post.personType.Dad.ordinal());
		}else if(personType==Post.personType.Dad.ordinal()){
			selfPerson=SelfPerson.createSelfPerson(baby.getName() + "爸", Post.personType.Dad.ordinal());
			familyPerson=FamilyPerson.createFamilyPerson(context.getResources().getString(R.string.mom), Post.personType.Mom.ordinal());
		}else{
			selfPerson=SelfPerson.createSelfPerson(baby.getName(), Post.personType.Other.ordinal());			
		}
		
		//blog��¼
    	Blog blog=new Blog("","", false, Blog.type.CurrUser.ordinal(),0);  //���û�idΪ0
    	
    	//user��¼
    	User user=new User( );
    	user.setUserId(0);//���û�idΪ0
		user.setGenderNumber(Post.personType.Mom.ordinal());//默认用户不需要设置性别，例如在评论时，默认显示妈妈的头像
    	user.setName(selfPerson.getName());
    	user.setBzCreateDate(System.currentTimeMillis());
    	user.setFrdTelephoneCountryCode(86+"");
    	
    	//�����ݱ���
    	UserRepository.save(user); 
    	BlogRepository.save(blog);
    	
    	//ϵͳ��ǰֵ�趨 �� ��Ϊ����ķ������õ���Щ��ǰֵ�����Ҫ��һ������
    	MyBabyApp.currentBlog=BlogRepository.getCurrentBlog();
    	MyBabyApp.currentUser=UserRepository.load(MyBabyApp.currentBlog.getUserId());    	
    	
    	//�������ݱ���
    	PostRepository.save(baby.getData());
    	PostRepository.save(selfPerson.getData());
    	if(familyPerson != null){
    		PostRepository.save(familyPerson.getData());
    	}
    	baby.setAvatar(avatarUrl);
    	
    	//��ʼ�ռǼ�¼
    	baby.initDiary();
    	
    	//同步到服务器//20160108:bj  匿名注册成功后才同步服务器
    	//Sync2ServerService.startSync2Server();
    	
	}


	private void showDateDialog(final TextView text){
		final Calendar cal= Calendar.getInstance();
		
	    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
	        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	        	cal.set(Calendar.YEAR, year); 
	            cal.set(Calendar.MONTH, monthOfYear); 
	            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth); 

	            text.setText(mDateFormat.format(cal.getTime()));
	            text.setTextColor(Color.BLACK);
				//大于当前时间
				new CustomAbsClass.doSomething(PersonEditActivity.this) {
					@Override
					public void todo() {
						if (cal.getTimeInMillis() > System.currentTimeMillis()) {//还未出生
							if (MyBabyApp.currentBlog == null) {
								mBabyTypeContainer.setVisibility(View.GONE);
								mBabyTypeContainerLine.setVisibility(View.GONE);
								setHosptail();
							} else {
								hasCheckBabyType = true;
								mBabyTypeContainer.setVisibility(View.GONE);
								mBabyTypeContainerLine.setVisibility(View.GONE);
							}
						} else {//已出生
							if (MyBabyApp.currentBlog == null) {
								setSex();
								mBabyTypeContainer.setVisibility(View.VISIBLE);
								mBabyTypeContainerLine.setVisibility(View.VISIBLE);
							} else {
								mBabyTypeContainer.setVisibility(View.VISIBLE);
								mBabyTypeContainerLine.setVisibility(View.VISIBLE);
								setSex();
							}
						}
						if (mIsBaby && StringUtils.isEmpty(mEditName.getText().toString().trim())) {
							mEditName.requestFocus();
							/*if(cal.getTimeInMillis()>System.currentTimeMillis() ){
								mEditName.setText(R.string.baby);
							}else{
								mEditName.requestFocus();
							}*/
						}
					}
				};
	        }
	    };
	    
	
	    try{
	    	Date date=mDateFormat.parse(text.getText().toString());
	    	cal.setTime(date);
	    }catch(Exception e){
	    	
	    }
	    
	    DatePickerDialog datePicker=new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,mDateSetListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
	    datePicker.setTitle(R.string.birthday_due_date);
	    long max=60*60*1000*24*280L;//280天
	//    cal.add(Calendar.YEAR, -10);//10年
	    	
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis() + max);//设置时间的最大范围
     //  long min = (long)cal.getTimeInMillis(); 
        long min=60*60*1000*24*365L;
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis()-10*min-60*60*1000*24*2-1000);//设置时间的最小范围
        datePicker.setCanceledOnTouchOutside(true);
        datePicker.show();
	}

	/**
	 * 设置性别
	 */
	private void setSex(){
		hasCheckBabyType=false;
		iconBabyType.setText(R.string.fa_user);
		mGrilCheckButton.setVisibility(View.VISIBLE);
		mBoyCheckButton.setVisibility(View.VISIBLE);
		baby_tip.setTextColor(PersonEditActivity.this.getResources().getColor(R.color.light_gray));
		baby_tip.setBackgroundResource(0);
		baby_tip.setText("宝宝性别:");
		baby_tip.setOnClickListener(null);
		Constants.babyPlace=null;
		toastTip="请选择宝宝的性别";
		setBabyType(mBabyType);
	}

	/**
	 * 设置预产医院
	 */
	private void setHosptail(){
		hasCheckBabyType=true;
		iconBabyType.setText(R.string.fa_hospital_o);
		mGrilCheckButton.setVisibility(View.GONE);
		mBoyCheckButton.setVisibility(View.GONE);
		//toastTip="请先设置预产医院";
		//baby_tip.setBackgroundResource(R.drawable.text_view_button);
		baby_tip.setText("预产医院(自动定位)");
		//baby_tip.setTextColor(PersonEditActivity.this.getResources().getColor(R.color.black));
		baby_tip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Constants.userPlaceSetting!=null){
					CustomAbsClass.startPlaceSetting(PersonEditActivity.this, Constants.userPlaceSetting, 0, 0,false,false,"");
				}else {
					WelcomeActivity.getPlaceSettingData(new BaseRPC.CallbackForMaps() {
						@Override
						public void onSuccess(final Map<?, ?> data) {
							new CustomAbsClass.doSomething(PersonEditActivity.this) {
								@Override
								public void todo() {
									CustomAbsClass.startPlaceSetting(PersonEditActivity.this, UserPlaceSetting.createByMapOrder(data), 0, 0,false,false,"");
								}
							};
						}

						@Override
						public void onFailure(long id, XMLRPCException error) {
						}
					});
				}
			}
		});
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	//requestCode = requestCode & 0xffff;//android bug:  https://groups.google.com/forum/#!msg/android-developers/NiM_dAOtXQU/ufi_FYqMGxIJ
    	
        if(requestCode==R.id.menu_main_create_sign_in){
        	if(resultCode==RESULT_OK){
                setResult(RESULT_OK);
                finish();
        	}
        }
		if (requestCode==Constants.RequestCode.ACTIVITY_REQUEST_CODE_SHOWPLACE) {
			if (data==null)
				return;
			Constants.babyPlace = (Place) data.getSerializableExtra("place");
			baby_tip.setText(Constants.babyPlace.getPlace_name());
			baby_tip.setTextColor(this.getResources().getColor(R.color.black));
			hasCheckBabyType=true;
		}
        mMediaHelper.onActivityResult(requestCode, resultCode, data);
        
        super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	public void onMediaFileDone(String[] mediaFilePaths) {
		if(mediaFilePaths.length>0){
			mPersonAvatar.setNewAvatar(mediaFilePaths[0]);
		}
	}
	
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    
    public boolean onTouchEvent(MotionEvent event) {  //点击屏幕，隐藏键盘
  	  // TODO Auto-generated method stub  
    InputMethodManager  manager = (InputMethodManager) getSystemService(getApplication().INPUT_METHOD_SERVICE);
  	  if(event.getAction() == MotionEvent.ACTION_DOWN){  
  	     if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){  
  	       manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
  	     }  
  	  }
		try {
			return super.onTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
  
}

