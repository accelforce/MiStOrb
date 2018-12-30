package net.accelf.mistorb.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextValidateUtil {

    private static final String REGEX_HOST_NAME =
            "(([A-Za-z0-9][A-Za-z0-9\\-]{1,61}[A-Za-z0-9]\\.)+[A-Za-z]+" + //example.com
                    "|(([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])" + //127.0.0.1
                    "|localhost)";
    private static final String REGEX_PORT = "([1-9][0-9]{3}|[1-9][0-9]{2}|[1-9][0-9]{1})";

    public static boolean isHostName(String text) {
        if (text == null) {
            return false;
        }
        String[] target = withPort(text);
        switch (target.length) {
            case 2: {
                Pattern pattern = Pattern.compile(REGEX_PORT);
                Matcher matcher = pattern.matcher(target[1]);
                if (!matcher.matches()){
                    return false;
                }
            }
            case 1: {
                Pattern pattern = Pattern.compile(REGEX_HOST_NAME);
                Matcher matcher = pattern.matcher(target[0]);
                return matcher.matches();
            }
            default:
                return false;
        }
    }

    private static String[] withPort(String text) {
        Pattern pattern = Pattern.compile(REGEX_HOST_NAME + ":" + REGEX_PORT);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            return text.split(":");
        }
        return new String[]{text};
    }

}
