package biz.enon.tra.uae.fragment;

import android.os.Bundle;
import android.view.View;

import biz.enon.tra.uae.interfaces.LoaderMarker;

/**
 * Created by ak-buffalo on 27.11.15.
 */
public class CustomLoaderFragment extends LoaderFragment{

    public static CustomLoaderFragment newInstance(String _msg, LoaderMarker _listener, boolean _showRating) {
        Bundle args = new Bundle();
        args.putString(MSG, _msg);
        args.putBoolean(SHOW_RATING, _showRating);
        CustomLoaderFragment fragment = new CustomLoaderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    protected void initViews() {
        super.initViews();
        tvBackOrCancelBtn.setVisibility(View.GONE);
    }
}
