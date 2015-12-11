package biz.enon.tra.uae.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Base64;
import android.util.TypedValue;

import com.enon.tra.uae.R;

import java.io.ByteArrayOutputStream;

import biz.enon.tra.uae.global.C;

/**
 * Created by mobimaks on 14.08.2015.
 */
public final class ImageUtils {

    private static final String BASE64_JPEG_HEADER = "data:image/jpeg;base64,";
    private static Boolean IS_BLACK_AND_WHITE_MODE;
    private static ColorMatrixColorFilter mColorFilter;

    private ImageUtils() {
    }

    @Nullable
    public static String imageToBase64(final ContentResolver _resolver, final Uri _uri) throws Exception {
        if (_resolver == null || _uri == null) {
            return null;
        }

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(_resolver, _uri);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        try {
            boolean isCompressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteStream);
            if (isCompressed) {
                byte[] ba = byteStream.toByteArray();
                return BASE64_JPEG_HEADER + Base64.encodeToString(ba, Base64.NO_WRAP);
            }
        } catch (Throwable exc) {
            throw new Exception(exc.getMessage());
        } finally {
            bitmap.recycle();
            byteStream.close();
        }
        return null;
    }

    public static void setBlackAndWhiteMode(final boolean _enabled) {
        IS_BLACK_AND_WHITE_MODE = _enabled;
    }

    public static boolean isBlackAndWhiteMode(final Context _context) {
        if (IS_BLACK_AND_WHITE_MODE == null) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
            IS_BLACK_AND_WHITE_MODE = prefs.getBoolean(C.KEY_BLACK_AND_WHITE_MODE, false);
        }
        return IS_BLACK_AND_WHITE_MODE;
    }

    public static Drawable getFilteredDrawable(final Context _context, final @DrawableRes int drawableRes) {
        final Drawable drawable = ContextCompat.getDrawable(_context, drawableRes);
        return getFilteredDrawable(_context, drawable);
    }

    public static <D extends Drawable> D getFilteredDrawable(final Context _context, final D _origDrawable) {
        if (mColorFilter == null) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            mColorFilter = new ColorMatrixColorFilter(matrix);
        }
        if (isBlackAndWhiteMode(_context)) {
            _origDrawable.mutate().setColorFilter(mColorFilter);
        }
        return _origDrawable;
    }

    public static Drawable getFilteredDrawableByTheme(final Context _context, final @DrawableRes int drawableRes, @AttrRes final int _attr) {
        final Drawable drawable = ContextCompat.getDrawable(_context, drawableRes);

        if (!isBlackAndWhiteMode(_context)) {
            final int drawableColor = getColorFromAttr(_context, _attr);

            Drawable wrappedDrawable = DrawableCompat.wrap(drawable.mutate());
            DrawableCompat.setTint(wrappedDrawable, drawableColor);
            return drawable;
        } else {
            return getFilteredDrawable(_context, drawable);
        }
    }

    @ColorInt
    public static int getColorFromAttr(@NonNull final Context _context, @AttrRes int _attr) {
        final TypedValue typedValue = new TypedValue();
        final Theme theme = _context.getTheme();
        theme.resolveAttribute(_attr, typedValue, true);
        return typedValue.data;
    }

    @ColorInt
    public static int getThemeColor(@NonNull final Context _context) {
        return getColorFromAttr(_context, R.attr.themeMainColor);
    }

}
