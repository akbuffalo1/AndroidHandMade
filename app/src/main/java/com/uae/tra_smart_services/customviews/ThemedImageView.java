package com.uae.tra_smart_services.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.uae.tra_smart_services.R;

/**
 * Created by mobimaks on 08.09.2015.
 */
public final class ThemedImageView extends ImageView {

    @ColorInt
    private int mTintColor;

    private Drawable mImageDrawable;

    public ThemedImageView(Context context) {
        this(context, null);
    }

    public ThemedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ThemedImageView);
        try {
            mTintColor = a.getColor(R.styleable.ThemedImageView_tintColor, Color.TRANSPARENT);
            mImageDrawable = a.getDrawable(R.styleable.ThemedImageView_imageSrc);
        } finally {
            a.recycle();
        }

        setImageDrawable(mImageDrawable);
    }

    public void setTintColor(@ColorInt int _tintColor) {
        mTintColor = _tintColor;
        invalidateDrawable();
    }

    private void invalidateDrawable() {
        setImageDrawable(getDrawable());
    }

    @Override
    public void setImageDrawable(final @Nullable Drawable _drawable) {
        Drawable wrappedDrawable = _drawable;
        if (_drawable != null) {
            wrappedDrawable = DrawableCompat.wrap(_drawable);
            DrawableCompat.setTint(wrappedDrawable.mutate(), mTintColor);
        }
        super.setImageDrawable(wrappedDrawable);
    }
}
