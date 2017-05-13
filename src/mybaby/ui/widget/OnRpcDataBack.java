package mybaby.ui.widget;

/**
 * Created by bj on 2016/1/9.
 */
public interface OnRpcDataBack {

    void onRpcSuccess(boolean isRefush,int lastId, Boolean hasMore, Object[] objects)throws Exception;
    void onRpcFail()throws Exception;
}
