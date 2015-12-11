package biz.enon.tra.uae.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.adapter.AttachmentOptionsAdapter;
import biz.enon.tra.uae.global.AttachmentOption;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

/**
 * Created by mobimaks on 11.08.2015.
 */
public class AttachmentPickerDialog extends DialogFragment implements OnItemClickListener {

    private static final String KEY_ATTACHMENT_OPTIONS = "ATTACHMENT_OPTIONS";

    private OnImageSourceSelectListener mSelectListener;
    private List<AttachmentOption> mAttachmentOptions;
    private ListView mOptionsList;

    public static AttachmentPickerDialog newInstance(final @NonNull Fragment targetFragment,
                                                     final @NonNull AttachmentOption[] _attachmentOptions) {
        final AttachmentPickerDialog pickerDialog = new AttachmentPickerDialog();
        final Bundle args = new Bundle();
        args.putSerializable(KEY_ATTACHMENT_OPTIONS, _attachmentOptions);
        pickerDialog.setArguments(args);
        pickerDialog.setTargetFragment(targetFragment, 0);
        return pickerDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof OnImageSourceSelectListener) {
            mSelectListener = (OnImageSourceSelectListener) targetFragment;
        }
        mAttachmentOptions = Arrays.asList((AttachmentOption[]) getArguments().getSerializable(KEY_ATTACHMENT_OPTIONS));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initOptionsList();
        return new AlertDialog.Builder(getActivity(), THEME_HOLO_LIGHT)
                .setTitle(R.string.fragment_complain_about_service_take_image_from)
                .setView(mOptionsList)
                .create();
    }

    private void initOptionsList() {
        mOptionsList = new ListView(getActivity());
        mOptionsList.setDivider(null);
        mOptionsList.setAdapter(new AttachmentOptionsAdapter(getActivity(), mAttachmentOptions));
        mOptionsList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        AttachmentOption mSelectedOption = (AttachmentOption) mOptionsList.getAdapter().getItem(position);
        if (mSelectListener != null) {
            mSelectListener.onImageSourceSelect(mSelectedOption);
        }
    }

    public final void show(FragmentManager manager) {
        super.show(manager, getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        mSelectListener = null;
        super.onDestroy();
    }

    public interface OnImageSourceSelectListener {
        void onImageSourceSelect(final AttachmentOption _option);
    }
}
