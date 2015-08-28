package com.uae.tra_smart_services.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.global.SmsService;

/**
 * Created by ak-buffalo on 11.08.15.
 */
public class SmsServiceListFragment extends BaseFragment implements TextView.OnClickListener {

    private TextView tvReportNum, tvBlockNum;

    private OnSmsServiceSelectListener mServiceSelectListener;

    public static SmsServiceListFragment newInstance() {
        return new SmsServiceListFragment();
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        mServiceSelectListener = (OnSmsServiceSelectListener) _activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        super.initViews();
        tvReportNum = findView(R.id.itmReportNum_FSS);
        tvReportNum.setText(SmsService.REPORT.toString());

        tvBlockNum = findView(R.id.itmBlockNum_FSS);
        tvBlockNum.setText(SmsService.BLOCK.toString());
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        tvReportNum.setOnClickListener(this);
        tvBlockNum.setOnClickListener(this);
    }

    @Override
    public final void onClick(View _view) {
        switch (_view.getId()) {
            case R.id.itmReportNum_FSS:
                mServiceSelectListener.onSmsServiceChildSelect(SmsService.REPORT);
                break;
            case R.id.itmBlockNum_FSS:
                mServiceSelectListener.onSmsServiceChildSelect(SmsService.BLOCK);
                break;
        }
    }

    @Override
    protected int getTitle() {
        return R.string.str_sms_service_list;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_sms_spam;
    }

    public interface OnSmsServiceSelectListener {
        void onSmsServiceChildSelect(SmsService _service);
    }
}
