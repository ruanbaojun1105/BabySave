/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mybaby.ui.community.parentingpost;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

import me.hibb.mybaby.android.R;

@Deprecated
public abstract class ToolbarControlBaseActivity<S extends Scrollable> extends AppCompatActivity implements ObservableScrollViewCallbacks {

    protected Toolbar toolbar;
    private S mScrollable;
    private int stauToolbar=0;
    private int color=0;
    private boolean isResourColor;
    protected ProgressBar webview_progess;
    protected FrameLayout dealError;
    protected RelativeLayout loading_rela;
    protected ProgressBar delayDisplayProgress;
    protected TextView tv_setShare, tv_title,tv_close,tv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        toolbar = (Toolbar) findViewById(R.id.toolbar_webview);
        webview_progess= (ProgressBar) findViewById(R.id.webview_progess);
        dealError= (FrameLayout) findViewById(R.id.dealError);
        loading_rela= (RelativeLayout) findViewById(R.id.loading_rela);
        delayDisplayProgress= (ProgressBar) findViewById(R.id.delayDisplay_progressBar);

        tv_title = (TextView) toolbar.findViewById(R.id.actionbar_title);
        tv_setShare = (TextView) toolbar.findViewById(R.id.share_tv);
        tv_close = (TextView) toolbar.findViewById(R.id.close_tv);
        tv_back = (TextView) toolbar.findViewById(R.id.actionbar_back);
        setSupportActionBar(toolbar);

        mScrollable = createScrollable();
        mScrollable.setScrollViewCallbacks(this);
    }

    public void setStauToolbar(int stauToolbar) {
        setStauToolbar(stauToolbar,0,true);
    }
    public void setStauToolbar(int stauToolbar,String bgcolor) {
        boolean issu= TextUtils.isEmpty(bgcolor);
        setStauToolbar(stauToolbar, issu?0:Color.parseColor("#" + bgcolor),issu);
    }
    private void reSetText(){
        setToolbarTextColor(R.color.black);
        toolbar.setBackgroundColor(getResources().getColor(R.color.bg_gray));
        tv_setShare.setBackgroundResource(R.drawable.btn_bg_gray);
        tv_close.setBackgroundResource(R.drawable.btn_bg_gray);
        tv_setShare.setTextColor(getResources().getColor(R.color.mainThemeColor));
        tv_close.setTextColor(getResources().getColor(R.color.mainThemeColor));
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ((View) mScrollable).getLayoutParams();
                lp.topMargin = toolbar.getHeight();
                ((View) mScrollable).requestLayout();
            }
        });
    }
    /**
     *
     * @param stauToolbar 1上下滑,2全屏,3透明
     */
    private void setStauToolbar(int stauToolbar,int color,boolean isResourColor) {
        this.stauToolbar = stauToolbar;
        this.color = color;
        this.isResourColor = isResourColor;
        toolbar.setVisibility(View.VISIBLE);
        switch (stauToolbar){
            case 0:
                reSetText();
                break;
            case 1:
                reSetText();
                break;
            case 2:
                toolbar.post(new Runnable() {
                    @Override
                    public void run() {
                        hideToolbar();
                    }
                });
                break;
            case 3:
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ((View) mScrollable).getLayoutParams();
                lp.topMargin=0;
                ((View) mScrollable).requestLayout();
                toolbar.bringToFront();
                toolbar.setBackground(null);
                setToolbarTextColor(R.color.white);
                webview_progess.setAlpha(0.8f);
                break;
        }
    }

    protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }

    protected void setToolbarTextColor(int color) {
        tv_title.setTextColor(getResources().getColor(color));
        tv_back.setTextColor(getResources().getColor(color));
        tv_setShare.setTextColor(getResources().getColor(color));
        tv_close.setTextColor(getResources().getColor(color));
        tv_setShare.setBackground(null);
        tv_close.setBackground(null);
    }


    protected abstract int getLayoutResId();

    protected abstract S createScrollable();

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (stauToolbar==3) {
            if (mScrollable instanceof ObservableWebView) {
                boolean isnu=color==0;
                int baseColor = isResourColor?(getResources().getColor(isnu?R.color.bg_gray:color)):color;
                float alpha = Math.min(1, (float) scrollY / getResources().getDimensionPixelSize(R.dimen.parallax_image_height));
                toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
                if (isnu)
                    setToolbarTextColor(alpha==1.0?R.color.black:R.color.white);
                //ViewHelper.setTranslationY((View) mScrollable, scrollY / 2);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollable.getCurrentScrollY(), false, false);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        Log.e("DEBUG", "onUpOrCancelMotionEvent: " + scrollState);
        switch (stauToolbar){
            case 0:
                break;
            case 1:
                if (scrollState == ScrollState.UP) {
                    if (toolbarIsShown()) {
                        hideToolbar();
                    }
                } else if (scrollState == ScrollState.DOWN) {
                    if (toolbarIsHidden()) {
                        showToolbar();
                    }
                }
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(toolbar) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(toolbar) == -toolbar.getHeight();
    }

    private void showToolbar() {
        moveToolbar(0);
    }

    private void hideToolbar() {
        moveToolbar(-toolbar.getHeight());
    }

    private void moveToolbar(final float toTranslationY) {
        if (ViewHelper.getTranslationY(toolbar) == toTranslationY) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(toolbar), toTranslationY).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(toolbar, translationY);
                ViewHelper.setTranslationY((View) mScrollable, translationY);
                ViewHelper.setTranslationY(webview_progess, translationY);
                ViewHelper.setTranslationY(dealError, translationY);
                ViewHelper.setTranslationY(loading_rela, translationY);

                FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) ((View) mScrollable).getLayoutParams();
                lp2.height = (int) -translationY + getScreenHeight() - lp2.topMargin;
                ((View) mScrollable).requestLayout();

                //toolbar.setVisibility(toTranslationY == 0 ? View.VISIBLE : View.GONE);
            }
        });
        animator.start();
    }
}
