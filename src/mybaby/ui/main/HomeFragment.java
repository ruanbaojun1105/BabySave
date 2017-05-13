package mybaby.ui.main;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.person.Baby;
import mybaby.models.person.Person;
import mybaby.rpc.community.CommunityItemRPC;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.fragment.LoadMoreListViewFragment;
import mybaby.ui.posts.home.NewPhotos;
import mybaby.ui.posts.person.PersonAvatar;
import mybaby.util.ImageViewUtil;

//社区主页
public class HomeFragment extends HomeBaseFragment{

	private PersonAvatar personAvatar;
	private Baby sBaby;
	private ViewHolder viewHolder=null;
	private Person person;
	private LoadMoreListViewFragment fragment;

	public void onResume() {
		super.onResume();
		initHeadView(null, viewHolder);
	}

	@Override
	protected boolean isSendAsnkRed() {
		return false;
	}

	@Override
	protected boolean isCreatSatus() {
		return true;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void onPause() {
		super.onPause();
	}
	@Override
	protected int getLayoutResId() {
		return R.layout.blank_page;
	}

	@Override
	protected void initView() {
		new InfoReceiver().registerInfoReceiver();
	}

	@Override
	protected String getInitRPC() {
		return CommunityItemRPC.getForMain();
	}

	@Override
	protected String getPageName() {
		return "主页";
	}

	@Override
	protected String getPageTag() {
		return MyBayMainActivity.homeTabTag;
	}

	@Override
	protected String getCacheKey() {
		return Constants.CacheKey_CommunityActivity_Main;
	}

	@Override
	protected int getPtrBgColor() {
		return R.color.mainThemeColor;
	}

	@Override
	protected int getFragmentAtId() {
		return R.id.content_page;
	}

	@Override
	protected LoadMoreListViewFragment getFragment() {
		fragment=new LoadMoreListViewFragment(Constants.CacheKey_CommunityActivity_Main, Constants.CACHE_PAGE_INTERVA,false,initHeadView(null,null)).setIsInViewPage(true);
		return fragment;
	}

	private View initHeadView(View headView,ViewHolder vHolder) {
		if (vHolder==null) {
			if (headView == null) {
				headView = LayoutInflater.from(getActivity()).inflate(
						R.layout.community_main_head_other, null);
				vHolder = new ViewHolder(headView);
				headView.setTag(vHolder);
			} else vHolder = (ViewHolder) headView.getTag();
			viewHolder=vHolder;
		}
		final NewPhotos mNewPhotos= new NewPhotos(getActivity());
		final ArrayList<Uri> uriArrayList=mNewPhotos.getPhotos(NewPhotos.getHomePhotosLastDatetime());
		viewHolder.newphoto_image.setVisibility(uriArrayList.size() > 0 ? View.VISIBLE : View.GONE);
		viewHolder.newphoto_image_bg.setVisibility(uriArrayList.size() > 0 ? View.VISIBLE : View.GONE);
		if (uriArrayList.size()>0){
			if (!viewHolder.newphoto_image.hasOnClickListeners()) {
				viewHolder.newphoto_image.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CustomAbsClass.starHomeTimelineActivity(getActivity());
						viewHolder.newphoto_image.postDelayed(new Runnable() {
							@Override
							public void run() {
								mNewPhotos.addPhotos(uriArrayList.get(0));
							}
						}, 150);
					}
				});
			}
			ImageViewUtil.displayImage(checkUri(uriArrayList.get(0).toString()), viewHolder.newphoto_image);
		}
		viewHolder.community_head_rela.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				CustomAbsClass.starHomeTimelineActivity(getActivity());
			}
		});
		viewHolder.babyspace_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				CustomAbsClass.starHomeTimelineActivity(getActivity());
			}
		});
		if (sBaby==null)
			sBaby=Baby.getSmallBaby();
		if (sBaby!=null) {
			if (personAvatar==null||viewHolder.person_frame.getChildCount()==0) {
				personAvatar = new PersonAvatar(getActivity(), viewHolder.person_frame, null,
						sBaby, false, PersonAvatar.CameraShowType.nullAvatar,MyBabyApp.dip2px(2),R.color.cyan);
				personAvatar.setCameraOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						person= personAvatar.getPerson();
						if (personAvatar.getmCameraImageView().getVisibility()==View.VISIBLE)
							mMediaHelper.launchPictureLibrary(true);
						else
							CustomAbsClass.starHomeTimelineActivity(getActivity());
					}
				});
			}
			viewHolder.persion_name.setText(sBaby.getName());
			viewHolder.persion_age.setText(sBaby.getAgeText(true));
		}
		return headView;
	}

	@NonNull
	@Override
	protected void onMediaFileDoneOver(String[] mediaFilePaths) {
		// 打开日记
		if (Constants.category!=null) {
			CustomAbsClass.starTopicEditIntent(getActivity(), Constants.category.getId(), Constants.category.getTitle(), null, Arrays.asList(mediaFilePaths));
			Constants.category=null;
		}else if (person!=null){
			person.setAvatar(mediaFilePaths[0]);
			personAvatar.setCameraOnClickListener(null);
			//广播
			Intent bi = new Intent();
			bi.setAction(Constants.BroadcastAction.BroadcastAction_Person_Edit);
			bi.putExtra("id", person.getId());
			LocalBroadcastManager.getInstance(MyBabyApp.getContext()).sendBroadcast(bi);
		}
	}

	@Override
	protected void init() {
		if (MyBabyApp.getSharedPreferences().getBoolean("guide_photo"+MyBabyApp.version,false))
			return;
		if (viewHolder!=null){
			viewHolder.babyspace_btn.postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						final Dialog dialog = new Dialog(getActivity(),R.style.guide_dialog);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setContentView(R.layout.image_item);
						dialog.setCancelable(false);
						dialog.setCanceledOnTouchOutside(false);
						dialog.getWindow().setBackgroundDrawableResource(R.drawable.nocolor);
						Display d = getActivity().getWindowManager().getDefaultDisplay(); // 为获取屏幕宽、高
						WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
						p.width = d.getWidth();
						p.height = d.getHeight();
						dialog.getWindow().setAttributes(p);
						ImageView imageView= (ImageView) dialog.findViewById(R.id.image);
						Picasso.with(getContext())
								.load(R.drawable.guidepage)
								.resize(d.getWidth(), d.getHeight())
								.config(Bitmap.Config.RGB_565)
										//.skipMemoryCache()
								.placeholder(new ColorDrawable(Color.parseColor("#77000000")))
								.centerCrop()
								.into(imageView);
						imageView.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								try {
									viewHolder.community_head_rela.performClick();
									dialog.dismiss();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						dialog.show();

						SharedPreferences.Editor edit = MyBabyApp.getSharedPreferences().edit();
						edit.putBoolean("guide_photo"+MyBabyApp.version, true);
						edit.commit();

						try {
							((MyBayMainActivity)getActivity()).getmViewPager().setCurrentItem(MyBayMainActivity.homeTabIndex);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (Resources.NotFoundException e) {
						e.printStackTrace();
					}
				}
			}, 3000);

		}

	}

	private String checkUri(String uri) {
		if(!uri.contains("content://")) {
			uri="file://"+uri;
			//photoUri = Uri.fromFile(new File(photoUri)).toString();
		}
		return uri;
	}

	static class ViewHolder{
		RelativeLayout community_head_rela;
		FrameLayout person_frame;
		TextView persion_name;
		TextView persion_age;
		ImageView babyspace_btn;
		ImageView newphoto_image;
		ImageView newphoto_image_bg;
		public ViewHolder(View headView) {
			community_head_rela= (RelativeLayout) headView.findViewById(R.id.community_head_rela);
			person_frame= (FrameLayout) headView.findViewById(R.id.person_frame);
			persion_name= (TextView) headView.findViewById(R.id.persion_name);
			persion_age= (TextView) headView.findViewById(R.id.persion_age);
			newphoto_image= (ImageView) headView.findViewById(R.id.newphoto_image);
			newphoto_image_bg= (ImageView) headView.findViewById(R.id.newphoto_image_bg);
			babyspace_btn= (ImageView) headView.findViewById(R.id.babyspace_btn);
		}
	}
	// 广播接收并响应处理 地点设置成功返回刷新
	private class InfoReceiver extends BroadcastReceiver {
		public void registerInfoReceiver() {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.BroadcastAction.BroadcastAction_CommunityMain_ImageUpLoading);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_CommunityMain_Refush);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Person_Add);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Person_Edit);
			filter.addAction(Constants.BroadcastAction.BroadcastAction_Person_Delete);

			LocalBroadcastManager.getInstance(MyBabyApp.getContext())
					.registerReceiver(this, filter);
		}

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Person_Add)||
				intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Person_Edit)||
				intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Person_Delete)||
				intent.getAction().equals(Constants.BroadcastAction.BroadcastAction_Post_Home_Open)
					){
				personAvatar = new PersonAvatar(getActivity(), viewHolder.person_frame, null, sBaby, false, PersonAvatar.CameraShowType.nullAvatar,MyBabyApp.dip2px(2),R.color.cyan);
				sBaby=Baby.getSmallBaby();
				initHeadView(null,viewHolder);
			}
		}
	}
}
