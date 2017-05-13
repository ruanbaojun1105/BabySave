package mybaby.ui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import mybaby.Constants;
import mybaby.models.diary.Media;
import mybaby.models.diary.MediaRepository;
import mybaby.rpc.BaseRPC;
import mybaby.ui.MyBabyApp;
import mybaby.util.Utils;

//广播接收并响应处理 地点设置成功返回刷新
			public class MediaUplodingReceiver extends BroadcastReceiver {
				List<Integer> mediaIds;
				boolean needRefush=false;
				ProgressBar progress_postMediaUpLoad;
				BaseRPC.CallbackToDo interListen;
				
				public MediaUplodingReceiver(BaseRPC.CallbackToDo interListen,ProgressBar progress_postMediaUpLoad){
					this.interListen=interListen;
					this.progress_postMediaUpLoad=progress_postMediaUpLoad;
				}

				public void mediaUplodingReceiver() {
					IntentFilter filter = new IntentFilter();
					filter.addAction(Constants.BroadcastAction.BroadcastAction_Post_UploadMediaByid_OK);
					filter.addAction(Constants.BroadcastAction.BroadcastAction_Post_Media_Uploding);
					LocalBroadcastManager.getInstance(MyBabyApp.getContext())
							.registerReceiver(this, filter);
				}

				@Override
				public void onReceive(Context arg0, Intent arg1) {
					// TODO Auto-generated method stub
					// LogUtils.i("over"+arg1.getExtras().toString());
					if (arg1.getAction()
							.equals(Constants.BroadcastAction.BroadcastAction_Post_UploadMediaByid_OK)) {
						if (arg1.getExtras()!=null) {
							if (mediaIds!=null&&mediaIds.size()>0) {
								for (int i = 0; i < mediaIds.size(); i++) {
									if(mediaIds.get(i)==arg1.getExtras().getInt("mediaId",0)){
										Utils.Loge("已完成上传的media id为："+mediaIds.get(i));//请在删除前打印
										mediaIds.remove(i);
										progress_postMediaUpLoad.setVisibility(mediaIds.size()==0?View.GONE:View.VISIBLE);
										if (interListen!=null) {
											if (mediaIds.size()==0) {
												interListen.todo();
											}
										}
									}
								}
							}
						}else {
							progress_postMediaUpLoad.setVisibility(View.GONE);
							if (interListen!=null){
								if (needRefush) {
									interListen.todo();
								}
							}
						}
					}
					else if (arg1.getAction()
							.equals(Constants.BroadcastAction.BroadcastAction_Post_Media_Uploding)) {
						if (arg1.getExtras()!=null) {
							int pid=arg1.getExtras().getInt("postId",0);
							needRefush=arg1.getExtras().getBoolean("needRefush",false);
							Utils.Loge("pid:"+pid);
							Media[] medias=MediaRepository.getForPId(pid);
							mediaIds=new ArrayList<Integer>();
							if (medias!=null) {
								for (int i = 0; i < medias.length; i++) {
									mediaIds.add(medias[i].getId());
									Utils.Loge("mediaId:"+medias[i].getId());
								}
								progress_postMediaUpLoad.setVisibility(View.VISIBLE);
							}
							
						}
					}
				}
			}	
