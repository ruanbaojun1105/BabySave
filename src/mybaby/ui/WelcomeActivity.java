package mybaby.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.hibb.mybaby.android.R;
import mybaby.BaseActivity;
import mybaby.Constants;
import mybaby.RxUtils;
import mybaby.models.community.UserPlaceSetting;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.UserRPC;
import mybaby.rpc.community.PlaceRPC;
import mybaby.ui.main.MainUtils;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.user.PersonEditActivity;
import mybaby.util.DrawableManager;
import mybaby.util.StringUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

@ContentView(R.layout.welcome)
public class WelcomeActivity extends BaseActivity{

	@ViewInject(R.id.logo_image)
	private ImageView logo;
	private static String[] Chanels={"tyyb","tgdt","bdzs","bdss","91zs","azsc","c360zs","c360ss","wdj","ppzs","azmt",
			"mmy","jfsc","sgzs","sgss","ndo","wbsc","wbfs","yyh","sams","xiao","huaw","oppo","meiz",
			"leno","web","dtxc","dtxc","ipadqc","webqc","b001","b002","b003","b004","b005","b006",
			"b007","","b008","b009","b010","yyk","yydpd","yytxd","yythgy","yyzjxc","yyhpdd","yyhcd",
			"khsc","yyrk","dsjx","mmsc","c703","ccwx","diwo",
			"ydzs","zysc","miai",};

	private Subscription subscription;

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.subscription =Observable.create(new Observable.OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> subscriber) {
				try {
					if (!subscriber.isUnsubscribed()) {
                        MainUtils.createSystemSwitcherShortCut(WelcomeActivity.this);
                        MyBabyApp.initScreenParams(WelcomeActivity.this);//不要随意移动此初始化
                        getPlaceSettingData(null);//地点设置信息保存
                        String chanel = StringUtils.getChanel();
                        Constants.Channel = chanel;
                        int resourceId = DrawableManager.getDrawableResourceId(WelcomeActivity.this, "welcome_logo");
                        if (!StringUtils.isEmpty(chanel)) {
                            for (String data : Chanels) {
								if (MyBabyApp.version>=5)
									break;
                                if (chanel.equals(data)) {
									int chanelresId=DrawableManager.getDrawableResourceId(WelcomeActivity.this, "welcome_logo_" + data);
                                    if (0 != chanelresId) {
										resourceId = chanelresId;
										break;
									}
                                }
                            }
                        }

                        subscriber.onNext(resourceId);
                        subscriber.onCompleted();
                    }
				} catch (Exception e) {
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		}).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
				.observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
				.subscribe(new Subscriber<Integer>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {
						Toast.makeText(WelcomeActivity.this, "Error!", Toast.LENGTH_SHORT).show();
						WelcomeActivity.this.finish();
					}

					@Override
					public void onNext(Integer resId) {
						Picasso.with(WelcomeActivity.this).load(resId).config(Bitmap.Config.RGB_565)
								.placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(logo);
					}
				});

		Observable.timer(1500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
				.map(new Func1<Long, Object>() {
					@Override
					public Object call(Long aLong) {
						startActivity(checkIntent(WelcomeActivity.this));
						WelcomeActivity.this.finish();
						return null;
					}
				}).subscribe();
		/*RxView.clickEvents(button) // RxBinding 代码，后面的文章有解释
				.throttleFirst(500, TimeUnit.MILLISECONDS) // 设置防抖间隔为 500ms
				.subscribe(subscriber);*/

	}

	@Override
	protected void onDestroy() {
		RxUtils.unsubscribeIfNotNull(this.subscription);
		super.onDestroy();
	}

	public static Intent checkIntent(Context context){
		if (MyBabyApp.currentBlog == null ) {//进入登入
			Intent intent = new Intent(context, PersonEditActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			return intent;
		} else {
			if (MyBabyApp.currentUser!=null){
				if (TextUtils.isEmpty(MyBabyApp.currentUser.getImUserName())){
					UserRPC.getUserInfo(new XMLRPCCallback() {
						@Override
						public void onSuccess(long id, Object result) {
							MainUtils.loginIM();
						}

						@Override
						public void onFailure(long id, XMLRPCException error) {
						}
					});
				}else {
					MainUtils.loginIM();
				}
			}
			Intent intent = new Intent(context, MyBayMainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			return intent;
			//overridePendingTransition(R.anim.in_from_right,R.anim.in_from_left);
		}
	}

	/**
	 * 初始化加载地点设置参数
	 */
	public static void getPlaceSettingData(final BaseRPC.CallbackForMaps callbackForMaps){
		PlaceRPC.getPlaceHospitalSettingData(new BaseRPC.CallbackForMaps() {
			@Override
			public void onSuccess(Map<?, ?> data) {
				Constants.userPlaceSetting = UserPlaceSetting.createByMapOrder(data);
				if (callbackForMaps != null)
					callbackForMaps.onSuccess(data);
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				if (callbackForMaps != null)
					callbackForMaps.onFailure(0, null);
			}
		});
	}
}
