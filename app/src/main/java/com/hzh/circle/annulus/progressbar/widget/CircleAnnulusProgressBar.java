package com.hzh.circle.annulus.progressbar.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.hzh.circle.annulus.progressbar.R;

import java.util.ArrayList;

/**
 * Package: com.hzh.circle.progressbar
 * FileName: CircleAnnulusProgressBar
 * Date: on 2017/12/30  下午3:15
 * Auther: zihe
 * Descirbe: 仿微博圆环饼形进度进度条
 * Email: hezihao@linghit.com
 */

public class CircleAnnulusProgressBar extends View {
    /**
     * 默认的当前进度，默认为0
     */
    private final int DEFAULT_PROGRESS = 0;
    /**
     * 默认的最大值，默认为100
     */
    private final int DEFAULT_MAX = 100;
    /**
     * 默认的外圆轮廓宽度，默认是1dp
     */
    private int DEFAULT_OUTER_CIRCLE_BORDER_WIDTH = 3;

    /**
     * 默认的外圆轮廓宽度，默认是1dp
     */
    private float mOuterCircleBorderWidth;
    /**
     * 默认的外圆的颜色
     */
    private final int DEFAULT_OUTER_CIRCLE_COLOR = Color.parseColor("#FFFFFF");
    /**
     * 默认的内饼形的颜色
     */
    private final int DEFAULT_PIE_CIRCLE = Color.parseColor("#FFFFFF");
    /**
     * 外圆的画笔
     */
    private Paint mOuterCirclePaint;
    /**
     * 内部的饼形的画笔
     */
    private Paint mPiePaint;
    /**
     * 外圆的颜色，默认白色
     */
    private int mOuterCircleColor = DEFAULT_OUTER_CIRCLE_COLOR;
    /**
     * 内饼图填充的颜色，默认是白色
     */
    private int mPieColor = DEFAULT_PIE_CIRCLE;
    /**
     * View的宽，包括padding
     */
    private int mWidth;
    /**
     * View的高，包括padding
     */
    private int mHeight;
    /**
     * View绘制区域，去除了padding
     */
    private RectF mRect;
    /**
     * 外圆的半径，已经处理了padding
     */
    private float mRadius;
    /**
     * 当前进度，默认为最大值
     */
    private int mProgress = DEFAULT_PROGRESS;
    /**
     * 设置的最大进度，默认为100
     */
    private int mMax = DEFAULT_MAX;
    /**
     * 外圆的轮廓宽度
     */
    private float outerCircleBorderWidth;
    /**
     * padding值
     */
    private int mPaddingTop;
    private int mPaddingBottom;
    private int mPaddingLeft;
    private int mPaddingRight;
    /**
     * 监听器集合
     */
    private ArrayList<OnProgressUpdateListener> mListeners;

    public CircleAnnulusProgressBar(Context context) {
        super(context);
        init(null);
    }

    public CircleAnnulusProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CircleAnnulusProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        //设置默认的外圆轮廓宽度
        DEFAULT_OUTER_CIRCLE_BORDER_WIDTH = dip2px(getContext(), 1f);
        mOuterCircleBorderWidth = DEFAULT_OUTER_CIRCLE_BORDER_WIDTH;

        //取出Xml设置的自定义属性，当前进度，最大进度
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CircleAnnulusProgressBar);
            //Xml设置的进度
            mProgress = array.getInt(R.styleable.CircleAnnulusProgressBar_progress, DEFAULT_PROGRESS);
            //Xml设置的最大值
            mMax = array.getInt(R.styleable.CircleAnnulusProgressBar_max, DEFAULT_MAX);
            //Xml设置的外圆颜色，先读取直接写#FFFFFF等样式的，如果没有，则读取使用引用方式的，就是@color/white这样的
            int resultOuterCircleColor = array.getColor(R.styleable.CircleAnnulusProgressBar_outer_circle_color, DEFAULT_OUTER_CIRCLE_COLOR);
            if (resultOuterCircleColor != DEFAULT_OUTER_CIRCLE_COLOR) {
                mOuterCircleColor = resultOuterCircleColor;
            } else {
                int outerCircleResId = array.getResourceId(R.styleable.CircleAnnulusProgressBar_outer_circle_color, android.R.color.white);
                mOuterCircleColor = getContext().getResources().getColor(outerCircleResId);
            }
            //Xml设置的内饼图颜色，同上，先读取直接写颜色值的，没有再读取使用引用方式的
            int resultPieCircleColor = array.getColor(R.styleable.CircleAnnulusProgressBar_pie_color, DEFAULT_PIE_CIRCLE);
            if (resultPieCircleColor != DEFAULT_PIE_CIRCLE) {
                mPieColor = resultPieCircleColor;
            } else {
                int pieColorResId = array.getResourceId(R.styleable.CircleAnnulusProgressBar_pie_color, android.R.color.white);
                mPieColor = getContext().getResources().getColor(pieColorResId);
            }
            //读取设置的外圆轮廓宽度，读取dimension
            mOuterCircleBorderWidth = array.getDimensionPixelSize(R.styleable.CircleAnnulusProgressBar_outer_circle_border_width, DEFAULT_OUTER_CIRCLE_BORDER_WIDTH);
            //记得回收资源
            array.recycle();
        }
        //外层圆的画笔
        mOuterCirclePaint = new Paint();
        mOuterCirclePaint.setColor(mOuterCircleColor);
        mOuterCirclePaint.setStyle(Paint.Style.STROKE);
        mOuterCirclePaint.setStrokeWidth(mOuterCircleBorderWidth);
        mOuterCirclePaint.setAntiAlias(true);
        //中间进度饼形画笔
        mPiePaint = new Paint();
        mPiePaint.setColor(mPieColor);
        mPiePaint.setStyle(Paint.Style.FILL);
        mPiePaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //取出总宽高
        mWidth = w;
        mHeight = h;
        //取出设置的padding值
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();
        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        //计算外圆直径，取宽高中最小的为圆的直径，这里要处理添加padding的情况。
        float diameter = (Math.min(mWidth, mHeight)) - mPaddingLeft - mPaddingRight;
        //直径除以2算出半径
        mRadius = (float) ((diameter / 2) * 0.98);

        //建立一个Rect保存View的范围，后面画饼形也需要用到
        mRect = new RectF(mPaddingLeft, mPaddingTop, mWidth - mPaddingRight, mHeight - mPaddingBottom);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //取出宽的模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        //取出高的模式和大小
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //设置的宽高不相等时，将宽高都进行校正，取最小的为标准
        if (widthSize != heightSize) {
            int finalSize = Math.min(widthSize, heightSize);
            widthSize = finalSize;
            heightSize = finalSize;
        }

        //默认宽高值
        int defaultWidth = dip2px(getContext(), 55);
        int defaultHeight = dip2px(getContext(), 55);

        if (widthMode != MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            //当宽高都设置wrapContent时设置我们的默认值
            if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(defaultWidth, defaultHeight);
            } else if (widthMode == MeasureSpec.AT_MOST) {
                //宽、高任意一个为wrapContent都设置我们默认值
                setMeasuredDimension(defaultWidth, heightSize);
            } else if (heightMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(widthSize, defaultHeight);
            }
        } else {
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取当前的进度
        int curProgress = getProgress();
        //越界处理
        if (curProgress < 0) {
            curProgress = 0;
        }
        if (curProgress > mMax) {
            curProgress = mMax;
        }
        //画外圆，这里使用宽和高都可以，因为我们限定宽和高都是相等的
        //这里圆心坐标一直都在View的宽高的中间，就算有padding都是不会变的，变的只是半径，半径初始化前已经去处理了padding，这里要注意
        canvas.drawCircle(mWidth / 2, mWidth / 2, mRadius, mOuterCirclePaint);
        //要进行画布缩放操作，先保存图层，因为缩放、平移等操作是叠加的，所以使用完必须恢复，否则下次的onDraw就会累加缩放
        canvas.save();
        //用缩放画布，进行缩放中心的饼图，设置缩放中心是控件的中心
        canvas.scale(0.90f, 0.90f, mWidth / 2, mHeight / 2);
        //计算当前进度对应的角度
        float angle = 360 * (curProgress * 1.0f / getMax());
        //画饼图，-90度就是12点方向开始
        canvas.drawArc(mRect, -90, angle, true, mPiePaint);
        //还原画布图层
        canvas.restore();
        //回调进度给外面的监听器
        for (OnProgressUpdateListener listener : mListeners) {
            listener.onProgressUpdate(curProgress);
        }
    }

    /**
     * 设置进度
     *
     * @param progress 要设置的进度
     */
    public void setProgress(int progress) {
        if (progress < 0) {
            progress = 0;
        }
        this.mProgress = progress;
        postInvalidate();
    }

    /**
     * 获取当前进度
     *
     * @return 当前的进度
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * 设置最大值
     *
     * @param max 要设置的最大值
     */
    public void setMax(int max) {
        if (max < 0) {
            max = 0;
        }
        this.mMax = max;
        postInvalidate();
    }

    /**
     * 进度更新的回调监听
     */
    public interface OnProgressUpdateListener {
        //当进度更新时回调
        void onProgressUpdate(int progress);
    }

    /**
     * 设置更新回调
     *
     * @param listener 监听器实例
     */
    public void addOnProgressUpdateListener(OnProgressUpdateListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<OnProgressUpdateListener>();
        }
        this.mListeners.add(listener);
    }

    /**
     * 获取设置的最大值
     *
     * @return 设置的最大值
     */
    public int getMax() {
        return mMax;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }
}