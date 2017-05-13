package mybaby.rpc;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCException;

import java.text.SimpleDateFormat;
import java.util.Map;

import mybaby.ui.MyBabyApp;
import mybaby.models.Register;
import mybaby.models.User;
import mybaby.models.UserRepository;
import mybaby.models.community.Place;
import mybaby.models.community.TopicCategory;
import mybaby.models.community.UserPlaceSetting;
import mybaby.models.community.UserPushSetting;
import mybaby.models.community.activity.AbstractMainActivity;
import mybaby.util.MapUtils;
import mybaby.util.Utils;

public class UserRPC extends BaseRPC {
	private static String prifixError = "XMLRPCException-UserRPC";

	public static void getUserInfo(final User user,
			final XMLRPCCallback callback) {
		if (user.isSelf()) {
			getUserInfo(callback);
		} else {
			getFriendUserInfo(user.getUserId(), callback);
		}
	}

	public static void getUserInfo(final XMLRPCCallback callback) {
		Object[] params;
		String xmlrpcMethod;

		params = extParams();
		xmlrpcMethod = "bz.xmlrpc.getUserInfo_new_v3";

		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				Map<?, ?> contentHash = (Map<?, ?>) result;

				UserRepository.save(User.createUserByMap(contentHash));
				MyBabyApp.currentUser = UserRepository.load(MyBabyApp.currentBlog
						.getUserId());

				if (callback != null) {
					callback.onSuccess(id, result);
				}
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				Utils.LogV("MyBaby", "XMLRPCException-UserRPC-getUserInfo: "
						+ error.getMessage());
				if (callback != null) {
					callback.onFailure(id, error);
				}
			}
		};

		try {
			getClient().callAsync(nCallback, xmlrpcMethod, params);
		} catch (Exception e) {
			Utils.LogV("MyBaby",
					"XMLRPCException-UserRPC-getUserInfo: " + e.getMessage());
		}
	}

	public static void updateUserInfo(final XMLRPCCallback callback) {
		String subscriptionEndDate = MyBabyApp.currentUser
				.getBzPhotoSubscriptionEndDate() == 0 ? ""
				: (new SimpleDateFormat("yyyy-MM-dd"))
						.format(MyBabyApp.currentUser
								.getBzPhotoSubscriptionEndDate());

		Object[] params = extParams(new Object[] {
				"",
				"2000-01-01",
				0,// 已作废的参数，为了保持让服务器兼容旧版本，因此随便上传内容
				"",// bzTitle
				"",// bzSubTitle
				subscriptionEndDate,
				MyBabyApp.currentUser.getFb_user_id() == null ? ""
						: MyBabyApp.currentUser.getFb_user_id(),
				MyBabyApp.currentUser.getFb_name() == null ? ""
						: MyBabyApp.currentUser.getFb_name(),
				"",// buyBookTheme
				MyBabyApp.currentUser.getFrdIsBindingContacts() ? 1 : 0,
				MyBabyApp.currentUser.getFrdIsOpen() ? 1 : 0,
				MyBabyApp.currentUser.getFrdPrivacyTypeNumber(),
				MyBabyApp.currentUser.getFrdTelephoneCountryCode() == null ? ""
						: MyBabyApp.currentUser.getFrdTelephoneCountryCode(),
				MyBabyApp.currentUser.getFrdTelephone() == null ? ""
						: MyBabyApp.currentUser.getFrdTelephone(),
				MyBabyApp.currentUser.getFrdAllowFindMeByPhone() ? 1 : 0,
				MyBabyApp.currentUser.getFrdAllowFindMeByEmail() ? 1 : 0,
				MyBabyApp.currentUser.getFrdAllowFindMeByFacebook() ? 1 : 0,
				"",// pushDeviceToken
				MyBabyApp.currentUser.getAndroidGCMRegId() == null ? ""
						: MyBabyApp.currentUser.getAndroidGCMRegId(),
				MyBabyApp.currentUser.getAndroidAppVersion(),
				MyBabyApp.currentUser.getuMengAndroidPushId()});

		String xmlrpcMethod = "bz.xmlrpc.updateBabyZoneInfo";

		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				MyBabyApp.currentUser.setBzInfoModified(false);
				UserRepository.save(MyBabyApp.currentUser);

				if (callback != null) {
					callback.onSuccess(id, result);
				}
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				Utils.LogV("MyBaby", "XMLRPCException-UserRPC-updateUserInfo: "
						+ error.getMessage());
				if (callback != null) {
					callback.onFailure(id, error);
				}
			}
		};

		try {
			getClient().callAsync(nCallback, xmlrpcMethod, params);
		} catch (Exception e) {
			Utils.LogV("MyBaby",
					"XMLRPCException-UserRPC-updateUserInfo: " + e.getMessage());
		}
	}

	private static void getFriendUserInfo(final int userId,
			final XMLRPCCallback callback) {
		Object[] params;
		String xmlrpcMethod;

		params = extParams(new Object[] { userId });
		xmlrpcMethod = "bz.xmlrpc.getFriendUserBlogInfo";

		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				Map<?, ?> rpcUser = (Map<?, ?>) result;

				User user = UserRepository.loadOrCreateByInfo(MapUtils
						.getMapInt(rpcUser, "userId"), MapUtils.getMapStr(
						rpcUser, "name"), MapUtils.getMapStr(rpcUser,
						"uniqueName"), MapUtils.getMapStr(rpcUser, "email"),
						MapUtils.getMapDateLongFrom_yyyyMMdd(rpcUser,
								"birthday"), MapUtils.getMapStr(rpcUser,
								"avatarThumbnailUrl"), MapUtils.getMapStr(
								rpcUser, "avatarUrl"), MapUtils.getMapInt(
								rpcUser, "gender"));
				UserRepository.save(user);

				if (callback != null) {
					callback.onSuccess(id, result);
				}
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				Utils.LogV(
						"MyBaby",
						"XMLRPCException-UserRPC-getFriendUserInfo: "
								+ error.getMessage());
				if (callback != null) {
					callback.onFailure(id, error);
				}
			}
		};

		try {
			getClient().callAsync(nCallback, xmlrpcMethod, params);
		} catch (Exception e) {
			Utils.LogV("MyBaby", "XMLRPCException-UserRPC-getFriendUserInfo: "
					+ e.getMessage());
		}
	}

	public static void getNewestInfo(int localMaxRecommendFriendId,
			int localMaxFriendRequestId, int lastSyncMaxActivityId,
			int lastSyncMaxMentionMeActivityId, final XMLRPCCallback callback) {
		Object[] params = extParams(new Object[] { localMaxRecommendFriendId,
				localMaxFriendRequestId, lastSyncMaxActivityId,
				lastSyncMaxMentionMeActivityId });
		String xmlrpcMethod = "bz.xmlrpc.getNewestInfo";

		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {

				if (callback != null) {
					callback.onSuccess(id, result);
				}
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				Utils.LogV("MyBaby", "XMLRPCException-UserRPC-getNewestInfo: "
						+ error.getMessage());
				if (callback != null) {
					callback.onFailure(id, error);
				}
			}
		};

		try {
			getClient().callAsync(nCallback, xmlrpcMethod, params);
		} catch (Exception e) {
			Utils.LogV("MyBaby",
					"XMLRPCException-UserRPC-getNewestInfo: " + e.getMessage());
		}
	}

	// －－－－－－－－－－－－－－2015-07-13增加－－－－－－－－－－－－－－－－－－－－－－

	public interface ListCallback {
		void onSuccess(int lastId,Boolean hasMore,User[] arrUser);
		void onFailure(long id, XMLRPCException error);
	}

	public interface UserCallback {
		void onSuccess(User user);
		void onFailure(long id, XMLRPCException error);
	}

	public interface ProfileDetailCallback {
		void onSuccess(Place[] arrPlace, TopicCategory[] arrTopicCategory, AbstractMainActivity newestActivity);
		void onFailure(long id, XMLRPCException error);
	}
	
	public interface PushSettingListCallback {
		void onSuccess(UserPushSetting[] arrSetting);
		void onFailure(long id, XMLRPCException error);
	}

	public interface PlaceSettingListCallback {
		void onSuccess(UserPlaceSetting[] arrSetting);
		void onFailure(long id, XMLRPCException error);
	}

	/**
	 * 关注某人
	 * @param userId
	 * @param callback
	 */
	public static void follow(int userId, final Callback callback) {
		final String xmlrpcMethod = "bz.xmlrpc.user_follow_user.follow";
		Object[] params = extParams(new Object[] { userId });
		rpcCallForReturnInt(prifixError, xmlrpcMethod, params, callback);
	}

	/**
	 * 取消关注
	 * @param userId
	 * @param callback
	 */
	public static void unfollow(int userId, final Callback callback) {
		final String xmlrpcMethod = "bz.xmlrpc.user_follow_user.unfollow";
		Object[] params = extParams(new Object[] { userId });
		rpcCallForReturnInt(prifixError, xmlrpcMethod, params, callback);
	}
	/**
	 * 注册，返回注册信息值
	 * *入口参数：
	 * $blog_ID//
	 * $username//
	 * $password//已默认加了
	 * $displayname String 昵称
	 * $mobile String 手机号
	 * $CountryCode String 用户所在国家
	 * $userpass String 密码
	 * @param callback
	 */
	public static void toRegister(Register register, final CallbackForRegisterId callback) {
		final String xmlrpcMethod = "bz.xmlrpc.registerUser.with.mobile";
		Object[] params = extParams(new Object[] { register.getDisplayname(),register.getMobile(),register.getCountryCode(),register.getUserpass() });
		rpcCallForReturnInt(register,prifixError, xmlrpcMethod, params, callback);
	} 

	/**
	 * 发送短信验证码
	 * mobile String 必须 手机号
	 * @param callback
	 */
	public static void sendSmsCode(String phoneNumber, final Callback callback) {
		final String xmlrpcMethod = "bz.xmlrpc.send.vercode.with.register";
		//只穿一个值，不要传默认的传
		rpcCallForReturnInt(prifixError, xmlrpcMethod, new Object[] { phoneNumber}, callback);
	}
	/**
	 * 检查验证码是否成功发送验证码
	 * mobile String 必须 手机号
	 * @param callback
	 */
	public static void checkSmsCode(String code,String phoneNumber, final Callback callback) {
		final String xmlrpcMethod = "bz.xmlrpc.check.vercode.with.register";
		//只穿一个值，不要传默认的传
		rpcCallForReturnInt(prifixError, xmlrpcMethod, new Object[]{phoneNumber, code}, callback);
	}
	/**
	/* 功能：bz.xmlrpc.check.mobile.with.register
	* 验证手机号是否已注册
	*入口参数：
	* $_mobile String 必须 手机号
	* $_CountryCode String 可选 国家编码*返回结果
	* 0 该手机号已注册
	* 1 该手机号还没注册
	* 在发送验证码之前判断
	* */
	public static void checkPhoneHasRegiest(String phone, String countryCode, final Callback callback) {
		final String xmlrpcMethod = "bz.xmlrpc.check.mobile.with.register";
		rpcCallForReturnInt(prifixError, xmlrpcMethod, new Object[]{phone, countryCode}, callback);
	}
	
	/**
	 * 是否关注
	 * @param userId
	 * @param callback
	 */
	public static void hasFollow(int userId, final CallbackForMap callback) {
		final String xmlrpcMethod = "bz.xmlrpc.user_follow_user.has_follow.v2";
		Object[] params = extParams(new Object[] { userId });
		rpcCallForReturnMap(prifixError, xmlrpcMethod, params, callback);
	}

	/**
	 * 获得关注列表
	 * @param userId
	 * @param callback
	 */
	public static void getFollow(int userId,int lastId, final ListCallback callback) {
		final String xmlrpcMethod = "bz.xmlrpc.user_follow_user.get_follow";
		Object[] params = extParams(new Object[] { userId,lastId});
		getList(xmlrpcMethod, params, callback);
	}

	public static void getFollower(int userId,int lastId, final ListCallback callback) {
		final String xmlrpcMethod = "bz.xmlrpc.user_follow_user.get_follower";
		Object[] params = extParams(new Object[] { userId,lastId});
		getList(xmlrpcMethod, params, callback);
	}
/**
 * *功能：
 * 根据im_userid获取用户的概要信息
 *调用方法：
 * bz.xmlrpc.getUserByImuserid
 *入口参数：
 * im_userid
 *返回结果
 * array(
 * id,
 * name,
 * avatarThumbnailUrl,
 * gender
 * )
 */
	/**
	 *
	 * @param IMuserId
	 * @param callback*/
	public static void getUserByIMUserID(String IMuserId, CallbackgetUserByIMuserid callback){
		final String xmlrpcMethod = "bz.xmlrpc.getUserByImuserid";
		Object[] params = extParams(new Object[] { IMuserId });
		getUser(xmlrpcMethod,params,callback);
	}
	private static void getUser(final String xmlrpcMethod, Object[] params,
								final CallbackgetUserByIMuserid callback) {
		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {

				if (result != null) {
					int userId = 0;
					String userName = "";
					String userAvatarThumbnailUrl = "";
					int userGender =0;
					if (result instanceof Map<?, ?>) {
						Map<?, ?> map = (Map<?, ?>) result;
						if (map.containsKey("id"))
							userId = MapUtils.getMapInt(map, "id");
						if (map.containsKey("name"))
							userName = MapUtils.getMapStr(map, "name");
						if (map.containsKey("avatarThumbnailUrl"))
							userAvatarThumbnailUrl = MapUtils.getMapStr(map, "avatarThumbnailUrl");
						if (map.containsKey("gender"))
							userGender = MapUtils.getMapInt(map, "gender");
					}
					User user=new User();
					user.setUserId(userId);
					user.setName(userName);
					user.setAvatarThumbnailUrl(userAvatarThumbnailUrl);
					user.setGenderNumber(userGender);
					if (callback != null) {
						callback.onSuccess(user);
					}
				}else {
						if (callback != null)
							callback.onFailure(id, null);
				}
			}
			@Override
			public void onFailure(long id, XMLRPCException error) {
				if (callback != null) {
					callback.onFailure(id, error);
				}
			}

		};

		try {
			getClient().callAsync(nCallback, xmlrpcMethod, params);
		} catch (Exception e) {
			Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + "-catch: "
					+ e.getMessage());
		}
	}


	/**
 *功能：
* 是否能和to_user_id用户通信
*调用方法：
* bz.xmlrpc.im.can_send_message
 *入口参数：
* to_user_id
 *返回结果
* array(
 * value 0--不能 1-－能
* message
 * to_user_imuserid
 * to_user_imuserpasswd
 */public static void canSendMessage(int userId, final CallbackCanSendMessage callback) {
		final String xmlrpcMethod = "bz.xmlrpc.im.can_send_message";
		Object[] params = extParams(new Object[] { userId });
		getCanSendMessage(xmlrpcMethod, params, callback);
	}

	private static void getCanSendMessage(final String xmlrpcMethod, Object[] params,
								final CallbackCanSendMessage callback) {
		XMLRPCCallback xmlrpcCallback=new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				if (result!=null){
					boolean cansend=false;
					String IMuserid="";
					String message="";
					if (result instanceof Map<?, ?>) {
						Map<?, ?> map = (Map<?, ?>) result;
						if (map.containsKey("value"))
							cansend = MapUtils.getMapBool(map, "value");
						if (cansend) {
							if (map.containsKey("to_im_userid"))
								IMuserid = MapUtils.getMapStr(map, "to_im_userid");
						} else {
							if (map.containsKey("message"))
								message = MapUtils.getMapStr(map, "message");
						}
						if (callback != null) {
							callback.onSuccess(cansend,IMuserid,message);
						}
					}else {
						if (callback != null) {
							callback.onFailure(id,null);
						}
					}
				}
			}
			@Override
			public void onFailure(long id, XMLRPCException error) {
				if (callback != null) {
					callback.onFailure(id,error);
				}
			}
		};
		try {
			getClient().callAsync(xmlrpcCallback, xmlrpcMethod, params);
		} catch (Exception e) {
			Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + "-catch: "
					+ e.getMessage());
		}
	}

	private static void getList(final String xmlrpcMethod, Object[] params,
			final ListCallback callback) {
		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				if (result != null&&result instanceof Map<?, ?>) {
					Map<?, ?> map=(Map<?, ?>) result;
					if (callback != null&&map!=null) {
						callback.onSuccess(
								MapUtils.getMapInt(map,"last_id"),
								MapUtils.getMapBool(map,"has_more"),
								User.createByArray_new(MapUtils.getArray(map,"data")));
					}
				} else {
					Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod
							+ ": 服务器api返回结果错误");
					if (callback != null) {
						callback.onFailure(id, null);
					}
				}
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				Utils.LogV("MyBaby", prifixError + xmlrpcMethod
						+ "-onFailure: " + error.getMessage());
				if (callback != null) {
					callback.onFailure(id, error);
				}
			}
		};

		try {
			getClient().callAsync(nCallback, xmlrpcMethod, params);
		} catch (Exception e) {
			Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + "-catch: "
					+ e.getMessage());
		}
	}

	public static void getPushSetting(final PushSettingListCallback callback) {
		if(MyBabyApp.currentUser.getUserId()<=0){ 
			BlogRPC.anonymousSignUp(new XMLRPCCallback() {
	            @Override
	            public void onSuccess(long id, Object result){
	            	getPushSetting(callback);
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
		
		
		final String xmlrpcMethod = "bz.xmlrpc.user_setting.get_push";
		Object[] params = extParams();

		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				if (result != null) {
					Object[] rpcArray = (Object[]) result;

					if (callback != null) {
						callback.onSuccess(UserPushSetting
								.createByArray(rpcArray));
					}
				} else {
					Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod
							+ ": 服务器api返回结果错误");
					if (callback != null) {
						callback.onFailure(id, null);
					}
				}
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				Utils.LogV("MyBaby", prifixError + xmlrpcMethod
						+ "-onFailure: " + error.getMessage());
				if (callback != null) {
					callback.onFailure(id, error);
				}
			}
		};

		try {
	    	getClient().callAsync(nCallback,xmlrpcMethod, params);
		} catch (Exception e) {
			Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + "-catch: "
					+ e.getMessage());
		}
	}

	//TODO 改动了参数，到测试
	public static void setPushSetting(String key, boolean value,
			final Callback callback) {
		final String xmlrpcMethod = "bz.xmlrpc.user_setting.set_push";
		Object[] params = extParams(new Object[] { key, value });
		rpcCallForReturnInt(prifixError, xmlrpcMethod, params, callback);
	}

	public static void getPlaceSetting(final PlaceSettingListCallback callback) {
		final String xmlrpcMethod = "bz.xmlrpc.user_setting.get_place";
		rpcCallForReturnObjs(prifixError, xmlrpcMethod, extParams(), new CallbackForObjs() {
			@Override
			public void onSuccess(Object[] data) {
				if (callback != null) {
					callback.onSuccess(UserPlaceSetting
							.createByArray(data));
				}
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				if (callback != null) {
					callback.onFailure(id, null);
				}
			}
		});
	}

	public static void setPlaceSetting(String key, Place place,
			final Callback callback) {
		final String xmlrpcMethod = "bz.xmlrpc.user_setting.set_place";
		Object[] params = extParams(new Object[] { key, place.getMap() });
		rpcCallForReturnInt(prifixError, xmlrpcMethod, params, callback);
	}

	public static void clearPlaceSetting(String key, final Callback callback) {
		final String xmlrpcMethod = "bz.xmlrpc.user_setting.clear_place";
		Object[] params = extParams(new Object[] { key });
		rpcCallForReturnInt(prifixError, xmlrpcMethod, params, callback);
	}

	public static void getProfile(final int userId, final UserCallback callback) {
		if(MyBabyApp.currentUser.getUserId()<=0){ 
			BlogRPC.anonymousSignUp(new XMLRPCCallback() {
	            @Override
	            public void onSuccess(long id, Object result){
	            	getProfile(userId,callback);
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
		
		
		final String xmlrpcMethod = "bz.xmlrpc.user.get_profile";
		Object[] params = extParams(new Object[] { userId });

		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				if (result != null) {
					Map<?, ?> map = (Map<?, ?>) result;
					if (callback != null) {
						callback.onSuccess(User.createByMap_new(map));
					}
				} else {
					Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod
							+ ": 服务器api返回结果错误");
					if (callback != null) {
						callback.onFailure(id, null);
					}
				}
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				Utils.LogV("MyBaby", prifixError + xmlrpcMethod
						+ "-onFailure: " + error.getMessage());
				if (callback != null) {
					callback.onFailure(id, error);
				}
			}
		};

		try {
	    	getClient().callAsync(nCallback,xmlrpcMethod, params);
		} catch (Exception e) {
			Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + "-catch: "
					+ e.getMessage());
		}
	}

	
	public static void getProfileDetail(int userId, final ProfileDetailCallback callback) {
		final String xmlrpcMethod = "bz.xmlrpc.user.get_profile_detail";
		Object[] params = extParams(new Object[] { userId });

		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				if (result != null) {
					Map<?, ?> map = (Map<?, ?>) result;
					
					Object[] arrPlaceMap = MapUtils.getArray(map,"place_list");
					Object[] arrCategoryMap = MapUtils.getArray(map,"topic_category_list");
					Map<?, ?> mapActivity = MapUtils.getMap(map,"newest_activity");
					
					Place[] arrPlace=null;
					if(arrPlaceMap != null)
						arrPlace=Place.createByArray(arrPlaceMap);
					
					TopicCategory[] arrCategory=null;
					if(arrCategoryMap != null)
						arrCategory=TopicCategory.createByArray(arrCategoryMap);
					
					AbstractMainActivity newestActivity=null;
					if(mapActivity != null)
						newestActivity=AbstractMainActivity.createByMap(mapActivity);

					if (callback != null) {
						callback.onSuccess(arrPlace,arrCategory,newestActivity);
					}
				} else {
					Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod
							+ ": 服务器api返回结果错误");
					if (callback != null) {
						callback.onFailure(id, null);
					}
				}
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				Utils.LogV("MyBaby", prifixError + xmlrpcMethod
						+ "-onFailure: " + error.getMessage());
				if (callback != null) {
					callback.onFailure(id, error);
				}
			}
		};

		try {
			getClient().callAsync(nCallback, xmlrpcMethod, params);
		} catch (Exception e) {
			Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + "-catch: "
					+ e.getMessage());
		}
	}

	/*
  *功能：
  *      获取好友列表
  *调用方法：
  *      bz.xmlrpc.user_follow_user.get_friends
  *入口参数：
  *      $user_id    用户id
  *     $lastId  服务器端分页使用，客户端下拉刷新时传0，获取更多时传服务器上页获取数据返回的结果
  *返回结果
  *     array(
  'has_more': 是否还有更多信息，0-－没有，1-－有
  'last_id' : 最后一条信息的id，分页时使用
  'data'    : 获取的数据，如下：
  array(
  'id'                   用户id
  'name'                 用户昵称
  'avatarThumbnailUrl'   用户头像
  );
  );
  */
	public static void setUserList(int userID, int lastId,
									   final ListCallback callback) {
		final String xmlrpcMethod = "bz.xmlrpc.user_follow_user.get_friends";
		Object[] params = extParams(new Object[] {userID,lastId});
		getList(xmlrpcMethod, params, callback);
	}

}
