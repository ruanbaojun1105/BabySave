package mybaby.ui.posts.edit;

import mybaby.models.diary.Media;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class MediaBean {

	
	FrameLayout.LayoutParams lp;
	Media media;
	String url;
	public FrameLayout.LayoutParams getLp() {
		return lp;
	}
	public void setLp(FrameLayout.LayoutParams lp) {
		this.lp = lp;
	}
	public Media getMedia() {
		return media;
	}
	public void setMedia(Media media) {
		this.media = media;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public MediaBean(LayoutParams lp, Media media, String url) {
		super();
		this.lp = lp;
		this.media = media;
		this.url = url;
	}
	public MediaBean() {
		super();
	}
	
	
}
