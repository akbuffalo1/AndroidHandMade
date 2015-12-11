package biz.enon.tra.uae.interfaces;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by mobimaks on 02.11.2015.
 */
public interface SaveStateObject {
    void onRestoreInstanceState(@NonNull final Bundle _savedInstanceState);

    void onSaveInstanceState(@NonNull final Bundle _outState);
}
