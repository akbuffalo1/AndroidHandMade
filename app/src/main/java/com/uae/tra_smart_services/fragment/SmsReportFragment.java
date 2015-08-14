package com.uae.tra_smart_services.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.entities.CustomFilterPool;
import com.uae.tra_smart_services.entities.Filter;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.rest.model.new_request.SmsSpamRequestModel;
import com.uae.tra_smart_services.rest.model.new_response.SmsSpamResponseModel;
import com.uae.tra_smart_services.rest.new_request.SmsSpamRequest;

/**
 * Created by ak-buffalo on 11.08.15.
 */
public class SmsReportFragment extends BaseFragment {
    public static SmsReportFragment newInstance() {
        return new SmsReportFragment();
    }

    @Override
    protected int getLayoutResource() { return R.layout.fragment_sms_report; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    private EditText etNumberOfSpammer;
    @Override
    protected void initViews() {
        super.initViews();
        etNumberOfSpammer = findView(R.id.etNumberOfSpammer_FSR);
    }

    private CustomFilterPool filters;
    @Override
    protected void initCustomEntities() {
        super.initCustomEntities();
        filters = new CustomFilterPool<String>(){
            {
                addFilter(new Filter<String>() {
                    @Override
                    public boolean check(String _data) {
                        // TODO Implement phone validation rule here, will return true by default
                        return true;
                    }
                });
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_send, menu);
    }

    @Override
    protected int getTitle() {
        return R.string.str_sms_report_number;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_send:
                collectAndSendToServer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private final void collectAndSendToServer(){
        if(filters.check(etNumberOfSpammer.getText().toString())){
            getSpiceManager().execute(
                    new SmsSpamRequest(
                            new SmsSpamRequestModel(
                                    etNumberOfSpammer.getText().toString(),
                                    getString(R.string.str_empty),
                                    getString(R.string.str_empty),
                                    getString(R.string.str_empty)
                            )
                    ),
                    new SmsSpamReportResponseListener()
            );
        } else {
            showMessage(R.string.str_error, R.string.str_invalid_number);
        }
    }

    private final class SmsSpamReportResponseListener implements RequestListener<SmsSpamResponseModel> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), spiceException.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestSuccess(SmsSpamResponseModel smsSpamReportResponse) {
            showMessage(R.string.str_success, R.string.str_report_has_been_sent);
        }
    }
}
