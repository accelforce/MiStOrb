package net.accelf.mistorb.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

import androidx.test.core.app.ApplicationProvider;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class InstancePickUtilTest {

    @Test
    public void test_addNewInstance() throws Exception {
        InstancePickUtil instancePicker = new InstancePickUtil(ApplicationProvider.getApplicationContext());
        SaveDataUtil saveData = getSaveData(instancePicker);

        instancePicker.addNewInstance("example.com", "test-data");
        assertEquals("test-data", saveData.cookie("example.com"));
        assertEquals("example.com", instancePicker.selectedInstance());
        assertEquals("test-data", instancePicker.getCookies());
    }

    @Test
    public void test_selectAnyway() throws Exception {
        InstancePickUtil instancePicker = new InstancePickUtil(ApplicationProvider.getApplicationContext());
        SaveDataUtil saveData = getSaveData(instancePicker);

        assertFalse(instancePicker.selectAnyway());

        saveData.saveCookie("example.com", "test-data-1");
        assertTrue(instancePicker.selectAnyway());
        assertEquals("example.com", instancePicker.selectedInstance());
        assertEquals("test-data-1", instancePicker.getCookies());

        instancePicker.removeInstance();

        saveData.saveCookie("example.com", "test-data-1");
        saveData.saveCookie("example.net", "test-data-2");
        saveData.saveCookie("example.jp", "test-data-3");
        assertTrue(instancePicker.selectAnyway());
        assertThat(instancePicker.selectedInstance(),
                anyOf(is("example.com"), is("example.net"), is("example.jp")));
        assertThat(instancePicker.getCookies(),
                anyOf(is("test-data-1"), is("test-data-2"), is("test-data-3")));
    }

    private static SaveDataUtil getSaveData(InstancePickUtil instancePicker) throws Exception {
        Field field = instancePicker.getClass().getDeclaredField("saveData");
        field.setAccessible(true);
        return (SaveDataUtil) field.get(instancePicker);
    }

}