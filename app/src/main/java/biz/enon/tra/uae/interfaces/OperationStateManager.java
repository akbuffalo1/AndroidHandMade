package biz.enon.tra.uae.interfaces;

/**
 * Created by mobimaks on 07.10.2015.
 */
public interface OperationStateManager {
    void showProgress();

    void showData();

    void showEmptyView();

    void endLoading();
}
