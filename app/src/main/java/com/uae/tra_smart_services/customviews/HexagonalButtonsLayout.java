package com.uae.tra_smart_services.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.util.HexagonUtils;
import com.uae.tra_smart_services.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Mikazme on 13/08/2015.
 */
public class HexagonalButtonsLayout extends View {

    private OnServiceSelected mServiceSelectedListener;

    private Paint mSeparatorPaint;
    private Paint mButtonPaint;
//    private Paint mShadowPaint;
    private Paint mButtonSecondColorPaint;
    private Paint mPressedButtonPaint;
    private Paint mOrangeTextPaint;
    private Paint mWhiteTextPain;
    private Path mSeparatorPath;
    private int mSeparatorColor;
    private int mPressedButtonColor;
    private int mButtonsCount;
    private float mHexagonGapWidth;
    private float mSeparatorStrokeWidth;
    private float mTriangleHeight;
    private float mRadius;
    private float mSeparatorTriangleHeight;
    private float mSeparatorRadius;
    private float mTextSize, mTextSizeDifference;
    private Integer mOldWidth;

    private boolean mIsTutorialMode;

    private List<PointF> mCenters;
    private List<Drawable> mDrawables;
    private List<PointF[]> mPolygons;

    private boolean isDown = false;
    private Integer mPressedButton;

    private float mStartGapWidth, mGapWidthDifference;
    private float mAnimationProgress = 0.0f;


    private final int DOUBLE_BUTTONS = 8;
    private final int HALF_BUTTONS = 2;
    private float mTwoDivSquare;
    private float mMaxDrawableHeight;

    private final int textColorByTheme = calculateColorByTheme(getContext(), R.attr.themeMainColor);

    public HexagonalButtonsLayout(final Context _context, final AttributeSet _attrs) {
        super(_context, _attrs);
        initProperties(_attrs);
        initPaint();
        initDrawables();
//        setLayerType();
    }

//    private void setLayerType() {
//        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//        if (currentapiVersion <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }
//    }

    public final PointF[] getCenters() {
        return mCenters.toArray(new PointF[mCenters.size()]);
    }

    public final float getStartPoint() {
        final int direction = getLayoutDirection();
        switch (direction) {
            default:
            case View.LAYOUT_DIRECTION_LTR:
                return 0;
            case View.LAYOUT_DIRECTION_RTL:
                return getWidth();
        }
    }

    public final float getDirectionCoeff() {
        final int direction = getLayoutDirection();
        switch (direction) {
            default:
            case LayoutDirection.LTR:
                return 1;
            case LayoutDirection.RTL:
                return -1;
        }
    }

    public final float calculateWithCoefficient(final float _number) {
        return getDirectionCoeff() * _number;
    }

    public final float calculateDependsOnDirection(final float _number) {
        return getStartPoint() + getDirectionCoeff() * _number;
    }

    public final float getHalfOuterRadius() {
        return mSeparatorRadius / 2 + mHexagonGapWidth;
    }

    public final void setServiceSelectedListener(final OnServiceSelected _serviceSelectedListener) {
        mServiceSelectedListener = _serviceSelectedListener;
    }

    public final void setAnimationProgress(final float _animationProgress) {
        mAnimationProgress = _animationProgress;

        mHexagonGapWidth = mStartGapWidth + mGapWidthDifference * _animationProgress;
        requestLayout();
//        calculateVariables(getWidth());
//        invalidate();
    }

    private void initProperties(final AttributeSet _attrs) {
        TypedArray typedArrayData = getContext().getTheme().
                obtainStyledAttributes(_attrs, R.styleable.HexagonalButtonsLayoutAttrs, 0, 0);
        try {
            mSeparatorColor = typedArrayData.getColor(R.styleable.HexagonalButtonsLayoutAttrs_separatorColor, Color.WHITE);
            mButtonsCount = typedArrayData.getInt(R.styleable.HexagonalButtonsLayoutAttrs_hexagonCount, 4);
            mSeparatorStrokeWidth = typedArrayData.getDimension(R.styleable.HexagonalButtonsLayoutAttrs_separatorStrokeWidth, 3);
            mHexagonGapWidth = typedArrayData.getDimension(R.styleable.HexagonalButtonsLayoutAttrs_hexagonGapWidth, 3);
            mTextSize = typedArrayData.getDimension(R.styleable.HexagonalButtonsLayoutAttrs_hexTextSize, 14);
            mIsTutorialMode = typedArrayData.getBoolean(R.styleable.HexagonalButtonsLayoutAttrs_isTutorialMode, false);
        } finally {
            typedArrayData.recycle();
        }

        mPolygons = new ArrayList<>();

        mGapWidthDifference = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        mTextSizeDifference = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        mStartGapWidth = mHexagonGapWidth;

        mTwoDivSquare = (float) (2 / Math.sqrt(3));
    }

    private void initPaint() {
        mSeparatorPaint = new Paint();
        mSeparatorPaint.setColor(mSeparatorColor);
        mSeparatorPaint.setStyle(Paint.Style.STROKE);
        mSeparatorPaint.setStrokeWidth(mSeparatorStrokeWidth);

        mButtonPaint = new Paint();
        mButtonPaint.setColor(Color.WHITE);
        mButtonPaint.setStyle(Paint.Style.FILL);

//        mShadowPaint = new Paint();
//        mShadowPaint.setStyle(Paint.Style.FILL);
//        if (mIsTutorialMode) {
//            mShadowPaint.setShadowLayer(15.0f, 0, 0, 0xff4b4b4b);
//        } else {
//            mShadowPaint.setShadowLayer(3.0f, 4.0f, 4.0f, 0xFF8C8C8C);
//        }

        mButtonSecondColorPaint = new Paint();
        mButtonSecondColorPaint.setColor(0xFF44545F);
        mButtonSecondColorPaint.setStyle(Paint.Style.FILL);

        mOrangeTextPaint = new Paint();
        mOrangeTextPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), CalligraphyConfig.get().getFontPath()));
        mOrangeTextPaint.setTextAlign(Paint.Align.CENTER);
        mOrangeTextPaint.setTextSize(mTextSize);
        mOrangeTextPaint.setColor(ImageUtils.isBlackAndWhiteMode(getContext()) ? Color.BLACK : 0xFFF68F1E);

        mWhiteTextPain = new Paint();
        mWhiteTextPain.setTypeface(Typeface.createFromAsset(getContext().getAssets(), CalligraphyConfig.get().getFontPath()));
        mWhiteTextPain.setTextAlign(Paint.Align.CENTER);
        mWhiteTextPain.setTextSize(mTextSize);
        mWhiteTextPain.setColor(Color.WHITE);
    }

    private void initDrawables() {
        mDrawables = new ArrayList<>();

        mDrawables.add(ImageUtils.getFilteredDrawableByTheme(getContext(), R.drawable.ic_earth, R.attr.themeMainColor));
        mDrawables.add(ImageUtils.getFilteredDrawableByTheme(getContext(), R.drawable.ic_coverage, R.attr.themeMainColor));
        mDrawables.add(ImageUtils.getFilteredDrawableByTheme(getContext(), R.drawable.ic_verif, R.attr.themeMainColor));
        mDrawables.add(ImageUtils.getFilteredDrawableByTheme(getContext(), R.drawable.ic_spam, R.attr.themeMainColor));

        mMaxDrawableHeight = getMaxDrawableHeight(mDrawables);
    }

    @Override
    protected final void onMeasure(final int _widthMeasureSpec, final int _heightMeasureSpec) {
        final int myHeight;
        final int width;
        final int height;

        final int heightMode = MeasureSpec.getMode(_heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(_widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(_heightMeasureSpec);

        width = widthSize;

//        final float triangleHeight = (width - getPaddingStart() - getPaddingEnd() -
//                mHexagonGapWidth * mButtonsCount - mHexagonGapWidth) / DOUBLE_BUTTONS;
//        final float radius = (float) (triangleHeight * 2 / Math.sqrt(3));

        mTriangleHeight = (width - getPaddingLeft() - getPaddingRight() -
                mHexagonGapWidth * mButtonsCount - mHexagonGapWidth) / DOUBLE_BUTTONS;
        mRadius = mTriangleHeight * mTwoDivSquare;

        mSeparatorTriangleHeight = mTriangleHeight + mHexagonGapWidth / 2;
        mSeparatorRadius = mSeparatorTriangleHeight * mTwoDivSquare;

        myHeight = (int) (mRadius * 2 + mHexagonGapWidth + mSeparatorStrokeWidth + getPaddingTop() + getPaddingBottom());

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(myHeight, heightSize);
        } else {
            height = myHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected final void onSizeChanged(final int _w, final int _h,
                                       final int _oldw, final int _oldh) {
        super.onSizeChanged(_w, _h, _oldw, _oldh);
//        calculateVariables(_w);

        if (mOldWidth == null || mOldWidth != _w || mCenters == null || mCenters.isEmpty()) {
            mOldWidth = _w;
            calculateCenters();
            measureDrawablesBounds();
//            calculateSeparatorPath();
        }

//        measureDrawablesBounds();
    }

    private void calculateCenters() {
        mCenters = new ArrayList<>();
        float centerY = getPaddingTop() + mRadius;
        float centerX = calculateDependsOnDirection(getPaddingStart() + mHexagonGapWidth + mTriangleHeight);

        for (int hexagon = 0; hexagon < mButtonsCount;
             hexagon++, centerX += getDirectionCoeff() * (mHexagonGapWidth + mTriangleHeight * 2)) {
            mCenters.add(new PointF(centerX, centerY));
        }
    }

    private void calculateSeparatorPath() {
        mSeparatorPath = new Path();

        float centerY = getPaddingTop() + mRadius + mHexagonGapWidth / 2;

        mSeparatorPath.moveTo(mCenters.get(0).x - calculateWithCoefficient(2 * mSeparatorTriangleHeight), centerY + mSeparatorRadius);
        mSeparatorPath.lineTo(mCenters.get(0).x - calculateWithCoefficient(mSeparatorTriangleHeight), centerY + mSeparatorRadius / 2);

        for (int hexagon = 0; hexagon < mButtonsCount; hexagon++) {

            mSeparatorPath.lineTo(mCenters.get(hexagon).x, centerY + mSeparatorRadius);
            mSeparatorPath.lineTo(mCenters.get(hexagon).x + calculateWithCoefficient(mSeparatorTriangleHeight), centerY + mSeparatorRadius / 2);
        }

        mSeparatorPath.lineTo(mCenters.get(mButtonsCount - 1).x + calculateWithCoefficient(mSeparatorTriangleHeight * 2), centerY + mSeparatorRadius);
    }

    private void measureDrawablesBounds() {
        float centerY = getPaddingTop() + mRadius / 2 + mMaxDrawableHeight;

        for (int hexagon = 0; hexagon < mButtonsCount; hexagon++) {
            final float drawableWidth = mDrawables.get(hexagon).getMinimumWidth();
            final float drawableHeight = mDrawables.get(hexagon).getMinimumHeight();

            final PointF point = mCenters.get(hexagon);

            mDrawables.get(hexagon).setBounds((int) (point.x - drawableWidth / 2), (int) (centerY - drawableHeight),
                    (int) (point.x + drawableWidth / 2), (int) centerY);
        }
    }

    private float getMaxDrawableHeight(final List<Drawable> _drawables) {
        float max = 0;

        for (final Drawable drawable : _drawables) {
            if (drawable.getMinimumHeight() > max) {
                max = drawable.getMinimumHeight();
            }
        }

        return max;
    }

    @Override
    protected final void onDraw(final Canvas _canvas) {
        super.onDraw(_canvas);

        drawHexagons(_canvas);

        if (!mIsTutorialMode) {
            drawBottomSeparator(_canvas);
        }

        drawDrawables(_canvas);
        drawText(_canvas, textColorByTheme);
    }

    private void drawHexagons(final Canvas _canvas) {
        Path buttonsPath = new Path();

        for (int hexagon = 0; hexagon < mButtonsCount; hexagon++) {

            final PointF point = mCenters.get(hexagon);

            buttonsPath = calculatePath(buttonsPath, point.x, point.y, hexagon);
        }

        _canvas.drawPath(buttonsPath, mButtonPaint);
    }

    private Path calculatePath(Path _path, final float _centerX, final float _centerY, final int _hexagonNumber) {
        if (_hexagonNumber + 1 <= mPolygons.size()) {
            changePoint(mPolygons.get(_hexagonNumber), _centerX, _centerY);
        } else {
            mPolygons.add(createPoints(_centerX, _centerY));
        }

        _path.moveTo(_centerX, _centerY + mRadius);
        _path.lineTo(_centerX - mTriangleHeight, _centerY + mRadius / 2);
        _path.lineTo(_centerX - mTriangleHeight, _centerY - mRadius / 2);
        _path.lineTo(_centerX, _centerY - mRadius);
        _path.lineTo(_centerX + mTriangleHeight, _centerY - mRadius / 2);
        _path.lineTo(_centerX + mTriangleHeight, _centerY + mRadius / 2);
        _path.close();

        return _path;
    }

    private PointF[] createPoints(final float _centerX, final float _centerY) {
        final PointF[] points = new PointF[6];

        points[0] = new PointF(_centerX, _centerY + mRadius);
        points[1] = new PointF(_centerX - mTriangleHeight, _centerY + mRadius / 2);
        points[2] = new PointF(_centerX - mTriangleHeight, _centerY - mRadius / 2);
        points[3] = new PointF(_centerX, _centerY - mRadius);
        points[4] = new PointF(_centerX + mTriangleHeight, _centerY - mRadius / 2);
        points[5] = new PointF(_centerX + mTriangleHeight, _centerY + mRadius / 2);

        return points;
    }

    private void changePoint(final PointF[] _points, final float _centerX, final float _centerY) {
        _points[0].set(_centerX, _centerY + mRadius);
        _points[1].set(_centerX - mTriangleHeight, _centerY + mRadius / 2);
        _points[2].set(_centerX - mTriangleHeight, _centerY - mRadius / 2);
        _points[3].set(_centerX, _centerY - mRadius);
        _points[4].set(_centerX + mTriangleHeight, _centerY - mRadius / 2);
        _points[5].set(_centerX + mTriangleHeight, _centerY + mRadius / 2);
    }

    private void drawBottomSeparator(final Canvas _canvas) {
        Path separatorPath = new Path();

        float centerY = getPaddingTop() + mRadius + mHexagonGapWidth / 2;
        final float halfSeparatorRadius = mSeparatorRadius / 2;

        separatorPath.moveTo(mCenters.get(0).x - calculateWithCoefficient(2 * mSeparatorTriangleHeight), centerY + mSeparatorRadius);
        separatorPath.lineTo(mCenters.get(0).x - calculateWithCoefficient(mSeparatorTriangleHeight), centerY + halfSeparatorRadius);

        for (int hexagon = 0; hexagon < mButtonsCount; hexagon++) {

            separatorPath.lineTo(mCenters.get(hexagon).x, centerY + mSeparatorRadius);
            separatorPath.lineTo(mCenters.get(hexagon).x + calculateWithCoefficient(mSeparatorTriangleHeight), centerY + halfSeparatorRadius);
        }

        separatorPath.lineTo(mCenters.get(mButtonsCount - 1).x + calculateWithCoefficient(mSeparatorTriangleHeight * 2), centerY + mSeparatorRadius);

        _canvas.drawPath(separatorPath, mSeparatorPaint);
    }

    private void drawDrawables(final Canvas _canvas) {
        for (final Drawable drawable : mDrawables) {
            drawable.draw(_canvas);
        }
    }

    private void drawText(final Canvas _canvas, final int _textColor) {
        drawTextLayout(_canvas, mCenters.get(0), getResources().getString(R.string.hexagon_button_block_website), _textColor);

        drawTextLayout(_canvas, mCenters.get(1), getResources().getString(R.string.hexagon_button_coverage), _textColor);

        drawTextLayout(_canvas, mCenters.get(2), getResources().getString(R.string.hexagon_button_verification), _textColor);

        drawTextLayout(_canvas, mCenters.get(3), getResources().getString(R.string.hexagon_button_sms_spam), _textColor);
    }

    private void drawTextLayout(final Canvas _canvas, final PointF _center, final String _text,
                                final int _textColor) {
        float spacingMult = 1.0f;
        if (getResources().getConfiguration().locale.toString().equals(C.ARABIC)) {
            spacingMult = 0.7f;
        }

        TextPaint mTextPaint = new TextPaint();
        mTextPaint.setTextSize(mTextSize - mTextSizeDifference * mAnimationProgress);
        mTextPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), CalligraphyConfig.get().getFontPath()));
        mTextPaint.setColor(ImageUtils.isBlackAndWhiteMode(getContext()) ? Color.BLACK : _textColor);
        StaticLayout mTextLayout = new StaticLayout(_text, mTextPaint,
                (int) (mTriangleHeight * 2), Layout.Alignment.ALIGN_CENTER, spacingMult, 0.0f, false);

        _canvas.save();

        _canvas.translate(_center.x - (mTextLayout.getWidth() / 2), _center.y + mRadius / 3 - mTextLayout.getHeight() / 2);
        mTextLayout.draw(_canvas);
        _canvas.restore();
    }

    @Override
    public final boolean onTouchEvent(@NonNull final MotionEvent _event) {
        final int action = MotionEventCompat.getActionMasked(_event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                capturePress(_event);
                return true;
            case (MotionEvent.ACTION_UP):
                if (isDown) {
                    captureClick(_event);
                }
                return true;
            case (MotionEvent.ACTION_MOVE):
                if (isDown) {
                    captureMove(_event);
                }
                return true;
            default:
                return super.onTouchEvent(_event);
        }
    }

    private void capturePress(final MotionEvent _event) {
        final PointF clickPoint = new PointF(_event.getX(), _event.getY());
        for (int i = 0; i < mPolygons.size(); i++) {
            if (mPolygons.get(i) != null && HexagonUtils.pointInPolygon(clickPoint, mPolygons.get(i))) {
                isDown = true;
                mPressedButton = i;
                return;
            }
        }
    }

    private int calculateColorByTheme(final Context _context, @AttrRes final int _attr){
        final TypedValue typedValue = new TypedValue();
        final Resources.Theme theme = _context.getTheme();
        theme.resolveAttribute(_attr, typedValue, true);
        return typedValue.data;
    }

    private void captureClick(final MotionEvent _event) {
        isDown = false;
        final PointF clickPoint = new PointF(_event.getX(), _event.getY());
        if (mPressedButton == null) return;
        if (HexagonUtils.pointInPolygon(clickPoint, mPolygons.get(mPressedButton))) {
            if (mServiceSelectedListener != null) {
                mServiceSelectedListener.serviceSelected(mPressedButton);
            }
        }
        mPressedButton = null;
    }

    private void captureMove(final MotionEvent _event) {
        final PointF clickPoint = new PointF(_event.getX(), _event.getY());
        if (mPressedButton != null && !HexagonUtils.pointInPolygon(clickPoint, mPolygons.get(mPressedButton))) {
            mPressedButton = null;
        }
    }

    public interface OnServiceSelected {
        void serviceSelected(final int _id);
    }

    public enum StaticService {
        BLOCK_WEBSITE(0),
        POOR_COVERAGE_SERVICE(1),
        VERIFY_DEVICE(2),
        SMS_SPAM(3);

        private final Integer value;

        StaticService(final Integer newValue) {
            value = newValue;
        }

        public Integer getValue() {
            return value;
        }

        public boolean isEquals(final int _value) {
            return value.equals(_value);
        }
    }
}
