package mybaby.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.contact.IYWContactService;
import com.alibaba.mobileim.contact.YWContactManager;

import org.xmlrpc.android.XMLRPCException;

import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.community.UserPlaceSetting;
import mybaby.rpc.BaseRPC;
import mybaby.rpc.BaseRPC.CallbackForString;
import mybaby.rpc.community.ReportRPC;
import mybaby.rpc.community.ReportRPC.ReportReason;
import mybaby.rpc.community.ReportRPC.ReportType;
import mybaby.rpc.community.TopicRPC;
import mybaby.ui.community.adapter.GridViewAdapter;
import mybaby.ui.broadcast.PostMediaTask;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.main.MyBayMainActivity;

public class DialogShow {
	private  Activity context;
	private ReportReason reason;
	private Dialog dialog;

	public DialogShow(Activity activity) {
		context = activity;
	}
/**
 * 将图片保存到手机
 */
	public static void savePictureDialog(final Activity activity,final Bitmap bitmap) {
		if (activity==null)
			return;
		final String items[]={"保存到手机"};
           //dialog参数设置
		MaterialDialogUtil.showListDialog(activity, null, items, new MaterialDialogUtil.DialogListListener() {
			@Override
			public void todosomething(int position) {
				if (position == 0) {
					long name = System.currentTimeMillis();
					try {
						ImageHelper.saveBitmapFile(activity, bitmap, name + "");
					} catch (Exception e) {
						e.printStackTrace();
					}
					Toast.makeText(activity, "保存到SD卡>辣妈说", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	/**
	 * 拉黑举报的dialog
	 */
	public  void showDefridendReport(final ReportType reportType, final int objectId,final String IMuserID,
									 final int objectUserId)
	{
		if (context==null)
			return;
		String[] items={"拉黑", "举报"};
		MaterialDialogUtil.showListDialog(context, null, items, new MaterialDialogUtil.DialogListListener() {
			@Override
			public void todosomething(int position) {
				if (position == 0) {
					MaterialDialogUtil.showCommDialog(context, "确认拉黑吗？", new MaterialDialogUtil.DialogCommListener() {
						@Override
						public void todosomething() {
							MybabyDefrenid(objectUserId, IMuserID);
						}
					});
				} else {
					showReportDialog(reportType, objectId,
							objectUserId);
				}
			}
		});

	}
/**
* 本地拉黑
* */

	public void MybabyDefrenid(final int objectUserId,final String IMuserID) {
		TopicRPC.pullBlack(objectUserId, new BaseRPC.Callback() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				IMDefrenid(IMuserID);
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething((Activity) context) {
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
	/**
	 * IM拉黑
	 * */
	public  void IMDefrenid(final String IMuserID )
	{
		if(!YWContactManager.isBlackListEnable()) {
			YWContactManager.enableBlackList();
		}
		YWIMKit imKit = MyBayMainActivity.getmIMKit();
		if (imKit==null)
			return;
		IYWContactService contactService = imKit.getIMCore().getContactService();
		contactService.addBlackContact(IMuserID.toLowerCase(), Constants.OPENIM_KEY, new IWxCallback() {
			public void onSuccess(Object... result) {
				//IYWContact iywContact = (IYWContact) result[0];
				Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
			}

			public void onError(int code, String info) {
				YWLog.i(null, "加入黑名单失败，code = " + code + ", info = " + info);
			}

			public void onProgress(int progress) {
			}
		});
	}



	/**
	 * 举报的dialog
	 * 
	 * @param reportType
	 *            被举报对象类别，1--举报用户 2--举报动态
	 * @param objectId
	 *            被举报对象的id, 用户id 或 动态id
	 * @param objectUserId
	 *            被举报的用户id或动态所属的用户id
	 */
	public void showReportDialog(final ReportType reportType, final int objectId,
								 final int objectUserId) {
		showReportDialog(reportType, objectId, objectUserId, true, null);
	}
	public void showReportDialog(final ReportType reportType, final int objectId,
			final int objectUserId, final boolean sendReport, final DoSomeThingListenerReturnInt doListener) {

		String[] items = { "不当言论", "广告欺诈", "虚假身份", "骚扰信息", "其他" };
		MaterialDialogUtil.showListDialog(context, null, items, new MaterialDialogUtil.DialogListListener() {
			@Override
			public void todosomething(int position) {
				switch (position) {
					case 0:
						reason = ReportReason.ReportReason_0;
						if (doListener != null)
							doListener.todo(reason.ordinal());
						break;
					case 1:
						reason = ReportReason.ReportReason_1;
						if (doListener != null)
							doListener.todo(reason.ordinal());
						break;
					case 2:
						reason = ReportReason.ReportReason_2;
						if (doListener != null)
							doListener.todo(reason.ordinal());
						break;
					case 3:
						reason = ReportReason.ReportReason_3;
						if (doListener != null)
							doListener.todo(reason.ordinal());
						break;
					case 4:
						reason = ReportReason.ReportReason_4;
						if (doListener != null)
							doListener.todo(reason.ordinal());
						break;
				}
				if (sendReport)
					reportUser(context, reportType, objectId, objectUserId, reason);
			}
		});
	}

	/**
	 * 举报联网请求 请求成功与否均会返回Toast告知用户
	 * 
	 * @param reportType
	 *            被举报对象类别，1--举报用户 2--举报动态
	 * @param objectId
	 *            被举报对象的id, 用户id 或 动态id
	 * @param objectUserId
	 *            被举报的用户id或动态所属的用户id
	 * @param reason
	 *            举报原因, 0-－其他 1—骚扰信息 2-－虚假身份 3-－广告欺诈 4-－不当言论
	 */
	public static void reportUser(final Activity context,ReportType reportType, int objectId,
								  int objectUserId, int reason) {
		ReportRPC.report(reportType, objectId, objectUserId, reason,
				new CallbackForString() {

					@Override
					public void onSuccess(final String str) {
						new CustomAbsClass.doSomething(context) {
							@Override
							public void todo() {
								MaterialDialogUtil.showCommDialog(context, str, "确认",null,false, null);
								//Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
							}
						};
					}

					@Override
					public void onFailure(long id, XMLRPCException error) {
						new CustomAbsClass.doSomething(context) {
							@Override
							public void todo() {
								Toast.makeText(context, "系统繁忙，请稍后再试！", Toast.LENGTH_SHORT)
										.show();
							}
						};
					}
				});
	}
	public static void reportUser(final Activity context,ReportType reportType, int objectId,
			int objectUserId, ReportReason reason) {
		reportUser(context,reportType,objectId,objectUserId,reason.ordinal());
	}

	//单个
	public void signDialog(final Context context, String desc, String okText,
					 final DoSomeThingListener doListener) {

		MaterialDialogUtil.showCommDialog(context, desc, okText, null, false, new MaterialDialogUtil.DialogCommListener() {
			@Override
			public void todosomething() {
				if (doListener != null) {
					doListener.todo();
				}
			}
		});
	}
	/**
	 * 不感兴趣
	 */
	public static class DisLikeDialog {

		//双个
		public DisLikeDialog(final Context context, String desc, String okText,
				String cancelText, final DoSomeThingListener doListener1,
				final DoSomeThingListener doListener2) {

			MaterialDialogUtil.showCommDialog(context, null, desc, okText, cancelText, new MaterialDialogUtil.DialogCommListener() {
				@Override
				public void todosomething() {
					if (doListener1 != null) {
						doListener1.todo();
					}
				}
			}, new MaterialDialogUtil.DialogCommListener() {
				@Override
				public void todosomething() {
					if (doListener2 != null) {
						doListener2.todo();
					}
				}
			});
		}
	}

	public interface DoSomeThingListener {
		void todo();
	}
	public interface DoSomeThingListenerReturnInt {
		void todo(int type);
	}

	public interface DeleteListener {
		void delete(int id, DialogInterface dialog);
	}

	public interface PlaceSettingClick {
		void change(UserPlaceSetting placeSetting);

		void delete(UserPlaceSetting placeSetting);
	}

	/**
	 *  修改地点、删除地点的对话框
	 * @param placeSettingClick 回调接口
	 */
	public void showEditPostPlaceDialog(PlaceSettingClick placeSettingClick) {
		showPlaceSettingDialog(null, placeSettingClick);
	}

	/**
	 * 修改地点、删除地点的对话框
	 * @param placeSetting     		Place对象 用于回调 方便修改界面
	 * @param placeSettingClick 	回调接口
	 */
	public void showPlaceSettingDialog(final UserPlaceSetting placeSetting,
			final PlaceSettingClick placeSettingClick) {
		String[] items = { "修改地点","删除地点" };
		MaterialDialogUtil.showListDialog(context, null, items, new MaterialDialogUtil.DialogListListener() {
			@Override
			public void todosomething(int position) {
				if (position == 0) {
					placeSettingClick.change(placeSetting);
				} else if (position == 1) {
					placeSettingClick.delete(placeSetting);
				}
			}
		});
	}

	public void showDeleteDialog(String text, int id,
								 DeleteListener deleteListener) {
		showDeleteDialog(text, null, false, id, deleteListener);
	}

	/**
	 * 显示删除的Dialog，根据ID删除某个条目
	 * 
	 * @param text
	 *            自定义删除标签的名称
	 * @param makeSureText
	 *            确定取消关注的Dialog的内容 不需要显示时为null
	 * @param isNeedSureDialog
	 *            是否需要确定取消关注的Dialog true为需要，false为不需要
	 * @param id
	 *            所需删除的条目的ID
	 * @param deleteListener
	 *            回调监听
	 */
	public void showDeleteDialog(final String text, final String makeSureText,
			final boolean isNeedSureDialog, final int id,
			final DeleteListener deleteListener) {
		if (context==null)
			return;
		String[] items = { text };
		MaterialDialogUtil.showListDialog(context, null, items, new MaterialDialogUtil.DialogListListener() {
			@Override
			public void todosomething(int position) {
				if (position == 0) {
					if (isNeedSureDialog) {
						showMakeSureDialog(makeSureText, id, deleteListener);
					} else {
						deleteListener.delete(id, dialog);
					}
				}
			}
		});
	}

	/**
	 * 确定删除取消关注的dialog
	 * 
	 * @param id
	 *            删除ID
	 * @param deleteListener
	 *            监听回调
	 */
	public void showMakeSureDialog(String text, final int id,
			final DeleteListener deleteListener) {
		MaterialDialogUtil.showCommDialog(context, null, text, null, null, new MaterialDialogUtil.DialogCommListener() {
			@Override
			public void todosomething() {
				if (deleteListener!=null)
					deleteListener.delete(id, dialog);
			}
		});
	}

	private static ProgressDialog progDialog;

	/**
	 * 显示进度框
	 * 
	 * @param activity
	 *            activity对象
	 * @param text
	 *            对话款文本
	 */
	public static void showProgressDialog(Activity activity, String text) {
		if (activity==null)
			return;
		if (progDialog == null)
			progDialog = new ProgressDialog(activity);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage(text);
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	public static void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
			progDialog = null;
		}
	}

	
public static void showRestImageDialog(final Context context,final List<String> restPaths,final int parentId) {
	if (context==null)
		return;
	final Dialog dialog = new Dialog(context,R.style.dialog);
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialog.setContentView(R.layout.dialog_restimage);
	dialog.setCanceledOnTouchOutside(true);
	//dialog.getWindow().setBackgroundDrawableResource(context.getResources().getDrawable(R.drawable.nocolor));
	Display d = ((Activity) context).getWindowManager().getDefaultDisplay(); // 为获取屏幕宽、高
	LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
	p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.95
	
	GridView gridView=(GridView) dialog.findViewById(R.id.restimage_gridView);
	Button button_rest=(Button) dialog.findViewById(R.id.reupload_btn);
	Button cancle=(Button) dialog.findViewById(R.id.cancel);
	gridView.setAdapter(new GridViewAdapter(context, restPaths));
	button_rest.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (Utils.isNetworkAvailable()) {
				Constants.postTask=new PostMediaTask(context, restPaths, parentId);
				Constants.postTask.execute();
				dialog.dismiss();
			}else {
				dialog.dismiss();
				showRestImageDialog(context, restPaths, parentId);
			}
		}
	});
	cancle.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Constants.postTask=null;
			dialog.dismiss();
		}
	});
	dialog.show();
}

}
