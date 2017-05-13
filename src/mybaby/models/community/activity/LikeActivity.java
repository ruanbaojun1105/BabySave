package mybaby.models.community.activity;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.User;
import mybaby.util.MapUtils;

public class LikeActivity extends AbstractActivity  implements Serializable{

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;

	public LikeActivity() {
		super();
	}
	
	public static LikeActivity[] createByArray(Object[] arr){
		LikeActivity[] retArr=new LikeActivity[arr.length];
		
       	for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = createByMap(map);
       	}
       	
		return retArr;
	}
	
	public static LikeActivity createByMap(Map<?, ?> map){
		LikeActivity obj=new LikeActivity();
		
		obj.setId(MapUtils.getMapInt(map, "id"));
		obj.setDatetime_gmt(MapUtils.getMapDateLong(map, "datetime"));
	    
		Map<?, ?> mapUser = MapUtils.getMap(map,"user");

	    if(mapUser != null)
	    	obj.setUser(User.createByMap_new(mapUser));


		return obj;
	}
	
}
