package mybaby.share;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.webkit.WebView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.io.Serializable;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.action.Action;
import mybaby.action.QQAction;
import mybaby.action.QzoneAction;
import mybaby.action.ShareMenuAction;
import mybaby.action.WechatAction;
import mybaby.action.WechatFriendsAction;
import mybaby.models.community.ParentingPost;
import mybaby.models.community.activity.AbstractMainActivity;
import mybaby.models.diary.MediaRepository;
import mybaby.models.diary.Post;
import mybaby.util.ImageViewUtil;
import mybaby.util.LogUtils;
import mybaby.util.Utils;

public class UmengShare {

	/**
	 * 设置分享并打开
	 */
	public static class OpenShare {
		// TODO Auto-generated method stub

		/**
		 * 打开默认分享同时设置分享内容
		 * 
		 * @param mController
		 */
		public OpenShare(Activity activity, UMSocialService mController,
				ShareBean shareBean){
			if (null == mController || null == activity) {
				return;
			}
			try {
				new UmengShare().setShareContent(activity, mController,
                        shareBean);
				mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
                        SHARE_MEDIA.DOUBAN, SHARE_MEDIA.TENCENT, SHARE_MEDIA.SINA);// 暂不加入微博
				mController.openShare(activity, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 只是打开默认分享
		 * 
		 * @param mController
		 */
		public OpenShare(Activity activity, UMSocialService mController) {
			if (null == mController || null == activity) {
				return;
			}
			mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
					SHARE_MEDIA.DOUBAN, SHARE_MEDIA.TENCENT, SHARE_MEDIA.SINA);// 暂不加入微博
			mController.openShare(activity, false);
		}

	}

	/**
	 * 直接分享，底层分享接口。如果分享的平台是新浪、腾讯微博、豆瓣、人人，则直接分享，无任何界面弹出； 其它平台分别启动客户端分享</br>
	 */
	public void directShare(final Context context, final WebView webView,
			UMSocialService mController, SHARE_MEDIA share_MEDIA)
			throws Exception {
		if (null == mController) {
			new UmengShare().configPlatforms(
					UMServiceFactory.getUMSocialService(Constants.DESCRIPTOR),
					context);
		}
		mController.directShare(context, share_MEDIA, new SnsPostListener() {

			@Override
			public void onStart() {
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode,
					SocializeEntity entity) {
				String showText = "分享成功";
				if (eCode != StatusCode.ST_CODE_SUCCESSED) {
					showText = "分享失败";
				} else {
					if (webView != null) {
						webView.loadUrl("javascript:mybaby_share_success();");
					}
				}
				Utils.Loge(showText);
				// Toast.makeText(context, showText, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 直接分享，底层分享接口。如果分享的平台是新浪、腾讯微博、豆瓣、人人，则直接分享，无任何界面弹出； 其它平台分别启动客户端分享</br>
	 */
	public void directShare(final Context context, UMSocialService mController,
			SHARE_MEDIA share_MEDIA) throws Exception {
		directShare(context, null, mController, share_MEDIA);
	}

	// 设置网页分享内容
	public static ShareBean setShareBean(ParentingPost parentingPost,
			Context context) {
		return setShareBean(context, parentingPost.getDetailUrl(),
				parentingPost.getExcerpt(),
				parentingPost.getFeaturedImageUrl(), TextUtils.isEmpty(parentingPost.getTitle())?Constants.APP_NAME:parentingPost.getTitle());
	}

	public static ShareBean setShareBean(Serializable serializable,
			Context context) {
		if (serializable instanceof ParentingPost) {
			ParentingPost parentingPost = (ParentingPost) serializable;
			return setShareBean(parentingPost, context);
		}
		if (serializable instanceof Action) {
			Action action = (Action) serializable;
			return setShareBean(action, context);
		}
		return null;

	}

	// 设置action指令的分享内容
	public static ShareBean setShareBean(Action actionBean, Context context) {
		ShareBean shareBean = new ShareBean();
		//下面继承待优化，有空再改
		if (actionBean instanceof QQAction) {
			QQAction action = (QQAction) actionBean;
			shareBean = setShareBean(context, action.getLink(),
					action.getContent(), action.getImageUrl(),
					action.getTitle());
		} else if (actionBean instanceof QzoneAction) {
			QzoneAction action = (QzoneAction) actionBean;
			shareBean = setShareBean(context, action.getLink(),
					action.getContent(), action.getImageUrl(),
					action.getTitle());
		} else if (actionBean instanceof WechatAction) {
			WechatAction action = (WechatAction) actionBean;
			shareBean = setShareBean(context, action.getLink(),
					action.getContent(), action.getImageUrl(),
					action.getTitle());
		} else if (actionBean instanceof WechatFriendsAction) {
			WechatFriendsAction action = (WechatFriendsAction) actionBean;
			shareBean = setShareBean(context, action.getLink(),
					action.getContent(), action.getImageUrl(),
					action.getTitle());
		}else if (actionBean instanceof ShareMenuAction) {
			ShareMenuAction action = (ShareMenuAction) actionBean;
			shareBean = setShareBean(context, action.getLink(),
					action.getContent(), action.getImageUrl(),
					action.getTitle());
		}
		return shareBean;
	}

	// 设置日记分享内容
	public static ShareBean setShareBean(Post post, Context context) {
		String imageurl = "";
		if (MediaRepository.getForPId(post.getId()) != null
				&& MediaRepository.getForPId(post.getId()).length > 0) {
			imageurl = ImageViewUtil.getFileUrl(
					MediaRepository.getForPId(post.getId())[0], 50, 50);
		}
		return setShareBean(context, Constants.APP_NAME, post.getDescription(), imageurl,
				DateFormat.getDateFormat(context).format(post.getDateCreated()));
	}

	// 设置社区分享内容
	public static ShareBean setShareBean(AbstractMainActivity item,
			Context context) {
		if (item==null)
			return setShareBean(context,"","" , "", "");
		String imageurl = "";
		String content="";
		String title=Constants.APP_NAME;
		String tar="";
		if (item.getUser()!=null)
			tar=Constants.MY_BABY_SITE_URL+"/activ-detail?id="+item.getId()+"&user="+item.getUser().getUserId();
		if (item.getImages() != null && item.getImages().length > 0) {
			imageurl = item.getImages()[0].getThumbnailUrl();
		}
		content=TextUtils.isEmpty(item.getContent())?content:item.getContent();
		title=TextUtils.isEmpty(item.getTitle())?title:item.getTitle();
		return setShareBean(context,tar,content , imageurl, title);
	}

	// 设置默认分享内容
	public static ShareBean setShareBean(Context context) {
		return setShareBean(context, "", "", "", "");
	}

	// 设置分享内容
	public static ShareBean setShareBean(Context context, String TargetUrl,
			String Content, String ImageUrl, String Title) {
		String shareTextNoUrl = String
				.format("[%1$s] %2$s",
						Constants.APP_NAME,
						context.getResources()
								.getText(
										R.string.record_and_share_baby_accompanies_your_every_happy_moment));
		String shareText = String.format("%1$s \n%2$s", shareTextNoUrl,
				Constants.MY_BABY_APP_SHARE_URL);
		ShareBean shareBean = new ShareBean();
		shareBean
				.setTargetUrl(TextUtils.isEmpty(TargetUrl) ? Constants.MY_BABY_APP_SHARE_URL
						: TargetUrl);
		shareBean.setContent(TextUtils.isEmpty(Content) ? shareText : Content);
		shareBean
				.setImageUrl(TextUtils.isEmpty(ImageUrl) ? Constants.MY_BABY_APP_ICON_URL
						: ImageUrl);
		shareBean.setTitle(TextUtils.isEmpty(Title) ? "邀请加入『"
				+ Constants.APP_NAME + "』"
				: Title);
		return shareBean;
	}

	/**
	 * 配置分享平台参数</br>
	 */
	public static void configPlatforms(UMSocialService mController,
			final Context context) {
		// 添加新浪SSO授权
		// mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加QQ、QZone平台
		new UmengShare().addQQQZonePlatform((Activity) context);
		// 添加微信、微信朋友圈平台
		new UmengShare().addWXPlatform((Activity) context);
		// 添加短信
		addSMS();
		if (mController.hasShareContent())
			return;
		try {
			mController.registerListener(new SnsPostListener() {

                @Override
                public void onStart() {
                }

                @Override
                public void onComplete(SHARE_MEDIA platform, int eCode,
                        SocializeEntity entity) {
                    String showText = "分享成功";
                    if (eCode != StatusCode.ST_CODE_SUCCESSED) {
                        showText = "分享失败";
                    }
                    // Toast.makeText(context, showText, Toast.LENGTH_SHORT).show();
                    Utils.Loge(showText);
                }
            });
		} catch (SocializeException e) {
			e.printStackTrace();
		}
		// 设置分享的内容
		new UmengShare().setShareContent((Activity) context, mController,
				setShareBean(context));
		LogUtils.e("initUmengShare--->ok");
	}

	/**
	 * 根据不同的平台设置不同的分享内容</br>
	 */
	public void setShareContent(Activity activity, UMSocialService mController,
			ShareBean shareBean) {
		// TODO Auto-generated method stub
		if (shareBean == null) {
			shareBean = setShareBean(activity, "", "", "", "");
		}

		// 配置SSO
		//mController.getConfig().setSsoHandler(new SinaSsoHandler());
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity,
				Constants.TENCENT_APPID, Constants.TENCENT_APPKEY);
		qZoneSsoHandler.addToSocialSDK();
		mController.setShareContent(shareBean.getContent());

		// UMImage localImage = new UMImage(activity,
		// R.drawable.img_init_post_gestational);
		UMImage urlImage = new UMImage(activity, shareBean.getImageUrl());
		// UMImage resImage = new UMImage(activity, R.drawable.icon);

		/*
		 * // 视频分享 UMVideo video = new UMVideo(
		 * "http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html"); //
		 * vedio.setThumb(
		 * "http://www.umeng.com/images/pic/home/social/img-1.png");
		 * video.setTitle("友盟社会化组件视频"); video.setThumb(urlImage);
		 * 
		 * UMusic uMusic = new UMusic(
		 * "http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");
		 * uMusic.setAuthor("umeng"); uMusic.setTitle("天籁之音"); //
		 * uMusic.setThumb(urlImage);
		 * uMusic.setThumb("http://www.umeng.com/images/pic/social/chart_1.png"
		 * );
		 */

		// UMEmoji emoji = new UMEmoji(activity,
		// "http://www.pc6.com/uploadimages/2010214917283624.gif");
		// UMEmoji emoji = new UMEmoji(activity,
		// "/storage/sdcard0/emoji.gif");

		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareContent(shareBean.getContent());
		weixinContent.setTitle(shareBean.getTitle());
		weixinContent.setTargetUrl(shareBean.getTargetUrl());
		weixinContent.setShareMedia(urlImage);
		mController.setShareMedia(weixinContent);

		// 设置朋友圈分享的内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(shareBean.getContent());
		circleMedia.setTitle(shareBean.getContent());
		circleMedia.setShareMedia(urlImage);
		// circleMedia.setShareMedia(uMusic);
		// circleMedia.setShareMedia(video);
		circleMedia.setTargetUrl(shareBean.getTargetUrl());
		mController.setShareMedia(circleMedia);

		// 设置QQ空间分享内容
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent(shareBean.getContent());
		qzone.setTargetUrl(shareBean.getTargetUrl());
		qzone.setTitle(shareBean.getTitle());
		qzone.setShareMedia(urlImage);
		// qzone.setShareMedia(uMusic);
		// qzone.setAppWebSite(shareBean.get);
		mController.setShareMedia(qzone);

		/*
		 * video.setThumb(new UMImage(activity, BitmapFactory.decodeResource(
		 * getResources(), R.drawable.device)));
		 */

		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(shareBean.getContent());
		qqShareContent.setTitle(shareBean.getTitle());
		qqShareContent.setShareMedia(urlImage);
		qqShareContent.setTargetUrl(shareBean.getTargetUrl());
		mController.setShareMedia(qqShareContent);

		/*
		 * // 视频分享 UMVideo umVideo = new UMVideo(
		 * "http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
		 * umVideo.setThumb("http://www.umeng.com/images/pic/home/social/img-1.png"
		 * ); umVideo.setTitle("友盟社会化组件视频");
		 */

	}

	/**
	 * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
	 *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
	 *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
	 *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
	 * @return
	 */
	public void addQQQZonePlatform(Activity activity) {
		String appId = Constants.TENCENT_APPID;
		String appKey = Constants.TENCENT_APPKEY;
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, appId,
				appKey);
		qqSsoHandler.setTargetUrl(Constants.MY_BABY_APP_SHARE_URL);
		qqSsoHandler.addToSocialSDK();

		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity, appId,
				appKey);
		qZoneSsoHandler.addToSocialSDK();
	}

	/**
	 * @功能描述 : 添加微信平台分享
	 * @return 暂未申请 15.09.07 rbj >ok0914
	 */
	public void addWXPlatform(Activity activity) {
		// 注意：在微信授权的时候，必须传递appSecret
		String appId = Constants.WEIXIN_APPID;
		String appSecret = Constants.WEIXIN_APPKEY;
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(activity, appId, appSecret);
		wxHandler.addToSocialSDK();

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(activity, appId,
				appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	/**
	 * 添加短信平台</br>
	 */
	private static void addSMS() {
		// 添加短信
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
	}
}
