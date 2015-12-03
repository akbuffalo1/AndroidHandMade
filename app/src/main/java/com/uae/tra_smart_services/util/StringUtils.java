package com.uae.tra_smart_services.util;

import android.support.annotation.Nullable;

/**
 * Created by Vitaliy on 08/10/2015.
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isAllLettersOrWhiteSpace(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                return false;
            }
        }

        return true;
    }

    @Nullable
    public static String trim(@Nullable final String string){
        return string == null ? null : string.trim();
    }
}
