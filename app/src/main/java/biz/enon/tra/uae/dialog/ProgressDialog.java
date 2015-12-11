package biz.enon.tra.uae.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.interfaces.Loader;

/**
 * Created by mobimaks on 14.08.2015.
 */
public final class ProgressDialog extends DialogFragment {

    static Loader.Cancelled dialogInterface;

    public static String TAG = ProgressDialog.class.getSimpleName();

    public static ProgressDialog newInstance(final String _title, Loader.Cancelled _onCancel) {
        ProgressDialog dialog = new ProgressDialog();
        Bundle bundle = new Bundle();
        bundle.putString(C.TITLE, _title);
        boolean isCancelable = _onCancel != null;
        bundle.putBoolean(C.IS_CANCELABLE, isCancelable);
        dialog.setArguments(bundle);
        dialogInterface = _onCancel;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        android.app.ProgressDialog dialog = new android.app.ProgressDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        dialog.setIndeterminate(true);
        dialog.setMessage(bundle.getString(C.TITLE));
        boolean isCancelable = bundle.getBoolean(C.IS_CANCELABLE);
        dialog.setCancelable(isCancelable);
        dialog.setCanceledOnTouchOutside(isCancelable);
        setCancelable(isCancelable);
        return dialog;
    }

    public void show(FragmentManager _manager) {
        show(_manager, TAG);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if(dialogInterface != null){
            dialogInterface.onLoadingCanceled();
        }
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {/*not implemented*/}
}
