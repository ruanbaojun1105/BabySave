package mybaby.models.discovery;

import java.io.Serializable;
import java.util.Map;

import mybaby.models.notification.NotificationCategory;
import mybaby.util.MapUtils;

/**
 * Created by LeJi_BJ on 2016/2/29.
 * ’发现‘ 模型
 */
public class DiscoveryObjs implements Serializable {
    private Discovery[] discoveries;

    public Discovery[] getDiscoveries() {
        return discoveries;
    }

    public void setDiscoveries(Discovery[] discoveries) {
        this.discoveries = discoveries;
    }

    public DiscoveryObjs(Discovery[] discoveries) {
        this.discoveries = discoveries;
    }

    public DiscoveryObjs() {
    }

    public static DiscoveryObjs[] createByArray(Object[] arr){
        if (arr==null)
            return null;
        DiscoveryObjs[] retArr=new DiscoveryObjs[arr.length];
        for (int i = 0; i < arr.length; i++) {
            retArr[i]=DiscoveryObjs.createForObj((Object[]) arr[i]);
        }
        return retArr;
    }
    public static DiscoveryObjs createForObj(Object[] arr){
        if (arr==null)
            return null;
        DiscoveryObjs retArr=new DiscoveryObjs(Discovery.createByArray(arr));
        return retArr;
    }
}
