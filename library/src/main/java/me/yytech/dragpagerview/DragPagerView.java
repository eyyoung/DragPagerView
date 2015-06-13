package me.yytech.dragpagerview;

import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Young on 15/6/9.
 */
public class DragPagerView extends FrameLayout implements View.OnTouchListener, View.OnDragListener {


    private static final int DISTANCE_LIMIT = 200;
    private View mFlDragView;
    private MyShadowBuilder mShadowBuilder;
    private View mFlDragView3;
    private TextView mTvTest;
    private View mFlDragView2;
    private boolean isDrop;

    public DragPagerView(Context context) {
        super(context);
        init();
    }

    public DragPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_drag_pager_view, this);
        mFlDragView = findViewById(R.id.flDragView);
        mFlDragView2 = findViewById(R.id.flDragView2);
        mFlDragView3 = findViewById(R.id.flDragView3);
        mFlDragView.setOnTouchListener(this);
        mFlDragView.setOnDragListener(this);
        mFlDragView3.setScaleX(0.9f);
        mFlDragView3.setScaleY(0.9f);
        mFlDragView2.setScaleX(0.95f);
        mFlDragView2.setScaleY(0.95f);
        mTvTest = (TextView) findViewById(R.id.tvTest);
        final int padding = dp2px(getContext(), 15);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            mShadowBuilder = new MyShadowBuilder(view);
            mShadowBuilder.offsetX = motionEvent.getX();
            mShadowBuilder.offsetY = motionEvent.getY();
            view.startDrag(data, mShadowBuilder, view, 0);
//            view.setVisibility(View.INVISIBLE);
            view.setAlpha(0f);
            return true;
        } else {
            return false;
        }
    }

    float mStartX = 0;
    float mStartY = 0;

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                if (mStartY == 0) {
                    mStartY = event.getY();
                }
                if (mStartX == 0) {
                    mStartX = event.getX();
                }
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
            case DragEvent.ACTION_DRAG_EXITED:
            case DragEvent.ACTION_DRAG_ENDED:
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
                Log.e("TEST", dxEnd + " " + dyEnd);
                if (distanceEnd < 500) {
                    // 动画回去
                    {
                        ObjectAnimator.ofFloat(mFlDragView, TRANSLATION_X, dxEnd, 0).setDuration(1000).start();
                        ObjectAnimator.ofFloat(mFlDragView, TRANSLATION_Y, dyEnd, 0).setDuration(1000).start();
                    }
                    {
                        ObjectAnimator.ofFloat(mFlDragView2, View.SCALE_X, mFlDragView2.getScaleX(), 0.95f).setDuration(1000).start();
                        ObjectAnimator.ofFloat(mFlDragView2, View.SCALE_Y, mFlDragView2.getScaleY(), 0.95f).setDuration(1000).start();
                        ObjectAnimator.ofFloat(mFlDragView2, View.TRANSLATION_Y, mFlDragView2.getTranslationY(), 0f).setDuration(1000).start();
                    }
                    {
                        ObjectAnimator.ofFloat(mFlDragView3, View.SCALE_X, mFlDragView3.getScaleX(), 0.9f).setDuration(1000).start();
                        ObjectAnimator.ofFloat(mFlDragView3, View.SCALE_Y, mFlDragView3.getScaleY(), 0.9f).setDuration(1000).start();
                        ObjectAnimator.ofFloat(mFlDragView3, View.TRANSLATION_Y, mFlDragView3.getTranslationY(), 0f).setDuration(1000).start();
                    }
                } else {
                    // 动画出去
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

    private class MyShadowBuilder extends DragShadowBuilder {

        private float offsetX;

        private float offsetY;

        public void setOffsetX(int pOffsetX) {
            offsetX = pOffsetX;
        }

        public void setOffsetY(int pOffsetY) {
            offsetY = pOffsetY;
        }

        public MyShadowBuilder(View view) {
            super(view);
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

    public int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
