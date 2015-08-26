package com.uae.tra_smart_services.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.util.HexagonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vitaliy on 01/08/2015.
 */
public class HexagonalHeader extends View {


    private Path mHexagonPath;
    private Path mSecondRowHexagonPath;
    private Path mLastHexagonPath;
    private Path mFirstRowButtonsPath;
    private Path mSecondRowButtonsPath;
    private Path mFirstRowButtonsBorderPath;
    private Path mSecondRowButtonsBorderPath;

    private Paint mHexagonPaint;
    private Paint mSecondRowPaint;
    private Paint mButtonPaint;
    private Paint mPressedButtonPaint;
    private Paint mDefaultAvatarBorderPaint;
    private Paint mDefaultAvatarBackgroundPaint;
    private int mHexPaintColor;
    private int mButtonColor;
    private int mPressedButtonColor;
    private int mAvatarPlaceholderBackground;
    private int mHexagonPerRow;
    private int mRowCount;
    private float mHexagonAvatarBorderWidth;
    private float mHexagonStrokeWidth;
    private float mTriangleHeight;
    private float mRadius;

    private Drawable mAvatarPlaceholder;
    private HashMap<Integer, Drawable> mDrawables;
    private HashMap<Integer, PointF[]> mHexagons;
    private List<Integer> mInvisibleHexagons;
    private Integer mPressedButton;

    private boolean isDown = false;

    private final float mAvatarRadiusCoefficientStartValue = 1.6f;
    private final float mAvatarRadiusCoefficientDifference = 0.25f;
    private float mAvatarRadiusCoefficient = 1.6f;

    private float mAnimationProgress = 0.0f;

    public HexagonalHeader(final Context _context, final AttributeSet _attrs) {
        super(_context, _attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initProperties(_attrs);
        initPaint();
        initButtons();
        setInvisibleHexagons();
        initDrawables();
    }

    public final void setAnimationProgress(final float _progress) {
        mAnimationProgress = _progress;
//        Log.d("RecyclerView_test", String.valueOf(_progress));
        calculateSecondRowHexagonPath();
        calculateButtonsPath();
        measureDrawableBounds();
        requestLayout();
        invalidate();
    }

    public final float getStartPoint() {
        final int direction = getLayoutDirection();
        switch (direction) {
            default:
            case LayoutDirection.LTR:
                return 0;
            case LayoutDirection.RTL:
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

    private void initProperties(final AttributeSet _attrs) {
        TypedArray typedArrayData = getContext().getTheme().
                obtainStyledAttributes(_attrs, R.styleable.HexagonalHeader, 0, 0);
        try {
            mHexPaintColor = typedArrayData.getColor(R.styleable.HexagonalHeader_hexColor, Color.WHITE);
            mButtonColor = typedArrayData.getColor(R.styleable.HexagonalHeader_buttonColor, Color.GRAY);
            mPressedButtonColor = typedArrayData.getColor(R.styleable.HexagonalHeader_pressedColor, Color.WHITE);
            mHexagonPerRow = typedArrayData.getInt(R.styleable.HexagonalHeader_hexPerRow, 8);
            mRowCount = typedArrayData.getInt(R.styleable.HexagonalHeader_hexagonRowCount, 2);
            mHexagonStrokeWidth = typedArrayData.getDimension(R.styleable.HexagonalHeader_hexagonStrokeWidth, 3);
            mAvatarPlaceholderBackground = typedArrayData.getColor(R.styleable.HexagonalHeader_avatarPlaceholderBackground, 0xFF455560);

            mHexagonAvatarBorderWidth = mHexagonStrokeWidth * 2.5f;
        } finally {
            typedArrayData.recycle();
        }
    }

    private void initPaint() {
        mHexagonPaint = new Paint();
        mHexagonPaint.setAntiAlias(true);
        mHexagonPaint.setColor(mHexPaintColor);
        mHexagonPaint.setStyle(Paint.Style.STROKE);
        mHexagonPaint.setStrokeWidth(mHexagonStrokeWidth);

        mSecondRowPaint = new Paint();
        mSecondRowPaint.setAntiAlias(true);
        mSecondRowPaint.setColor(mHexPaintColor);
        mSecondRowPaint.setStyle(Paint.Style.STROKE);
        mSecondRowPaint.setStrokeWidth(mHexagonStrokeWidth);

        mDefaultAvatarBorderPaint = new Paint();
        mDefaultAvatarBorderPaint.setAntiAlias(true);
        mDefaultAvatarBorderPaint.setColor(mHexPaintColor);
        mDefaultAvatarBorderPaint.setStyle(Paint.Style.STROKE);
        mDefaultAvatarBorderPaint.setStrokeWidth(mHexagonAvatarBorderWidth);

        mDefaultAvatarBackgroundPaint = new Paint();
        mDefaultAvatarBackgroundPaint.setColor(mAvatarPlaceholderBackground);
        mDefaultAvatarBackgroundPaint.setStyle(Paint.Style.FILL);

        mButtonPaint = new Paint();
        mButtonPaint.setColor(mButtonColor);
        mButtonPaint.setStyle(Paint.Style.FILL);

        mPressedButtonPaint = new Paint();
        mPressedButtonPaint.setColor(mPressedButtonColor);
        mPressedButtonPaint.setStyle(Paint.Style.FILL);
    }

    private void initButtons() {
        mHexagons = new HashMap<>();

        mHexagons.put(7, null);
        mHexagons.put(8, null);
        mHexagons.put(15, null);
    }

    private void setInvisibleHexagons() {
        mInvisibleHexagons = new ArrayList<>();

        mInvisibleHexagons.add(11);
    }

    private void initDrawables() {
        mDrawables = new HashMap<>();
        mDrawables.put(7, ContextCompat.getDrawable(getContext(), R.drawable.ic_lamp));
        mDrawables.put(8, ContextCompat.getDrawable(getContext(), R.drawable.ic_search));
        mDrawables.put(15, ContextCompat.getDrawable(getContext(), R.drawable.ic_not));

        mAvatarPlaceholder = ContextCompat.getDrawable(getContext(), R.drawable.ic_user);
    }

    @Override
    protected final void onMeasure(final int _widthMeasureSpec, final int _heightMeasureSpec) {
        int myHeight;
        int width;
        int height;

        final int heightMode = MeasureSpec.getMode(_heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(_widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(_heightMeasureSpec);

        width = widthSize;

        final float triangleHeight = ((width - getPaddingLeft() - getPaddingRight()) / mHexagonPerRow) / 2;
        final float radius = (float) (triangleHeight * 2 / Math.sqrt(3));
        final float paddings = getPaddingBottom() + getPaddingTop();

        mAvatarRadiusCoefficient = mAvatarRadiusCoefficientStartValue -
                mAnimationProgress * mAvatarRadiusCoefficientDifference;
        myHeight = (int) Math.ceil(radius * mAvatarRadiusCoefficient * 2
                + radius + mHexagonStrokeWidth + paddings - paddings * mAnimationProgress
                - mAnimationProgress * radius);

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

        calculateVariables(_w);
        measureDrawableBounds();
//        calculateHexagonPaths();
        calculateButtonsPath();
        calculateFirstRowPath();
        calculateSecondRowHexagonPath();
    }

    private void measureDrawableBounds() {
        float centerY = getPaddingTop() + mRadius * mAvatarRadiusCoefficient + mHexagonAvatarBorderWidth / 2;
        float centerX = calculateDependsOnDirection(getPaddingLeft() + mHexagonStrokeWidth / 2);

        for (final Integer number : mHexagons.keySet()) {
            final int currentRow = (int) Math.floor((number - 1) / mHexagonPerRow);
            final int hexagonInRow = (number % mHexagonPerRow) == 0 ? mHexagonPerRow : (number % mHexagonPerRow);

            float currentY = centerY + currentRow * mRadius * 1.5f;
            float currentX = centerX + calculateWithCoefficient((hexagonInRow - 1) * 2 * mTriangleHeight);

            if (currentRow % 2 == 0) {
                currentX += calculateWithCoefficient(mTriangleHeight / 2);
            } else {
                currentX += calculateWithCoefficient(mTriangleHeight * 1.5f);
            }

            if (number == 7) {
                currentX -= calculateWithCoefficient(mTriangleHeight * 2 * mAnimationProgress);
            } else if (number == 15) {
                currentX -= calculateWithCoefficient(mTriangleHeight * mAnimationProgress);
                currentY -= mRadius * 1.5f * mAnimationProgress;
            }

            final int drawableWidth = mDrawables.get(number).getMinimumWidth();
            final int drawableHeight = mDrawables.get(number).getMinimumHeight();

            mDrawables.get(number).setBounds((int) (currentX - drawableWidth / 2), (int) (currentY - drawableHeight / 2),
                    (int) (currentX + drawableWidth / 2), (int) (currentY + drawableHeight / 2));
        }
    }

    private void calculateFirstRowPath() {
        mHexagonPath = new Path();

        float centerY = getPaddingTop() + mAvatarRadiusCoefficient * mRadius + mHexagonAvatarBorderWidth / 2;
        float centerX = calculateDependsOnDirection(getPaddingStart() + mHexagonStrokeWidth / 2 + mTriangleHeight / 2);

        for (int hexagon = 0; hexagon < mHexagonPerRow;
             hexagon++, centerX += calculateWithCoefficient(mTriangleHeight * 2)) {

            if (!mHexagons.containsKey(hexagon + 1)) {
                mHexagonPath = calculatePath(mHexagonPath, centerX, centerY);
            }
        }

    }

    private void calculateSecondRowHexagonPath() {
        mSecondRowPaint.setAlpha((int) (255 * (1.0 - mAnimationProgress)));
        mSecondRowHexagonPath = new Path();
        mLastHexagonPath = new Path();

        float centerY = getPaddingTop() + mAvatarRadiusCoefficient * mRadius + mRadius * 1.5f + mHexagonAvatarBorderWidth / 2;
        float centerX = calculateDependsOnDirection(getPaddingLeft() + mHexagonStrokeWidth / 2 + mTriangleHeight * 1.5f);

        for (int hexagon = 0; hexagon < mHexagonPerRow;
             hexagon++, centerX += calculateWithCoefficient(mTriangleHeight * 2)) {

            if (hexagon == 2) continue;

            float currentY = centerY - 1.5f * mRadius * mAnimationProgress;
            float currentX;

            if (hexagon == 0) {
                currentX = centerX - calculateWithCoefficient(mTriangleHeight * mAnimationProgress);
            } else {
                currentX = centerX + calculateWithCoefficient(mTriangleHeight * mAnimationProgress);
            }

            if (!mHexagons.containsKey(mHexagonPerRow + hexagon + 1)) {
                if (hexagon == mHexagonPerRow - 1) {
                    mLastHexagonPath = calculatePath(mLastHexagonPath, currentX, currentY);
                } else {
                    mSecondRowHexagonPath = calculatePath(mSecondRowHexagonPath, currentX, currentY);
                }
            }
        }
    }

    private void calculateButtonsPath() {
        mFirstRowButtonsPath = new Path();
        mSecondRowButtonsPath = new Path();
        mFirstRowButtonsBorderPath = new Path();
        mSecondRowButtonsBorderPath = new Path();

        float centerY = getPaddingTop() + mRadius * mAvatarRadiusCoefficient + mHexagonAvatarBorderWidth / 2;
        float centerX = calculateDependsOnDirection(getPaddingLeft() + mHexagonStrokeWidth / 2);

        for (final Integer number : mHexagons.keySet()) {
            final int currentRow = getRowNumber(number);
            final int hexagonInRow = (number % mHexagonPerRow) == 0 ? mHexagonPerRow : (number % mHexagonPerRow);

            float currentY = centerY + currentRow * mRadius * 1.5f;
            float currentX = centerX + calculateWithCoefficient((hexagonInRow - 1) * 2 * mTriangleHeight);

            if (currentRow % 2 == 0) {
                currentX += calculateWithCoefficient(mTriangleHeight / 2);
            } else {
                currentX += calculateWithCoefficient(mTriangleHeight * 1.5f);
            }

            if (number == 7) {
                currentX -= calculateWithCoefficient(mTriangleHeight * 2 * mAnimationProgress);
            } else if (number == 15) {
                currentX -= calculateWithCoefficient(mTriangleHeight * mAnimationProgress);
                currentY -= mRadius * 1.5f * mAnimationProgress;
            }

            if (currentRow == 0) {
                mFirstRowButtonsBorderPath = calculatePathAndSave(mFirstRowButtonsBorderPath, number, currentX, currentY);
                mFirstRowButtonsPath = calculateButtonFill(mFirstRowButtonsPath, currentX, currentY);
            } else {
                mSecondRowButtonsBorderPath = calculatePathAndSave(mSecondRowButtonsBorderPath, number, currentX, currentY);
                mSecondRowButtonsPath = calculateButtonFill(mSecondRowButtonsPath, currentX, currentY);
            }
        }
    }

    @Override
    protected final void onDraw(final Canvas _canvas) {
        drawHexagons(_canvas);
        drawDrawables(_canvas);
        drawAvatarHexagon(_canvas);
    }

    private void calculateVariables(final int _w) {
        mTriangleHeight = ((_w - getPaddingStart() - getPaddingEnd())/ mHexagonPerRow) / 2;
        mRadius = (float) (mTriangleHeight * 2 / Math.sqrt(3));
    }

    private void drawHexagons(final Canvas _canvas) {
        Path pressedButtonPath = null;

        if (mPressedButton != null) {
            float centerY = getPaddingTop() + mAvatarRadiusCoefficient * mRadius + mHexagonAvatarBorderWidth / 2;
            float centerX = calculateDependsOnDirection(getPaddingStart() + mHexagonStrokeWidth / 2);

            final int currentRow = getRowNumber(mPressedButton);
            final int hexagonInRow = (mPressedButton % mHexagonPerRow) == 0 ? mHexagonPerRow : (mPressedButton % mHexagonPerRow);

            float currentY = centerY + currentRow * mRadius * 1.5f;
            float currentX = centerX + calculateWithCoefficient((hexagonInRow - 1) * 2 * mTriangleHeight);

            if (currentRow % 2 == 0) {
                currentX += calculateWithCoefficient(mTriangleHeight / 2);
            } else {
                currentX += calculateWithCoefficient(mTriangleHeight * 1.5f);
            }

            if (mPressedButton == 7) {
                currentX -= calculateWithCoefficient(mTriangleHeight * 2 * mAnimationProgress);
            } else if (mPressedButton == 15) {
                currentX -= calculateWithCoefficient(mTriangleHeight * mAnimationProgress);
                currentY -= calculateWithCoefficient(mRadius * 1.5f * mAnimationProgress);
            }

            pressedButtonPath = calculateButtonFill(new Path(), currentX, currentY);
        }

        _canvas.drawPath(mSecondRowHexagonPath, mSecondRowPaint);
        _canvas.drawPath(mLastHexagonPath, mHexagonPaint);
        _canvas.drawPath(mHexagonPath, mHexagonPaint);
        _canvas.drawPath(mSecondRowButtonsPath, mButtonPaint);
        if (pressedButtonPath != null && getRowNumber(mPressedButton) == 1) {
            _canvas.drawPath(pressedButtonPath, mPressedButtonPaint);
        }
        _canvas.drawPath(mSecondRowButtonsBorderPath, mHexagonPaint);
        _canvas.drawPath(mFirstRowButtonsPath, mButtonPaint);
        if (pressedButtonPath != null && getRowNumber(mPressedButton) == 0) {
            _canvas.drawPath(pressedButtonPath, mPressedButtonPaint);
        }
        _canvas.drawPath(mFirstRowButtonsBorderPath, mHexagonPaint);
    }

    private void drawDrawables(final Canvas _canvas) {
        for (final Integer number : mHexagons.keySet()) {
            mDrawables.get(number).draw(_canvas);
        }
    }

    private Path calculateButtonFill(Path _path, final float _centerX, final float _centerY) {
        final float halfStroke = mHexagonStrokeWidth / 2;

        _path.moveTo(_centerX, _centerY + mRadius - halfStroke);
        _path.lineTo(_centerX - mTriangleHeight + halfStroke, _centerY + mRadius / 2 - 0);
        _path.lineTo(_centerX - mTriangleHeight + halfStroke, _centerY - mRadius / 2 + 0);
        _path.lineTo(_centerX, _centerY - mRadius + halfStroke);
        _path.lineTo((_centerX + mTriangleHeight) - halfStroke, _centerY - mRadius / 2 + 0);
        _path.lineTo((_centerX + mTriangleHeight) - halfStroke, _centerY + mRadius / 2 - 0);
        _path.close();

        return _path;
    }

    private Path calculatePath(Path _path, final float _centerX, final float _centerY) {
        _path.moveTo(_centerX, _centerY + mRadius);
        _path.lineTo(_centerX - mTriangleHeight, _centerY + mRadius / 2);
        _path.lineTo(_centerX - mTriangleHeight, _centerY - mRadius / 2);
        _path.lineTo(_centerX, _centerY - mRadius);
        _path.lineTo(_centerX + mTriangleHeight, _centerY - mRadius / 2);
        _path.lineTo(_centerX + mTriangleHeight, _centerY + mRadius / 2);
        _path.close();

        return _path;
    }

    private Path calculatePathAndSave(Path _path, final int _number,
                                      final float _centerX, final float _centerY) {
        mHexagons.put(_number, createPoints(_centerX, _centerY));

        return calculatePath(_path, _centerX, _centerY);
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

    private void drawAvatarHexagon(final Canvas _canvas) {
        final float avatarTriangleHeight = mTriangleHeight * mAvatarRadiusCoefficient;
        final float radius = mRadius * mAvatarRadiusCoefficient;

        final float centerY = getPaddingTop() + radius + mHexagonAvatarBorderWidth / 2;
        final float centerX = calculateDependsOnDirection(getPaddingStart() + mHexagonStrokeWidth / 2 + 2.5f * mTriangleHeight);

        Path avatarPath = new Path();
        avatarPath.moveTo(centerX, centerY + radius);
        avatarPath.lineTo(centerX - avatarTriangleHeight, centerY + radius / 2);
        avatarPath.lineTo(centerX - avatarTriangleHeight, centerY - radius / 2);
        avatarPath.lineTo(centerX, centerY - radius);
        avatarPath.lineTo(centerX + avatarTriangleHeight, centerY - radius / 2);
        avatarPath.lineTo(centerX + avatarTriangleHeight, centerY + radius / 2);
        avatarPath.close();

        Path avatarClip = new Path();
        avatarClip.moveTo(centerX, centerY + radius - mHexagonAvatarBorderWidth / 2);
        avatarClip.lineTo(centerX - avatarTriangleHeight + mHexagonAvatarBorderWidth / 2, centerY + radius / 2 - mHexagonAvatarBorderWidth / 2);
        avatarClip.lineTo(centerX - avatarTriangleHeight + mHexagonAvatarBorderWidth / 2, centerY - radius / 2 + mHexagonAvatarBorderWidth / 2);
        avatarClip.lineTo(centerX, centerY - radius + mHexagonAvatarBorderWidth / 2);
        avatarClip.lineTo(centerX + avatarTriangleHeight - mHexagonAvatarBorderWidth / 2, centerY - radius / 2 + mHexagonAvatarBorderWidth / 2);
        avatarClip.lineTo(centerX + avatarTriangleHeight - mHexagonAvatarBorderWidth / 2, centerY + radius / 2 - mHexagonAvatarBorderWidth / 2);
        avatarClip.close();

        _canvas.drawPath(avatarPath, mDefaultAvatarBackgroundPaint);
        _canvas.clipPath(avatarClip);

        float drawableWidth = mAvatarPlaceholder.getMinimumWidth();
        float drawableHeight = mAvatarPlaceholder.getMinimumHeight();

        if (drawableWidth < avatarTriangleHeight * 2 && drawableHeight < radius * 2) {
            mAvatarPlaceholder.setBounds((int) (centerX - drawableWidth / 2), (int) (centerY - drawableHeight / 2),
                    (int) (centerX + drawableWidth / 2), (int) (centerY + drawableHeight / 2));
        } else {
            final float dependDimen;
            final float dependParam;
            if (drawableHeight > drawableWidth) {
                dependDimen = drawableWidth;
                dependParam = avatarTriangleHeight * 2;
            } else {
                dependDimen = drawableHeight;
                dependParam = radius * 2;
            }
            final float coef = dependParam / dependDimen;

            drawableWidth *= coef;
            drawableHeight *= coef;

            mAvatarPlaceholder.setBounds((int) (centerX - drawableWidth / 2), (int) (centerY - drawableHeight / 2),
                    (int) (centerX + drawableWidth / 2), (int) (centerY + drawableHeight / 2));
        }
        mAvatarPlaceholder.draw(_canvas);
        _canvas.clipPath(avatarPath, Region.Op.UNION);
        _canvas.drawPath(avatarPath, mDefaultAvatarBorderPaint);
    }

    @Override
    public final boolean onTouchEvent(@NonNull final MotionEvent _event) {
        final int action = MotionEventCompat.getActionMasked(_event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                capturePress(_event);
                return true;
            case (MotionEvent.ACTION_UP) :
                if (isDown) {
                    captureClick(_event);
                }
                return true;
            case (MotionEvent.ACTION_MOVE) :
                if (isDown) {
                    captureMove(_event);
                }
                return true;
            default :
                return super.onTouchEvent(_event);
        }
    }

    private void capturePress(final MotionEvent _event) {
        final PointF clickPoint = new PointF(_event.getX(), _event.getY());
        for (final Integer key : mHexagons.keySet()) {
            if (mHexagons.get(key) != null && HexagonUtils.pointInPolygon(clickPoint, mHexagons.get(key))) {
                isDown = true;
                mPressedButton = key;
                invalidate();
                return;
            }
        }
    }

    private void captureClick(final MotionEvent _event) {
        isDown = false;
        final PointF clickPoint = new PointF(_event.getX(), _event.getY());
        if (mPressedButton == null) return;
        if (HexagonUtils.pointInPolygon(clickPoint, mHexagons.get(mPressedButton))) {
            Toast.makeText(getContext(), "Clicked hexagon " + mPressedButton, Toast.LENGTH_SHORT).show();
        }
        mPressedButton = null;
        invalidate();
    }

    private void captureMove(final MotionEvent _event) {
        final PointF clickPoint = new PointF(_event.getX(), _event.getY());
        if (mPressedButton != null && !HexagonUtils.pointInPolygon(clickPoint, mHexagons.get(mPressedButton))) {
            mPressedButton = null;
            invalidate();
        }
    }

    private int getRowNumber(final int _hexagon) {
        return (int) Math.floor((_hexagon - 1) / mHexagonPerRow);
    }
}