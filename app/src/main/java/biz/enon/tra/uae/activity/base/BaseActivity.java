package biz.enon.tra.uae.activity.base;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.enon.tra.uae.R;

import java.util.Locale;

import biz.enon.tra.uae.baseentities.BaseCustomSwitcher;
import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.util.ImageUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static biz.enon.tra.uae.global.C.ARABIC;
import static biz.enon.tra.uae.global.C.AR_SCALE_COEFFICIENT;
import static biz.enon.tra.uae.global.C.ENGLISH;
import static biz.enon.tra.uae.global.C.MEDIUM_FONT_SCALE;

/**
 * Created by Mikazme on 22/07/2015.
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseFragment.ThemaDefiner {

    private String mThemaStringValue;
    private Float mFontSize;
    private String mLanguage;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setApplicationTheme();
        setApplicationLanguage();
        setApplicationFontSize();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public final <T extends View> T findView(@IdRes int id) {
        return (T) findViewById(id);
    }

    @Override
    public String getThemeStringValue() {
        return mThemaStringValue;
    }

    public void setApplicationTheme() {
        if (ImageUtils.isBlackAndWhiteMode(this)) {
            mThemaStringValue = C.BLACK_AND_WHITE_THEME;
        } else {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            mThemaStringValue = prefs.getString(BaseCustomSwitcher.Type.THEME.toString(), C.ORANGE_THEME);
        }
        setTheme(getResources().getIdentifier(mThemaStringValue, "style", getPackageName()));
    }

    public void setApplicationFontSize() {
        mFontSize = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getFloat(BaseCustomSwitcher.Type.FONT.toString(), MEDIUM_FONT_SCALE);
        Configuration config = getResources().getConfiguration();
        config.fontScale = (mLanguage.equals(ARABIC)) ? (mFontSize * AR_SCALE_COEFFICIENT) : mFontSize;
        getResources().updateConfiguration(config, null);
    }

    public final void setApplicationLanguage() {
        mLanguage = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(BaseCustomSwitcher.Type.LANGUAGE.toString(), (Locale.getDefault().getLanguage().equals(ARABIC)) ? ARABIC : ENGLISH);

        if (mLanguage.equals(ENGLISH)) {
            initDefaultFont("fonts/Lato-Regular.ttf");
        } else {
            initDefaultFont("fonts/DroidKufi-Regular.ttf");
        }

        Locale locale = new Locale(mLanguage);
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        config.locale = locale;
        getResources().updateConfiguration(config, null);
    }

    public final void initDefaultFont(final String _fontPath) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(_fontPath)
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}