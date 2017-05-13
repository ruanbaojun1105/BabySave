package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;
import mybaby.ui.community.customclass.CustomAbsClass;

/**
 *
 * @author bj
 * 群聊
标识符：mybaby_group_chat
 *
 */
public class TribeChattingAciton extends Action implements Serializable {
    public static String TribeChattingAciton="mybaby_group_chat";//.群聊·
    long im_tribeid;
    String tribe_name;

    public String getTribe_name() {
        return tribe_name;
    }

    public void setTribe_name(String tribe_name) {
        this.tribe_name = tribe_name;
    }

    public long getIm_tribeid() {
        return im_tribeid;
    }

    public void setIm_tribeid(long im_tribeid) {
        this.im_tribeid = im_tribeid;
    }

    public TribeChattingAciton(String tribe_name, long im_tribeid) {
        this.tribe_name = tribe_name;
        this.im_tribeid = im_tribeid;
    }

    public TribeChattingAciton() {
        super();
    }
    @Override
    public Action setData(String url,Map<String, Object> map) {
        // TODO Auto-generated method stub
        if (url.contains(TribeChattingAciton)) {
           // Map<String, Object> map=Action.getHeader(getReal_url());
            if (map.containsKey("im_tribeid"))
                this.im_tribeid=Integer.parseInt((String)map.get("im_tribeid"));
            if (map.containsKey("tribe_name"))
                this.tribe_name=(String)map.get("tribe_name");
            return new TribeChattingAciton(tribe_name,im_tribeid);
        }else {
            return null;
        }

    }

    @Override
    public Boolean excute(final Activity activity, UMSocialService webviewController,
                       WebView webView, ParentingPost parentingPost) {
        // TODO Auto-generated method stub
        CustomAbsClass.starTribeChatting(activity, im_tribeid, tribe_name);
        return true;
    }

}
