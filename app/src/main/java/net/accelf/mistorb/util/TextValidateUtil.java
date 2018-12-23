package net.accelf.mistorb.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextValidateUtil {

    private static final String REGEX_HOST_NAME =
            "([A-Za-z0-9][A-Za-z0-9\\-]{1,61}[A-Za-z0-9]\\.)+[A-Za-z]+";

    public static boolean isHostName(String text) {
        if (text == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(REGEX_HOST_NAME);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

}
