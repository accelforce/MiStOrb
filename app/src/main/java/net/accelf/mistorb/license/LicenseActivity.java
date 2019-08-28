package net.accelf.mistorb.license;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.accelf.mistorb.R;

import team.birdhead.widget.LicenseView;

public class LicenseActivity extends Activity {

    public static Intent createIntent(Context context) {
        return new Intent(context, LicenseActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new LicenseView(this));
        setTitle(R.string.menu_oss_licenses);
    }
}
