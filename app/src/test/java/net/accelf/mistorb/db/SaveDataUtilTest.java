package net.accelf.mistorb.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class SaveDataUtilTest {

    @Test
    public void test_cookie() {
        SaveDataUtil saveData = new SaveDataUtil(ApplicationProvider.getApplicationContext());

        assertNull(saveData.cookie("unknown.domain"));

        saveData.saveCookie("example.com", "test-data");
        assertEquals("test-data", saveData.cookie("example.com"));
    }

}