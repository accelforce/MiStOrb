package net.accelf.mistorb.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SaveDataUtil {

    private SharedPreferences preferences;

    private static final String PREF_COOKIE_STORE = "cookie_store";

    SaveDataUtil(Context context) {
        preferences = context.getSharedPreferences(PREF_COOKIE_STORE, Context.MODE_PRIVATE);
    }

    void saveCookie(String domain, String cookie) {
        preferences.edit()
                .putString(domain, cookie)
                .apply();
    }

    String cookie(String domain) {
        if (!domainExists(domain)) {
            return null;
        }
        return preferences.getString(domain, null);
    }

    void removeCookie(String domain){
        preferences.edit()
                .remove(domain)
                .apply();
    }

    Set<String> listCookies(){
        return preferences.getAll().keySet();
    }

    boolean domainExists(String domain) {
        return preferences.getString(domain, null) != null;
    }

}
