package biz.enon.tra.uae.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.enon.tra.uae.R;

import biz.enon.tra.uae.customviews.HexagonView;
import biz.enon.tra.uae.fragment.HexagonHomeFragment.OnServiceSelectListener;
import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.rest.model.response.GetTransactionResponseModel;
import biz.enon.tra.uae.util.ImageUtils;

import static biz.enon.tra.uae.global.C.TRANSACTION_STATUS_COLOR_INDEX;
import static biz.enon.tra.uae.global.C.TRANSACTION_STATUS_ICON_INDEX;

/**
 * Created by and on 02.12.15.
 */

public class TransactionDetailsFragment extends BaseFragment implements OnClickListener {

    private static final String KEY_TRANSACTION = "TRANSACTION";
    private static final String KEY_ICON_COLOR = "ICON_COLOR";

    public static TransactionDetailsFragment newInstance(int[] _icon_color, GetTransactionResponseModel _model) {
        final TransactionDetailsFragment fragment = new TransactionDetailsFragment();
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

        final boolean isBlackAndWhiteMode = ImageUtils.isBlackAndWhiteMode(getActivity());

        if (isBlackAndWhiteMode) {
            hexagonView.setBorderColor(R.color.hex_color_dark_gray);
        } else {
            hexagonView.setBorderColor(mIconColor[TRANSACTION_STATUS_COLOR_INDEX]);
        }

        if (isBlackAndWhiteMode) {
            hexagonView.setSrcTintColorRes(R.color.hex_color_dark_gray);
        } else if (C.WEB_REPORT.equals(mTransaction.type)) {
            hexagonView.setSrcTintColorRes(mIconColor[TRANSACTION_STATUS_COLOR_INDEX]);
        } else {
            hexagonView.setSrcTintColor(Color.TRANSPARENT);
        }

        if (C.WEB_REPORT.equals(mTransaction.type)) {
            hexagonView.setHexagonSrcDrawable(Service.BLOCK_WEBSITE.getDrawableRes());
        } else {
            hexagonView.setHexagonSrcDrawable(mIconColor[TRANSACTION_STATUS_ICON_INDEX]);
        }

        tvTile = findView(R.id.hvTitle_FTD);
        tvTile.setText(mTransaction.title);

        tvDescription = findView(R.id.hvDescr_FTD);
        tvDescription.setText(mTransaction.description);

        tvComplainInformation = findView(R.id.tvComplainInformation_FTD);
        tvComplainInformation.setText(mTransaction.description);

        btnEdit = findView(R.id.btnEdit_FTD);
        btnEdit.setVisibility(/*WAITING_FOR_DETAILS.equals(mTransaction.statusCode) ? View.VISIBLE :*/ View.GONE);
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
