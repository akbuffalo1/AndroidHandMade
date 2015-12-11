package biz.enon.tra.uae.customviews;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import static android.graphics.drawable.GradientDrawable.Orientation;

/**
 * Created by mobimaks on 26.11.2015.
 */
public class RectShadowDrawable extends Drawable {

    private final Rect mRect;
    private final int mShadowSize;

    @Size(4)
    private final Rect[] mRectangles = new Rect[4];
    private final GradientDrawable[] mSideDrawables = new GradientDrawable[4];

    public RectShadowDrawable(final RectF _rect, final int _shadowSize, @ColorInt int... _colors) {
        this(convertToRect(_rect), _shadowSize, _colors);
    }

    public RectShadowDrawable(final Rect _rect, final int _shadowSize, @ColorInt int... _colors) {
        mRect = _rect;
        mShadowSize = _shadowSize;

        mRectangles[0] = new Rect(); //Left rect
        mRectangles[0].left = _rect.left - _shadowSize;
        mRectangles[0].top = _rect.top - _shadowSize / 2;
        mRectangles[0].right = _rect.left;
        mRectangles[0].bottom = _rect.bottom + _shadowSize / 2;

        mRectangles[1] = new Rect(); //Top rect
        mRectangles[1].left = _rect.left - _shadowSize / 2;
        mRectangles[1].top = _rect.top - _shadowSize;
        mRectangles[1].right = _rect.right + _shadowSize / 2;
        mRectangles[1].bottom = _rect.top;

        mRectangles[2] = new Rect(); //Right rect
        mRectangles[2].left = _rect.right;
        mRectangles[2].top = _rect.top - _shadowSize / 2;
        mRectangles[2].right = _rect.right + _shadowSize;
        mRectangles[2].bottom = _rect.bottom + _shadowSize / 2;

        mRectangles[3] = new Rect(); //Bottom rect
        mRectangles[3].left = _rect.left - _shadowSize / 2;
        mRectangles[3].top = _rect.bottom;
        mRectangles[3].right = _rect.right + _shadowSize / 2;
        mRectangles[3].bottom = _rect.bottom + _shadowSize;

        setColors(_colors);
    }

    private void setColors(@ColorInt @NonNull int... _colors) {
        mSideDrawables[0] = new GradientDrawable(Orientation.RIGHT_LEFT, _colors);
        mSideDrawables[0].setBounds(mRectangles[0]);

        mSideDrawables[1] = new GradientDrawable(Orientation.BOTTOM_TOP, _colors);
        mSideDrawables[1].setBounds(mRectangles[1]);

        mSideDrawables[2] = new GradientDrawable(Orientation.LEFT_RIGHT, _colors);
        mSideDrawables[2].setBounds(mRectangles[2]);

        mSideDrawables[3] = new GradientDrawable(Orientation.TOP_BOTTOM, _colors);
        mSideDrawables[3].setBounds(mRectangles[3]);
    }

    @Override
    public void draw(Canvas canvas) {
        for (Drawable sideDrawable : mSideDrawables) {
            sideDrawable.draw(canvas);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        for (GradientDrawable sideDrawable : mSideDrawables) {
            sideDrawable.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        for (GradientDrawable sideDrawable : mSideDrawables) {
            sideDrawable.setColorFilter(colorFilter);
        }
    }

    @Override
    public int getOpacity() {
        return mSideDrawables[0].getOpacity();
    }

    protected static Rect convertToRect(@NonNull final RectF _rectF) {
        final Rect rect = new Rect();
        rect.left = Math.round(_rectF.left);
        rect.top = Math.round(_rectF.top);
        rect.right = Math.round(_rectF.right);
        rect.bottom = Math.round(_rectF.bottom);
        return rect;
    }
}
