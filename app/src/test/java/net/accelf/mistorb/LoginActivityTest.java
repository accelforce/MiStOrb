package net.accelf.mistorb;

import android.net.Uri;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class LoginActivityTest {

    @Test
    public void test_buildLoginUrl() throws Exception {
        Method method =
                LoginActivity.class.getDeclaredMethod("buildLoginUrl", String.class);
        method.setAccessible(true);

        assertEquals(method.invoke(null, "example.com"),
                "https://example.com/auth/sign_in");
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