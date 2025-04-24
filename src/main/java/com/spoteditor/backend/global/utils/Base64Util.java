package com.spoteditor.backend.config.util;

import java.util.Base64;

public class Base64Util {

    public static String URLENCODE(String text) {
        return Base64.getUrlEncoder().encodeToString(text.getBytes());
    }

    public static String URLDECODE(String encodedText) {
        return new String(Base64.getUrlDecoder().decode(encodedText));
    }

}
