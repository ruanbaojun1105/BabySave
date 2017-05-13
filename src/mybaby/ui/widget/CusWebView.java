package mybaby.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class CusWebView extends WebView {
    public ScrollInterface mScrollInterface;

    public CusWebView(Context context) {
        super(context);
    }

    public CusWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CusWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollInterface!=null)
            mScrollInterface.onSChanged(l, t, oldl, oldt);

    }

    public void setOnCustomScroolChangeListener(ScrollInterface scrollInterface) {

        this.mScrollInterface = scrollInterface;

    }

    public interface ScrollInterface {

        public void onSChanged(int l, int t, int oldl, int oldt);

    }

}