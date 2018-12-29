package net.accelf.mistorb;

import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.accelf.mistorb.util.InstancePickUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class LoginActivityTest {

    @Test
    public void test_onCreate() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_INSTANCE_DOMAIN, "example.com");
        LoginActivity activity = Robolectric.buildActivity(LoginActivity.class, intent)
                .create().get();

        WebView webView = activity.findViewById(R.id.activity_login_web_view);
        assertEquals("https://example.com/auth/sign_in", webView.getUrl());
    }

    @Test
    public void test_buildLoginUrl() throws Exception {
        Method method =
                LoginActivity.class.getDeclaredMethod("buildLoginUrl", String.class);
        method.setAccessible(true);

        assertEquals("https://example.com/auth/sign_in",
                method.invoke(null, "example.com"));
    }

    @Test
    public void test_checkSucceedPath() throws Exception {
        Method method =
                LoginActivity.class.getDeclaredMethod("checkSucceedPath", String.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(null, "/"));

        Uri uri1 = Uri.parse("https://example.com/");
        assertTrue((boolean) method.invoke(null, uri1.getPath()));

        Uri uri2 = Uri.parse("https://example.com/auth/sign_in");
        assertFalse((boolean) method.invoke(null, uri2.getPath()));
    }

    @Test
    public void test_onOptionsItemSelected() {
        LoginActivity activity = Robolectric.setupActivity(LoginActivity.class);

        MenuItem licenseMenuItem = new RoboMenuItem(R.id.menu_shared_licenses);
        activity.onOptionsItemSelected(licenseMenuItem);
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent intent = shadowActivity.peekNextStartedActivity();
        assertNotNull(intent);
        ShadowIntent shadowIntent = Shadows.shadowOf(intent);
        assertEquals(LicenseActivity.class, shadowIntent.getIntentClass());
    }

    @Test
    public void test_shouldOverrideUrlLoading() throws Exception {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_INSTANCE_DOMAIN, "example.com");
        LoginActivity activity = Robolectric.buildActivity(LoginActivity.class, intent)
                .create().get();
        WebView webView = activity.findViewById(R.id.activity_login_web_view);
        WebViewClient client = webView.getWebViewClient();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie("https://example.com", "test-data");

        WebResourceRequest request1 = createWebResourceRequest(Uri.parse("https://example.com/auth/sign_in"));
        assertFalse(client.shouldOverrideUrlLoading(webView, request1));
        ShadowActivity shadowActivity1 = Shadows.shadowOf(activity);
        Intent nextIntent1 = shadowActivity1.peekNextStartedActivity();
        assertNull(nextIntent1);

        WebResourceRequest request2 = createWebResourceRequest(Uri.parse("https://example.com/"));
        assertTrue(client.shouldOverrideUrlLoading(webView, request2));
        InstancePickUtil instancePicker = getInstancePicker(activity);
        assertEquals("test-data", instancePicker.getCookies());
        ShadowActivity shadowActivity2 = Shadows.shadowOf(activity);
        Intent nextIntent2 = shadowActivity2.peekNextStartedActivity();
        assertNotNull(nextIntent2);
        ShadowIntent shadowIntent = Shadows.shadowOf(nextIntent2);
        assertEquals(MainActivity.class, shadowIntent.getIntentClass());
    }

    private WebResourceRequest createWebResourceRequest(Uri url) {
        return new WebResourceRequest() {
            @Override
            public Uri getUrl() {
                return url;
            }

            @Override
            public boolean isForMainFrame() {
                return false;
            }

            @Override
            public boolean isRedirect() {
                return false;
            }

            @Override
            public boolean hasGesture() {
                return false;
            }

            @Override
            public String getMethod() {
                return null;
            }

            @Override
            public Map<String, String> getRequestHeaders() {
                return null;
            }
        };
    }

    private static InstancePickUtil getInstancePicker(LoginActivity activity) throws Exception {
        Field field = activity.getClass().getDeclaredField("instancePicker");
        field.setAccessible(true);
        return (InstancePickUtil) field.get(activity);
    }

}