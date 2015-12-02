package com.uae.tra_smart_services.entities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import com.uae.tra_smart_services.customviews.HexagonView;
import com.uae.tra_smart_services.customviews.HexagonView.ScaleType;
import com.uae.tra_smart_services.util.ImageUtils;

/**
 * Created by mobimaks on 09.10.2015.
 */
public class HexagonViewTarget extends ViewTarget<HexagonView, Drawable> implements Target {

    @ScaleType
    private final int mScaleType;

    public HexagonViewTarget(final HexagonView _view) {
        this(_view, ScaleType.CENTER_CROP, false);
    }

    public HexagonViewTarget(final HexagonView _view, final boolean _isGlideTarget) {
        this(_view, ScaleType.CENTER_CROP, _isGlideTarget);
    }

    public HexagonViewTarget(final HexagonView _view, final @ScaleType int _scaleType) {
        this(_view, _scaleType, false);
    }

    public HexagonViewTarget(final HexagonView _view, final @ScaleType int _scaleType, final boolean _isGlideTarget) {
        super(_view);
        mScaleType = _scaleType;
        _view.setTag(_isGlideTarget ? null : this);
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        super.onLoadStarted(placeholder);
        updateHexagonViewSrc(placeholder);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        super.onLoadFailed(e, errorDrawable);
        updateHexagonViewSrc(errorDrawable);
    }

    @Override
    public void onLoadCleared(Drawable placeholder) {
        super.onLoadCleared(placeholder);
        updateHexagonViewSrc(placeholder);
    }

    @Override
    public void onResourceReady(Drawable resource, GlideAnimation<? super Drawable> glideAnimation) {
        updateHexagonViewSrc(resource);
    }

    private void updateHexagonViewSrc(final Drawable _drawable) {
        final HexagonView hexagonView = getView();
        hexagonView.postScaleType(mScaleType);
        hexagonView.setHexagonSrcDrawable(ImageUtils.getFilteredDrawable(hexagonView.getContext(), _drawable));
    }

    @Override
    public final void onBitmapLoaded(Bitmap _bitmap, LoadedFrom _from) {
        if (_bitmap != null) {
            final BitmapDrawable drawable = new BitmapDrawable(getView().getResources(), _bitmap);
            updateHexagonViewSrc(drawable);
            getView().setTag(null);
        }
    }

    @Override
    public final void onBitmapFailed(final Drawable _errorDrawable) {
        if (_errorDrawable != null) {
            updateHexagonViewSrc(_errorDrawable);
            getView().setTag(null);
        }
    }

    @Override
    public final void onPrepareLoad(final Drawable _placeHolderDrawable) {
        if (_placeHolderDrawable != null) {
            updateHexagonViewSrc(_placeHolderDrawable);
            getView().setTag(null);
        }
    }

}
