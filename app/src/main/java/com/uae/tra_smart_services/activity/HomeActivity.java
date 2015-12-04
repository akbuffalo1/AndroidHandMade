package com.uae.tra_smart_services.activity;

import android.app.Fragment;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.TRAApplication;
import com.uae.tra_smart_services.activity.base.BaseFragmentActivity;
import com.uae.tra_smart_services.adapter.TransactionsAdapter.OnTransactionPressedListener;
import com.uae.tra_smart_services.customviews.HexagonalButtonsLayout;
import com.uae.tra_smart_services.entities.FragmentType;
import com.uae.tra_smart_services.fragment.AboutTraFragment;
import com.uae.tra_smart_services.fragment.AddServiceFragment;
import com.uae.tra_smart_services.fragment.AddServiceFragment.OnFavoriteServicesSelectedListener;
import com.uae.tra_smart_services.fragment.ApprovedDevicesFragment;
import com.uae.tra_smart_services.fragment.ApprovedDevicesFragment.OnDeviceSelectListener;
import com.uae.tra_smart_services.fragment.ComplainAboutServiceFragment;
import com.uae.tra_smart_services.fragment.ComplainAboutTraFragment;
import com.uae.tra_smart_services.fragment.DeviceApprovalFragment;
import com.uae.tra_smart_services.fragment.DomainCheckerFragment;
import com.uae.tra_smart_services.fragment.DomainInfoFragment;
import com.uae.tra_smart_services.fragment.DomainIsAvailableFragment;
import com.uae.tra_smart_services.fragment.DynamicServiceFragment;
import com.uae.tra_smart_services.fragment.EnquiriesFragment;
import com.uae.tra_smart_services.fragment.FavoritesFragment;
import com.uae.tra_smart_services.fragment.FavoritesFragment.OnFavoritesEventListener;
import com.uae.tra_smart_services.fragment.HexagonHomeFragment;
import com.uae.tra_smart_services.fragment.HexagonHomeFragment.OnHeaderStaticServiceSelectedListener;
import com.uae.tra_smart_services.fragment.HexagonHomeFragment.OnOpenUserProfileClickListener;
import com.uae.tra_smart_services.fragment.HexagonHomeFragment.OnServiceSelectListener;
import com.uae.tra_smart_services.fragment.HexagonHomeFragment.OnStaticServiceSelectListener;
import com.uae.tra_smart_services.fragment.InfoHubFragment;
import com.uae.tra_smart_services.fragment.MobileVerificationFragment;
import com.uae.tra_smart_services.fragment.MobileVerificationFragment.OnDeviceVerifiedListener;
import com.uae.tra_smart_services.fragment.MobileVerifiedInfoFragment;
import com.uae.tra_smart_services.fragment.PoorCoverageFragment;
import com.uae.tra_smart_services.fragment.ServiceInfoDetailsFragment;
import com.uae.tra_smart_services.fragment.ServiceInfoFragment;
import com.uae.tra_smart_services.fragment.ServiceInfoFragment.OnOpenServiceInfoDetailsListener;
import com.uae.tra_smart_services.fragment.SettingsFragment;
import com.uae.tra_smart_services.fragment.SettingsFragment.OnOpenAboutTraClickListener;
import com.uae.tra_smart_services.fragment.SuggestionFragment;
import com.uae.tra_smart_services.fragment.TransactionDetailFragment;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.fragment.hexagon_fragment.InnovationsFragment;
import com.uae.tra_smart_services.fragment.hexagon_fragment.NotificationsFragment;
import com.uae.tra_smart_services.fragment.hexagon_fragment.SearchFragment;
import com.uae.tra_smart_services.fragment.spam.ReportSmsSpamFragment;
import com.uae.tra_smart_services.fragment.spam.ReportSpamFragment;
import com.uae.tra_smart_services.fragment.spam.ReportSpamFragment.OnReportSpamServiceSelectListener;
import com.uae.tra_smart_services.fragment.spam.ReportSpamFragment.SpamOption;
import com.uae.tra_smart_services.fragment.spam.ReportWebSpamFragment;
import com.uae.tra_smart_services.fragment.spam.SpamHistoryFragment;
import com.uae.tra_smart_services.fragment.spam.SpamHistoryFragment.OnAddToSpamClickListener;
import com.uae.tra_smart_services.fragment.spam.SpamHistoryFragment.SpamType;
import com.uae.tra_smart_services.fragment.tutorial.TutorialContainerFragment;
import com.uae.tra_smart_services.fragment.tutorial.TutorialContainerFragment.OnTuorialClosedListener;
import com.uae.tra_smart_services.fragment.user_profile.ChangePasswordFragment;
import com.uae.tra_smart_services.fragment.user_profile.EditUserProfileFragment;
import com.uae.tra_smart_services.fragment.user_profile.EditUserProfileFragment.OnUserProfileDataChangeListener;
import com.uae.tra_smart_services.fragment.user_profile.UserProfileFragment;
import com.uae.tra_smart_services.fragment.user_profile.UserProfileFragment.OnUserProfileClickListener;
import com.uae.tra_smart_services.fragment.user_profile.UserProfileFragment.UserProfileAction;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.global.C.ServiceName;
import com.uae.tra_smart_services.global.HeaderStaticService;
import com.uae.tra_smart_services.global.Service;
import com.uae.tra_smart_services.interfaces.OnActivateTutorialListener;
import com.uae.tra_smart_services.interfaces.ToolbarTitleManager;
import com.uae.tra_smart_services.rest.model.response.DomainAvailabilityCheckResponseModel;
import com.uae.tra_smart_services.rest.model.response.DomainInfoCheckResponseModel;
import com.uae.tra_smart_services.rest.model.response.DynamicServiceInfoResponseModel;
import com.uae.tra_smart_services.rest.model.response.GetTransactionResponseModel;
import com.uae.tra_smart_services.rest.model.response.SearchDeviceResponseModel;
import com.uae.tra_smart_services.rest.model.response.UserProfileResponseModel;
import com.uae.tra_smart_services.util.PersistentCookieStore;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;

/**
 * Created by ak-buffalo on 23.07.15.
 */
public class HomeActivity extends BaseFragmentActivity implements //region INTERFACES
        ToolbarTitleManager, OnServiceSelectListener, OnDeviceSelectListener,
        OnBackStackChangedListener, OnStaticServiceSelectListener,
        OnCheckedChangeListener, OnFavoritesEventListener, OnFavoriteServicesSelectedListener,
        OnOpenUserProfileClickListener, OnUserProfileClickListener, OnHeaderStaticServiceSelectedListener,
        OnOpenAboutTraClickListener, OnReportSpamServiceSelectListener, OnAddToSpamClickListener,
        OnActivateTutorialListener, OnDeviceVerifiedListener, OnTuorialClosedListener,
        OnUserProfileDataChangeListener, OnOpenServiceInfoDetailsListener, OnTransactionPressedListener {
    //endregion

    private static final String KEY_CHECKED_TAB_ID = "CHECKED_TAB_ID";
    private static final String KEY_PREVIOUS_CHECKED_TAB_ID = "PREVIOUS_CHECKED_TAB_ID";
    private static final String KEY_SETTINGS_CHANGE_PROCESSED = "SETTINGS_CHANGE_PROCESSED";

    private static final String TAG = "HomeActivity";
    protected static final int REQUEST_CHECK_SETTINGS = 1000;

    private Toolbar mToolbar;
    private RadioGroup bottomNavRadios;

    @IdRes
    private int mCheckedTabId, mPreviousCheckedTabId;

    private boolean mSettingsChangeProcessed;

    @Override
    public final void onCreate(final Bundle _savedInstanceState) {
        getFragmentManager().addOnBackStackChangedListener(this);

        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_home);
        CookieHandler.setDefault(new CookieManager(new PersistentCookieStore(this), CookiePolicy.ACCEPT_ALL));

        if (_savedInstanceState != null) {
            mCheckedTabId = _savedInstanceState.getInt(KEY_CHECKED_TAB_ID);
            mPreviousCheckedTabId = _savedInstanceState.getInt(KEY_PREVIOUS_CHECKED_TAB_ID);
            mSettingsChangeProcessed = _savedInstanceState.getBoolean(KEY_SETTINGS_CHANGE_PROCESSED);
        }

        mToolbar = findView(R.id.toolbar);
        setSupportActionBar(mToolbar);

        bottomNavRadios = findView(R.id.rgBottomNavRadio_AH);
        bottomNavRadios.setOnCheckedChangeListener(this);

        if (!mSettingsChangeProcessed && getIntent().getBooleanExtra(SettingsFragment.CHANGED, false)) {
            mSettingsChangeProcessed = true;
            bottomNavRadios.check(R.id.rbSettings_BNRG);
        } else if (getFragmentManager().findFragmentById(getContainerId()) == null) {
            bottomNavRadios.check(R.id.rbHome_BNRG);
            showTutorialIfNeed();
        }

        onBackStackChanged();
    }

    private void showTutorialIfNeed() {
        if (!PreferenceManager.getDefaultSharedPreferences(this).contains(C.NOT_FIRST_START)) {
            showTutorial();
        }
    }

    private void showTutorial() {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .add(getGlobalContainerId(), TutorialContainerFragment.newInstance())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                final boolean isBackStackEmpty = getFragmentManager().getBackStackEntryCount() == 0;
                if (!isBackStackEmpty) {
                    onBackPressed();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceSelect(final DynamicServiceInfoResponseModel _service, @Nullable Parcelable _data) {
        onServiceSelect(_service, _data, true);
    }

    public void onServiceSelect(final DynamicServiceInfoResponseModel _service, @Nullable Parcelable _data, final boolean _useBackStack) {
        if (_service.needAuth) {
            openFragmentIfAuthorized(DynamicServiceFragment.newInstance(_service), FragmentType.DYNAMIC_SERVICE, _data, _useBackStack);
        } else {
            replaceFragment(DynamicServiceFragment.newInstance(_service), _useBackStack);
        }
    }

    @Override
    public void onServiceSelect(final Service _service, @Nullable Parcelable _data) {
        onServiceSelect(_service, _data, true);
    }

    public void onServiceSelect(final Service _service, @Nullable Parcelable _data, final boolean _useBackStack) {
        switch (_service) {
            case DOMAIN_CHECK:
                replaceFragment(DomainCheckerFragment.newInstance(), _useBackStack);
                break;
            case DOMAIN_CHECK_INFO:
                replaceFragment(DomainInfoFragment.newInstance((DomainInfoCheckResponseModel) _data), _useBackStack);
                break;
            case DOMAIN_CHECK_AVAILABILITY:
                replaceFragment(DomainIsAvailableFragment.newInstance((DomainAvailabilityCheckResponseModel) _data), _useBackStack);
                break;
            case COMPLAIN_ABOUT_PROVIDER:
                openFragmentIfAuthorized(ComplainAboutServiceFragment.newInstance(), _service, _useBackStack);
                break;
            case ENQUIRIES:
                openFragmentIfAuthorized(EnquiriesFragment.newInstance(), _service, _useBackStack);
                break;
            case COMPLAINT_ABOUT_TRA:
                openFragmentIfAuthorized(_data != null ? ComplainAboutTraFragment.newInstance(_data) : ComplainAboutTraFragment.newInstance(), _service, _useBackStack);
                break;
            case SUGGESTION:
                openFragmentIfAuthorized(SuggestionFragment.newInstance(), _service, _useBackStack);
                break;
            case BLOCK_WEBSITE:
                openFragmentIfAuthorized(_data != null ? ReportWebSpamFragment.newInstance(_data) : ReportWebSpamFragment.newInstance(), _service, _useBackStack);
                break;
            case APPROVED_DEVICES:
                replaceFragment(ApprovedDevicesFragment.newInstance(), _useBackStack);
                break;
            case REPORT_SPAM:
                openFragmentIfAuthorized(ReportSmsSpamFragment.newInstance(), _service, _useBackStack);
                break;
            case MOBILE_VERIFICATION:
                replaceFragment(MobileVerificationFragment.newInstance(), _useBackStack);
                break;
            case POOR_COVERAGE:
                replaceFragment(PoorCoverageFragment.newInstance(), _useBackStack);
                break;
            case SMS_SPAM:
                replaceFragment(_data != null ? ReportSmsSpamFragment.newInstance(_data) : ReportSmsSpamFragment.newInstance(), _useBackStack);
                break;

//            case INTERNET_SPEEDTEST:
//                replaceFragment(SpeedTestFragment.newInstance(), _useBackStack);
//                break;
        }
    }

    private void openFragmentIfAuthorized(final BaseFragment _fragment, final Enum _service) {
        openFragmentIfAuthorized(_fragment, _service, null, true);
    }

    private void openFragmentIfAuthorized(final BaseFragment _fragment, final Enum _service, final boolean _useBackStack) {
        openFragmentIfAuthorized(_fragment, _service, null, _useBackStack);
    }

    private void openFragmentIfAuthorized(final BaseFragment _fragment, final Enum _service, final Parcelable _data) {
        openFragmentIfAuthorized(_fragment, _service, _data, true);
    }

    private void openFragmentIfAuthorized(final BaseFragment _fragment, final Enum _service, final Parcelable _data, final boolean _useBackStack) {
        if (TRAApplication.isLoggedIn()) {
            replaceFragment(_fragment, _useBackStack);
        } else {
            Intent intent = AuthorizationActivity.getStartForResultIntent(this, _service);
            intent.putExtra(C.USE_BACK_STACK, _useBackStack);
            intent.putExtra(C.FRAGMENT_DATA, _data);
            startActivityForResult(intent, C.REQUEST_CODE_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(final int _requestCode, final int _resultCode, final Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);
        switch (_requestCode) {
            case C.REQUEST_CODE_LOGIN:
                handleLoginResult(_resultCode, _data);
                break;
            case REQUEST_CHECK_SETTINGS:
                Log.i(TAG, "User agreed to make required location settings changes.");
                getFragmentManager()
                        .findFragmentById(R.id.flContainer_AH)
                        .onActivityResult(_requestCode, _resultCode, _data);
                break;
        }
    }

    private void handleLoginResult(int _resultCode, Intent _data) {
        final boolean uncheckTabIfNotLogged = _data.getBooleanExtra(C.UNCHECK_TAB_IF_NOT_LOGGED_IN, false);
        if (_resultCode == C.LOGIN_SUCCESS) {
            final Enum fragmentType = (Enum) _data.getSerializableExtra(C.FRAGMENT_FOR_REPLACING);
            final boolean useBackStack = _data.getBooleanExtra(C.USE_BACK_STACK, true);
            final Parcelable data = _data.getParcelableExtra(C.FRAGMENT_DATA);
            if (uncheckTabIfNotLogged) {
                mPreviousCheckedTabId = 0;
                clearBackStack();
            }
            openFragmentAfterLogin(fragmentType, data, useBackStack);
        } else if (uncheckTabIfNotLogged) {
            bottomNavRadios.check(mPreviousCheckedTabId);
            mPreviousCheckedTabId = 0;
        }
    }

    private void openFragmentAfterLogin(final Enum _fragmentType, @Nullable Parcelable _data, final boolean _useBackStack) {
        if (_fragmentType instanceof Service) {
            onServiceSelect((Service) _fragmentType, null, _useBackStack);
        } else if (_fragmentType instanceof FragmentType) {
            openFragmentAfterLogin((FragmentType) _fragmentType, _data, _useBackStack);
        }
    }

    private void openFragmentAfterLogin(final FragmentType _fragmentType, @Nullable Parcelable _data, final boolean _useBackStack) {
        switch (_fragmentType) {
            case REPORT_SMS_SPAM:
                replaceFragment(ReportSmsSpamFragment.newInstance(), _useBackStack);
                break;
            case REPORT_WEB_SPAM:
                replaceFragment(ReportWebSpamFragment.newInstance(), _useBackStack);
                break;
            case SPAM_REPORT_HISTORY:
                replaceFragment(SpamHistoryFragment.newInstance(), _useBackStack);
                break;
            case ENQUIRIES:
                replaceFragment(_data != null ? EnquiriesFragment.newInstance(_data) : EnquiriesFragment.newInstance(), _useBackStack);
                break;
            case INFO_HUB:
                replaceFragment(InfoHubFragment.newInstance(), _useBackStack);
                break;
            case INNOVATIONS:
                replaceFragment(InnovationsFragment.newInstance(), _useBackStack);
                break;
            case DYNAMIC_SERVICE:
                replaceFragment(DynamicServiceFragment.newInstance((DynamicServiceInfoResponseModel) _data), _useBackStack);
                break;
        }
    }

    @Override
    public void onStaticServiceSelect(HexagonalButtonsLayout.StaticService _service) {
        switch (_service) {
            case BLOCK_WEBSITE:
                openFragmentIfAuthorized(ReportWebSpamFragment.newInstance(), Service.BLOCK_WEBSITE);
                break;
            case POOR_COVERAGE_SERVICE:
                replaceFragmentWithBackStack(PoorCoverageFragment.newInstance());
                break;
            case VERIFY_DEVICE:
                replaceFragmentWithBackStack(MobileVerificationFragment.newInstance());
                break;
            case SMS_SPAM:
                openFragmentIfAuthorized(ReportSmsSpamFragment.newInstance(), Service.REPORT_SPAM);
                break;
        }
    }

    @Override
    public void onDeviceSelect(int _selectedBrandLogoRes, final SearchDeviceResponseModel.List _device) {
        replaceFragmentWithBackStack(DeviceApprovalFragment.newInstance(_selectedBrandLogoRes, _device));
    }

    @Override
    public void onBackStackChanged() {
        final boolean isBackStackEmpty = getFragmentManager().getBackStackEntryCount() == 0;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(!isBackStackEmpty);
            actionBar.setHomeButtonEnabled(isBackStackEmpty);
            if (isBackStackEmpty) {
                mToolbar.setNavigationIcon(R.mipmap.ic_launcher);
            }
        }
    }

    @Override
    protected int getContainerId() {
        return R.id.flContainer_AH;
    }

    @Override
    protected int getGlobalContainerId() {
        return R.id.rlGlobalContainer_AH;
    }

//    @Override
//    public void onSmsServiceChildSelect(final SmsService _service) {
//        switch (_service) {
//            case REPORT:
//                // not implement
//                replaceFragmentWithBackStack(SmsReportFragment.newInstance());
//                break;
//            case BLOCK:
//                // not implement
//                replaceFragmentWithBackStack(SmsBlockNumberFragment.newInstance());
//                break;
//        }
//    }

    @Override
    public final void onReportSpamServiceSelect(@SpamOption int _service) {
        switch (_service) {
            case ReportSpamFragment.SPAM_OPTION_REPORT_SMS:
                openFragmentIfAuthorized(ReportSmsSpamFragment.newInstance(), FragmentType.REPORT_SMS_SPAM);
                break;
            case ReportSpamFragment.SPAM_OPTION_REPORT_WEB:
                openFragmentIfAuthorized(ReportWebSpamFragment.newInstance(), FragmentType.REPORT_WEB_SPAM);
                break;
            case ReportSpamFragment.SPAM_OPTION_REPORT_HISTORY:
                openFragmentIfAuthorized(SpamHistoryFragment.newInstance(), FragmentType.SPAM_REPORT_HISTORY);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d(getClass().getSimpleName() + "log", "onCheckedChanged");

        if (mCheckedTabId == checkedId) {
            Log.d(getClass().getSimpleName() + "log", "onCheckedChanged mCheckedTabId");
            return;
        } else if (mPreviousCheckedTabId == checkedId) {
            Log.d(getClass().getSimpleName() + "log", "onCheckedChanged mPreviousCheckedTabId");
            mCheckedTabId = checkedId;
            return;
        }
        mPreviousCheckedTabId = 0;

        Log.d(getClass().getSimpleName() + "log", "onCheckedChanged: new check " + getResources().getResourceEntryName(checkedId));

        switch (checkedId) {
            case R.id.rbHome_BNRG:
                clearBackStack();
                replaceFragmentWithOutBackStack(HexagonHomeFragment.newInstance());
                break;
            case R.id.rbFavorites_BNRG:
                clearBackStack();
                replaceFragmentWithOutBackStack(FavoritesFragment.newInstance());
                break;
            case R.id.rbInfoHub_BNRG:
                openInfoHubIfAuthorized(false);
                break;
            case R.id.rbInquiries_BNRG:
                openEnquiriesIfAuthorized(false);
                break;
            case R.id.rbSettings_BNRG:
                clearBackStack();
                replaceFragmentWithOutBackStack(SettingsFragment.newInstance());
                break;
        }
        mCheckedTabId = checkedId;
    }

    private void openEnquiriesIfAuthorized(final boolean _useBackStack) {
        if (TRAApplication.isLoggedIn()) {
            clearBackStack();
            replaceFragment(EnquiriesFragment.newInstance(), _useBackStack);
        } else {
            mPreviousCheckedTabId = mCheckedTabId;
            Intent intent = AuthorizationActivity.getStartForResultIntent(this, FragmentType.ENQUIRIES);
            intent.putExtra(C.USE_BACK_STACK, _useBackStack);
            intent.putExtra(C.UNCHECK_TAB_IF_NOT_LOGGED_IN, true);
            startActivityForResult(intent, C.REQUEST_CODE_LOGIN);
        }
    }

    private void openInfoHubIfAuthorized(final boolean _useBackStack) {
        if (TRAApplication.isLoggedIn()) {
            clearBackStack();
            replaceFragment(InfoHubFragment.newInstance(), _useBackStack);
        } else {
            mPreviousCheckedTabId = mCheckedTabId;
            Intent intent = AuthorizationActivity.getStartForResultIntent(this, FragmentType.INFO_HUB);
            intent.putExtra(C.USE_BACK_STACK, _useBackStack);
            intent.putExtra(C.UNCHECK_TAB_IF_NOT_LOGGED_IN, true);
            startActivityForResult(intent, C.REQUEST_CODE_LOGIN);
        }
    }

    @Override
    public final void onAddFavoritesClick() {
        replaceFragmentWithBackStack(AddServiceFragment.newInstance());
    }

    @Override
    public final void onAddToSpamClick(@SpamType final int _spamType) {
        switch (_spamType) {
            case SpamHistoryFragment.SPAM_SMS:
                openFragmentIfAuthorized(ReportSmsSpamFragment.newInstance(), FragmentType.REPORT_SMS_SPAM);
                break;
            case SpamHistoryFragment.SPAM_WEB:
                openFragmentIfAuthorized(ReportWebSpamFragment.newInstance(), FragmentType.REPORT_WEB_SPAM);
                break;
        }
    }

    @Override
    public final void onOpenServiceClick(final Service _service) {
        onServiceSelect(_service, null);
    }

    @Override
    public final void onFavoriteServicesSelected(final List<Service> _items) {
        getFragmentManager().popBackStackImmediate();
        final FavoritesFragment favoritesFragment = (FavoritesFragment) getFragmentManager().findFragmentById(getContainerId());
        Log.d("Favorites", "Selected items count: " + _items.size());
        favoritesFragment.addServicesToFavorites(_items);
    }

    @Override
    public final void onOpenUserProfileClick(final UserProfileResponseModel _userProfile) {
        replaceFragmentWithBackStack(UserProfileFragment.newInstance(_userProfile));
//        openFragmentIfAuthorized(UserProfileFragment.newInstance(), FragmentType.USER_PROFILE);
    }

    @Override
    public final void onUserProfileItemClick(Fragment _fragment, UserProfileResponseModel _userProfile, @UserProfileAction int _profileItem) {
        switch (_profileItem) {
            case UserProfileFragment.USER_PROFILE_EDIT_PROFILE:
                replaceFragmentWithBackStack(EditUserProfileFragment.newInstance(_userProfile));
                break;
            case UserProfileFragment.USER_PROFILE_CHANGE_PASSWORD:
                replaceFragmentWithBackStack(ChangePasswordFragment.newInstance());
                break;
        }
    }

    @Override
    public final void onUserProfileDataChange(final UserProfileResponseModel _userProfile) {
        final Fragment fragment = getFragmentManager().findFragmentById(getContainerId());
        if (fragment != null && fragment instanceof UserProfileFragment) {
            final UserProfileFragment userProfileFragment = (UserProfileFragment) fragment;
            userProfileFragment.updateUserProfileData(_userProfile);
        }
    }

    @Override
    public void onOpenServiceInfo(final @ServiceName String _serviceName) {
        addFragmentWithBackStackGlobally(ServiceInfoFragment.newInstance(_serviceName));
    }

    @Override
    public void onOpenServiceInfoDetails(final @DrawableRes int _iconSrc, final String _serviceInfo) {
        replaceFragmentWithBackStackGlobally(ServiceInfoDetailsFragment.newInstance(_iconSrc, _serviceInfo));
    }

    @Override
    public void onOpenAboutTraClick() {
        replaceFragmentWithBackStack(AboutTraFragment.newInstance());
    }

    @Override
    public void setToolbarVisibility(boolean _isVisible) {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (_isVisible) {
                actionBar.show();
            } else {
                actionBar.hide();
            }
        }
    }

    @Override
    public void onHeaderStaticServiceSelected(HeaderStaticService _service) {
        switch (_service) {
            case INNOVATIONS:
//                onServiceSelect(Service.SUGGESTION, null);
                openFragmentIfAuthorized(InnovationsFragment.newInstance(), FragmentType.INNOVATIONS);
                break;
            case NOTIFICATION:
                addFragmentWithBackStackGlobally(NotificationsFragment.newInstance());
                break;
            case SEARCH:
                addFragmentWithBackStackGlobally(SearchFragment.newInstance());
                break;
        }
    }

    @Override
    public void onActivateTutorial() {
        clearBackStack();
        replaceFragmentWithOutBackStack(HexagonHomeFragment.newInstance());
        bottomNavRadios.check(R.id.rbHome_BNRG);
        showTutorial();
    }

    @Override
    public void onDeviceVerified(SearchDeviceResponseModel.List _device) {
        replaceFragmentWithBackStack(MobileVerifiedInfoFragment.newInstance(_device));
    }

    @Override
    protected void onSaveInstanceState(final Bundle _outState) {
        _outState.putBoolean(KEY_SETTINGS_CHANGE_PROCESSED, mSettingsChangeProcessed);
        _outState.putInt(KEY_CHECKED_TAB_ID, mCheckedTabId);
        _outState.putInt(KEY_PREVIOUS_CHECKED_TAB_ID, mPreviousCheckedTabId);
        super.onSaveInstanceState(_outState);
    }

    @Override
    public void onTutorialClosed() {
        bottomNavRadios.check(R.id.rbSettings_BNRG);
    }

    @Override
    public void onBackPressed() {
        final BaseFragment globalContainerFragment = (BaseFragment) getFragmentManager().findFragmentById(getGlobalContainerId());
        if (globalContainerFragment != null) {
            if (globalContainerFragment.onBackPressed()) {
                return;
            }
        }

        final BaseFragment fragment = (BaseFragment) getFragmentManager().findFragmentById(getContainerId());
        if (fragment == null || !fragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onTransactionPressed(int[] _icon_color, GetTransactionResponseModel _model) {
        replaceFragmentWithBackStack(TransactionDetailFragment.newInstance(_icon_color, _model));
    }
}
