package mybaby.action;

import android.app.Activity;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import org.json.JSONArray;
import org.json.JSONException;
import org.xutils.common.util.LogUtil;

import java.io.Serializable;
import java.util.Map;

import mybaby.Constants;
import mybaby.models.community.ParentingPost;
import mybaby.ui.main.MainUtils;
import mybaby.util.MaterialDialogUtil;


/**
 *
 * @author bj
 *  mybaby_action_sheet?title=xxx&items=json串
 */
public class DialogSheetAction extends Action implements Serializable {
    public static String DialogSheetAction="mybaby_action_sheet";//操作选项列表

    private String title_text="";
    private String itmes_json="";

    public String getTitle_text() {
        return title_text;
    }

    public void setTitle_text(String title_text) {
        this.title_text = title_text;
    }

    public String getItmes_json() {
        return itmes_json;
    }

    public void setItmes_json(String itmes_json) {
        this.itmes_json = itmes_json;
    }

    public DialogSheetAction() {
    }

    public DialogSheetAction(String title_text, String itmes_json) {
        this.title_text = title_text;
        this.itmes_json = itmes_json;
    }

    @Override
    public Action setData(String url,Map<String, Object> map) {
        // TODO Auto-generated method stub
        if (url.contains(DialogSheetAction)) {
            //Map<String, Object> map=Action.getHeader(getReal_url());
            this.title_text=(String) map.get("title");
            this.itmes_json=(String) map.get("items");
            return new DialogSheetAction(title_text,itmes_json).setActiontTitle(title_text);
        }else {
            return null;
        }
    }

    @Override
    public Boolean excute(final Activity activity, final UMSocialService webviewController,
                       final WebView webView, final ParentingPost parentingPost) {
        // TODO Auto-generated method stub
        //Gson gson=new Gson();可以用，但是没必要
        try {
            JSONArray array=new JSONArray(getItmes_json());
            if (array==null)
                return true;
            //final DialogItem[] items=new DialogItem[array.length()];
            final String[] titles=new String[array.length()];
            final String[] actions=new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                titles[i]=array.getJSONObject(i).getString("title");
                actions[i]=array.getJSONObject(i).getString("action");
                //items[i]=gson.fromJson(array.getString(i), DialogItem.class);
            }

            MaterialDialogUtil.showListDialog(activity, getTitle_text(), titles, new MaterialDialogUtil.DialogListListener() {
                @Override
                public void todosomething(int position) {
                    try {
                        if (!Constants.hasLoginOpenIM)
                            MainUtils.loginIM();
                        Action.createAction(actions[position]).excute(activity, webviewController, webView, parentingPost);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.e("action执行异常");
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }

        return true;
    }

}
