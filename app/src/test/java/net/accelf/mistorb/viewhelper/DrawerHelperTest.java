package net.accelf.mistorb.viewhelper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DrawerHelperTest {

    @Test
    public void test_DrawerItem_fromId(){
        assertNull(DrawerHelper.DrawerItem.fromId(-1));
        assertEquals(DrawerHelper.DrawerItem.ITEM_RE_LOGIN, DrawerHelper.DrawerItem.fromId(0));
        assertEquals(DrawerHelper.DrawerItem.ITEM_ADD_NEW, DrawerHelper.DrawerItem.fromId(1));
    }

}