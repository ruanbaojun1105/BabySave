package mybaby.models.community;

import android.content.ContentValues;
import android.database.Cursor;
import mybaby.models.Repository;
import mybaby.models.User;
import mybaby.models.UserRepository;

public class PlaceRepository extends Repository {
	   public static Place load(int placeObjId) {

	        Cursor c = db().query(table_place(), null, "objId=" + placeObjId, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        Place place=null;
	        if (numRows > 0) {
	        	place=new Place(c);
	        }
	        c.close();

	        return place;
	    }
	   
	   public static Place loadByPlaceId(int placeId) {

	        Cursor c = db().query(table_place(), null, "placeId=" + placeId, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        Place place=null;
	        if (numRows > 0) {
	        	place=new Place(c);
	        }
	        c.close();

	        return place;
	    }
	   
	   public static int save(Place place) {
	       int returnValue = -1;
	       if (place != null) {

	           ContentValues values = new ContentValues();

	           values.put("placeId", place.getPlaceId());
	           values.put("country", place.getCountry());
	           values.put("state", place.getState());
	           values.put("city", place.getCity());
	           values.put("district", place.getDistrict());
	           values.put("address", place.getAddress());
	           values.put("place_name", place.getPlace_name());
	           values.put("latitude", place.getLatitude());
	           values.put("longitude", place.getLongitude());
	           values.put("adcode", place.getAdcode());

	           if(PlaceRepository.exist(place.getObjId())){
	           		int rows=db().update(table_place(), values, "objId=" + place.getObjId(), null);
	           		if(rows>0){
	           			returnValue=place.getObjId();
	           		}
	           }else{
	           		returnValue =(int)db().insert(table_place(), null, values);
	           		if(returnValue>0){
	           			place.setObjId(returnValue);
	           		}
	           }
	       }
	       return (returnValue);
	   } 
	   
	   
	    public static boolean delete(Place place) {
		      boolean returnValue = false;

		      int result = 0;
		      result = db().delete(table_place(), "objId=" + place.getObjId(), null);

		      if (result == 1) {
		          returnValue = true;
		      }

		      return returnValue;
		  }
	    
	    public static boolean clear() {
		      db().delete(table_place(), null, null);
		      return true;
		}
	    
	    public static boolean exist(int objId) {
	        Cursor c = db().query(table_place(), null, "objId=" + objId, null,
	                null, null, null);
	        int numRows = c.getCount();
	        c.close();
			return numRows > 0;
	    }
	    
}
