package biz.enon.tra.uae.interfaces;

import android.support.annotation.StringRes;

/**
 * Created by Mikazme on 23/07/2015.
 */
public interface ToolbarTitleManager {
    void setTitle(@StringRes int _resId);

    void setTitle(CharSequence _title);

    void setToolbarVisibility(final boolean _isVisible);
}
