package biz.enon.tra.uae.entities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.net.HttpURLConnection;

import biz.enon.tra.uae.BuildConfig;
import biz.enon.tra.uae.R;
import biz.enon.tra.uae.TRAApplication;
import biz.enon.tra.uae.activity.HomeActivity;
import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.rest.model.response.ErrorResponseModel;
import biz.enon.tra.uae.util.PreferenceManager;
import retrofit.RetrofitError;

/**
 * Created by mobimaks on 07.10.2015.
 */
public final class NetworkErrorHandler {

    private NetworkErrorHandler() {
    }

    public static void processError(final BaseFragment _fragment, final SpiceException _exception) {
        if (_fragment.isAdded()) {
            final String errorMessage;
            Throwable cause = _exception.getCause();
            if (cause != null && cause instanceof RetrofitError) {
                errorMessage = processRetrofitError(_fragment.getActivity(), ((RetrofitError) cause));
                if (errorMessage != null)
                    _fragment.loaderOverlayFailed(errorMessage, true);
            } else if (_exception instanceof NoNetworkException) {
                errorMessage = _fragment.getString(R.string.error_no_network);
                _fragment.loaderOverlayFailed(errorMessage, false);
            } else {
                errorMessage = _exception.getMessage();
                _fragment.loaderOverlayCancelled(errorMessage);
            }
            _fragment.loaderDialogDismiss(errorMessage);
        }
    }

    @Nullable
    public static String processRetrofitError(final Activity _activity, final RetrofitError _error) {
        switch (_error.getKind()) {
            case NETWORK:
                return _activity.getString(R.string.error_server_connection);
            case HTTP:
                if (_error.getResponse().getStatus() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    return _activity.getString(R.string.error_server);
                } else if (_error.getResponse().getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    TRAApplication.setIsLoggedIn(false);
                    PreferenceManager.setLoggedIn(_activity, false);
                    final Intent intent = new Intent(_activity, HomeActivity.class);
                    _activity.startActivity(intent);
                    _activity.finish();
                    return null;
                }
                try {
                    ErrorResponseModel errorResponse = (ErrorResponseModel) _error.getBodyAs(ErrorResponseModel.class);
                    return errorResponse.error;
                } catch (RuntimeException _exc) {
                    _exc.printStackTrace();
                    return _activity.getString(R.string.error_server);
                }
            case CONVERSION:
                if (BuildConfig.DEBUG)
                    return _activity.getString(R.string.error_conversion_error);
            case UNEXPECTED:
            default:
                return _activity.getString(R.string.str_something_went_wrong);
        }
    }
}
