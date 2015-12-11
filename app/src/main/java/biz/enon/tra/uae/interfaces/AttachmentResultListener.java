package biz.enon.tra.uae.interfaces;

import android.net.Uri;

/**
 * Created by mobimaks on 05.11.2015.
 */
public interface AttachmentResultListener {
    void onResult(final Uri _attachmentUri, final String _result);

    void onError(final Uri _attachmentUri);
}