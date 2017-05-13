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
 *  mybaby_alert?title=xxx&desc=xxx&cancel=xxx&items=json串
 */
public class DialogTipInfoAction extends Action implements Serializable {
    public static String DialogSheetAction="mybaby_alert";//操作选项列表

    private String title_text="";
    private String desc_text="";
    private String cancel_text="";
    private String itmes_json="";

    public String getDesc_text() {
        return desc_text;
    }

    public void setDesc_text(String desc_text) {
        this.desc_text = desc_text;
    }

    public String getCancel_text() {
        return cancel_text;
    }

    public void setCancel_text(String cancel_text) {
        this.cancel_text = cancel_text;
    }

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

    public DialogTipInfoAction() {
    }

    public DialogTipInfoAction(String title_text, String desc_text, String cancel_text, String itmes_json) {
        this.title_text = title_text;
        this.desc_text = desc_text;
        this.cancel_text = cancel_text;
        this.itmes_json = itmes_json;
    }

    @Override
    public Action setData(String url,Map<String, Object> map) {
        // TODO Auto-generated method stub
        if (url.contains(DialogSheetAction)) {
            //Map<String, Object> map=Action.getHeader(getReal_url());
            this.title_text=(String) map.get("title");
            this.desc_text=(String) map.get("desc");
            this.cancel_text=(String) map.get("cancel");
            this.itmes_json=(String) map.get("items");
            return new DialogTipInfoAction(title_text,desc_text,cancel_text,itmes_json).setActiontTitle(title_text);
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
            if (actions.length>0){
                MaterialDialogUtil.DialogCommListener listener1=new MaterialDialogUtil.DialogCommListener() {
                    @Override
                    public void todosomething() {
                        try {
                            if (!Constants.hasLoginOpenIM)
                                MainUtils.loginIM();
                            Action.createAction(actions[0]).excute(activity, webviewController, webView, parentingPost);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.e("action执行异常");
                        }
                    }
                };
                MaterialDialogUtil.showCommDialog(activity,getTitle_text(),desc_text,titles[0],cancel_text,listener1,null);
                /*if (actions.length==1){
                    MaterialDialogUtil.DialogCommListener listener1=new MaterialDialogUtil.DialogCommListener() {
                        @Override
                        public void todosomething() {
                            try {
                                if (!Constants.hasLoginOpenIM)
                                    MainUtils.loginIM();
                                Action.createAction(actions[0]).excute(activity, webviewController, webView, parentingPost);
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogUtil.e("action执行异常");
                            }
                        }
                    };
                    MaterialDialogUtil.showCommDialog(activity,getTitle_text(),desc_text,null,cancel_text,listener1,null);
                }else {
                    //MaterialDialogUtil.showCommDialog(activity,getTitle_text(),desc_text,"",cancel_text,listener1,null);
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
                }*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }

        return true;
    }

}
