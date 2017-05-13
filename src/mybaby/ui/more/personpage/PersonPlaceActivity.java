package mybaby.ui.more.personpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.xmlrpc.android.XMLRPCException;

import com.umeng.analytics.MobclickAgent;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.models.community.Place;
import mybaby.models.community.UserPlaceSetting;
import mybaby.rpc.BaseRPC.Callback;
import mybaby.rpc.UserRPC;
import mybaby.rpc.UserRPC.PlaceSettingListCallback;
import mybaby.rpc.community.PlaceRPC;
import mybaby.ui.broadcast.PostMediaTask;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.more.BaseHolder;
import mybaby.ui.more.PlaceSettingActivity;
import mybaby.ui.more.holder.PlaceSettingHolder;
import mybaby.ui.more.holder.PlaceTopicHolder;
import mybaby.util.ActionBarUtils;
import mybaby.util.DialogShow;
import mybaby.util.DialogShow.PlaceSettingClick;
import mybaby.util.AppUIUtils;
import mybaby.util.DialogShow.DeleteListener;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 所关注地点Activity
 * 
 * @author Administrator
 * 
 */
public class PersonPlaceActivity extends Activity {

	private static final int PLACE_SETTING = 0;
	private static final int PLACE_TOPIC = 1;
	private static final int PLACE_SETTING_TITLE = 2;
	private static final int PLACE_TOPIC_TITLE = 3;

	private ListView iv_place;
	private ProgressBar pb_loading;
	private MyAdapter adapter;
	private ArrayList<Map<String, Object>> item;
	private DialogShow dialogShow = new DialogShow(PersonPlaceActivity.this);
//	private PopupWindow pop;


	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("个人地点列表"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
	}
	
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("个人地点列表"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
											// onPageEnd 在onPause 之前调用,因为
											// onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.obj!=null) {
				pb_loading.setVisibility(View.GONE);
				final UserPlaceSetting[] arrSetting = (UserPlaceSetting[]) msg.obj;
				if (item==null)
					return;
				adapter = new MyAdapter(PersonPlaceActivity.this, item, arrSetting);
				iv_place.setAdapter(adapter);

				iv_place.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						setOnLongClick(position, arrSetting);
						return true;
					}
				});
				
				iv_place.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
						setOnClick(position, arrSetting, view);
					}
				});
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place);
		ActionBarUtils.initActionBar("地点", PersonPlaceActivity.this);
		iv_place = (ListView) findViewById(R.id.iv_place);
		OverScrollDecoratorHelper.setUpOverScroll(iv_place);
		pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
		pb_loading.setVisibility(View.VISIBLE);
		Bundle bundle = getIntent().getExtras();
		if (bundle!=null)
			item = (ArrayList<Map<String, Object>>) bundle.getSerializable("item");
		if (item!=null)
			loadData();
		else {
			if (MyBabyApp.currentUser==null)
				finish();
			PlaceRPC.getPlacesByUserId(MyBabyApp.currentUser.getUserId(), new PlaceRPC.ListCallback() {
				@Override
				public void onSuccess(Place[] arrPlace) {
					item=new ArrayList<Map<String, Object>>();
					for (int i = 0; i < arrPlace.length; i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("item", arrPlace[i]);
						item.add(map);
					}
					loadData();
				}

				@Override
				public void onFailure(long id, XMLRPCException error) {

				}
			});
		}
	}

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.RequestCode.ACTIVITY_REQUEST_CODE_RESETPLACE:
			if (resultCode == Activity.RESULT_OK) {
				pb_loading.setVisibility(View.VISIBLE);
				loadData();
			}
			break;
		}
	}

	private void loadData() {

		UserRPC.getPlaceSetting(new PlaceSettingListCallback() {

			@Override
			public void onSuccess(UserPlaceSetting[] arrSetting) {
				Message msg = new Message();
				msg.obj = arrSetting;
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				runOnUiThread(new Runnable() {
					public void run() {
						pb_loading.setVisibility(View.GONE);
						Toast.makeText(PersonPlaceActivity.this, "网络错误！，请检查网络是否连接", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * 点击事件的响应
	 * @param position  点击的条目对应的位置
	 * @param arrSetting Palce数组
	 * @param view       父View
	 */
	private void setOnClick(int position, UserPlaceSetting[] arrSetting,
			View view) {
		if (position > 0 && position < (arrSetting.length + 1)) {
			dialogShow.showPlaceSettingDialog(arrSetting[position-1],new PlaceSettingClick() {
				
				@Override
				public void delete(UserPlaceSetting placeSetting) {
					showMakeSureDialog("您确定您要删除该地点吗", placeSetting);
				}
				
				@Override
				public void change(UserPlaceSetting placeSetting) {
					Intent intent = new Intent(PersonPlaceActivity.this,
							PlaceSettingActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("CommunityPlaceSettingItem",placeSetting);
					intent.putExtras(bundle);
					startActivityForResult(intent,Constants.RequestCode.ACTIVITY_REQUEST_CODE_RESETPLACE);
				}
			});
		}
		if (position > (arrSetting.length + 1)) {
			Place place = (Place) item.get(position - arrSetting.length - 2).get("item");
			CustomAbsClass.openPlaceList(PersonPlaceActivity.this, place);
		}
	}
	
	public void showMakeSureDialog(String text,final UserPlaceSetting placeSetting){
		new DialogShow.DisLikeDialog(PersonPlaceActivity.this,text, "确定", "取消",
				new DialogShow.DoSomeThingListener() {

					@Override
					public void todo() {
						clearPlaceSetting(placeSetting);
					}
				}, null);
	}

	/**
	 * 长点击事件的响应
	 * @param position  点击的条目对应的位置
	 * @param arrSetting Palce数组
	 */
	private void setOnLongClick(int position, UserPlaceSetting[] arrSetting) {
		if (position > (arrSetting.length + 1)) {
			Place place = (Place) item.get(position - arrSetting.length - 2).get("item");
			// 显示删除的dialog
			dialogShow.showDeleteDialog("取消关注","确定取消关注？",true,place.getPlaceId(),
					new DeleteListener() {

						@Override
						public void delete(int id, DialogInterface dialog) {
							deletePlaceByNet(id, dialog);
						}
					});
		}
	}

	/**
	 * 向服务器发出请求，删除Place对应的话题
	 * @param placeId   要删除的PlaceID
	 * @param dialog    显示删除的对话框（需要在数据获取返回结果后手动dismiss（无论是否成功））
	 */
	// 
	protected void deletePlaceByNet(final int placeId, final DialogInterface dialog) {
		PlaceRPC.unfollow(placeId, new Callback() {

			@Override
			public void onSuccess() {
				new CustomAbsClass.doSomething(PersonPlaceActivity.this) {
					@Override
					public void todo() {
						deleteItemPlace(placeId);
						Toast.makeText(MyBabyApp.getContext(), "已取消该地点", Toast.LENGTH_SHORT).show();
					}
				};
				Intent data = new Intent();
				data.setAction(Constants.BroadcastAction.BroadcastAction_PersonPlace_Unfollow);
				LocalBroadcastManager.getInstance(MyBabyApp.getContext()).sendBroadcast(data);
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				new CustomAbsClass.doSomething(PersonPlaceActivity.this) {
					@Override
					public void todo() {
						Toast.makeText(PersonPlaceActivity.this, "网络错误！请检查网络是否连接", Toast.LENGTH_SHORT).show();
					}
				};
			}
		});
	}

	/**
	 * 删除地点后在本地更新数据
	 * @param id  PlaceId
	 */
	// 
	public void deleteItemPlace(int id) {
		for (int i = 0; i < item.size(); i++) {
			Place place = (Place) item.get(i).get("item");
			if (place.getPlaceId() == id) {
				item.remove(i);
			}
		}
		adapter.setItem(item);
		iv_place.setAdapter(adapter);
	}

	class MyAdapter<Data> extends BaseAdapter {
		Context context;
		ArrayList<Map<String, Object>> item;
		UserPlaceSetting[] arrSetting;

		public MyAdapter(Context context, ArrayList<Map<String, Object>> item,UserPlaceSetting[] arrSetting) {
			this.context = context;
			this.item = item;
			this.arrSetting = arrSetting;
		}

		public void setItem(ArrayList<Map<String, Object>> item) {
			this.item = item;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return PLACE_SETTING_TITLE;
			} else if (position < (arrSetting.length + 1)) {
				return PLACE_SETTING;
			} else if (position == (arrSetting.length + 1)) {
				return PLACE_TOPIC_TITLE;
			} else {
				return PLACE_TOPIC;
			}
		}

		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 3;
		}

		@Override
		public int getCount() {
			return item.size() + arrSetting.length + 2;
		}

		@Override
		public Object getItem(int position) {
			if (position == PLACE_TOPIC) {
				return item.get(position - arrSetting.length - 2);
			} else if (position == PLACE_SETTING) {
				return arrSetting[position - 1];
			} else {
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			BaseHolder holder = null;
			if (getItemViewType(position) == PLACE_SETTING_TITLE) {
				if (arrSetting.length != 0) {
					TextView tv_setting = new TextView(getApplicationContext());
					tv_setting.setPadding(AppUIUtils.dip2px(10), AppUIUtils.dip2px(5), 0, AppUIUtils.dip2px(5));
					tv_setting.setBackgroundResource(R.color.bg_page);
					tv_setting.setTextColor(Color.GRAY);
					tv_setting.setText("设置");
					tv_setting.setTextSize(14);
					return tv_setting;
				} else {
					View view = new View(getApplicationContext());
					return view;
				}
			}
			if (getItemViewType(position) == PLACE_TOPIC_TITLE) {
				TextView tv = new TextView(getApplicationContext());
				tv.setPadding(AppUIUtils.dip2px(10), AppUIUtils.dip2px(5), 0, AppUIUtils.dip2px(5));
				tv.setBackgroundResource(R.color.bg_page);
				tv.setTextColor(Color.GRAY);
				tv.setText("关注的地点");
				tv.setTextSize(14);
				return tv;
			}
			if (getItemViewType(position) == PLACE_SETTING) {
				if (convertView != null && convertView.getTag() instanceof PlaceSettingHolder) {
					holder = (PlaceSettingHolder) convertView.getTag();
				} else {
					holder = new PlaceSettingHolder();
					convertView = holder.getRootView();
					convertView.setTag(holder);
				}
				holder.setData(arrSetting[position - 1]);
			}
			if (getItemViewType(position) == PLACE_TOPIC) {
				if (convertView != null&& convertView.getTag() instanceof PlaceTopicHolder) {
					holder = (PlaceTopicHolder) convertView.getTag();
				} else {
					holder = new PlaceTopicHolder();
					convertView = holder.getRootView();
					convertView.setTag(holder);
				}
				holder.setData(item.get(position - arrSetting.length - 2).get("item"));
			}
			return convertView;
		}
	}

	/**
	 * 删除地点设置的地点
	 * @param placeSetting 点击对应的UserPlaceSetting对象
	 */
	protected void clearPlaceSetting(UserPlaceSetting placeSetting) {
		UserRPC.clearPlaceSetting(placeSetting.getSettingKey(), new Callback() {

			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(MyBabyApp.getContext(), "地点删除成功", Toast.LENGTH_SHORT).show();
						PostMediaTask.sendBroadcast(Constants.BroadcastAction.BroadcastAction_PlaceSetting_IsSuccess, null);
						loadData();
					}
				});
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(MyBabyApp.getContext(), "地点删除失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
