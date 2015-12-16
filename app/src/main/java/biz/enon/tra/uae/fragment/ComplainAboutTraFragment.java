package biz.enon.tra.uae.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.octo.android.robospice.request.listener.RequestListener;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.TRAApplication;
import biz.enon.tra.uae.customviews.LoaderView;
import biz.enon.tra.uae.customviews.ThemedImageView;
import biz.enon.tra.uae.dialog.AlertDialogFragment;
import biz.enon.tra.uae.fragment.base.BaseComplainFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.interfaces.Loader;
import biz.enon.tra.uae.rest.model.request.ComplainTRAServiceModel;
import biz.enon.tra.uae.rest.robo_requests.BaseRequest;
import biz.enon.tra.uae.rest.robo_requests.ComplainAboutTRAServiceRequest;
import biz.enon.tra.uae.rest.robo_requests.PutTransactionsRequest;
import retrofit.client.Response;

/**
 * Created by mobimaks on 11.08.2015.
 */
public class ComplainAboutTraFragment extends BaseComplainFragment
        implements OnClickListener, AlertDialogFragment.OnOkListener {

    protected static final String KEY_COMPLAIN_REQUEST = "COMPLAIN_ABOUT_TRA_REQUEST";

    private ThemedImageView tivAddAttachment;
    protected EditText etComplainTitle, etDescription;

    private BaseRequest mRequest;
    private RequestResponseListener mRequestListener;

    public static ComplainAboutTraFragment newInstance() {
        return new ComplainAboutTraFragment();
    }

    public static ComplainAboutTraFragment newInstance(Parcelable data) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DATA, data);
        ComplainAboutTraFragment fragment = new ComplainAboutTraFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!TRAApplication.isLoggedIn()) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareFieldsIfNeed();
    }

    private void prepareFieldsIfNeed(){
        if(getArguments() != null && (mTransactionModel = getArguments().getParcelable(KEY_DATA)) != null) {
            mIsInEditMode = true;
            if (mTransactionModel.hasAttachment) {
                onAttachmentGet(null);
            }
            etComplainTitle.setText(mTransactionModel.title);
            etDescription.setText(mTransactionModel.description);
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        tivAddAttachment = findView(R.id.tivAddAttachment_FCAT);
        etComplainTitle = findView(R.id.etComplainTitle_FCAT);
        setCapitalizeTextWatcher(etComplainTitle);
        etDescription = findView(R.id.etDescription_FCAT);
        setCapitalizeTextWatcher(etDescription);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        mRequestListener = new RequestResponseListener();
        tivAddAttachment.setOnClickListener(this);
        etComplainTitle.setOnFocusChangeListener(this);
        etDescription.setOnFocusChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getSpiceManager().getFromCache(Response.class, getRequestKey(), DurationInMillis.ALWAYS_RETURNED, mRequestListener);
//        getSpiceManager().addListenerIfPending(Response.class, getRequestKey(), mRequestListener);
    }

    protected String getRequestKey() {
        return KEY_COMPLAIN_REQUEST;
    }

    @Override
    public void onClick(View v) {
        hideKeyboard(v);
        switch (v.getId()) {
            case R.id.tivAddAttachment_FCAT:
                openImagePicker();
                break;
        }
    }

    @Override
    public void onAttachmentGet(@NonNull Uri _uri) {
        tivAddAttachment.setImageResource(R.drawable.ic_check);
    }

    @Override
    protected void onAttachmentDeleted() {
        mTransactionModel.hasAttachment = false;
        tivAddAttachment.setImageResource(R.drawable.ic_action_attachment);
    }

    @Override
    protected boolean validateData() {
        final String title = etComplainTitle.getText().toString().trim();
        final String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_fill_all_fields, C.TOAST_LENGTH).show();
            return false;
        }
        return true;
    }

    @Override
    protected void sendComplain() {
        if(mIsInEditMode && mTransactionModel != null) {
            mTransactionModel.title = etComplainTitle.getText().toString();
            mTransactionModel.description = etDescription.getText().toString();
            mRequest = new PutTransactionsRequest(mTransactionModel, getActivity(), getImageUri());
        } else {
            ComplainTRAServiceModel traServiceModel = new ComplainTRAServiceModel();
            traServiceModel.title = getTitleText();
            traServiceModel.description = getDescriptionText();
            mRequest = new ComplainAboutTRAServiceRequest(traServiceModel, getActivity(), getImageUri());
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
        getSpiceManager().execute(mRequest, getRequestKey(), DurationInMillis.ALWAYS_EXPIRED, mRequestListener);
    }

    @Override
    public void onLoadingCanceled() {
        if (getSpiceManager().isStarted()) {
            getSpiceManager().removeDataFromCache(Response.class, getRequestKey());
            getSpiceManager().cancel(mRequest);
        }
    }

    @Override
    protected String getServiceName() {
        return C.RATE_NAME_COMPLAIN_ABOUT_TRA;//"complain about TRA Service";
    }

    private class RequestResponseListener implements PendingRequestListener<Response> {

        @Override
        public void onRequestNotFound() {
            Log.d(getClass().getSimpleName(), "Request Not Found. isAdded: " + isAdded());
        }

        @Override
        public void onRequestSuccess(Response result) {
            Log.d(getClass().getSimpleName(), "Success. isAdded: " + isAdded());
            if (isAdded()) {
                loaderOverlaySuccess(getString(R.string.str_complain_has_been_sent));
                getSpiceManager().removeDataFromCache(Response.class, getRequestKey());
            }
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            getSpiceManager().removeDataFromCache(Response.class, getRequestKey());
            processError(spiceException);
        }
    }

    @Override
    protected Service getServiceType() {
        return Service.COMPLAINT_ABOUT_TRA;
    }

    protected RequestListener<Response> getRequestListener() {
        return mRequestListener;
    }

    @NonNull
    protected final String getDescriptionText() {
        return etDescription.getText().toString();
    }

    @NonNull
    protected final String getTitleText() {
        return etComplainTitle.getText().toString();
    }

    @Override
    protected int getTitle() {
        return R.string.service_complain_about_tra;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_complain_about_tra;
    }
}
