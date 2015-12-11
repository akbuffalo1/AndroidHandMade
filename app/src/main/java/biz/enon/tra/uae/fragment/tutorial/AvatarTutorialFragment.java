package biz.enon.tra.uae.fragment.tutorial;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.customviews.HexagonalHeader;
import biz.enon.tra.uae.customviews.tutorial.AvatarTipView;

/**
 * Created by Mikazme on 21/09/2015.
 */
public class AvatarTutorialFragment extends Fragment {

    private HexagonalHeader hhAvatar;
    private AvatarTipView atvAvatarTip;

    public static AvatarTutorialFragment newInstance() {
        AvatarTutorialFragment fragment = new AvatarTutorialFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater _inflater, final ViewGroup _viewGroup, final Bundle _savedInstanceState) {
        final View view = _inflater.inflate(R.layout.fragment_avatar_tutorial, _viewGroup, false);

        initViews(view);
        initListeners();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initViews(final View _view) {
        hhAvatar = (HexagonalHeader) _view.findViewById(R.id.hhHeader_FAT);

        atvAvatarTip = (AvatarTipView) _view.findViewById(R.id.atvAvatarTip_FAT);
        hhAvatar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                atvAvatarTip.setCenterPoints(new PointF[]{hhAvatar.getAvatarCenter()});
                atvAvatarTip.setSideOffset(hhAvatar.getAvatarSideOffset());

                hhAvatar.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    private void initListeners() {

    }
}
