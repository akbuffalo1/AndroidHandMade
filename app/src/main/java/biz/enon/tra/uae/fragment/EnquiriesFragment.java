package biz.enon.tra.uae.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.octo.android.robospice.persistence.DurationInMillis;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.TRAApplication;
import biz.enon.tra.uae.customviews.LoaderView;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.interfaces.Loader;
import biz.enon.tra.uae.rest.model.request.ComplainTRAServiceModel;
import biz.enon.tra.uae.rest.robo_requests.BaseRequest;
import biz.enon.tra.uae.rest.robo_requests.ComplainEnquiriesServiceRequest;
import biz.enon.tra.uae.rest.robo_requests.PutTransactionsRequest;

/**
 * Created by mobimaks on 14.08.2015.
 */
public class EnquiriesFragment extends ComplainAboutTraFragment {

    private static final String KEY_ENQUIRIES_REQUEST = "ENQUIRIES_REQUEST";

    private BaseRequest mRequest;
    private EditText etTitle;

    public static EnquiriesFragment newInstance() { return new EnquiriesFragment(); }

    public static EnquiriesFragment newInstance(Parcelable data) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DATA, data);
        EnquiriesFragment fragment = new EnquiriesFragment();
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
    protected void sendComplain() {
        if(mIsInEditMode && mTransactionModel != null){
            mTransactionModel.title = getTitleText();
            mTransactionModel.description = getDescriptionText();
            mRequest = new PutTransactionsRequest(mTransactionModel, getActivity(), getImageUri());
        } else {
            ComplainTRAServiceModel traServiceModel = new ComplainTRAServiceModel();
            traServiceModel.title = getTitleText();
            traServiceModel.description = getDescriptionText();
            mRequest = new ComplainEnquiriesServiceRequest(traServiceModel, getActivity(), getImageUri());
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
        getSpiceManager().execute(mRequest, getRequestKey(), DurationInMillis.ALWAYS_EXPIRED, getRequestListener());
    }

    @Override
    protected void initViews() {
        super.initViews();
        etTitle = findView(R.id.etComplainTitle_FCAT);
        etTitle.setHint(getString(R.string.str_enquires_title));
        setCapitalizeTextWatcher(etTitle);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments() != null && (mTransactionModel = getArguments().getParcelable(KEY_DATA)) != null){
            mIsInEditMode = true;
            if(mTransactionModel.hasAttachment){
                onAttachmentGet(null);
            }
            etTitle.setText(mTransactionModel.title);
            etDescription.setText(mTransactionModel.description);
        }
    }

    @Override
    public void onLoadingCanceled() {
        if (getSpiceManager().isStarted() && mRequest != null) {
            getSpiceManager().cancel(mRequest);
        }
    }

    @Override
    protected String getServiceName() {
        return C.RATE_NAME_ENQUIRIES;
    }

    @NonNull
    @Override
    protected Service getServiceType() {
        return Service.ENQUIRIES;
    }

    @Override
    protected String getRequestKey() {
        return KEY_ENQUIRIES_REQUEST;
    }

    @Override
    protected int getTitle() {
        return R.string.service_enquiries;
    }
}
