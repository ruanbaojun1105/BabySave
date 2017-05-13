package mybaby.models.notification;

import java.io.Serializable;

/**
 * Created by LeJi_BJ on 2016/2/24.
 */
public class TribeNotificationCategory extends NotificationCategory implements Serializable {
    boolean space_isStrong;
    int space_unread;

    public boolean isSpace_isStrong() {
        return space_isStrong;
    }

    public void setSpace_isStrong(boolean space_isStrong) {
        this.space_isStrong = space_isStrong;
    }

    public int getSpace_unread() {
        return space_unread;
    }

    public void setSpace_unread(int space_unread) {
        this.space_unread = space_unread;
    }

    public TribeNotificationCategory(boolean space_isStrong, int space_unread) {
        this.space_isStrong = space_isStrong;
        this.space_unread = space_unread;
    }

    public TribeNotificationCategory(NotificationCategory category) {
        this.setStrongRemind(category.isStrongRemind());
        this.setTribe_id(category.getTribe_id());
        this.setTitle(category.getTitle());
        this.setAction(category.getAction());
        this.setImageUrl(category.getImageUrl());
        this.setKey(category.getKey());
        this.setNewestDatetime_gmt(category.getNewestDatetime_gmt());
        this.setNewestDesc(category.getNewestDesc());
        this.setUnreadCount(category.getUnreadCount());
    }


}
