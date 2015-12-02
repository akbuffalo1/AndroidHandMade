package com.uae.tra_smart_services.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.fragment.base.BaseFragment;

import hugo.weaving.DebugLog;

/**
 * Created by mobimaks on 01.12.2015.
 */
public class AboutTraFragment extends BaseFragment implements OnTabSelectedListener {

    private static final String KEY_SELECTED_TAB = "SELECTED_TAB";

    private static final int CONTAINER = R.id.flContainer_FAT;
    public static final int ABOUT_US_TAB = 0;
    public static final int CONTACT_US_TAB = 1;

    private TabLayout tlTabs;
    private int mSelectedTabPosition;

    public static AboutTraFragment newInstance() {
        return new AboutTraFragment();
    }

    @Override
    protected void initViews() {
        super.initViews();
        tlTabs = findView(R.id.tlTabs_FAT);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        tlTabs.setOnTabSelectedListener(this);
    }

    @DebugLog
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mSelectedTabPosition = savedInstanceState.getInt(KEY_SELECTED_TAB);
        }

        tlTabs.addTab(tlTabs.newTab().setText(R.string.str_about_tra), ABOUT_US_TAB, ABOUT_US_TAB == mSelectedTabPosition);
        tlTabs.addTab(tlTabs.newTab().setText(R.string.fragment_contact_us), CONTACT_US_TAB, CONTACT_US_TAB == mSelectedTabPosition);
    }

    @DebugLog
    @Override
    public void onTabSelected(final Tab tab) {
        mSelectedTabPosition = tab.getPosition();
        switch (tab.getPosition()) {
            case ABOUT_US_TAB:
                setFragmentIfNeed(AboutTraInfoFragment.newInstance());
                break;
            case CONTACT_US_TAB:
                setFragmentIfNeed(ContactUsFragment.newInstance());
                break;
        }
    }

    private void setFragmentIfNeed(@NonNull BaseFragment _fragment) {
        final Fragment containerFragment = getChildFragmentManager().findFragmentById(CONTAINER);
        if (!_fragment.getClass().isInstance(containerFragment)) {
            setFragment(_fragment);
        }
    }

    private void setFragment(@NonNull BaseFragment _fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(CONTAINER, _fragment)
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_TAB, mSelectedTabPosition);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_about_tra;
    }

    @Override
    protected int getTitle() {
        return R.string.str_about_tra;
    }

    @Override
    public void onTabUnselected(Tab tab) {

    }

    @Override
    public void onTabReselected(Tab tab) {

    }
}
