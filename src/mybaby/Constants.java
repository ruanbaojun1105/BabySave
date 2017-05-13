package mybaby;

import android.os.Environment;

import me.hibb.mybaby.android.BuildConfig;
import mybaby.models.community.Place;
import mybaby.models.community.TopicCategory;
import mybaby.models.community.UserPlaceSetting;
import mybaby.ui.broadcast.PostMediaTask;

public class Constants {

	/**
	 * User-Agent string used when making HTTP connections. This is used both
	 * for API traffic as well as embedded WebViews.
	 */
	public static final String USER_AGENT = "MyBaby";
	
	public static final String DESCRIPTOR = "com.umeng.share"; 
	
	public static final String TENCENT_APPID;

	static {
		TENCENT_APPID = "1104772101";
	}
	/*阿里百川app key和secret
	测试环境：
	appkey=23265821
	secret=b10b00b318ab933e70be5e12108ea39c

	正式环境：
	appkey=23111765
	secret=78395371c4ad3a02835e5922f153e9c0*/
	public static final String OPENIM_KEY=BuildConfig.DEBUG ?"23265821":"23111765";//此处还是需要debug的key，因为服务器返回的百川登录信息是debug的结果
	public static final String OPENIM_SECRET=BuildConfig.DEBUG ?"b10b00b318ab933e70be5e12108ea39c":"78395371c4ad3a02835e5922f153e9c0";

	// openIM UI解决方案提供的相关API，创建成功后，保存为全局变量使用
	//腾讯开放api id//  lj  1104084483  bj  1104772101
	public static final String TENCENT_APPKEY = "GLSkr5fmBGEkQhvj";
	//腾讯开放api KEY . lj.  GLSkr5fmBGEkQhvj  .bj.  MA2RkA5u0UhJUBSG
	public static final String WEIXIN_APPID = "wx3dda02c8fb13c92a";
	//腾讯开放api id    bj wx97429c7254b1dd4e lj wx3dda02c8fb13c92a
	public static final String WEIXIN_APPKEY = "8480e42d118b80044dd05dcc4af82be7";
	//腾讯开放api KEY   bj 6e1dd7557b2f9e47a137e16300370c1b  lj 8480e42d118b80044dd05dcc4af82be7

	public static final String MY_BABY_SITE_URL = BuildConfig.DEBUG ? "http://www.lejimami.com" : "http://www.hibb.me";
	public static final String MY_BABY_SITE_URL_CHECK = BuildConfig.DEBUG ? ".lejimami.com" : ".hibb.me";
//  public static final String MY_BABY_SITE_URL ="http://192.168.1.125/mybaby";//junwei
//	public static final String MY_BABY_SITE_URL ="http://192.168.1.105/mybaby";
//  public static final String MY_BABY_SITE_URL ="http://192.168.1.120:5008";
//	public static final String MY_BABY_SITE_URL = "http://www.hibb.me";
//    public static final String MY_BABY_SITE_URL ="http://192.168.1.205/mybaby";	//cuijian

	public static final String MY_BABY_SHOP_STREE=MY_BABY_SITE_URL+"/shopping-home";
	public static final String MY_BABY_HLEP_HELP=MY_BABY_SITE_URL+"/help-list";
	public static final String MY_BABY_HLEP_FEEDBACK=MY_BABY_SITE_URL+"/feedback";
	public static final String MY_BABY_HLEP_FEEDBACK_DETAIL=MY_BABY_SITE_URL+"/help-post?id=";	//连接后面续上ID
	public static final String MY_BABY_GPassword_URL=MY_BABY_SITE_URL+"/forget-password";
	public static final String MY_BABY_XMLRPC_URL = MY_BABY_SITE_URL+ "/xmlrpc.php";
	public static final String MY_BABY_FAVORITE=MY_BABY_SITE_URL+"/favorite";//收藏夹连接
	public static final String MY_BABY_TribeURL=MY_BABY_SITE_URL+"/group-chat-icon?im_group_id=";//后接群id,群图片
	public static final String MY_BABY_InvaidURL="http://img.hibb.me/s/icon/notification/group_invite.jpg";//群邀请
	public static final String MY_BABY_UserPIC_URL=MY_BABY_SITE_URL+"/user-agreement";//用户协议
	public static final String MY_BABY_Topic_URL=MY_BABY_SITE_URL+"/my_topic_category";//我的话题

	public static final String MY_BABY_APP_SHARE_URL = MY_BABY_SITE_URL + "/a?f=ard";
	public static final String MY_BABY_POST_SHARE_URL = MY_BABY_SITE_URL
			+ "/b?t=%1$s";
	public static final String MY_BABY_DEFAULT_AVATAR_URL = MY_BABY_SITE_URL
			+ "/wp-content/themes/babyzone/postView/images/avatar.jpg";
	public static final String MY_BABY_APP_ICON_URL = MY_BABY_SITE_URL
			+ "/wp-content/themes/babyzone/appHome/images/logo.png";
	public static final String MY_BABY_APP_AD_IMAGE_URL = MY_BABY_SITE_URL
			+ "/wp-content/themes/babyzone/appHome/images/adimage.png";

	public static final String MY_BABY_DEFAULT_PASSWORD = "fjdlj87kj8768"; // �������޸�
	public static final String BASE64_CODE_KEY = "876943"; // Ҫ��php��һ��

	public static final int PHOTO_MAX_SIZE = 1024; // ��Ƭ���ߵ���С�ߴ�

	public static final int MAX_PHOTO_PER_POST = 9; // 日记每条记录最多支持的照片数量
	public static final int TOPIC_MAX_PHOTO_PER_POST = 6; // 帖子每条记录最多支持的照片数量
	public static int WEBVIEW_PAGE_COUNTS_MAX = 5; // 浏览器支持最多开启的窗口
	public static int CACHE_PAGE_INTERVA =  BuildConfig.DEBUG?5:60; // 页面缓存过期时间
	
	public static TopicCategory[] TOPIC_CATEGORIES = null;// 全局推荐话题
	public static TopicCategory category=null;//短暂保存的topiccategory，程序中已控制内存，无需处理
	public static UserPlaceSetting userPlaceSetting=null;
	public static Place babyPlace=null;
	public static String Channel="";
	public static String Action_Msg_Notifition="";
	public static Boolean hasLoginOpenIM=false;
	public static PostMediaTask postTask=null;
	public static Boolean hasTipReUpImage=false;//如果上传图片失败，则标记是否已提示需要重试

	public static final String MyBaby_SdCachePath = "sdcard/MyBaby/";
	public static final String Conversation_Key = "conversation_key";
	public static final String ConversationManager_Key = "conversation_system_manager_key";
	public static final String APP_NAME = "辣妈说";
	public static final String MyBaby_PhotoPath = Environment.getExternalStorageDirectory()+"/"+APP_NAME+"/";
	public static final String CacheKey_CommunityActivity_Main = "CommunityActivity_Main";
	public static final String CacheKey_CommunityActivity = "CommunityActivity";
	public static final String CacheKey_CommunityActivity_Hot = "CommunityActivity_hot";
	public static final String CacheKey_CommunityActivity_Place = "CommunityActivity_Place";
	public static final String CacheKey_CommunityActivity_Friend = "CommunityActivity_Friend";
	public static final String CacheKey_CommunityActivity_TribeSpace = "CommunityActivity_TribeSpace";
	public static final String CacheKey_CommunityActivity_Near = "CommunityActivity_Near";
	public static final String CacheKey_CommunityActivity_Follow_Category = "CommunityActivity_Follow_Category";//我关注的话题列表
	public static final String CacheKey_CommunityActivity_Topic_Main = "CommunityActivity_Topic_Main";
	public static final String CacheKey_CommunityActivity_Topic_Top = "CommunityActivity_Topic_Top";
	public static final String CacheKey_CommunityActivity_NotificationCategory = "CommunityActivity_NotificationCategory";
	public static final String CacheKey_CommunityActivity_Notifications = "CommunityActivity_Notification";
	public static final String CacheKey_CommunityActivity_ParentPost = "CommunityActivity_ParentPosts";
	public static final String CacheKey_NotificationCategory_Self = "NotificationCategory_self";
	public static final String CacheKey_Discovery_ntfc = "Discovery_ntfc";//发现列表缓存列表
	public static final String CacheKey_Discovery_list = "Discovery_list";//发现列表
	public static final String CacheKey_NotificationOthers = "CacheKey_NotificationOther";//通知列表Others节点更新导航红点
	public static final String CacheKey_SuitOpenRPC = "CacheKey_SuitOpenRPC";
	
	public static final String CacheKey_More_Looked= "CacheKey_More_Looked";//看过
	public static final String CacheKey_More_Likeed = "CacheKey_More_Likeed";//赞过
	public static final String CacheKey_Looked_And_More_Likeed = "CacheKey_Looked_And_More_Likeed";//赞过和看过

	public static final String CacheKey_FollowActivity_follow = "FollowActivity_follow";
	public static final String CacheKey_FollowerActivity_follow = "FollowerActivity_follow";
	public static final String CacheKey_Friend = "CacheKey_Friend";
	public static final String CacheKey_Trinbes = "CacheKey_Trinbes";
	
	public static final String CacheKey_Version = "Version";
	public static final String CacheKey_LastTime = "LastTime";
	public static final String CacheKey_LastId = "LastId";
	public static final String CacheKey_HasMore = "HasMore";


	public class RequestCode {
		public static final int ACTIVITY_REQUEST_CODE_PICTURE_LIBRARY = 1000;
		public static final int ACTIVITY_REQUEST_CODE_TAKE_PHOTO = 1100;
		public static final int ACTIVITY_REQUEST_CODE_VIDEO_LIBRARY = 1200;
		public static final int ACTIVITY_REQUEST_CODE_TAKE_VIDEO = 1300;
		public static final int ACTIVITY_REQUEST_CODE_BROWSE_FILES = 1400;
		public static final int ACTIVITY_REQUEST_CODE_CROP_PHOTO = 1400;
		public static final int ACTIVITY_REQUEST_CODE_MUL_PICKER = 1500;


		public static final int ACTIVITY_REQUEST_CODE_SIGN_UP_REQUEST = 100;
		public static final int ACTIVITY_REQUEST_CODE_EDIT_POST_REQUEST = 101;
		public static final int ACTIVITY_REQUEST_CODE_GALLERY_REQUEST = 102;

		public static final int ACTIVITY_REQUEST_CODE_CONTACTS = 200;

		public static final int ACTIVITY_REQUEST_CODE_SHOWPLACE = 1003;
		public static final int ACTIVITY_REQUEST_CODE_RESETPLACE = 11;
		
		public static final int ACTIVITY_REQUEST_CODE_SETLABEL =  12;
		
	}

	public class BroadcastAction {
		public static final String BroadcastAction_UMeng_Push_Register_Done = "me.hibb.mybaby.android.boardcast.umeng.push.register.done";

		public static final String BroadcastAction_Anonymous_SignUp_Done = "me.hibb.mybaby.android.boardcast.anonymous.signup.done";

		public static final String BroadcastAction_Person_Add = "me.hibb.mybaby.android.boardcast.person.add";
		public static final String BroadcastAction_Person_Edit = "me.hibb.mybaby.android.boardcast.person.edit";
		public static final String BroadcastAction_Person_Delete = "me.hibb.mybaby.android.boardcast.person.delete";

		public static final String BroadcastAction_Post_Add = "me.hibb.mybaby.android.boardcast.post.add";
		public static final String BroadcastAction_Post_Edit = "me.hibb.mybaby.android.boardcast.post.edit";
		public static final String BroadcastAction_Post_Delete = "me.hibb.mybaby.android.boardcast.post.delete";

		public static final String BroadcastAction_Post_Home_Open = "me.hibb.mybaby.android.boardcast.post.home.open";

		public static final String BroadcastAction_Sign_Up_Done = "me.hibb.mybaby.android.boardcast.sign.up";

		public static final String BroadcastAction_Refresh_Self_HomeTimeline_Notification = "me.hibb.mybaby.android.boardcast.refresh.self.hometimeline.Notification";
		public static final String BroadcastAction_Refresh_Self_HomeTimeline_Success = "me.hibb.mybaby.android.boardcast.refresh.self.hometimeline.success";
		public static final String BroadcastAction_Refresh_Friend_HomeTimeline_Success = "me.hibb.mybaby.android.boardcast.refresh.friend.hometimeline.success";
		public static final String BroadcastAction_Refresh_HomeTimeline_Faile = "me.hibb.mybaby.android.boardcast.refresh.hometimeline.faile";

		public static final String BroadcastAction_Edit_Post_Create_Medias_Done = "me.hibb.mybaby.android.boardcast.edit.post.create.medias.done";
		public static final String BroadcastAction_Notification_Summary = "me.hibb.mybaby.android.boardcast.notification.summary";
		public static final String BroadcastAction_Notification_Refush_OK = "me.hibb.mybaby.android.boardcast.Notification.refush.ok";
		public static final String BroadcastAction_PersonPage_Follow = "me.hibb.mybaby.android.boardcast.personpage.follow";

		public static final String BroadcastAction_PersonPlace_Unfollow = "me.hibb.mybaby.android.boardcast.personplace.unfollow";
		public static final String BroadcastAction_Persontopic_Unfollow = "me.hibb.mybaby.android.boardcast.persontopic.unfollow";
		
		public static final String BroadcastAction_PlaceSetting_IsSuccess = "me.hibb.mybaby.android.boardcast.placesetting.issuccess";//地点设置是否成功
		
		public static final String BroadcastAction_Detail_Refush = "me.hibb.mybaby.android.boardcast.detail.refush";//详情赞刷新
		public static final String BroadcastAction_Detail_Like_Add = "me.hibb.mybaby.android.boardcast.detail.like.add";//add
		public static final String BroadcastAction_Detail_Like_Remove = "me.hibb.mybaby.android.boardcast.detail.like.remove";//remove
		public static final String BroadcastAction_Topic_Category_Refush = "me.hibb.mybaby.android.boardcast.topic.category.refush";//话题刷新
		public static final String BroadcastAction_Topic_Delete = "me.hibb.mybaby.android.boardcast.topic.delete";
		public static final String BroadcastAction_Main_UpdateTabBadge = "me.hibb.mybaby.android.boardcast.main.uptadeTabBadge";//更新红点获取服务器红点信息
		public static final String BroadcastAction_Main_KillAllPage = "me.hibb.mybaby.android.boardcast.main.killAllPage";

		public static final String BroadcastAction_CommunityMain_Refush = "me.hibb.mybaby.android.boardcast.communityMain.refush";

		public static final String BroadcastAction_Post_UploadMediaByid_OK = "me.hibb.mybaby.android.boardcast.post.uploadmedia.ok";//单张图片在日记中的上传OK标志
		public static final String BroadcastAction_Post_Media_Uploding = "me.hibb.mybaby.android.boardcast.post.media_uploding";

		public static final String BroadcastAction_MainActivity_Home_TagBadgeUpdate = "me.hibb.mybaby.android.boardcast.mainActivity.updateTagBadge";
	
		public static final String BroadcastAction_Activity_Main_Need_BackTop = "me.hibb.mybaby.android.boardcast.mainactivity.BackTop";
		
		public static final String BroadcastAction_Netok = "me.hibb.mybaby.android.boardcast.netok";
		public static final String BroadcastAction_Netbad = "me.hibb.mybaby.android.boardcast.netbad";

		public static final String BroadcastAction_CommunityMain_ImageUpLoading = "me.hibb.mybaby.android.boardcast.communityMain.ImageUpLoading";
		public static final String BroadcastAction_CommunityMain_ImageUpLoadingFinshed = "me.hibb.mybaby.android.boardcast.communityMain.ImageUpLoading.Finedsh";
		
		public static final String BroadcastAction_MainActivity_CurrentTabTag = "me.hibb.mymaby.android.boardcast.mainactivity.currenttabtag";

		public static final String BroadcastAction__OpenIM_Login_Success = "OpenIM_Login_Success";
		public static final String BroadcastAction__OpenIM_Notification_Change = "OpenIM_Notification_Change";
		public static final String BroadcastAction_CloseTribe="CloseTribe";//关闭群
		public static final String BroadcastAction_NotificationCagetory_Refush="NotificationCagetory_Refush";//刷新消息列表
		public static final String BroadcastAction_StargetNewestSummaryFromService="stargetNewestSummaryFromService";
	}

}
