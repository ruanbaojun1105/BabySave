package mybaby.rpc;

import org.xmlrpc.android.XMLRPCCallback;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;
import org.xutils.common.util.LogUtil;

import java.util.Map;

import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.models.BlogRepository;
import mybaby.models.Register;
import mybaby.models.User;
import mybaby.models.UserRepository;
import mybaby.models.diary.PostRepository;
import mybaby.models.person.SelfPerson;
import mybaby.util.ArrayUtils;
import mybaby.util.Utils;

/**
 * 此类很多不合理的地方
 *
 */
public class  BaseRPC {
	private static XMLRPCClient rpcClient;

	public interface CallbackToDo {
		void todo();
	}

	public interface Callback {
		void onSuccess();

		void onFailure(long id, XMLRPCException error);
	}

	public interface CallbackForId {
		void onSuccess(int Id);

		void onFailure(long id, XMLRPCException error);
	}
	public interface CallbackForRegisterId {
		void onSuccess();
		void onHasPhone();
		void onFailure(long id, XMLRPCException error);
	}

	public interface CallbackForBool {
		void onSuccess(boolean boolValue);

		void onFailure(long id, XMLRPCException error);
	}
	public interface CallbackForMaps {
		void onSuccess( Map<?, ?> data);

		void onFailure(long id, XMLRPCException error);
	}
	public interface CallbackForObjs {
		void onSuccess( Object[] data);

		void onFailure(long id, XMLRPCException error);
	}

	public interface CallbackForMap {
		void onSuccess(boolean boolValue, String data);

		void onFailure(long id, XMLRPCException error);
	}

	public interface CallbackForString {
		void onSuccess(String str);

		void onFailure(long id, XMLRPCException error);
	}

	public interface CallbackCanSendMessage {
		void onSuccess(Boolean canSend,String IMuserID,String message);

		void onFailure(long id, XMLRPCException error);
	}

	public interface CallbackgetUserByIMuserid {
		void onSuccess(User user);
		void onFailure(long id, XMLRPCException error);
	}

		public static XMLRPCClient getClient() {
		if (rpcClient == null) {
			rpcClient = new XMLRPCClient(Constants.MY_BABY_XMLRPC_URL, "", null);
		}
		return rpcClient;
	}

	public static Object[] extParams() {
		return extParams(null);
	}

	public static Object[] extParams(Object[] extObjects) {
		if (MyBabyApp.currentBlog==null)
			return null;
		Object[] initParams = { 1, MyBabyApp.currentBlog.getUsername(),
				MyBabyApp.currentBlog.getPassword() };
		if (extObjects != null && extObjects.length > 0) {
			Object[] mergeParams = ArrayUtils
					.mergeArray(initParams, extObjects);
			return mergeParams;
		} else {
			return initParams;
		}
	}

	public static void rpcCallForReturnInt(final String prifixError,
			final String xmlrpcMethod, Object[] params, final Callback callback) {
		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				if (result instanceof Integer) {
					int retValue = Integer.parseInt(result.toString());
					if (retValue > 0) {
						if (callback != null) {
							callback.onSuccess();
						}
					} else {
						Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod
								+ ": 服务器api返回结果错误");
						if (callback != null) {
							callback.onFailure(id, null);
						}
					}
				}else if (result instanceof Integer){
					int retValue = (int) result;
					if (retValue > 0) {
						if (callback != null) {
							callback.onSuccess();
						}
					} else {
						if (callback != null) {
							callback.onSuccess();
						}
					}
				}else {
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
			if(callback != null){
				callback.onFailure(0,null);
			}
			Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + "-catch: "
					+ e.getMessage());
		}
	}

	public static void rpcCallForReturnInt(final String prifixError,
			final String xmlrpcMethod, Object[] params,
			final CallbackForId callback) {
		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				if (result instanceof Integer) {
					int retValue = Integer.parseInt(result.toString());
					if (retValue > 0) {
						if (callback != null) {
							callback.onSuccess(retValue);
						}
					} else {
						Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod
								+ ": 服务器api返回结果错误");
						if (callback != null) {
							callback.onFailure(id, null);
						}
					}
				}else if (result instanceof Integer){
					int retValue = (int) result;
					if (retValue > 0) {
						if (callback != null) {
							callback.onSuccess(retValue);
						}
					} else {
						if (callback != null) {
							callback.onSuccess(retValue);
						}
					}
				}else {
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
			if(callback != null){
				callback.onFailure(0,null);
			}
			Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + "-catch: "
					+ e.getMessage());
		}
	}

	public static void rpcCallForReturnMaps(final String prifixError,
			final String xmlrpcMethod, Object[] params,
			final CallbackForMaps callback) {
		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				if (result != null&&result instanceof Map<?, ?>) {
					if (callback != null) {
						callback.onSuccess((Map<?, ?>) result);
					}
				}else {
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
			if(callback != null){
				callback.onFailure(0,null);
			}
		}
	}

	public static void rpcCallForReturnObjs(final String prifixError,
											final String xmlrpcMethod, Object[] params,
											final CallbackForObjs callback) {
		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				if (result != null&&result instanceof Object[]) {
					if (callback != null) {
						callback.onSuccess((Object[]) result);
					}
				}else {
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
			if(callback != null){
				callback.onFailure(0,null);
			}
		}
	}

	public static void rpcCallForReturnMap(final String prifixError,
										   final String xmlrpcMethod, Object[] params,
										   final CallbackForMap callback) {
		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				if (result != null&&result instanceof Map<?, ?>) {
					Map<?, ?> map = (Map<?, ?>) result;
					int retValue = 0;
					String data = "";
					//修复空指针 2015/12/4bj
					if (map.containsKey("value")) {
						retValue = Integer
								.parseInt(map.get("value").toString());
					}
					if (map.containsKey("data")) {
						data = map.get("data").toString();
					}
					if (map.containsKey("message")) {
						data = map.get("message").toString();
					}
					if (retValue > 0) {
						if (callback != null) {
							callback.onSuccess(true, data);
						}
					} else {
						if (callback != null) {
							callback.onSuccess(false, data);
						}
					}
				}else {
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
			if(callback != null){
				callback.onFailure(0,null);
			}
			Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod + "-catch: "
					+ e.getMessage());
		}
	}



	public static void rpcCallForReturnInt(final String prifixError,
			final String xmlrpcMethod, Object[] params,
			final CallbackForBool callback) {
		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				if (result != null) {
					if (result instanceof String){
						int retValue = Integer.parseInt(result.toString());
						if (retValue > 0) {//此处服务器PHP返回异常，可能是STRING OR INT
							if (callback != null) {
								callback.onSuccess(true);
							}
						} else {
							if (callback != null) {
								callback.onSuccess(false);
							}
						}
					}else if (result instanceof Integer){
						int retValue = (int) result;
						if (retValue > 0) {
							if (callback != null) {
								callback.onSuccess(true);
							}
						} else {
							if (callback != null) {
								callback.onSuccess(false);
							}
						}
					}
				}else {
					if (callback != null) {
						callback.onSuccess(false);
					}
				}
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				/*Utils.LogV("MyBaby", prifixError + xmlrpcMethod
						+ "-onFailure: " + error.getMessage());*/
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
			if(callback != null){
				callback.onFailure(0,null);
			}
		}
	}


	public static void rpcCallForReturnString(final String prifixError,
			final String xmlrpcMethod, Object[] params,
			final CallbackForString callback) {
		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				if (result != null&&result instanceof String) {
					if (callback != null) {
						callback.onSuccess((String) result);
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
			if(callback != null){
				callback.onFailure(0,null);
			}
		}
	}
	
	/**
	 * 此回调专属手机注册，他处不要使用
	 * 手机短信注册返回码
	 * @param prifixError
	 * @param xmlrpcMethod
	 * @param params
	 * @param callback
	 */
	public static void rpcCallForReturnInt(final Register register,final String prifixError,
			final String xmlrpcMethod, final Object[] params,
			final CallbackForRegisterId callback) {
		XMLRPCCallback nCallback = new XMLRPCCallback() {
			@Override
			public void onSuccess(long id, Object result) {
				LogUtil.e(result+"");
				int code = Integer.parseInt(result.toString());
				if (code > 0) {
					if (callback != null) {
						SelfPerson person=PostRepository.loadSelfPerson(MyBabyApp.currentUser.getUserId());
						person.setName(register.getDisplayname());
						MyBabyApp.currentUser.setFrdTelephone(register.getMobile());
	                    MyBabyApp.currentBlog.setPassword(register.getUserpass());
	                    MyBabyApp.currentUser.setName(register.getDisplayname());
	                    MyBabyApp.currentUser.setBzRegistered(true);
	                    MyBabyApp.currentUser.setBzInfoModified(true);//信息修改需要上传标记
	                	UserRepository.save(MyBabyApp.currentUser); 
	                	BlogRepository.save(MyBabyApp.currentBlog);
	                	PostRepository.save(person.getData());//保存编辑信息
						callback.onSuccess();
					}
				} else if (code==-2) {
					if (callback != null) {
						callback.onHasPhone();
					}
				}else {
					Utils.LogV("MyBaby", prifixError + "-" + xmlrpcMethod
							+ ": 服务器api返回结果错误");
					if (callback != null) {
						callback.onFailure(code, null);
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
			if(callback != null){
				callback.onFailure(0,null);
			}

		}
	}
}
