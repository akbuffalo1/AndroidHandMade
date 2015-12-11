package biz.enon.tra.uae.customviews;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.enon.tra.uae.R;

import java.util.Locale;

import biz.enon.tra.uae.baseentities.BaseCustomSwitcher;

/**
 * Created by ak-buffalo on 24.07.15.
 */
public class LanguageSwitcherView extends BaseCustomSwitcher implements View.OnClickListener {
    private TextView mAcitveLang, enLang, arLang;
    private TextView[] langViews;

    public LanguageSwitcherView(Context context) {
        super(context);
    }

    public LanguageSwitcherView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public <T> void initPreferences(T prefs){}

    @Override
    protected void initViews() {
        super.initViews();
        setOrientation(HORIZONTAL);
        langViews = new TextView[]{
                enLang = findView(R.id.tvEnglishLang_LS),
                arLang = findView(R.id.tvArabicLang_LS)
        };
        for (TextView langView : langViews){
            if(Locale.getDefault().getLanguage().equals(langView.getTag().toString())){
                unBindView(langView);
            } else {
                bindView(langView);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_language_switcher;
    }

    @Override
    public Type getType() {
        return Type.LANGUAGE;
    }

    @Override
    protected void bindView(View view){
        view.setBackgroundColor(Color.LTGRAY);
        view.setOnClickListener(this);
    }

    @Override
    protected void unBindView(View view){
        view.setOnClickListener(null);
    }

    @Override
    public void onClick(View view) {
        mSettingsChangeListener.onSettingsChanged(this, view.getTag());
    }
}