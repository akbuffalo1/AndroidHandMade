package biz.enon.tra.uae.util;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by mobimaks on 09.07.2015.
 */
public final class IntentUtils {

    private static final String IMAGE_MIME_TYPE = "image/*";

    private IntentUtils() {
    }

    public static Intent getGalleryStartIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        galleryIntent.setType(IMAGE_MIME_TYPE);
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        return galleryIntent;
    }

    public static Intent getCameraStartIntent(final Uri fileUri) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        return takePictureIntent;
    }
}
