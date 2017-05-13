package mybaby.ui.community.holder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.widget.IconTextView;
import com.umeng.analytics.MobclickAgent;

import org.xmlrpc.android.XMLRPCException;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.action.Action;
import mybaby.models.community.Comment;
import mybaby.models.community.Image;
import mybaby.models.community.Link;
import mybaby.models.community.TopicCategory;
import mybaby.models.community.activity.AbstractMainActivity;
import mybaby.models.community.activity.LikeActivity;
import mybaby.models.community.item.CommunityActivityItem;
import mybaby.models.community.item.CommunityHotItem;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.BaseRPC.Callback;
import mybaby.rpc.community.LikeRPC;
import mybaby.rpc.community.ReportRPC.ReportType;
import mybaby.rpc.community.TopicRPC;
import mybaby.share.UmengShare;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.DetailsActivity;
import mybaby.ui.community.ViewReUseFaceListener;
import mybaby.ui.community.customclass.CusCommentLinearLayout;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.customclass.CustomEventDelActivityId;
import mybaby.ui.community.customclass.CustomLinkTextView;
import mybaby.ui.community.customclass.NineGridlayout;
import mybaby.ui.main.HomeFragment;
import mybaby.ui.main.NotificationCategoryFragment;
import mybaby.ui.widget.RoundImageViewByXfermode;
import mybaby.util.DateUtils;
import mybaby.util.DialogShow;
import mybaby.util.DialogShow.DoSomeThingListener;
import mybaby.util.ImageViewUtil;
import mybaby.util.MaterialDialogUtil;
import mybaby.util.ScreenUtils;
import mybaby.util.Utils;
import tyrantgit.explosionfield.ExplosionField;

/**
 * 自定义列表适配器
 * 
 * @author baojun
 *
 */
@SuppressLint("ShowToast")
public class ActivityItem extends  ItemState {

	private int count;
	private View parentView;
	private Holeder vHolder;
	private static StringBuilder builder=new StringBuilder();
	public static StringBuilder getBuilder() {
		if (builder==null)
			builder=new StringBuilder();
		builder.setLength(0);
		return builder;
	}

	@Override
	public void onBindViewHolder(Context context, RecyclerView.ViewHolder vHolder, View parentView, int position, boolean hasVisiPlace, HtmlItem.SetWebViewOnTouchListener listener) {
		if(vHolder instanceof Holeder){
			Holeder holder= (Holeder) vHolder;
			bindDatas((Activity) context,this,false,holder,parentView,position,hasVisiPlace);
		}

	}

	public static RecyclerView.ViewHolder getHolder(ViewGroup parent) {
		View convertView =LayoutInflater.from(parent.getContext())
				.inflate(R.layout.community_all_listitem, parent, false);
		Holeder holder = new Holeder(convertView);
		return holder;
	}


	@Override
	public int getStateType() {
		return ItemState.ActivityHolderTYPE_1;
	}

	@Override
	public View getItemView(View convertView, LayoutInflater inflater, Object data, Activity activity, boolean hasVisiPlace, int position, HtmlItem.SetWebViewOnTouchListener listener) {
		Holeder holder;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.community_all_listitem, null);
			holder = new Holeder(convertView);
			convertView.setTag(R.id.activityitem_mainitem,holder);
		}else{
			holder = (Holeder)convertView.getTag(R.id.activityitem_mainitem);
		}
		bindDatas(activity,data,false,holder,convertView,position,hasVisiPlace);
		return convertView;
	}
	public void bindDatas(Activity activity, Object data,
						  final boolean isDatilPage,final Holeder vHolder, View parentView,int position,boolean hasVisiPlace) {
		AbstractMainActivity mainActivity=null;
		int type_tag=0;
		if (data instanceof CommunityActivityItem) {
			CommunityActivityItem item = (CommunityActivityItem) data;
			mainActivity=item.getActivity();
			type_tag=0;
		}else if (data instanceof CommunityHotItem) {
			CommunityHotItem item = (CommunityHotItem) data;
			mainActivity=item.getActivity();
			type_tag=1;
		}
		if (mainActivity==null)
			return ;
		bindDatas(activity,mainActivity,isDatilPage,type_tag,vHolder,parentView,position,hasVisiPlace);
	}

	public static void setHeadContent(RoundImageViewByXfermode avatarThumbnailUrl,TextView tv_name,String url,String name){
		tv_name.setText(name);
		ImageViewUtil.displayImage(url, avatarThumbnailUrl);
	}

	public void setLikeData(final AbstractMainActivity item,final Context context,boolean isDetail) {
		//vHolder.icon_like.setCompoundDrawablesWithIntrinsicBounds(new IconDrawable(context, FontAwesomeIcons.fa_heart).colorRes(item.isLiked() ? R.color.mainThemeColor : R.color.gray_1).sizePx(Utils.sp2px(context, 13)), null, null, null);
		StringBuilder builder=getBuilder();
		builder.append("{fa-heart ");
		builder.append(item.isLiked()?"@color/mainThemeColor} " : "@color/gray_1} ");
		builder.append(item.getLikeCount() <= 0 ? "赞" : String.valueOf(item.getLikeCount()));
		vHolder.icon_like.setText(builder.toString());

		//vHolder.icon_replay.setCompoundDrawablesWithIntrinsicBounds(new IconDrawable(context,
		//		FontAwesomeIcons.fa_commenting.key()).colorRes(R.color.gray_1).sizePx(Utils.sp2px(context, 13)), null, null, null);
		builder.setLength(0);
		builder.append("{fa-commenting @color/gray_1} ");
		builder.append(item.getReplyCount() <= 0 ? "评论" : String.valueOf(item.getReplyCount()));
		vHolder.icon_replay.setText(builder.toString());

		builder.setLength(0);

		vHolder.icon_share.setVisibility(isDetail?View.VISIBLE:View.GONE);
	}
	@SuppressLint("NewApi")
	public void bindDatas(final Activity activity, final AbstractMainActivity item,
						  final boolean isDatilPage, final int type,final Holeder vHolder, View parentView,int position,boolean hasVisiPlace) {
		this.vHolder = vHolder;
		this.parentView = parentView;

/*ColorDrawable cDrawable=new ColorDrawable(activity.getResources().getColor(R.color.bg_gray));
		vHolder.avatarThumbnailUrl.setImageDrawable(cDrawable);*/
		vHolder.edit_menu_tag.setTypeface(MyBabyApp.fontAwesome);
		vHolder.tv_placePic.setTypeface(MyBabyApp.fontAwesome);

		OnClickSet onClickSet=new OnClickSet(item,activity,isDatilPage);

		int categoryId = 0;
		String title = "", content = item.getContent();
		if (item.getCategory() != null) {
			if (!TextUtils.isEmpty(item.getCategory().getTitle())) {
				title = item.getCategory().getTitle();
				categoryId = item.getCategory().getCategoryId();
			}
		}
		vHolder.tv_content.setVisibility(TextUtils.isEmpty(content) ? (TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE) : View.VISIBLE);
		getSpecilText(activity, vHolder.tv_content, categoryId, title, content, item.getTitle(), isDatilPage,item.getCategory());

		vHolder.tv_content.post(new Runnable() {
			@Override
			public void run() {
				if (vHolder.tv_content.getLineCount() > 5) {
					vHolder.tv_content
							.setMaxLines(isDatilPage ? Integer.MAX_VALUE : 5);
					vHolder.tv_content.invalidate();
					vHolder.tv_quanwen.setVisibility(isDatilPage ? View.GONE
							: View.VISIBLE);
				} else {
					vHolder.tv_quanwen.setVisibility(isDatilPage ? View.GONE
							: View.GONE);
				}
			}
		});
		
		
		if (item.getUser() != null) {
			String name=TextUtils.isEmpty(item.getUser().getName())?"":item.getUser().getName();
			setHeadContent(vHolder.avatarThumbnailUrl,vHolder.tv_name,item.getUser().getAvatarThumbnailUrl(),name);
		}else vHolder.tv_name.setText("");

		//vHolder.tv_source.setText(item.getSource());
		// vHolder.tv_time.setText(DateUtils.datetime2DisplayString(item.getDatetime()));
		vHolder.tv_time.setText(DateUtils.showDate(item.getDatetime()));
		String desc=TextUtils.isEmpty(item.getActionDesc())?"":item.getActionDesc();
		vHolder.tv_cometype.setText(desc);

		final int width = (ScreenUtils.getScreenWidth(activity) - ScreenUtils.dip2px(
				activity, 10*2+15));// 45的头像+10的右margin+10*2的pading+右移25
		AddImage(!isDatilPage, item.getImages(), width);// 图片间隔:3*3个*2边//放在线程会闪烁


		count = item.getLikeCount();

		if (item.getLink()!=null) {
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) vHolder.link_rela.getLayoutParams();
			layoutParams.width = width;
			layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
			layoutParams.topMargin=MyBabyApp.dip2px(12);
			vHolder.link_rela.requestLayout();
		}
		madeLinksLin(activity,item.getLink(),vHolder.link_rela,vHolder.link_image,vHolder.link_desc);
		setLikeData(item, activity, isDatilPage);

		if (!hasVisiPlace) {
			vHolder.place_view.setVisibility(View.GONE);
		}else if (item.getPlace() != null) {
			vHolder.place_view.setVisibility(View.VISIBLE);
			vHolder.tv_placeName.setText(item.getPlace().getPlace_name());
		} else {
			vHolder.place_view.setVisibility(View.GONE);
		}

		if (item.getComments()==null){
			vHolder.activity_comments_rela.setVisibility(View.GONE);
		}else {
			vHolder.activity_comments_rela.setVisibility(View.GONE);
			if (!isDatilPage) {
				vHolder.activity_comments_rela.setVisibility(View.VISIBLE);
				CusCommentLinearLayout.setLinGreat(activity, vHolder.activity_comments_lin, item.getComments(), new ViewReUseFaceListener() {
					@Override
					public int backViewRes() {
						return 0;
					}

					@Override
					public View backView() {
						return new CustomLinkTextView(activity);
					}

					@Override
					public void justItemToDo(Object data, View itemView, int position) {
						CustomLinkTextView textView = (CustomLinkTextView) itemView;
						textView.setIncludeFontPadding(false);//去除默认的padding
						//textView.setTextSize(Utils.sp2px(activity, 13));
						final Comment comment = (Comment) data;
						LinearLayout.LayoutParams lps = (LinearLayout.LayoutParams) textView.getLayoutParams();
						lps.topMargin = MyBabyApp.dip2px(8);
						textView.requestLayout();
						getSpecilText(activity, textView, comment.getCommentUser().getName(), ": " + comment.getCommentContent(), null, isDatilPage, new OnClickListener() {
							@Override
							public void onClick(View v) {
								CustomAbsClass.starUserPage(activity, comment.getCommentUser());
							}
						});
					}
				});
			}
		}


		vHolder.footer_line.setVisibility(View.GONE);
		vHolder.driver_lin.setVisibility(isDatilPage?View.GONE:View.VISIBLE);
		vHolder.hot_lin.setVisibility(type == 0 ? View.GONE : View.VISIBLE);
		vHolder.hot_morelin.setVisibility(type == 0 ? View.GONE : View.VISIBLE);
		// 事件点击

		if (type == 0) {
			vHolder.itemView.bringToFront();
			vHolder.itemView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					ClipboardManager cmb = (ClipboardManager) activity
							.getSystemService(Context.CLIPBOARD_SERVICE);
					if (cmb!=null&&!TextUtils.isEmpty(vHolder.tv_content.getText().toString()))
						cmb.setText(vHolder.tv_content.getText().toString());
					return true;
				}
			});
			vHolder.itemView.setOnClickListener(onClickSet);
			vHolder.tv_placeName.setOnClickListener(onClickSet);
			vHolder.share_focus.setOnClickListener(onClickSet);
			vHolder.avatarThumbnailUrl.setOnClickListener(onClickSet);
			vHolder.tv_name.setOnClickListener(onClickSet);
			vHolder.icon_replay.setOnClickListener(onClickSet);
			vHolder.icon_share.setOnClickListener(onClickSet);
			vHolder.icon_like.setOnClickListener(onClickSet);
			vHolder.report_text.setOnClickListener(onClickSet);
			vHolder.activity_comments_rela.setOnClickListener(onClickSet);
			vHolder.item_grayView.setOnClickListener(null);
			vHolder.main_item.setOnClickListener(null);
		} else {
			vHolder.item_grayView.bringToFront();
			vHolder.item_grayView.setOnClickListener(onClickSet);
			vHolder.main_item.setOnClickListener(onClickSet);
		}
	}

	public static void madeLinksLin(final Activity activity,final Link link,View link_rela,ImageView link_image,TextView link_desc){
		link_rela.setVisibility(View.GONE);
		if (link==null)
			return ;
		link_rela.setVisibility(View.VISIBLE);
		ImageViewUtil.displayImage(link.getImage_url(),link_image);
		link_desc.setText(link.getDesc());
		link_rela.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					Action.createAction(link.getAction(), link.getDesc(), null).excute(activity, NotificationCategoryFragment.newsController, null, null);
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.e("action执行异常");
				}
			}
		});
	}



	public static class Holeder extends RecyclerView.ViewHolder{
		public CustomLinkTextView tv_content;//tv_source
		public RoundImageViewByXfermode avatarThumbnailUrl;
		public RelativeLayout itemView,item_grayView,place_view,like_view,image_rela,link_rela,activity_comments_rela;
		public LinearLayout hot_lin,hot_morelin,main_item,driver_lin,image_icon_lin,activity_comments_lin;
		public ImageView link_image;
		public NineGridlayout iv_ngrid_layout;
		public TextView tv_name,tv_placeName,tv_placePic,share_focus,tv_quanwen,edit_menu_tag,image_icon,image_number,link_desc,tv_cometype,tv_time,report_text;
		public IconTextView icon_share,icon_like,icon_replay,activity_comment_tag;
		public View footer_line;

		public Holeder(View view) {
			super(view);

			itemView = (RelativeLayout) view.findViewById(R.id.item);
			item_grayView = (RelativeLayout) view.findViewById(R.id.item_grayview);
			driver_lin = (LinearLayout) view.findViewById(R.id.driver);
			main_item = (LinearLayout) view.findViewById(R.id.mainitem);
			footer_line= view.findViewById(R.id.footer_line);

			like_view= (RelativeLayout) view.findViewById(R.id.like_rela);


			edit_menu_tag=(TextView) view.findViewById(R.id.edit_menu_tag);
			avatarThumbnailUrl =(RoundImageViewByXfermode) view.findViewById(R.id.avatarThumbnailUrl);
			tv_cometype=(TextView) view.findViewById(R.id.cometype);
			tv_time = (TextView) view.findViewById(R.id.time);
			tv_name= (TextView) view.findViewById(R.id.name);

			hot_lin = (LinearLayout) view.findViewById(R.id.hotlinear);
			hot_morelin = (LinearLayout) view.findViewById(R.id.hot_morelinear);


			//tv_titlename= (CustomLinkTextView) view.findViewById(R.id.topic_title);
			//tv_source= (CustomLinkTextView) view.findViewById(R.id.source);
			tv_content = (CustomLinkTextView) view.findViewById(R.id.tv_content);
			tv_placeName = (TextView) view.findViewById(R.id.placename);
			tv_placePic = (TextView) view.findViewById(R.id.place_pic);
			place_view = (RelativeLayout) view.findViewById(R.id.place_rl);
			share_focus = (TextView) view.findViewById(R.id.share_focus);
			tv_quanwen = (TextView) view.findViewById(R.id.quanwen);

			//
			image_rela=(RelativeLayout) view.findViewById(R.id.image_rela);
			image_icon_lin=(LinearLayout) view.findViewById(R.id.image_icon);
			image_icon= (TextView) view.findViewById(R.id.image_text);
			image_number= (TextView) view.findViewById(R.id.image_number);
			iv_ngrid_layout=(NineGridlayout) view.findViewById(R.id.iv_ngrid_layout);

			report_text=(TextView) view.findViewById(R.id.report_text);
			icon_share=(IconTextView) view.findViewById(R.id.icon_share);
			icon_like=(IconTextView) view.findViewById(R.id.icon_like);
			icon_replay=(IconTextView) view.findViewById(R.id.icon_replay);

			link_rela=(RelativeLayout) view.findViewById(R.id.link_item);
			link_image=(ImageView) view.findViewById(R.id.link_image);
			link_desc=(TextView) view.findViewById(R.id.link_desc);

			activity_comments_rela=(RelativeLayout) view.findViewById(R.id.activity_comments_rela);
			activity_comments_lin= (LinearLayout) view.findViewById(R.id.activity_comments_lin);
			activity_comment_tag= (IconTextView) view.findViewById(R.id.activity_comment_tag);
		}
	}



	private class OnClickSet implements OnClickListener{
		private AbstractMainActivity item;
		private Activity activity;
		private boolean isDatilPage;

		public OnClickSet(AbstractMainActivity item, Activity activity, boolean isDatilPage) {
			this.item = item;
			this.activity = activity;
			this.isDatilPage = isDatilPage;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.item_grayview:
					CustomAbsClass.openHotActivityListActivity(activity, 0, true);
					break;
				case R.id.mainitem:
					CustomAbsClass.openHotActivityListActivity(activity, 0, true);
					break;
				case R.id.avatarThumbnailUrl:
					CustomAbsClass.starUserPage(activity, item.getUser());
					break;

				case R.id.name:
					CustomAbsClass.starUserPage(activity, item.getUser());
					break;
				case R.id.placename:
					CustomAbsClass.openPlaceList(activity, item.getPlace());
					break;
				case R.id.share_focus:
					try {
						showEditDialog(item,activity,isDatilPage);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case R.id.item:
					if (isDatilPage) {
						new CustomAbsClass.StarEdit() {

							@Override
							public void todo() {
								// TODO Auto-generated method stub
								DetailsActivity.openText(DetailsActivity.editReply,
										DetailsActivity.comment_tag, activity, 1, true);
							}
						}.StarTopicEdit(activity);
					} else {
						// 统计点击次数
						MobclickAgent.onEvent(activity, "12");// 统计发送的个数
						CustomAbsClass.openDetailPage(activity, item, 0);
					}

					break;
				case R.id.icon_replay:
					// 统计点击次数
					MobclickAgent.onEvent(activity, "14");// 统计发送的个数
					new CustomAbsClass.StarEdit() {

						@Override
						public void todo() {
							// TODO Auto-generated method stub
							if (isDatilPage) {
								DetailsActivity.openText(DetailsActivity.editReply,
										DetailsActivity.comment_tag, activity, 1, true);
							} else {
								CustomAbsClass.openDetailPage(activity, item, 0, true);
							}
						}
					}.StarTopicEdit(activity);
					break;
				case R.id.report_text:
					new DialogShow(activity).showReportDialog(ReportType.Activity,
							item.getId(), item.getUser().getUserId());
					break;
				case R.id.icon_share:
					new UmengShare.OpenShare(activity,
							HomeFragment.homeController,
							UmengShare.setShareBean(item, activity));
					break;
				case R.id.activity_comments_rela:
					if (!isDatilPage)
						CustomAbsClass.openDetailPage(activity, item, 0);
					break;
				case R.id.icon_like:
					// 统计点击次数
					MobclickAgent.onEvent(activity, "13");// 统计发送的个数

					new CustomAbsClass.doSomething(activity) {
						@Override
						public void todo() {
							// TODO Auto-generated method stub
							// like.setTextColor(context.getResources().getColor(
							// item.isLiked() ? R.color.white : R.color.red));
							if (item.isLiked()) {
								if (count <= 0) {
									count = 0;
								} else {
									count -= 1;
								}
								LikeRPC.unlike(item.getPostId(), null);
							} else {
								count += 1;
								LikeRPC.like(item.getPostId(),null);
							}
							item.setLiked(item.isLiked() ? false : true);
							item.setLikeCount(count);

							StringBuilder builder=getBuilder();
							builder.append("{fa-heart ");
							builder.append(item.isLiked()?"@color/mainThemeColor} " : "@color/gray_1} ");
							builder.append(count < 1 ? "赞" : count);
							vHolder.icon_like.setText(builder.toString());
							builder.setLength(0);
							//vHolder.icon_like.setCompoundDrawablesWithIntrinsicBounds(new IconDrawable(activity, FontAwesomeIcons.fa_heart.key())
							//		.colorRes(item.isLiked() ? R.color.gray_1:R.color.mainThemeColor).sizePx(Utils.sp2px(activity, 13)), null, null, null);
							//vHolder.icon_like.setText(" "+(count<1?"赞":count));
							if (isDatilPage) {
								LikeActivity likeActivity = new LikeActivity();
								likeActivity.setDatetime_gmt(DateUtils.localTime2GMTTime(System.currentTimeMillis()));
								likeActivity.setUser(MyBabyApp.currentUser);
								Bundle bundle=new Bundle();
								bundle.putSerializable("likeActivity", likeActivity);
								MyBabyApp.sendLocalBroadCast(item.isLiked() ?Constants.BroadcastAction.BroadcastAction_Detail_Like_Add
										:  Constants.BroadcastAction.BroadcastAction_Detail_Like_Remove, bundle);
							}
						}
					};
					break;
			}
		}
	}

	/**
	 * 返回地图小背景
	 */
	private String getMapBg(boolean isSmall) {
		// TODO Auto-generated method stub
		return isSmall?"assets://map_bg_small.png":"assets://map_bg.png";
	}
	
	/**
	 * 举报、删除、分享、不感兴趣 话题
	 * 
	 * @param isDeatilPage
	 *            是否需要关掉详情页面的标签
	 */
	public void showEditDialog(final AbstractMainActivity item,final Activity activity,final boolean isDeatilPage) throws Exception{
		final boolean isMe=item.getUser().isSelf();
		final boolean isadmin=MyBabyApp.currentUser.isAdmin();
		final String[] items;
		if (isMe){
			items =isadmin?new String[] { "分享", "删除","置顶","取消置顶","设置推荐","取消推荐" }:new String[] { "分享", "删除"};
		}else {
			items =isadmin?new String[] { "分享", "删除","置顶","取消置顶","设置推荐","取消推荐" }:new String[] { "分享", "不感兴趣", "举报"};
		}
		final BaseRPC.CallbackForBool callbackForBool=new BaseRPC.CallbackForBool() {
			@Override
			public void onSuccess(boolean boolValue) {
				if (boolValue) {
					//MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_CommunityMain_Refush);
					new CustomAbsClass.doSomething(activity) {
						@Override
						public void todo() {
							Toast.makeText(activity,"成功",Toast.LENGTH_SHORT).show();
						}
					};
				}
				else {
					new CustomAbsClass.doSomething(activity) {
						@Override
						public void todo() {
							Toast.makeText(activity,"失败",Toast.LENGTH_SHORT).show();
						}
					};
				}
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				new CustomAbsClass.doSomething(activity) {
					@Override
					public void todo() {
						Toast.makeText(activity,"error",Toast.LENGTH_SHORT).show();
					}
				};
			}
		};

		MaterialDialogUtil.showListDialog(activity, null, items, new MaterialDialogUtil.DialogListListener() {
			@Override
			public void todosomething(int which) {

				switch (items[which]) {
					case "分享":
						new UmengShare.OpenShare(activity, HomeFragment.homeController,UmengShare.setShareBean(item, activity));
						break;
					case "举报":
						new DialogShow(activity).showReportDialog(ReportType.Activity,
								item.getId(), item.getUser().getUserId());
						break;
					case "置顶":
						TopicRPC.top(item.getId(), callbackForBool);
						break;
					case "取消置顶":
						TopicRPC.cancle_top(item.getId(), callbackForBool);
						break;
					case "设置推荐":
						TopicRPC.essence(item.getId(), callbackForBool);
						break;
					case "取消推荐":
						TopicRPC.cancle_essence(item.getId(), callbackForBool);
						break;
					case "不感兴趣":
						new DialogShow.DisLikeDialog(activity,
								"确认不感兴趣则不会再看到该用户的所有信息", "确定", "取消",
								new DoSomeThingListener() {

									@Override
									public void todo() {
										LogUtil.e("准备删除的activityid:" + item.getId());
										pullBlackTopic(item.getUser().getUserId(), item.getId(), isDeatilPage, activity);
									}
								}, null);
						break;
					case "删除":
						if (!item.isCan_delete()&&!isadmin) {
							Toast.makeText(activity, R.string.disde, Toast.LENGTH_SHORT)
									.show();
						} else {
							new DialogShow.DisLikeDialog(activity,
									"确定删除吗？", "确定", "取消",
									new DoSomeThingListener() {

										@Override
										public void todo() {
											LogUtil.e("准备删除的activityid:" + item.getId());
											ExplosionField.attach2Window(activity).explode(parentView);
											delectTopic(item.getPostId(),
													activity);
											Bundle bundle = new Bundle();
											bundle.putSerializable("CustomEventDelActivityId", new CustomEventDelActivityId(item.getId(), isDeatilPage,activity));
											MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_Topic_Delete, bundle);
										}
									}, null);
						}
						break;
				}
			}
		});
	}

	/**
	 * 删除话题
	 */
	private static void delectTopic(int topicId, final Context context) {
		TopicRPC.delete(topicId, new Callback() {
			//
			@SuppressLint("ShowToast")
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething((Activity) context) {
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
					}
				};
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething((Activity) context) {
					@SuppressLint("ShowToast")
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
					}
				};
			}
		});
	}

	/**
	 * 加不感兴趣话题
	 */
	public static void pullBlackTopic(int userid, final int activityId,
			final Boolean isDeatilPage, final Context context) {
		TopicRPC.pullBlack(userid, new Callback() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething(context) {
					@SuppressLint("ShowToast")
					@Override
					public void todo() {
						// TODO Auto-generated method stub、、//不感兴趣
						Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
					}
				};
				Bundle bundle = new Bundle();
				bundle.putSerializable("CustomEventDelActivityId", new CustomEventDelActivityId(activityId, isDeatilPage, (Activity) context));
				MyBabyApp.sendLocalBroadCast(Constants.BroadcastAction.BroadcastAction_Topic_Delete, bundle);
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething(context) {
					@SuppressLint("ShowToast")
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						Toast.makeText(context, "失败", Toast.LENGTH_SHORT).show();
					}
				};
			}
		});
	}


	/*延迟隐藏
	Handler handlerLess = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			popWindow.dismiss();
		}
	};
	handlerLess.sendEmptyMessageDelayed(0, 130);*/
	/**
	 * 内容中的文字可点击的TEXTVIEW
	 */
	public static void getSpecilText(final Context context, final TextView textView,
									 final int parentId, final String title, final String content,
									 final String outlineText, final boolean isDatilPage, final TopicCategory category) {
		getSpecilText(context, textView, title, content,outlineText,isDatilPage, category==null?null:new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (parentId!=0) {
					CustomAbsClass.openTopicTitleList(context, category);
				}
			}
		});
	}

	/**
	 *
	 * @param context
	 * @param textView
	 * @param title  //大标题
	 * @param content 内容
	 * @param outlineText  小标题
	 * @param isDatilPage 是否详情
	 * @param titleListener 点击事件·1
	 */
	public static void getSpecilText(
			final Context context, final TextView textView,final String title,
			final String content,final String outlineText, final boolean isDatilPage,
			View.OnClickListener titleListener) {

		final Clickable clickTitle = new Clickable(titleListener, context,context.getResources().getColor(R.color.blue));

		// 是否添加#
		boolean hastitle = TextUtils.isEmpty(title) ? false : true;
		boolean hasOutlineText = TextUtils.isEmpty(outlineText) ? false : true;
		String title1 = hastitle ? title: "";
		String outlineTextE =hasOutlineText?((hastitle?" ":"")+"【"+outlineText+"】"):"";
		String contentE=title1+outlineTextE+(hastitle?(hasOutlineText?"":" "):(hasOutlineText?" ":""))+content;
		SpannableString sp = new SpannableString(contentE);
		if (hastitle) {
			//String sp1=ToDBC(title1 + contentE);
			sp.setSpan(clickTitle, 0, title1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if (hasOutlineText&&(!isDatilPage)){
			sp.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.community_content_gray)),
					contentE.length()-content.length(),
					contentE.length(),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		}
		textView.setText(sp);
		textView.setMovementMethod(CustomLinkTextView.LocalLinkMovementMethod.getInstance());
		//textView.setMovementMethod(LinkMovementMethod.getInstance());

		
		// 设置超链接
		// sp.setSpan(new URLSpan(communityData.getItem().getContent()), 5, 7,
		// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 设置高亮样式一
		// sp.setSpan(new BackgroundColorSpan(Color.RED), 17
		// ,19,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// 设置高亮样式二
		/*
		 * sp.setSpan(new ForegroundColorSpan(Color.BLUE),
		 * communityData.getItem().getContent().length(),
		 * communityData.getItem().getContent().length()+2,
		 * Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		 */
		// sp.setSpan(new Clickable(l), start, end,
		// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 设置斜体
		// sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 27,
		// 29, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		// SpannableString对象设置给TextView
	}
	
	
	public static String ToDBC(String input) {//半角字符转全角字符
		   char[] c = input.toCharArray();
		   for (int i = 0; i< c.length; i++) {
		       if (c[i] == 12288) {
		         c[i] = (char) 32;
		         continue;
		       }if (c[i]> 65280&& c[i]< 65375)
		          c[i] = (char) (c[i] - 65248);
		       }
		   return new String(c);
		}
	


	/**
	 * 返回三张count=3 如不固定，则全部显示，count不=3就OK
	 * 
	 * @param images
	 * 图片间距
	 */

	@SuppressWarnings({ "deprecation" })
	@SuppressLint("NewApi")
	public void AddImage(boolean isCutCount,final Image[] images, int countWidth) {

		if (images==null) {
			//vHolder.images_lin_Stub.isInLayout()setVisibility(View.GONE);
			vHolder.image_rela.setVisibility(View.GONE);
			return;
		}
		if (images.length==0) {
			//vHolder.images_lin_Stub.setVisibility(View.GONE);
			vHolder.image_rela.setVisibility(View.GONE);
			return;
		}
		vHolder.image_rela.setVisibility(View.VISIBLE);
		/*final int iamgePadding = 5;
		int height = 0;
		int count=images.length;
		final int imageWidth = (countWidth - iamgePadding * iamgePadding * 2) / 3;
		if (count <= 3) {
			height = countWidth - imageWidth * 2 - iamgePadding * 0;
		} else if (count <= 6&&count > 3) {
			height = countWidth - imageWidth * 1 - iamgePadding * 1;
		} else if (count <= 9&& count> 6) {
			height = countWidth - imageWidth * 0 - iamgePadding * 2 ;
		}
		LinearLayout.LayoutParams layoutParams = getLayoutParams();
		layoutParams.width=countWidth;
		layoutParams.height=LinearLayout.LayoutParams.WRAP_CONTENT;
		//layoutParams.height=(isDatilPage ? height : imageWidth + iamgePadding * 2 * 1);
		layoutParams.setMargins(0, ScreenUtils.dip2px(activity, vHolder.tv_content.getVisibility()==View.VISIBLE?5:12), 0, 0);
		vHolder.image_rela.setLayoutParams(layoutParams);*/

		ArrayList<Image> itemList=new ArrayList<>();
		List<Image> itemListAll=Arrays.asList(images);
		if (images!=null) {
			for (int i = 0; i < images.length; i++) {
				itemList.add(images[i]);
				if (isCutCount)
					if (i == 2)
						break;
			}
		}
		vHolder.image_icon_lin.setVisibility(isCutCount?(itemListAll.size()>3?View.VISIBLE:View.GONE):View.GONE);
		vHolder.image_number.setText(String.valueOf(itemListAll.size()));
		vHolder.iv_ngrid_layout.setVisibility(itemList.size() == 0 ? View.GONE : View.VISIBLE);
		if (itemList.size()>0)
			vHolder.iv_ngrid_layout.setImagesData(itemList, itemListAll, countWidth);
	}

	/**
	 * 返回单张背景图片控件
	 * 
	 * @param context
	 * @param imageView
	 * @param url
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void backGroundHttpImage(final Context context,
			final ImageView imageView, final String url, final int width, final int paddingWidth) {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				width, width);
		layoutParams.setMargins(paddingWidth, paddingWidth, paddingWidth,
				paddingWidth);// 4个参数按顺序分别是左上右下
		layoutParams.weight=1;
		imageView.setLayoutParams(layoutParams);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		if (!TextUtils.isEmpty(url)) {
			imageView.setBackgroundColor(context.getResources().getColor(
				R.color.bg_gray));
			ImageViewUtil.displayImage(url, imageView);
			/*Picasso.with(context).
			load(url)
			.resize(width*3,width*3)
			.centerCrop()
			//.skipMemoryCache()//跳过内存
			.placeholder(new ColorDrawable(Color.parseColor("#f5f5f5")))
			.config(Bitmap.Config.RGB_565)
			.error(context.getResources().getDrawable(R.drawable.ic_action_picture))
			.into(imageView);*/
		}
	}


	/**
	 * 处理图片
	 * 
	 * @param bm
	 *            所要转换的bitmap
	 * @ newWidth新的宽
	 * @ newHeight新的高
	 * @return 指定宽高的bitmap
	 */
	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		return Bitmap.createBitmap(bm, 0, 0, width, height, matrix,true);
	}

	/**
	 * 
	 * TODO<图片圆角处理>
	 * 
	 * @throw
	 * @return Bitmap
	 * @param srcBitmap
	 *            源图片的bitmap
	 * @param ret
	 *            圆角的度数
	 */
	public static Bitmap getRoundImage(Bitmap srcBitmap, float ret) {

		if (null == srcBitmap) {
			Utils.Loge("the srcBitmap is null");
			return null;
		}

		int bitWidth = srcBitmap.getWidth();
		int bitHight = srcBitmap.getHeight();

		BitmapShader bitmapShader = new BitmapShader(srcBitmap,
				Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(bitmapShader);

		RectF rectf = new RectF(0, 0, bitWidth, bitHight);

		Bitmap outBitmap = Bitmap.createBitmap(bitWidth, bitHight,
				Config.RGB_565);
		Canvas canvas = new Canvas(outBitmap);
		canvas.drawRoundRect(rectf, ret, ret, paint);
		canvas.save();
		canvas.restore();

		return outBitmap;
	}

	public static class Clickable extends ClickableSpan implements OnClickListener {
		private final View.OnClickListener mListener;
		private Context context;
		private int color;

		public Clickable(View.OnClickListener l, Context context,int color) {
			mListener = l;
			this.context = context;
			this.color=color;
		}

		@Override
		public void onClick(View v) {
			if (mListener!=null)
				mListener.onClick(v);
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public void updateDrawState(TextPaint ds) {
			// TODO Auto-generated method stub
			super.updateDrawState(ds);
			ds.setColor(color); // 设置文件颜色
			ds.setUnderlineText(false); // 设置下划线
		}
	}

}
