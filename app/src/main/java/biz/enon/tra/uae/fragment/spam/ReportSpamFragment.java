package biz.enon.tra.uae.fragment.spam;

import android.app.Activity;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.enon.tra.uae.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import biz.enon.tra.uae.customviews.HexagonView;
import biz.enon.tra.uae.fragment.base.BaseServiceFragment;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.util.ImageUtils;

/**
 * Created by mobimaks on 23.09.2015.
 */
public final class ReportSpamFragment extends BaseServiceFragment implements OnClickListener {

    //region @SpamOption declaration
    @IntDef({SPAM_OPTION_REPORT_SMS, SPAM_OPTION_REPORT_WEB, SPAM_OPTION_REPORT_HISTORY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SpamOption {
    }

    public static final int SPAM_OPTION_REPORT_SMS = 0;
    public static final int SPAM_OPTION_REPORT_WEB = 1;
    public static final int SPAM_OPTION_REPORT_HISTORY = 2;
    //endregion

    private HexagonView hvSpamIcon;
    private HexagonView hvReportSms, hvReportWeb, hvReportList;
    private TextView tvReportSms, tvReportWeb, tvReportList;

    private OnReportSpamServiceSelectListener mSpamServiceSelectListener;

    public static ReportSpamFragment newInstance() {
        return new ReportSpamFragment();
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        if (_activity instanceof OnReportSpamServiceSelectListener) {
            mSpamServiceSelectListener = (OnReportSpamServiceSelectListener) _activity;
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        hvSpamIcon = findView(R.id.hvSpamIcon_FRS);
        hvSpamIcon.setHexagonSrcDrawable(
                ImageUtils.getFilteredDrawableByTheme(hvSpamIcon.getContext(), R.drawable.ic_spam, R.attr.themeMainColor));

        hvReportSms = findView(R.id.hvReportSms_FRS);
        hvReportWeb = findView(R.id.hvReportWeb_FRS);
        hvReportList = findView(R.id.hvReportList_FRS);

        tvReportSms = findView(R.id.tvReportSms_FRS);
        tvReportWeb = findView(R.id.tvReportWeb_FRS);
        tvReportList = findView(R.id.tvReportList_FRS);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        hvReportSms.setOnClickListener(this);
        tvReportSms.setOnClickListener(this);

        hvReportWeb.setOnClickListener(this);
        tvReportWeb.setOnClickListener(this);

        hvReportList.setOnClickListener(this);
        tvReportList.setOnClickListener(this);
    }

    @Override
    public final void onClick(final View _view) {
        if (mSpamServiceSelectListener != null) {
            switch (_view.getId()) {
                case R.id.hvReportSms_FRS:
                case R.id.tvReportSms_FRS:
                    mSpamServiceSelectListener.onReportSpamServiceSelect(SPAM_OPTION_REPORT_SMS);
                    break;
                case R.id.hvReportWeb_FRS:
                case R.id.tvReportWeb_FRS:
                    mSpamServiceSelectListener.onReportSpamServiceSelect(SPAM_OPTION_REPORT_WEB);
                    break;
                case R.id.hvReportList_FRS:
                case R.id.tvReportList_FRS:
                    mSpamServiceSelectListener.onReportSpamServiceSelect(SPAM_OPTION_REPORT_HISTORY);
                    break;
            }
        }
    }

    @Override
    public void onDetach() {
        mSpamServiceSelectListener = null;
        super.onDetach();
    }

    @Nullable
    @Override
    protected Service getServiceType() {
        return Service.REPORT_SPAM;
    }

    @Override
    protected String getServiceName() {
        return "Spam Report";
    }

    @Override
    public void onLoadingCanceled() {

    }

    @Override
    protected int getTitle() {
        return R.string.fragment_report_spam_title;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_report_spam;
    }

    public interface OnReportSpamServiceSelectListener {
        void onReportSpamServiceSelect(final @SpamOption int _service);
    }

}
