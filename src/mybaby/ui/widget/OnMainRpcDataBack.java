package mybaby.ui.widget;

import org.xmlrpc.android.XMLRPCException;

import java.util.List;

import mybaby.models.community.Banner;
import mybaby.ui.community.holder.ItemState;

/**
 * Created by bj on 2016/1/9.
 */
public interface OnMainRpcDataBack {
    void onTSuccessToList(boolean isRefush, int lastId, Boolean hasMore,  List<ItemState> items, final Banner[] banners)throws Exception;
    void onTFailToList(long id, XMLRPCException error)throws Exception;
}
