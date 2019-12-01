package com.hackson.skdbc.dialog;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.hackson.skdbc.R;

public class LoadingSpinView extends AppCompatImageView {
    private float mRotateDegrees;
    private int mFrameTime;
    private boolean mNeedToUpdateView;
    private Runnable mUpdateViewRunnable;

    public LoadingSpinView(Context context) {
        super(context);
        this.init();
    }

    public LoadingSpinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        this.setImageResource(R.mipmap.loading);
        this.mFrameTime = 83;
        this.mUpdateViewRunnable = new Runnable() {
            @Override
            public void run() {
                mRotateDegrees = mRotateDegrees + 30.0F;
                mRotateDegrees = mRotateDegrees < 360.0F ? mRotateDegrees : mRotateDegrees - 360.0F;
                invalidate();
                if (mNeedToUpdateView) {
                    postDelayed(this, (long) mFrameTime);
                }

            }
        };
    }

    public void setAnimationSpeed(float scale) {
        this.mFrameTime = (int) (83.0F / scale);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(this.mRotateDegrees, (float) (this.getWidth() / 2), (float) (this.getHeight() / 2));
        super.onDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mNeedToUpdateView = true;
        this.post(this.mUpdateViewRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        this.mNeedToUpdateView = false;
        super.onDetachedFromWindow();
    }
}
