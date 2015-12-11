package biz.enon.tra.uae.entities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import biz.enon.tra.uae.util.ImageUtils;

/**
 * Created by mobimaks on 26.08.2015.
 */

public final class ImageViewTarget implements Target {

    private final ImageView mImageView;

    public ImageViewTarget(final ImageView _imageView) {
        mImageView = _imageView;
        mImageView.setTag(this);
    }

    @Override
    public final void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if (bitmap != null) {
            BitmapDrawable drawable = new BitmapDrawable(mImageView.getResources(), bitmap);
            mImageView.setImageDrawable(ImageUtils.getFilteredDrawable(mImageView.getContext(), drawable));
            mImageView.setTag(null);
        }
    }

    @Override
    public final void onBitmapFailed(Drawable errorDrawable) {
        if (errorDrawable != null) {
            mImageView.setImageDrawable(ImageUtils.getFilteredDrawable(mImageView.getContext(), errorDrawable));
            mImageView.setTag(null);
        }
    }

    @Override
    public final void onPrepareLoad(Drawable placeHolderDrawable) {
        if (placeHolderDrawable != null) {
            mImageView.setImageDrawable(ImageUtils.getFilteredDrawable(mImageView.getContext(), placeHolderDrawable));
            mImageView.setTag(null);
        }
    }
}
