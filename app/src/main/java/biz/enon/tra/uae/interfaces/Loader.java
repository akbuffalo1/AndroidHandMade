package biz.enon.tra.uae.interfaces;

import biz.enon.tra.uae.customviews.LoaderView;

/**
 * Created by ak-buffalo on 24.09.15.
 */

public interface Loader {

    void startLoading(String _msg);

    void continueLoading();

    void successLoading(String _msg);

    void cancelLoading(String _msg);

    void failedLoading(String _msg, boolean _hasToShowRating);

    void dissmissLoadingWithAction(Dismiss afterDissmiss);

    void setButtonPressedBehavior(BackButton backButtonPressed);

    boolean isInLoading();

    boolean isDone();

    interface Dismiss {
        void onLoadingDismissed();
    }

    interface Cancelled  extends LoaderMarker {
        void onLoadingCanceled();
    }

    interface BackButton{
        void onBackButtonPressed(LoaderView.State _currentState);
    }
}
