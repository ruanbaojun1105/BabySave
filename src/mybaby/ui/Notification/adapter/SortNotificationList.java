package mybaby.ui.Notification.adapter;

import java.util.Comparator;

import mybaby.models.notification.NotificationCategory;

/**
 * Created by bj on 2015/12/28.
 */
public class SortNotificationList implements Comparator<NotificationCategory> {
    @Override
    public int compare(NotificationCategory category0, NotificationCategory category1) {
        long arg0num=0,arg1num=0;
        arg0num=category0.getNewestDatetime_gmt();
        arg1num=category1.getNewestDatetime_gmt();
        long a=arg1num-arg0num;
        return a==0?0:((a>0)?1:-1);
    }
}
