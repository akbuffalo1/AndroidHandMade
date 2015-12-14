package biz.enon.tra.uae.rest.request_listeners;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

import biz.enon.tra.uae.adapter.AnnouncementsAdapter;
import biz.enon.tra.uae.fragment.InfoHubAnnouncementsFragment;
import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.interfaces.OperationStateManager;
import biz.enon.tra.uae.rest.model.response.GetAnnouncementsResponseModel;

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
                    mAnnouncementsListAdapter.addAll(result.announcements);
                    mOperationStateManager.showData();
                }
            } else {
                mAnnouncementsPageNum--;
            }
            mOperationStateManager.endLoading();
        }
    }

    @Override
    public final void onRequestFailure(SpiceException spiceException) {
        mOperationStateManager.endLoading();
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
    }
}