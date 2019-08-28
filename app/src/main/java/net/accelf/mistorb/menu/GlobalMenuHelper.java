package net.accelf.mistorb.menu;

import android.content.Context;
import android.view.MenuItem;

import net.accelf.mistorb.R;
import net.accelf.mistorb.license.LicenseActivity;

public class GlobalMenuHelper {
    public static boolean onGlobalMenuItemSelected(Context context, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_shared_licenses:
                context.startActivity(LicenseActivity.createIntent(context));
                return true;
        }
        return false;
    }
}
