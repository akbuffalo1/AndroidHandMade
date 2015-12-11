package biz.enon.tra.uae.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import biz.enon.tra.uae.fragment.tutorial.AvatarTutorialFragment;
import biz.enon.tra.uae.fragment.tutorial.HotBarTutorialFragment;
import biz.enon.tra.uae.fragment.tutorial.TabBarTutorialFragment;

/**
 * Created by Mikazme on 24/09/2015.
 */
public class TutorialPagerAdapter extends FragmentPagerAdapter {

    public static final int TUTORIAL_SCREENS_COUNT = 3;

    public static final int TUTORIAL_AVATAR = 0;
    public static final int TUTORIAL_HOT_BAR = 1;
    public static final int TUTORIAL_TAB_BAR = 2;

    public TutorialPagerAdapter(FragmentManager _fragmentManager) {
        super(_fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        final Fragment fragment;
        switch (position) {
            default:
            case TUTORIAL_AVATAR:
                fragment = AvatarTutorialFragment.newInstance();
                break;
            case TUTORIAL_HOT_BAR:
                fragment = HotBarTutorialFragment.newInstance();
                break;
            case TUTORIAL_TAB_BAR:
                fragment = TabBarTutorialFragment.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TUTORIAL_SCREENS_COUNT;
    }
}
