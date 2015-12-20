package biz.enon.tra.uae.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import biz.enon.tra.uae.BuildConfig;
import biz.enon.tra.uae.R;
import biz.enon.tra.uae.activity.HomeActivity;
import biz.enon.tra.uae.baseentities.BaseCustomSwitcher;
import biz.enon.tra.uae.customviews.FontSizeSwitcherView;
import biz.enon.tra.uae.customviews.LanguageSwitcherView;
import biz.enon.tra.uae.customviews.ThemeSwitcherView;
import biz.enon.tra.uae.dialog.AlertDialogFragment.OnOkListener;
import biz.enon.tra.uae.entities.CustomFilterPool;
import biz.enon.tra.uae.entities.Filter;
import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.global.ServerConstants;
import biz.enon.tra.uae.interfaces.OnActivateTutorialListener;
import biz.enon.tra.uae.interfaces.SettingsChangeListener;
import biz.enon.tra.uae.util.ImageUtils;

/**
 * Created by ak-buffalo on 30.07.15.
 */
public class SettingsFragment extends BaseFragment
        implements SettingsChangeListener, OnClickListener, OnOkListener, OnCheckedChangeListener {

    public static final String CHANGED = "changed";

    private LinearLayout llAboutTRA;
    private SwitchCompat swBlackAndWhiteMode, swActivateTutorial;
    private TextView tvVersionName;

    private FontSizeSwitcherView fontSwitch;
    private LanguageSwitcherView langSwitch;
    private ThemeSwitcherView themeSwitch;

    private SharedPreferences mPrefs;

    private OnOpenAboutTraClickListener mOpenAboutTraClickListener;
    private OnActivateTutorialListener mOnActivateTutorialListener;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        if (_activity instanceof OnOpenAboutTraClickListener) {
            mOpenAboutTraClickListener = (OnOpenAboutTraClickListener) _activity;
        }
        if (_activity instanceof OnActivateTutorialListener) {
            mOnActivateTutorialListener = (OnActivateTutorialListener) _activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    protected void initViews() {
        super.initViews();
        swBlackAndWhiteMode = findView(R.id.swBlackAndWhiteMode_FS);
        swBlackAndWhiteMode.setChecked(mPrefs.getBoolean(C.KEY_BLACK_AND_WHITE_MODE, false));
        llAboutTRA = findView(R.id.llAboutTRA_FS);
        tvVersionName = findView(R.id.tvVersionName_FS);
        swActivateTutorial = findView(R.id.swActivateTutorial_FS);
        swActivateTutorial.setChecked(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        globalInitViews();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        swBlackAndWhiteMode.setOnCheckedChangeListener(this);
        swActivateTutorial.setOnCheckedChangeListener(this);
        llAboutTRA.setOnClickListener(this);
    }

    private static CustomFilterPool<String> filters = new CustomFilterPool<String>() {
        {
            addFilter(new Filter<String>() {
                @Override
                public boolean check(String _data) {
                    return Patterns.WEB_URL.matcher(_data).matches();
                }
            });
        }
    };

    private void globalInitViews() {
        langSwitch = findView(R.id.cvLangSwitch);
        langSwitch.globalInit();
        langSwitch.registerObserver(this);

        fontSwitch = findView(R.id.cvFontSwitch);
        fontSwitch.globalInit(mPrefs.getFloat(BaseCustomSwitcher.Type.FONT.toString(), 1f));
        fontSwitch.registerObserver(this);

        themeSwitch = findView(R.id.cvThemeSwitch);
        themeSwitch.globalInit(mThemaDefiner.getThemeStringValue());
        themeSwitch.registerObserver(this);

        tvVersionName.setHint("v" + BuildConfig.VERSION_NAME);
    }

    @Override
    public void onSettingsChanged(BaseCustomSwitcher caller, Object data) {
        switch (caller.getType()) {
            case LANGUAGE:
                updateLocale((String) data);
                break;
            case FONT:
                updateFont((float) data);
                break;
            case THEME:
                updateTheme((String) data);
                break;
        }
        getActivity().finish();
        restartActivity();
    }

    private void updateLocale(String _lang) {
        mPrefs.edit()
                .putString(
                        BaseCustomSwitcher.Type.LANGUAGE.toString(),
                        _lang)
                .commit();
    }

    private void updateFont(final float _scale) {
        mPrefs.edit()
                .putFloat(
                        BaseCustomSwitcher.Type.FONT.toString(),
                        _scale)
                .commit();
    }

    private void updateTheme(String _themeStrValue) {
        mPrefs.edit()
                .putString(BaseCustomSwitcher.Type.THEME.toString(), _themeStrValue)
                .putBoolean(C.KEY_BLACK_AND_WHITE_MODE, false)
                .commit();
        ImageUtils.setBlackAndWhiteMode(false);
    }

    private void restartActivity() {
        Intent refresh = new Intent(getActivity(), HomeActivity.class);
        refresh.putExtra(CHANGED, true);
        refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
    }

    @Override
    public final void onCheckedChanged(final CompoundButton _buttonView, final boolean _isChecked) {
        switch (_buttonView.getId()) {
            case R.id.swBlackAndWhiteMode_FS:
                changeBlackAndWhite(_isChecked);
                break;
            case R.id.swActivateTutorial_FS:
                activateTutorial();
                break;
        }
    }

    private void changeBlackAndWhite(final boolean _isChecked) {
        mPrefs.edit()
                .putBoolean(C.KEY_BLACK_AND_WHITE_MODE, _isChecked)
                .commit();
        ImageUtils.setBlackAndWhiteMode(_isChecked);
        restartActivity();
    }

    private void activateTutorial() {
        mOnActivateTutorialListener.onActivateTutorial();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llAboutTRA_FS:
                if (mOpenAboutTraClickListener != null) {
                    mOpenAboutTraClickListener.onOpenAboutTraClick();
                }
                break;
        }
    }

    @Override
    public void onDetach() {
        mOpenAboutTraClickListener = null;
        super.onDetach();
    }

    @Override
    public void onOkPressed(final int _mMessageId) {
        // Unimplemented method
        // Used exceptionally to specify OK button in dialog
    }

    public interface OnOpenAboutTraClickListener {
        void onOpenAboutTraClick();
    }

    @Override
    protected int getTitle() {
        return R.string.str_settings;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_settings;
    }
}
