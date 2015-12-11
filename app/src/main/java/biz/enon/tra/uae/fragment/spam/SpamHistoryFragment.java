package biz.enon.tra.uae.fragment.spam;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.enon.tra.uae.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.fragment.spam.ReportSpamFragment.OnReportSpamServiceSelectListener;

/**
 * Created by mobimaks on 24.09.2015.
 */
public class SpamHistoryFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener {

    private static final String KEY_SELECTED_TAB_ID = "SELECTED_TAB_ID";

    //region Declare @SpamType
    @IntDef({SPAM_SMS, SPAM_WEB})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SpamType {
    }

    public static final int SPAM_SMS = 0;
    public static final int SPAM_WEB = 1;
    //endregion

    private RadioGroup rgSpamHistoryTabs;
    private LinearLayout llAddToSpam;

    private OnAddToSpamClickListener mAddToSpamClickListener;
    private OnReportSpamServiceSelectListener mSpamServiceSelectListener;

    @IdRes
    private int mSelectedTabId;

    public static SpamHistoryFragment newInstance() {
        return new SpamHistoryFragment();
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        if (_activity instanceof OnAddToSpamClickListener) {
            mAddToSpamClickListener = (OnAddToSpamClickListener) _activity;
        }
        if (_activity instanceof OnReportSpamServiceSelectListener) {
            mSpamServiceSelectListener = (OnReportSpamServiceSelectListener) _activity;
        }
    }

    @Override
    public void onCreate(final Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        if (_savedInstanceState != null) {
            mSelectedTabId = _savedInstanceState.getInt(KEY_SELECTED_TAB_ID);
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        rgSpamHistoryTabs = findView(R.id.rgSpamHistoryTabs_FSH);
        llAddToSpam = findView(R.id.llAddToSpam_FSH);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        llAddToSpam.setOnClickListener(this);
        rgSpamHistoryTabs.setOnCheckedChangeListener(this);
    }

    @Override
    public void onViewCreated(final View _view, final Bundle _savedInstanceState) {
        super.onViewCreated(_view, _savedInstanceState);
        if (getChildFragmentManager().findFragmentById(R.id.flSpamHistoryContainer_FSH) == null) {
            rgSpamHistoryTabs.check(R.id.rbSmsSpam_FSH);
        }
    }

    @Override
    public final void onClick(final View _view) {
        switch (_view.getId()) {
            case R.id.llAddToSpam_FSH:
                if (mAddToSpamClickListener != null) {
                    handleAddToSpamClick();
                }
                break;
        }
    }

    private void handleAddToSpamClick() {
        switch (rgSpamHistoryTabs.getCheckedRadioButtonId()) {
            case R.id.rbSmsSpam_FSH:
                mAddToSpamClickListener.onAddToSpamClick(SPAM_SMS);
                break;
            case R.id.rbWebSpam_FSH:
                mAddToSpamClickListener.onAddToSpamClick(SPAM_WEB);
                break;
        }
    }

    @Override
    public final void onCheckedChanged(final RadioGroup _group, final int _checkedId) {
        if (mSelectedTabId == _checkedId) {
            return;
        }
        mSelectedTabId = _checkedId;
        switch (_checkedId) {
            case R.id.rbSmsSpam_FSH:
                setFragment(SmsSpamHistoryFragment.newInstance());
                break;
            case R.id.rbWebSpam_FSH:
                if (mSpamServiceSelectListener != null) {
                    mSpamServiceSelectListener.onReportSpamServiceSelect(ReportSpamFragment.SPAM_OPTION_REPORT_WEB);
                }
                mSelectedTabId = R.id.rbSmsSpam_FSH;
                rgSpamHistoryTabs.check(mSelectedTabId);
                break;
        }
    }

    private void setFragment(@NonNull final BaseFragment _fragment){
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.flSpamHistoryContainer_FSH, _fragment)
                .commit();
    }

    @Override
    public void onSaveInstanceState(final Bundle _outState) {
        _outState.putInt(KEY_SELECTED_TAB_ID, mSelectedTabId);
        super.onSaveInstanceState(_outState);
    }

    @Override
    public void onDetach() {
        mSpamServiceSelectListener = null;
        mAddToSpamClickListener = null;
        super.onDetach();
    }

    @Override
    protected int getTitle() {
        return R.string.fragment_report_history_title;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_spam_history;
    }

    public interface OnAddToSpamClickListener {
        void onAddToSpamClick(@SpamType int _spamType);
    }

}
