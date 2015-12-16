package biz.enon.tra.uae.fragment.tutorial;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.Locale;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.adapter.TutorialPagerAdapter;
import biz.enon.tra.uae.global.C;

/**
 * Created by Mikazme on 24/09/2015.
 */
public class TutorialContainerFragment extends Fragment implements OnClickListener {

    private ViewPager vpTutorial;
    private CirclePageIndicator cpiCirclePageIndicator;
    private ImageView ivCLose;
    private TutorialPagerAdapter mAdapter;
    private OnTuorialClosedListener mOnTutorialClosedListener;
    private boolean isFirstStart = false;

    public static TutorialContainerFragment newInstance() {
        TutorialContainerFragment fragment = new TutorialContainerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnTutorialClosedListener = (OnTuorialClosedListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater _inflater, final ViewGroup _viewGroup, final Bundle _savedInstanceState) {
        final View view = _inflater.inflate(R.layout.fragment_tutorial_container, _viewGroup, false);

        if (!PreferenceManager.getDefaultSharedPreferences(getActivity()).contains(C.NOT_FIRST_START)) {
            isFirstStart = true;
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().
                    putBoolean(C.NOT_FIRST_START, true).apply();
        }

        initViews(view);
        initListeners();

        return view;
    }

    private void initViews(final View _view) {
        vpTutorial = (ViewPager) _view.findViewById(R.id.vpTutorial_FTC);
        mAdapter = new TutorialPagerAdapter(getChildFragmentManager(), vpTutorial);
        vpTutorial.setAdapter(mAdapter);
        mAdapter.setRtlMode(Locale.getDefault().getLanguage().equals("ar"));

        cpiCirclePageIndicator = (CirclePageIndicator) _view.findViewById(R.id.cpiCirclePageIndicator_FTC);
        cpiCirclePageIndicator.setViewPager(vpTutorial);

        ivCLose = (ImageView) _view.findViewById(R.id.ivCLose_FTC);
    }

    private void initListeners() {
        ivCLose.setOnClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnTutorialClosedListener = null;
    }

    @Override
    public final void onClick(final View _view) {
        switch (_view.getId()) {
            case R.id.ivCLose_FTC:
                if (!isFirstStart)
                    mOnTutorialClosedListener.onTutorialClosed();
                getFragmentManager().popBackStack();
                break;
        }
    }

    public interface OnTuorialClosedListener {
        void onTutorialClosed();
    }
}
