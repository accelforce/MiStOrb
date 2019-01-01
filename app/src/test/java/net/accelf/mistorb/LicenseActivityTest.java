package net.accelf.mistorb;

import android.view.ViewGroup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import team.birdhead.widget.LicenseView;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class LicenseActivityTest {

    @Test
    public void test_onCreate() {
        LicenseActivity activity = Robolectric.setupActivity(LicenseActivity.class);
        ViewGroup allView = activity.findViewById(android.R.id.content);
        assertTrue(findLicenseView(allView));
    }

    private boolean findLicenseView(ViewGroup allView) {
        for (int i = 0; i < allView.getChildCount(); i++) {
            if (allView.getChildAt(i) instanceof LicenseView) {
                return true;
            }
        }
        return false;
    }

}