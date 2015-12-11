package biz.enon.tra.uae.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.enon.tra.uae.R;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.math.BigDecimal;

import biz.enon.tra.uae.fragment.base.BaseServiceFragment;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.interfaces.Loader;
import biz.enon.tra.uae.rest.robo_requests.SpeedTestRequest;

/**
 * Created by mobimaks on 07.09.2015.
 */
@Deprecated
public final class SpeedTestFragment extends BaseServiceFragment implements OnClickListener, Loader.Cancelled, RequestListener<Long> {

    private static final String KEY_SPEED_TEST_REQUEST = "SPEED_TEST_REQUEST";

    private TextView tvResult;
    private Button btnRunSpeedtest;

    private SpeedTestRequest mSpeedTestRequest;

    public static SpeedTestFragment newInstance() {
        return new SpeedTestFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected final void initViews() {
        super.initViews();
        tvResult = findView(R.id.tvResult_FS);
        btnRunSpeedtest = findView(R.id.btnRunSpeedtest_FS);
    }

    @Override
    protected final void initListeners() {
        super.initListeners();
        btnRunSpeedtest.setOnClickListener(this);
    }

    @Override
    public final void onClick(final View _view) {
        loaderOverlayShow(getString(R.string.fragment_speed_test_progress), this);
        mSpeedTestRequest = new SpeedTestRequest();
        getSpiceManager().execute(mSpeedTestRequest, KEY_SPEED_TEST_REQUEST, DurationInMillis.ALWAYS_EXPIRED, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getSpiceManager().getFromCache(Long.class, KEY_SPEED_TEST_REQUEST, DurationInMillis.ALWAYS_RETURNED, this);
    }

    @Override
    public final void onRequestSuccess(final Long _kbPerSecond) {
        if (isAdded()) {
            loaderOverlayDismissWithAction(SpeedTestFragment.this);
            if (_kbPerSecond != null) {
                double mBitPerSecond = _kbPerSecond / 1024f;
                BigDecimal bd = new BigDecimal(mBitPerSecond);
                bd = bd.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                tvResult.setText(getString(R.string.fragment_speed_test_result, bd.toString()));
                getSpiceManager().removeDataFromCache(Long.class, KEY_SPEED_TEST_REQUEST);
            }
        }
    }

    @Override
    public final void onLoadingCanceled() {
        if (getSpiceManager().isStarted() && mSpeedTestRequest != null) {
            getSpiceManager().cancel(mSpeedTestRequest);
        }
    }

    @Override
    public final void onRequestFailure(final SpiceException _spiceException) {
        getSpiceManager().removeDataFromCache(Long.class, KEY_SPEED_TEST_REQUEST);
        processError(_spiceException);
    }

    @Override
    protected String getServiceName() {
        return "Internet Spees Service";
    }

    @Nullable
    @Override
    protected Service getServiceType() {
        return null;
    }

    @Override
    protected final int getTitle() {
        return R.string.fragment_speed_test_title;
    }

    @Override
    protected final int getLayoutResource() {
        return R.layout.fragment_speedtest;
    }
}
