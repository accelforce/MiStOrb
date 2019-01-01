package net.accelf.mistorb.robolectric;

import net.accelf.mistorb.viewhelper.DrawerHelper;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(DrawerHelper.class)
public class ShadowDrawerHelper {

    @Implementation
    public void initializeImageLoader() {
    }
}
