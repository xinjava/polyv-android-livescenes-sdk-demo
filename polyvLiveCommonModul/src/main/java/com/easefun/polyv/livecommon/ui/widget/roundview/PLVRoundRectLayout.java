package com.easefun.polyv.livecommon.ui.widget.roundview;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.RelativeLayout;

import com.easefun.polyv.livecommon.R;
import com.plv.thirdpart.blankj.utilcode.util.ScreenUtils;


//need default bg
public class PLVRoundRectLayout extends RelativeLayout {

    private Path mPath;
    private int mRadius;

    private int mWidth;
    private int mHeight;
    private int mLastRadius;

    public static final int MODE_NONE = 0;
    public static final int MODE_ALL = 1;
    public static final int MODE_LEFT = 2;
    public static final int MODE_TOP = 3;
    public static final int MODE_RIGHT = 4;
    public static final int MODE_BOTTOM = 5;

    private int mRoundMode = MODE_ALL;

    private OnOrientationChangedListener onOrientationChangedListener;

    public PLVRoundRectLayout(Context context) {
        this(context, null);
    }

    public PLVRoundRectLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PLVRoundRectLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PLVRoundRectLayout, defStyleAttr, 0);
        int radius = a.getDimensionPixelSize(R.styleable.PLVRoundRectLayout_radius, 10);
        int mode = a.getInt(R.styleable.PLVRoundRectLayout_mode, MODE_ALL);
        a.recycle();

        mRoundMode = mode;
        setCornerRadius(radius);
        init();

        if (Build.VERSION.SDK_INT >= 21 && mRoundMode == MODE_ALL) {
            mRoundMode = MODE_NONE;
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        outline.setRoundRect(0, 0, getWidth(), getHeight(), mRadius);
                    }
                }
            });
            setClipToOutline(true);
        }
    }

    private void init() {
        /**
         * ///暂时保留该代码
         * setBackgroundDrawable(new ColorDrawable(0x33ff0000));
         * setCornerRadius(10);
         */
        mPath = new Path();
        mPath.setFillType(Path.FillType.EVEN_ODD);

    }

    /**
     * 设置是否圆角裁边
     *
     * @param roundMode
     */
    public void setRoundMode(int roundMode) {
        mRoundMode = roundMode;
    }

    /**
     * 设置圆角半径
     *
     * @param radius
     */
    public void setCornerRadius(int radius) {
        mRadius = radius;
    }

    /**
     * 设置屏幕旋转变化监听
     * @param li 监听器
     */
    public void setOnOrientationChangedListener(OnOrientationChangedListener li) {
        this.onOrientationChangedListener = li;
        li.onChanged(ScreenUtils.isPortrait());
    }

    private void checkPathChanged() {

        if (getWidth() == mWidth && getHeight() == mHeight && mLastRadius == mRadius) {
            return;
        }

        mWidth = getWidth();
        mHeight = getHeight();
        mLastRadius = mRadius;

        mPath.reset();

        switch (mRoundMode) {
            case MODE_ALL:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight), mRadius, mRadius, Path.Direction.CW);
                break;
            case MODE_LEFT:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight),
                        new float[]{mRadius, mRadius, 0, 0, 0, 0, mRadius, mRadius},
                        Path.Direction.CW);
                break;
            case MODE_TOP:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight),
                        new float[]{mRadius, mRadius, mRadius, mRadius, 0, 0, 0, 0},
                        Path.Direction.CW);
                break;
            case MODE_RIGHT:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight),
                        new float[]{0, 0, mRadius, mRadius, mRadius, mRadius, 0, 0},
                        Path.Direction.CW);
                break;
            case MODE_BOTTOM:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight),
                        new float[]{0, 0, 0, 0, mRadius, mRadius, mRadius, mRadius},
                        Path.Direction.CW);
                break;
            default:
                break;
        }

    }

    @Override
    public void draw(Canvas canvas) {

        if (mRoundMode != MODE_NONE) {
            int saveCount = canvas.save();

            checkPathChanged();

            canvas.clipPath(mPath);
            super.draw(canvas);

            canvas.restoreToCount(saveCount);
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (onOrientationChangedListener != null) {
            onOrientationChangedListener.onChanged(newConfig.orientation != Configuration.ORIENTATION_LANDSCAPE);
        }
    }

    public interface OnOrientationChangedListener {
        void onChanged(boolean isPortrait);
    }
}