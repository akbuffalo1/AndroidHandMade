package com.uae.tra_smart_services_cutter.util;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by mobimaks on 09.07.2015.
 */
public final class IntentUtils {

    public static final String STRING_IMAGE_URI = "imageUri";
    public static final String STRING_CUTTED_IMAGE_URI = "cuttedImageUri";
    private static final String IMAGE_MIME_TYPE = "image/*";
    private static final String IMAGE_CUTTER_INTENT = "com.uae.tra_smart_services.intent.image_cutter";

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

    public static Intent getImageCutterStartIntent(final Uri _imageUri, final Uri _cuttedImageUri){
        return new Intent(IMAGE_CUTTER_INTENT).putExtra(STRING_IMAGE_URI, _imageUri.toString()).putExtra(STRING_CUTTED_IMAGE_URI, _cuttedImageUri.toString());
    }
}
