package com.uae.tra_smart_services.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.customviews.ServiceRatingView;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

/**
 * Created by ak-buffalo on 02.10.15.
 */
public class ServiceRatingDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String LOG_TAG = ServiceRatingDialog.class.getSimpleName();

    private CallBacks mCallBacks;
    private ServiceRatingView ratingView;

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
        TextView title = new TextView(getActivity());
        title.setText(R.string.rate_service_title);
        title.setPadding(40, 30, 0, 20);
        title.setGravity(Gravity.LEFT);
        title.setTextColor(Color.DKGRAY);
        title.setTextSize(20);

        AlertDialog.Builder alertBuilder =
                new AlertDialog.Builder(getActivity(), THEME_HOLO_LIGHT)
                        .setCustomTitle(title)
                        .setView(ratingView, 20, 20, 20, 20);

        alertBuilder.setNegativeButton(getString(R.string.btn_close), this);
        alertBuilder.setPositiveButton(getString(R.string.btn_send), this);

        return alertBuilder.create();
    }

    public final void show(FragmentManager manager) {
        super.show(manager, getClass().getSimpleName());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mCallBacks != null) {
            switch (which){
                case DialogInterface.BUTTON_NEGATIVE:
                    dismiss();
                    break;
                case DialogInterface.BUTTON_POSITIVE:
                    Object[] rating = ratingView.getRating();
                    if ((int) rating[0] == 0){
                        Toast.makeText(getActivity(), R.string.choose_rating_error_message, Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        mCallBacks.onRate((int) rating[0], (String) rating[1]);
                        dismiss();
                    }
                    break;
            }
        }
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