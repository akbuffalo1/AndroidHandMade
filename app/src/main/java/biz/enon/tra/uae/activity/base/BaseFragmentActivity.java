package biz.enon.tra.uae.activity.base;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.inputmethod.InputMethodManager;

import com.octo.android.robospice.SpiceManager;

import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.fragment.user_profile.UserProfileFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.interfaces.SpiceLoader;
import biz.enon.tra.uae.rest.DynamicRestService;
import biz.enon.tra.uae.rest.TRARestService;

/**
 * Created by Mikazme on 22/07/2015.
 */
public abstract class BaseFragmentActivity extends BaseActivity implements SpiceLoader {

    private InputMethodManager mInputMethodManager;
    private SpiceManager mSpiceManager = new SpiceManager(TRARestService.class);
    private SpiceManager mDynamicSpiceManager = new SpiceManager(DynamicRestService.class);

    @CallSuper
    @Override
    public void onStart() {
        super.onStart();
        mSpiceManager.start(this);
        mDynamicSpiceManager.start(this);
    }

    @CallSuper
    @Override
    public void onStop() {
        mDynamicSpiceManager.shouldStop();
        mSpiceManager.shouldStop();
        super.onStop();
    }

    @Override
    public final SpiceManager getSpiceManager(final @C.SpiceManager int _spiceManager) {
        if (_spiceManager == C.SpiceManager.DYNAMIC_SERVICES_API) {
            return mDynamicSpiceManager;
        } else {
            return mSpiceManager;
        }
    }

    @IdRes
    protected abstract int getContainerId();

    protected final void addFragmentWithBackStackGlobally(final @NonNull BaseFragment _fragment) {
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .add(getGlobalContainerId(), _fragment)
                .commit();
    }

    @IdRes
    protected abstract int getGlobalContainerId();

    protected final void addFragmentWithOutBackStack(final @NonNull BaseFragment _fragment) {
        getFragmentManager()
                .beginTransaction()
                .add(getContainerId(), _fragment)
                .commit();
    }

    public final void replaceFragment(final @NonNull BaseFragment _fragment, final boolean _useBackstack) {
        if (_useBackstack) {
            replaceFragmentWithBackStack(_fragment);
        } else {
            replaceFragmentWithOutBackStack(_fragment);
        }
    }

    public final void replaceFragmentWithBackStack(final @NonNull BaseFragment _fragment) {
        hideKeyboard();
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(getContainerId(), _fragment)
                .commit();
    }

    public final void replaceFragmentWithBackStack(final @NonNull Fragment _targetFragment, final @NonNull BaseFragment _fragment) {
        hideKeyboard();
        _fragment.setTargetFragment(_targetFragment, UserProfileFragment.LOGOUT_REQUEST_CODE);
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(getContainerId(), _fragment)
                .commit();
    }

    public final void replaceFragmentWithOutBackStack(final @NonNull BaseFragment _fragment) {
        hideKeyboard();
        getFragmentManager()
                .beginTransaction()
                .replace(getContainerId(), _fragment)
                .commit();
    }

    protected final void replaceFragmentWithBackStackGlobally(final @NonNull BaseFragment _fragment) {
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .add(getGlobalContainerId(), _fragment)
                .commit();
    }

    public final void clearBackStack() {
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public final void popBackStack() {
        getFragmentManager().popBackStack();
        hideKeyboard();
    }

    protected final void hideKeyboard() {
        if (mInputMethodManager == null) {
            mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
//        if (getCurrentFocus() != null) {
        mInputMethodManager.hideSoftInputFromWindow(findView(getContainerId()).getWindowToken(), 0);
//        }
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            popBackStack();
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            hideKeyboard();
        } else {
            finish();
        }
    }
}
