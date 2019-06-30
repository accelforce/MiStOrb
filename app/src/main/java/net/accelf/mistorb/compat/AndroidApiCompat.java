package net.accelf.mistorb.compat;

import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.ColorRes;

public class AndroidApiCompat {

    public static int getColor(Resources resources, Resources.Theme theme, @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return resources.getColor(color, theme);
        } else {
            //noinspection deprecation
            return resources.getColor(color);
        }
    }

}
