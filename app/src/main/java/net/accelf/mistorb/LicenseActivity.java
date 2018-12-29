package net.accelf.mistorb;

import android.app.Activity;
import android.os.Bundle;

import team.birdhead.widget.LicenseView;

public class LicenseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new LicenseView(this));
        setTitle(R.string.menu_oss_licenses);
    }
}
