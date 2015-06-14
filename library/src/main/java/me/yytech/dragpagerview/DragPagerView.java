package me.yytech.dragpagerview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Young on 15/6/9.
 */
public class DragPagerView extends FrameLayout implements View.OnTouchListener, View.OnDragListener {

    public static final int DURATION = 300;
    private static final int DISTANCE_LIMIT = 200;
    public OnMoreListener mOnMoreListener;
    public OnEmptyListener mOnEmptyListener;
    float mStartX = 0;
    float mStartY = 0;
    private SquareFrameLayout mFlDragView;
    private MyShadowBuilder mShadowBuilder;
    private SquareFrameLayout mFlDragView3;
    private SquareFrameLayout mFlDragView2;
    private SquareFrameLayout mFlDragView4;
    private boolean isDrop;
    private int mScreenWidth;
    private QueeAdapter mQueeAdapter;

    public DragPagerView(Context context) {
        super(context);
        init();
    }

    public DragPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnMoreListener(OnMoreListener pOnMoreListener) {
        mOnMoreListener = pOnMoreListener;
    }

    public void setOnEmptyListener(OnEmptyListener pOnEmptyListener) {
        mOnEmptyListener = pOnEmptyListener;
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_drag_pager_view, this);
        mFlDragView = (SquareFrameLayout) findViewById(R.id.flDragView);
        mFlDragView2 = (SquareFrameLayout) findViewById(R.id.flDragView2);
        mFlDragView3 = (SquareFrameLayout) findViewById(R.id.flDragView3);
        mFlDragView4 = (SquareFrameLayout) findViewById(R.id.flDragView4);
        mFlDragView.setOnTouchListener(this);
        mFlDragView.setOnDragListener(this);
        mFlDragView3.setScaleX(0.9f);
        mFlDragView3.setScaleY(0.9f);
        mFlDragView2.setScaleX(0.95f);
        mFlDragView2.setScaleY(0.95f);
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            mStartY = motionEvent.getY();
            mStartX = motionEvent.getX();
            ClipData data = ClipData.newPlainText("", "");
            mShadowBuilder = new MyShadowBuilder(view);
            mShadowBuilder.offsetX = motionEvent.getX();
            mShadowBuilder.offsetY = motionEvent.getY();
            view.startDrag(data, mShadowBuilder, view, 0);
            view.setAlpha(0f);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                final float dx = event.getX() - mStartX;
                final float dy = event.getY() - mStartY;
                double distance = Math.sqrt(dx * dx + dy * dy);
                distance = distance > DISTANCE_LIMIT ? DISTANCE_LIMIT : distance;
                final double percent = distance / (double) DISTANCE_LIMIT;
                double scale = (percent * 0.05d) + 0.95f;
                double scale2 = (percent * 0.05d) + 0.9f;
                scale = scale > 1f ? 1f : scale;
                mFlDragView2.setScaleY((float) scale);
                mFlDragView2.setScaleX((float) scale);
                mFlDragView2.setTranslationY((float) (-dp2px(getContext(), 15) * percent));
                mFlDragView3.setScaleY((float) scale2);
                mFlDragView3.setScaleX((float) scale2);
                mFlDragView3.setTranslationY((float) (-dp2px(getContext(), 15) * percent));
                isDrop = false;
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DROP:
                if (isDrop) {
                    break;
                }
                // Dropped, reassign View to ViewGroup
                View view = (View) event.getLocalState();
//                ViewGroup owner = (ViewGroup) view.getParent();
//                owner.removeView(view);
//                LinearLayout container = (LinearLayout) v;
//                container.addView(view);
                view.setVisibility(View.VISIBLE);
                mFlDragView.setVisibility(View.VISIBLE);
                mFlDragView.setAlpha(1.0f);
                final float dxEnd = event.getX() - mStartX;
                final float dyEnd = event.getY() - mStartY;
                double distanceEnd = Math.sqrt(dxEnd * dxEnd + dyEnd * dyEnd);
                if (distanceEnd < 350) {
                    // 动画回去
                    {
                        ObjectAnimator.ofFloat(mFlDragView, TRANSLATION_X, dxEnd, 0).setDuration(DURATION).start();
                        ObjectAnimator.ofFloat(mFlDragView, TRANSLATION_Y, dyEnd, 0).setDuration(DURATION).start();
                    }
                    {
                        ObjectAnimator.ofFloat(mFlDragView2, View.SCALE_X, mFlDragView2.getScaleX(), 0.95f).setDuration(DURATION).start();
                        ObjectAnimator.ofFloat(mFlDragView2, View.SCALE_Y, mFlDragView2.getScaleY(), 0.95f).setDuration(DURATION).start();
                        ObjectAnimator.ofFloat(mFlDragView2, View.TRANSLATION_Y, mFlDragView2.getTranslationY(), 0f).setDuration(DURATION).start();
                    }
                    {
                        ObjectAnimator.ofFloat(mFlDragView3, View.SCALE_X, mFlDragView3.getScaleX(), 0.9f).setDuration(DURATION).start();
                        ObjectAnimator.ofFloat(mFlDragView3, View.SCALE_Y, mFlDragView3.getScaleY(), 0.9f).setDuration(DURATION).start();
                        ObjectAnimator.ofFloat(mFlDragView3, View.TRANSLATION_Y, mFlDragView3.getTranslationY(), 0f).setDuration(DURATION).start();
                    }
                } else {
                    mFlDragView.setTranslationY(dyEnd);
                    // 动画出去
                    ObjectAnimator animator;
                    if (dxEnd < 0) {
                        animator = ObjectAnimator
                                .ofFloat(mFlDragView, TRANSLATION_X, dxEnd, -mFlDragView.getRight())
                                .setDuration(DURATION);
                        animator
                                .addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        changeView();
                                        resetView();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });
                    } else {
                        animator = ObjectAnimator
                                .ofFloat(mFlDragView, TRANSLATION_X, dxEnd, mScreenWidth - mFlDragView.getLeft())
                                .setDuration(DURATION);
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                resetView();
                                changeView();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }
                    animator.start();
                }
                mStartX = 0;
                mStartY = 0;
                isDrop = true;
                break;
            default:
                break;
        }
        return true;
    }

    private void changeView() {
        View view2;
        if (mFlDragView2.getChildCount() > 0) {
            view2 = mFlDragView2.getChildAt(0);
            mFlDragView.setVisibility(View.VISIBLE);
            mFlDragView.removeAllViews();
            mFlDragView2.removeAllViews();
            mFlDragView.addView(view2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            mFlDragView.setVisibility(View.GONE);
            if (mOnEmptyListener != null) {
                mOnEmptyListener.onEmpty();
            }
        }
        View view3;
        if (mFlDragView3.getChildCount() > 0) {
            view3 = mFlDragView3.getChildAt(0);
            mFlDragView2.setVisibility(View.VISIBLE);
            mFlDragView3.removeAllViews();
            mFlDragView2.addView(view3, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            mFlDragView2.setVisibility(View.GONE);
        }
        View newView = mQueeAdapter.getNewView();
        if (newView != null) {
            mFlDragView3.addView(newView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFlDragView3.setVisibility(View.VISIBLE);
            mFlDragView4.setVisibility(View.VISIBLE);
        } else {
            mFlDragView3.setVisibility(View.GONE);
            mFlDragView4.setVisibility(View.GONE);
            if (mOnMoreListener != null) {
                mOnMoreListener.onMore();
            }
        }
    }

    private void resetView() {
        mFlDragView.setTranslationX(0f);
        mFlDragView.setTranslationY(0f);
        mFlDragView.setScaleX(1.0f);
        mFlDragView.setScaleY(1.0f);
        mFlDragView2.setTranslationX(0f);
        mFlDragView2.setTranslationY(0f);
        mFlDragView2.setScaleX(0.95f);
        mFlDragView2.setScaleY(0.95f);
        mFlDragView3.setTranslationX(0f);
        mFlDragView3.setTranslationY(0f);
        mFlDragView3.setScaleX(0.9f);
        mFlDragView3.setScaleY(0.9f);
    }

    public int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void setQueeAdapter(QueeAdapter pQueeAdapter) {
        mQueeAdapter = pQueeAdapter;
        {
            View newView = mQueeAdapter.getNewView();
            if (newView != null) {
                mFlDragView.addView(newView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        {
            View newView = mQueeAdapter.getNewView();
            if (newView != null) {
                mFlDragView2.addView(newView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        {
            View newView = mQueeAdapter.getNewView();
            if (newView != null) {
                mFlDragView3.addView(newView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
    }

    /**
     * 数据发生改变
     */
    public void notifyDataChange() {
        {
            if (mFlDragView.getChildCount() == 0) {
                View newView = mQueeAdapter.getNewView();
                if (newView != null) {
                    mFlDragView.addView(newView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }
        }
        {
            if (mFlDragView2.getChildCount() == 0) {
                View newView = mQueeAdapter.getNewView();
                if (newView != null) {
                    mFlDragView2.addView(newView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }
        }
        {
            if (mFlDragView3.getChildCount() == 0) {
                View newView = mQueeAdapter.getNewView();
                if (newView != null) {
                    mFlDragView3.addView(newView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }
        }
    }

    public interface OnEmptyListener {
        void onEmpty();
    }

    public interface OnMoreListener {
        void onMore();
    }

    private class MyShadowBuilder extends DragShadowBuilder {

        private float offsetX;

        private float offsetY;

        public MyShadowBuilder(View view) {
            super(view);
        }

        public void setOffsetX(int pOffsetX) {
            offsetX = pOffsetX;
        }

        public void setOffsetY(int pOffsetY) {
            offsetY = pOffsetY;
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            final View view = getView();
            if (view != null) {
                shadowSize.set(view.getWidth(), view.getHeight());
                shadowTouchPoint.offset(((int) offsetX), ((int) offsetY));
            } else {
                Log.e(View.VIEW_LOG_TAG, "Asked for drag thumb metrics but no view");
            }
        }
    }
}
