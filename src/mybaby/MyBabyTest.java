package mybaby;

import org.xmlrpc.android.XMLRPCException;


import mybaby.models.User;
import mybaby.rpc.UserRPC;
import mybaby.rpc.UserRPC.UserCallback;
import mybaby.ui.MyBabyApp;
import mybaby.util.Utils;


public class MyBabyTest {
	public static boolean enabled(){
		return false;
	}
	
	public static void execTest(){
		UserRPC.getProfile(MyBabyApp.currentUser.getUserId(),
					 			   new UserCallback() {
							            @Override
							            public void onSuccess(User user){
							            	Utils.LogV("MyBabyTest", "成功");
							            }
							            @Override
							            public void onFailure(long id, XMLRPCException error){
							            	Utils.LogV("MyBabyTest", "失败");
										}
								    });
								
	}
}
