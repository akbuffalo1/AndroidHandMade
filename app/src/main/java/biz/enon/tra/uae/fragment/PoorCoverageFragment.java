package biz.enon.tra.uae.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.customviews.LoaderView;
import biz.enon.tra.uae.dialog.AlertDialogFragment.OnOkListener;
import biz.enon.tra.uae.dialog.SingleChoiceDialog;
import biz.enon.tra.uae.entities.Permission;
import biz.enon.tra.uae.fragment.base.BaseServiceFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.global.LocationType;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.interfaces.Loader;
import biz.enon.tra.uae.interfaces.OnOpenPermissionExplanationDialogListener;
import biz.enon.tra.uae.manager.PermissionManager;
import biz.enon.tra.uae.rest.model.request.PoorCoverageRequestModel;
import biz.enon.tra.uae.rest.robo_requests.GeoLocationRequest;
import biz.enon.tra.uae.rest.robo_requests.PoorCoverageRequest;
import biz.enon.tra.uae.util.ImageUtils;
import biz.enon.tra.uae.util.Logger;
import retrofit.client.Response;

/**
 * Created by ak-buffalo on 11.08.15.
 */
public class PoorCoverageFragment extends BaseServiceFragment implements //region INTERFACES
        OnOkListener, /*OnItemPickListener,*/
        ConnectionCallbacks, OnConnectionFailedListener,
        OnSeekBarChangeListener, /*OnClickListener,*/ ResultCallback<LocationSettingsResult>,
        LocationListener, PermissionManager.OnPermissionRequestSuccessListener, OnOpenPermissionExplanationDialogListener, OnFocusChangeListener,
        OnMapReadyCallback {
    //endregion

    //region CONSTANTS
    private static final String TAG = "PoorCoverageFragment";
    private static final int REQUEST_CHECK_SETTINGS = 1000;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int LOCATION_PERMISSION_REQUEST = 0;
    private static final List<Permission> LOCATION_PERMISSION_LIST = new ArrayList<>();

    static {
        LOCATION_PERMISSION_LIST.add(
                new Permission(Manifest.permission.ACCESS_FINE_LOCATION, R.string.fragment_poor_coverage_location_permission_explanation));
    }
    //endregion

    //region MEMBERS
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates = false;
    private String mLastUpdateTime = "";
    private PoorCoverageRequest mPoorCoverageRequest;
    private PoorCoverageRequestModel mLocationModel = new PoorCoverageRequestModel();
    private TelephonyManager mTelephonyManager;
    private SignalStrengthListener mSignalStrengthListener;

    private GoogleMap mGoogleMap;
    private boolean isLoaderAdded;
    private PermissionManager mLocationPermissionManager;
    //endregion

    //region VIEWS
    private SingleChoiceDialog locationTypeChooser;
    private TextView tvSignalLevel;
    private EditText etLocation;
    private SeekBar sbPoorCoverage;
    private ProgressBar sbProgressBar;
    private MapView mvMap;
    //endregion

    public static PoorCoverageFragment newInstance() {
        return new PoorCoverageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mLocationPermissionManager = new PermissionManager(getActivity(), LOCATION_PERMISSION_LIST, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_send, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                hideKeyboard(tvSignalLevel);
                collectDataAndSendToServer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViews() {
        super.initViews();
        etLocation = findView(R.id.etLocation_FPC);
        etLocation.clearFocus();
        setCapitalizeTextWatcher(etLocation);
        sbPoorCoverage = findView(R.id.sbPoorCoverage_FPC);
        sbProgressBar = findView(R.id.pbFindLoc_FPC);
        tvSignalLevel = findView(R.id.tvSignalLevel_FPC);
        tvSignalLevel.setText(getResources().getStringArray(R.array.fragment_poor_coverage_signal_levels)[0]);

        mvMap = findView(R.id.mvMap_FPC);
        mvMap.getMapAsync(this);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        mLocationPermissionManager.setRequestSuccessListener(this);
        sbPoorCoverage.setOnSeekBarChangeListener(this);
    }

    private void removeListeners() {
        sbPoorCoverage.setOnSeekBarChangeListener(null);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus && !isLoaderAdded) {
            locationTypeChooser = SingleChoiceDialog.newInstance(PoorCoverageFragment.this,
                    R.string.str_select_location_type, LocationType.toStringResArray());
            locationTypeChooser.show(getFragmentManager());
        }
    }

    private void createSignalStrengthListener() {
        mSignalStrengthListener = new SignalStrengthListener();
        mTelephonyManager = ((TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE));
        mTelephonyManager.listen(mSignalStrengthListener, SignalStrengthListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    protected void initData() {
        super.initData();
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        createSignalStrengthListener();
    }

    @Override
    public void onActivityCreated(Bundle _savedInstanceState) {
        super.onActivityCreated(_savedInstanceState);
        hideKeyboard(tvSignalLevel);
        if (_savedInstanceState != null) {
            mLocationPermissionManager.onRestoreInstanceState(_savedInstanceState);
            mLocationPermissionManager.removeSavedState(_savedInstanceState);
            if (mLocationPermissionManager.isAllPermissionsChecked()) {
                checkLocationSettings();
            }
        } else {
            checkLocationSettingsIfPermissionGranted();
        }
        mvMap.onCreate(_savedInstanceState);
    }

    @Override
    public void onMapReady(final GoogleMap _googleMap) {
        mGoogleMap = _googleMap;
        if (mLocationPermissionManager.isAllPermissionsChecked()) {
            invalidateMapLocation();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        getSpiceManager().addListenerIfPending(Response.class, TAG, new PoorCoverageRequestListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        mvMap.onResume();
    }

    @Override
    public final void onOpenPermissionExplanationDialog(int _requestCode, String _explanation) {
        showMessage(_explanation);
    }

    @Override
    public final void onOkPressed(final int _mMessageId) {
        mLocationPermissionManager.onConfirmPermissionExplanationDialog(this, LOCATION_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int _requestCode, @NonNull String[] _permissions, @NonNull int[] _grantResults) {
        if (_requestCode == LOCATION_PERMISSION_REQUEST) {
            if (!mLocationPermissionManager.onRequestPermissionsResult(this, _requestCode, _permissions, _grantResults)) {
                super.onRequestPermissionsResult(_requestCode, _permissions, _grantResults);
            }
        } else {
            super.onRequestPermissionsResult(_requestCode, _permissions, _grantResults);
        }
    }

    @Override
    public final void onPermissionRequestSuccess(Fragment _fragment, int _requestCode) {
        checkLocationSettings();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        sbProgressBar.setVisibility(View.INVISIBLE);
                        etLocation.requestFocus();
                        break;
                }
                break;
        }
    }

    @Override
    public void onPause() {
        mvMap.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        removeListeners();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
    }

    protected void buildLocationSettingsRequest() {
        mLocationSettingsRequest =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(mLocationRequest)
                        .setAlwaysShow(true)
                        .build();
    }

    protected void checkLocationSettings() {
        sbProgressBar.setVisibility(View.VISIBLE);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "Starting location update.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the location settings dialog");
                try {
                    status.startResolutionForResult(getActivity(), 1000);
                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here");
                break;
        }
    }

    private class SignalStrengthListener extends PhoneStateListener {

        private static final float MAX_GSM_LEVEL = 31;
        private static final float MAX_SEEK_BAR_LEVEL = 4;
        private static final float ERROR_SIGNAL_LEVEL = 99;

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int strengthAmplitude = signalStrength.getLevel();
                sbPoorCoverage.setProgress(strengthAmplitude);
                Logger.d("onSignalStrengthsChanged", "SignalStrengths = " + strengthAmplitude);
                sbPoorCoverage.setEnabled(false);
                super.onSignalStrengthsChanged(signalStrength);
            } else if (signalStrength.isGsm()) {
                int gsmSignalStrength = signalStrength.getGsmSignalStrength();
                if (gsmSignalStrength != ERROR_SIGNAL_LEVEL) {
                    int strengthAmplitude = Math.round(signalStrength.getGsmSignalStrength() / MAX_GSM_LEVEL * MAX_SEEK_BAR_LEVEL);
                    sbPoorCoverage.setProgress(strengthAmplitude);
                    Logger.d("onSignalStrengthsChanged", "SignalStrengths(pre-M) = " + strengthAmplitude);
                    sbPoorCoverage.setEnabled(false);
                }
            } else {
                sbPoorCoverage.setEnabled(true);
                mTelephonyManager.listen(mSignalStrengthListener, SignalStrengthListener.LISTEN_NONE);
            }
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
            }
        });
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
            }
        });
    }

    private void collectDataAndSendToServer() {
        mLocationModel.setAddress(etLocation.getText().toString());
        mLocationModel.setSignalLevel(sbProgressBar.getProgress() + 1);
        if (TextUtils.isEmpty(mLocationModel.getAddress()) && mLocationModel.getLocation() == null) {
            Toast.makeText(getActivity(), R.string.str_location_error_message, Toast.LENGTH_SHORT).show();
            return;
        }

        loaderOverlayShow(getString(R.string.str_sending), this);
        loaderOverlayButtonBehavior(new Loader.BackButton() {
            @Override
            public void onBackButtonPressed(LoaderView.State _currentState) {
                getFragmentManager().popBackStack();
                if (_currentState == LoaderView.State.FAILURE || _currentState == LoaderView.State.SUCCESS) {
                    getFragmentManager().popBackStack();
                }
            }
        });
        getSpiceManager().execute(
                mPoorCoverageRequest = new PoorCoverageRequest(
                        mLocationModel
                ), TAG, DurationInMillis.ALWAYS_EXPIRED,
                new PoorCoverageRequestListener()
        );
    }

    private void defineUserFriendlyAddress() {
        final Geocoder geocoder = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
        getSpiceManager().execute(
                new GeoLocationRequest(geocoder, mCurrentLocation),
                new GeoLocationRequestListener()
        );
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        defineUserFriendlyAddress();
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        stopLocationUpdates();
        invalidateMapLocation();
    }

    private void invalidateMapLocation() {
        if (mGoogleMap != null && mCurrentLocation != null) {
            mvMap.setVisibility(View.VISIBLE);

            int colorRGB = ImageUtils.getThemeColor(getActivity());
            float[] hslColor = new float[3];
            ColorUtils.colorToHSL(colorRGB, hslColor);

            final LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
            mGoogleMap.animateCamera(cameraUpdate);

            mGoogleMap.clear();
            mGoogleMap.addMarker(
                    new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(hslColor[0])));
            defineUserFriendlyAddress();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connection succeed");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ErrorCode = " + result.getErrorCode());
    }

    /*@Override
    public void onClick(View _view) {
        switch (_view.getId()) {
            case R.id.etLocation_FPC:
                locationTypeChooser = SingleChoiceDialog.newInstance(this, R.string.str_select_location_type, LocationType.toStringResArray());
                locationTypeChooser.show(getFragmentManager());
                break;
        }
    }*/

    /*@Override
    public void onItemPicked(int _dialogItem) {
        switch (LocationType.values()[_dialogItem]) {
            case AUTO:
//                hideKeyboard(tvSignalLevel);
//                checkLocationSettingsIfPermissionGranted();
                break;
            case MANUAL:
                etLocation.requestFocus();
                etLocation.setText(getString(R.string.str_empty));
                break;
        }
    }*/

    private void checkLocationSettingsIfPermissionGranted() {
        if (mLocationPermissionManager.isAllPermissionsChecked()) {
            checkLocationSettings();
        } else {
            mLocationPermissionManager.requestUncheckedPermissions(this, LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            getSpiceManager().getFromCache(Response.class, TAG, DurationInMillis.ALWAYS_EXPIRED, new PoorCoverageRequestListener());
            etLocation.clearFocus();
            initListeners();
            isLoaderAdded = true;
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mvMap.onSaveInstanceState(outState);
        mLocationPermissionManager.onSaveInstanceState(outState);
        removeListeners();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvSignalLevel.setText(getActivity().getResources().getStringArray(R.array.fragment_poor_coverage_signal_levels)[progress]);
        mLocationModel.setSignalLevel(progress + 1);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Unhandled callback
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Unhandled callback
    }

    @Override
    public void onLoadingCanceled() {
        if (getSpiceManager().isStarted() && mPoorCoverageRequest != null) {
            getSpiceManager().cancel(mPoorCoverageRequest);
        }
    }

    @Override
    public void onDestroyView() {
        mvMap.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mLocationPermissionManager = null;
        super.onDestroy();
    }

    private class PoorCoverageRequestListener implements RequestListener<Response> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            processError(spiceException);
            getSpiceManager().removeAllDataFromCache();
        }

        @Override
        public void onRequestSuccess(Response poorCoverageRequestModel) {
            boolean isDialog = loaderDialogDismiss();
            if (poorCoverageRequestModel != null) {
                switch (poorCoverageRequestModel.getStatus()) {
                    case 200:
                        if (isDialog) {
                            showMessage(R.string.str_success, R.string.str_data_has_been_sent);
                        } else {
                            loaderOverlaySuccess(getString(R.string.str_data_has_been_sent));
                        }
                        break;
                    case 400:
                        if (isDialog) {
                            showMessage(R.string.str_error, R.string.str_something_went_wrong);
                        } else {
                            loaderOverlayCancelled(getString(R.string.str_something_went_wrong));
                        }
                        break;
                    case 500:
                        if (isDialog) {
                            showMessage(R.string.str_error, R.string.str_request_failed);
                        } else {
                            loaderOverlayCancelled(getString(R.string.str_request_failed));
                        }
                        break;
                    case 0:
                        if (isDialog) {
                            showMessage(R.string.str_error, R.string.str_request_failed);
                        } else {
                            loaderOverlayFailed(getString(R.string.str_request_failed), true);
                        }
                        break;
                }
            } else {
                loaderOverlayFailed(getString(R.string.str_request_failed), true);
            }
            getSpiceManager().removeAllDataFromCache();
        }
    }

    private class GeoLocationRequestListener implements RequestListener<Address> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            processError(spiceException);
            sbProgressBar.setVisibility(View.INVISIBLE);
            mvMap.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), getString(R.string.error_no_network), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(Address address) {
            Set<String> addressData = new LinkedHashSet<>();
            addressData.add(address.getLocality());
            addressData.add(address.getThoroughfare());
            addressData.add(address.getSubThoroughfare());
            addressData.add(address.getAdminArea());
            addressData.add(address.getCountryName());
            addressData.remove(null);

            String userFriendlyAddress = StringUtils.join(addressData, ", ");
//            etLocation.setOnClickListener(PoorCoverageFragment.this);
            etLocation.setText(userFriendlyAddress);
            sbProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected String getServiceName() {
        return C.RATE_NAME_COVERAGE;//"complain Poor Coverage";
    }

    @Nullable
    @Override
    protected Service getServiceType() {
        return Service.POOR_COVERAGE;
    }

    @Override
    protected int getTitle() {
        return R.string.str_signal_coverage;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_poor_coverage;
    }
}
