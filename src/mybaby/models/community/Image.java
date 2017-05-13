package mybaby.models.community;

import java.io.Serializable;
import java.util.Map;

import mybaby.util.MapUtils;

public class Image implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int id;
	String thumbnailUrl;
	String largeUrl;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public String getLargeUrl() {
		return largeUrl;
	}
	public void setLargeUrl(String largeUrl) {
		this.largeUrl = largeUrl;
	}

	public Image() {
		super();
	}

	public static Image[] createByArray(Object[] arr){
		if (arr==null)
			return null;
		Image[] retArr=new Image[arr.length];
		
       	for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = createByMap(map);
       	}
       	
		return retArr;
	}
	
	public static Image createByMap(Map<?, ?> map){
		Image obj=new Image();
		
		obj.setId(MapUtils.getMapInt(map, "id"));
		obj.setThumbnailUrl(MapUtils.getMapStr(map, "thumbnailUrl"));
		obj.setLargeUrl(MapUtils.getMapStr(map, "largeUrl"));

		return obj;
	}
	
}
