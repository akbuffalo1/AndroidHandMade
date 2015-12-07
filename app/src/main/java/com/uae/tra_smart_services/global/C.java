package com.uae.tra_smart_services.global;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;
import android.widget.Toast;

import com.uae.tra_smart_services.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mobimaks on 13.08.2015.
 */
public final class C {

    private C() {
    }

    public static final int TOAST_LENGTH = Toast.LENGTH_LONG;

    public static final String KEY_SCANNER_RESULT_TEXT = "SCANNER_RESULT_TEXT";

    public static final String KEY_BASE_URL = "BASE_URL";

    public static final int REQUEST_CODE_LOGIN = 1;
    public static final int LOGIN_SUCCESS = 2;
    public static final int LOGIN_SKIP = 3;
    public static final String ACTION_LOGIN = "login";

    public static final String KEY_BLACK_AND_WHITE_MODE = "BLACK_AND_WHITE_MODE";

    public static final String BLACK_AND_WHITE_THEME = "AppThemeBlackAndWhite";

    public static final String ORANGE_THEME = "AppThemeOrange";

    public static final String DOMAIN_PATTERN = "^(http|ftp|https)://|^[a-zA-Z0-9]+\\.[a-zA-Z][a-zA-Z]";

    public static final String DOMAIN_INFO = "domainStrValue";

    public static final String DOMAIN_STATUS = "domain_status";

    public static final String DOMAIN_INFO_RATING = "domain_info_rating";

    public static final String INFO_HUB_ANN_DATA = "InfoHubAnnData";

    public static final String IS_CANCELABLE = "isCancelabel";

    public static final String TITLE = "text";

    public static final String KEY_FAVORITE_SERVICES = "FAVORITE_SERVICES";

    public static final String FAVORITE_SERVICES_DELIMITER = ",";

    public static final String FRAGMENT_FOR_REPLACING = "fragment_for_replacing";

    public static final String NOT_FIRST_START = "not_first_start";

    public static final String USE_BACK_STACK = "USE_BACK_STACK";

    public static final String UNCHECK_TAB_IF_NOT_LOGGED_IN = "UNCHECK_TAB_IF_NOT_LOGGED_IN";

    public static final String FRAGMENT_DATA = "FRAGMENT_DATA";

    public static final String IS_LOGGED_IN = "is_logged_in";

    //region Service names const
    @StringDef({
            COVERAGE,
            COMPLAIN_ABOUT_TRA,
            COMPLAIN_ABOUT_SERVICE_PROVIDER,
            SUGGESTION,
            ENQUIRIES,
            SPAM_REPORT,
            MOBILE_BRAND,
            VERIFICATION,
            DOMAIN_CHECK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ServiceName {
    }

    public static final String COVERAGE = "coverage";
    public static final String COMPLAIN_ABOUT_TRA = "complain about tra";
    public static final String COMPLAIN_ABOUT_SERVICE_PROVIDER = "complain about service provider";
    public static final String SUGGESTION = "suggestion";
    public static final String ENQUIRIES = "enquiries";
    public static final String SPAM_REPORT = "spam report";
    public static final String MOBILE_BRAND = "mobile brand";
    public static final String VERIFICATION = "verification";
    public static final String DOMAIN_CHECK = "domain check";
    //endregion

    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 32;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 32;

    //region Languages names const
    @StringDef({ENGLISH, ARABIC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Language {
    }

    public static final String ENGLISH = "en";
    public static final String ARABIC = "ar";
    //endregion

    public static final float AR_SCALE_COEFFICIENT = 0.85f;
    public static final float SMALL_FONT_SCALE = 0.5f;
    public static final float MEDIUM_FONT_SCALE = 1f;
    public static final float LARGE_FONT_SCALE = 1.5f;


    //region Http method const
    @StringDef({HttpMethod.GET, HttpMethod.POST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HttpMethod {
        String GET = "GET";
        String POST = "POST";
    }
    //endregion

    //region Spice manager const
    @IntDef({SpiceManager.TRA_SERVICES_API, SpiceManager.DYNAMIC_SERVICES_API})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SpiceManager {
        int TRA_SERVICES_API = 0;
        int DYNAMIC_SERVICES_API = 1;
    }
    //endregion

    //region Transactions statuses const
    public static Map<String, int[]> TRANSACTION_STATUS = new HashMap<>();

    public static final String WAITING_FOR_DETAILS = "Waiting for Details";
    public static final String RESEARCHING = "Researching";
    public static final String ON_HOLD = "On Hold";
    public static final String WAITING_TO_BE_REVIEWED = "Waiting to be Reviewed";
    public static final String APPROVED = "Approved";
    public static final String IMPLEMENTED = "Implemented";
    public static final String REFERRED_TO_LICENSEE = "Referred To Licensee";
    public static final String IN_PROGRESS = "In Progress";

    static {
        TRANSACTION_STATUS.put(null, new int[]{R.drawable.ic_form, R.color.hex_primary_green});

        TRANSACTION_STATUS.put(IN_PROGRESS, new int[]{R.drawable.ic_form, R.color.hex_primary_green});
        TRANSACTION_STATUS.put(REFERRED_TO_LICENSEE, new int[]{R.drawable.ic_form, R.color.hex_primary_green});
        TRANSACTION_STATUS.put(IMPLEMENTED, new int[]{R.drawable.ic_form, R.color.hex_primary_green});
        TRANSACTION_STATUS.put(APPROVED, new int[]{R.drawable.ic_form, R.color.hex_primary_green});
        TRANSACTION_STATUS.put(WAITING_TO_BE_REVIEWED, new int[]{R.drawable.ic_form, R.color.hex_primary_green});

        TRANSACTION_STATUS.put(ON_HOLD, new int[]{R.drawable.ic_download, R.color.hex_primary_orange});
        TRANSACTION_STATUS.put(RESEARCHING, new int[]{R.drawable.ic_download, R.color.hex_primary_orange});

        TRANSACTION_STATUS.put(WAITING_FOR_DETAILS, new int[]{R.drawable.ic_domain, R.color.hex_primary_red});
    }

    public static final int TRANSACTION_STATUS_ICON_INDEX = 0;
    public static final int TRANSACTION_STATUS_COLOR_INDEX = 1;
    //endregion

    //region Transaction names const
    public static final String COMPLAINT_ABOUT_SERVICE_PROVIDER = "Complaint about Service Provider";
    public static final String COMPLAINT_ABOUT_TRA = "Complaint about TRA";
    public static final String INQUIRY = "Inquiry";
    public static final String WEB_REPORT = "Web Report";
    public static final String SMS_SPAM = "SMS Spam";
    //endregion

}
