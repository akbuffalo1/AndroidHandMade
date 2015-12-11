package biz.enon.tra.uae.fragment.hexagon_fragment;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.fragment.base.BaseFragment;

/**
 * Created by ak-buffalo on 9/9/2015.
 */
public class NotificationsFragment extends BaseFragment implements View.OnClickListener, View.OnTouchListener {

    private ImageView ivCloseNotificationList;
    private TextView tvNotificationListStatus;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    protected void initViews() {
        super.initViews();
        ivCloseNotificationList = findView(R.id.ivNotificationListClose_FN);
        tvNotificationListStatus = findView(R.id.tvNotificationListStatus_FN);
        tvNotificationListStatus.setText(
                getResources().getString(
                        R.string.str_you_have_notification,
                        getResources().getString(R.string.str_no)
                )
        );
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        ivCloseNotificationList.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivNotificationListClose_FN:
                getFragmentManager().popBackStackImmediate();
                break;
        }
    }

    @Override
    public final boolean onTouch(final View _view, final MotionEvent _event) {
        return true;
    }

    @Override
     protected void setToolbarVisibility() {
        toolbarTitleManager.setToolbarVisibility(false);
    }

    @Override
    protected int getTitle() {
        return 0;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_notification;
    }
}
