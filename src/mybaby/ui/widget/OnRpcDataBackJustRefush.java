package mybaby.ui.widget;

/**
 * Created by bj on 2016/1/9.
 */
public interface OnRpcDataBackJustRefush {

    void onRpcSuccess(Object[] objects)throws Exception;
    void onRpcFail()throws Exception;
}
