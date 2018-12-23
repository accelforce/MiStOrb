package net.accelf.mistorb;

import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.widget.Button;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class DomainInputActivityTest {

    @Test
    public void submit() {
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

    private void setEditTextValue(DomainInputActivity activity, String value) {
        AppCompatEditText editText = activity.findViewById(R.id.activity_edit_text_domain_input);
        editText.setText(value);
    }

    private void clickButton(DomainInputActivity activity){
        Button button = activity.findViewById(R.id.activity_button_domain_submit);
        button.performClick();
    }

}