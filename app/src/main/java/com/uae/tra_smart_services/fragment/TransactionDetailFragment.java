package com.uae.tra_smart_services.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.customviews.HexagonView;
import com.uae.tra_smart_services.fragment.HexagonHomeFragment.OnServiceSelectListener;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.global.Service;
import com.uae.tra_smart_services.rest.model.response.GetTransactionResponseModel;

import static com.uae.tra_smart_services.global.C.TRANSACTION_STATUS_COLOR_INDEX;
import static com.uae.tra_smart_services.global.C.TRANSACTION_STATUS_ICON_INDEX;
import static com.uae.tra_smart_services.global.C.WAITING_FOR_DETAILS;

/**
 * Created by and on 02.12.15.
 */

public class TransactionDetailFragment extends BaseFragment implements OnClickListener {

    private static final String KEY_TRANSACTION = "TRANSACTION";
    private static final String KEY_ICON_COLOR = "ICON_COLOR";

    public static TransactionDetailFragment newInstance(int[] _icon_color, GetTransactionResponseModel _model) {
        final TransactionDetailFragment fragment = new TransactionDetailFragment();
        final Bundle args = new Bundle();
        args.putParcelable(KEY_TRANSACTION, _model);
        args.putIntArray(KEY_ICON_COLOR, _icon_color);
        fragment.setArguments(args);
        return fragment;
    }

    private HexagonView hexagonView;
    private TextView tvTile;
    private TextView tvDescription;
    private TextView tvComplainInformation;
    private Button btnEdit;

    private OnServiceSelectListener mOnServiceSelectListener;

    private GetTransactionResponseModel mTransaction;
    private int[] mIconColor;

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        if (_activity instanceof OnServiceSelectListener) {
            mOnServiceSelectListener = (OnServiceSelectListener) _activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        mTransaction = args.getParcelable(KEY_TRANSACTION);
        mIconColor = args.getIntArray(KEY_ICON_COLOR);
    }

    @Override
    protected void initViews() {
        super.initViews();
        hexagonView = findView(R.id.hvIcon_FTD);

        hexagonView.setBorderColor(mIconColor[TRANSACTION_STATUS_COLOR_INDEX]);
        if (C.WEB_REPORT.equals(mTransaction.type)) {
            hexagonView.setSrcTintColorRes(mIconColor[TRANSACTION_STATUS_COLOR_INDEX]);
            hexagonView.setHexagonSrcDrawable(Service.BLOCK_WEBSITE.getDrawableRes());
        } else {
            hexagonView.setSrcTintColor(Color.TRANSPARENT);
            hexagonView.setHexagonSrcDrawable(mIconColor[TRANSACTION_STATUS_ICON_INDEX]);
        }

        tvTile = findView(R.id.hvTitle_FTD);
        tvTile.setText(mTransaction.title);

        tvDescription = findView(R.id.hvDescr_FTD);
        tvDescription.setText(mTransaction.description);

        tvComplainInformation = findView(R.id.tvComplainInformation_FTD);
        tvComplainInformation.setText(mTransaction.description);

        btnEdit = findView(R.id.btnEdit_FTD);
        btnEdit.setVisibility(WAITING_FOR_DETAILS.equals(mTransaction.statusCode) ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnEdit.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbarTitleManager().setTitle(mTransaction.title);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnEdit_FTD && mOnServiceSelectListener != null) {
            mOnServiceSelectListener.onServiceSelect(Service.getServiceTypeByString(mTransaction.type), mTransaction);
        }
    }

    @Override
    public void onDetach() {
        mOnServiceSelectListener = null;
        super.onDetach();
    }

    @Override
    protected int getTitle() {
        return 0;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_transactions_detail;
    }
}
