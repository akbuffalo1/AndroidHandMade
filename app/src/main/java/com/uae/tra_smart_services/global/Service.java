package com.uae.tra_smart_services.global;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.global.C.ServiceName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobimaks on 10.08.2015.
 */
public enum Service {

    COMPLAIN_ABOUT_PROVIDER {
        @Override
        @StringRes
        public int getTitleRes() {
            return R.string.service_complain_about_provider;
        }

        @Override
        @DrawableRes
        public final int getDrawableRes() {
            return R.drawable.ic_edit;
        }

        @Override
        public String getServiceName() {
            return C.COMPLAIN_ABOUT_SERVICE_PROVIDER;
        }
    },
    COMPLAINT_ABOUT_TRA {
        @Override
        @StringRes
        public int getTitleRes() {
            return R.string.service_complain_about_tra;
        }

        @Override
        @DrawableRes
        public final int getDrawableRes() {
            return R.drawable.ic_chat;
        }

        @Override
        public String getServiceName() {
            return C.COMPLAIN_ABOUT_TRA;
        }
    },
    SUGGESTION {
        @Override
        @StringRes
        public int getTitleRes() {
            return R.string.service_suggestion;
        }

        @Override
        @DrawableRes
        public final int getDrawableRes() {
            return R.drawable.ic_sugg;
        }

        @Override
        public String getServiceName() {
            return C.SUGGESTION;
        }
    },
    DOMAIN_CHECK {
        protected boolean isStaticMainScreenService() {
            return true;
        }

        @Override
        @StringRes
        public int getTitleRes() {
            return R.string.service_domain_check;
        }

        @Override
        @DrawableRes
        public final int getDrawableRes() {
            return R.drawable.ic_glb;
        }

        @Nullable
        @Override
        public String getServiceName() {
            return C.DOMAIN_CHECK;
        }
    },
    DOMAIN_CHECK_INFO {
        @Override
        protected boolean isMainScreenService() {
            return false;
        }

        @Override
        @StringRes
        public int getTitleRes() {
            return R.string.service_domain_check;
        }

        @Override
        @DrawableRes
        public final int getDrawableRes() {
            return R.drawable.ic_glb;
        }

        @Override
        public String toString() {
            return "domain_info_check";
        }
    },
    DOMAIN_CHECK_AVAILABILITY {
        @Override
        protected boolean isMainScreenService() {
            return false;
        }

        @Override
        @StringRes
        public int getTitleRes() {
            return R.string.service_domain_check;
        }

        @Override
        @DrawableRes
        public final int getDrawableRes() {
            return R.drawable.ic_glb;
        }

        @Override
        public String toString() {
            return "domain_info_availabity";
        }
    },
    ENQUIRIES {
        @Override
        @StringRes
        public int getTitleRes() {
            return R.string.service_enquiries;
        }

        @Override
        @DrawableRes
        public final int getDrawableRes() {
            return R.drawable.ic_play;
        }

        @Override
        public String getServiceName() {
            return C.ENQUIRIES;
        }
    },
    APPROVED_DEVICES {
        @Override
        @StringRes
        public int getTitleRes() {
            return R.string.service_approved_devices;
        }

        @Override
        @DrawableRes
        public final int getDrawableRes() {
            return R.drawable.ic_mob_br;
        }

        @Override
        public String getServiceName() {
            return C.MOBILE_BRAND;
        }
    },
    //    HELP_SALIM {
//        @Override
//        @StringRes
//        public int getTitleRes() {
//            return R.string.service_help_salim;
//        }
//
//        @Override
//        @DrawableRes
//        public final int getDrawableRes() {
//            return R.drawable.ic_edit;
//        }
//
//    },
    MOBILE_VERIFICATION {
        @Override
        protected boolean isStaticMainScreenService() {
            return true;
        }

        @Override
        @StringRes
        public int getTitleRes() {
            return R.string.service_mobile_verification;
        }

        @Override
        public int getDrawableRes() {
            return R.drawable.ic_verif_gray;
        }

        @Override
        public String getServiceName() {
            return C.VERIFICATION;
        }
    },
    REPORT_SPAM {
        @Override
        protected boolean isStaticMainScreenService() {
            return true;
        }

        @Override
        @StringRes
        public int getTitleRes() {
            return R.string.service_report_spam;
        }

        @Override
        public int getDrawableRes() {
            return R.drawable.ic_spam_gray;
        }

        @Nullable
        @Override
        public String getServiceName() {
            return C.SPAM_REPORT;
        }
    },
    POOR_COVERAGE {
        @Override
        protected boolean isStaticMainScreenService() {
            return true;
        }

        @Override
        @StringRes
        public int getTitleRes() {
            return R.string.service_poor_coverage;
        }

        @Override
        public int getDrawableRes() {
            return R.drawable.ic_coverage_gray;
        }

        @Override
        public String getServiceName() {
            return C.COVERAGE;
        }
    };

//    , INTERNET_SPEEDTEST {
//        @Override
//        protected boolean isStaticMainScreenService() {
//            return true;
//        }
//
//        @Override
//        public int getTitleRes() {
//            return R.string.fragment_speed_test_title;
//        }
//
//        @Override
//        public int getDrawableRes() {
//            return R.drawable.ic_internet_gray;
//        }
//    };

    @DrawableRes
    public int getDrawableRes() {
        return R.drawable.ic_glb;
    }

    @StringRes
    public abstract int getTitleRes();

    @ServiceName
    @Nullable
    public String getServiceName() {
        return null;
    }

    @Nullable
    public String getTransactionName(){
        return null;
    }

    protected boolean isMainScreenService() {
        return true;
    }

    protected boolean isStaticMainScreenService() {
        return false;
    }

    public final String getTitle(final Context _context) {
        return _context.getString(getTitleRes());
    }

    private static List<Service> UNIQUE_SERVICES;

    public static List<Service> getUniqueServices() {
        initUniqueServicesList();
        return new ArrayList<>(UNIQUE_SERVICES);
    }

    private static void initUniqueServicesList() {
        if (UNIQUE_SERVICES == null) {
            UNIQUE_SERVICES = new ArrayList<>();
            for (Service service : Service.values()) {
                if (service.isMainScreenService()) {
                    UNIQUE_SERVICES.add(service);
                }
            }
        }
    }

    public static int getUniqueServicesCount() {
        initUniqueServicesList();
        return UNIQUE_SERVICES.size();
    }

    public static List<Service> getSecondaryServices() {
        List<Service> services = new ArrayList<>();
        for (Service service : Service.values()) {
            if (service.isMainScreenService() && !service.isStaticMainScreenService()) {
                services.add(service);
            }
        }
        return services;
    }

    public static Service getServiceTypeByString(String _serviceStr){
        for(Service service : UNIQUE_SERVICES){
            if(_serviceStr != null && _serviceStr.equals(service.getTransactionName())){
                return service;
            }
        }
        return null;
    }
}