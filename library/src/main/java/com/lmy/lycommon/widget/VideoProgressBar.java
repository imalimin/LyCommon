package com.lmy.lycommon.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.lmy.lycommon.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李明艺 on 2016/3/9.
 *
 * @author lrlmy@foxmail.com
 */
public class VideoProgressBar extends View {
    public final static String TAG = "ProgressView";
    private Paint mPaint;
    private int backgroundColor;
    private int progressColor;
    private int interruptColor;

    private int max;
    private List<Integer> interrupts;
    private int progress;
    private int limit;
    private int mWidth;
    private int mHeight;

    private float eachProgressPx;
    private int interruptSize;
    private boolean showInterrupt;

    public VideoProgressBar(Context context) {
        super(context);
        init();
    }

    public VideoProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyStyle(context, attrs, 0, 0);
        init();
    }

    public VideoProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyStyle(context, attrs, defStyleAttr, 0);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        a.recycle();
        requestLayout();
    }

    private void init() {
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        this.progress = 0;
        this.interrupts = new ArrayList<>(0);
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
        eachProgressPx = width / (float) max;
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
            for (int i : interrupts)
                canvas.drawRect(i * eachProgressPx - interruptSize, 0, i * eachProgressPx, getHeight(), mPaint);
        }
        if (limit > 0) {
            mPaint.setColor(progressColor);
            canvas.drawRect(limit * eachProgressPx - interruptSize, 0, limit * eachProgressPx, getHeight(), mPaint);
        }
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        if (getWidth() > 0)
            this.eachProgressPx = getWidth() / max;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setProgress(int progress) {
        if (progress > max) return;
        this.progress = progress;
        invalidate();
    }

    public void interrupt() {
        interrupts.add(progress);
        invalidate();
    }
}
