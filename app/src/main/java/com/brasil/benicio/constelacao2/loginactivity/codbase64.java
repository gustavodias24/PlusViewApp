package com.brasil.benicio.constelacao2.loginactivity;

import android.util.Base64;

public class codbase64 {

    public static String cod(String text){
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replaceAll("(\\n | \\r)", "");
    }

    public static String decod(String text){
        return new String(Base64.decode(text, Base64.DEFAULT));
    }
}
