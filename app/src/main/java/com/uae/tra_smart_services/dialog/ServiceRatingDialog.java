package com.uae.tra_smart_services.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.TRAApplication;
import com.uae.tra_smart_services.customviews.ServiceRatingView;
import com.uae.tra_smart_services.global.H;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

/**
 * Created by ak-buffalo on 02.10.15.
 */
public class ServiceRatingDialog extends DialogFragment implements View.OnClickListener, DialogInterface.OnShowListener {
    private static final String LOG_TAG = ServiceRatingDialog.class.getSimpleName();

    private CallBacks mCallBacks;
    private ServiceRatingView ratingView;
    private AlertDialog mDialog;

    public static ServiceRatingDialog newInstance(Fragment targetFragment) {
        ServiceRatingView.MODE = -1;
        ServiceRatingDialog pickerDialog = new ServiceRatingDialog();
        pickerDialog.setTargetFragment(targetFragment, 0);
        return pickerDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        try {
            mCallBacks = (CallBacks) targetFragment;
        } catch (ClassCastException ex){
            Log.e(LOG_TAG, "Must implement 'ServiceRatingDialog.CallBacks'.");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ratingView = new ServiceRatingView(getActivity());
        mDialog =
                new AlertDialog.Builder(getActivity(), THEME_HOLO_LIGHT)
                        .setTitle(R.string.rate_service_title)
                        .setView(ratingView, 20, 20, 20, 20)
                        .setNegativeButton(getString(R.string.btn_close), null)
                        .setPositiveButton(getString(R.string.btn_send), null)
                        .create();
        mDialog.setOnShowListener(this);
        return mDialog;
    }

    public final void show(FragmentManager manager) {
        super.show(manager, getClass().getSimpleName());
    }

    @Override
    public void onClick(View _view) {
        if (mCallBacks != null) {
            switch ((int) _view.getTag()){
                case AlertDialog.BUTTON_NEGATIVE:
                    dismiss();
                    break;
                case AlertDialog.BUTTON_POSITIVE:
                    Object[] rating = ratingView.getRating();
                    if (rating == null) {
                        Toast.makeText(getActivity(), R.string.choose_rating_error_message, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mCallBacks.onRate((int) rating[0], (String) rating[1]);
                    mDialog.dismiss();
                    break;
            }
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        int primaryColor = H.getPrimaryColor(getActivity());

        Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(primaryColor);
        positiveButton.setTag(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(this);

        Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(primaryColor);
        negativeButton.setTag(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBacks = null;
    }

    public interface CallBacks{
        void onRate(int _rate, String _description);
    }
}