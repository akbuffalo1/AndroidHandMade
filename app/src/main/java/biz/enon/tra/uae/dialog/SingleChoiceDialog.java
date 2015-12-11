package biz.enon.tra.uae.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

/**
 * Created by ak-buffalo on 14.08.15.
 */
public class SingleChoiceDialog extends DialogFragment implements Dialog.OnClickListener {

    private static final String KEY_TITLE_RES = "TITLE_RES";
    private static final String KEY_ITEMS_RES = "ITEMS_RES";
    private static final String KEY_TITLE_STR = "TITLE_STR";
    private static final String KEY_ITEMS_STR = "ITEMS_STR";
    private static final String KEY_IS_DATA_FROM_RES = "IS_DATA_FROM_RES";

    private OnItemPickListener mSelectListener;
    private boolean mIsDataFromRes;

    private String mTitle;
    private String[] mBodyItems;

    @StringRes
    private int mTitleRes;
    private int[] mBodyItemsRes;

    public static SingleChoiceDialog newInstance(Fragment _targetFragment, @StringRes int _titleRes, int[] _itemsRes) {
        SingleChoiceDialog pickerDialog = new SingleChoiceDialog();
        pickerDialog.setTargetFragment(_targetFragment, 0);
        Bundle args = new Bundle();
        args.putInt(KEY_TITLE_RES, _titleRes);
        args.putIntArray(KEY_ITEMS_RES, _itemsRes);
        args.putBoolean(KEY_IS_DATA_FROM_RES, true);
        pickerDialog.setArguments(args);
        return pickerDialog;
    }

    public static SingleChoiceDialog newInstance(Fragment _targetFragment, String _title, String[] _items) {
        SingleChoiceDialog pickerDialog = new SingleChoiceDialog();
        pickerDialog.setTargetFragment(_targetFragment, 0);
        Bundle args = new Bundle();
        args.putString(KEY_TITLE_STR, _title);
        args.putStringArray(KEY_ITEMS_STR, _items);
        args.putBoolean(KEY_IS_DATA_FROM_RES, false);
        pickerDialog.setArguments(args);
        return pickerDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof OnItemPickListener) {
            mSelectListener = (OnItemPickListener) targetFragment;
        }

        final Bundle args = getArguments();
        if (args != null) {
            initArguments(args);
        }

    }

    private void initArguments(final Bundle _args) {
        mTitle = _args.getString(KEY_TITLE_STR);
        mBodyItems = _args.getStringArray(KEY_ITEMS_STR);

        mTitleRes = _args.getInt(KEY_TITLE_RES);
        mBodyItemsRes = _args.getIntArray(KEY_ITEMS_RES);

        mIsDataFromRes = _args.getBoolean(KEY_IS_DATA_FROM_RES);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), THEME_HOLO_LIGHT);
        if (mIsDataFromRes) {
            builder.setTitle(mTitleRes)
                    .setItems(getStringArrayFromRes(mBodyItemsRes), this);
        } else {
            builder.setTitle(mTitle)
                    .setItems(mBodyItems, this);
        }
        return builder.create();
    }

    private String[] getStringArrayFromRes(int[] _itemsRes) {
        final String[] items = new String[_itemsRes.length];
        for (int i = 0; i < _itemsRes.length; i++) {
            items[i] = getString(_itemsRes[i]);
        }
        return items;
    }

    public final void show(FragmentManager manager) {
        super.show(manager, getClass().getSimpleName());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mSelectListener != null) {
            mSelectListener.onItemPicked(which);
        }
    }

    @Override
    public void onDestroy() {
        mSelectListener = null;
        super.onDestroy();
    }

    public void unregisterListener() {
        mSelectListener = null;
    }

    public interface OnItemPickListener {
        void onItemPicked(final int _dialogItem);
    }
}