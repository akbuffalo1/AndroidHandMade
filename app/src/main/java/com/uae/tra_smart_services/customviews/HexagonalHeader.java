package com.uae.tra_smart_services.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.view.MotionEvent;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.util.HexagonUtils;
import com.uae.tra_smart_services.util.ImageUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mikazme on 01/08/2015.
 */
public class HexagonalHeader extends View implements Target {

    public static final int HEXAGON_BUTTON_INNOVATIONS = 7;
    public static final int HEXAGON_BUTTON_SEARCH = 8;
    public static final int HEXAGON_BUTTON_NOTIFICATION = 15;

    @IntDef({HEXAGON_BUTTON_INNOVATIONS, HEXAGON_BUTTON_SEARCH, HEXAGON_BUTTON_NOTIFICATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HexagonButton {
    }

    @IntDef({SCALE_TYPE_FIT_CENTER, SCALE_TYPE_CENTER_CROP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScaleType {
    }

    public static final int TUTORIAL_TYPE_NONE = 0;
    public static final int TUTORIAL_TYPE_AVATAR = 1;
    public static final int TUTORIAL_TYPE_INNOVATIONS = 2;
    public static final int TUTORIAL_TYPE_SEARCH = 3;
    public static final int TUTORIAL_TYPE_NOTIFICATIONS = 4;

    public static final int SCALE_TYPE_FIT_CENTER = 0;
    public static final int SCALE_TYPE_CENTER_CROP = 1;

    private Path mHexagonPath;
    private Path mSecondRowHexagonPath;
    private Path mLastHexagonPath;
    private Path mFirstRowButtonsPath;
    private Path mSecondRowButtonsPath;
    private Path mFirstRowButtonsBorderPath;
    private Path mSecondRowButtonsBorderPath;
    private Path mAvatarPath;
    private Path mAvatarClipPath;

    private Paint mHexagonPaint;
    private Paint mSecondRowPaint;
    private Paint mButtonPaint;
    private Paint mPressedButtonPaint;
    private Paint mDefaultAvatarBorderPaint;
    private Paint mDefaultAvatarBackgroundPaint;
    private Paint mNotificationBorderPaint;
    private TextPaint mNotificationTextPaint;
    private Paint mShadowPaint;
    private int mHexPaintColor;
    private int mButtonColor;
    private int mPressedButtonColor;
    private int mAvatarPlaceholderBackground;
    private int mHexagonPerRow;
    private int mRowCount;
    @ScaleType
    private int mScaleType = SCALE_TYPE_FIT_CENTER;
    private float mHexagonAvatarBorderWidth;
    private float mHexagonStrokeWidth;
    private float mTriangleHeight;
    private float mRadius;

    private int mTutorialType;

    private Drawable mAvatarPlaceholder;
    private HashMap<Integer, Drawable> mDrawables;
    private HashMap<Integer, PointF[]> mHexagons;

    @Size(6)
    private PointF[] mAvatarHexagon;

    private List<Integer> mInvisibleHexagons;

    @HexagonButton
    private Integer mPressedButton;

    private boolean isDown = false;
    private boolean mIsAvatarPressed = false;

    private final float mAvatarRadiusCoefficientStartValue = 1.6f;
    private final float mAvatarRadiusCoefficientDifference = 0.25f;
    private float mAvatarRadiusCoefficient = 1.6f;

    private float mAnimationProgress = 0.0f;
    private int mNotificationCount = 0;

    private String mNotificationCountText;
    private OnButtonClickListener mButtonClickListener;

    private final RectF mNotificationTextBounds = new RectF();
    private final RectF mNotificationBorderBounds = new RectF();

    public HexagonalHeader(final Context _context, final AttributeSet _attrs) {
        super(_context, _attrs);
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initProperties(_attrs);
        initPaint();
        initButtons();
        setInvisibleHexagons();
        initDrawables();
        setLayerType();
    }

    private void setLayerType() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public final void setOnButtonClickListener(final OnButtonClickListener _buttonClickListener) {
        mButtonClickListener = _buttonClickListener;
    }

    public final void setAnimationProgress(@FloatRange(from = 0, to = 1) final float _progress) {
        mAnimationProgress = _progress;
//        Log.d("RecyclerView_test", String.valueOf(_progress));
        calculateSecondRowHexagonPath();
        calculateButtonsPath();
        measureDrawableBounds();
        requestLayout();
    }

    public final PointF getAvatarCenter() {
        final float radius = mRadius * mAvatarRadiusCoefficient;

        final float centerY = getPaddingTop() + radius + mHexagonAvatarBorderWidth / 2;
        final float centerX = calculateDependsOnDirection(getPaddingStart() + mHexagonStrokeWidth / 2 + 2.5f * mTriangleHeight);

        return new PointF(centerX, centerY);
    }

    public final float getAvatarSideOffset() {
        return mTriangleHeight;
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

    public final void clearNotificationCount() {
        setNotificationCount(0);
    }

    public final void setNotificationCount(@IntRange(from = 0, to = 1000) int _notificationCount) {
        _notificationCount = _notificationCount <= 0 ? 0 : _notificationCount;
        if (mNotificationCount == _notificationCount) {
            return;
        }
        mNotificationCount = _notificationCount;

        final Drawable notificationIconDrawable;
        if (mNotificationCount == 0) {
            notificationIconDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_not);
        } else {
            notificationIconDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_ntf);
        }
        mDrawables.put(HEXAGON_BUTTON_NOTIFICATION, notificationIconDrawable);

        if (mNotificationCount < 100) {
            mNotificationCountText = String.valueOf(mNotificationCount);
        } else {
            mNotificationCountText = "99+";
        }
        measureDrawableBounds();
        invalidate();
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
            mTutorialType = typedArrayData.getInt(R.styleable.HexagonalHeader_tutorialType, TUTORIAL_TYPE_NONE);

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

        mShadowPaint = new Paint();
        mShadowPaint.setShadowLayer(12.0f, 0f, 0f, 0xff4b4b4b);
        mShadowPaint.setStyle(Paint.Style.FILL);

        mButtonPaint = new Paint();
        mButtonPaint.setColor(mButtonColor);
        mButtonPaint.setStyle(Paint.Style.FILL);

        mPressedButtonPaint = new Paint();
        mPressedButtonPaint.setColor(mPressedButtonColor);
        mPressedButtonPaint.setStyle(Paint.Style.FILL);

        final float density = getResources().getDisplayMetrics().density;

        mNotificationTextPaint = new TextPaint();
        mNotificationTextPaint.setAntiAlias(true);
        mNotificationTextPaint.setTextAlign(Paint.Align.CENTER);
        mNotificationTextPaint.setColor(mHexPaintColor);
        mNotificationTextPaint.setTextSize(12 * density);

        mNotificationBorderPaint = new Paint();
        mNotificationBorderPaint.setAntiAlias(true);
        mNotificationBorderPaint.setStrokeWidth(1.5f * density);
        mNotificationBorderPaint.setColor(mHexPaintColor);
        mNotificationBorderPaint.setStyle(Paint.Style.STROKE);
    }

    private void initButtons() {
        mHexagons = new HashMap<>();

        mHexagons.put(HEXAGON_BUTTON_INNOVATIONS, null);
        mHexagons.put(HEXAGON_BUTTON_SEARCH, null);
        mHexagons.put(HEXAGON_BUTTON_NOTIFICATION, null);
    }

    private void setInvisibleHexagons() {
        mInvisibleHexagons = new ArrayList<>();

        mInvisibleHexagons.add(11);
    }

    private void initDrawables() {
        mDrawables = new HashMap<>();
        mDrawables.put(HEXAGON_BUTTON_INNOVATIONS, ContextCompat.getDrawable(getContext(), R.drawable.ic_lamp));
        mDrawables.put(HEXAGON_BUTTON_SEARCH, ContextCompat.getDrawable(getContext(), R.drawable.ic_search));
        mDrawables.put(HEXAGON_BUTTON_NOTIFICATION, ContextCompat.getDrawable(getContext(), R.drawable.ic_not));

        mAvatarPlaceholder = ContextCompat.getDrawable(getContext(), R.drawable.ic_user_placeholder);
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
        if (mTutorialType == TUTORIAL_TYPE_NONE) {
            myHeight = (int) Math.ceil(radius * mAvatarRadiusCoefficient * 2
                    + radius + mHexagonStrokeWidth + paddings - paddings * mAnimationProgress
                    - mAnimationProgress * radius);
        } else {
            myHeight = (int) Math.ceil(radius * mAvatarRadiusCoefficient * 2 + paddings);
        }

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
        calculateAvatarHexagonPath();
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

            if (number == HEXAGON_BUTTON_INNOVATIONS) {
                currentX -= calculateWithCoefficient(mTriangleHeight * 2 * mAnimationProgress);
            } else if (number == HEXAGON_BUTTON_NOTIFICATION) {
                currentX -= calculateWithCoefficient(mTriangleHeight * mAnimationProgress);
                currentY -= mRadius * 1.5f * mAnimationProgress;
            }

            final int drawableWidth = mDrawables.get(number).getMinimumWidth();
            final int drawableHeight = mDrawables.get(number).getMinimumHeight();

            float drawableHorizontalOffset = 0f, drawableVerticalOffset = 0f;
            if (number == HEXAGON_BUTTON_NOTIFICATION && mNotificationCount > 0) {
                measureNotificationIconBounds(currentX, currentY);
                drawableHorizontalOffset = 0.25f;
                drawableVerticalOffset = 0.1f;
            }

            mDrawables.get(number).setBounds(
                    Math.round(currentX - drawableWidth * (0.5f + drawableHorizontalOffset)),
                    Math.round(currentY - drawableHeight * (0.5f + drawableVerticalOffset)),
                    Math.round(currentX + drawableWidth * (0.5f - drawableHorizontalOffset)),
                    Math.round(currentY + drawableHeight * (0.5f - drawableVerticalOffset)));
        }
    }

    private void measureNotificationIconBounds(final float _centerX, float _centerY) {
        final float notificationBorderPadding = 3 * getResources().getDisplayMetrics().density;
        final float hexagonBorderPadding = 4 * getResources().getDisplayMetrics().density;
        _centerY -= notificationBorderPadding;

        final Rect notificationTextBounds = new Rect();
        mNotificationTextPaint.getTextBounds(mNotificationCountText, 0, mNotificationCountText.length(), notificationTextBounds);

        mNotificationBorderBounds.set(notificationTextBounds);
        mNotificationBorderBounds.inset(-notificationBorderPadding, -notificationBorderPadding);
        if (mNotificationBorderBounds.width() < mNotificationBorderBounds.height()) {//make border rounded
            final float dif = Math.abs(mNotificationBorderBounds.height() - mNotificationBorderBounds.width());
            mNotificationBorderBounds.right += dif;
        }
        final float notificationBorderWidth = mNotificationBorderBounds.width();
        final float notificationBorderRight = Math.min(_centerX + notificationBorderWidth, _centerX + mTriangleHeight - hexagonBorderPadding);
        mNotificationBorderBounds.offsetTo(notificationBorderRight - notificationBorderWidth, _centerY);

        mNotificationTextBounds.set(notificationTextBounds);
        mNotificationTextBounds.offsetTo(
                mNotificationBorderBounds.centerX() - mNotificationTextBounds.width() / 2,
                mNotificationBorderBounds.centerY() - mNotificationTextBounds.height() / 2);
    }

    private void calculateFirstRowPath() {
        if (mHexagonPath == null) {
            mHexagonPath = new Path();
        } else {
            mHexagonPath.reset();
        }

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
        if (mSecondRowHexagonPath == null) {
            mSecondRowHexagonPath = new Path();
            mLastHexagonPath = new Path();
        } else {
            mSecondRowHexagonPath.reset();
            mLastHexagonPath.reset();
        }

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
        if (mFirstRowButtonsBorderPath == null) {
            mFirstRowButtonsPath = new Path();
            mSecondRowButtonsPath = new Path();
            mFirstRowButtonsBorderPath = new Path();
            mSecondRowButtonsBorderPath = new Path();
        } else {
            mFirstRowButtonsPath.reset();
            mSecondRowButtonsPath.reset();
            mFirstRowButtonsBorderPath.reset();
            mSecondRowButtonsBorderPath.reset();
        }

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

            if (number == HEXAGON_BUTTON_INNOVATIONS) {
                currentX -= calculateWithCoefficient(mTriangleHeight * 2 * mAnimationProgress);
            } else if (number == HEXAGON_BUTTON_NOTIFICATION) {
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

    private void calculateAvatarHexagonPath() {
        final float avatarTriangleHeight = mTriangleHeight * mAvatarRadiusCoefficient;
        final float radius = mRadius * mAvatarRadiusCoefficient;

        final float centerY = getPaddingTop() + radius + mHexagonAvatarBorderWidth / 2;
        final float centerX = calculateDependsOnDirection(getPaddingStart() + mHexagonStrokeWidth / 2 + 2.5f * mTriangleHeight);

        mAvatarHexagon = new PointF[6];
        mAvatarHexagon[0] = new PointF(centerX, centerY + radius);
        mAvatarHexagon[1] = new PointF(centerX - avatarTriangleHeight, centerY + radius / 2);
        mAvatarHexagon[2] = new PointF(centerX - avatarTriangleHeight, centerY - radius / 2);
        mAvatarHexagon[3] = new PointF(centerX, centerY - radius);
        mAvatarHexagon[4] = new PointF(centerX + avatarTriangleHeight, centerY - radius / 2);
        mAvatarHexagon[5] = new PointF(centerX + avatarTriangleHeight, centerY + radius / 2);

        mAvatarPath = new Path();
        mAvatarPath.moveTo(mAvatarHexagon[0].x, mAvatarHexagon[0].y);
        mAvatarPath.lineTo(mAvatarHexagon[1].x, mAvatarHexagon[1].y);
        mAvatarPath.lineTo(mAvatarHexagon[2].x, mAvatarHexagon[2].y);
        mAvatarPath.lineTo(mAvatarHexagon[3].x, mAvatarHexagon[3].y);
        mAvatarPath.lineTo(mAvatarHexagon[4].x, mAvatarHexagon[4].y);
        mAvatarPath.lineTo(mAvatarHexagon[5].x, mAvatarHexagon[5].y);
        mAvatarPath.close();

//        mAvatarClipPath = new Path();
//        mAvatarClipPath.moveTo(centerX, centerY + radius - mHexagonAvatarBorderWidth / 2);
//        mAvatarClipPath.lineTo(centerX - avatarTriangleHeight + mHexagonAvatarBorderWidth / 2, centerY + radius / 2 - mHexagonAvatarBorderWidth / 2);
//        mAvatarClipPath.lineTo(centerX - avatarTriangleHeight + mHexagonAvatarBorderWidth / 2, centerY - radius / 2 + mHexagonAvatarBorderWidth / 2);
//        mAvatarClipPath.lineTo(centerX, centerY - radius + mHexagonAvatarBorderWidth / 2);
//        mAvatarClipPath.lineTo(centerX + avatarTriangleHeight - mHexagonAvatarBorderWidth / 2, centerY - radius / 2 + mHexagonAvatarBorderWidth / 2);
//        mAvatarClipPath.lineTo(centerX + avatarTriangleHeight - mHexagonAvatarBorderWidth / 2, centerY + radius / 2 - mHexagonAvatarBorderWidth / 2);
//        mAvatarClipPath.close();
        mAvatarClipPath = new Path();
        mAvatarClipPath.moveTo(centerX, centerY + radius);
        mAvatarClipPath.lineTo(centerX - avatarTriangleHeight, centerY + radius / 2);
        mAvatarClipPath.lineTo(centerX - avatarTriangleHeight, centerY - radius / 2);
        mAvatarClipPath.lineTo(centerX, centerY - radius);
        mAvatarClipPath.lineTo(centerX + avatarTriangleHeight, centerY - radius / 2);
        mAvatarClipPath.lineTo(centerX + avatarTriangleHeight, centerY + radius / 2);
        mAvatarClipPath.close();
    }

    @Override
    protected final void onDraw(final Canvas _canvas) {
        if (mTutorialType != TUTORIAL_TYPE_AVATAR) {
            drawHexagons(_canvas);
            drawDrawables(_canvas);
        }
        drawAvatarHexagon(_canvas);
    }

    private void calculateVariables(final int _w) {
        mTriangleHeight = ((_w - getPaddingStart() - getPaddingEnd()) / mHexagonPerRow) / 2;
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

            if (mPressedButton == HEXAGON_BUTTON_INNOVATIONS) {
                currentX -= calculateWithCoefficient(mTriangleHeight * 2 * mAnimationProgress);
            } else if (mPressedButton == HEXAGON_BUTTON_NOTIFICATION) {
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
            if (number == HEXAGON_BUTTON_NOTIFICATION && mNotificationCount > 0) {
                drawNotificationIcon(_canvas);
            }
        }
    }

    private void drawNotificationIcon(final Canvas _canvas) {
        _canvas.drawRoundRect(mNotificationBorderBounds, mNotificationBorderBounds.height(), mNotificationBorderBounds.height(), mButtonPaint);
        _canvas.drawRoundRect(mNotificationBorderBounds, mNotificationBorderBounds.height(), mNotificationBorderBounds.height(), mNotificationBorderPaint);
        _canvas.drawText(mNotificationCountText, mNotificationTextBounds.centerX(), mNotificationTextBounds.bottom, mNotificationTextPaint);
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
        if (mHexagons.get(_number) != null) {
            changePoints(mHexagons.get(_number), _centerX, _centerY);
        } else {
            mHexagons.put(_number, createPoints(_centerX, _centerY));
        }

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

    private void changePoints(final PointF[] _points, final float _centerX, final float _centerY) {
        _points[0].set(_centerX, _centerY + mRadius);
        _points[1].set(_centerX - mTriangleHeight, _centerY + mRadius / 2);
        _points[2].set(_centerX - mTriangleHeight, _centerY - mRadius / 2);
        _points[3].set(_centerX, _centerY - mRadius);
        _points[4].set(_centerX + mTriangleHeight, _centerY - mRadius / 2);
        _points[5].set(_centerX + mTriangleHeight, _centerY + mRadius / 2);
    }

    private void drawAvatarHexagon(final Canvas _canvas) {
        if (mTutorialType == TUTORIAL_TYPE_AVATAR) {
            _canvas.drawPath(mAvatarPath, mShadowPaint);
        }
        _canvas.drawPath(mAvatarPath, mDefaultAvatarBackgroundPaint);
        _canvas.clipPath(mAvatarClipPath);

        final float avatarTriangleHeight = mTriangleHeight * mAvatarRadiusCoefficient;
        final float radius = mRadius * mAvatarRadiusCoefficient;

        final float centerY = getPaddingTop() + radius + mHexagonAvatarBorderWidth / 2;
        final float centerX = calculateDependsOnDirection(getPaddingStart() + mHexagonStrokeWidth / 2 + 2.5f * mTriangleHeight);

        int drawableWidth = mAvatarPlaceholder.getMinimumWidth();
        int drawableHeight = mAvatarPlaceholder.getMinimumHeight();


        if (mScaleType == SCALE_TYPE_FIT_CENTER) {
            mAvatarPlaceholder.setBounds((int) (centerX - drawableWidth / 2), (int) (centerY - drawableHeight / 2),
                    (int) (centerX + drawableWidth / 2), (int) (centerY + drawableHeight / 2));
        } else {
            int vwidth = (int) (avatarTriangleHeight * 2);
            int vheight = (int) (radius * 2);

            float scale;
            if (drawableWidth * vheight > vwidth * drawableHeight) {
                scale = (float) vheight / (float) drawableHeight;
            } else {
                scale = (float) vwidth / (float) drawableWidth;
            }

            drawableWidth *= scale;
            drawableHeight *= scale;

            mAvatarPlaceholder.setBounds((int) (centerX - drawableWidth / 2), (int) (centerY - drawableHeight / 2),
                    (int) (centerX + drawableWidth / 2), (int) (centerY + drawableHeight / 2));
        }
        mAvatarPlaceholder.draw(_canvas);
        _canvas.clipPath(mAvatarPath, Region.Op.UNION);
        _canvas.drawPath(mAvatarPath, mDefaultAvatarBorderPaint);
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
        for (final Integer key : mHexagons.keySet()) {
            if (mHexagons.get(key) != null && HexagonUtils.pointInPolygon(clickPoint, mHexagons.get(key))) {
                isDown = true;
                mPressedButton = key;
                invalidate();
                return;
            }
        }
        if (mAvatarHexagon != null && HexagonUtils.pointInPolygon(clickPoint, mAvatarHexagon)) {
            isDown = true;
            mIsAvatarPressed = true;
        }
    }

    private void captureClick(final MotionEvent _event) {
        isDown = false;
        final PointF clickPoint = new PointF(_event.getX(), _event.getY());
        if (mPressedButton != null && HexagonUtils.pointInPolygon(clickPoint, mHexagons.get(mPressedButton))) {
//            Toast.makeText(getContext(), "Clicked hexagon " + mPressedButton, Toast.LENGTH_SHORT).show();
            if (mButtonClickListener != null) {
                mButtonClickListener.onHexagonButtonClick(mPressedButton);
            }
            mPressedButton = null;
            invalidate();
        } else if (mIsAvatarPressed && HexagonUtils.pointInPolygon(clickPoint, mAvatarHexagon)) {
//            Toast.makeText(getContext(), "Clicked avatar ", Toast.LENGTH_SHORT).show();
            if (mButtonClickListener != null) {
                mButtonClickListener.onAvatarButtonClick();
            }
            mIsAvatarPressed = false;
            invalidate();
        }
    }

    private void captureMove(final MotionEvent _event) {
        final PointF clickPoint = new PointF(_event.getX(), _event.getY());
        if (mPressedButton != null && !HexagonUtils.pointInPolygon(clickPoint, mHexagons.get(mPressedButton))) {
            mPressedButton = null;
            invalidate();
        } else if (mIsAvatarPressed && !HexagonUtils.pointInPolygon(clickPoint, mAvatarHexagon)) {
            mIsAvatarPressed = false;
        }
    }

    private int getRowNumber(final int _hexagon) {
        return (int) Math.floor((_hexagon - 1) / mHexagonPerRow);
    }


    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        mScaleType = SCALE_TYPE_CENTER_CROP;
        mAvatarPlaceholder = ImageUtils.getFilteredDrawable(getContext(), new BitmapDrawable(getResources(), bitmap));
        invalidate();
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        mScaleType = SCALE_TYPE_FIT_CENTER;
        mAvatarPlaceholder = errorDrawable;
        invalidate();
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        mScaleType = SCALE_TYPE_FIT_CENTER;
        mAvatarPlaceholder = placeHolderDrawable;
        invalidate();
    }


    public interface OnButtonClickListener {
        void onAvatarButtonClick();

        void onHexagonButtonClick(@HexagonButton final int _hexagonButton);
    }
}
