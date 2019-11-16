package com.github.warren_bank.bonjour_webrtc.ui.components;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class FloatPreference extends EditTextPreference {

    public FloatPreference(Context context) {
        super(context);
    }

    public FloatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private static float parseValue(String value) {
        return parseValue(value, Float.NEGATIVE_INFINITY);
    }

    private static float parseValue(String value, float defValue) {
        try {
            return Float.parseFloat(value);
        }
        catch(Exception e) {
            return defValue;
        }
    }

    private static String formatString(double d) {
        return (d == (long) d)
          ? String.format("%d", (long)d)
          : String.format("%s", d)
        ;
    }

    @Override
    protected String getPersistedString(String defValueStr) {
        float defValue = parseValue(defValueStr);
        return formatString(getPersistedFloat(defValue));
    }

    @Override
    protected boolean persistString(String valueStr) {
        float value = parseValue(valueStr, Float.NEGATIVE_INFINITY);
        return (value != Float.NEGATIVE_INFINITY)
          ? persistFloat(value)
          : false
        ;
    }
}
