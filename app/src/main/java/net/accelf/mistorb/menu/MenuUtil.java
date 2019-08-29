package net.accelf.mistorb.menu;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.core.graphics.drawable.DrawableCompat;

import net.accelf.mistorb.R;

import static net.accelf.mistorb.compat.AndroidApiCompat.getColor;

public class MenuUtil {

    public static void setOptionsItemState(Menu menu, @IdRes int id, boolean state, Resources resources, Resources.Theme theme) {
        MenuItem item = menu.findItem(id);
        item.setEnabled(state);
        Drawable icon = DrawableCompat.wrap(item.getIcon());
        DrawableCompat.setTint(icon, state ? getColor(resources, theme, R.color.colorMenuEnabled)
                : getColor(resources, theme, R.color.colorMenuDisabled));
        item.setIcon(icon);
    }
}
