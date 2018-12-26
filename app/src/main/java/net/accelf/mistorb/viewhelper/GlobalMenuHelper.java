package net.accelf.mistorb.viewhelper;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

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
        Intent intent = new Intent(context, OssLicensesMenuActivity.class);
        intent.putExtra("title", context.getString(R.string.menu_oss_licenses));
        context.startActivity(intent);
    }
}
