package mybaby.models;

import java.io.Serializable;
import java.util.Map;

import mybaby.util.MapUtils;

/**
 * Created by LeJi_BJ on 2016/3/11.
 * 浏览器页面属性模型
 * * "urlRegex"=>"^http://h5.m.taobao.com/awp/core/detail.htm\?", 判断页面的正则表达式
 "title"=>"宝贝详情", 页面标题，为空则用html页自己的标题
 "hideTopHeight"=>0, 顶部隐藏高度
 "hideBottomHeight"=>0, 底部隐藏高度
 "hideLeftWidth"=>0, 左边隐藏宽度
 "hideRightWidth"=>0, 右边隐藏宽度
 "openNewPageUrlRegex"=>"http://shop.m.taobao.com/|http://h5.m.taobao.com/awp/core/detail.htm\?", 需要打开新页面的常规链接url正则表达式，通过“|”区分多个url
 "openNewPageUrlRegex_Other"=>"http://shop.m.taobao.com/", 需要打开新页面的其他链接的url正则表达式，通过“|”区分多个url
 "htmlPageScript"=>$script, 本页面需要执行的javascript脚本
 "delayDisplayMillisecond"=>0 页面加载延迟显示时间，毫秒
 *
 */
public class WebViewOption implements Serializable{
    String urlRegex;
    String title;
    int hideTopHeight;
    int hideBottomHeight;
    int hideLeftWidth;
    int hideRightWidth;
    String openNewPageUrlRegex;
    String openNewPageUrlRegex_Other;
    String htmlPageScript;
    String default_html;
    long delayDisplayMillisecond;

    public static WebViewOption[] createByArray(Object[] arr){
        if (arr==null)
            return null;
        WebViewOption[] retArr=new WebViewOption[arr.length];

        for (int i = 0; i < arr.length; i++) {
            Map<?, ?> map = (Map<?, ?>) arr[i];
            retArr[i] = createByMap(map);
            //Utils.Loge(map.toString());
        }

        return retArr;
    }

    public static WebViewOption createByMap(Map<?, ?> map){
        WebViewOption obj=new WebViewOption();

        obj.setDelayDisplayMillisecond(MapUtils.getMapLong(map, "delayDisplayMillisecond"));
        obj.setHideBottomHeight(MapUtils.getMapInt(map, "hideBottomHeight"));
        obj.setHideLeftWidth(MapUtils.getMapInt(map, "hideLeftWidth"));
        obj.setHideRightWidth(MapUtils.getMapInt(map, "hideRightWidth"));
        obj.setHideTopHeight(MapUtils.getMapInt(map, "hideTopHeight"));
        obj.setHtmlPageScript(MapUtils.getMapStr(map, "htmlPageScript"));
        obj.setOpenNewPageUrlRegex(MapUtils.getMapStr(map, "openNewPageUrlRegex"));
        obj.setOpenNewPageUrlRegex_Other(MapUtils.getMapStr(map, "openNewPageUrlRegex_Other"));
        obj.setTitle(MapUtils.getMapStr(map, "title"));
        obj.setDefault_html(MapUtils.getMapStr(map,"default_html"));
        obj.setUrlRegex(MapUtils.getMapStr(map,"urlRegex"));

        return obj;
    }

    public String getDefault_html() {
        return default_html;
    }

    public void setDefault_html(String default_html) {
        this.default_html = default_html;
    }

    public String getUrlRegex() {
        return urlRegex;
    }

    public void setUrlRegex(String urlRegex) {
        this.urlRegex = urlRegex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHideTopHeight() {
        return hideTopHeight;
    }

    public void setHideTopHeight(int hideTopHeight) {
        this.hideTopHeight = hideTopHeight;
    }

    public int getHideBottomHeight() {
        return hideBottomHeight;
    }

    public void setHideBottomHeight(int hideBottomHeight) {
        this.hideBottomHeight = hideBottomHeight;
    }

    public int getHideLeftWidth() {
        return hideLeftWidth;
    }

    public void setHideLeftWidth(int hideLeftWidth) {
        this.hideLeftWidth = hideLeftWidth;
    }

    public int getHideRightWidth() {
        return hideRightWidth;
    }

    public void setHideRightWidth(int hideRightWidth) {
        this.hideRightWidth = hideRightWidth;
    }

    public String getOpenNewPageUrlRegex() {
        return openNewPageUrlRegex;
    }

    public void setOpenNewPageUrlRegex(String openNewPageUrlRegex) {
        this.openNewPageUrlRegex = openNewPageUrlRegex;
    }

    public String getOpenNewPageUrlRegex_Other() {
        return openNewPageUrlRegex_Other;
    }

    public void setOpenNewPageUrlRegex_Other(String openNewPageUrlRegex_Other) {
        this.openNewPageUrlRegex_Other = openNewPageUrlRegex_Other;
    }

    public String getHtmlPageScript() {
        return htmlPageScript;
    }

    public void setHtmlPageScript(String htmlPageScript) {
        this.htmlPageScript = htmlPageScript;
    }

    public long getDelayDisplayMillisecond() {
        return delayDisplayMillisecond;
    }

    public void setDelayDisplayMillisecond(long delayDisplayMillisecond) {
        this.delayDisplayMillisecond = delayDisplayMillisecond;
    }
}
