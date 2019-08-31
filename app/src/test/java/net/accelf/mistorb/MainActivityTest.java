package net.accelf.mistorb;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.test.core.app.ApplicationProvider;

import net.accelf.mistorb.db.InstancePickUtil;
import net.accelf.mistorb.license.LicenseActivity;
import net.accelf.mistorb.login.LoginActivity;
import net.accelf.mistorb.main.MainActivity;
import net.accelf.mistorb.main.StatsViewAdapter;
import net.accelf.mistorb.main.components.datavalue.DataValueModel;
import net.accelf.mistorb.main.components.singletitle.SingleTitleModel;
import net.accelf.mistorb.model.Stats;
import net.accelf.mistorb.robolectric.CustomRobolectricTestRunner;
import net.accelf.mistorb.robolectric.ShadowDrawerHelper;

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
import java.util.List;

import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(CustomRobolectricTestRunner.class)
@Config(sdk = 28)
public class MainActivityTest {

    private static void testStatsUpdate(MainActivity activity, boolean useSuccess, Stats body, boolean refresh) throws Exception {
        if (useSuccess) {
            Method method = MainActivity.class.getDeclaredMethod("onFetchStatsSuccess", Response.class, boolean.class);
            method.setAccessible(true);
            method.invoke(activity, Response.success(body), refresh);
        } else {
            Method method = MainActivity.class.getDeclaredMethod("onFetchStatsFail", Throwable.class, boolean.class);
            method.setAccessible(true);
            method.invoke(activity, new Throwable(), refresh);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Object> getList(MainActivity activity) throws Exception {
        RecyclerView recyclerView = activity.findViewById(R.id.activity_main_recycler_view);
        StatsViewAdapter adapter = (StatsViewAdapter) recyclerView.getAdapter();
        Field field = StatsViewAdapter.class.getDeclaredField("list");
        field.setAccessible(true);
        return (List<Object>) field.get(adapter);
    }

    private static String getTitleFromList(List<Object> list) throws Exception {
        Field field = SingleTitleModel.class.getDeclaredField("title");
        field.setAccessible(true);
        return (String) field.get(list.get(0));
    }

    private static String getValueFromList(List<Object> list, String label) throws Exception {
        Field titleField = DataValueModel.class.getDeclaredField("title");
        titleField.setAccessible(true);
        Field valueField = DataValueModel.class.getDeclaredField("value");
        valueField.setAccessible(true);
        for (Object item : list) {
            if (item instanceof DataValueModel &&
                    label.equals(titleField.get(item))) {
                return (String) valueField.get(item);
            }
        }
        return null;
    }

    @Test
    public void test_onOptionsItemSelected() {
        @SuppressWarnings("deprecation")
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

        @SuppressWarnings("deprecation")
        MainActivity activity1 = Robolectric.setupActivity(MainActivity.class);
        testStatsUpdate(activity1, true, null, false);
        List<Object> list1 = getList(activity1);
        assertEquals("example.com", getTitleFromList(list1));
        assertEquals(View.INVISIBLE, activity1.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity1.findViewById(R.id.activity_main_recycler_view).getVisibility());
        assertEquals("0", getValueFromList(list1, "Retries"));

        Stats body = new Stats();
        body.sidekiq.processed = 22802891;
        @SuppressWarnings("deprecation")
        MainActivity activity2 = Robolectric.setupActivity(MainActivity.class);
        testStatsUpdate(activity2, true, body, false);
        List<Object> list2 = getList(activity2);
        assertEquals(View.INVISIBLE, activity2.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity2.findViewById(R.id.activity_main_recycler_view).getVisibility());
        assertEquals("22,802,891", getValueFromList(list2, "Processed"));

        @SuppressWarnings("deprecation")
        MainActivity activity3 = Robolectric.setupActivity(MainActivity.class);
        testStatsUpdate(activity3, false, null, false);
        List<Object> list3 = getList(activity3);
        assertEquals(View.INVISIBLE, activity3.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity3.findViewById(R.id.activity_main_recycler_view).getVisibility());
        assertEquals("0", getValueFromList(list3, "Processed"));
    }

    @Test
    @Config(shadows = {ShadowDrawerHelper.class})
    public void test_refreshStats() throws Exception {
        InstancePickUtil instancePicker = new InstancePickUtil(ApplicationProvider.getApplicationContext());
        instancePicker.addNewInstance("example.com", "data=value");

        Stats body1 = new Stats();
        body1.sidekiq.processed = 22802891;

        @SuppressWarnings("deprecation")
        MainActivity activity1 = Robolectric.setupActivity(MainActivity.class);
        testStatsUpdate(activity1, true, body1, false);
        ((SwipeRefreshLayout) activity1.findViewById(R.id.activity_main_swipe_refresh_layout)).setRefreshing(true);
        testStatsUpdate(activity1, true, null, true);
        List<Object> list1 = getList(activity1);
        assertEquals("example.com", getTitleFromList(list1));
        assertEquals(View.INVISIBLE, activity1.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity1.findViewById(R.id.activity_main_recycler_view).getVisibility());
        assertEquals("0", getValueFromList(list1, "Retries"));
        assertFalse(((SwipeRefreshLayout) activity1.findViewById(R.id.activity_main_swipe_refresh_layout)).isRefreshing());

        Stats body2 = new Stats();
        body2.sidekiq.processed = 20190101;
        @SuppressWarnings("deprecation")
        MainActivity activity2 = Robolectric.setupActivity(MainActivity.class);
        testStatsUpdate(activity2, true, body1, false);
        ((SwipeRefreshLayout) activity2.findViewById(R.id.activity_main_swipe_refresh_layout)).setRefreshing(true);
        testStatsUpdate(activity2, true, body2, true);
        List<Object> list2 = getList(activity2);
        assertEquals(View.INVISIBLE, activity2.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity2.findViewById(R.id.activity_main_recycler_view).getVisibility());
        assertEquals("20,190,101", getValueFromList(list2, "Processed"));
        assertFalse(((SwipeRefreshLayout) activity2.findViewById(R.id.activity_main_swipe_refresh_layout)).isRefreshing());

        @SuppressWarnings("deprecation")
        MainActivity activity3 = Robolectric.setupActivity(MainActivity.class);
        testStatsUpdate(activity3, true, body1, false);
        ((SwipeRefreshLayout) activity3.findViewById(R.id.activity_main_swipe_refresh_layout)).setRefreshing(true);
        testStatsUpdate(activity3, false, null, true);
        List<Object> list3 = getList(activity3);
        assertEquals(View.INVISIBLE, activity3.findViewById(R.id.activity_main_loading).getVisibility());
        assertEquals(View.VISIBLE, activity3.findViewById(R.id.activity_main_recycler_view).getVisibility());
        assertEquals("0", getValueFromList(list3, "Retries"));
        assertFalse(((SwipeRefreshLayout) activity3.findViewById(R.id.activity_main_swipe_refresh_layout)).isRefreshing());
    }

    @Test
    @Config(shadows = {ShadowDrawerHelper.class})
    public void test_startReLogin() throws Exception {
        InstancePickUtil instancePicker = new InstancePickUtil(ApplicationProvider.getApplicationContext());
        instancePicker.addNewInstance("example.com", "data=value");

        @SuppressWarnings("deprecation")
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

}