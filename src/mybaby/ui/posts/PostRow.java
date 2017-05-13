package mybaby.ui.posts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.User;
import mybaby.models.diary.Post;
import mybaby.models.diary.PostRepository;
import mybaby.models.person.Baby;
import mybaby.share.UmengShare;
import mybaby.ui.MyBabyApp;
import mybaby.ui.community.DetailsActivity;
import mybaby.ui.main.MyBayMainActivity;
import mybaby.ui.posts.edit.EditPostActivity;
import mybaby.util.Utils;

public class PostRow{
	   public static View createPostRowView(LayoutInflater inflater,ViewGroup container){
		   View view=inflater.inflate(R.layout.post, null);
		   return view;
	   }
	private static SimpleDateFormat format1=new SimpleDateFormat("MMM");
	private static SimpleDateFormat format2=new SimpleDateFormat("dd");
	private static SimpleDateFormat format3=new SimpleDateFormat("yyyy");
	private static SimpleDateFormat format4=new SimpleDateFormat("EEEE");
	   public static void fillContent(final TimelineListFragment fragment,User user, final Post post,Baby ageForBaby,boolean isFriend, TimelineListFragment.PostHolder postHolder){
           final FragmentActivity activity=fragment.getActivity();
		   
		   //����
		   long localTime = post.getDateCreated();

		   postHolder.txtDateMonth.setText(format1.format(localTime));
		   
		   postHolder.txtDateDay.setText(format2.format(localTime));
		   postHolder.txtDateDay.getPaint().setFakeBoldText(true);

		   postHolder.txtDateYear.setText(format3.format(localTime));
		   
		   //���겻��ʾ���
		   LayoutParams lp_year=postHolder.txtDateYear.getLayoutParams();
           if(format3.format(localTime).equals(format3.format(System.currentTimeMillis()))){//����
        	   lp_year.height=0;
           }else{
        	   lp_year.height=LayoutParams.WRAP_CONTENT;
           }
           postHolder.txtDateYear.setLayoutParams(lp_year);
		   
		   String ageText;
	        if(ageForBaby != null){
	        	ageText=ageForBaby.getAgeText(post.getDateCreated());
	        	if(ageText==null){
	        		ageText=format4.format(post.getDateCreated());
	        	}
	        }else{
	        	ageText=format4.format(post.getDateCreated());
	        }
		   postHolder.txtAge.setText(ageText);

		   PostContent.fillContent(activity, post, postHolder);
		   
		   postHolder.btnComment.setTypeface(MyBabyApp.fontAwesome);
		   postHolder.iconLock.setTypeface(MyBabyApp.fontAwesome);
		   postHolder.btnShare.setTypeface(MyBabyApp.fontAwesome);
		   postHolder.btnMore.setTypeface(MyBabyApp.fontAwesome);
		   postHolder.post_lin.setOnClickListener(new View.OnClickListener() {
			   @Override
			   public void onClick(View v) {
				   if (post.getStatus().equals(Post.Status_Draft)){
					   editPost(activity,post);
				   }
			   }
		   });
		   postHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					editPost(activity,post);
				}
	       });
		   postHolder.btnComment.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//统计点击次数
					MobclickAgent.onEvent(activity, "4");//统计发送的个数
					
					clickComment(activity,post);
				}
	       });
		   postHolder.btnShare.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//统计点击次数
					MobclickAgent.onEvent(activity, "5");//统计发送的个数
					
					try {
						new UmengShare.OpenShare(activity, MyBayMainActivity.mainShareController, UmengShare.setShareBean(post, activity));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//Share.showSharePostIntent(activity, post);
				}
	       });
		   postHolder.btnMore.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//统计点击次数
					MobclickAgent.onEvent(activity, "6");//统计发送的个数
					
					clickMoreButton(fragment,post);
				}
	       });		   
		   
		   if(user.isSelf()){//�Լ�
			   postHolder.btnShare.setVisibility(View.VISIBLE);
			   postHolder.btnMore.setVisibility(View.VISIBLE);
		        
		        if(post.getStatus().equals(Post.Status_Draft)){  //ϵͳ��ʼ�Ĳݸ�
		        	postHolder.btnComment.setVisibility(View.GONE);
		        	postHolder.iconLock.setVisibility(View.GONE);
		        	postHolder.btnEdit.setVisibility(View.VISIBLE);
		        }else{
		        	postHolder.btnEdit.setVisibility(View.GONE);
		            
		            if( post.getPrivacyTypeNumber()!=Post.privacyType.Private.ordinal()){//δ��ͨ���ѹ��� ���� ��ͨ���ѹ��ܵ�û�������κκ����ҷ���˽
		            	postHolder.btnComment.setVisibility(View.VISIBLE);
		            	postHolder.iconLock.setVisibility(View.GONE);
		            }else{//�ѿ�ͨ���ѹ���
		            	postHolder.btnShare.setVisibility(View.GONE);
		            	postHolder.btnComment.setVisibility(View.GONE);
		            	postHolder.iconLock.setVisibility(View.VISIBLE);
		            }
		        }
		    }else{//����
		    	postHolder.btnShare.setVisibility(View.GONE);
		    	postHolder.btnMore.setVisibility(View.GONE);
		    	postHolder.btnEdit.setVisibility(View.GONE);
		    	postHolder.iconLock.setVisibility(View.GONE);
		        
		        if(isFriend){//����
		        	postHolder.btnComment.setVisibility(View.VISIBLE);
		        }else{//�Ǻ���
		        	postHolder.btnComment.setVisibility(View.GONE);
		        }
		    }
		   
		   
	   }
	   
	   public static void editPost(final Activity activity,final Post post){
		   Intent i = new Intent(activity, EditPostActivity.class);
           i.putExtra("id", post.getId());
           activity.startActivityForResult(i, Constants.RequestCode.ACTIVITY_REQUEST_CODE_EDIT_POST_REQUEST);
          
	   }
	   
	   private static void clickComment(FragmentActivity activity,Post post){
			Intent intent = new Intent(activity, DetailsActivity.class);
			Post p=PostRepository.load(post.getId());//取最新的，有可能上传后更新了
			intent.putExtra("postId", p.getPostid());
			activity.startActivity(intent);
	   }
	   
	   private static void clickMoreButton(final TimelineListFragment fragment,final Post post){
		    final FragmentActivity activity=fragment.getActivity();
		    
	    	final String[] items = {activity.getString(R.string.edit),activity.getString(R.string.delete)};  
	    	AlertDialog.Builder builder = new AlertDialog.Builder(activity);  
	    	builder.setTitle(R.string.more);
	        builder.setItems(items, new DialogInterface.OnClickListener() {  
	            public void onClick(DialogInterface dialog, int which) {
	            	if(which==0){  //�༭
	            		editPost(activity,post);
	            	}else if(which==1){ //ɾ��
	                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
	                    dialogBuilder.setTitle(activity.getResources().getText(R.string.delete));
	                    dialogBuilder.setMessage(String.format("%1$s \"%2$s %3$s\"", activity.getResources().getText(R.string.delete),
	                    							DateFormat.getDateFormat(activity).format(post.getDateCreated()),
	                    							post.getDescription()));
	                    dialogBuilder.setPositiveButton(activity.getResources().getText(R.string.ok),
	                            new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog,int whichButton) {
	                                	PostRepository.deletePost(post);
	                                	Utils.Loge("即将删除动态postid为"+post.getPostid());
	                                	//�㲥
	                                    Intent bi = new Intent();
	                                    bi.setAction(Constants.BroadcastAction.BroadcastAction_Post_Delete);
	                                    bi.putExtra("id", post.getId());
	                                    bi.putExtra("postId", post.getPostid());
	                                    LocalBroadcastManager.getInstance(activity).sendBroadcast(bi);
	                                }
	                            });
	                    dialogBuilder.setNegativeButton(activity.getResources().getText(R.string.cancel),null);
	                    Dialog dialog1=dialogBuilder.create();
	                    dialog1.setCanceledOnTouchOutside(true);
	                    dialog1.show();
	            	}
	            }  
	        });  
	        Dialog dialog=builder.create();
	        dialog.setCanceledOnTouchOutside(true);
	        dialog.show();
	   }
}
