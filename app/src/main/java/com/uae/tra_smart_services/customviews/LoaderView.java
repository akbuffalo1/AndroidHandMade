package com.uae.tra_smart_services.customviews;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.uae.tra_smart_services.R;

/**
 * Created by ak-buffalo on 21.09.15.
 */
public class LoaderView extends View implements Animator.AnimatorListener {
    private static final String LOG_TAG = "LoaderView";
    public enum State{
        INITIALL(0), PROCESSING(1), FILLING(2), SUCCESS(3), CANCELLED(4), FAILURE(5);

        private int state;
        State(int _state){ state = _state; }
    }

    private Drawable mSrcDrawable;

    @ColorInt
    private int mBorderColor, mProcessBorderColor, mSuccessBorderColor, mSrcTintColor;

    private double mHexagonSide, mHexagonInnerRadius;
    private float mBorderSize, mProcessBorderSize, mSuccessBorderSize;

    private final int DEFAULT_HEXAGON_RADIUS = Math.round(30 * getResources().getDisplayMetrics().density);
    private final int HEXAGON_BORDER_COUNT = 6;
    private final double mHexagonSection = 2.0 * Math.PI / HEXAGON_BORDER_COUNT;

    private int mCenterWidth, mCenterHeight;

    private int mSuccessFigureWH = 90;
    private float mSuccessFigureOffsetX;
    private float mSuccessFigureOffsetY;

    private int mFailureFigureWH = 70;
    private int mFailureFigureOffsetX;
    private int mFailureFigureOffsetY;

    public State mAnimationState = State.INITIALL;
    private State mCurrentState;
    private boolean isInitLoading;

    private final Path mHexagonPath, successIconPath, dismissedIconPath;
    private final Paint mBorderPaint, mProcessPaint, mFillArePaint, mSuccessOrFailPaint;

    private ObjectAnimator animatorStart;
    private ObjectAnimator animatorEnd;
    private ObjectAnimator animatorFilling;
    private ObjectAnimator animatorSuccessOrFailed;
    private float mSuccessAnimationLength, mFailedAnimationLength;
    private float mProcessAnimationLength;
    private int mLoadingAnimPeriod, mFillingAnimPeriod, mStatusAnimPeriod;

    public LoaderView(Context context) {
        this(context, null);
    }

    public LoaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);
        mHexagonPath = new Path();
        successIconPath = new Path();
        dismissedIconPath = new Path();

        mBorderPaint = new Paint();
        mProcessPaint = new Paint();
        mFillArePaint = new Paint();
        mSuccessOrFailPaint = new Paint();

        initParams(context, attrs);
        initPaints();
        tintDrawableIfNeed();
    }

    private void initParams(Context context, AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LoaderView);
        try {
            mHexagonSide = array.getDimensionPixelSize(R.styleable.LoaderView_hexagonSideSize, DEFAULT_HEXAGON_RADIUS);
            mBorderColor = array.getColor(R.styleable.LoaderView_hexagonBorderColor, 0xFFC8C7C6);
            mProcessBorderColor = array.getColor(R.styleable.LoaderView_hexagonProcessBorderColor, 0xFFFFFFFF);
            mSuccessBorderColor = array.getColor(R.styleable.LoaderView_hexagonSuccessBorderColor, 0xFFFFFFFF);
            mBorderSize = array.getDimensionPixelSize(R.styleable.LoaderView_hexagonBorderSize, 3);
            mProcessBorderSize = array.getDimensionPixelSize(R.styleable.LoaderView_hexagonProcessBorderSize, 3);
            mSuccessBorderSize = array.getDimensionPixelSize(R.styleable.LoaderView_hexagonSuccessBorderSize, 5);
            mLoadingAnimPeriod = array.getInt(R.styleable.LoaderView_hexagonLoaderPeriod, 1500);
            mFillingAnimPeriod = array.getInt(R.styleable.LoaderView_hexagonFillingPeriod, 500);
            mStatusAnimPeriod = array.getInt(R.styleable.LoaderView_hexagonStatusPeriod, 500);
            mHexagonInnerRadius = Math.sqrt(3) * mHexagonSide / 2;
            mSrcDrawable = array.getDrawable(R.styleable.LoaderView_hexagonSrc);
            mSrcTintColor = array.getColor(R.styleable.LoaderView_hexagonSrcTintColor, Color.TRANSPARENT);
            isInitLoading = array.getBoolean(R.styleable.LoaderView_hexagonIsInitiallLoading, false);
        } finally {
            array.recycle();
        }
    }

    private void initPaints(){
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setAlpha(20);
        mBorderPaint.setStrokeWidth(mBorderSize);
        mBorderPaint.setStyle(Paint.Style.STROKE);

        mProcessPaint.setAntiAlias(true);
        mProcessPaint.setColor(mProcessBorderColor);
        mProcessPaint.setStrokeWidth(mProcessBorderSize);
        mProcessPaint.setStyle(Paint.Style.STROKE);

        mFillArePaint.setAntiAlias(true);
        mFillArePaint.setColor(mProcessBorderColor);
        mFillArePaint.setStrokeWidth(mBorderSize);
        mFillArePaint.setStyle(Paint.Style.FILL);

        mSuccessOrFailPaint.setAntiAlias(true);
        mSuccessOrFailPaint.setColor(mSuccessBorderColor);
        mSuccessOrFailPaint.setStrokeWidth(mSuccessBorderSize);
        mSuccessOrFailPaint.setStyle(Paint.Style.STROKE);
    }

    private void initPaths(){
        mProcessAnimationLength = new PathMeasure(mHexagonPath, false).getLength();
        mSuccessAnimationLength = new PathMeasure(successIconPath, false).getLength();
        mFailedAnimationLength = new PathMeasure(dismissedIconPath, false).getLength();
    }

    private void initAnimators(){
        animatorStart = ObjectAnimator.ofFloat(LoaderView.this, "phaseStart", 0.0f, 1.0f);
        animatorStart.setDuration(mLoadingAnimPeriod);
        animatorStart.setInterpolator(new DecelerateInterpolator(1.3f));
        animatorStart.setRepeatCount(ObjectAnimator.INFINITE);
        animatorStart.setRepeatMode(ObjectAnimator.RESTART);
        animatorStart.addListener(this);

        animatorEnd = ObjectAnimator.ofFloat(LoaderView.this, "phaseEnd", 0.0f, 1.0f);
        animatorEnd.setDuration(mLoadingAnimPeriod);
        animatorEnd.setInterpolator(new AccelerateInterpolator(0.7f));
        animatorEnd.setRepeatCount(ObjectAnimator.INFINITE);
        animatorEnd.setRepeatMode(ObjectAnimator.RESTART);
        animatorEnd.addListener(this);

        animatorFilling = ObjectAnimator.ofFloat(LoaderView.this, "phaseFilling", 0.0f, 255.0f);
        animatorFilling.setDuration(mFillingAnimPeriod);
        animatorFilling.setInterpolator(new DecelerateInterpolator());
        animatorFilling.addListener(this);

        animatorSuccessOrFailed = ObjectAnimator.ofFloat(LoaderView.this, "phaseSuccessOrFailure", 0.0f, 1.0f);
        animatorSuccessOrFailed.setDuration(mStatusAnimPeriod);
        animatorSuccessOrFailed.addListener(this);
    }

    public void init(int _color){
        mAnimationState = State.INITIALL;
        mSuccessOrFailPaint.setColor(_color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final float widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final float myWidth = Math.round(2 * mHexagonInnerRadius + mBorderSize);
        final float width;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(myWidth, widthSize);
        } else {
            width = myWidth;
        }
        setMeasuredDimension((int) width, (int) Math.round(2 * mHexagonSide + mBorderSize));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculatePaths(w, h);
        initPaths();
        initAnimators();
        initLoadingIfNeed();
    }

    private void calculatePaths(int w, int h){
        mCenterWidth = w / 2;
        mCenterHeight = h / 2;

        mSuccessFigureOffsetX = (float) (mCenterWidth -  mSuccessFigureWH / 2);
        mSuccessFigureOffsetY = (float) (mCenterHeight - mSuccessFigureWH * 0.4);

        mFailureFigureOffsetX = mCenterWidth - mFailureFigureWH / 2;
        mFailureFigureOffsetY = mCenterHeight - mFailureFigureWH / 2;

        mHexagonPath.reset();
        mHexagonPath.moveTo(
                (float) (mCenterWidth - mHexagonSide * Math.sin(0)),
                (float) (mCenterHeight - mHexagonSide * Math.cos(0)));
        for (int i = 1; i < HEXAGON_BORDER_COUNT; i++) {
            mHexagonPath.lineTo(
                    (float) (mCenterWidth - mHexagonSide * Math.sin(mHexagonSection * -i)),
                    (float) (mCenterHeight - mHexagonSide * Math.cos(mHexagonSection * -i)));
        }
        mHexagonPath.close();

        successIconPath.reset();
        successIconPath.moveTo(mSuccessFigureOffsetX, mSuccessFigureOffsetY + mSuccessFigureWH / 2);
        successIconPath.lineTo(mSuccessFigureOffsetX + (float) (mSuccessFigureWH * 0.4), mSuccessFigureOffsetY + (float) (mSuccessFigureWH * 0.8));
        successIconPath.lineTo(mSuccessFigureOffsetX + mSuccessFigureWH, mSuccessFigureOffsetY);

        dismissedIconPath.reset();
        dismissedIconPath.moveTo(mFailureFigureOffsetX, mFailureFigureOffsetY);
        dismissedIconPath.lineTo(mFailureFigureOffsetX + mFailureFigureWH, mFailureFigureOffsetY + mFailureFigureWH);
        dismissedIconPath.moveTo(mFailureFigureOffsetX + mFailureFigureWH, mFailureFigureOffsetY);
        dismissedIconPath.lineTo(mFailureFigureOffsetX, mFailureFigureOffsetY + mFailureFigureWH);
    }

    private void initLoadingIfNeed(){
        if(isInitLoading)
            startProcessing();
    }

    private void tintDrawableIfNeed() {
        if (mSrcDrawable != null && mSrcTintColor != Color.TRANSPARENT) {
            Drawable wrappedDrawable = DrawableCompat.wrap(mSrcDrawable);
            DrawableCompat.setTint(wrappedDrawable.mutate(), mSrcTintColor);
            mSrcDrawable = wrappedDrawable;
        }
    }

    public State getCurrentState(){
        return mCurrentState;
    }

    public boolean isInLoading(){
        return mAnimationState == State.PROCESSING;
    }

    public void setProgress(float progress){
        if(progress > 1.0f){
            return;
        }
        setAlpha(progress);
        mProcessPaint.setPathEffect(createLinearPathEffect(mProcessAnimationLength, progress));
        invalidate();
    }

    public void startProcessing(){
        setAlpha(1);
        mAnimationState = State.PROCESSING;
        animatorStart.start();
        animatorEnd.start();
    }

    public void stopProcessing(){
        setAlpha(0);
        mAnimationState = State.INITIALL;
        if(isInitLoading){
            animatorStart.cancel();
            animatorEnd.cancel();
        }
    }

    public void startFilling(final State _currentState){
        mAnimationState = State.FILLING;
        mCurrentState = _currentState;
        animatorFilling.start();
    }

    private void startDrawSuccessFigure() {
        mAnimationState = State.SUCCESS;
        animatorSuccessOrFailed.start();
    }

    private void startDrawFailureFigure() {
        mAnimationState = State.FAILURE;
        animatorSuccessOrFailed.start();
    }

    public void showState(State _state){
        mAnimationState = _state;
        invalidate();
    }

    private float phaseStart;
    /** It will be called by animator to draw the start of loading animation */
    public void setPhaseStart(float _phaseStart) {
        phaseStart = _phaseStart;
    }

    /** It will be called by animator to draw the end of loading animation */
    public void setPhaseEnd(float _phaseEnd) {
        mProcessPaint.setPathEffect(createProgressPathEffect(phaseStart, _phaseEnd));
        invalidate();
    }

    private PathEffect createProgressPathEffect(float _phaseStart, float _phaseEnd){
        float toEnd = mProcessAnimationLength - _phaseStart * mProcessAnimationLength;
        float fromStart = _phaseEnd * mProcessAnimationLength;
        float filled = mProcessAnimationLength - (toEnd + fromStart);
        return new DashPathEffect(
                new float[] { 0, fromStart, filled, toEnd }, 0
        );
    }

    /** It will be called by animator to fill the hexagon area smoothly after loading finished */
    public void setPhaseFilling(float _phaseFilling){
        mFillArePaint.setAlpha((int) _phaseFilling);
        invalidate();
    }

    /** It will be called by animator to draw failure figure on filled hexagon area*/
    public void setPhaseSuccessOrFailure(float _phaseSuccessOrFailure){
        mSuccessOrFailPaint.setPathEffect(createLinearPathEffect(
                mCurrentState == State.SUCCESS ? mSuccessAnimationLength : mFailedAnimationLength,
                _phaseSuccessOrFailure));
        invalidate();
    }

    private static PathEffect createLinearPathEffect(float _pathLength, float _phase) {
        return new DashPathEffect(
                new float[] { _phase * _pathLength, _pathLength}, 0
        );
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if(animation == animatorFilling && mAnimationState == State.FILLING){
            animatorStart.end();
            animatorEnd.end();
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (animatorFilling == animation && mAnimationState == State.FILLING) {
            switch (mCurrentState) {
                case SUCCESS:
                    startDrawSuccessFigure();
                    break;
                case CANCELLED:
                case FAILURE:
                    startDrawFailureFigure();
                    break;
            }
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) { /* Unimplemented method*/ }

    @Override
    public void onAnimationRepeat(Animator animation) { /* Unimplemented method*/ }

    @Override
    public void onDraw(Canvas _canvas) {
        super.onDraw(_canvas);
        switch (mAnimationState){
            case INITIALL: {
                _canvas.drawPath(mHexagonPath, mProcessPaint);
                if (mSrcDrawable != null) {
                    drawCenterInsideImage(_canvas, mSrcDrawable);
                }
            } break;
            case PROCESSING: {
                _canvas.drawPath(mHexagonPath, mBorderPaint);
                _canvas.drawPath(mHexagonPath, mProcessPaint);
                if (mSrcDrawable != null) {
                    drawCenterInsideImage(_canvas, mSrcDrawable);
                }
            } break;
            case FILLING: {
                _canvas.drawPath(mHexagonPath, mFillArePaint);
            } break;
            case SUCCESS: {
                _canvas.drawPath(mHexagonPath, mFillArePaint);
                _canvas.drawPath(successIconPath, mSuccessOrFailPaint);
            } break;
            case FAILURE: {
                _canvas.drawPath(mHexagonPath, mFillArePaint);
                _canvas.drawPath(dismissedIconPath, mSuccessOrFailPaint);
            } break;
        }
    }

    private void drawCenterImage(Canvas _canvas, Drawable _drawable) {
        float drawableWidth = _drawable.getMinimumWidth(), drawableHeight = _drawable.getMinimumHeight();

        float centerY = (float) (mHexagonSide + mBorderSize / 2f);
        float centerX = (float) getWidth() / 2;

        mSrcDrawable.setBounds((int) (centerX - drawableWidth / 2), (int) (centerY - drawableHeight / 2),
                (int) (centerX + drawableWidth / 2), (int) (centerY + drawableHeight / 2));
        _drawable.draw(_canvas);
    }

    private void drawCenterInsideImage(final Canvas _canvas, final Drawable _drawable) {
        float drawableWidth = _drawable.getMinimumWidth(), drawableHeight = _drawable.getMinimumHeight();
        float canvasWidth = _canvas.getWidth(), canvasHeight = _canvas.getHeight();

        final float scale;
        final float offsetX, offsetY;

        if (drawableWidth <= canvasWidth && drawableHeight <= canvasHeight) {
            drawCenterImage(_canvas, _drawable);
            return;
        } else if (drawableWidth <= drawableHeight) {
            scale = canvasHeight / drawableHeight;
        } else {
            scale = canvasWidth / drawableWidth;
        }
        offsetX = (canvasWidth - (drawableWidth * scale)) / 2;
        offsetY = (canvasHeight - (drawableHeight * scale)) / 2;
        _drawable.setBounds(0, 0, Math.round(drawableWidth), Math.round(drawableHeight));

        _canvas.save();
        _canvas.translate(Math.round(offsetX), Math.round(offsetY));
        _canvas.scale(scale, scale);
        _drawable.draw(_canvas);
        _canvas.restore();
    }
}


