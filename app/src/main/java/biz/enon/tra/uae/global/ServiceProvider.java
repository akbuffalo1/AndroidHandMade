package biz.enon.tra.uae.global;

import android.support.annotation.StringRes;

import biz.enon.tra.uae.R;

/**
 * Created by mobimaks on 11.08.2015.
 */
public enum ServiceProvider {

    DU {
        @Override
        public int getTitleRes() {
            return R.string.fragment_complain_about_service_du;
        }

        @Override
        public String toString() {
            return "du";
        }
    },
    ETISALAT {
        @Override
        public int getTitleRes() {
            return R.string.fragment_complain_about_service_etisalat;
        }

        @Override
        public String toString() {
            return "Etisalat";
        }
    };

    @StringRes
    public abstract int getTitleRes();

//    public static String[] toStringArray() {
//        final ServiceProvider[] providers = values();
//        final String[] strProviders = new String[providers.length];
//        for (int i = 0; i < providers.length; i++) {
//            strProviders[i] = providers[i].toString();
//        }
//        return strProviders;
//    }

    public static int[] toStringResArray(){
        final ServiceProvider[] providers = values();
        final int[] strProviders = new int[providers.length];
        for (int i = 0; i < providers.length; i++) {
            strProviders[i] = providers[i].getTitleRes();
        }
        return strProviders;
    }
}
