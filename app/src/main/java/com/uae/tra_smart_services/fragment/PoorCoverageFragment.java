package com.uae.tra_smart_services.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.fragment.base.BaseFragment;

/**
 * Created by ak-buffalo on 11.08.15.
 */
public class PoorCoverageFragment extends BaseFragment {

    public static PoorCoverageFragment newInstance() {
        return new PoorCoverageFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_poor_coverage;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    private EditText etLocation;
    private SeekBar sbPoorCoverage;
    @Override
    protected void initViews() {
        super.initViews();
        etLocation = findView(R.id.etLocation_FPC);
        sbPoorCoverage = findView(R.id.sbPoorCoverage_FPC);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_send, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.menu.menu_send:
                // TODO implement sending logic
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}