package net.accelf.mistorb;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Method;

import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

}