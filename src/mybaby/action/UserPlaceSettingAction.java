package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.models.community.UserPlaceSetting;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.util.MapUtils;

/**
 * 
 * @author bj
 *  地点设置
	标识符：mybaby_user_placesetting
	参数：settingkey；keywords；types；settingname；excluderegex；nearbyDistance（int类 型）
 *
 */
public class UserPlaceSettingAction extends Action implements Serializable{
	public static String actionUrl="mybaby_user_placesetting";//关闭页面
	int nearbyDistance;
	String settingkey; 
	String keywords; 
	String types; 
	String settingname;
	String excluderegex;
	UserPlaceSetting userPlaceSetting;
	String success_msg;

	public String getSuccess_msg() {
		return success_msg;
	}

	public void setSuccess_msg(String success_msg) {
		this.success_msg = success_msg;
	}

	public static String getActionUrl() {
		return actionUrl;
	}

	public static void setActionUrl(String actionUrl) {
		UserPlaceSettingAction.actionUrl = actionUrl;
	}

	public UserPlaceSetting getUserPlaceSetting() {
		return userPlaceSetting;
	}

	public void setUserPlaceSetting(UserPlaceSetting userPlaceSetting) {
		this.userPlaceSetting = userPlaceSetting;
	}

	public int getNearbyDistance() {
		return nearbyDistance;
	}

	public void setNearbyDistance(int nearbyDistance) {
		this.nearbyDistance = nearbyDistance;
	}

	public String getSettingkey() {
		return settingkey;
	}

	public void setSettingkey(String settingkey) {
		this.settingkey = settingkey;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String getSettingname() {
		return settingname;
	}

	public void setSettingname(String settingname) {
		this.settingname = settingname;
	}

	public String getExcluderegex() {
		return excluderegex;
	}

	public void setExcluderegex(String excluderegex) {
		this.excluderegex = excluderegex;
	}

	public UserPlaceSettingAction() {
		super();
	}
	public UserPlaceSettingAction(int nearbyDistance, String settingkey,
			String keywords, String types, String settingname,
			String excluderegex,String success_msg) {
		super();
		this.nearbyDistance = nearbyDistance;
		this.settingkey = settingkey;
		this.keywords = keywords;
		this.types = types;
		this.settingname = settingname;
		this.excluderegex = excluderegex;
		this.success_msg = success_msg;
	}

	@Override
	public Action setData(String url,Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (url.contains(actionUrl)){
			//Map<String, Object> map=Action.getHeader(getReal_url());
			this.nearbyDistance= MapUtils.getMapInt(map, "nearbyDistance");
			this.settingkey=MapUtils.getMapStr(map,"settingkey");
			this.keywords=MapUtils.getMapStr(map,"keywords");
			this.types=MapUtils.getMapStr(map,"types");
			this.settingname=MapUtils.getMapStr(map,"settingname");
			this.excluderegex=MapUtils.getMapStr(map,"excluderegex");
			this.success_msg=MapUtils.getMapStr(map,"success_msg");
			return new UserPlaceSettingAction(nearbyDistance, settingkey, keywords, types,settingname,excluderegex,success_msg);
		}else return null;
	}


	@Override
	public Boolean excute(Activity activity, UMSocialService webviewController,
					   WebView webView, ParentingPost parentingPost) {
		CustomAbsClass.startPlaceSetting(activity,new UserPlaceSetting(settingkey, keywords,settingname, null, excluderegex, nearbyDistance),success_msg);
		return true;
	}
}
