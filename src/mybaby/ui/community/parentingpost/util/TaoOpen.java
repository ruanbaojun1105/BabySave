package mybaby.ui.community.parentingpost.util;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

public class TaoOpen {

    public static final int OAUTH_REQUESTCODE = 1;
    private static final String OAUTH_API = "taobao.oauth.code.create";

    /**
     * 启动手机淘宝的授权页面
     * 
     * @param activity
     *            具体启动的页面，是一个activity
     * @param appkey
     *            第三方应用在淘宝无线开放平台注册的appkey
     * @param secret
     *            第三方应用在淘宝无线开放平台注册后获得的secret.
     */
    public static void startOauth(Activity activity, String appkey, String secret) {
        if (null == activity || null == appkey || null == secret) {
            Toast.makeText(activity, "参数不可为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String apkSign = getApkSignNumber(activity);
        TreeMap<String, String> tree = new TreeMap<String, String>();
        tree.put("appKey", appkey);
        tree.put("apkSign", apkSign);
        tree.put("apiName", OAUTH_API);

        Intent intent = new Intent();
        intent.setAction("com.taobao.open.intent.action.GETWAY");
        Uri uri = Uri.parse(new StringBuilder("tbopen://m.taobao.com/getway/oauth?").append("&appkey=").append(appkey).append("&pluginName=").append(OAUTH_API).append("&apkSign=").append(apkSign)
                .append("&sign=").append(createParamsTipSign(tree, secret)).toString());
        intent.setData(uri);

        List<ResolveInfo> list = activity.getPackageManager().queryIntentActivities(intent, 0);
        if (list.size() > 0) {
            activity.startActivityForResult(intent, OAUTH_REQUESTCODE);
        } else {
            Toast.makeText(activity, "淘宝未安装", Toast.LENGTH_SHORT).show();
        }
    }

    /** 生成第三方应用的Sign */
    private static String createParamsTipSign(TreeMap<String, String> params, String appSecret) {
        StringBuilder paramsString = new StringBuilder(appSecret);

        Set<Entry<String, String>> paramsEntry = params.entrySet();
        String key;
        String value;
        for (Entry<String, String> paramEntry : paramsEntry) {
            key = paramEntry.getKey();
            value = paramEntry.getValue();
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                paramsString.append(key).append(value);
            }
        }

        String sign = "";
        String query = paramsString.append(appSecret).toString();
        try {
            sign = md5ToHex(query.getBytes("utf-8")).toUpperCase();
        } catch (Exception e) {
        }

        return sign;
    }

    private static String md5ToHex(byte[] input) {
        try {
            byte[] src = MessageDigest.getInstance("MD5").digest(input);

            StringBuilder stringBuilder = new StringBuilder("");
            if (src == null || src.length <= 0)
                return null;

            int len = src.length;
            String hv;
            for (int i = 0; i < len; i++) {
                byte b = src[i];
                int v = b & 255;
                hv = Integer.toHexString(v);
                if (hv.length() < 2)
                    stringBuilder.append(0);
                stringBuilder.append(hv);
            }

            return stringBuilder.toString();
        } catch (Exception e) {
        }

        return "";
    }

    /** 获取第三方应用签名所使用的序列号，与注册的序列号对比 */
    private static String getApkSignNumber(Activity activity) {
        try {
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_SIGNATURES);
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(packageInfo.signatures[0].toByteArray()));
            return cert.getSerialNumber().toString();
            // return "abcd"; //测试
        } catch (Exception e) {
        }

        return "";
    }
}
