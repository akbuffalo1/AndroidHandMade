package com.uae.tra_smart_services.baseentities;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.uae.tra_smart_services.interfaces.I_SettingsChanged;

/**
 * Created by Andrey Korneychuk on 24.07.15.
 */
public abstract class BaseCustomSwitcher extends LinearLayout{

    protected I_SettingsChanged mSettingsChangeListener;

    public enum Type {
        FONT, LANGUAGE, THEME
    }

    public BaseCustomSwitcher(Context context) {
        super(context);
    }

    public BaseCustomSwitcher(Context context, AttributeSet _attrs) {
        super(context, _attrs);
        initData(getContext(), _attrs);
        setWillNotDraw(false);
    }

    public <T> void globalInit(T prefs){
        initPreferences(prefs);
        globalInit();
    }

    public void globalInit(){
        initFactories();
        initViews();
        initListeners();
        initConfigs();
    }

    public abstract <T> void initPreferences(T prefs);

    protected void initFactories() {}

    protected void initData(Context context, AttributeSet attrs) {}

    protected void initViews() {
        if (getLayoutId() > 0){
            inflate(getContext(), getLayoutId(), this);
        }
    }

    protected void initListeners() {}

    protected void initConfigs() {}

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void registerObserver(I_SettingsChanged _listener){
        try {
            mSettingsChangeListener = _listener;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString()
                    + " must implement I_SettingsChanged interface");
        }
    }

    protected abstract void bindView(View view);

    protected abstract void unBindView(View view);

    protected abstract @LayoutRes int getLayoutId();

    public abstract Type getType();

    public final <T extends View> T findView(@IdRes int id) {
        return (T) findViewById(id);
    }
}