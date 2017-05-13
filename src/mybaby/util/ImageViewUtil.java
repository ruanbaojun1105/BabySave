package mybaby.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;
import mybaby.models.diary.Media;
import mybaby.ui.community.customclass.PhotoView;
import mybaby.ui.posts.person.PersonAvatar;

public class ImageViewUtil {
	public static ImageView showImageByMediaFile(Activity activity,Media mf,ViewGroup.LayoutParams lp){
		return showImageByMediaFile(activity,null,mf,lp,lp.width,lp.height,false,null,false,0,0);
	}
	public static ImageView showImageByMediaFile(Activity activity,Media mf,ViewGroup.LayoutParams lp,
													ImageLoadingListener imageLoadingListener,boolean showRemoteLoadingImage){
		return showImageByMediaFile(activity,null,mf,lp,lp.width,lp.height,false,imageLoadingListener,showRemoteLoadingImage,0,0);
	}	
	public static ImageView showImageByMediaFile(Activity activity,Media mf,int width,int height,boolean isRound){
		return showImageByMediaFile(activity,null,mf,null,width,height,isRound,null,false,0,0);
	}
	public static ImageView showImageByMediaFile(Activity activity,Media mf,int width,int height,boolean isRound,float sideWidth,int sideColor){
		return showImageByMediaFile(activity,null,mf,null,width,height,isRound,null,false,sideWidth,sideColor);
	}
	
	public static ImageView showImageByMediaFile(ImageView imageView,Media mf){
		ViewGroup.LayoutParams lp= imageView.getLayoutParams();
		return showImageByMediaFile(null,imageView,mf,lp,lp.width,lp.height,false,null,false,0,0);
	}
		

	//mediafileͼƬ
	private static ImageView showImageByMediaFile(Activity activity,ImageView imageView,Media mf,ViewGroup.LayoutParams lp,int width,int height,boolean isRound,
												ImageLoadingListener imageLoadingListener,boolean showRemoteLoadingImage,float sideWidth,int sideColor){
		if(width<=0)
			width=MyBabyApp.screenWidth; //当LayoutParams没有设置具体宽度时按屏幕宽度要求
		String fileUrl=getFileUrl(mf,width,height);
		
		if(imageView == null){
			if(isRound){
				imageView= PersonAvatar.setImageView(activity,sideWidth,sideColor);
			}else{
				imageView=new PhotoView(activity);
			}	
		}
		
		if(lp==null){
			imageView.setLayoutParams(new ViewGroup.LayoutParams(width,height));
		}else{
			imageView.setLayoutParams(lp);
		}
		
		displayImage(fileUrl,imageView,imageLoadingListener,showRemoteLoadingImage);
		return imageView;
	}
	
	public static void displayImage(String fileUrl,ImageView imageView){
		displayImage(fileUrl,imageView,null,false);
	}
	
	

	public static void displayImage(String fileUrl,ImageView imageView,ImageLoadingListener imageLoadingListener){
		ImageLoader.getInstance().displayImage(fileUrl, imageView, imageLoadingListener);
	}
	public static void displayImage(String fileUrl,ImageView imageView,ImageLoadingListener imageLoadingListener,boolean showRemoteLoadingImage){
		//DisplayImageOptions.Builder builder=new DisplayImageOptions.Builder();
		//builder.imageScaleType(ImageScaleType.EXACTLY);
		//builder.displayer(new FadeInBitmapDisplayer(500));
		//builder.bitmapConfig(Bitmap.Config.RGB_565);
		/*if(fileUrl.toLowerCase().startsWith("http://")){
			//builder.cacheOnDisk(true);
			if(showRemoteLoadingImage){
				//builder.showImageOnLoading(R.drawable.nocolor);
			}
		}*/
		//DisplayImageOptions options=builder.build();
		ImageLoader.getInstance().displayImage(fileUrl, imageView,imageLoadingListener);		
	}

	public static void displayRoundImage(String fileUrl,ImageView imageView,ImageLoadingListener imageLoadingListener){
		/*DisplayImageOptions.Builder builder=new DisplayImageOptions.Builder();
		builder.imageScaleType(ImageScaleType.EXACTLY);
		//builder.displayer(new FadeInBitmapDisplayer(500));
		builder.bitmapConfig(Bitmap.Config.RGB_565);
		builder.displayer(new RoundedBitmapDisplayer(20));
		//builder.cacheOnDisk(true);
		DisplayImageOptions options=builder.build();*/
		DisplayImageOptions displayImageOptions=new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true)// 设置下载的资源是否缓存在SD卡中
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY)//EXACTLY  IN_SAMPLE_POWER_OF_2
				.resetViewBeforeLoading(false)  // default
				//.delayBeforeLoading(500)  //延迟500毫秒开始加载
				.displayer(new RoundedBitmapDisplayer(10))
				.showImageForEmptyUri(R.drawable.btn_bg_gray)
				.showImageOnFail(R.drawable.btn_bg_gray)
				.showImageOnLoading(R.drawable.btn_bg_gray)
				/*.showImageForEmptyUri(new ColorDrawable(Color.parseColor("#f5f5f5")))
				.showImageOnFail(new ColorDrawable(Color.parseColor("#f5f5f5")))
				.showImageOnLoading(new ColorDrawable(Color.parseColor("#f5f5f5")))*/
				.build();
		ImageLoader.getInstance().displayImage(fileUrl,imageView,displayImageOptions,imageLoadingListener);
	}

	public static boolean isLocalImage(Media mf){
		return !StringUtils.isEmpty(mf.getFilePath());
	}
	
	public static String getFileUrl(Media mf,int width,int height){
		if(!StringUtils.isEmpty(mf.getFilePath())){
	        //����ԭͼ
			String filePath=mf.getFilePath();
			if(!filePath.startsWith("file://")){
				filePath="file://" + filePath;
			}
			return filePath;
		}else if(mf.getImageThumbnailRemoteURL() !=null && imageSizeCanFill(mf.getImageThumbnailWidth(),mf.getImageThumbnailHeight(), width,height)){
	        //Զ��Сͼ
			return mf.getImageThumbnailRemoteURL() ;
		}else if(mf.getImageMediumRemoteURL() !=null && imageSizeCanFill(mf.getImageMediumWidth(),mf.getImageMediumHeight(), width,height)){
	        //Զ����ͼ
			return mf.getImageMediumRemoteURL();
		}else if(mf.getImageLargeRemoteURL() !=null && imageSizeCanFill(mf.getImageLargeWidth(),mf.getImageLargeHeight(), width,height)){
	        //Զ�̴�ͼ
			return mf.getImageLargeRemoteURL();
		}else if(mf.getFileURL() !=null){
	        //Զ��ԭͼ
			return mf.getFileURL();
		}
		
		
		return null;
	}
	
	
	
	//�ж�image�ĳߴ��Ƿ������䵽imageView��
	private static boolean imageSizeCanFill(int imageWidth,int imageHeight,int containerWidth,int containerHeight){
		int offset=50;//�����ƫ��
		return imageWidth + offset >= containerWidth && imageHeight + offset >= containerHeight;
	}	
	

	
}
