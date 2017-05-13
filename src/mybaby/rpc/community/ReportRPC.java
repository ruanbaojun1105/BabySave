package mybaby.rpc.community;

import mybaby.rpc.BaseRPC;
import mybaby.rpc.BaseRPC.CallbackForId;

public class ReportRPC extends BaseRPC {
	private static String prifixError="XMLRPCException-ReportRPC";
	
	//被举报对象类别: 1--举报用户  2--举报动态
	public enum  ReportType{
	    User,
	    Activity
	}

	//举报原因, 0-－其他  1—骚扰信息  2-－虚假身份  3-－广告欺诈  4-－不当言论
	public enum ReportReason{
	    ReportReason_0,
	    ReportReason_1,
	    ReportReason_2,
	    ReportReason_3,
	    ReportReason_4
	}


	public static void report(ReportType reportType, int objectId,int objectUserId,ReportReason reportReason,final CallbackForString callback){
		report(reportType, objectId, objectUserId, reportReason.ordinal(),callback);
	}
	public static void report(ReportType reportType, int objectId,int objectUserId,int reportReason,final CallbackForString callback){
		final String xmlrpcMethod="bz.xmlrpc.report.report";
		Object[] params=extParams(new Object[]{reportType.ordinal()+1,objectId,objectUserId,reportReason});
		rpcCallForReturnString(prifixError,xmlrpcMethod,params,callback);
	}
}
