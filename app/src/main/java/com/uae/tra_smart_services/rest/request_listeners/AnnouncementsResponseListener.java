package com.uae.tra_smart_services.rest.request_listeners;

import android.os.Parcelable;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.uae.tra_smart_services.adapter.AnnouncementsAdapter;
import com.uae.tra_smart_services.fragment.InfoHubAnnouncementsFragment;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.interfaces.OperationStateManager;
import com.uae.tra_smart_services.rest.model.response.GetAnnouncementsResponseModel;

import java.util.ArrayList;

/**
 * Created by and on 23.10.15.
 */
public final class AnnouncementsResponseListener implements RequestListener<GetAnnouncementsResponseModel> {

    private BaseFragment mFragment;
    private OperationStateManager mOperationStateManager;
    private AnnouncementsAdapter mAnnouncementsListAdapter;
    private InfoHubAnnouncementsFragment.BooleanHolder mIsAnnouncementsInLoading;
    ArrayList<GetAnnouncementsResponseModel.Announcement> mModel;
    private boolean mIsAllAnnouncementsDownloaded;
    private int mAnnouncementsPageNum;

    public AnnouncementsResponseListener(BaseFragment _fragment, OperationStateManager _manager,AnnouncementsAdapter _announcementsListAdapter,
                            InfoHubAnnouncementsFragment.BooleanHolder _isAnnouncementsInLoading, boolean _isAllAnnouncementsDownloaded, int _announcementsPageNum, ArrayList<GetAnnouncementsResponseModel.Announcement> _model) {
        mFragment = _fragment;
        mOperationStateManager = _manager;
        mAnnouncementsListAdapter = _announcementsListAdapter;
        mIsAnnouncementsInLoading = _isAnnouncementsInLoading;
        mIsAllAnnouncementsDownloaded = _isAllAnnouncementsDownloaded;
        mAnnouncementsPageNum = _announcementsPageNum;
        mModel = _model;
    }

    @Override
    public final void onRequestSuccess(GetAnnouncementsResponseModel result) {
        mIsAnnouncementsInLoading.falseV();
        if(mFragment.isAdded()){
            if (result != null) {
                if (mIsAllAnnouncementsDownloaded = result.announcements.isEmpty()) {
                    handleNoResult();
                } else {
                    if(mModel != null) mModel.addAll(result.announcements);
                    mOperationStateManager.showData();
                    mAnnouncementsListAdapter.addAll(result.announcements);
                }
            } else {
                mAnnouncementsPageNum--;
            }
            mOperationStateManager.endLoading();
        }
    }

    @Override
    public final void onRequestFailure(SpiceException spiceException) {
        mIsAnnouncementsInLoading.falseV();
        mAnnouncementsPageNum--;
        handleNoResult();
        mFragment.processError(spiceException);
    }

    private void handleNoResult() {
        if (mAnnouncementsListAdapter.isEmpty()) {
            mOperationStateManager.showEmptyView();
        } else {
            mAnnouncementsListAdapter.stopLoading();
        }
        mOperationStateManager.endLoading();
    }
}