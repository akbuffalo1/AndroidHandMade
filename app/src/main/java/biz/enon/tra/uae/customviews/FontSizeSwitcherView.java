package biz.enon.tra.uae.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.enon.tra.uae.R;
import com.google.gson.internal.LinkedHashTreeMap;

import java.util.Map;

import biz.enon.tra.uae.baseentities.BaseCustomSwitcher;
import biz.enon.tra.uae.global.C;

/**
 * Created by ak-buffalo on 23.07.15.
 */
public class FontSizeSwitcherView extends BaseCustomSwitcher implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    protected float mScaleMin, mScaleMax, mFontScale, mFontScaleNew;

    TextView makeBigger, makeMedium, makeSmaller;
    SeekBar seekbar;
    LinearLayout valuesContainer;
    Float[] keyset;

    private Map<Float, TextView> scaleSeekbarMap = new LinkedHashTreeMap<>();

    public FontSizeSwitcherView(Context _context) {
        super(_context);
    }

    public FontSizeSwitcherView(Context _context, AttributeSet _attrs) {
        super(_context, _attrs);
    }

    @Override
    public <T> void initPreferences(T _prefs) {
        mFontScale = (Float) _prefs;
    }

    @Override
    protected void initData(Context context, AttributeSet attrs) {
        TypedArray typedArrayData =
                context.getTheme().obtainStyledAttributes(attrs, R.styleable.FontSizeSwitcherView, 0, 0);
        mFontScale = getResources().getConfiguration().fontScale;
        try {
            mScaleMin = typedArrayData.getFloat(R.styleable.FontSizeSwitcherView_scaleMin, C.SMALL_FONT_SCALE);
            mScaleMax = typedArrayData.getFloat(R.styleable.FontSizeSwitcherView_scaleMax, C.LARGE_FONT_SCALE);
        } finally {
            typedArrayData.recycle();
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        setOrientation(VERTICAL);
        seekbar = findView(R.id.sbFontSizeSwitcher_LFS);
        valuesContainer = findView(R.id.lL_fontSizeValuesContainer_LFS);
        /**
         * Make sure the elements should be added by growth order
         * START
         * */
        makeSmaller = findView(R.id.tvMakeSmaller_SA);
        scaleSeekbarMap.put(mScaleMin, makeSmaller);

        makeMedium = findView(R.id.tvMakeMedium_SA);
        scaleSeekbarMap.put(C.MEDIUM_FONT_SCALE, makeMedium);

        makeBigger = findView(R.id.tvMakeBigger_SA);
        scaleSeekbarMap.put(mScaleMax, makeBigger);
        /** END */
    }

    @Override
    protected final void initConfigs() {
        int i = 0;
        for(Map.Entry<Float, TextView> entry : scaleSeekbarMap.entrySet()){
            if(entry.getKey() == mFontScale){
                seekbar.setProgress(i);
                unBindView(entry.getValue());
            } else {
                bindView(entry.getValue());
            }
            i++;
        }
        seekbar.setOnSeekBarChangeListener(this);
        keyset = new Float[scaleSeekbarMap.size()];
        scaleSeekbarMap.keySet().toArray(keyset);
    }

    @Override
    protected final int getLayoutId() {
        return R.layout.layout_fontsize_swither;
    }

    @Override
    public Type getType() {
        return Type.FONT;
    }

    @Override
    public final void onClick(final View _view) {
        bindView(scaleSeekbarMap.get(mFontScale));
        mSettingsChangeListener.onSettingsChanged(this, Float.valueOf((String) _view.getTag()));
    }

    @Override
    protected final void bindView(final View _view) {
        _view.setOnClickListener(this);
        ((TextView) _view).setTextColor(Color.LTGRAY);
    }

    @Override
    protected final void unBindView(final View _view) {
        _view.setOnClickListener(null);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        unBindView(scaleSeekbarMap.get(keyset[progress]));
        bindView(scaleSeekbarMap.get(mFontScale));
        mSettingsChangeListener.onSettingsChanged(this, keyset[progress]);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Has not to be implemented
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Has not to be implemented
    }
}