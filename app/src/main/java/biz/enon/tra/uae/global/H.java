package biz.enon.tra.uae.global;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.XmlRes;
import android.util.Log;
import android.util.TypedValue;

import com.enon.tra.uae.R;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ak-buffalo on 28.07.15.
 *
 * Helper class collect all necessary methods
 */
public class H {
    /**
     * Returns the last non NULL variable
     *
     * @param items items to be set
     *
     * @return Last non NULL value
     * */
    public static <T> T coalesce(T ...items) {
        for(T i : items) if(i != null) return i;
        return null;
    }
    /**
     * Returns parsed resource xml
     *
     * @param context Application context
     * @param xmlRes id of xml resource
     *
     * @return Map
     * */
    public static Map<String, String> parseXmlToMap(Context context, @XmlRes int xmlRes){
        Map<String,String> map = null;
        String key = null, value = null;
        XmlResourceParser parser = context.getResources().getXml(xmlRes);

        try {
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("utils", "Start document");
                } else if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("map")) {
                        boolean isLinked = parser.getAttributeBooleanValue(null, "linked", false);

                        map = isLinked ? new LinkedHashMap<String, String>() : new HashMap<String, String>();
                    } else if (parser.getName().equals("entry")) {
                        key = parser.getAttributeValue(null, "key");

                        if (null == key) {
                            parser.close();
                            return null;
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equals("entry")) {
                        map.put(key, value);
                        key = null;
                        value = null;
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    if (null != key) {
                        value = parser.getText();
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }
    /**
     * Get resource id from string by type
     *
     * @param resName resource id in string representation
     * @param className resource type
     * @return resource id integer representation
     **/
    public static int getResIdFromString(String resName, Class<?> className) {
        try {
            Field idField = className.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Flip Bitmap depends on direction
     *
     * @param src Bitmap to be flipped
     * @param dir Flipping direction
     * @return Already flipped bitmap
     **/
    public static Bitmap flipBitmap(Bitmap src, int dir) {
        Matrix matrix = new Matrix();
        //Vertical
        if(dir == 1) {
            matrix.preScale(1.0f, -1.0f);
        } else
        //Horizontal
        if(dir == -1) {
            matrix.preScale(-1.0f, 1.0f);
        } else {
            return src;
        }

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public static int getPrimaryColor(Context _context){
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = _context.getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }
}
