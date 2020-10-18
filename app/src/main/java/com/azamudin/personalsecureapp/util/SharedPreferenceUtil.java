package com.azamudin.personalsecureapp.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.azamudin.personalsecureapp.app.AppContext;

public class SharedPreferenceUtil {
    private static final String SHARED_NAME = "save";

    public static void put(String key, Object value) {
        SharedPreferences preference = AppContext.getAppContext().getSharedPreferences(SHARED_NAME, 0);
        Editor editor = preference.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }
        editor.commit();
    }

    public static Object get(String key, Object defaultValue) {
        SharedPreferences preference = AppContext.getAppContext().getSharedPreferences(SHARED_NAME, 0);
        if (defaultValue instanceof String) {
            return preference.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return preference.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Float) {
            return preference.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return preference.getBoolean(key, (Boolean) defaultValue);
        } else {
//            return preference.get(key, (Object) defaultValue);
            throw new RuntimeException("Error type");
        }
    }

    public static void clear() {
        SharedPreferences preference = AppContext.getAppContext().getSharedPreferences(SHARED_NAME, 0);
        Editor eidtor = preference.edit();
        eidtor.clear().commit();
    }

}
