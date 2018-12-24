package net.accelf.mistorb.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class InstancePickUtilTest {

    @Test
    public void test_addNewInstance() throws Exception {
        InstancePickUtil instancePicker = new InstancePickUtil(ApplicationProvider.getApplicationContext());
        SaveDataUtil saveData = getSaveData(instancePicker);

        instancePicker.addNewInstance("example.com", "test-data");
        assertEquals("test-data", saveData.cookie("example.com"));
        assertEquals("example.com", instancePicker.selectedInstance());
    }

    private static SaveDataUtil getSaveData(InstancePickUtil instancePicker) throws Exception {
        Field field = instancePicker.getClass().getDeclaredField("saveData");
        field.setAccessible(true);
        return (SaveDataUtil) field.get(instancePicker);
    }

}