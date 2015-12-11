package biz.enon.tra.uae.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import biz.enon.tra.uae.R;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

/**
 * Created by ak-buffalo on 13.08.15.
 */
public class AlertDialogFragment extends DialogFragment implements OnClickListener {

    private static final String KEY_IS_DATA_FROM_RESOURCE = "IS_DATA_FROM_RESOURCE";
    private static final String KEY_MESSAGE_ID = "MESSAGE_ID";
    private static final String KEY_TITLE = "TITLE";
    private static final String KEY_BODY = "BODY";

    private OnOkListener mSelectListener;

    private String mTitle, mBody;
    private int mMessageId;

    public static AlertDialogFragment newInstance(final Fragment targetFragment, final int _messageId,
                                                  @StringRes final int _titleRes, @StringRes final int _bodyRes) {
        final AlertDialogFragment pickerDialog = new AlertDialogFragment();
        pickerDialog.setTargetFragment(targetFragment, 0);
        final Bundle args = new Bundle();
        args.putBoolean(KEY_IS_DATA_FROM_RESOURCE, true);
        args.putInt(KEY_MESSAGE_ID, _messageId);
        args.putInt(KEY_TITLE, _titleRes);
        args.putInt(KEY_BODY, _bodyRes);
        pickerDialog.setArguments(args);
        return pickerDialog;
    }


    public static AlertDialogFragment newInstance(final Fragment targetFragment, final int _messageId,
                                                  final String _title, final String _body) {
        final AlertDialogFragment pickerDialog = new AlertDialogFragment();
        pickerDialog.setTargetFragment(targetFragment, 0);
        final Bundle args = new Bundle();
        args.putBoolean(KEY_IS_DATA_FROM_RESOURCE, false);
        args.putInt(KEY_MESSAGE_ID, _messageId);
        args.putString(KEY_TITLE, _title);
        args.putString(KEY_BODY, _body);
        pickerDialog.setArguments(args);
        return pickerDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof OnOkListener) {
            mSelectListener = (OnOkListener) targetFragment;
        }

        final Bundle args = getArguments();
        mMessageId = args.getInt(KEY_MESSAGE_ID);
        if (args.getBoolean(KEY_IS_DATA_FROM_RESOURCE)) {
            int titleRes = args.getInt(KEY_TITLE);
            mTitle = titleRes == 0 ? "" : getString(titleRes);
            int bodyRes = args.getInt(KEY_BODY);
            mBody = bodyRes == 0 ? "" : getString(bodyRes);
        } else {
            mTitle = args.getString(KEY_TITLE, "");
            mBody = args.getString(KEY_BODY, "");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertBuilder =
                new AlertDialog.Builder(getActivity(), THEME_HOLO_LIGHT)
                        .setTitle(mTitle)
                        .setMessage(mBody);

        if (mSelectListener != null) {
            alertBuilder.setPositiveButton(getString(R.string.str_ok), this);
        }
        if (mSelectListener instanceof OnOkCancelListener) {
            alertBuilder.setNegativeButton(getString(R.string.str_cancel), this);
        }

        return alertBuilder.create();
    }

    public final void show(FragmentManager manager) {
        super.show(manager, getClass().getSimpleName());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mSelectListener != null) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    mSelectListener.onOkPressed(mMessageId);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    ((OnOkCancelListener) mSelectListener).onCancelPressed(mMessageId);
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        mSelectListener = null;
        super.onDestroy();
    }

    public interface OnOkListener {
        void onOkPressed(final int _messageId);
    }

    public interface OnOkCancelListener extends OnOkListener {
        void onCancelPressed(final int _messageId);
    }
}
