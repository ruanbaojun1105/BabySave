package mybaby.ui.widget;

import org.xmlrpc.android.XMLRPCException;

import java.util.List;

/**
 * Created by bj on 2016/1/9.
 */
public interface OnRpcDataBaseBack {

    void onRpcSuccess(boolean isRefush, int lastId, Boolean hasMore,Object[] objects, List lists)throws Exception;
    void onRpcFail(long id, XMLRPCException error)throws Exception;
}
