package biz.enon.tra.uae.global;

import android.content.Context;
import android.support.annotation.StringRes;

import com.enon.tra.uae.R;

/**
 * Created by mobimaks on 11.08.2015.
 */
public enum AttachmentOption {
    CAMERA {
        @Override
        public int getTitleRes() {
            return R.string.fragment_complain_about_service_camera;
        }
    },
    GALLERY {
        @Override
        public int getTitleRes() {
            return R.string.fragment_complain_about_service_gallery;
        }
    },
    ATTACHMENT{
        @Override
        public int getTitleRes() {
            return R.string.app_name;
        }
    },
    DELETE_ATTACHMENT {
        @Override
        public int getTitleRes() {
            return R.string.attachment_manager_delete_attachment;
        }
    };

    @StringRes
    public abstract int getTitleRes();

    public static String[] toStringArray(final AttachmentOption[] _attachmentOptions, final Context _context) {
        final String[] strProviders = new String[_attachmentOptions.length];
        for (int i = 0; i < _attachmentOptions.length; i++) {
            strProviders[i] = _context.getString(_attachmentOptions[i].getTitleRes());
        }
        return strProviders;
    }

    public static String[] toStringArray(final Context _context) {
        return toStringArray(values(), _context);
    }
}
