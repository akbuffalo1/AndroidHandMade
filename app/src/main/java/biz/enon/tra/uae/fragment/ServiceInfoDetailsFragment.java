package biz.enon.tra.uae.fragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.customviews.HexagonView;
import biz.enon.tra.uae.fragment.base.BaseFragment;

/**
 * Created by Mikazme on 21/08/2015.
 */
public class ServiceInfoDetailsFragment extends BaseFragment implements OnTouchListener, OnClickListener {

    private static final String HEX_ICON_RES = "hexagon_icon_res";
    private static final String DESCRIPTION = "description";

    private LinearLayout llFragmentContainer;
    private ImageView ivCloseInfo;
    private TextView tvServiceInfoContent;
    private HexagonView hvServiceInfoIcon;

    private int mServiceInfoIconDrawableRes;
    private String mServiceInfoContent;

    public static ServiceInfoDetailsFragment newInstance(@DrawableRes int _hexagonSrc, String _text) {
        Bundle args = new Bundle();
        args.putInt(HEX_ICON_RES, _hexagonSrc);
        args.putString(DESCRIPTION, _text);
        ServiceInfoDetailsFragment fragment = new ServiceInfoDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {
        super.initData();
        mServiceInfoIconDrawableRes = getArguments().getInt(HEX_ICON_RES);
        mServiceInfoContent = getArguments().getString(DESCRIPTION);
    }

    @Override
    protected final void initViews() {
        llFragmentContainer = findView(R.id.llFragmentContainer_FSID);
        tvServiceInfoContent = findView(R.id.tvServiceInfoContent_FSID);
        tvServiceInfoContent.setText(mServiceInfoContent);
        hvServiceInfoIcon = findView(R.id.hvServiceInfoIcon_FSID);
        hvServiceInfoIcon.setHexagonSrcDrawable(mServiceInfoIconDrawableRes);
        ivCloseInfo = findView(R.id.ivCloseInfo_FSID);
    }

    @Override
    protected final void initListeners() {
        llFragmentContainer.setOnTouchListener(this);
        ivCloseInfo.setOnClickListener(this);
    }

    @Override
    public final boolean onTouch(final View _view, final MotionEvent _event) {
        return true;
    }

    @Override
    public final void onClick(final View _view) {
        switch (_view.getId()) {
            case R.id.ivCloseInfo_FSID:
                getFragmentManager().popBackStack();
                break;
        }
    }

    @Override
    protected final int getTitle() {
        return 0;
    }

    @Override
    protected final int getLayoutResource() {
        return R.layout.fragment_service_info_details;
    }
}
