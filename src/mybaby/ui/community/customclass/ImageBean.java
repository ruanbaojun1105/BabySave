package mybaby.ui.community.customclass;

/**
 * Created by Pan_ on 2015/2/3.
 */
public class ImageBean {
    private int width;
    private int height;
    private String url;//小图，为后续功能预留
    private String relazeUrl;//大图

   
    public ImageBean(int width, int height, String url, String relazeUrl) {
		super();
		this.width = width;
		this.height = height;
		this.url = url;
		this.relazeUrl = relazeUrl;
	}


	public int getWidth() {
		return width;
	}


	public void setWidth(int width) {
		this.width = width;
	}


	public int getHeight() {
		return height;
	}


	public void setHeight(int height) {
		this.height = height;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getRelazeUrl() {
		return relazeUrl;
	}


	public void setRelazeUrl(String relazeUrl) {
		this.relazeUrl = relazeUrl;
	}


	@Override
    public String toString() {

        return "image---->>url="+url+"width="+width+"height"+height;
    }
}
