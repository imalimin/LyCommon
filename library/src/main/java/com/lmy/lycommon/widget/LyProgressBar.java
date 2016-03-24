package com.lmy.lycommon.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.lmy.lycommon.R;

/**
 * Created by 李明艺 on 2016/1/20.
 *
 * @author lrlmy@foxmail.com
 */
public class LyProgressBar extends View {
    public final static String TAG = "ProgressView";
    private Paint mPaint;
    private int backgroundColor;
    private int progressColor;
    private int interruptColor;

    private int max;
    private int progress;
    private int mWidth;
    private int mHeight;

    private int eachProgressPx;
    private int interruptSize;
    private boolean showInterrupt;

    public LyProgressBar(Context context) {
        super(context);
        init();
    }

    public LyProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyStyle(context, attrs, 0, 0);
        init();
    }

    public LyProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyStyle(context, attrs, defStyleAttr, 0);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LyProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyStyle(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    protected void applyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressBar, defStyleAttr, defStyleRes);
        for (int i = 0, count = a.getIndexCount(); i < count; i++) {
            int attr = a.getIndex(i);

            if (attr == R.styleable.ProgressBar_max)
                max = a.getInt(attr, 0);
            else if (attr == R.styleable.ProgressBar_progress)
                progress = a.getInt(attr, 0);
            else if (attr == R.styleable.ProgressBar_interruptSize)
                interruptSize = a.getDimensionPixelSize(attr, 4);
            else if (attr == R.styleable.ProgressBar_showInterrupt)
                showInterrupt = a.getBoolean(attr, false);
            else if (attr == R.styleable.ProgressBar_backgroundColor)
                backgroundColor = a.getColor(attr, 0);
            else if (attr == R.styleable.ProgressBar_progressColor)
                progressColor = a.getColor(attr, 0);
            else if (attr == R.styleable.ProgressBar_interruptColor)
                interruptColor = a.getColor(attr, 0);
        }
        Log.v(TAG, "applyStyle backgroundColor=" + backgroundColor + ", progressColor=" + progressColor);
        a.recycle();
        requestLayout();
    }

    private void init() {
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        this.progress = 0;
        this.max = 5;
        this.eachProgressPx = -1;
    }

    private void measureMonthView(int widthMeasureSpec, int heightMeasureSpec) {
        //在View初始化之前首先定义View的基本大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = -1;
        int height = -1;

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                width = Math.min(width, widthSize);
                break;
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
        }

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                height = Math.min(height, heightSize);
                break;
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
        }

        mWidth = width;
        mHeight = height;
        eachProgressPx = width / max;
//        Log.v(TAG, "measureMonthView mWidth=" + mWidth + ", mHeight=" + mHeight);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureMonthView(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(backgroundColor);
        mPaint.setColor(progressColor);
        canvas.drawRect(0, 0, eachProgressPx * progress, getHeight(), mPaint);
        if (showInterrupt) {
            mPaint.setColor(interruptColor);
            for (int i = 1; i < max; i++)
                canvas.drawRect(i * eachProgressPx - interruptSize / 2, 0, i * eachProgressPx + interruptSize / 2, getHeight(), mPaint);
        }
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

}
