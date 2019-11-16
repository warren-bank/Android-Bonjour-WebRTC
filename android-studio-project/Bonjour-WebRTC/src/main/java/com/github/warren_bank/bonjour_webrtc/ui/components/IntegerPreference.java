package com.github.warren_bank.bonjour_webrtc.ui.components;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class IntegerPreference extends EditTextPreference {

    public IntegerPreference(Context context) {
        super(context);
    }

    public IntegerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntegerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private int parseValue(String value) {
        return parseValue(value, Integer.MIN_VALUE);
    }

    private int parseValue(String value, int defValue) {
        try {
            return Integer.parseInt(value, 10);
        }
        catch(Exception e) {
            return defValue;
        }
    }

    @Override
    protected String getPersistedString(String defValueStr) {
        int defValue = parseValue(defValueStr);
        return Integer.toString(getPersistedInt(defValue));
    }

    @Override
    protected boolean persistString(String valueStr) {
        int value = parseValue(valueStr, Integer.MIN_VALUE);
        return (value != Integer.MIN_VALUE)
          ? persistInt(value)
          : false
        ;
    }
}
