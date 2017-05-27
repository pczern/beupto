package me.speeddeveloper.beupto.util;

/**
 * Created by phili on 7/17/2016.
 */

import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by speedDeveloper on 26.06.2016.
 */
public class MenuTintUtils {

    public static void tintAllIcons(Menu menu, int color) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            tintMenuItemIcon(color, item);
        }
    }

    private static void tintMenuItemIcon(int color, MenuItem item) {
        Drawable drawable = item.getIcon();
        if (drawable != null) {
            Drawable wrapped = DrawableCompat.wrap(drawable);
            drawable.mutate();
            DrawableCompat.setTint(wrapped, color);
            item.setIcon(drawable);
        }
    }
}
