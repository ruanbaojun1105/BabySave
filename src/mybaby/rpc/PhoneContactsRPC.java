package mybaby.rpc;

import mybaby.ui.MyBabyApp;
import mybaby.util.Utils;

public class PhoneContactsRPC extends BaseRPC {
	
	public static boolean sync2Server(Object[] addressBook){
		Object[] params=extParams(new Object[]{ MyBabyApp.currentUser.getFrdTelephoneCountryCode(),
	    										MyBabyApp.currentUser.getFrdTelephone(),
	    										addressBook});
	    String xmlrpcMethod="bz.xmlrpc.syncPhoneContacts";

	    try {
	        getClient().call(xmlrpcMethod, params);
	        return true;
	    } catch ( Exception e) {
	    	Utils.LogV("MyBaby", "XMLRPCException-PhoneContactsRPC-sync2Server: " + e.getMessage());
	        return false;
	    }
	}
	
}
