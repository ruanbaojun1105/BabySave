package mybaby.rpc.community;

import mybaby.rpc.BaseRPC;

/**
 * Created by LeJi_BJ on 2016/3/16.
 */
public class BannerRPC extends BaseRPC{
    private static String prifixError="XMLRPCException-BannerRPC";

    public static void getBannerArray(CallbackForObjs callback){
        rpcCallForReturnObjs(prifixError, "bz.xmlrpc.banner.get_main", extParams(), callback);
    }
}
