package biz.enon.tra.uae.global;

import android.support.annotation.StringRes;

import biz.enon.tra.uae.R;

/**
 * Created by ak-buffalo on 14.08.15.
 */
public enum LocationType {
    AUTO {
        @Override
        public int getTitleRes() {
            return R.string.fragment_poor_coverage_location_type_auto;
        }

        @Override
        public String toString() {
            return "Automatically";
        }
    },
    MANUAL {
        @Override
        public int getTitleRes() {
            return R.string.fragment_poor_coverage_location_type_manual;
        }

        @Override
        public String toString() {
            return "Enter your location";
        }
    };

    public static String[] toStringArray() {
        final LocationType[] providers = values();
        final String[] strTypes = new String[providers.length];
        for (int i = 0; i < providers.length; i++) {
            strTypes[i] = providers[i].toString();
        }
        return strTypes;
    }

    @StringRes
    public abstract int getTitleRes();

    public static int[] toStringResArray() {
        final LocationType[] locationTypes = values();
        final int[] strLocationTypes = new int[locationTypes.length];
        for (int i = 0; i < locationTypes.length; i++) {
            strLocationTypes[i] = locationTypes[i].getTitleRes();
        }
        return strLocationTypes;
    }
}
