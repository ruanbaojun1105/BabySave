package mybaby.ui.community.customclass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.xmlrpc.android.XMLRPCException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mybaby.ui.Notification.NotificationCategoryActivity;
import photopickerlib.PhotoPickerActivity;
import me.hibb.mybaby.android.BuildConfig;
import me.hibb.mybaby.android.IMActivity.ChattingFragmentActivity;
import me.hibb.mybaby.android.IMActivity.ChattingTribeFragmentActivity;
import me.hibb.mybaby.android.IMActivity.TribeSystemMessageActivity;
import mybaby.Constants;
import mybaby.models.User;
import mybaby.models.community.ParentingPost;
import mybaby.models.community.Place;
import mybaby.models.community.TopicCategory;
import mybaby.models.community.activity.AbstractMainActivity;
import mybaby.models.diary.Media;
import mybaby.models.diary.PostRepository;
import mybaby.models.person.Baby;
import mybaby.models.person.Person;
import mybaby.models.person.SelfPerson;
import mybaby.rpc.BaseRPC.Callback;
import mybaby.rpc.BaseRPC.CallbackForBool;
import mybaby.rpc.BaseRPC.CallbackForMap;
import mybaby.rpc.UserRPC;
import mybaby.rpc.community.PlaceRPC;
import mybaby.rpc.community.PlaceRPC.PlaceCallback;
import mybaby.rpc.notification.TopicCategoryRPC;
import mybaby.ui.GuideActivity;
import mybaby.ui.MyBabyApp;
import mybaby.ui.Notification.NotificationActivity;
import mybaby.ui.Notification.TribeGroupListActivity;
import mybaby.ui.community.DetailsActivity;
import mybaby.ui.community.FriendListActivity;
import mybaby.ui.community.ImagePageActivity;
import mybaby.ui.community.LikedAndLookedListActivity;
import mybaby.ui.community.MyFollowTopicCategoryListActivity;
import mybaby.ui.community.PlaceListActivity;
import mybaby.ui.community.SuitOpenRPCListActivity;
import mybaby.ui.community.TopicEditActivity;
import mybaby.ui.community.TopicListActivity;
import mybaby.ui.community.TribeSpaceListActivity;
import mybaby.ui.community.parentingpost.ParentingPostActivity;
import mybaby.ui.community.parentingpost.ParentingWebViewActivity;
import mybaby.ui.main.MainUtils;
import mybaby.ui.more.PersonPageActivity;
import mybaby.ui.more.PlaceSettingActivity;
import mybaby.ui.more.SettingFragmentActivity;
import mybaby.ui.more.ShowMapActivity;
import mybaby.ui.more.personpage.FollowActivity;
import mybaby.ui.more.personpage.FollowerActivity;
import mybaby.ui.more.personpage.PersonPlaceActivity;
import mybaby.ui.posts.edit.EditPostActivity;
import mybaby.ui.posts.home.HomeTimelineActivity;
import mybaby.ui.user.PersonEditActivity;
import mybaby.ui.user.RegisterActivity;
import mybaby.ui.user.UserFriendListActivity;
import mybaby.util.DialogShow;
import mybaby.util.DialogShow.DoSomeThingListener;
import mybaby.util.Utils;

public class CustomAbsClass {
	/**
	 * 直接开启相机
	 * @param context
	 * @param requestCod
     */
	public static void starPhotoPickerActivity(final Activity context,int requestCod,boolean isCrop){
		Intent intent = new Intent(context, PhotoPickerActivity.class);
		intent.putExtra(PhotoPickerActivity.EXTRA_OPEN_CAMERA, true);
		intent.putExtra(PhotoPickerActivity.EXTRA_OPEN_CAMERA_CUT, isCrop);
		context.startActivityForResult(intent, requestCod);
	}

	/**
	 * 单选照片
	 * @param context
	 * @param requestCod
	 * @param showCamera
     */
	public static void starPhotoPickerActivity(final Activity context,int requestCod,boolean showCamera,boolean isCrop){
		Intent intent = new Intent(context, PhotoPickerActivity.class);
		intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera);
		intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, PhotoPickerActivity.MODE_SINGLE);
		intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, PhotoPickerActivity.REQUEST_CAMERA);
		intent.putExtra(PhotoPickerActivity.EXTRA_OPEN_CAMERA_CUT, isCrop);
		context.startActivityForResult(intent, requestCod);
	}
	/**
	 * 打开照片选择器
	 */
	public static void starPhotoPickerActivity(final Activity context,int requestCod,boolean showCamera,int selectedMode,int maxNum){
		Intent intent = new Intent(context, PhotoPickerActivity.class);
		intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera);
		intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, selectedMode);
		intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, maxNum);
		context.startActivityForResult(intent, requestCod);
	}
	/**
	 * 打开宝宝空间
	 * @param context
	 */
	public static void starHomeTimelineActivity(final Context context){
		Intent intent = new Intent(context, HomeTimelineActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 打开宝宝空间
	 * 选择照片并去写日记
	 * @param context
	 * @param needOpenPhotos
	 */
	public static void starHomeTimelineActivity(final Context context,boolean needOpenPhotos){
		Intent intent = new Intent(context, HomeTimelineActivity.class);
		intent.putExtra("needOpenPhotos", needOpenPhotos);
		context.startActivity(intent);
	}

	/**
	 * 打开宝宝空间
	 * 选择照片去修改头像
	 * @param context
	 * @param needSelectPhoto
	 * @param person
	 */
	public static void starHomeTimelineActivity(final Context context,boolean needSelectPhoto,Person person){
		Intent intent = new Intent(context, HomeTimelineActivity.class);
		intent.putExtra("needSelectPhoto", needSelectPhoto);
		intent.putExtra("person", person);
		context.startActivity(intent);
	}
	/**
	 * 打开个人信息编辑页
	 */
	public static void starPersionEditActivity(final Context context,SelfPerson selfPerson){
		if (selfPerson!=null) {
			Intent intent = new Intent(context,
					PersonEditActivity.class);
			intent.putExtra("personId", selfPerson.getId());
			context.startActivity(intent);
		}
	}
	/**
	 * 打开好友列表
	 */
	public static void starUserFriendListActivity(final Context context){
		Intent intent = new Intent(context, UserFriendListActivity.class);
		context.startActivity(intent);
	}
	/**
	 * 打开群组列表
	 */
	public static void starTribeGroupActivity(final Context context){
		Intent intent = new Intent(context, TribeGroupListActivity.class);
		context.startActivity(intent);
	}
	/**
	 * 打开引导页
	 */
	public static void starGuideActivity(final Context context){
		Intent intent = new Intent(context, GuideActivity.class);
		context.startActivity(intent);
	}
	/**
	 * 我关注的话题列表
	 * @param context
	 */
	public static void starMyFollowTopicActivity(final Context context){
		Intent intent = new Intent(context, MyFollowTopicCategoryListActivity.class);
		context.startActivity(intent);
	}
	/**
	 * 打开群邀请消息管理
	 * @param context
	 */
	public static void starTribeSystemManager(final Context context){
		Intent intent = new Intent(context, TribeSystemMessageActivity.class);
		context.startActivity(intent);
	}
	/**
	 * 个人关注
	 * @param context
	 * @param userId
	 */
	public static void starPersionFollow(final Context context,int userId){
		Intent intent = new Intent(context, FollowActivity.class);
		intent.putExtra("userId", userId);
		context.startActivity(intent);
	}

	/**
	 * 个人粉丝
	 * @param context
	 * @param userId
	 */
	public static void starPersionFance(final Context context,int userId){
		Intent intent = new Intent(context, FollowerActivity.class);
		intent.putExtra("userId", userId);
		context.startActivity(intent);
	}
	/**
	 * 开启群空间
	 * @param context
	 * @param tribeId 群Id
	 */
	public static void starTribeSpace(final Context context,long tribeId,String tribe_name,boolean isNeedRefresh){
		Intent intent=new Intent(context, TribeSpaceListActivity.class);
		intent.putExtra("tribeId",tribeId);
		intent.putExtra("space_name",tribe_name);
		intent.putExtra("isNeedRefresh",isNeedRefresh);
		context.startActivity(intent);
	}
	/**
	 * 开启群空间
	 * @param context
	 * @param groupid 服务器群Id
	 * @此方法禁用
	 */
	/*public static void starTribeSpace(final Context context,int groupid,String space_name){
		Intent intent=new Intent(context, TribeSpaceListActivity.class);
		intent.putExtra("groupid",groupid);
		intent.putExtra("space_name",space_name);
		context.startActivity(intent);
	}*/
	/**
	 * 开启通知列表或者获取我参与的通知列表
	 * 0:我的动态通知列表
	 * 1：我参与的通知列表
	 * 2:我关注的通知列表
	 */
	public static void starNotificationList(final Context context,int caseRpcIndex,String title,int unread){
		Intent intent = new Intent(context, NotificationActivity.class);
		intent.putExtra("caseRpcIndex", caseRpcIndex);
		intent.putExtra("title", title);
		intent.putExtra("unread", unread);
		context.startActivity(intent);
	}
	/**
	 * 開啟设置界面
	 */
	public static void starSettingActivity(final Context context){
			Intent intent1 = new Intent(context, SettingFragmentActivity.class);
			context.startActivity(intent1);
	}
	/**
	 * 開啟單聊界面
	 */
	public static void starP2PChatting(final Context context,String openuserid,String username){
		/*if (!Utils.isNetworkAvailable()) {
			Toast.makeText(context, "没有网络连接", Toast.LENGTH_SHORT).show();
			return;
		}*/
		if (!Constants.hasLoginOpenIM) {
			MainUtils.loginIM();
		}
		Intent intent1 = new Intent(context, ChattingFragmentActivity.class);
		intent1.putExtra(ChattingFragmentActivity.TARGET_ID, openuserid);
		if (!TextUtils.isEmpty(username))
			intent1.putExtra(ChattingFragmentActivity.USERNAME, username);
		context.startActivity(intent1);
	}
	/**
	 * 開啟群聊界面
	 */
	public static void starTribeChatting(final Context context,long tribeId,String tribeName){
		/*if (!Utils.isNetworkAvailable()) {
			Toast.makeText(context, "没有网络连接", Toast.LENGTH_SHORT).show();
			return;
		}*/
		if (!Constants.hasLoginOpenIM) {
			MainUtils.loginIM();
		}
		Intent intent = new Intent(context, ChattingTribeFragmentActivity.class);
		intent.putExtra("tribeId", tribeId);
		intent.putExtra("tribeName",tribeName);
		context.startActivity(intent);
	}
	/**
	 * 通过制定标记打开赞过、看过的页面
	 */
	public static void starLikeOrLooked(final Context context,
			int initIndex) {
		Intent intent=new Intent(context, LikedAndLookedListActivity.class);
		intent.putExtra("initIndex", initIndex);
		context.startActivity(intent);
	}
	/**
	 * 打开日记或社区帖子编辑页,并且判断未注册的动作
	 */
	public abstract static class StarEdit {
		public StarEdit() {
			// TODO Auto-generated constructor stub
		}
		public abstract void todo();
		/**
		 * 检测是否已设置头像
		 * @return
		 */
		private boolean checkNeedSetImage() {
			SelfPerson mSelfPerson;
			Baby[] mBabies;
			mSelfPerson = PostRepository.loadSelfPerson(MyBabyApp.currentUser.getUserId());
			mBabies = PostRepository.loadBabies(MyBabyApp.currentUser.getUserId());
			if (mSelfPerson==null) {
				for (int i = 0; i < mBabies.length; i++) {
					if (mBabies[i]!=null) {
						if (mBabies[i].getAvatar()!=null) {
							return false;
						}
					}
				}
			}else {
				if (mSelfPerson.getAvatar()!=null) {
					return false;
				}
			}
			return true;
			
		}
		/**
		 * 回调
		 * @param context
		 */
		public void StarPostEdit(final Context context) {
			// TODO Auto-generated constructor stub
			if (BuildConfig.DEBUG){
				todo();
				return;
			}
			int postcount = PostRepository.countRealPost();
			Log.e("总数日记", postcount+"");
			if (!MyBabyApp.currentUser.getBzRegistered() && postcount > 1) {
				// 用户的日记超过2篇（不计算初始的草稿日记，草稿日记编辑发布后则计算）时要求用户必须注册才能继续写，
				// 写之前弹出注册提示框“为确保数据安全可靠，请及时注册”（只有一个确定按钮），点击确定后进入注册页面
				new DialogShow((Activity) context).signDialog(context,
						"为确保数据安全可靠，请及时注册", "马上注册", new DoSomeThingListener() {
							@Override
							public void todo() {
								// TODO Auto-generated method stub
								starRegister(context);
							}
						});
			} else if (MyBabyApp.currentUser.getBzRegistered() && postcount > 2&&checkNeedSetImage()) {
				new DialogShow((Activity) context).signDialog(context,
						"请设置你的头像", "确定", new DoSomeThingListener() {
							// 用户的日记超过3篇时要求用户必须设置头像（宝宝头像或自己的头像均可），如果没有设置则弹出提示框“请设置你的头像”，
							// （只有一个确定按钮），点击确定后进入个人信息维护界面
							@Override
							public void todo() {
								SelfPerson person = PostRepository
										.loadSelfPerson(
												MyBabyApp.currentUser.getUserId());
								starPersionInfoEdit(context, person.getId());
							}
						});
			} else {
				todo();
			}
		}
		
		/**
		 * 回调
		 */
		public void StarTopicEdit(final Context context) {
			StarTopicEdit((Activity)context);
		}
		public void StarTopicEdit(final Activity topicEditActivity) {
			if (topicEditActivity==null)
				return;
			if (BuildConfig.DEBUG){
				todo();
				return;
			}
			if (!MyBabyApp.currentUser.getBzRegistered()) {
				new DialogShow(topicEditActivity).signDialog(topicEditActivity, "请先注册", "注册",
						new DoSomeThingListener() {
							@Override
							public void todo() {
								// TODO Auto-generated method stub
								starRegister(topicEditActivity);
							}
						});
			} else {
				todo();
			}
		}
	}
	/**
	 * 无需判断未注册的动作 打开日记编辑页,不带参数传值
	 */
	public static void starPostEdit(final Context context,final Bundle bundle) {
		new StarEdit() {
			@Override
			public void todo() {
				// TODO Auto-generated method stub
				starEditPostActivity(context,bundle);
			}
		}.StarPostEdit(context);
	}
	/**
	 * 无需判断未注册的动作 打开日记编辑页,不带参数传值
	 */
	public static void starPostEdit(final Context context) {
		new StarEdit() {
			@Override
			public void todo() {
				// TODO Auto-generated method stub
				starEditPostActivity(context,null);
			}
		}.StarPostEdit(context);
	}

	private static void starEditPostActivity(final Context context,Bundle bundle) {
		Intent intentEditPost = new Intent(context, EditPostActivity.class);
		if (null!=bundle) {
			intentEditPost.putExtras(bundle);
		}
		context.startActivity(intentEditPost);
	}

	/**
	 * 打开个人信息修改编辑页
	 */
	public static void starPersionInfoEdit(final Context context,
			int personId) {
		Intent intent = new Intent(context, PersonEditActivity.class);
		intent.putExtra("personId", personId);
		context.startActivity(intent);
	}

	/**
	 * 打开注册
	 */
	public static void starRegister(Context context) {
		// TODO Auto-generated method stub
		if (MyBabyApp.currentUser.getBzRegistered())
			Toast.makeText(context,"你已注册",Toast.LENGTH_SHORT).show();
		else
			context.startActivity(new Intent(context, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}
	public static void starGalleryImagePage(Context context, Media mf, int index) {
		// TODO Auto-generated method stub
		starImagePage((Activity)context, mf,null,index, false, false, false);
	}
	/**
	 * 打开图片展示页
	 */
	public static void starImagePage(Context context, int count, int index,
			String[] imageUrls) {
		// TODO Auto-generated method stub
		starImagePage(context, count, index, imageUrls, false, false, false);
	}
	public static void starImagePage(Context context, int count, int index,
									 String[] imageUrls,boolean isEdit,boolean hasActionbar,boolean isFilePath) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, ImagePageActivity.class);
		intent.putExtra("count", count);
		intent.putExtra("index", index);
		intent.putExtra("images", imageUrls);
		intent.putExtra("isEdit", isEdit);
		intent.putExtra("hasActionbar", hasActionbar);
		intent.putExtra("isFilePath", isFilePath);
		if (isEdit)
			((Activity)context).startActivityForResult(intent, Constants.RequestCode.ACTIVITY_REQUEST_CODE_GALLERY_REQUEST);
		else context.startActivity(intent);
	}
	public static void starImagePage(Activity activity,Media mf,ArrayList<Integer> filterList,int index,boolean isEdit,boolean hasActionbar,boolean isFilePath) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, ImagePageActivity.class);
		intent.putExtra("media", mf);
		intent.putExtra("index", index);
		intent.putExtra("isEdit", isEdit);
		intent.putIntegerArrayListExtra("filterList", filterList);
		intent.putExtra("hasActionbar", hasActionbar);
		intent.putExtra("isFilePath", isFilePath);
		if (isEdit)
			activity.startActivityForResult(intent, Constants.RequestCode.ACTIVITY_REQUEST_CODE_GALLERY_REQUEST);
		else activity.startActivity(intent);
	}

	/**
	 * 打开个人主页
	 */
	public static void starUserPage(Context context, User user) {
		// TODO Auto-generated method stub
		context.startActivity(new Intent(context, PersonPageActivity.class)
				.putExtra("user", user));
	}
	public static void starUserPage(Context context, String imUserId) {
		// TODO Auto-generated method stub
		context.startActivity(new Intent(context, PersonPageActivity.class)
				.putExtra("im_userId", imUserId));
	}


	/**
	 * 打开地点设置界面
	 */
	public static void startPlaceSetting(Activity activity, Object object,
										  double Latitude, double Longitude) {
		startPlaceSetting(activity, object, Latitude, Longitude, true, true, "");
	}
	public static void startPlaceSetting(Activity activity, Object object,String success_msg) {
		startPlaceSetting(activity, object, 0, 0, true, true, success_msg);
	}
	public static void startPlaceSetting(Activity activity, Object object,
										 double Latitude, double Longitude,Boolean setBackBroadCastTag) {
		startPlaceSetting(activity, object, Latitude, Longitude, setBackBroadCastTag, true, "");
	}
	public static void startPlaceSetting(Activity activity, Object object,
										 double Latitude, double Longitude,Boolean setBackBroadCastTag,Boolean needSavePlace,String success_msg) {
		Intent intent = new Intent(activity, PlaceSettingActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("CommunityPlaceSettingItem",
				(Serializable) object);//地点设置对象,传入空则返回一个地点设置对象，不为空则为设置小区或周边地点
		bundle.putDouble("Latitude", Latitude);
		bundle.putDouble("Longitude", Longitude);
		bundle.putBoolean("setBackBroadCastTag", setBackBroadCastTag);//设置完成后是否发送广播通知
		bundle.putBoolean("needSavePlace", needSavePlace);//是否需要保存地点信息到服务器
		bundle.putString("success_msg", success_msg);
		intent.putExtras(bundle);
		activity.startActivityForResult(intent,
				Constants.RequestCode.ACTIVITY_REQUEST_CODE_SHOWPLACE);
	}

	/**
	 * 开启地图界面
	 * 
	 * @param context
	 *            activity对象
	 * @param place
	 *            place对象
	 */
	public static void startShowMapActivity(Context context, Place place) {
		Intent intent = new Intent(context, ShowMapActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("place", place);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
	public static void startShowMapActivity(Context context, int placeId) {
		Intent intent = new Intent(context, ShowMapActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("placeId", placeId);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 统一加载url网页，纯粹浏览器，不含其它页面属性 进入跳转网页页面
	 * 
	 * @param url
	 */
	public static void starWebViewIntent(Context context, String url) {

		starWebViewIntent(context, url, 0);
	}
	public static void starWebViewIntent(Context context, String url,int mbtopbar) {

		Intent intent = new Intent(context, ParentingWebViewActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("canChangeTitle", true);
		intent.putExtra("mbtopbar", mbtopbar);
		context.startActivity(intent);
	}
	public static void starSuitOpenRPCIntent(Context context,Map<String, Object> rpcmap){
		Intent intent = new Intent(context, SuitOpenRPCListActivity.class);
		intent.putExtra("rpcmap", (HashMap<String, Object>)rpcmap);
		context.startActivity(intent);
	}
	/**
	 * 增加支持根据特定rpc打开动态列表
	 *
	 * @param title
	 */
	public static void starSuitOpenRPCIntent(Context context, String title,String rpc,int right_button,
											 boolean refresh_notification,boolean need_refresh,String ext_params,String bar_color,String icon_url) {
	//下次直接传map
		Intent intent = new Intent(context, SuitOpenRPCListActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("rpc", rpc);
		intent.putExtra("right_button", right_button);
		intent.putExtra("refresh_notification", refresh_notification);
		intent.putExtra("isNeedRefresh", need_refresh);
		intent.putExtra("ext_params", ext_params);
		intent.putExtra("bar_color", bar_color);
		intent.putExtra("icon_url", icon_url);
		context.startActivity(intent);
	}

	/**
	 * 如果是育儿的统一在这进入
	 * 
	 * @param context
	 * @param parentingPost
	 */
	public static void starParentingWebViewIntent(Context context,
			ParentingPost parentingPost) {

		Intent intent = new Intent(context, ParentingWebViewActivity.class);
		intent.putExtra("parentingPost", parentingPost);
		context.startActivity(intent);
	}

	/**
	 * 进入育儿列表页面
	 */
	public static void starParentingIntent(Context context) {

		Intent intent = new Intent(context, ParentingPostActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 进入编辑发表话题页面
	 */
	public static void starTopicEditIntent(final Context context) {
		starTopicEditIntent(context, 0, "", null, null);
	}
	public static void starTopicEditIntent(final Context context,
			final int TopicCategory_id, final String TopicCategory_title, final Place place) {
		starTopicEditIntent(context, TopicCategory_id, TopicCategory_title, place,
				null);
	}
	
	/**
	 * 进入编辑发表话题页面 可自带预先设定图片，不可超过九张
	 */
	public static void starTopicEditIntent(final Context context,
			final int TopicCategory_id, final String TopicCategory_title, final Place place,
			final List<String> listData) {
		new StarEdit() {
			
			@Override
			public void todo() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, TopicEditActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("TopicCategory_title", TopicCategory_title);
				bundle.putInt("TopicCategory_id", TopicCategory_id);
				bundle.putSerializable("place", place);
				bundle.putSerializable("listData", (Serializable) listData);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		}.StarTopicEdit((Activity) context);
	}

	/**
	 * 根据 话题title查看该话题所有列表
	 */
	public static void openTopicTitleList(Context context,
			TopicCategory topicCategory) {
		openTopicTitleList(context, topicCategory, false);
	}

	/**
	 * **
	 * 
	 * @param context
	 * @param topicCategory
	 * @param needAddImage
	 *            如果需要加入图片进入编辑帖子，必须传topicCategory进来
	 */

	public static void openTopicTitleList(Context context,
			TopicCategory topicCategory, boolean needAddImage) {
		openTopicTitleList(context, topicCategory, needAddImage, topicCategory.getCategoryId(), topicCategory.getTitle(),false);
	}

	public static void openTopicTitleList(Context context,
										  TopicCategory topicCategory, boolean needAddImage,boolean needPerformClick) {
		openTopicTitleList(context, topicCategory, needAddImage, topicCategory.getCategoryId(), topicCategory.getTitle(),needPerformClick);
	}

	public static void openTopicTitleList(Context context, int categoryId,
										  String title) {
		openTopicTitleList(context, null, false, categoryId,title,false);
	}
	/**
	 *
	 * @param context
	 * @param topicCategory
	 * @param needAddImage
	 * @param categoryId
	 * @param title
	 */
	public static List<Integer> TopicIDList=new ArrayList<Integer>();//统一管理开启的topic LIST PAGE
	public static boolean checkHasTopicById(int categoryId){
		boolean hasThisId=false;
		for (int i=0;i<TopicIDList.size();i++){
			if (TopicIDList.get(i)==categoryId){
				hasThisId=true;
				break;
			}
		}
		return hasThisId;
	}
	public static void cutTopicById(int categoryId){
		if (TopicIDList.isEmpty())
			return;
		for (int i=0;i<TopicIDList.size();i++){
			if (TopicIDList.get(i)==categoryId){
				TopicIDList.remove(i);
				break;//切记，一次只能删一个
			}
		}
	}

	public static void openTopicTitleList(Context context, TopicCategory topicCategory,
										  boolean needAddImage,int categoryId,String title ,boolean needPerformClick) {
		int intentFlags=Intent.FLAG_ACTIVITY_CLEAR_TOP;
		if (checkHasTopicById(categoryId)) {
			intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
			TopicIDList.clear();
		}else
			intentFlags=Intent.FLAG_ACTIVITY_NEW_TASK;
		TopicIDList.add(categoryId);
		Intent intent=new Intent(context, TopicListActivity.class);
		intent.setFlags(intentFlags);
		intent.putExtra("TopicCategory_id", categoryId);
		intent.putExtra("TopicCategory_title", title);
		intent.putExtra("topicCategory", topicCategory);
		intent.putExtra("needAddImage", needAddImage);
		intent.putExtra("needPerformClick", needPerformClick);
		context.startActivity(intent);
	}

	/**
	 * 打开地点话题列表
	 */
	public static void openPlaceList(Context context, Place place) {
		Intent intent = new Intent(context, PlaceListActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.putExtra("place", place);
		context.startActivity(intent);
	}

	/**
	 * 打开地点话题列表
	 */
	public static void openPlaceList(Context context, int placeId) {
		Intent intent = new Intent(context, PlaceListActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.putExtra("placeId", placeId);
		context.startActivity(intent);
	}
	/**
	 * 打开好友动态
	 */
	public static void openFriendListActivity(Context context) {
		openFriendListActivity(context, 0, 0, false);
	}
	/**
	 * 打开个人动态
	 */
	public static void openPersionListActivity(Context context, int userId) {
		openFriendListActivity(context, 1, userId, false);
	}

	/**
	 * 打开热门动态
	 */
	public static void openHotActivityListActivity(Context context, int userId,
			boolean isHot) {
		openFriendListActivity(context, 2, userId, true);
	}
	public static void openFriendListActivity(Context context, int caseRpcIndex,int userId,boolean isHot) {
		Intent intent = new Intent(context, FriendListActivity.class);
		intent.putExtra("userId", userId);
		intent.putExtra("isHot", isHot);
		intent.putExtra("caseRpcIndex", caseRpcIndex);
		context.startActivity(intent);
	}
	/**
	 * 打开关注的地点
	 */
	public static void openFollowPlaceActivity(Context context) {
		context.startActivity(new Intent(context,
				PersonPlaceActivity.class));
	}
	public static void openFollowPlaceActivity(Context context,Place[] places) {
		Intent intent = new Intent(context,
				PersonPlaceActivity.class);
		Bundle bundle = new Bundle();
		ArrayList<Map<String, Object>> item = new ArrayList<>();
		item = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < places.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("item", places[i]);
			item.add(map);
		}
		bundle.putSerializable("item", item);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
	/**
	 * 打开详情,bj15 9 18修改，新增type,默认为0或者填写null
	 */
	public static void openDetailPage(Context context, AbstractMainActivity obj,
			int type) {
		openDetailPage(context, obj, type, false);
	}
	public static void openDetailPage(Context context, int postId) {
		Intent intent = new Intent(context,DetailsActivity.class);
		intent.putExtra("postId", postId);
		Utils.Loge("postid------" + postId);
		context.startActivity(intent);
	}
	public static void openDetailPageByActivityId(Context context, int activityId) {
		Intent intent = new Intent(context,DetailsActivity.class);
		intent.putExtra("activityId", activityId);
		Utils.Loge("activityId------" + activityId);
		context.startActivity(intent);
	}
	// 初始显示键盘
	public static void openDetailPage(Context context, AbstractMainActivity obj,
			int type, boolean visiKeyboard) {
		Intent intent = new Intent(context, DetailsActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		Bundle bundle = new Bundle();
		intent.putExtra("item", obj);
		intent.putExtra("type", type);
		intent.putExtra("visiKeyboard", visiKeyboard);
		intent.putExtras(bundle);
		context.startActivity(intent);
		// intent.setAction(Constants.BroadcastAction.BroadcastAction_Detail_Refush);
		// LocalBroadcastManager.getInstance(MyBaby.getContext()).sendBroadcast(intent);
	}

	/**
	 * 打开消息
	 * @param context
	 */
	public static void starNotificationCategoryActivity(final Context context){
		Intent intent = new Intent(context, NotificationCategoryActivity.class);
		context.startActivity(intent);
	}

	/**
	 * UI线程中工作
	 * 
	 * @author baojun
	 *
	 */
	public abstract static class doSomething {

		private Activity activity;
		public abstract void todo();

		public doSomething(Context context) {
			// TODO Auto-generated constructor stub
			this.activity = (Activity) context;
			try {
				synDo();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public doSomething(Activity activity) {
			// TODO Auto-generated constructor stub
			this.activity = activity;
			try {
				synDo();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void synDo() {
			if (activity==null)
				return;
			if (!activity.isFinishing()) {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							todo();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}

	/**
	 * 关注话题 取消关注话题 判断是否关注话题
	 * 
	 * @author baojun
	 *
	 */
	public abstract static class HasTopicFollow {
		int categoryId;
		Activity activity;

		public HasTopicFollow(int categoryId, Activity activity) {
			this.categoryId = categoryId;
			this.activity = activity;
		}

		public abstract void isFollow(boolean boolValue);

		public abstract void Follow();

		public abstract void unFollow();
		public boolean getHasFollow() {

			TopicCategoryRPC.hasFollow(categoryId, new CallbackForBool() {

				@Override
				public void onSuccess(final boolean boolValue) {
					// TODO Auto-generated method stub
					if (activity==null)
						return;
					// ui线程执行
					new doSomething(activity) {
						@Override
						public void todo() {
							isFollow(boolValue);
						}
					};
				}

				@Override
				public void onFailure(long id, XMLRPCException error) {
					// TODO Auto-generated method stub
					getHasFollow();
				}
			});
			return false;
		}

		/**
		 * 关注该话题 1为成功《=0失败
		 */
		public void toFollow() {

			TopicCategoryRPC.follow(categoryId, new Callback() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					if (activity==null)
						return;
					// ui线程执行
					new doSomething(activity) {
						@Override
						public void todo() {
							Follow();
						}
					};
				}

				@Override
				public void onFailure(long id, XMLRPCException error) {
					// TODO Auto-generated method stub
					// 防止错误
					getHasFollow();
				}
			});

		}

		/**
		 * 取消关注该话题 1为成功《=0失败
		 */
		public void toUnfollow() {
			TopicCategoryRPC.unfollow(categoryId, new Callback() {

				@Override
				public void onSuccess() {
					if (activity==null)
						return;
					new doSomething(activity) {
						@Override
						public void todo() {
							unFollow();
						}
					};
				}

				@Override
				public void onFailure(long id, XMLRPCException error) {

				}
			});
		}
	}

	/**
	 * 所有关注和取消关注集合类 判断是否关注话题
	 * 
	 * @author baojun
	 *
	 */
	public abstract static class PlaceFollow {
		int Placeid;
		Activity activity;

		public PlaceFollow(int Placeid, Activity activity) {
			this.Placeid = Placeid;
			this.activity = activity;
		}

		public abstract void isFollow(boolean boolValue);

		public abstract void Follow();

		public abstract void unFollow();
		public boolean getHasFollow() {

			PlaceRPC.hasFollow(Placeid, new CallbackForBool() {

				@Override
				public void onSuccess(final boolean boolValue) {
					// TODO Auto-generated method stub
					// ui线程执行
					new doSomething(activity) {
						@Override
						public void todo() {
							isFollow(boolValue);
						}
					};
				}

				@Override
				public void onFailure(long id, XMLRPCException error) {
					// TODO Auto-generated method stub
					//getHasFollow();
				}
			});
			return false;
		}

		/**
		 * 关注该话题 1为成功《=0失败
		 */
		public void toFollow() {

			PlaceRPC.follow(Placeid, new Callback() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub

					// ui线程执行
					new doSomething(activity) {
						@Override
						public void todo() {
							Follow();
						}
					};
				}

				@Override
				public void onFailure(long id, XMLRPCException error) {
					// TODO Auto-generated method stub
					// 防止错误
					getHasFollow();
				}
			});

		}

		/**
		 * 取消关注该话题 1为成功《=0失败
		 */
		public void toUnfollow() {
			PlaceRPC.unfollow(Placeid, new Callback() {

				@Override
				public void onSuccess() {
					new doSomething(activity) {
						@Override
						public void todo() {
							unFollow();
						}
					};
				}

				@Override
				public void onFailure(long id, XMLRPCException error) {

				}
			});
		}
	}

	/**
	 * 关注某个用户 取消关注某个用户 判断是否关注该用户
	 * 
	 * @author baojun
	 *
	 */
	public abstract static class HasUserFollow {
		int userId;
		Activity activity1;

		public HasUserFollow(int userid, Activity activity) {
			this.userId = userid;
			this.activity1 = activity;
		}

		
		public abstract void isFollow(boolean boolValue, String data,boolean isBackSuccess);

		public abstract void Follow();

		public abstract void unFollow();

		public void getHasFollow() {
			UserRPC.hasFollow(userId, new CallbackForMap() {

				@Override
				public void onSuccess(final boolean boolValue,
						final String data) {
					// TODO Auto-generated method stub
					// ui线程执行
					new doSomething(activity1) {
						@Override
						public void todo() {
							isFollow(boolValue, data ,true);
						}
					};
				}

				@Override
				public void onFailure(long id, XMLRPCException error) {
					// TODO Auto-generated method stub
					new doSomething(activity1) {
						@Override
						public void todo() {
							isFollow(false, "" ,false);
						}
					};
				}
			});
		}

		/**
		 * 关注该人物 1为成功《=0失败
		 */
		public void toFollow() {

			UserRPC.follow(userId, new Callback() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub

					// ui线程执行
					new doSomething(activity1) {
						@Override
						public void todo() {
							Follow();
						}
					};
				}

				@Override
				public void onFailure(long id, XMLRPCException error) {
					// TODO Auto-generated method stub
					// 防止错误
					//getHasFollow();
				}
			});

		}

		/**
		 * 取消关注该人物 1为成功《=0失败
		 */
		  /*public void toUnfollow() {

			  UserRPC.unfollow(userId, new Callback() {

				  @Override
				  public void onSuccess() {
					  // TODO Auto-generated method stub
					  // ui线程执行
					  activity1.runOnUiThread(new Runnable() {

						  @Override
						  public void run() {
							  unFollow();
						  }
					  });
				  }

				  @Override
				  public void onFailure(long id, XMLRPCException error) {
					  //TODO Auto-generated method stub
					  // 防止错误
					  getHasFollow();
				  }
			  });
		  }*/
	}

	/**
	 * 根据placeid获取place 本人动态的类型本地数据库有place，若他人则没有需要网络获取，现在暂时都获取
	 * 
	 * @category 0915 bj
	 */
	public static abstract class InitPlaceById {
		int id = 0;

		public InitPlaceById(int placeId) {
			id = placeId;
			initPlace();
		}

		public abstract void succBackPlace(Place place);

		public abstract void onError();

		private void initPlace() {

			PlaceRPC.getById(id, new PlaceCallback() {

				@Override
				public void onSuccess(Place place) {
					// TODO Auto-generated method stub
					succBackPlace(place);
				}

				@Override
				public void onFailure(long id, XMLRPCException error) {
					// TODO Auto-generated method stub
				}
			});
		}
	}

}
