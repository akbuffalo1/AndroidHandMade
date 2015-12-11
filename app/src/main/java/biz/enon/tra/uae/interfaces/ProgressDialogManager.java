package biz.enon.tra.uae.interfaces;

/**
 * Created by Mikazme on 22/07/2015.
 */
public interface ProgressDialogManager {

    void showProgressDialog();

    void showProgressDialog(final String _text, boolean isCancelabel);

    void hideProgressDialog();
}
