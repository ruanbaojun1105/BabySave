package mybaby.rpc;

import android.content.SharedPreferences.Editor;
import android.util.Log;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import me.hibb.mybaby.android.BuildConfig;
import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.Blog;
import mybaby.models.BlogRepository;
import mybaby.models.User;
import mybaby.models.UserRepository;
import mybaby.models.diary.Post;
import mybaby.models.diary.PostRepository;
import mybaby.models.person.Baby;
import mybaby.models.person.SelfPerson;
import mybaby.ui.MyBabyApp;
import mybaby.util.DateUtils;
import mybaby.util.LogUtils;
import mybaby.util.MapUtils;
import mybaby.util.Utils;

public class BlogRPC extends BaseRPC {
	private static String prifixError="XMLRPCException-BlogRPC";
	
	public static void anonymousSignUp(final XMLRPCCallback callback){
		if(MyBabyApp.isAnonymousSignUp){
			return;
		}
		MyBabyApp.isAnonymousSignUp=true;
		
		SelfPerson selfPerson=PostRepository.loadSelfPerson(MyBabyApp.currentUser.getUserId());
		Baby baby = PostRepository.loadBabies(MyBabyApp.currentUser.getUserId())[0];
		String creatDate=(new SimpleDateFormat("yyyy-MM-dd")).format(MyBabyApp.currentUser.getBzCreateDate());
		String birthdayDate=(new SimpleDateFormat("yyyy-MM-dd")).format(baby.getBirthday());
	    Object[] params = {
	            1,
	            baby.getName(),
				birthdayDate,
	            baby.getGenderNumber(),
	            creatDate,
	            selfPerson.getPersonTypeNumber(),
	            Constants.Channel
	    };
		if (Constants.babyPlace!=null)
			params = new Object[]{
					1,
					baby.getName(),
					new Date(baby.getBirthday()),
					baby.getGenderNumber(),
					creatDate,
					selfPerson.getPersonTypeNumber(),
					Constants.Channel,
					Constants.babyPlace.getMap()//不然无法序列化
			};
	    XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result){
            	final Map<?, ?> contentHash = (Map<?, ?>) result;

            	MyBabyApp.currentBlog.setUsername(MapUtils.getMapStr(contentHash, "user_name"));
            	MyBabyApp.currentBlog.setPassword(Constants.MY_BABY_DEFAULT_PASSWORD);
            	MyBabyApp.currentBlog.setUserId(MapUtils.getMapInt(contentHash, "user_id"));
            	BlogRepository.save(MyBabyApp.currentBlog);

            	MyBabyApp.currentUser=User.createUserByMap(contentHash);
				UserRepository.save(MyBabyApp.currentUser);
            	
            	MyBabyApp.myBabyDB.db.execSQL("update post set userid=" + MyBabyApp.currentUser.getUserId());
            	MyBabyApp.myBabyDB.db.execSQL("update media set userid=" + MyBabyApp.currentUser.getUserId());
                	
            	
    	    	SelfPerson selfPerson=PostRepository.loadSelfPerson(MyBabyApp.currentUser.getUserId());
    	    	if(!MyBabyApp.currentUser.getName().equals(selfPerson.getName())){
    		    	selfPerson.setName(MyBabyApp.currentUser.getName());
    		    	Post personData=selfPerson.getData();
    		    	personData.setRemoteSyncFlag(Post.remoteSync.LocalModified.ordinal());
    		    	PostRepository.save(personData);
    	    	}
            	
            	//MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_Anonymous_SignUp_Done);
            	
            	if(callback != null){
            		callback.onSuccess(id, result);
            	}
            	
            	MyBabyApp.isAnonymousSignUp=false;
            }
	    
            @Override
            public void onFailure(long id, XMLRPCException error){
            	Utils.LogV("MyBaby", "XMLRPCException--BlogRPC-anonymousSignUp: " + error.getMessage());
            	if(callback != null){
            		callback.onFailure(id, error);
            	}
            	MyBabyApp.isAnonymousSignUp=false;
            }
	    };
	    
	    try {
	        getClient().callAsync(nCallback,"bz.xmlrpc.createBabyZone_v3", params);
	    } catch ( Exception e) {
			if(callback != null){
				callback.onFailure(0, null);
			}
	    	Utils.LogV("MyBaby", "XMLRPCException-BlogRPC-anonymousSignUp: " + e.getMessage());
	    }
	}
	
	public static void signUp(final String email,final String password,final XMLRPCCallback callback){
		if(MyBabyApp.currentUser.getUserId()<=0){ 
			anonymousSignUp(new XMLRPCCallback() {
	            @Override
	            public void onSuccess(long id, Object result){
	            	signUp(email,password,callback);
	            }
	            @Override
	            public void onFailure(long id, XMLRPCException error){
	            	if(callback != null){
	            		callback.onFailure(id, error);
	            	}
	            }
	        });
			return;
		}
		
		
		
		Object[] params=extParams(new Object[]{email,password});

	    XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result){
            	int iResult=Integer.parseInt(result.toString());
            	
            	if(iResult==-2){
                	if(callback != null){
                		XMLRPCException emailRepeatException=new XMLRPCException(MyBabyApp.getContext().getString(R.string.email_cannot_be_registered_repeatedly));
                		callback.onFailure(id, emailRepeatException);
                	}
            	}else if(iResult<=0){
                	if(callback != null){
                		callback.onFailure(id, null);
                	}
            	}else{
            		MyBabyApp.currentUser.setEmail(email);
                    MyBabyApp.currentBlog.setPassword(password);
                    MyBabyApp.currentUser.setBzRegistered(true);
                    
                    MyBabyApp.currentUser.setFrdAllowFindMeByEmail(true);//注册后允许按邮件找到我
                    MyBabyApp.currentUser.setBzInfoModified(true);
                    
                	UserRepository.save(MyBabyApp.currentUser); 
                	BlogRepository.save(MyBabyApp.currentBlog);
                    
                	if(callback != null){
                		callback.onSuccess(id, result);
                	}
            	}

            }
	    
            @Override
            public void onFailure(long id, XMLRPCException error){
            	Utils.LogV("MyBaby", "XMLRPCException-BlogRPC-signUp: " + error.getMessage());
            	if(callback != null){
            		callback.onFailure(id, error);
            	}
            }
	    };
	    
	    try {
	        getClient().callAsync(nCallback,"bz.xmlrpc.registerUser", params);
	    } catch ( Exception e) {
	    	Utils.LogV("MyBaby", "XMLRPCException-BlogRPC-signUp: " + e.getMessage());
	    }
	}
	
	/**
	 * 支持手机号登陆和邮箱登陆
	 * @param isEmail
	 * @param email
	 * @param password
	 * @param callback
	 */
	public static void signIn(Boolean isEmail,String email,final String password,final XMLRPCCallback callback){
		String rpcName="";
		if (isEmail) {
			rpcName="bz.xmlrpc.getUserInfo_new_v3";//邮箱登陆
		}else {
			rpcName="bz.xmlrpc.getUserInfoByModile_v3";//手机号登陆
		}
		
	    Object[] params = { 
	            1, 
	            email,
	            password
	    };
	    /**
	     * * bz.xmlrpc.getUserInfoByModile_v3
	     *入口参数：不用传blogid,username, password
	     * $mobile 手机号
	     * $countryCode
	     * $password 密码
	     * $result -2：手机号或密码错误 －1: 用户不存在 否则返回用户信息（同 bz.xmlrpc.getUserInfo_new_v3）
	     */
	    Object[] paramsPhone = { 
	            email,
	            86,
	            password
	    };

	    XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result){
            	if (result instanceof Map<?, ?>) {
            	final Map<?, ?> contentHash = (Map<?, ?>) result;
            	UserRepository.clear();
            	BlogRepository.clear();
            	
            	Blog blog=new Blog(MapUtils.getMapStr(contentHash, "user_name"),
            			password, 
            			false, 
            			Blog.type.CurrUser.ordinal(), 
            			MapUtils.getMapInt(contentHash, "user_id"));
            	
            	UserRepository.save(User.createUserByMap(contentHash));             	
            	BlogRepository.save(blog);

            	MyBabyApp.currentBlog=BlogRepository.getCurrentBlog();
            	MyBabyApp.currentUser=UserRepository.load(MyBabyApp.currentBlog.getUserId());
                
            	if(callback != null){
            		callback.onSuccess(id, result);
            		}
				}else {
					callback.onFailure(id,null);
				}
            }
	    
            @Override
            public void onFailure(long id, XMLRPCException error){
            	Utils.LogV("MyBaby", "XMLRPCException-BlogRPC-signIn: " + error.getMessage());
            	if(callback != null){
            		callback.onFailure(id, error);
            	}
            }
	    };
	    
	    try {
	        getClient().callAsync(nCallback,rpcName, isEmail?params:paramsPhone);
	    } catch ( Exception e) {
	    	Utils.LogV("MyBaby", "XMLRPCException-BlogRPC-signIn: " + e.getMessage());
	    }
	}
	
	
	public static void userLogSend(final XMLRPCCallback callback){
		if(MyBabyApp.currentUser==null || MyBabyApp.currentUser.getUserId()<=0){
			return;
		}
		
		final long lastUserLogTime=MyBabyApp.getSharedPreferences().getLong("lastUserLogTime", 0);
        long timeLimit=BuildConfig.DEBUG  ? 1000*60 : 1000*60*10;//最少10分钟才允许同步一次(调试模式60秒)
        if(lastUserLogTime>0 && System.currentTimeMillis() - lastUserLogTime<timeLimit){ 
        	return;
        }

		Object[] params=extParams(new Object[]{android.os.Build.DEVICE,
											"Android",
							                android.os.Build.VERSION.RELEASE,
							                Locale.getDefault().getLanguage(),
							                Locale.getDefault().getCountry(),
							                TimeZone.getDefault().getOffset(System.currentTimeMillis())/1000,
							                Utils.getAppVersion(),
							                Constants.Channel
							                });

	    XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result){
         		Editor edit=MyBabyApp.getSharedPreferences().edit();
         		edit.putLong("lastUserLogTime", System.currentTimeMillis());
         		edit.commit();
         		
            	if(callback != null){
            		Log.i("上传用户日志信息最后时间",DateUtils.date2DisplayString(lastUserLogTime));
            		callback.onSuccess(id, result);
            	}
            }
	    
            @Override
            public void onFailure(long id, XMLRPCException error){
            	Utils.LogV("MyBaby", "XMLRPCException-BlogRPC-userLog: " + error.getMessage());
            	if(callback != null){
            		callback.onFailure(id, error);
            	}
            }
	    };
	    
	    try {
	        getClient().callAsync(nCallback,"bz.xmlrpc.userLog", params);
	    } catch ( Exception e) {
	    	Utils.LogV("MyBaby", "XMLRPCException-BlogRPC-userLog: " + e.getMessage());
	    }
	}


	/**
	 * ”bz.xmlrpc.getSystemInfo“ 改为了 “bz.xmlrpc.system.option.get” 增加返回参数“webPageAttVer”
	 * bz.xmlrpc.system.option.get
	 * @param callback
	 */

	public static void getSystemInfo(final Callback callback){
		LogUtils.e("getSystemInfo");
    	final String xmlrpcMethod="bz.xmlrpc.system.option.get";
	    Object[] params=extParams();
		
		XMLRPCCallback nCallback = new XMLRPCCallback() {
            @Override
            public void onSuccess(long id, Object result){
            	if(result != null&&result instanceof Map<?, ?>){
            		Map<?, ?> map=(Map<?, ?>)result;
            		//TODO 此处添加普通地点设置的各个属性
            		Blog.setNearbyDistance((int)(MapUtils.getMapDouble(map, "nearbyDistance")));
            		Blog.setNewVersion((int) (MapUtils.getMapDouble(map, "newVersion")));
            		Blog.setBookThemesVersion(MapUtils.getMapInt(map, "bookThemesVersion"));
            		Blog.setMapExcludeRegex(MapUtils.getMapStr(map, "mapExcludeRegex"));
            		Blog.setMapPlaceKeywords(MapUtils.getMapStr(map, "mapPlaceKeywords"));
            		Blog.setMapPlaceTypes(MapUtils.getMapStr(map, "mapPlaceTypes"));
            		Blog.setMediaUpload(MapUtils.getMapInt(map, "mediaUpload"));
            		Blog.setOpenAD(MapUtils.getMapInt(map, "openAD"));
            		Blog.setBookThemesBaseVersion(MapUtils.getMapInt(map, "bookThemesBaseVersion"));
					Blog.setWebPageAttVer(MapUtils.getMapStr(map,"webPageAttVer"));
            		
            		if(callback != null){
                		callback.onSuccess( );
                	}
            	}else{
            		Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + ": 服务器api返回结果错误");
                	if(callback != null){
                		callback.onFailure(id, null);
                	}
            	}
            }
	    
            @Override
            public void onFailure(long id, XMLRPCException error){
            	Utils.LogV("MyBaby", prifixError + xmlrpcMethod + "-onFailure: " + error.getMessage());
            	if(callback != null){
            		callback.onFailure(id, error);
            	}
            }
	    };
	    
	    try {
	        getClient().callAsync(nCallback,xmlrpcMethod, params);
	    } catch ( Exception e) {
	    	Utils.LogV("MyBaby", prifixError + "-" +  xmlrpcMethod + "-catch: " + e.getMessage());
	    }
	}
}
