package mybaby.models.friend;

import mybaby.models.Repository;
import android.content.ContentValues;
import android.database.Cursor;

public class PhoneContactRepository extends Repository {
	   public static PhoneContact load(int contactId) {

	        Cursor c = db().query(table_phone_contact(), null, "contactId=" + contactId, null, null, null, null);

	        int numRows = c.getCount();
	        c.moveToFirst();

	        PhoneContact phoneContact=null;
	        if (numRows > 0) {
	        	phoneContact=new PhoneContact(c);
	        }
	        c.close();

	        return phoneContact;
	    }
	   
	   public static int save(PhoneContact phoneContact) {
	       int returnValue = -1;
	       if (phoneContact != null) {

	           ContentValues values = new ContentValues();
	           
	           values.put("contactId", phoneContact.getContactId());
	           values.put("rowVersion", phoneContact.getRowVersion());
	           values.put("remoteSyncFlag", phoneContact.getRemoteSyncFlag());

	           if(PhoneContactRepository.exist(phoneContact.getContactId()) ){
	           		int rows=db().update(table_phone_contact(), values, "contactId=" + phoneContact.getContactId(), null);
	           		if(rows>0){
	           			returnValue=phoneContact.getContactId();
	           		}
	           }else{
	           		db().insert(table_phone_contact(), null, values);
	           		returnValue=phoneContact.getContactId();
	           }

	       }
	       return (returnValue);
	   } 
	   
	   
	    public static boolean delete(int contactId) {
		      boolean returnValue = false;

		      int result = 0;
		      result = db().delete(table_phone_contact(), "contactId=" + contactId, null);

		      if (result == 1) {
		          returnValue = true;
		      }

		      return returnValue;
		}
	    
	    public static boolean exist(int contactId) {
	        Cursor c = db().query(table_phone_contact(), null, "contactId=" + contactId, null,
	                null, null, null);
	        int numRows = c.getCount();
	        c.close();
			return numRows > 0;
	    }
	    
	    public static boolean setAllSysnSuccess() {
	    	ContentValues values = new ContentValues();
            values.put("remoteSyncFlag", PhoneContact.remoteSync.SyncSuccess.ordinal());
	    	db().update(table_phone_contact(), values, null, null);
	    	return true;
	    }
}
