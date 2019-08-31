package net.accelf.mistorb;

import android.content.Intent;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatEditText;

import net.accelf.mistorb.license.LicenseActivity;
import net.accelf.mistorb.login.DomainInputActivity;
import net.accelf.mistorb.login.LoginActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class DomainInputActivityTest {

    @Test
    public void submit() {
        @SuppressWarnings("deprecation")
        DomainInputActivity activity = Robolectric.setupActivity(DomainInputActivity.class);
        AppCompatEditText editText = activity.findViewById(R.id.activity_edit_text_domain_input);

        ShadowActivity shadowActivity1 = Shadows.shadowOf(activity);
        Intent intent1 = shadowActivity1.peekNextStartedActivity();
        clickButton(activity);
        assertNull(intent1);
        assertEquals("", Objects.requireNonNull(editText.getText()).toString());

        ShadowActivity shadowActivity2 = Shadows.shadowOf(activity);
        Intent intent2 = shadowActivity2.peekNextStartedActivity();
        setEditTextValue(activity, "not a domain");
        clickButton(activity);
        assertNull(intent2);
        assertEquals("", Objects.requireNonNull(editText.getText()).toString());

        setEditTextValue(activity, "example.com");
        clickButton(activity);
        ShadowActivity shadowActivity3 = Shadows.shadowOf(activity);
        Intent intent3 = shadowActivity3.peekNextStartedActivity();
        assertNotNull(intent3);
        String domain = intent3.getStringExtra(LoginActivity.EXTRA_INSTANCE_DOMAIN);
        assertEquals("example.com", domain);
    }

    @Test
    public void test_onOptionsItemSelected() {
        @SuppressWarnings("deprecation")
        DomainInputActivity activity = Robolectric.setupActivity(DomainInputActivity.class);

        MenuItem licenseMenuItem = new RoboMenuItem(R.id.menu_shared_licenses);
        activity.onOptionsItemSelected(licenseMenuItem);
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent intent = shadowActivity.peekNextStartedActivity();
        assertNotNull(intent);
        ShadowIntent shadowIntent = Shadows.shadowOf(intent);
        assertEquals(LicenseActivity.class, shadowIntent.getIntentClass());
    }

    private void setEditTextValue(DomainInputActivity activity, String value) {
        AppCompatEditText editText = activity.findViewById(R.id.activity_edit_text_domain_input);
        editText.setText(value);
    }

    private void clickButton(DomainInputActivity activity) {
        Button button = activity.findViewById(R.id.activity_button_domain_submit);
        button.performClick();
    }

}