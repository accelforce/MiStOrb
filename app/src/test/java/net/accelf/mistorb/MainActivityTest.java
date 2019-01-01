package net.accelf.mistorb;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import net.accelf.mistorb.api.MastodonSidekiqApi;
import net.accelf.mistorb.api.Sidekiq;
import net.accelf.mistorb.api.Stats;
import net.accelf.mistorb.robolectric.CustomRobolectricTestRunner;
import net.accelf.mistorb.robolectric.ShadowDrawerHelper;
import net.accelf.mistorb.util.InstancePickUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import java.lang.reflect.Field;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.test.core.app.ApplicationProvider;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(CustomRobolectricTestRunner.class)
public class MainActivityTest {

    @Test
    public void test_onOptionsItemSelected() {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);

        MenuItem licenseMenuItem = new RoboMenuItem(R.id.menu_shared_licenses);
        activity.onOptionsItemSelected(licenseMenuItem);
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent intent = shadowActivity.peekNextStartedActivity();
        assertNotNull(intent);
        ShadowIntent shadowIntent = Shadows.shadowOf(intent);
        assertEquals(LicenseActivity.class, shadowIntent.getIntentClass());
    }

    @Test
    @Config(shadows = {ShadowDrawerHelper.class})
    public void test_fetchStats() throws Exception {
        InstancePickUtil instancePicker = new InstancePickUtil(ApplicationProvider.getApplicationContext());
        instancePicker.addNewInstance("example.com", "data=value");

        MainActivity activity1 = Robolectric.setupActivity(MainActivity.class);
        assertEquals("example.com",
                ((AppCompatTextView) activity1.findViewById(R.id.activity_main_server_info)).getText());

        Callback<Stats> callback1 = getCallback(activity1, "fetchStatsCallback");
        MastodonSidekiqApi sidekiqApi1 = getMastodonSidekiqApi(activity1);
        callback1.onResponse(sidekiqApi1.getStats(), Response.success(null));
        assertEquals(View.INVISIBLE, activity1.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity1.findViewById(R.id.activity_main_stats_container).getVisibility());
        assertEquals("0",
                ((AppCompatTextView) activity1.findViewById(R.id.activity_main_stats_data_processed)).getText());

        MainActivity activity2 = Robolectric.setupActivity(MainActivity.class);
        Callback<Stats> callback2 = getCallback(activity2, "fetchStatsCallback");
        MastodonSidekiqApi sidekiqApi2 = getMastodonSidekiqApi(activity2);
        Stats body = new Stats();
        Sidekiq sidekiq = new Sidekiq();
        sidekiq.processed = 22802891;
        body.sidekiq = sidekiq;
        callback2.onResponse(sidekiqApi2.getStats(), Response.success(body));
        assertEquals(View.INVISIBLE, activity2.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity2.findViewById(R.id.activity_main_stats_container).getVisibility());
        assertEquals("22,802,891",
                ((AppCompatTextView) activity2.findViewById(R.id.activity_main_stats_data_processed)).getText());

        MainActivity activity3 = Robolectric.setupActivity(MainActivity.class);
        Callback<Stats> callback3 = getCallback(activity3, "fetchStatsCallback");
        MastodonSidekiqApi sidekiqApi3 = getMastodonSidekiqApi(activity3);
        callback3.onFailure(sidekiqApi3.getStats(), new Throwable());
        assertEquals(View.INVISIBLE, activity3.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity3.findViewById(R.id.activity_main_stats_container).getVisibility());
        assertEquals("0",
                ((AppCompatTextView) activity3.findViewById(R.id.activity_main_stats_data_processed)).getText());
    }

    private static MastodonSidekiqApi getMastodonSidekiqApi(MainActivity activity) throws Exception {
        Field field = activity.getClass().getDeclaredField("sidekiqApi");
        field.setAccessible(true);
        return (MastodonSidekiqApi) field.get(activity);
    }

    @SuppressWarnings("unchecked")
    private static Callback<Stats> getCallback(MainActivity activity, String target) throws Exception {
        Field field = activity.getClass().getDeclaredField(target);
        field.setAccessible(true);
        return (Callback<Stats>) field.get(activity);
    }

}