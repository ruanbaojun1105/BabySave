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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

import me.hibb.mybaby.android.R;
import mybaby.ui.community.parentingpost.util.ColorEvaluator;

public abstract class ToolbarControlBaseFragment<S extends Scrollable> extends Fragment implements ObservableScrollViewCallbacks {

    protected int finishTag=0;
    protected Toolbar toolbar;
    private S mScrollable;
    protected int stauToolbar=0;
    private int color=0;
    private boolean isResourColor;
    protected ProgressBar webview_progess,webview_progress_two,delayDisplayProgress;
    protected FrameLayout dealError;
    protected RelativeLayout loading_rela;
    protected TextView tv_setShare, tv_title,tv_close,tv_back,webview_bellow_line;
    protected ImageView actionbar_title_image;
    protected View rootView,line_c;
    //protected ObservableWebView scrollable_staus;
    protected abstract int getLayoutResId();
    protected abstract S createScrollable();
    protected abstract Toolbar getToolbar();
    protected abstract void initToolbar();
    protected abstract void initData();
    protected abstract void star();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutResId(), null);
        webview_progess= (ProgressBar)rootView.findViewById(R.id.webview_progess);
        dealError = (FrameLayout) rootView.findViewById(R.id.dealError);
        loading_rela= (RelativeLayout) rootView.findViewById(R.id.loading_rela);
        delayDisplayProgress= (ProgressBar) rootView.findViewById(R.id.delayDisplay_progressBar);
        webview_progress_two= (ProgressBar) rootView.findViewById(R.id.webview_progress_two);
        //scrollable_staus= (ObservableWebView) rootView.findViewById(R.id.scrollable_staus);
        mScrollable = createScrollable();
        mScrollable.setScrollViewCallbacks(this);
        initData();
        if (getToolbar()!=null) {
            toolbar = getToolbar();
            tv_title = (TextView) toolbar.findViewById(R.id.actionbar_title);
            tv_setShare = (TextView) toolbar.findViewById(R.id.share_tv);
            tv_close = (TextView) toolbar.findViewById(R.id.close_tv);
            tv_back = (TextView) toolbar.findViewById(R.id.actionbar_back);
            actionbar_title_image = (ImageView) toolbar.findViewById(R.id.actionbar_title_image);
            line_c=toolbar.findViewById(R.id.line_c);
            webview_bellow_line = (TextView) toolbar.findViewById(R.id.webview_bellow_line);
            initToolbar();
        }
        star();
        //finish();
        return rootView;
    }

    public void finish(){
        if (finishTag<0)
            rootView.postDelayed(new Runnable() {

                @Override
                public void run() {
                    getActivity().onBackPressed();
                }
            }, 100);
    }

    public void setFinishTag(int finishTag) {
        this.finishTag = finishTag;
    }

    public ProgressBar getWebview_progress_two() {
        return webview_progress_two;
    }

    public void setWebviewMarginTop(final View v){
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "alpha", 0.5f, 0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                if (currentValue == 1f) {
                    ((FrameLayout.LayoutParams) dealError.getLayoutParams()).topMargin=0;
                    dealError.requestLayout();
                    ((FrameLayout.LayoutParams) v.getLayoutParams()).topMargin=0;
                    v.requestLayout();
                    ((FrameLayout.LayoutParams) webview_progess.getLayoutParams()).topMargin=0;
                    webview_progess.requestLayout();
                    ((FrameLayout.LayoutParams) loading_rela.getLayoutParams()).topMargin=0;
                    loading_rela.requestLayout();
                }
            }
        });
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.5f, 0.5f,1.2f,0.8f,1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(animator).with(animator2);
        animSet.setDuration(500);
        animSet.setInterpolator(new AnticipateInterpolator(3.0f));
        animSet.start();

    }
    public void setStauToolbar(int stauToolbar) {
        setStauToolbar(stauToolbar, 0, true);
    }
    public void setStauToolbar(int stauToolbar,String bgcolor) {
        boolean issu = TextUtils.isEmpty(bgcolor);
        setStauToolbar(stauToolbar, issu ? 0 : Color.parseColor("#" + bgcolor), issu);
    }
    private void reSetText(){
        final boolean nocolor=color==0;
        final int baseColor = isResourColor?(getResources().getColor(nocolor?R.color.bg_gray:color)):color;
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                setToolbarTextColor(nocolor?R.color.black:R.color.white);
                toolbar.setBackgroundColor(baseColor);
                if (nocolor) {
                    tv_setShare.setBackgroundResource(R.drawable.btn_bg_gray);
                    tv_close.setBackgroundResource(R.drawable.btn_bg_gray);
                    tv_setShare.setTextColor(getResources().getColor(R.color.mainThemeColor));
                    tv_close.setTextColor(getResources().getColor(R.color.mainThemeColor));
                }else {
                    webview_bellow_line.setBackgroundColor(0);
                }
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ((View) mScrollable).getLayoutParams();
                lp.topMargin = toolbar.getHeight();
                ((View) mScrollable).requestLayout();
            }
        });

    }
    /**
     * @param stauToolbar 0正常,1上下滑,2全屏,3透明
     */
    private void setStauToolbar(int stauToolbar,int color,boolean isResourColor) {
        this.stauToolbar = stauToolbar;
        this.color = color;
        this.isResourColor = isResourColor;
        if (toolbar==null)
            return;
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
                toolbar.post(new Runnable() {
                    @Override
                    public void run() {
                        initTopBar3();
                    }
                });
                break;
        }
    }

    protected int getScreenHeight() {
        return getActivity().findViewById(android.R.id.content).getHeight();
    }

    protected void setToolbarTextColor(int color) {
        tv_title.setTextColor(getResources().getColor(color));
        tv_back.setTextColor(getResources().getColor(color));
        tv_setShare.setTextColor(getResources().getColor(color));
        tv_close.setTextColor(getResources().getColor(color));
        tv_setShare.setBackground(null);
        tv_close.setBackground(null);
    }


    private void initTopBar3(){
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ((View) mScrollable).getLayoutParams();
        lp.topMargin = 0;
        ((View) mScrollable).requestLayout();
        toolbar.bringToFront();
        toolbar.setBackground(null);
        webview_progess.setAlpha(0.8f);
        webview_progess.setVisibility(View.GONE);
        line_c.setBackground(null);
        webview_bellow_line.setBackground(null);
        setToolbarTextColor(R.color.transparent);
        tv_back.setTextColor(getResources().getColor(R.color.white));

        tv_setShare.setTextColor(getResources().getColor(R.color.white));
        tv_close.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (stauToolbar==3) {
            if (mScrollable instanceof ObservableWebView) {
                boolean nocolor=color==0;
                int baseColor = isResourColor?(getResources().getColor(nocolor?R.color.bg_gray:color)):color;
                int baseColor2 = nocolor?Color.BLACK:Color.WHITE;//getResources().getColor(nocolor?R.color.black:R.color.white);
                int baseColor3 = getResources().getColor(nocolor?R.color.gray_1:R.color.white);
                int baseColor4 = isResourColor?(getResources().getColor(nocolor?R.color.gray_1:color)):color;
                float alpha = Math.min(1, (float) scrollY / getResources().getDimensionPixelSize(R.dimen.parallax_image_height));
                toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
                line_c.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor3));
                webview_bellow_line.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor4));
                actionbar_title_image.setAlpha(alpha);

                tv_title.setTextColor(ScrollUtils.getColorWithAlpha(alpha, baseColor2));

                if (nocolor) {
                    tv_back.setTextColor(alpha == 0.0 ? Color.WHITE:ScrollUtils.getColorWithAlpha(alpha, Color.BLACK));
                    tv_setShare.setTextColor(alpha == 0.0 ? Color.WHITE:ScrollUtils.getColorWithAlpha(alpha, Color.BLACK));
                    tv_close.setTextColor(alpha == 0.0 ? Color.WHITE:ScrollUtils.getColorWithAlpha(alpha, Color.BLACK));
                    //starColorAnim(alpha);
                }

                //ViewHelper.setTranslationY((View) mScrollable, scrollY / 2);
            }
        }
    }

    private void starColorAnim(float alpha) {
        ObjectAnimator anim = ObjectAnimator.ofObject(tv_setShare, "color", new ColorEvaluator(),"#FFFFFF", "#000000");//白到黑
        ObjectAnimator anim2 = ObjectAnimator.ofObject(tv_setShare, "color", new ColorEvaluator(),"#000000","#FFFFFF");//黑到白

        if (alpha==0){
            anim2.setDuration(150);
            anim2.start();
        }else {
            if (tv_setShare.getCurrentTextColor() != getResources().getColor(R.color.black)) {
                anim.setDuration(150);
                anim.start();
            }
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
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
        ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(toolbar), toTranslationY).setDuration(150);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(toolbar, translationY);
                ViewHelper.setTranslationY((View) mScrollable, translationY);
                ViewHelper.setTranslationY(webview_progess, translationY);
                ViewHelper.setTranslationY(dealError, translationY);

                FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) ((View) mScrollable).getLayoutParams();
                lp2.height = (int) -translationY + getScreenHeight() - lp2.topMargin;
                ((View) mScrollable).requestLayout();

                //toolbar.setVisibility(toTranslationY == 0 ? View.VISIBLE : View.GONE);
            }
        });
        animator.start();
    }
}
