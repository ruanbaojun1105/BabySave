package mybaby.share;

public class ShareBean {

	String title;//标题
	String cagetoryTitle;//话题标题
	String content;//内容
	String imageUrl;//图片地址
	String targetUrl;//分享链接
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCagetoryTitle() {
		return cagetoryTitle;
	}
	public void setCagetoryTitle(String cagetoryTitle) {
		this.cagetoryTitle = cagetoryTitle;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getTargetUrl() {
		return targetUrl;
	}
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	public ShareBean(String title, String cagetoryTitle, String content,
			String imageUrl, String targetUrl) {
		super();
		this.title = title;
		this.cagetoryTitle = cagetoryTitle;
		this.content = content;
		this.imageUrl = imageUrl;
		this.targetUrl = targetUrl;
	}
	public ShareBean() {
		super();
	}
	
	
}
