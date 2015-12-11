package biz.enon.tra.uae.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import biz.enon.tra.uae.R;

/**
 * Created by mobimaks on 04.09.2015.
 */
public final class ServiceHeader extends LinearLayout {

    private final int DEFAULT_PADDING = Math.round(14 * getResources().getDisplayMetrics().density);

    private HexagonView hvIcon;
    private TextView tvTitle;

    public ServiceHeader(final Context _context) {
        this(_context, null);
    }

    public ServiceHeader(final Context _context, final AttributeSet _attrs) {
        super(_context, _attrs);
        initView();
        initProperties(_context, _attrs);
    }

    private void initView() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        inflate(getContext(), R.layout.layout_domain_check_common_header, this);
        setPaddingRelative(0, DEFAULT_PADDING, 0, DEFAULT_PADDING);

        hvIcon = (HexagonView) findViewById(R.id.hvIcon_LIIHS);
        tvTitle = (TextView) findViewById(R.id.tvTitle_LIHLI);
    }

    private void initProperties(final Context _context, final AttributeSet _attrs) {
        TypedArray array = _context.obtainStyledAttributes(_attrs, R.styleable.ServiceHeader);
        try {
            tvTitle.setText(array.getString(R.styleable.ServiceHeader_headerTitle));
            Drawable drawable = array.getDrawable(R.styleable.ServiceHeader_headerIconSrc);
            if (drawable != null) {
                Drawable wrappedDrawable = DrawableCompat.wrap(drawable.mutate());
                DrawableCompat.setTint(wrappedDrawable, Color.WHITE);
                hvIcon.setHexagonSrcDrawable(drawable);
            }
        } finally {
            array.recycle();
        }
    }


    public void setTitle(String _title) {
        tvTitle.setText(_title);
    }

    public void setTitle(@StringRes int _titleRes) {
        tvTitle.setText(_titleRes);
    }

    public void setHexagonIcon(Drawable _icon) {
        hvIcon.setHexagonSrcDrawable(_icon);
    }

    public void setHexagonIcon(@DrawableRes int _iconRes) {
        hvIcon.setHexagonSrcDrawable(_iconRes);
    }

}
