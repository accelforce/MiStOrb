package net.accelf.mistorb.viewhelper;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import net.accelf.mistorb.LicenseActivity;
import net.accelf.mistorb.R;

public class GlobalMenuHelper {
    public static boolean onGlobalMenuItemSelected(Context context, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_shared_licenses:
                showOssLicenses(context);
                return true;
        }
        return false;
    }

    private static void showOssLicenses(Context context) {
        Intent intent = new Intent(context, LicenseActivity.class);
        context.startActivity(intent);
    }
}
