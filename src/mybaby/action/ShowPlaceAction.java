package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.ui.community.customclass.CustomAbsClass;

/**
 * Created by Administrator on 2016/1/11 0011.
 *通过Action支持根据placeid显示地图
 * Action格式为：mybaby_place_map?pid=xxx
 */
public class ShowPlaceAction extends Action implements Serializable {
    public static String ShowPlaceAction="mybaby_place_map";//显示地图
    int pid;
    public  ShowPlaceAction()
    {
        super();
    }
    public  ShowPlaceAction(int pid)
    {
        this.pid=pid;
    }

    public int getPid() {
        return pid;
    }

    @Override
    public Action setData(String url,Map<String, Object> map) {
        if (url.contains(ShowPlaceAction)) {
            //Map<String, Object> map=Action.getHeader(getReal_url());
            this.pid=Integer.parseInt((String)map.get("pid"));
            return new ShowPlaceAction(pid);
        }else {
            return null;
        }
    }

    @Override
    public Boolean excute(Activity activity, UMSocialService webviewController, WebView webView, ParentingPost parentingPost) {
        CustomAbsClass.startShowMapActivity(activity, pid);
        return true;
    }
}
