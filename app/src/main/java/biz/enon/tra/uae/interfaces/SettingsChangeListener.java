package biz.enon.tra.uae.interfaces;

        import biz.enon.tra.uae.baseentities.BaseCustomSwitcher;

/**
 * Created by ak-buffalo on 27.07.15.
 */
public interface SettingsChangeListener {
    void onSettingsChanged(BaseCustomSwitcher caller, Object data);
}
