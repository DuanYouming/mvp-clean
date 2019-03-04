package com.foxconn.bandon.custom.viewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.foxconn.bandon.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class ConvenientBanner<T> extends RelativeLayout {
    private List<T> mDatas;
    private int[] page_indicatorId;
    private ArrayList<ImageView> mPointViews = new ArrayList<>();
    private CBPageAdapter pageAdapter;
    private CBLoopViewPager viewPager;
    private ViewGroup loPageTurningPoint;
    private long autoTurningTime = -1;
    private boolean turning;
    private boolean canTurn = false;
    private boolean canLoop = true;
    private CBLoopScaleHelper cbLoopScaleHelper;
    private CBPageChangeListener pageChangeListener;
    private OnPageChangeListener onPageChangeListener;
    private AdSwitchTask adSwitchTask;

    public enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
    }

    public ConvenientBanner(Context context) {
        super(context);
        init(context);
    }

    public ConvenientBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ConvenientBanner);
        canLoop = a.getBoolean(R.styleable.ConvenientBanner_canLoop, true);
        autoTurningTime = a.getInteger(R.styleable.ConvenientBanner_autoTurningTime, -1);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        View hView = LayoutInflater.from(context).inflate(R.layout.include_viewpager, this, true);
        viewPager = hView.findViewById(R.id.cbLoopViewPager);
        loPageTurningPoint = hView.findViewById(R.id.loPageTurningPoint);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        viewPager.setLayoutManager(linearLayoutManager);
        cbLoopScaleHelper = new CBLoopScaleHelper();
        adSwitchTask = new AdSwitchTask(this);
    }

    public ConvenientBanner setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        viewPager.setLayoutManager(layoutManager);
        return this;
    }

    public ConvenientBanner setPages(CBViewHolderCreator holderCreator, List<T> datas) {
        this.mDatas = datas;
        pageAdapter = new CBPageAdapter(holderCreator, mDatas, canLoop);
        viewPager.setAdapter(pageAdapter);

        if (page_indicatorId != null) {
            setPageIndicator(page_indicatorId);
        }

        cbLoopScaleHelper.setFirstItemPos(canLoop ? mDatas.size() : 0);
        cbLoopScaleHelper.attachToRecyclerView(viewPager);

        return this;
    }

    public ConvenientBanner setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        pageAdapter.setCanLoop(canLoop);
        notifyDataSetChanged();
        return this;
    }

    public boolean isCanLoop() {
        return canLoop;
    }


    /**
     * 通知数据变化
     */
    public void notifyDataSetChanged() {
        viewPager.getAdapter().notifyDataSetChanged();
        if (page_indicatorId != null) {
            setPageIndicator(page_indicatorId);
        }
        cbLoopScaleHelper.setCurrentItem(canLoop ? mDatas.size() : 0);
    }

    /**
     * 设置底部指示器是否可见
     *
     * @param visible
     */
    public ConvenientBanner setPointViewVisible(boolean visible) {
        loPageTurningPoint.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public ConvenientBanner setPageIndicator(int[] page_indicatorId) {
        loPageTurningPoint.removeAllViews();
        mPointViews.clear();
        this.page_indicatorId = page_indicatorId;
        if (mDatas == null) {
            return this;
        }
        for (int count = 0; count < mDatas.size(); count++) {
            // 翻页指示的点
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(5, 0, 5, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            pointView.setLayoutParams(params);
            if (cbLoopScaleHelper.getFirstItemPos() % mDatas.size() == count)
                pointView.setImageResource(page_indicatorId[1]);
            else
                pointView.setImageResource(page_indicatorId[0]);
            mPointViews.add(pointView);
            loPageTurningPoint.addView(pointView);
        }
        pageChangeListener = new CBPageChangeListener(mPointViews,
                page_indicatorId);
        cbLoopScaleHelper.setOnPageChangeListener(pageChangeListener);
        if (onPageChangeListener != null)
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);

        return this;
    }

    public OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }


    public ConvenientBanner setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        if (pageChangeListener != null) {
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        } else {
            cbLoopScaleHelper.setOnPageChangeListener(onPageChangeListener);
        }
        return this;
    }


    public ConvenientBanner setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        if (onItemClickListener == null) {
            pageAdapter.setOnItemClickListener(null);
            return this;
        }
        pageAdapter.setOnItemClickListener(onItemClickListener);
        return this;
    }


    public int getCurrentItem() {
        return cbLoopScaleHelper.getRealCurrentItem();
    }

    public ConvenientBanner setCurrentItem(int position, boolean smoothScroll) {
        cbLoopScaleHelper.setCurrentItem(canLoop ? mDatas.size() + position : position, smoothScroll);
        return this;
    }

    public ConvenientBanner setFirstItemPos(int position) {
        cbLoopScaleHelper.setFirstItemPos(canLoop ? mDatas.size() + position : position);
        return this;
    }

    public ConvenientBanner setPageIndicatorAlign(PageIndicatorAlign align) {
        LayoutParams layoutParams = (LayoutParams) loPageTurningPoint.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
        loPageTurningPoint.setLayoutParams(layoutParams);
        return this;
    }


    public boolean isTurning() {
        return turning;
    }

    public ConvenientBanner startTurning(long autoTurningTime) {
        if (autoTurningTime < 0) return this;
        if (turning) {
            stopTurning();
        }
        canTurn = true;
        this.autoTurningTime = autoTurningTime;
        turning = true;
        postDelayed(adSwitchTask, autoTurningTime);
        return this;
    }

    public ConvenientBanner startTurning() {
        startTurning(autoTurningTime);
        return this;
    }


    public void stopTurning() {
        turning = false;
        removeCallbacks(adSwitchTask);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            if (canTurn) {
                startTurning(autoTurningTime);
            }
        } else if (action == MotionEvent.ACTION_DOWN) {
            if (canTurn) {
                stopTurning();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    static class AdSwitchTask implements Runnable {

        private final WeakReference<ConvenientBanner> reference;

        AdSwitchTask(ConvenientBanner convenientBanner) {
            this.reference = new WeakReference<ConvenientBanner>(convenientBanner);
        }

        @Override
        public void run() {
            ConvenientBanner convenientBanner = reference.get();

            if (convenientBanner != null) {
                if (convenientBanner.viewPager != null && convenientBanner.turning) {
                    int page = convenientBanner.cbLoopScaleHelper.getCurrentItem() + 1;
                    convenientBanner.cbLoopScaleHelper.setCurrentItem(page, true);
                    convenientBanner.postDelayed(convenientBanner.adSwitchTask, convenientBanner.autoTurningTime);
                }
            }
        }
    }
}
