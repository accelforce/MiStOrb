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
import java.lang.reflect.Method;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.test.core.app.ApplicationProvider;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        testStatsUpdate(activity1, "fetchStatsCallback", true, null);
        assertEquals("example.com",
                ((AppCompatTextView) activity1.findViewById(R.id.activity_main_server_info)).getText());
        assertEquals(View.INVISIBLE, activity1.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity1.findViewById(R.id.activity_main_stats_container).getVisibility());
        assertEquals("0",
                ((AppCompatTextView) activity1.findViewById(R.id.activity_main_stats_data_processed)).getText());

        Stats body = new Stats();
        Sidekiq sidekiq = new Sidekiq();
        sidekiq.processed = 22802891;
        body.sidekiq = sidekiq;
        MainActivity activity2 = Robolectric.setupActivity(MainActivity.class);
        testStatsUpdate(activity2, "fetchStatsCallback", true, body);
        assertEquals(View.INVISIBLE, activity2.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity2.findViewById(R.id.activity_main_stats_container).getVisibility());
        assertEquals("22,802,891",
                ((AppCompatTextView) activity2.findViewById(R.id.activity_main_stats_data_processed)).getText());

        MainActivity activity3 = Robolectric.setupActivity(MainActivity.class);
        testStatsUpdate(activity3, "fetchStatsCallback", false, null);
        assertEquals(View.INVISIBLE, activity3.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity3.findViewById(R.id.activity_main_stats_container).getVisibility());
        assertEquals("0",
                ((AppCompatTextView) activity3.findViewById(R.id.activity_main_stats_data_processed)).getText());
    }

    @Test
    @Config(shadows = {ShadowDrawerHelper.class})
    public void test_refreshStats() throws Exception {
        InstancePickUtil instancePicker = new InstancePickUtil(ApplicationProvider.getApplicationContext());
        instancePicker.addNewInstance("example.com", "data=value");

        Stats body1 = new Stats();
        Sidekiq sidekiq1 = new Sidekiq();
        sidekiq1.processed = 22802891;
        body1.sidekiq = sidekiq1;

        MainActivity activity1 = Robolectric.setupActivity(MainActivity.class);
        testStatsUpdate(activity1, "fetchStatsCallback", true, body1);
        ((SwipeRefreshLayout) activity1.findViewById(R.id.activity_main_swipe_refresh_layout)).setRefreshing(true);
        testStatsUpdate(activity1, "refreshStatsCallback", true, null);
        assertEquals("example.com",
                ((AppCompatTextView) activity1.findViewById(R.id.activity_main_server_info)).getText());
        assertEquals(View.INVISIBLE, activity1.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity1.findViewById(R.id.activity_main_stats_container).getVisibility());
        assertEquals("22,802,891",
                ((AppCompatTextView) activity1.findViewById(R.id.activity_main_stats_data_processed)).getText());
        assertFalse(((SwipeRefreshLayout) activity1.findViewById(R.id.activity_main_swipe_refresh_layout)).isRefreshing());

        Stats body2 = new Stats();
        Sidekiq sidekiq2 = new Sidekiq();
        sidekiq2.processed = 20190101;
        body2.sidekiq = sidekiq2;
        MainActivity activity2 = Robolectric.setupActivity(MainActivity.class);
        testStatsUpdate(activity2, "fetchStatsCallback", true, body1);
        ((SwipeRefreshLayout) activity2.findViewById(R.id.activity_main_swipe_refresh_layout)).setRefreshing(true);
        testStatsUpdate(activity2, "refreshStatsCallback", true, body2);
        assertEquals(View.INVISIBLE, activity2.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity2.findViewById(R.id.activity_main_stats_container).getVisibility());
        assertEquals("20,190,101",
                ((AppCompatTextView) activity2.findViewById(R.id.activity_main_stats_data_processed)).getText());
        assertFalse(((SwipeRefreshLayout) activity2.findViewById(R.id.activity_main_swipe_refresh_layout)).isRefreshing());

        MainActivity activity3 = Robolectric.setupActivity(MainActivity.class);
        testStatsUpdate(activity3, "fetchStatsCallback", true, body1);
        ((SwipeRefreshLayout) activity3.findViewById(R.id.activity_main_swipe_refresh_layout)).setRefreshing(true);
        testStatsUpdate(activity3, "refreshStatsCallback", false, null);
        assertEquals(View.INVISIBLE, activity3.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity3.findViewById(R.id.activity_main_stats_container).getVisibility());
        assertEquals("22,802,891",
                ((AppCompatTextView) activity3.findViewById(R.id.activity_main_stats_data_processed)).getText());
        assertFalse(((SwipeRefreshLayout) activity3.findViewById(R.id.activity_main_swipe_refresh_layout)).isRefreshing());
    }

    @Test
    @Config(shadows = {ShadowDrawerHelper.class})
    public void test_startReLogin() throws Exception {
        InstancePickUtil instancePicker = new InstancePickUtil(ApplicationProvider.getApplicationContext());
        instancePicker.addNewInstance("example.com", "data=value");

        MainActivity activity = Robolectric.setupActivity(MainActivity.class);

        Method method = MainActivity.class.getDeclaredMethod("startReLogin");
        method.setAccessible(true);
        method.invoke(activity);

        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent intent = shadowActivity.peekNextStartedActivity();
        assertNotNull(intent);
        assertEquals("example.com", intent.getStringExtra(LoginActivity.EXTRA_INSTANCE_DOMAIN));
        ShadowIntent shadowIntent = Shadows.shadowOf(intent);
        assertEquals(LoginActivity.class, shadowIntent.getIntentClass());
    }

    private static void testStatsUpdate(MainActivity activity, String targetCallback, boolean useSuccess, Stats body) throws Exception {
        Callback<Stats> callback = getCallback(activity, targetCallback);
        MastodonSidekiqApi sidekiqApi = getMastodonSidekiqApi(activity);
        if (useSuccess) {
            callback.onResponse(sidekiqApi.getStats(), Response.success(body));
        } else {
            callback.onFailure(sidekiqApi.getStats(), new Throwable());
        }
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