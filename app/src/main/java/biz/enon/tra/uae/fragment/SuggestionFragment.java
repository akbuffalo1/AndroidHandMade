package biz.enon.tra.uae.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.octo.android.robospice.persistence.DurationInMillis;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.TRAApplication;
import biz.enon.tra.uae.customviews.LoaderView;
import biz.enon.tra.uae.fragment.base.BaseServiceFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.interfaces.Loader;
import biz.enon.tra.uae.rest.model.request.ComplainTRAServiceModel;
import biz.enon.tra.uae.rest.model.response.TransactionModel;
import biz.enon.tra.uae.rest.robo_requests.BaseRequest;
import biz.enon.tra.uae.rest.robo_requests.ComplainSuggestionServiceRequest;
import biz.enon.tra.uae.rest.robo_requests.PutTransactionsRequest;

/**
 * Created by mobimaks on 14.08.2015.
 */
public class SuggestionFragment extends ComplainAboutTraFragment {

    private static final String KEY_SUGGESTION_REQUEST = "SUGGESTION_REQUEST";

    private BaseRequest mRequest;

    public static SuggestionFragment newInstance() {
        return new SuggestionFragment();
    }

    public static SuggestionFragment newInstance(Parcelable data) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BaseServiceFragment.KEY_TARNS_MODEL, data);
        SuggestionFragment fragment = new SuggestionFragment();
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
    protected void initViews() {
        super.initViews();
        ((EditText)findView(R.id.etComplainTitle_FCAT)).setHint(getString(R.string.str_suggestion_title));
    }

    @Override
    protected void prepareFieldsIfModelExist(TransactionModel _transactionModel){
        if (_transactionModel.hasAttachment) {
            onAttachmentGet(null);
        }
        etComplainTitle.setText(_transactionModel.title);
        etDescription.setText(_transactionModel.description);
    }

    @Override
    protected void sendComplain() {
        TransactionModel model = getTransactionModel();
        if(isIsInEditMode() && model != null) {
            model.title = etComplainTitle.getText().toString();
            model.description = etDescription.getText().toString();
            mRequest = new PutTransactionsRequest(model, getActivity(), getImageUri());
        } else {
            ComplainTRAServiceModel traServiceModel = new ComplainTRAServiceModel();
            traServiceModel.title = getTitleText();
            traServiceModel.description = getDescriptionText();
            mRequest = new ComplainSuggestionServiceRequest(traServiceModel, getActivity(), getImageUri());
        }
        loaderOverlayShow(getString(R.string.str_sending), this);
        loaderOverlayButtonBehavior(new Loader.BackButton() {
            @Override
            public void onBackButtonPressed(LoaderView.State _currentState) {
                getFragmentManager().popBackStack();
                if (_currentState == LoaderView.State.FAILURE || _currentState == LoaderView.State.SUCCESS) {
                    getFragmentManager().popBackStack();
                    if(isIsInEditMode()){
                        getFragmentManager().popBackStack();
                    }
                }
            }
        });
        getSpiceManager().execute(mRequest, getRequestKey(), DurationInMillis.ALWAYS_EXPIRED, getRequestListener());
    }

    @NonNull
    @Override
    protected Service getServiceType() {
        return Service.SUGGESTION;
    }

    @Override
    protected String getRequestKey() {
        return KEY_SUGGESTION_REQUEST;
    }

    @Override
    protected String getServiceName() {
        return C.RATE_NAME_SUGGESTION;
    }

    @Override
    public void onLoadingCanceled() {
        if(getSpiceManager().isStarted() && mRequest != null){
            getSpiceManager().cancel(mRequest);
        }
    }

    @Override
    protected int getTitle() {
        return R.string.service_suggestion;
    }
}
