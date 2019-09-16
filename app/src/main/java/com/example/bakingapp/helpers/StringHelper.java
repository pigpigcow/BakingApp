package com.example.bakingapp.helpers;

public class StringHelper {
    public static boolean isValid(String s) {
        return s != null && s.trim().length() > 0;
    }

    // Valid if all is valid
    public static boolean isValid(String... s) {
        boolean result = s != null;
        if(result) {
            for(String str : s) {
                if(!isValid(str)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    public static Object getIfNotNull(Object o, Object O) {
        return o == null ? null : O;
    }

    public static Object getIfNotNull(Object o) {
        return o == null ? null : o;
    }

    public static Object getIfNotNull(String s, Object O) {
        return isValid(s) ? O : null;
    }

    public static String getIfNotNull(String s) {
        return isValid(s) ? s : null;
    }
}
