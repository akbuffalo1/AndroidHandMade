package biz.enon.tra.uae.util;

import java.util.regex.Pattern;

/**
 * Created by Mikazme on 23/09/2015.
 */
public class TRAPatterns {

    public static final Pattern EMIRATES_ID = Pattern.compile("^[0-9]{3}[-][0-9]{4}[-][0-9]{7}[-][0-9]$");

    public static final Pattern AE_DOMAIN_PATTERN = Pattern.compile("^.+(\\.ae)\\/?$");

    public static final int MIN_PHONE_NUMBER_LENGTH = 4;
}
