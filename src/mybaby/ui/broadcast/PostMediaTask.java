package mybaby.ui.broadcast;

import java.util.List;

import mybaby.Constants;
import mybaby.ui.MyBabyApp;
import mybaby.models.diary.Media;
import mybaby.models.diary.MediaRepository;
import mybaby.rpc.MediaRPC;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import mybaby.ui.main.MyBayMainActivity;
import mybaby.util.DialogShow;
import mybaby.util.Utils;

/**
 * 异步上传图片
 */
public class PostMediaTask extends AsyncTask<Void, Void, String[]> {

	Context context;
	List<String> mediaFilePaths;
	List<String> restPaths;
	int PARENT_ID;

	/**
	 * 异步上传图片
	 */
	public PostMediaTask(Context context, List<String> mediaFilePaths,
			int PARENT_ID) {
		this.mediaFilePaths = mediaFilePaths;
		this.restPaths = mediaFilePaths;
		this.context = context;
		this.PARENT_ID = PARENT_ID;
	}

	public List<String> getRestPaths() {
		return restPaths;
	}
	public int getPARENT_ID() {
		return PARENT_ID;
	}
	@Override
	protected String[] doInBackground(Void... arg0) {
		// TODO Auto-generated method stub

		Utils.Loge(PARENT_ID + "id图片总数" + mediaFilePaths.size());
		String[] result= new String[mediaFilePaths.size()];
		//String upok[][] = new String[mediaFilePaths.size()][2];
		for (int i = 0; i < mediaFilePaths.size(); i++) {
			Media media = MediaRepository.createMediaByParentId(PARENT_ID,
					mediaFilePaths.get(i), i);
			try {
				//result[i] =
				result[i] = MediaRPC.uploadMedia(media)?mediaFilePaths.get(i):"error";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result[i] = "error";
			}
		}
		return result;
	}

	// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		Toast.makeText(context, "图片正在上传中", 0).show();
		sendBroadcast(Constants.BroadcastAction.BroadcastAction_CommunityMain_ImageUpLoading,null);
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String[] results) {
		// TODO Auto-generated method stub
		String result = "";

		for (int i = 0; i < results.length; i++) {
			for (int j = 0; j < restPaths.size(); j++) {
				if (restPaths.get(j).equals(results[i])) {
				restPaths.remove(j);
				}
			}
		}
		for (int i = 0; i < results.length; i++) {
			result += i + results[i] + "\n";
			
		}
		Utils.Loge("图片上传结果如下:" + result);
		if (restPaths.size()==0) {
			Utils.Loge("图片上传完成,结果如下:" + result);
			Toast.makeText(context, "图片上传完成", 0).show();
			sendBroadcast(Constants.BroadcastAction.BroadcastAction_CommunityMain_ImageUpLoadingFinshed,null);
			Constants.postTask=null;
		}else {
			PostMediaTask.sendBroadcast(Constants.BroadcastAction.BroadcastAction_Netbad,null);
		}
		

		super.onPostExecute(results);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}
	
	public static  void sendBroadcast(String name,Bundle bundle) {
		Log.i("发送广播",name);
		Intent intent = new Intent();
		intent.setAction(name);
		if (bundle!=null) {
			intent.putExtras(bundle);
		}
		LocalBroadcastManager.getInstance(MyBabyApp.getContext())
				.sendBroadcast(intent);
	}

	/**
	 * 显示提示重试上传提示框
	 */
	public static void showReUpload(final Context context) throws Exception{
		ComponentName componentName=((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0).topActivity;
		// TODO Auto-generated method stub
		if (!Constants.hasTipReUpImage) {
			Constants.hasTipReUpImage=true;
			new DialogShow.DisLikeDialog(context,"有几张图片还没有上传成功,请检查网络连接,需要重试吗？", "重试", "放弃", new DialogShow.DoSomeThingListener() {

				@Override
				public void todo() {
					// TODO Auto-generated method stub
					if (Utils.isNetworkAvailable()) {
						if (Constants.postTask!=null&&Constants.postTask.getRestPaths()!=null&&Constants.postTask.getPARENT_ID()!=0) {
							Constants.postTask=new PostMediaTask(context, Constants.postTask.getRestPaths(), Constants.postTask.getPARENT_ID());
							Constants.postTask.execute();
						}
					}else {
						try {
							showReUpload(context);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							try {
								Constants.hasTipReUpImage=false;
								showReUpload(MyBayMainActivity.activity);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}
			}, null);
		}
	}
}
