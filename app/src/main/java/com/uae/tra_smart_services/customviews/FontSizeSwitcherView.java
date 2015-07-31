package com.uae.tra_smart_services.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.baseentities.BaseCustomSwitcher;


/**
 * Created by Andrey Korneychuk on 23.07.15.
 */
public class FontSizeSwitcherView extends BaseCustomSwitcher implements View.OnClickListener {

    protected int scaleMin, scaleMax, scaleStep, fontScale, fontScaleNew;

    TextView makeBigger, makeSmaller;

    public FontSizeSwitcherView(Context context) {
        super(context);
    }

    public FontSizeSwitcherView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public <T> void initPreferences(T prefs) {
        fontScale = (Integer) prefs;
    }

    @Override
    protected void initData(Context context, AttributeSet attrs){
        TypedArray typedArrayData =
                context.getTheme().obtainStyledAttributes(attrs, R.styleable.FontSizeSwitcherView, 0, 0);
        try {
            scaleMin = typedArrayData.getInt(R.styleable.FontSizeSwitcherView_scaleMin, 5);
            scaleMax = typedArrayData.getInt(R.styleable.FontSizeSwitcherView_scaleMax, 15);
            scaleStep = typedArrayData.getInt(R.styleable.FontSizeSwitcherView_scaleStep, 1);
        } finally {
            typedArrayData.recycle();
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        makeBigger = findView(R.id.tvMakeBigger_SA);
        makeSmaller = findView(R.id.tvMakeSmaller_SA);
    }

    @Override
    protected void initConfigs() {
        if (fontScale < scaleMax){
            bindView(makeBigger);
        } else {
            unBindView(makeBigger);
        }

        if (fontScale > scaleMin){
            bindView(makeSmaller);
        } else {
            unBindView(makeSmaller);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fontsize_swither;
    }

    @Override
    public Type getType() {
        return Type.FONT;
    }

    @Override
    public void onClick(View view) {
        fontScale = fontScaleNew == 0 ? fontScale : fontScaleNew;
        switch (view.getId()){
            case R.id.tvMakeBigger_SA:
                makeTextBigger(view);
                break;
            case R.id.tvMakeSmaller_SA:
                makeTextSmaller(view);
                break;
        }
        mSettingsChangeListener.onSettingsChanged(this, fontScaleNew);
    }

    private void makeTextBigger(View view){
        if ((fontScaleNew = fontScale + scaleStep) >= scaleMax) {
            unBindView(view);
        } else if((fontScaleNew = fontScale + scaleStep) == scaleMin + scaleStep){
            bindView(makeSmaller);
        }
    }

    private void makeTextSmaller(View view){
        if ((fontScaleNew = fontScale - scaleStep) <= scaleMin) {
            unBindView(view);
        } else if((fontScaleNew = fontScale - scaleStep) == scaleMax - scaleStep){
            bindView(makeBigger);
        }
    }

    @Override
    protected void bindView(View view){
        view.setOnClickListener(this);
        ((TextView)view).setTextColor(getResources().getColor(R.color.hex_black_color));
    }

    @Override
    protected void unBindView(View view){
        view.setOnClickListener(null);
        ((TextView)view).setTextColor(getResources().getColor(R.color.hex_color_light_gray));
    }
}