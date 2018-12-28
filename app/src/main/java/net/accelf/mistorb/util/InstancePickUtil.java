package net.accelf.mistorb.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

public class InstancePickUtil {

    private SharedPreferences preferences;
    private SaveDataUtil saveData;

    private static final String PREF_SELECTED_DOMAIN = "selected_domain";

    public InstancePickUtil(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        saveData = new SaveDataUtil(context);
    }

    public String selectedInstance() {
        return preferences.getString(PREF_SELECTED_DOMAIN, null);
    }

    public void setSelectedInstance(String domain) {
        if (!saveData.domainExists(domain)) {
            return;
        }
        preferences.edit()
                .putString(PREF_SELECTED_DOMAIN, domain)
                .apply();
    }

    public void removeInstance() {
        saveData.removeCookie(selectedInstance());
        preferences.edit()
                .remove(selectedInstance())
                .apply();
    }

    public void addNewInstance(String domain, String cookie) {
        saveData.saveCookie(domain, cookie);
        setSelectedInstance(domain);
    }

    public boolean selectAnyway() {
        if (selectedInstance() != null) {
            return true;
        }
        Set<String> domainList = saveData.listCookies();
        if (domainList.isEmpty()) {
            return false;
        }
        Object[] domainArray = domainList.toArray();
        if (domainArray == null) {
            return false;
        }
        setSelectedInstance((String) domainArray[0]);
        return true;
    }

    public String getCookies() {
        return saveData.cookie(selectedInstance());
    }
}
