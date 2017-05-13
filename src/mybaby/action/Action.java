package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import org.xutils.common.util.LogUtil;

import java.io.Serializable;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import mybaby.Constants;
import mybaby.models.community.ParentingPost;
import mybaby.util.ExceptionUtil;
import mybaby.util.LogUtils;
import mybaby.util.StringUtils;
import mybaby.util.Utils;

public abstract class Action implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String actiontTitle = "";
	private Object serializable_data;//任意数据，留出拓展空余
	private String real_url;//url


	private static QQAction qqAction = new QQAction();
	private static QzoneAction qzoneAction = new QzoneAction();
	private static WechatFriendsAction wechatFriendsAction = new WechatFriendsAction();
	private static WechatAction wechatAction = new WechatAction();
	private static SinaWBAction sinaWBAction = new SinaWBAction();
	private static ShareMenuAction shareMenuAction = new ShareMenuAction();
	private static BrowseAction browseAction = new BrowseAction();
	private static OpenSignUpPageAction openSignUpPageAction = new OpenSignUpPageAction();
	private static OpenPlaceListAction openPlaceListAction = new OpenPlaceListAction();
	private static OpenTopicListAction openTopicListAction = new OpenTopicListAction();
	private static AddDiaryAction addDiaryAction = new AddDiaryAction();
	private static AddTopicAction addTopicAction = new AddTopicAction();
	private static ControllerCloseAction controllerCloseAction = new ControllerCloseAction();
	private static P2PChattingAction p2PChattingAction = new P2PChattingAction();
	private static TribeChattingAciton tribeChattingAciton = new TribeChattingAciton();
	private static TribeSpaceAction tribeSpaceAction = new TribeSpaceAction();
	private static CallPhoneAction callPhoneAction = new CallPhoneAction();
	private static ShowPlaceAction showPlaceAction = new ShowPlaceAction();
	private static OpenSignInPageAction openSignInPageAction = new OpenSignInPageAction();
	private static UpdatePersionInfoAction updatePersionInfoAction = new UpdatePersionInfoAction();
	private static NotificationAction notificationAction = new NotificationAction();
	private static NotificationJoinAction notificationJoinAction = new NotificationJoinAction();
	private static UserPlaceSettingAction userPlaceSettingAction = new UserPlaceSettingAction();
	private static FollowNotificationAction followNotificationAction = new FollowNotificationAction();
	private static FollowTopicCategoryActivityAction followTopicCategoryActivityAction = new FollowTopicCategoryActivityAction();
	private static FriendsActivityAction friendsActivityAction = new FriendsActivityAction();
	private static LoadUrlAction loadUrlAction = new LoadUrlAction();
	private static TribeSystemManagerAction tribeSystemManagerAction = new TribeSystemManagerAction();
	private static RPCAction rpcAction = new RPCAction();
	private static DialogSheetAction dialogSheetAction = new DialogSheetAction();
	private static DialogTipInfoAction dialogTipInfoAction = new DialogTipInfoAction();
	private static RefreshNotifyAction refreshNotifyAction = new RefreshNotifyAction();
	private static ActivityDetailAction activityDetailAction = new ActivityDetailAction();
	private static ShoppingCartAction shoppingCartAction = new ShoppingCartAction();
	private static ShoppingItemDetailAction shoppingItemDetailAction = new ShoppingItemDetailAction();
	private static ShoppingOrderAction shoppingOrderAction = new ShoppingOrderAction();
	private static BabySpaceAction babySpaceAction = new BabySpaceAction();
	private static JumpShopAction jumpShopAction = new JumpShopAction();
	private static JumpDetailAction jumpDetailAction = new JumpDetailAction();
	private static JumpTBURIAction jumpTBURIAction = new JumpTBURIAction();

	private boolean needCheckUrl = false;//是否检查url是否具有与app服务器相同的域名

	public abstract Action setData(String url, Map<String, Object> map);

	public abstract Boolean excute(final Activity activity, UMSocialService webviewController, final WebView webView, ParentingPost parentingPost);

	public Action(String actiontTitle) {
		this.actiontTitle = actiontTitle;
	}

	public Action() {
	}

	public static Action createAction(String url, String title, Object data) {
		LogUtils.e("action执行的链接：" + url +"头部字符："+title);
		Action action = null;
		try {
			action = createAction(url).setReal_url(url).setActiontTitle(title).setSerializable_data(data);
		} catch (Exception e) {
			e.printStackTrace();
			return action;
		}
		return action;
	}

	public static Action createAction(String url, boolean needCheckUrl) {
		LogUtils.e("action执行的链接：" + url + "是否需要检查url的服务器~~~~~~" + needCheckUrl);

		if (needCheckUrl) {
			//int ex = url.indexOf("//");
			//int ed = Constants.MY_BABY_SITE_URL_CHECK.length();
			if (!StringUtils.getHost(url).contains(Constants.MY_BABY_SITE_URL_CHECK))
				return null;
		}
		Action action = null;
		try {
			action = createAction(url).setReal_url(url);
		} catch (Exception e) {
			return action;
		}
		return action;
	}

	public static Action createAction(String real_url) {
		Utils.Loge(real_url);
		int start = 0;
		start = real_url.indexOf("?");
		String url = real_url;
		if (start > 0)
			url = real_url.substring(0, start);
		//if (real_url.length() - start >= 0)
		//else return null;//此处不可返回空，因为可能URL中只有ACTION的key却没有参数
		Map<String, Object> map;
		map=Action.getHeader(real_url);
		Action[] actions = new Action[]{qqAction.setData(url, map),
				qzoneAction.setData(url, map),
				wechatAction.setData(url, map),
				wechatFriendsAction.setData(url, map),
				sinaWBAction.setData(url, map),
				shareMenuAction.setData(url, map),
				browseAction.setData(url, map),
				openSignUpPageAction.setData(url, map),
				openPlaceListAction.setData(url, map),
				openTopicListAction.setData(url, map),
				addDiaryAction.setData(url, map),
				addTopicAction.setData(url, map),
				controllerCloseAction.setData(url, map),
				p2PChattingAction.setData(url, map),
				tribeChattingAciton.setData(url, map),
				tribeSpaceAction.setData(url, map),
				callPhoneAction.setData(url, map),
				showPlaceAction.setData(url, map),
				openSignInPageAction.setData(url, map),
				updatePersionInfoAction.setData(url, map),
				notificationAction.setData(url, map),
				notificationJoinAction.setData(url, map),
				userPlaceSettingAction.setData(url, map),
				followNotificationAction.setData(url, map),
				followTopicCategoryActivityAction.setData(url, map),
				friendsActivityAction.setData(url, map),
				loadUrlAction.setData(url, map),
				tribeSystemManagerAction.setData(url, map),
				rpcAction.setData(url, map),
				dialogSheetAction.setData(url, map),
				dialogTipInfoAction.setData(url, map),
				refreshNotifyAction.setData(url, map),
				activityDetailAction.setData(url, map),
				shoppingCartAction.setData(url, map),
				shoppingItemDetailAction.setData(url, map),
				shoppingOrderAction.setData(url, map),
				babySpaceAction.setData(url, map),
				jumpDetailAction.setData(url, map),
				jumpShopAction.setData(url, map),
				jumpTBURIAction.setData(url, map)
		};
		for (int i = 0; i < actions.length; i++) {
			if (null != actions[i]) {
				Action action = actions[i];
				actions[i] = null;
				return action.setReal_url(url);
			} else {
				continue;
			}
		}
		return null;
	}



//将获取链接转为map匹配
		/**
		 * 方法名称:transStringToMap
		 * 传入参数:mapString 形如 username=chenziwen&password=1234
		 * 返回值:Map
		*/
		public static Map<String, Object> getHeader(String url) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				//url = URLDecoder.decode(url, "UTF-8");//后一步解码
				int start = 0;
				try {
					start = url.indexOf("?");
					if (url.length() - start >= 0) {
						String str = url.substring(start + 1);
						String[] paramsArr = str.split("&");
						if (paramsArr != null && paramsArr.length > 0) {
							for (String param : paramsArr) {
								String[] temp = param.split("=");
								if (temp != null ){
									if (temp.length > 1)
										map.put(temp[0], URLDecoder.decode(param.substring(temp[0].length()+1), "UTF-8"));//此处取所有参数
									//map.put(temp[0], temp[1]);
									else map.put(temp[0], "");
								}
								else
									map.put(param, param);
							}
						} else {
							String[] temp = str.split("=");
							if (temp != null && temp.length > 0)
								map.put(temp[0], "");
							else
								map.put(str, str);
						}
					}
				} catch (Exception e) {
					map.put(url, url);
				}
				return map;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ExceptionUtil.getStack(e);
				return map;
			}
		}

	public static  String newActionLink(String[][] strings,String actionType){
		String link = Constants.MY_BABY_SITE_URL + "/" + actionType;
		if (strings!=null&&strings.length>0) {
			for (int i = 0; i < strings.length; i++){
				link+=(i==0?"?":"&")+strings[i][0]+"="+strings[i][1];
			}
		}else LogUtil.e("link creat fail!");
		return  link;
	}

	public String getActiontTitle() {
		return actiontTitle;
	}

	public Action setActiontTitle(String actiontTitle) {
		this.actiontTitle = actiontTitle;
		return this;
	}

	public Object getSerializable_data() {
		return serializable_data;
	}

	public Action setSerializable_data(Object serializable_data) {
		this.serializable_data = serializable_data;
		return this;
	}

	public String getReal_url() {
		return real_url;
	}

	public Action setReal_url(String real_url) {
		this.real_url = real_url;
		return this;
	}
}
