package mybaby.action;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

import com.umeng.socialize.controller.UMSocialService;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.community.ParentingPost;

/**
 * Created by Administrator on 2016/1/11 0011.
 * 通过Action支持app内浏览器支持拨打电话，
 * Action格式为:mybaby_tel?phone_number=123456,
 * html内容如：<a href="tel:4006661166">测试拨打电话</a>
 */
public class CallPhoneAction extends Action implements Serializable {
    public static String call="mybaby_tel";//拨打电话
    public static String call2="tel:";//拨打电话
    String phone_number;
    public String getPhone_number() {
        return phone_number;
    }

    public CallPhoneAction() {
        super();
    }
    public  CallPhoneAction(String phone_number)
    {
        this.phone_number=phone_number;
    }

    @Override
    public Action setData(String url,Map<String, Object> map) {
        // TODO Auto-generated method stub
        if (url.contains(call)||url.contains(call2)) {
            //Map<String, Object> map=Action.getHeader(getReal_url());
            if (map.containsKey(url))
                this.phone_number=url.substring(url.indexOf(call2)+1);
            else
                this.phone_number=(String)map.get("phone_number");
            return new CallPhoneAction(phone_number);
        }else {
            return null;
        }
    }

    @Override
    public Boolean excute(Activity activity, UMSocialService webviewController,
                       WebView webView, ParentingPost parentingPost) {
        // TODO Auto-generated method stub
        Uri uri = Uri.parse("tel:"+phone_number);
        Intent it = new Intent(Intent.ACTION_DIAL, uri);
        activity.startActivity(it);
        return true;
    }
}
