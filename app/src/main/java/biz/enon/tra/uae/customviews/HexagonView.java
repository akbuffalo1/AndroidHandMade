package biz.enon.tra.uae.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.enon.tra.uae.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static biz.enon.tra.uae.customviews.HexagonView.ScaleType.CENTER_CROP;
import static biz.enon.tra.uae.customviews.HexagonView.ScaleType.INSIDE_CROP;


/**
 * Created by mobimaks on 02.08.2015.
 */
public class HexagonView extends View {

    private final int DEFAULT_TEXT_SIZE = Math.round(14 * getResources().getDisplayMetrics().density);
    private final int DEFAULT_HEXAGON_RADIUS = Math.round(30 * getResources().getDisplayMetrics().density);
    private final int HEXAGON_BORDER_COUNT = 6;

    //region Scale type
    @IntDef({INSIDE_CROP, CENTER_CROP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScaleType {
        int INSIDE_CROP = 0;
        int CENTER_CROP = 1;
    }
    //endregion

    private final Path mPath;
    private Paint mBorderPathPaint, mTextPaint;//, mBackgroundPaint;
    private Rect mTextRect;
    private Drawable mBackgroundDrawable, mSrcDrawable;

    @DrawableRes
    private int mSrcRes;
    private int mScaleType;

    private String mText;
    private double mHexagonSide, mHexagonInnerRadius;
    private float mBorderWidth, mTextSize;

    @ColorInt
    private int mBorderColor, mTextColor, mSrcTintColor;

    public HexagonView(Context context) {
        this(context, null);
    }

    public HexagonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HexagonView);
        try {
            setHexagonSide(a.getDimensionPixelSize(R.styleable.HexagonView_hexagonSideSize, DEFAULT_HEXAGON_RADIUS));
            mBorderWidth = a.getDimensionPixelSize(R.styleable.HexagonView_hexagonBorderSize, 0);
            mTextSize = a.getDimensionPixelSize(R.styleable.HexagonView_hexagonTextSize, DEFAULT_TEXT_SIZE);
            mBorderColor = a.getColor(R.styleable.HexagonView_hexagonBorderColor, 0xFFA8A7C6);
            mTextColor = a.getColor(R.styleable.HexagonView_hexagonTextColor, mBorderColor);
            mSrcTintColor = a.getColor(R.styleable.HexagonView_hexagonSrcTintColor, Color.TRANSPARENT);
            mBackgroundDrawable = a.getDrawable(R.styleable.HexagonView_hexagonBackground);
            mSrcDrawable = a.getDrawable(R.styleable.HexagonView_hexagonSrc);
            mSrcRes = a.getResourceId(R.styleable.HexagonView_hexagonSrc, R.drawable.authorization_logo);
            mText = a.getString(R.styleable.HexagonView_hexagonText);
            mScaleType = a.getInt(R.styleable.HexagonView_scaleType, INSIDE_CROP);
        } finally {
            a.recycle();
        }

        tintDrawableIfNeed();

        mBorderPathPaint = new Paint();
        mBorderPathPaint.setAntiAlias(true);
        mBorderPathPaint.setColor(mBorderColor);
        mBorderPathPaint.setStrokeWidth(mBorderWidth);
        mBorderPathPaint.setStyle(Paint.Style.STROKE);

//        mBackgroundPaint = new Paint();
//        mBackgroundPaint.setAntiAlias(true);
//        mBackgroundPaint.setColor(mBackgroundDrawable);
//        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);

        mPath = new Path();

        mTextRect = new Rect();
    }

    private void tintDrawableIfNeed() {
        if (mSrcDrawable != null && mSrcTintColor != Color.TRANSPARENT) {
            Drawable wrappedDrawable = DrawableCompat.wrap(mSrcDrawable);
            DrawableCompat.setTint(wrappedDrawable.mutate(), mSrcTintColor);
            mSrcDrawable = wrappedDrawable;
        }
    }

    public void setBorderColor(@ColorRes final int _borderColor) {
        mBorderPathPaint.setColor(ContextCompat.getColor(getContext(), _borderColor));
        invalidate();
    }

    public final void setHexagonSide(final int _hexagonSideSize) {
        mHexagonSide = _hexagonSideSize;
        mHexagonInnerRadius = Math.sqrt(3) * mHexagonSide / 2;
    }

    @Override
    public void setBackground(Drawable background) {
        mBackgroundDrawable = background;
        mBackgroundDrawable.setBounds(0, 0, getWidth(), getHeight());
        invalidate();
//        super.setBackground(background);
    }

    @Override
    public void setBackgroundResource(@DrawableRes final int _id) {
        setBackground(ContextCompat.getDrawable(getContext(), _id));
    }

    public final void setHexagonSrcDrawable(@DrawableRes final int _drawableRes) {
        setHexagonSrcDrawable(ContextCompat.getDrawable(getContext(), _drawableRes));
    }

    public final void setHexagonSrcDrawable(final Drawable _backgroundDrawable) {
        mSrcDrawable = _backgroundDrawable;
        tintDrawableIfNeed();
        invalidate();
    }

    public void setSrcTintColor(@ColorInt int _srcTintColor) {
        mSrcTintColor = _srcTintColor;
    }

    public void setSrcTintColorRes(@ColorRes int _srcTintColorRes) {
        mSrcTintColor = ContextCompat.getColor(getContext(), _srcTintColorRes);
    }

    public void postScaleType(@ScaleType int _scaleType) {
        mScaleType = _scaleType;
    }

    public void setScaleType(@ScaleType int _scaleType) {
        postScaleType(_scaleType);
        invalidate();
    }

    public final double getHexagonRadius() {
        return mHexagonSide;
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    @DrawableRes
    public int getHexagonSrc() {
        return mSrcRes;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final float widthSize = MeasureSpec.getSize(widthMeasureSpec);
        float myWidth = Math.round(2 * mHexagonInnerRadius + mBorderWidth);
        final float width;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
//            final double newInner = widthSize / 2;
//            final double side = (newInner * 2) / Math.sqrt(3);
//
//            myWidth = Math.round(2 * newInner + mBorderWidth);
//            mHexagonInnerRadius = newInner;
//            mHexagonSide = side;
            width = Math.min(myWidth, widthSize);
        } else {
            width = myWidth;
        }

        setMeasuredDimension((int) width, (int) Math.round(2 * mHexagonSide + mBorderWidth));
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        boolean stateChangeHandled = false;
        if (mBackgroundDrawable != null) {
            mBackgroundDrawable.setState(getDrawableState());
            stateChangeHandled = true;
        }

        if (mSrcDrawable != null) {
            mSrcDrawable.setState(getDrawableState());
            stateChangeHandled = true;
        }

        if (stateChangeHandled) {
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        double section = 2.0 * Math.PI / HEXAGON_BORDER_COUNT;

        mPath.reset();
        mPath.moveTo(
                (float) (w / 2 + mHexagonSide * Math.sin(0)),
                (float) (h / 2 + mHexagonSide * Math.cos(0)));

        for (int i = 1; i < HEXAGON_BORDER_COUNT; i++) {
            mPath.lineTo(
                    (float) (w / 2 + mHexagonSide * Math.sin(section * i)),
                    (float) (h / 2 + mHexagonSide * Math.cos(section * i)));
        }
        mPath.close();

        if (mBackgroundDrawable != null) {
            mBackgroundDrawable.setBounds(0, 0, w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.clipPath(mPath);
        if (mBackgroundDrawable != null) {
            mBackgroundDrawable.draw(canvas);
        }
//        canvas.drawPath(mPath, mBackgroundPaint);
        if (mSrcDrawable != null) {
//            canvas.clipPath(mPath);
            drawImageSrc(canvas, mSrcDrawable);
        }
        canvas.clipRect(0, 0, canvas.getWidth(), canvas.getHeight(), Region.Op.UNION);

        if (!TextUtils.isEmpty(mText)) {
            drawText(canvas);
        }

        canvas.drawPath(mPath, mBorderPathPaint);
    }

    private void drawImageSrc(final Canvas _canvas, final Drawable _drawable) {
        if (mScaleType == INSIDE_CROP) {
            drawCenterInsideImage(_canvas, _drawable);
        } else if (mScaleType == CENTER_CROP) {
            drawCenterCropImage(_canvas, _drawable);
        }
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

    private void drawCenterImage(Canvas _canvas, Drawable _drawable) {
        float drawableWidth = _drawable.getMinimumWidth(), drawableHeight = _drawable.getMinimumHeight();

        float centerY = (float) (mHexagonSide + getBorderWidth() / 2f);
        float centerX = (float) getWidth() / 2;

        _drawable.setBounds((int) (centerX - drawableWidth / 2), (int) (centerY - drawableHeight / 2),
                (int) (centerX + drawableWidth / 2), (int) (centerY + drawableHeight / 2));
        _drawable.draw(_canvas);
    }

    private void drawCenterCropImage(Canvas _canvas, Drawable _drawable) {
        float drawableWidth = _drawable.getMinimumWidth(), drawableHeight = _drawable.getMinimumHeight();
        float canvasWidth = _canvas.getWidth(), canvasHeight = _canvas.getHeight();

        final float scale;
        final float offsetX, offsetY;
        if (drawableWidth < drawableHeight) {
            scale = canvasWidth / drawableWidth;
            offsetX = 0;
            offsetY = (canvasHeight - (drawableHeight * scale)) / 2;
        } else {
            scale = canvasHeight / drawableHeight;
            offsetX = (canvasWidth - (drawableWidth * scale)) / 2;
            offsetY = 0;
        }
        _drawable.setBounds(0, 0, Math.round(drawableWidth), Math.round(drawableHeight));

        _canvas.save();
        _canvas.scale(scale, scale);
        _canvas.translate(Math.round(offsetX), Math.round(offsetY));
        _drawable.draw(_canvas);
        _canvas.restore();
    }

    private void drawText(final Canvas _canvas) {
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
        float y = (_canvas.getHeight() + mTextRect.height()) / 2f - mTextRect.bottom;
        _canvas.drawText(mText, _canvas.getWidth() / 2, y, mTextPaint);
    }
}