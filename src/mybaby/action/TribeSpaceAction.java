package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import org.xutils.common.util.LogUtil;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.ui.community.customclass.CustomAbsClass;


/**
 *
 * @author bj
 *  群组空间的action: mybaby_to_group_activity_list?groupid=xxx&tribe_id=xxx
 */
public class TribeSpaceAction extends Action implements Serializable {
    public static String TribeSpaceAction="mybaby_to_group_activity_list";//.群空间·
    int groupid;
    long tribe_id;

    public long getTribe_id() {
        return tribe_id;
    }

    public void setTribe_id(int tribe_id) {
        this.tribe_id = tribe_id;
    }

    public static String getTribeSpaceAction() {
        return TribeSpaceAction;
    }

    public static void setTribeSpaceAction(String tribeSpaceAction) {
        TribeSpaceAction = tribeSpaceAction;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public TribeSpaceAction() {
        super();
    }

    public TribeSpaceAction(int groupid, long tribe_id) {
        this.groupid = groupid;
        this.tribe_id = tribe_id;
    }

    @Override
    public Action setData(String url,Map<String, Object> map) {
        // TODO Auto-generated method stub
        if (url.contains(TribeSpaceAction)) {
            //Map<String, Object> map=Action.getHeader(getReal_url());
            this.groupid=Integer.parseInt((String) map.get("groupid"));
            this.tribe_id=Long.parseLong((String) map.get("tribe_id"));
            return new TribeSpaceAction(groupid,tribe_id);
        }else {
            return null;
        }
    }

    @Override
    public Boolean excute(Activity activity, UMSocialService webviewController,
                       WebView webView, ParentingPost parentingPost) {
        // TODO Auto-generated method stub
        if (tribe_id!=0)
            CustomAbsClass.starTribeSpace(activity,tribe_id,getActiontTitle().toString(),false);
        else LogUtil.e("解析方式不支持");
        return true;
    }
}
