package mybaby.share;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import me.hibb.mybaby.android.R;
import mybaby.Constants;
import mybaby.models.diary.Media;
import mybaby.models.diary.MediaRepository;
import mybaby.models.diary.Post;
import mybaby.models.diary.PostRepository;
import mybaby.models.person.Baby;
import mybaby.models.person.SelfPerson;
import mybaby.ui.MyBabyApp;

public class Share {
	public static void showShareAppIntent(FragmentActivity activity) { 
		showInviteFriendsIntent(activity,"",null,null);
    }
	
	public static void showInviteFriendsIntent(FragmentActivity activity){
		showInviteFriendsIntent(activity,"",null,null);
	}
	
	@SuppressLint("StringFormatMatches")
	public static void showInviteFriendsIntent(FragmentActivity activity,String friendName,String[] emails,String[] phoneNumbers) {  
		String shareTextNoUrl=String.format("[%1$s] %2$s", Constants.APP_NAME,
				activity.getResources().getText(R.string.record_and_share_baby_accompanies_your_every_happy_moment));
		String shareText=String.format("%1$s \n%2$s", shareTextNoUrl, Constants.MY_BABY_APP_SHARE_URL);
		String subject=getInviteTitle(activity);
		
		String dialogTitle=String.format(activity.getResources().getText(R.string.hi_xxx) + ":\n    %2$s",friendName==null?"":friendName,shareText);
		dialogTitle =String.format("%1$s - %2$s", activity.getResources().getText(R.string.invite_friends),dialogTitle);
		
		showShareIntent(activity,dialogTitle,subject,shareText,Constants.MY_BABY_APP_SHARE_URL,shareTextNoUrl,getShareImageUrl(null),emails,phoneNumbers);
	}
	
	private static String getInviteTitle(FragmentActivity activity){
		return String.format((String) activity.getResources().getText(R.string.invite_to_xxx),"\"" + Constants.APP_NAME + "\"");
	}
	
	public static void showSharePostIntent(FragmentActivity activity,Post post){
		String postUrl=Constants.MY_BABY_APP_SHARE_URL;//String.format(Constants.MY_BABY_POST_SHARE_URL,post.getGuid());
		String content="";
		int maxDescLength=80;
		if(post.getDescription()!=null && post.getDescription().length()>0){
			String desc=post.getDescription();
			if(post.getDescription().length()>maxDescLength){
				desc=post.getDescription().substring(0,maxDescLength) + "...";
			}
			content=String.format(", %1$s", desc);
		}
		String pictureDesc="";
		if(post.getTypeNumber()==Post.type.Post.ordinal()){
			int postMediaCount=MediaRepository.getCountForPId(post.getId());
			if(postMediaCount>0){
				int photoTextId=postMediaCount>1 ? R.string.photos : R.string.photo;
				pictureDesc=String.format(" - %1$s(%2$d)",   activity.getResources().getText(photoTextId),
															postMediaCount);
			}
		}
		String shareTextNoUrl=String.format("[%1$s] %2$s, %3$s%4$s%5$s", Constants.APP_NAME,
																	DateFormat.getDateFormat(MyBabyApp.getContext()).format(post.getDateCreated()),
																	(new SimpleDateFormat("EEEE")).format(post.getDateCreated()),
																	content,
																	pictureDesc);
		String shareText=String.format("%1$s \n%2$s", shareTextNoUrl, postUrl);
		
		showShareIntent(activity,(String)activity.getResources().getText(R.string.share),Constants.APP_NAME,shareText,postUrl,shareTextNoUrl,getShareImageUrl(post),null,null);
	}
	
	//参考 http://stackoverflow.com/questions/9730243/android-how-to-filter-specific-apps-for-action-send-intent/18068122#18068122
	private static void showShareIntent(FragmentActivity activity,String dialogTitle,String subject,String shareText,String url,String shareTextNoUrl,String shareImageUrl,String[] emails,String[] phoneNumbers){
	    Intent emailIntent = new Intent();
	    emailIntent.setAction(Intent.ACTION_SEND);
	    // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
	    emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(shareText));
	    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
	    if(emails!=null && emails.length>0)
	    	emailIntent.putExtra(Intent.EXTRA_EMAIL, emails);
	    emailIntent.setType("message/rfc822");

	    PackageManager pm = activity.getPackageManager();
	    Intent sendIntent = new Intent(Intent.ACTION_SEND);     
	    sendIntent.setType("text/plain");

	    Intent openInChooser = Intent.createChooser(emailIntent, dialogTitle);

	    List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
	    List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();        
	    for (int i = 0; i < resInfo.size(); i++) {
	        // Extract the label, append it, and repackage it in a LabeledIntent
	        ResolveInfo ri = resInfo.get(i);
	        String packageName = ri.activityInfo.packageName;
	        if(packageName.contains("android.email")) {
	            emailIntent.setPackage(packageName);
	        } else {
	        	if(packageName.contains("bluetooth") || packageName.contains("cloud")){
	        		continue;
	        	}
	        	
	            Intent intent = new Intent();
	            intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
	            intent.setAction(Intent.ACTION_SEND);
	            intent.setType("text/plain");

	            if(packageName.contains("com.android.mms")){ //短信
		            if(phoneNumbers != null && phoneNumbers.length>0)
		            	intent.setData(Uri.parse("smsto:" + phoneNumbers[0]));
		            intent.putExtra(Intent.EXTRA_TEXT, shareText);
	            }else if(packageName.contains("com.facebook.katana")) {//facebook主应用
	            	String delimiter="?";
	            	if(url.contains("?")){
	            		delimiter="&";
	            	}
	            	String facebookShareUrl=String.format("%1$s%2$sog:title=%3$s&og:image=%4$s&source=android_facebook_share", url,delimiter,shareTextNoUrl,shareImageUrl);
	            	
	                intent.putExtra(Intent.EXTRA_TEXT, facebookShareUrl);
	            } else if(packageName.contains("android.gm")) {
	                intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(shareText));
	                intent.putExtra(Intent.EXTRA_SUBJECT, subject);               
	                intent.setType("message/rfc822");
	            }else{
	                intent.putExtra(Intent.EXTRA_TEXT, shareText);
	            }

	            intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
	        }
	    }

	    // convert intentList to array
	    LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

	    openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
	    activity.startActivity(openInChooser);
		
	}
	
	
	private static String getShareImageUrl(Post post){
		if(post != null){
			Media[] medias=MediaRepository.getForPId(post.getId());
			for(int i=0;i<medias.length;i++){
				if(medias[i].getMediaId()>0){
					return medias[i].getImageLargeRemoteURL();
				}
			}
		}
		
		Baby[] babies=PostRepository.loadBabies(MyBabyApp.currentUser.getUserId());
		
		for(int i=0;i<babies.length;i++){
			Media media=babies[i].getAvatar();
			if(media != null && media.getMediaId()>0){
				return media.getImageLargeRemoteURL();
			}
		}
		
		SelfPerson selfPerson=PostRepository.loadSelfPerson(MyBabyApp.currentUser.getUserId());
		Media media=selfPerson.getAvatar();
		if(media != null && media.getMediaId()>0){
			return media.getImageLargeRemoteURL();
		}
		
		return Constants.MY_BABY_APP_AD_IMAGE_URL;
	}
	

}
