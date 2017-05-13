package mybaby.models.community;

import java.io.Serializable;

import mybaby.models.BaseObject;
import mybaby.models.diary.Media;

public class Reply  extends BaseObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String content;
	Media[] medias;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Media[] getMedias() {
		return medias;
	}
	public void setMedias(Media[] medias) {
		this.medias = medias;
	}
	
	public Reply() {
		super();
	}
	
}
