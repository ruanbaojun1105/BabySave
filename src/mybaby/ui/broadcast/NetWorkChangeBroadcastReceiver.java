package mybaby.ui.broadcast;

import mybaby.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import mybaby.util.Utils;

/**
 * //广播接收并响应处理  获取网络实时状况
 * @author bj
 *
 */
public class NetWorkChangeBroadcastReceiver extends BroadcastReceiver { 
	   
    @Override 
    public void onReceive(Context context, Intent intent) { 
        ConnectivityManager connectivityManager= 
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
        if (connectivityManager!=null) { 
            NetworkInfo [] networkInfos=connectivityManager.getAllNetworkInfo(); 
            for (int i = 0; i < networkInfos.length; i++) { 
                State state=networkInfos[i].getState(); 
                if (NetworkInfo.State.CONNECTED==state) { 
                    Utils.Loge("------------> Network is ok"); 
                    PostMediaTask.sendBroadcast(Constants.BroadcastAction.BroadcastAction_Netok,null);
                    return; 
                } 
            } 
        } 
           
        //没有执行return,则说明当前无网络连接 
        Utils.Loge("------------> Network is validate"); 
        PostMediaTask.sendBroadcast(Constants.BroadcastAction.BroadcastAction_Netbad,null);
    } 
}
