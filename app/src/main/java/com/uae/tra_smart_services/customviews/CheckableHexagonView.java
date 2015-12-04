package com.uae.tra_smart_services.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;

/**
 * Created by and on 04.12.15.
 */

public class CheckableHexagonView extends HexagonView implements Checkable {
    private boolean mChecked = false;
    public CheckableHexagonView(Context context) {
        super(context);
    }

    public CheckableHexagonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        refreshDrawableState();
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void toggle() {
        setChecked(!mChecked);
    }
}
