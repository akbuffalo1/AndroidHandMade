package com.uae.tra_smart_services.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.customviews.HexagonView;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.global.Service;
import com.uae.tra_smart_services.rest.model.response.GetTransactionResponseModel;

/**
 * Created by and on 02.12.15.
 */

public class TransactionDetailFragment extends BaseFragment implements View.OnClickListener {
    private static final String MODEL = "model";
    private static final String ICON_COLOR = "ic_col";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";

    public static TransactionDetailFragment newInstance(int[] _icon_color, GetTransactionResponseModel _model) {
        Bundle args = new Bundle();
        args.putParcelable(MODEL, _model);
        args.putIntArray(ICON_COLOR, _icon_color);
        args.putString(TITLE, _model.title);
        args.putString(DESCRIPTION, _model.description);
        TransactionDetailFragment fragment = new TransactionDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private HexagonView hexagonView;
    private TextView tvTile;
    private TextView tvDescr;
    private TextView tvComplainInformation;
    private Button btnEdit;

    private HexagonHomeFragment.OnServiceSelectListener mOnServiceSelectListener;

    private GetTransactionResponseModel mModel;

    @Override
    protected void initViews() {
        super.initViews();
        hexagonView = findView(R.id.hvIcon_FTD);
        hexagonView.setBorderColor(getArguments().getIntArray(ICON_COLOR)[1]);
        hexagonView.setHexagonSrcDrawable(getArguments().getIntArray(ICON_COLOR)[0]);
        tvTile = findView(R.id.hvTitle_FTD);
        tvTile.setText(getArguments().getString(TITLE));
        tvDescr = findView(R.id.hvDescr_FTD);
        tvDescr.setText(getArguments().getString(DESCRIPTION));
        tvComplainInformation = findView(R.id.tvComplainInformation_FTD);
        tvComplainInformation.setText(
                getArguments().getString(DESCRIPTION) +" "+
                        getArguments().getString(DESCRIPTION) +" "+
                        getArguments().getString(DESCRIPTION) +" "+
                        getArguments().getString(DESCRIPTION) +" "+
                        getArguments().getString(DESCRIPTION) +" "+
                        getArguments().getString(DESCRIPTION) +" "+
                        getArguments().getString(DESCRIPTION) +" "+
                        getArguments().getString(DESCRIPTION) +" "+
                        getArguments().getString(DESCRIPTION) +" "+
                        getArguments().getString(DESCRIPTION));
        btnEdit = findView(R.id.btnEdit_FTD);
//        btnEdit.setVisibility(mModel.statusCode.equals(C.WAITING_FOR_DETAILS) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = getArguments().getParcelable(MODEL);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnEdit.setOnClickListener(this);
    }

    @Override
    public void onAttach(Activity _activity) {
        mOnServiceSelectListener = (HexagonHomeFragment.OnServiceSelectListener) _activity;
        super.onAttach(_activity);
    }

    @Override
    protected int getTitle() { return 0; }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_transactions_detail;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnEdit_FTD){
            mOnServiceSelectListener.onServiceSelect(Service.getServiceTypeByString(mModel.type), mModel);
        }
    }
}
